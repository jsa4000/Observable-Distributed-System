# Microservice Template Chart

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

- Update dependencies within `requirements.yaml`

    helm dependency update
    
  > Dependencies are downloaded into `charts` folder in `tgz` format.

- Deploy micro-service using current helm chart.

  > It can be used also --set to set additional properties in addition to the values file.

    helm install --debug --dry-run --name micro-hello-world --namespace dev-micro -f values.yaml .
    
    helm install --name micro-hello-world --namespace dev-micro -f values.yaml .
    
    # Install from other location
    # helm install --name charge-microservice --namespace dev-micro -f ../../charge-microservice/values.yaml .
    
- Upgrade a helm chart image version

    helm upgrade micro-hello-world -f values.yaml --set image.tag1.0.1.RELEASE .
    
> Previous configuration file is needed to get the proper state.

 - Remove the helm chart
 
        helm delete micro-hello-world --purge    
        
        # Remove all the charts
        helm delete $(helm list -q) --purge
    
- Verify chart installation

        kubectl get pods -n dev-micro
