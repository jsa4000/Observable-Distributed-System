# Spring Boot Microservices

This is an example to demonstrate a Booking System developed with microservices.

## Build

In order to build the container images use tthe following commands

```bash
# Build and create Far Jar files for the microservices
mvn clean install 

# Build the images using maven spring boot plugin
mvn spring-boot:build-image

# Publish image to docker hub
docker push jsa4000/car-microservice:0.0.1-SNAPSHOT
```

To scan for vulnerabilities docker has introduced an option for scanning the image, using **Snyk** provider by default.

```bash
# Scan using the name of the image created priously
docker scan --severity high jsa4000/car-microservice:0.0.1-SNAPSHOT

Testing jsa4000/car-microservice:0.0.1-SNAPSHOT...

Package manager:   deb
Project name:      docker-image|jsa4000/car-microservice
Docker image:      jsa4000/car-microservice:0.0.1-SNAPSHOT
Platform:          linux/amd64
Base image:        ubuntu:18.04

✓ Tested 97 dependencies for known vulnerabilities, no vulnerable paths found.

Base Image    Vulnerabilities  Severity
ubuntu:18.04  22               0 high, 1 medium, 21 low

Recommendations for base image upgrade:

Major upgrades
Base Image    Vulnerabilities  Severity
ubuntu:20.04  15               0 high, 0 medium, 15 low


For more free scans that keep your images secure, sign up to Snyk at https://dockr.ly/3ePqVcp
```

## Deployment

Deploy the application into a local environment using kubernetes and helm 3.

### Dependencies

Install `Traefik` into the cluster

```bash
export MANIFEST_DIR=kubernetes/manifests
## Install `traefik` Chart into `tools` namespace
helm3 install -n tools --create-namespace traefik traefik/traefik --version 10.3.2 -f $MANIFEST_DIR/traefik-values.yaml

## Check the resources and load balancer created
kubectl get services -n tools

# Uninstall
helm3 uninstall traefik --namespace tools
```

Install `Prometheus` into the cluster

```bash
## Install `kube-prometheus-stack` Chart into `monitoring` namespace
helm3 install -n monitoring --create-namespace prometheus prometheus-community/kube-prometheus-stack --version 18.0.6 \
--set 'prometheus-node-exporter.hostRootFsMount=false'

## Prometheus dashboard (http://localhost:9090)
kubectl port-forward -n monitoring svc/prometheus-kube-prometheus-prometheus 9090
```

Install `MongoDB` into the cluster

```bash
export MANIFEST_DIR=kubernetes/manifests
# Install MongoDB chart into datastore namespace
helm3 install mongo --namespace datastore --create-namespace bitnami/mongodb --version 10.19.0 -f $MANIFEST_DIR/mongodb-values.yaml

# Test (mongodb://user:password@localhost:27017/db_1)
kubectl port-forward --namespace datastore svc/mongo-mongodb 27017:27017

# Uninstall
helm3 uninstall mongo --namespace datastore
```

### Application

> Following Commands must be executed from root project

```bash
# Create temporary folder to copy configuration files and charts
export TEMP_DIR=/tmp/local-deployment
export CHART_DIR=kubernetes/charts/microservice-chart-java
export CONFIG_DIR=kubernetes/deployments/02-multiplle-spring-boot-tracing/car-microservice/LOCAL
mkdir -p $TEMP_DIR
cp -r $CHART_DIR/. $TEMP_DIR
cp -r $CONFIG_DIR/. $TEMP_DIR

# Install the chart
helm3 install -n micro --create-namespace car-microservice $TEMP_DIR -f $TEMP_DIR/values.yaml
 
# Remove temp files created
rm -rf $TEMP_DIR
```

Wait until the microservice has been deployed

```bash
# Get all the common resources created from previous chart
kubectl get all -n micro

# Get the logs from the pod u
kubectl logs -n micro car-microservice-8ff49d869-xptgp -f

# Test microservice by using Port-forward(http://localhost:8080/swagger-ui/)
kubectl port-forward --namespace micro svc/car-microservice-srv 8080:80
# Get all the vehicles
curl "http://localhost:8080/vehicles" | jq .

# Test microservice by using Traefik Ingress (http://localhost/car/swagger-ui/)
curl "http://localhost/car/vehicles" | jq .

```

To delete (uninstall) the chart use the following command

```bash
# Uninstall the chart
helm3 uninstall -n micro car-microservice
```
