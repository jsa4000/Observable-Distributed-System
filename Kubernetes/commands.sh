
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
helm3 install -n tools --create-namespace traefik traefik/traefik --version 9.19.1 \
--set 'additionalArguments[0]=--api.insecure' \
--set 'additionalArguments[1]=--metrics.prometheus=true' \
--set 'additionalArguments[2]=--tracing.jaeger=true' \
--set 'additionalArguments[3]=--tracing.serviceName=traefik-service' \
--set 'additionalArguments[4]=--tracing.jaeger.samplingParam=1.0' \
--set 'additionalArguments[5]=--tracing.jaeger.disableAttemptReconnecting=false' \
--set 'additionalArguments[6]=--tracing.jaeger.samplingType=const' \
--set 'additionalArguments[7]=--tracing.jaeger.samplingServerURL=http://jaeger-all-in-one-agent.tracing.svc:5778/sampling' \
--set 'additionalArguments[8]=--tracing.jaeger.localAgentHostPort=jaeger-all-in-one-agent.tracing.svc:6831'

## Install `Grafana Loki Stack` Chart into `logging` namespace
helm3 upgrade --install loki -n logging --create-namespace grafana/loki-stack --set grafana.enabled=true

####################
# Deployment
####################

### It can be deployed by using a yaml file with all the manifests.
kubectl apply -n logging -f Kubernetes/files/eck.yaml

## Apply flow (ClusterFlow) and output (elasticsearch) manifests to monitor kubernetes cluster entirely (Check manifest yaml file)
kubectl apply -n logging -f Kubernetes/files/logging.yaml

# Create Jaeger all-in-once inmemory instace with agents, collector, querier and backend

## Configure OPENTRACING_JAEGER_ENABLED (examples/deployments/01-simple-spring-boot-tracing/deployment.yaml) to false using sidecar
#kubectl apply -n tracing -f Kubernetes/files/jaeger-sidecar.yaml

## Configure OPENTRACING_JAEGER_ENABLED (examples/deployments/01-simple-spring-boot-tracing/deployment.yaml) to true using daemonset
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