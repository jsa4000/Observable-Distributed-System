
####################
# Initialize
####################

# Add Helm Repositories
helm3 repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm3 repo add elastic https://helm.elastic.co
helm3 repo add banzaicloud-stable https://kubernetes-charts.banzaicloud.com
helm3 repo add jaegertracing https://jaegertracing.github.io/helm-charts
helm3 repo add traefik https://helm.traefik.io/traefik
helm3 repo add grafana https://grafana.github.io/helm-charts
helm3 repo add minio https://operator.min.io/
helm3 repo add bitnami https://charts.bitnami.com/bitnami

# Update Repositories
helm3 repo update

# Get Repository Charts Versions
helm3 search repo

####################
# Installation
####################

## Install `kube-prometheus-stack` Chart into `monitoring` namespace
helm3 install -n monitoring --create-namespace prometheus prometheus-community/kube-prometheus-stack --version 18.0.6 \
--set 'prometheus-node-exporter.hostRootFsMount=false'

# Install the ECK Operator (Elastic Cloud on Kubernetes: Elastic + Kibana)      
helm3 install elastic-operator elastic/eck-operator -n logging --create-namespace --version 1.7.1

## Install Logging Operator using helm3 (v3.14.2)
helm3 install logging-operator banzaicloud-stable/logging-operator -n logging --create-namespace --version 3.14.2 \
--set 'createCustomResource=false'

## Install `jaeger-operator` Chart into `tracing` namespace
helm3 install -n tracing --create-namespace jaeger-operator jaegertracing/jaeger-operator --version 2.24.0

## Install `traefik` Chart into `tools` namespace
helm3 install -n tools --create-namespace traefik traefik/traefik --version 10.3.2 -f manifests/traefik-values.yaml

## Install `Grafana Loki Stack` Chart into `logging` namespace
helm3 upgrade --install loki -n logging --create-namespace grafana/loki-stack --version 2.4.1 --set grafana.enabled=true

## Install MinIO Operator with 'default' tentant
helm3 install minio --namespace minio --create-namespace minio/minio-operator --version 4.2.7  -f manifests/minio-operator-values.yaml

####################
# Initialize
####################

## Create default minio buckets
kubectl create -n minio -f manifests/minio-create-buckets.yaml

####################
# DataStore
####################

## Install MongoB into datastore namespace
helm3 install mongo --namespace datastore --create-namespace bitnami/mongodb --version 10.19.0 -f manifests/mongodb-values.yaml

## Instal MongoDB Exporterr into datastore using previous prometheus release installed (kube-prometheus-stack)
helm3 install prometheus-mongodb-exporter --namespace datastore prometheus-community/prometheus-mongodb-exporter --version 2.8.1 \
--set 'mongodb.uri=mongodb://monitor:password@mongo-mongodb.datastore.svc.cluster.local:27017/admin' \
--set 'serviceMonitor.additionalLabels.release=prometheus'

####################
# Deployment
####################

## It can be deployed by using a yaml file with all the manifests.
kubectl apply -n logging -f manifests/eck.yaml

## Create logging instances to monitor kubernetes cluster entirely

### Apply Clusterflow and ClusterOutputs (elasticsearch and s3) using SSL
kubectl apply -n logging -f manifests/logging.yaml

## Create Jaeger all-in-once inmemory instace with agents, collector, querier and backend

### Configure OPENTRACING_JAEGER_ENABLED (deployments/spring-boot-tracing/deployment.yaml) to false using sidecar
#kubectl apply -n tracing -f manifests/jaeger-sidecar.yaml

### Configure OPENTRACING_JAEGER_ENABLED (deployments/spring-boot-tracing/deployment.yaml) to true using daemonset
kubectl apply -n tracing -f manifests/jaeger-daemonset.yaml

## Deploy the prometheus-operator `ServiceMonitor` to monitor trraefik form prometheus
kubectl apply -n tools -f manifests/traefik-service-monitor.yaml
kubectl apply -n tools -f manifests/traefik-ingress-route.yaml

####################
# Application
####################

# Create the 'micro' namespace
kubectl create namespace micro

# Deploy the Application example
kubectl apply -n micro -f deployments/spring-boot-tracing

## Note: Deploying jaeger as a sidecar mode, simple-spring-boot-tracing pod must have 2/2 containers running

####################
# Test
####################

###### Api Gateway (Traefik)  ######

## Port-Forward
kubectl port-forward -n tools svc/traefik-dashboard 9000

## IngressRoute
http://traefik.management.com (`admin/pass`)

###### Metrics  ######

## Prometheus dashboard (http://localhost:9090)
kubectl port-forward -n monitoring svc/prometheus-kube-prometheus-prometheus 9090

## Grafana dashboard (http://localhost:3000) (`admin/prom-operator`)
kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80

## Import Grafana dashboards
##      Dashboard            |   ID   
## -----------------------------------
##   Node Exporter Full      |  1860
##   Traefik                 |  4475
##   Spring Boot Statistics  |  6756
##   MongoDB Exporter        |  2583


##  Add Grafana DataSources  (Explore -> Select "Loki" DataSource)
##      DataSource    |                     URL   
## --------------------------------------------------------------------
##        Loki        |  http://loki.logging.svc.cluster.local:3100

###### Microservice ######

# Usinig Port forward (Without LoadBalancer)
kubectl port-forward -n tools svc/traefik 8080:80

## Spring Boot Application

### Metrics (firstly warm-up endpoints /tracer/**)
http://localhost:8080/tracer/management/metrics

### Health Checks
http://localhost:8080/tracer/management/health
http://localhost:8080/tracer/management/health/readiness
http://localhost:8080/tracer/management/health/liveness

####### Distributed Tracing ######

## Jaeger dashboard
http://localhost:8080

## GET /trace
http://localhost:8080/tracer/trace

## GET /tracee
http://localhost:8080/tracer/tracee

## Check Jaeger to Get the different Traces and Spans at http://localhost:8080  

####### Logging ######

## Kibana (https://localhost:5601/)
kubectl get secret -n logging elastic-cluster-es-elastic-user -o=jsonpath='{.data.elastic}' | base64 --decode; echo
kubectl port-forward -n logging service/kibana-cluster-kb-http 5601

## Grafana Loki (http://loki.logging.svc.cluster.local:3100)
kubectl get secret -n logging loki-grafana -o=jsonpath='{.data.admin-user}' | base64 --decode; echo
kubectl get secret -n logging loki-grafana -o=jsonpath='{.data.admin-password}' | base64 --decode; echo
kubectl port-forward -n logging svc/loki-grafana 3000:80

####### Minio ######

# Access to the `default` MinIO Dashboard (`minio/minio123`) at http://localhost:9000
kubectl --namespace minio port-forward svc/minio 9000:80

#Access to the `default` MinIO Tenant Console (`admin/minio123`) at http://localhost:9090
kubectl --namespace minio port-forward svc/default-console 9090

    