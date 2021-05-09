# Operator Lifecycle Manager (OLM)

OLM extends Kubernetes to provide a declarative way to install, manage, and upgrade Operators and their dependencies in a cluster. It provides the following features:

**Over-the-Air Updates and Catalogs**

Kubernetes clusters are being kept up to date using elaborate update mechanisms today, more often automatically and in the background. Operators, being cluster extensions, should follow that. OLM has a concept of catalogs from which Operators are available to install and being kept up to date. In this model OLM allows maintainers granular authoring of the update path and gives commercial vendors a flexible publishing mechanism using channels.

**Dependency Model**

With OLMs packaging format Operators can express dependencies on the platform and on other Operators. They can rely on OLM to respect these requirements as long as the cluster is up. In this way, OLMs dependency model ensures Operators stay working during their long lifecycle across multiple updates of the platform or other Operators.

**Discoverability**

OLM advertises installed Operators and their services into the namespaces of tenants. They can discover which managed services are available and which Operator provides them. Administrators can rely on catalog content projected into a cluster, enabling discovery of Operators available to install.

**Cluster Stability**

Operators must claim ownership of their APIs. OLM will prevent conflicting Operators owning the same APIs being installed, ensuring cluster stability.

**Declarative UI controls**

Operators can behave like managed service providers. Their user interface on the command line are APIs. For graphical consoles OLM annotates those APIs with descriptors that drive the creation of rich interfaces and forms for users to interact with the Operator in a natural, cloud-like way.

## Prerequisites

Following are the pre-requisites to install OLM

* git
* go version v1.12+.
* docker version 17.03+.
* Alternatively podman v1.2.0+ or buildah v1.7+
* kubectl version v1.11.3+.
* Access to a Kubernetes v1.11.3+ cluster.

## Install OLM

A quick way to install OLM on a Kubernetes cluster with default settings and appropriate permission is by running these commands:

`kubectl apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.1/crds.yaml`

`kubectl apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.1/olm.yaml`

This will deploy OLM, which consists of several Operators (catalog-operator, olm-operator, operatorhubio-catalog and packageserver) running in Pods in the `olm` namespace. OLM is now ready to run.  

`kubectl get pods -n olm`

```bash
NAME                                READY   STATUS    RESTARTS   AGE
catalog-operator-7bf8d64748-dxgtr   1/1     Running   0          98s
olm-operator-78cf69458f-495dj       1/1     Running   0          98s
operatorhubio-catalog-p24dg         1/1     Running   0          93s
packageserver-9d57f94ff-86qbg       1/1     Running   0          93s
packageserver-9d57f94ff-hcpng       1/1     Running   0          93s
```

If you receive permission errors running the above command referring to a failed attempt to grant extra privileges to the `system:controller:operator-lifecycle-manager` service account, please kubectl delete -f the above commands and add more privileges to your current user:

`kubectl create clusterrolebinding cluster-admin-binding --clusterrole cluster-admin --user [USER_NAME]`

Then retry to install OLM.
