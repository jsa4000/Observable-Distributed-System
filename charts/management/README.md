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

## Ingress Controller

Installation of Ingress-controller will install a traefik ingress.

```bash
# Install current chart
helm install --dep-up --name traefik-ingress --namespace kube-system ingress-controller

# Install using another domain
helm install --dep-up --name traefik-ingress --namespace kube-system --set traefik.dashboard.domain=traefik.management.com .
```

### Upgrade values to support tracing with jaeger

```bash
# Upgrade from previous
helm upgrade traefik-ingress --values ingress-controller/values-tracing.yaml ingress-controller

# Install
helm install --dep-up --name traefik-ingress --namespace kube-system --values ingress-controller/values-tracing.yaml ingress-controller
```

## Logging

- Installation of logging chart will install a `elastic-search`, `kibana` and `fluent-bit` ingress.

    > the URLs will be '{chartname}-elasticsearch-client'

    ```bash
    # Install current chart
    helm install --dep-up --name logging --namespace logging logging

    # Install using another domain and change other hostsnames
    helm install --dep-up --name logging --namespace logging . --set kibana.ingress.hosts={kibana.logging.com},elasticsearch.client.ingress.hosts={elasticsearch.logging.com},fluent-bit.backend.es.host=logging-elasticsearch-client,kibana.env.ELASTICSEARCH_HOSTS=http://logging-elasticsearch-client:9200 .
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
  
  ```bash
  helm delete logging --purge
  # Remove all the charts
  helm delete $(helm list -q) --purge
  ```

## Metrics

- Installation of Metrics will install a `Prometheus` and `Grafana` charts.

    ```bash
    # Install current chart
    helm install --dep-up --name metrics --namespace metrics metrics

    # Install using another domain
    helm install --name metrics --namespace metrics . --set grafana.ingress.hosts={grafana.monitoring.com},prometheus.server.ingress.hosts={prometheus.monitoring.com},prometheus.alertmanager.ingress.hosts={alertmanager.monitoring.com} .

    ```

- Get the credentials to access grafana dashboard (user `admin`)

    ```bash
    kubectl get secret --namespace metrics metrics-grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo
    ```

- Set `prometheus` datasource and the dashboards into grafana (http://grafana.monitoring.com/)

  > Use `http://metrics-prometheus-server.metrics.svc.cluster.local` as URL in grafana datasource
  
  - Node eXporter: 405
  - Cadvisor: 893
  - Kubernetes cluster monitoring: 1621 (315 preferred)
  - Traefik dashborad: 4475
  - Kubernetes Cluster (Prometheus): 6417 (8685 preferred)
  - Kubernetes cluster monitoring (via Prometheus): 
  - Analysis by Pod: 6879
  - Cluster cost & utilization metrics: 8670
  - K8s Cluster Summary : 8685 (by Node, Deployments, namespaces, Pods and containers)
  - Kubernetes App Metrics: 1471 (cpu, memory compared to limits, etc..)
  - Kubernetes Cluster (workload): 7249 (Good summary, however it must be split by namespace, deployment, pods, etc..)
  - K8/Openshift Projects: 8184 (Good summary by namespace)

  - JVM (Micrometer): 4701
  - Spring Boot Statistics: 6756 (https://grafana.com/dashboards/6756)

## Tracing

- Installation of tracing chart will install a jaeger operator and a jaeger instance.

    ```bash
    #Update Helm dependencies
    helm dependency update

    # Install current chart with the jaeger operator
    helm install --dep-up --name tracing --namespace tracing tracing

    # Create jaeger instance using elastic search
    kubectl apply -n tracing -f tracing/jaeger-elasticsearch.yaml
    ```

- In order to connect through jeager ui and using port-forward (via [localhost](http://localhost:16686/))

    ```bash
    kubectl port-forward svc/jaeger-query -n tracing 16686:16686
    ```

## Local Host

Configure following DNS mapping on your machine hosts file `/etc/hosts`

```txt
127.0.0.1   traefik.management.com
127.0.0.1   grafana.monitoring.com
127.0.0.1   prometheus.monitoring.com
127.0.0.1   alertmanager.monitoring.com
127.0.0.1   elasticsearch.logging.com
127.0.0.1   kibana.logging.com
```
