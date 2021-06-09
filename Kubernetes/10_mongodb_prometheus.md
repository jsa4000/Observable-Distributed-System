# MongoDB Prometheus

MongoDB® is a cross-platform document-oriented database. Classified as a NoSQL database, MongoDB® eschews the traditional table-based relational database structure in favor of JSON-like documents with dynamic schemas, making the integration of data in certain types of applications easier and faster.

## Deployment

This procedure will install `MongoDB` and `MongoDB Exporter` Separately. Finally a ServiceMonitor resource is created to scrape MongoDB metrics form an existing Prometheus release using `prometheus-operator` CRDs.

> Some Mongodb installations procedures may install new prometheus releases using `prometheus-operator`. Since Prometheus is a **decentralized** system it recommends to create new Prometheus instances instead reusing other mixing applications, namespaces, components, etc...

### MongoDB

There are several options to deploy mongodb in a Kubernetes Cluster:

* [Via Helm Charts](https://github.com/bitnami/charts/tree/master/bitnami/mongodb)
* [Community Mongo Operator](https://github.com/mongodb/mongodb-kubernetes-operator)
* [The Enterprise Mongo Operator](https://docs.mongodb.com/kubernetes-operator/master/)
* Manifest YAML files

Following process describes how to deploy Mongodb using Helm charts:

* Configure **Bitnami** Helm repo

    `helm3 repo add bitnami https://charts.bitnami.com/bitnami`

* Upgrade helm charts

    `helm3 repo update`

* Installing the Chart

    `helm3 install mongo --namespace datastore --create-namespace bitnami/mongodb --version 10.19.0 --set architecture=standalone`

### MongoDB Exporter

Following process describes how to deploy Mongodb Exporter using Helm charts:

* Configure **Prometheus Community** Helm repo

    `helm3 repo add prometheus-community https://prometheus-community.github.io/helm-charts`

* Upgrade helm charts

    `helm3 repo update`

* Installing the Chart

    > The chart also creates a `ServiceMonitor` resource attached with the `release` prometheus instance selected.

    ```bash
    helm3 install prometheus-mongodb-exporter --namespace datastore prometheus-community/prometheus-mongodb-exporter --version 2.8.1 \
    --set mongodb.uri=mongo-mongodb.datastore.svc.cluster.local,serviceMonitor.additionalLabels.release=prometheus
    ```

## Test

### MongoDB Test

MongoDB can be accessed on the following DNS `mongo-mongodb.datastore.svc.cluster.local` and ports from within the cluster.

To get the root password run the following command.

`export MONGODB_ROOT_PASSWORD=$(kubectl get secret --namespace datastore mongo-mongodb -o jsonpath="{.data.mongodb-root-password}" | base64 --decode)`

To connect to your database, create a MongoDB(R) client container:

`kubectl run --namespace datastore mongo-mongodb-client --rm --tty -i --restart='Never' --env="MONGODB_ROOT_PASSWORD=$MONGODB_ROOT_PASSWORD" --image docker.io/bitnami/mongodb:4.4.6-debian-10-r8 --command -- bash`

Then, run the following command:

`mongo admin --host "mongo-mongodb" --authenticationDatabase admin -u root -p $MONGODB_ROOT_PASSWORD`

To connect to your database from outside the cluster execute the following commands:

`kubectl port-forward --namespace datastore svc/mongo-mongodb 27017:27017`

`mongo --host 127.0.0.1 --authenticationDatabase admin -p $MONGODB_ROOT_PASSWORD`

> Instead using terminal tools there are other UI tools such as [Robo 3T](https://robomongo.org/)

### MongoB Exporter test

Use kubernetes port-forward to expose MongoB Exporter

`kubectl port-forward -n datastore service/prometheus-mongodb-exporter 9216`

Use following command or [URL](http://localhost:9216/metrics) to get the metrics.

`curl http://localhost:9216/metrics`

Check Targets available in [Prometheus dashboard](http://localhost:9090/targets)

`kubectl port-forward -n monitoring svc/prometheus-kube-prometheus-prometheus 9090`

Add [Grafana dashboard](http://localhost:3000) using the [MongoDB Exporter dashboard - 2583](https://grafana.com/grafana/dashboards/2583)

> Grafana dashboard credentials (`admin/prom-operator`)

`kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80`