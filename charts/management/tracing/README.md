# Management installation

## Helm (Pre-requisites)

Following dependencies are needed

- Helm (*current v2.13.1*)

Install `tiller` into kubernetes cluster

```bash
kubectl -n kube-system create sa tiller
kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
helm init --service-account tiller
```

## Installation

Installation of tracing chart will install a jaeger operator and a jaeger instance.

```bash
#Update Helm dependencies
helm dependency update

# Install current chart
helm install --name tracing --namespace tracing .

# Create jaeger instance using elastic search
kubectl apply -n tracing -f tracing/jaeger-elasticsearch.yaml
```