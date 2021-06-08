
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

# Update Repositories
helm3 repo update

# Get Repository Charts Versions
helm3 search repo

####################
# Installation
####################

## Install `kube-prometheus-stack` Chart into `monitoring` namespace
helm3 install -n monitoring --create-namespace prometheus prometheus-community/kube-prometheus-stack --version 16.3.1 \
--set 'prometheus-node-exporter.hostRootFsMount=false'

# Install the ECK Operator (Elastic Cloud on Kubernetes: Elastic + Kibana)      
helm3 install elastic-operator elastic/eck-operator -n logging --create-namespace --version 1.6.0

## Install Logging Operator using helm3 (v3.10.0)
helm3 install logging-operator banzaicloud-stable/logging-operator -n logging --create-namespace --version 3.10.0 \
--set 'createCustomResource=false'

## Install `jaeger-operator` Chart into `tracing` namespace
helm3 install -n tracing --create-namespace jaeger-operator jaegertracing/jaeger-operator --version 2.21.2

## Install `traefik` Chart into `tools` namespace
helm3 install -n tools --create-namespace traefik traefik/traefik --version 9.19.1 -f Kubernetes/files/traefik-values.yaml

## Install `Grafana Loki Stack` Chart into `logging` namespace
helm3 upgrade --install loki -n logging --create-namespace grafana/loki-stack --version 2.4.1 --set grafana.enabled=true

## Install MinIO Operator with 'default' tentant
helm3 install minio --namespace minio --create-namespace minio/minio-operator --version 4.1.0 -f Kubernetes/files/minio-operator-values.yaml

####################
# Initialize
####################

## Create default minio buckets
kubectl create -n minio -f Kubernetes/files/minio-create-buckets.yaml

####################
# Deployment
####################

## It can be deployed by using a yaml file with all the manifests.
kubectl apply -n logging -f Kubernetes/files/eck.yaml

## Create logging instances to monitor kubernetes cluster entirely

### Apply Clusterflow and ClusterOutput (elasticsearch) manifests
kubectl apply -n logging -f Kubernetes/files/logging.yaml

### Apply Clusterflow and ClusterOutputs (elasticsearch and s3) using SSL
#kubectl apply -n logging -f Kubernetes/files/logging-advanced.yaml

## Create Jaeger all-in-once inmemory instace with agents, collector, querier and backend

### Configure OPENTRACING_JAEGER_ENABLED (examples/deployments/01-simple-spring-boot-tracing/deployment.yaml) to false using sidecar
#kubectl apply -n tracing -f Kubernetes/files/jaeger-sidecar.yaml

### Configure OPENTRACING_JAEGER_ENABLED (examples/deployments/01-simple-spring-boot-tracing/deployment.yaml) to true using daemonset
kubectl apply -n tracing -f Kubernetes/files/jaeger-daemonset.yaml

## Deploy the prometheus-operator `ServiceMonitor` to monitor trraefik form prometheus
kubectl apply -n tools -f Kubernetes/files/traefik-service-monitor.yaml
kubectl apply -n tools -f Kubernetes/files/traefik-ingress-route.yaml

####################
# Application
####################

# Create the 'micro' namespace
kubectl create namespace micro

# Deploy the Application example
kubectl apply -n micro -f examples/deployments/01-simple-spring-boot-tracing

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

## Prometheus dashboarfd
kubectl port-forward -n monitoring svc/prometheus-kube-prometheus-prometheus 9090

## Grafana dashboard (`admin/prom-operator`)
kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80

###### Microservice ######

### Metrics (firstly warm-up endpoints /tracer/**)
http://localhost/tracer/management/metrics

### Health Checks
http://localhost/tracer/management/health
http://localhost/tracer/management/health/readiness
http://localhost/tracer/management/health/liveness

####### Distributed Tracing ######

## Jaeger dashboard
http://localhost

## GET /trace
http://localhost/tracer/trace

## GET /tracee
http://localhost/tracer/tracee

####### Logging ######

## Kibana
kubectl get secret -n logging elastic-cluster-es-elastic-user -o=jsonpath='{.data.elastic}' | base64 --decode; echo
kubectl port-forward -n logging service/kibana-cluster-kb-http 5601

## Grafana Loki
kubectl get secret -n logging loki-grafana -o=jsonpath='{.data.admin-user}' | base64 --decode; echo
kubectl get secret -n logging loki-grafana -o=jsonpath='{.data.admin-password}' | base64 --decode; echo
kubectl port-forward -n logging svc/loki-grafana 3000:80

####### Minio ######

# Access to the `default` MinIO Dashboard (`minio/minio123`) at http://localhost:9000
kubectl --namespace minio port-forward svc/minio 9000:80

#Access to the `default` MinIO Tenant Console (`admin/minio123`) at http://localhost:9090
kubectl --namespace minio port-forward svc/default-console 9090

    