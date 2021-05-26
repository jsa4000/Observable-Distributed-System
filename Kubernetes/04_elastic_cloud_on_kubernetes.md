# Elastic Cloud on Kubernetes (ECK)

[Elastic Cloud on Kubernetes](ttps://github.com/elastic/cloud-on-k8s) automates the deployment, provisioning, management, and orchestration of Elasticsearch, Kibana, APM Server, Enterprise Search, and Beats on Kubernetes based on the operator pattern.

Current features:

* Elasticsearch, Kibana, APM Server, Enterprise Search, and Beats deployments
* TLS Certificates management
* Safe Elasticsearch cluster configuration & topology changes
* Persistent volumes usage
* Custom node configuration and attributes
* Secure settings keystore updates

Supported versions:

* kubectl 1.11+
* Kubernetes 1.12+ or OpenShift 3.11+
* Google Kubernetes Engine (GKE), Azure Kubernetes Service (AKS), and Amazon Elastic Kubernetes Service (EKS)
* Elasticsearch, Kibana, APM Server: 6.8+, 7.1+
* Enterprise Search: 7.7+
* Beats: 7.0+

## Deploy ECK in your Kubernetes Cluster

Read the upgrade notes first if you are attempting to upgrade an existing ECK deployment.

* If you are using GKE, make sure your user has cluster-admin permissions. For more information, see Prerequisites for using Kubernetes RBAC on GKE.
* If you are using Amazon EKS, make sure the Kubernetes control plane is allowed to communicate with the Kubernetes nodes on port 443. This is required for communication with the Validating Webhook. For more information, see Recommended inbound traffic.
* Refer to Install ECK for more information on installation options.

Steps to install ECK Operator using **OLM**:

1. Install custom resource definitions and the operator with its RBAC rules:

    `kubectl apply -f https://download.elastic.co/downloads/eck/1.6.0/all-in-one.yaml`

2. Get Operator name deployed:

    `kubectl get pods -n elastic-system`

3. Monitor the operator logs:

    `kubectl -n elastic-system logs elastic-operator-0 -f`

Steps to install ECK Operator using **Helm Charts**:

1. Add and update repositories for the ECK helm charts

    `helm3 repo add elastic https://helm.elastic.co`

    `helm3 repo update`

2. Install Helm Chart

    > By using `helm3 search repo` it can be seen all the charts in repos added. IT can be sen the name and current version

    `helm3 install elastic-operator elastic/eck-operator -n elastic-system --create-namespace --version 1.6.0`

    Using `logging` namespace instead

    `helm3 install elastic-operator elastic/eck-operator -n logging --create-namespace --version 1.6.0`

## Create logging namespace

> If `logging` namespace was not created previously, it is needed to be created onto the cluster.

It is always a good practice to create a custom namespace depending on the system, to maintain the cluster organized.

1. Create the namespace

    `kubectl create ns logging`

1. Swith to `logging` namespace

    `kubectl config set-context --current --namespace=logging`

## Deploy an Elasticsearch Cluster

Apply a simple Elasticsearch cluster specification, with one Elasticsearch node:

> If your Kubernetes cluster does not have any Kubernetes nodes with at least 2GiB of free memory, the pod will be stuck in Pending state. See Manage compute resources for more information about resource requirements and how to configure them.

```bash
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
```

>  Setting `node.store.allow_mmap: false` has performance implications and should be tuned for production workloads as described in the Virtual memory section.

## Monitor Cluster Health and Creation Progress

Get an overview of the current Elasticsearch clusters in the Kubernetes cluster, including health, version and number of nodes:

`kubectl get -n logging elasticsearch`

```bash
NAME          HEALTH    NODES     VERSION   PHASE         AGE
elastic-cluster    green     1         7.12.1     Ready         1m
```

> Since the namespace has not been changed, elastic search will be deployed within the `default` or current namespace

When you create the cluster, there is no `HEALTH` status and the `PHASE` is empty. After a while, the `PHASE` turns into `Ready`, and `HEALTH` becomes `green`. The `HEALTH` status comes from Elasticsearch’s cluster health API.

You can see that one Pod is in the process of being started:

`kubectl get pods -n logging --selector='elasticsearch.k8s.elastic.co/cluster-name=elastic-cluster' -w`

```bash
NAME                      READY   STATUS    RESTARTS   AGE
elastic-cluster-es-default-0   1/1     Running   0          79s
```

Access the logs for that Pod:

`kubectl logs -n logging elastic-cluster-es-default-0 -f`

## Request Elasticsearch Access

A ClusterIP Service is automatically created for your cluster:

`kubectl get -n logging service elastic-cluster-es-http`

```bash
NAME                 TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
elastic-cluster-es-http   ClusterIP   10.15.251.145   <none>        9200/TCP   34m
```

1. Get the credentials.

    A default user named elastic is automatically created with the password stored in a Kubernetes secret:

    `PASSWORD=$(kubectl get secret -n logging elastic-cluster-es-elastic-user -o go-template='{{.data.elastic | base64decode}}')`

2. Request the Elasticsearch endpoint.

    From inside the Kubernetes cluster:

    `curl -u "elastic:$PASSWORD" -k "https://elastic-cluster-es-http:9200"`

    From your local workstation, use the following command in a separate terminal:

    `kubectl port-forward -n logging service/elastic-cluster-es-http 9200`

    Then request localhost:

    `curl -u "elastic:$PASSWORD" -k "https://localhost:9200"`

1. Disabling certificate verification using the -k flag is not recommended and should be used for testing purposes only. See: Setup your own certificate

    ```json
    {
    "name" : "elastic-cluster-es-default-0",
    "cluster_name" : "elastic-cluster",
    "cluster_uuid" : "Yj5VMoaBQoSwAL6BhcYHNQ",
    "version" : {
        "number" : "7.12.1",
        "build_flavor" : "default",
        "build_type" : "docker",
        "build_hash" : "3186837139b9c6b6d23c3200870651f10d3343b7",
        "build_date" : "2021-04-20T20:56:39.040728659Z",
        "build_snapshot" : false,
        "lucene_version" : "8.8.0",
        "minimum_wire_compatibility_version" : "6.8.0",
        "minimum_index_compatibility_version" : "6.0.0-beta1"
    },
    "tagline" : "You Know, for Search"
    }
    ```

## Deploy a Kibana instance

To deploy your Kibana instance go through the following steps.

1. Specify a Kibana instance and associate it with your Elasticsearch cluster:

```bash
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
```

2. Monitor Kibana health and creation progress.

    Similar to Elasticsearch, you can retrieve details about Kibana instances:

    `kubectl get -n logging kibana`

    And the associated Pods:

    `kubectl get pod -n logging --selector='kibana.k8s.elastic.co/name=kibana-cluster' -w`

    ```bash
    NAME                                  READY   STATUS    RESTARTS   AGE
    elastic-cluster-kb-564bdbb757-n8xvr   1/1     Running   0          2m14s
    ```

3. Access Kibana.

    A ClusterIP Service is automatically created for Kibana:

    `kubectl get service -n logging kibana-cluster-kb-http`

    Use kubectl port-forward to access Kibana from your local workstation:

    `kubectl port-forward -n logging service/kibana-cluster-kb-http 5601`

    Open https://localhost:5601 in your browser. Your browser will show a warning because the self-signed certificate configured by default is not verified by a known certificate authority and not trusted by your browser. You can temporarily acknowledge the warning for the purposes of this quick start but it is highly recommended that you configure valid certificates for any production deployments.

    Login as `elastic` user. The password can be obtained with the following command:

    `kubectl get secret -n logging elastic-cluster-es-elastic-user -o=jsonpath='{.data.elastic}' | base64 --decode; echo`

## Deploy using yaml manifest

> This deployment method is equal to the previous once, however this uses a YAML manifest file instead with all the needed dependencies

Deploy the ECK resources (elastic + kibana) using the following command.

`kubectl apply -n logging -f Kubernetes/files/eck.yaml`
