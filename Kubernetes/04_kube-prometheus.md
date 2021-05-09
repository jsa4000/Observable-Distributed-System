# Kube Prometheus

[Prometheus](https://github.com/prometheus/prometheus), a Cloud Native Computing Foundation project, is a systems and service monitoring system. It collects metrics from configured targets at given intervals, evaluates rule expressions, displays the results, and can trigger alerts when specified conditions are observed.

The features that distinguish Prometheus from other metrics and monitoring systems are:

* A multi-dimensional data model (time series defined by metric name and set of key/value dimensions)
* PromQL, a powerful and flexible query language to leverage this dimensionality
* No dependency on distributed storage; single server nodes are autonomous
* An HTTP pull model for time series collection
* Pushing time series is supported via an intermediary gateway for batch jobs
* Targets are discovered via service discovery or static configuration
* Multiple modes of graphing and dashboarding support
* Support for hierarchical and horizontal federation

![Prometheus Architecture](images/prometheus-architecture.png)

[Kube Prometheus](https://github.com/prometheus-operator/kube-prometheus) collects Kubernetes manifests, Grafana dashboards, and Prometheus rules combined with documentation and scripts to provide easy to operate end-to-end Kubernetes cluster monitoring with Prometheus using the Prometheus Operator.

The content of this project is written in jsonnet. This project could both be described as a package as well as a library.

Components included in this package:

* The Prometheus Operator
* Highly available Prometheus
* Highly available Alertmanager
* Prometheus node-exporter
* Prometheus Adapter for Kubernetes Metrics APIs
* kube-state-metrics
* Grafana

This stack is meant for cluster monitoring, so it is pre-configured to collect metrics from all Kubernetes components. In addition to that it delivers a default set of dashboards and alerting rules. Many of the useful dashboards and alerts come from the kubernetes-mixin project, similar to this project it provides composable jsonnet as a library for users to customize to their needs.

## kube Prometheus Stack

[Kube-prometheus stack](https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack) is a collection of Kubernetes manifests, Grafana dashboards, and Prometheus rules combined with documentation and scripts to provide easy to operate end-to-end Kubernetes cluster monitoring with Prometheus using **Kube Prometheus** and the **Prometheus Operator**.

> Note: This chart was formerly named `prometheus-operator` chart, now renamed to more clearly reflect that it installs the kube-prometheus project stack, within which Prometheus Operator is only one component.
Prerequisites

**Pre-requisites**

* Kubernetes 1.16+
* Helm 3+

## kube Prometheus Stack Installation

In order to install kube Prometheus Stack:

1. Add Get Repo to Helm

    `helm3 repo add prometheus-community https://prometheus-community.github.io/helm-charts`

2. Update Helm for new repositories.

    `helm3 repo update`

3. Install `kube-prometheus-stack` Chart into `monitoring` namespaceç

    > Use `helm3 show values prometheus-community/kube-prometheus-stack` to get the default values configuration

    `helm3 install -n monitoring --create-namespace prometheus prometheus-community/kube-prometheus-stack --version 15.4.4`

4. Uninstall chart

    `helm3 uninstall -n monitoring prometheus`

5. Verify the installation by accessing to Prometheus Dashboard http://localhost:9090.

    `kubectl port-forward -n monitoring svc/prometheus-kube-prometheus-prometheus 9090:9090`

## Grafana Dashboards

1. Create a Port forward to access Grafana Dashboard.

    `kubectl port-forward -n monitoring svc/prometheus-grafana 8080:80`

2. Access to Grafana using http://localhost:8080 (`admin/prom-operator`)