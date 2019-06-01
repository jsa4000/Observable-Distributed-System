# Monitoring

Monitoring Kubernetes should take into account several layers.

## Underlying VMs (Nodes)

To make sure the underlying virtual machines are healthy, the following metrics should be collected.

- Number of nodes
- Resource utilization per node (CPU, Memory, Disk, Network bandwidth)
- Node status (ready, not ready, etc.)
- Number of pods running per node

## Kubernetes Infrastructure

To make sure the Kubernetes infrastructure is healthy, the following metrics should be collected.

- Pods health — instances ready, status, restarts, age
- Deployments status — desired, current, up-to-date, available, age
- StatefulSets status
- CronJobs execution stats
- Pod resource utilization (CPU and Memory)
- Health checks
- Kubernetes Events
- API Server requests
- Etcd stats
- Mounted volumes stats

## User Applications

Each application should expose its own metrics, based on its core functionality, however, there are metrics which are common to most of the applications.

- HTTP requests (Total number, Latency, Response Code, etc.)
- Number of outgoing connections (e.g. database connections)
- Number of threads