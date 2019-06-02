# Metrics installation

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

- Installation of Metrics will install a `Prometheus` and `Grafana` charts.

    ```bash
    #Update Helm dependencies
    helm dependency update

    # Install current chart
    helm install --name metrics --namespace metrics .

    # Install using another domain
    helm install --name metrics --namespace metrics . --set grafana.ingress.hosts={grafana.monitoring.com},prometheus.server.ingress.hosts={prometheus.monitoring.com},prometheus.alertmanager.ingress.hosts={alertmanager.monitoring.com} .

    ```

- Get the credentials to access grafana dashboard (`admin`)

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