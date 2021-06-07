# Kubernetes Dashboards

There are several options to visualize, monitor and manage Kubernetes clusters via graphical interface:

* [Kubernetes Dashboard](https://github.com/kubernetes/dashboard)
* [Octant](https://github.com/vmware-tanzu/octant)
* [KubeWatch](https://github.com/bitnami-labs/kubewatch)
* [k9s](https://github.com/derailed/k9s)
* [Weave Scope](https://github.com/weaveworks/scope)
* [Zabbix](https://www.zabbix.com/)

## Octant

**Octant** is a tool for developers to understand how applications run on a Kubernetes cluster. It aims to be part of the developer's toolkit for gaining insight and approaching complexity found in Kubernetes. Octant offers a combination of introspective tooling, cluster navigation, and object management along with a plugin system to further extend its capabilities.

* Download the latest binaries from the official [release](https://github.com/vmware-tanzu/octant/releases) page.
* Open Octant and select the current kubernetes context (`kubeconfig`)
    > Note: If any error loading context happens, remove `.kube/config` file and use `KUBECONFIG` environment variable to point to a kubeconfig file instead.
* Select any options to see kubernetes resources, nodes, configuration, workloads, etc..

## K9s

**K9s** provides a terminal UI to interact with your Kubernetes clusters. The aim of this project is to make it easier to navigate, observe and manage your applications in the wild. K9s continually watches Kubernetes for changes and offers subsequent commands to interact with your observed resources.

K9s is available on Linux, macOS and Windows platforms.

* Binaries for Linux, Windows and Mac are available as tarballs in the [release](https://github.com/derailed/k9s/releases) page.

* Via Homebrew for macOS or LinuxBrew for Linux

    `brew install k9s`

* Via Webi for Linux and macOS

    `curl -sS https://webinstall.dev/k9s | bash`

* Via Webi for Windows

    `curl.exe -A MS https://webinstall.dev/k9s | powershell`
