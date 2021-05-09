
####################
# Initialize
####################

# Install Operator LifeCycle Manager (OLM)
kubectl apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.1/crds.yaml
kubectl apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.1/olm.yaml

## Check OLM installation (Wait until all the pod are running. Use '-f' to watch the progress in real-time)
kubectl get pods -n olm

####################
# Logging
####################

# Install the ECK Operator (Elastic Cloud on Kubernetes: Elastic + Kibana)
kubectl apply -f https://download.elastic.co/downloads/eck/1.5.0/all-in-one.yaml

## Check the operator is currently installed and running
kubectl get pods -n elastic-system

## Check the logs the pod is generating
kubectl -n elastic-system logs elastic-operator-0 -f

# Create logging namespace
kubectl create ns logging

## Check if the namespace has been created
kubectl get namespace

## Switch to the current namespace 
kubectl config set-context --current --namespace=logging

# Deploy Elastic Cloud Cluster

## Deploy an Elasticsearch instance

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

## Check Elastic and Kibana are running
kubectl get pods

## Access to kibana dashboard 

### The the name service
kubectl get service kibana-cluster-kb-http

### Create a port forward to kibana service at port 5601
kubectl port-forward service/kibana-cluster-kb-http 5601

### Get the password for 'elastic' user
kubectl get secret elastic-cluster-es-elastic-user -o=jsonpath='{.data.elastic}' | base64 --decode; echo

### Access to the dashboard at https://localhost:5601 using (user: elastic) and user and password previously mentioned.

# Install Logging Operator

## Add Logging Operator helm repo from `banzaicloud.com`

helm3 repo add banzaicloud-stable https://kubernetes-charts.banzaicloud.com

## Install Logging Operator using helm3 (v3.9.4)

helm3 install logging-operator banzaicloud-stable/logging-operator --version 3.9.4 --set createCustomResource=false

## Verify installation checking all the pods are running (Use -w to wait until all the posd are running)

kubectl get pods

## Apply flow and output manifest to monitor kubernetes cluster entirely (Check manifest file)

kubectl apply -f Kubernetes/files/logging.yaml