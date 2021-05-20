
####################
# Initialize
####################

# Add Helm Repositories
helm3 repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm3 repo add elastic https://helm.elastic.co
helm3 repo add banzaicloud-stable https://kubernetes-charts.banzaicloud.com
helm3 repo add jaegertracing https://jaegertracing.github.io/helm-charts
helm3 repo add traefik https://helm.traefik.io/traefik

# Update Repositories
helm3 repo update

####################
# Installation
####################

## Install `kube-prometheus-stack` Chart into `monitoring` namespace
helm3 install -n monitoring --create-namespace prometheus prometheus-community/kube-prometheus-stack --version 15.4.4 \
--set 'prometheus-node-exporter.hostRootFsMount=false'

# Install the ECK Operator (Elastic Cloud on Kubernetes: Elastic + Kibana)
helm3 install elastic-operator elastic/eck-operator -n logging --create-namespace --version 1.5.0

## Install Logging Operator using helm3 (v3.9.4)
helm3 install logging-operator banzaicloud-stable/logging-operator -n logging --create-namespace --version 3.9.4 \
--set 'createCustomResource=false'

## Install `jaeger-operator` Chart into `tracing` namespace
helm3 install -n tracing --create-namespace jaeger-operator jaegertracing/jaeger-operator --version 2.21.0

## Install `traefik` Chart into `tools` namespace
helm3 install -n tools --create-namespace traefik traefik/traefik --version 9.19.1 \
--set 'additionalArguments[0]=--api.insecure' \
--set 'additionalArguments[1]=--metrics.prometheus=true' \
--set 'additionalArguments[2]=--tracing.jaeger=true' \
--set 'additionalArguments[3]=--tracing.serviceName=traefik-service' \
--set 'additionalArguments[4]=--tracing.jaeger.samplingParam=1.0' \
--set 'additionalArguments[5]=--tracing.jaeger.disableAttemptReconnecting=false' \
--set 'additionalArguments[6]=--tracing.jaeger.samplingType=const' \
--set 'additionalArguments[7]=--tracing.jaeger.samplingServerURL=http://jaeger-all-in-one-inmemory-agent.tracing.svc:5778/sampling' \
--set 'additionalArguments[8]=--tracing.jaeger.localAgentHostPort=jaeger-all-in-one-inmemory-agent.tracing.svc:6831'

####################
# Deployment
####################

### It can be deployed by using a yaml file with all the manifests.
kubectl apply -n logging -f Kubernetes/files/eck.yaml

## Apply flow (ClusterFlow) and output (elasticsearch) manifests to monitor kubernetes cluster entirely (Check manifest yaml file)
kubectl apply -n logging -f Kubernetes/files/logging.yaml

# Create Jaeger all-in-once inmemory instace with agents, collector, querier and backend
kubectl apply -n tracing -f Kubernetes/files/jaeger-inmemory.yaml

## Deploy the prometheus-operator `ServiceMonitor` to monitor trraefik form prometheus
kubectl apply -n tools -f Kubernetes/files/traefik-service-monitor.yaml

####################
# Application
####################

# Create the 'micro' namespace
kubectl create namespace micro

# Deploy the Application example
kubectl apply -n micro -f examples/deployments/01-simple-spring-boot-tracing
