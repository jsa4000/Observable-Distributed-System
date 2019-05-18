# Observable-Distributed-System

This repository is a project to show how to build an observable distributed system using common standards and frameworks

## Kubernetes

### Helm

**Helm** is a tool for managing Kubernetes charts. Charts are packages of pre-configured Kubernetes resources.

- Find and use popular software packaged as Helm charts to run in Kubernetes
- Share your own applications as Helm charts
- Create reproducible builds of your Kubernetes applications
- Intelligently manage your Kubernetes manifest files
- Manage releases of Helm packages

#### Helm Installation

- Download helm from official repository

    ```bash
    # Following will install the latest version, for production use specific version
    curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get
    ```

- Initialize `helm` to deploy the necessary pods into eks cluster

    ```bash
    kubectl -n kube-system create sa tiller

    kubectl create clusterrolebinding tiller --clusterrole cluster-admin --serviceaccount=kube-system:tiller

    helm init --service-account tiller
    ```

- Verify helm is correctly installed

    ```bash
    helm version
    ```

- To **remove** `tiller` from server

    ```bash
    helm reset --force
    ```

### Traefik Ingress

**Traefik** is a modern HTTP reverse proxy and load balancer that makes deploying microservices easy. Traefik integrates with your existing infrastructure components (Docker, Swarm mode, Kubernetes, Marathon, Consul, Etcd, Rancher, Amazon ECS, ...) and configures itself automatically and dynamically. Pointing Traefik at your orchestrator should be the only configuration step you need.

#### Traefik Installation

- Install `traefik` for ingress controller using helm

    ```bash
    helm install --name traefik-ingress --namespace kube-system --set dashboard.enabled=true,metrics.prometheus.enabled=true,rbac.enabled=true stable/traefik

    # Configured to use with Promtheues 
    helm install --name traefik-ingress --namespace kube-system --set dashboard.enabled=true,metrics.prometheus.enabled=true,rbac.enabled=true,dashboard.domain=traefik.management.com,dashboard.service.annotations."prometheus\.io/scrape"=true,dashboard.service.annotations."prometheus\.io/port"=8080,dashboard.ingress.annotations."kubernetes\.io/ingress\.class"=traefik stable/traefik
    ```

    Output:

    ```txt
    NOTES:

    1. Get Traefik's load balancer IP/hostname:

    NOTE: It may take a few minutes for this to become available.

    You can watch the status by running:

            $ kubectl get svc traefik-ingress --namespace kube-system -w

    Once 'EXTERNAL-IP' is no longer '<pending>':

            $ kubectl describe svc traefik-ingress --namespace kube-system | grep Ingress | awk '{print $3}'

    1. Configure DNS records corresponding to Kubernetes ingres  resources to point to the load balancer IP/hostname found in step 1
    ```

    > Wait until de the `EXTERNAL-IP` is no longer `<pending>`, like it is noted after installing the chart.

- Check the configuration for `traefik-ingress`

    ```bash
        kubectl describe svc traefik-ingress --namespace kube-system
        kubectl get pod,svc --all-namespaces
    ```

### Prometheus and Grafana

- Install `Prometheus`

    ```bash
    helm install --name prometheus --namespace monitoring --set server.ingress.enabled=true,server.ingress.annotations."kubernetes\.io/ingress\.class"=traefik,server.ingress.hosts={prometheus.monitoring.com},alertmanager.ingress.enabled=true,alertmanager.ingress.annotations."kubernetes\.io/ingress\.class"=traefik,alertmanager.ingress.hosts={alertmanager.monitoring.com} stable/prometheus
    ```

    > The Prometheus server can be accessed via port 80 on the following DNS name from within your cluster: `prometheus-server.monitoring.svc.cluster.local`

- Check all the resources (pods, services, pvc, pc, ingress,etc..) haven been **successfully** created.

    ```bash
    kubectl get all -n monitoring
    ```

    > NOTE: `cAdvisor` (integrated inside `kubelet`), `node-exporter` and `kube-state-metrics` are **eXporters** that expose different metrics with snapshots of the kubernetes cluster.

    ```txt
    service/prometheus-kube-state-metrics   ClusterIP   None             <none>        80/TCP     1m
    service/prometheus-node-exporter        ClusterIP   None             <none>        9100/TCP   1m
    ```

- Install `Grafana` 

    ```bash
    helm install --name grafana-dashboard --namespace monitoring --set persistence.enabled=true,ingress.enabled=true,ingress.annotations."kubernetes\.io/ingress\.class"=traefik,ingress.hosts={grafana.monitoring.com},plugins={grafana-piechart-panel} stable/grafana
    ```

    > It can be added custom plugins via helm chart. i.e `grafana-piechart-panel`

- Get the credentials to access grafana dashboard (`admin`)

    ```bash
    kubectl get secret --namespace monitoring grafana-dashboard -o jsonpath="{.data.admin-password}" | base64 --decode ; echo
    ```

- Set `prometheus` datasource and the dashboards into grafana (http://grafana.monitoring.com/)

  > Use `http://prometheus-server.monitoring.svc.cluster.local` as URL in grafana datasource
  
  - Node eXporter: 405
  - Cadvisor: 893
  - Kubernetes cluster monitoring: 1621
  - Kubernetes Cluster (Prometheus): 6417
  - Kubernetes cluster monitoring (via Prometheus): 315
  - Analysis by Pod: 6879
  - Cluster cost & utilization metrics: 8670
  - K8s Cluster Summary (by Node and Namespaces): 8685
  - Kubernetes App Metrics: 1471 (cpu, memory compared to limits, etc..)
  - Kubernetes Cluster (workload): 7249 (Good summary, split by namespace, deployment, pods, etc..)

- Add `traefik` extra metrics and dashboard (grafana dashboard: 4475)

    ```bash
    kubectl get service -n kube-system
    kubectl get -n kube-system service traefik-ingress-dashboard -o yaml > traefik-service.yaml

    # Open and add modify following information
    # Change - type: ClusterIP to type: NodePort
    # Add    - annotations:
    #             prometheus.io/scrape: "true"
    #             prometheus.io/port: '8080'

    kubectl delete -f traefik-service.yaml
    kubectl apply -f traefik-service.yaml

    # Get the NodePort assigned and http://localhost:nodeport, use /dashboard or /metrics
    kubectl get service -n kube-system traefik-ingress-dashboard
    ```

- **Uninstall** helm packages

        helm del grafana-dashboard --purge
        helm del prometheus --purge
        helm del traefik-ingress --purge

### Logging

### Tracing


### Prometheus Scrape in kubernetes

There are multiple ways to configure `targets` using **Prometheus**. Those targets are scraped (via **pull** method) in order to collect their metrics. The traditional approach is by using **static** configuration, using a configuration file. On the other hand, the **dynamic** approach uses *service discovery* methods to get the online targets. Since the beginning of kubernetes, new ways have appeared to configure endpoints, one of them is by using the `Prometheus Operator` and the other is using `Annotations`, the last one using k8s DSL and service definitions.

#### Annotations

Prometheus scrapes can be added dynamically by configuration (annotations) via DSL and using kubernetes definitions. Refer to this [link](https://www.weave.works/docs/cloud/latest/tasks/monitor/configuration-k8s/) for further information

Annotations on pods allow a fine control of the scraping process:

- `prometheus.io/scrape`: The default configuration will scrape all pods and, if set to `false`, this annotation will exclude the pod from the scraping process.
- `prometheus.io/path`: If the metrics path is not `/metrics`, define it with this annotation.
- `prometheus.io/port`: Scrape the pod on the indicated port instead of the pod’s declared ports (default is a port-free `target` if none are declared).

Following is a real example used to create kube-state-metrics service

```yaml
apiVersion: v1
kind: Service
metadata:
  annotations:
    prometheus.io/scrape: "true"
  labels:
    app: prometheus
    chart: prometheus-8.9.2
    component: kube-state-metrics
    heritage: Tiller
    release: prometheus
  name: prometheus-kube-state-metrics
  namespace: prometheus
spec:
  clusterIP: None
  ports:
  - name: http
    port: 80
    protocol: TCP
    targetPort: 8080
  selector:
    app: prometheus
    component: kube-state-metrics
    release: prometheus
  sessionAffinity: None
  type: ClusterIP
```

### Kubernetes Limits

Resource **Limits** (*cpu* and *memory*) per deployment are exposed via metrics. Those metrics related to the API configuration are collected by `kube-state-metrics`. This eXporter collects the limits configured by a deployment; also it collects many other metrics HPA, status, etc... This is not a daemoner, since all the metrics are the same and it is not needed HA. 

Dashboard and metrics that help us to get this information are:

- Kubernetes App Metrics: 1471 (cpu, memory compared to limits, etc..)
- `container_spec_memory_limit_bytes` vs `container_memory_usage_bytes`
- `container_spec_memory_reservation_limit_bytes`
- `container_cpu_usage_seconds_total` vs `container_spec_cpu_quota` vs `container_spec_cpu_period`

#### MY-SQL

Resources configured by default (`request`) are:

```yaml
## Configure resource requests and limits
## ref: http://kubernetes.io/docs/user-guide/compute-resources/
##
resources:
  requests:
    memory: 256Mi
    cpu: 100m
```

- Create the my-sql using helm:

    ```bash
    helm install --name mysql-release --namespace storage --set mysqlRootPassword=secretpassword,mysqlUser=my-user,mysqlPassword=my-password,mysqlDatabase=my-database stable/mysql
    ```

- Check the current status

    ```bash
    kubectl get all -n storage
    ```

- Query example:

    ```json
    container_spec_memory_reservation_limit_bytes{container_name="mysql-release"}
    container_spec_memory_limit_bytes{container_name="mysql-release"}
    ```