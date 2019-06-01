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

# Ingress Controller

Installation of Ingress-controller will install a traefik ingress.

```bash
#Update Helm dependencies
helm dependency update

# Install current chart
helm install --name traefik-ingress --namespace kube-system .

# Install using another domain
helm install --name traefik-ingress --namespace kube-system --set traefik.dashboard.domain=traefik.management.com .

```