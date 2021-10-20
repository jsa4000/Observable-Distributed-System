#!/bin/bash

# Export manifest path
export MANIFEST_DIR=../../../kubernetes/manifests

## Install `traefik` Chart into `tools` namespace
helm3 install -n tools --create-namespace traefik traefik/traefik --version 10.3.2 -f $MANIFEST_DIR/traefik-values.yaml

## Install `kube-prometheus-stack` Chart into `monitoring` namespace
helm3 install -n monitoring --create-namespace prometheus prometheus-community/kube-prometheus-stack --version 18.0.6 \
--set 'prometheus-node-exporter.hostRootFsMount=false'

# Install the ECK Operator (Elastic Cloud on Kubernetes: Elastic + Kibana)      
helm3 install elastic-operator elastic/eck-operator -n logging --create-namespace --version 1.7.1

## Install Logging Operator using helm3 (v3.14.2)
helm3 install logging-operator banzaicloud-stable/logging-operator -n logging --create-namespace --version 3.14.2 \
--set 'createCustomResource=false'

## Install `Grafana Loki Stack` Chart into `logging` namespace
helm3 upgrade --install loki -n logging --create-namespace grafana/loki-stack --version 2.4.1 --set grafana.enabled=true

## Install `jaeger-operator` Chart into `tracing` namespace
helm3 install -n tracing --create-namespace jaeger-operator jaegertracing/jaeger-operator --version 2.24.0

# Install MongoDB chart into datastore namespace
helm3 install mongo --namespace datastore --create-namespace bitnami/mongodb --version 10.19.0 -f $MANIFEST_DIR/mongodb-values.yaml

## Instal MongoDB Exporterr into datastore using previous prometheus release installed (kube-prometheus-stack)
helm3 install prometheus-mongodb-exporter --namespace datastore prometheus-community/prometheus-mongodb-exporter --version 2.8.1 \
--set 'mongodb.uri=mongodb://monitor:password@mongo-mongodb.datastore.svc.cluster.local:27017/admin' \
--set 'serviceMonitor.additionalLabels.release=prometheus'