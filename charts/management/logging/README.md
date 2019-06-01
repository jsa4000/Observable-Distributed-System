# Logging installation

## Helm (Pre-requisites)
 
Following dependencies are needed
 
 - Helm (*current v2.13.1*)
 
Install `tiller` into kubernetes cluster
 
```bash
kubectl -n kube-system create sa tiller
kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller
helm init --service-account tiller
```

# Installation

- Installation of logging chart will install a `elastic-search`, `kibana` and `fluent-bit` ingress.

    > the URLs will be '{chartname}-elasticsearch-client'

    ```bash
    #Update Helm dependencies
    helm dependency update
    
    # Install current chart
    helm install --name logging --namespace logging --set fluent-bit.backend.es.host=logging-elasticsearch-client,kibana.env.ELASTICSEARCH_HOSTS=http://logging-elasticsearch-client:9200 .
    
    # Install using another domain
    helm install --name logging --namespace logging . --set kibana.ingress.hosts={kibana.logging.com},elasticsearch.client.ingress.hosts={elasticsearch.logging.com},fluent-bit.backend.es.host=logging-elasticsearch-client,kibana.env.ELASTICSEARCH_HOSTS=http://logging-elasticsearch-client:9200 .
    
    ```

**Wait** until all the pods still pending:

```bash
kubectl get pods -n logging -w
```

**Verify** the current es client can be accessed from ingress:

- http://elasticsearch.logging.com/
- http://elasticsearch.logging.com/_count?pretty (if `port-forward` use http://localhost:9200 )

  ```json
  {
    "count" : 0,
    "_shards" : {
      "total" : 0,
      "successful" : 0,
      "skipped" : 0,
      "failed" : 0
    }
  }
  ```
  
 Access to [kibana.logging.com]() and configure the index and the key (`@timestamp_es`).
 
  - Remove the helm chart
  
         helm delete logging --purge    
         
         # Remove all the charts
         helm delete $(helm list -q) --purge
 
 # Resources
 
 [AWS Open distro for elastic search](https://opendistro.github.io/for-elasticsearch/)