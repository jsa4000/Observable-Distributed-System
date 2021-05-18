
####################
# Initialize
####################

# This initialization is needed in order to install operators that are built using OperatorSDK and OLM.

# Install Operator LifeCycle Manager (OLM)

## By using YAML manifest files
kubectl apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.1/crds.yaml
kubectl apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.1/olm.yaml

## By using Script file
curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.1/install.sh | bash -s v0.18.1

####################
# Logging
####################

# Install the ECK Operator (Elastic Cloud on Kubernetes: Elastic + Kibana)

## OLM Method
kubectl apply -f https://download.elastic.co/downloads/eck/1.5.0/all-in-one.yaml

## Helm charts Method(into logging namespace instead)

### Addd ellastic repo to Helm
helm3 repo add elastic https://helm.elastic.co
helm3 repo update

### Install Helm Chart using specific version and default values
helm3 install elastic-operator elastic/eck-operator -n logging --create-namespace --version 1.5.0

# Create logging namespace and Switch to the current namespace (only if not exists)
kubectl create ns logging
kubectl config set-context --current --namespace=logging

# Deploy Elastic Cloud Cluster

## Deploy an Elasticsearch instance

### It can be deployed by using a yaml file with all the manifests.

kubectl apply -n logging -f Kubernetes/files/eck.yaml

### or using bash commands to create the manifests

cat <<EOF | kubectl apply -f -
apiVersion: elasticsearch.k8s.elastic.co/v1
kind: Elasticsearch
metadata:
  name: elastic-cluster
spec:
  version: 7.12.1
  nodeSets:
  - name: default
    count: 1
    config:
      node.store.allow_mmap: false
EOF

## Deploy a Kibana instance

cat <<EOF | kubectl apply -f -
apiVersion: kibana.k8s.elastic.co/v1
kind: Kibana
metadata:
    name: kibana-cluster
spec:
    version: 7.12.1
    count: 1
    elasticsearchRef:
        name: elastic-cluster
EOF

# Install Logging Operator

## Add Logging Operator helm repo from `banzaicloud.com`

helm3 repo add banzaicloud-stable https://kubernetes-charts.banzaicloud.com

## Install Logging Operator using helm3 (v3.9.4)

helm3 install logging-operator banzaicloud-stable/logging-operator -n logging --create-namespace --version 3.9.4 --set createCustomResource=false

## Apply flow (ClusterFlow) and output (elasticsearch) manifests to monitor kubernetes cluster entirely (Check manifest yaml file)

kubectl apply -n logging -f Kubernetes/files/logging.yaml

## Create an index pattern using `fluentd*` and using `time` as the primary time field

####################
# Monitoring
####################

# Install Kube Prometheus Stack

## Add Git Repo to Helm

helm3 repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm3 repo update

## Install `kube-prometheus-stack` Chart into `monitoring` namespace

helm3 install -n monitoring --create-namespace prometheus prometheus-community/kube-prometheus-stack --version 15.4.4 --set prometheus-node-exporter.hostRootFsMount=false

## This will create the edfault components such as Prometheus, Grafana, NodeExporter, Kubestatemetrics, Prometheus-operator, etc..
## From now crd manifest will be required such as servicemonitor, thanos, etc.

####################
# Tracing
####################

# Install Jaeger Operator v 1.X (Without OpenTelemetry support)

## Add Git Repo to Helm

helm3 repo add jaegertracing https://jaegertracing.github.io/helm-charts
helm3 repo update

## Install `jaeger-operator` Chart into `tracing` namespace

helm3 install -n tracing --create-namespace jaeger-operator jaegertracing/jaeger-operator --version 2.21.0

## Create inmemry/daemon instence for Jaeeger

cat <<EOF | kubectl apply -n tracing -f -
apiVersion: jaegertracing.io/v1
kind: Jaeger
metadata:
  name: jaeger-all-in-one-inmemory
  agent:
    strategy: DaemonSet
EOF

# Create Jaeger all-in-once inmemory instace with agents, collector, querier and backend
kubectl apply -n tracing -f Kubernetes/files/jaeger-inmemory.yaml

####################
# Ingress
####################

# Install Traefik Controller

## Add Git Repo to Helm

helm3 repo add traefik https://helm.traefik.io/traefik
helm3 repo update

## Install `traefik` Chart into `tools` namespace
helm3 install -n tools --create-namespace traefik traefik/traefik --version 9.19.1 \
--set 'additionalArguments[0]=--api.insecure' \
--set 'additionalArguments[1]=--metrics.prometheus=true' \
--set 'additionalArguments[2]=--tracing.jaeger=true' \
--set 'additionalArguments[3]=--tracing.serviceName=traefik-service' \
--set 'additionalArguments[4]=--tracing.jaeger.samplingParam=1.0' \
--set 'additionalArguments[5]=--tracing.jaeger.samplingType=const' \
--set 'additionalArguments[6]=--tracing.jaeger.samplingServerURL=http://jaeger-all-in-one-inmemory-agent.tracing.svc:5778/sampling' \
--set 'additionalArguments[7]=--tracing.jaeger.localAgentHostPort=jaeger-all-in-one-inmemory-agent.tracing.svc:6831'

## Deploy the prometheus-operator `ServiceMonitor` to monitor trraefik form prometheus
kubectl apply -n tools -f Kubernetes/files/traefik-service-monitor.yaml

####################
# Application
####################

# Create the 'micro' namespace
kubectl create namespace micro

# Deploy the Application example
kubectl apply -n micro -f examples/deployments/01-simple-spring-boot-tracing

# Delete the Application example
kubectl delete -n micro -f examples/deployments/01-simple-spring-boot-tracing