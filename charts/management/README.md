# Installation

## Helm (Pre-requisites)

Following dependencies are needed

 - Helm (*current v2.13.1*)
 
Install `tiller` into kubernetes cluster
 
```bash
kubectl -n kube-system create sa tiller
kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
helm init --service-account tiller
```

