# Spring Boot Microservices

## Installation

### Pre-Requisites

```bash
# Export manifest path
export MANIFEST_DIR=../../../kubernetes/manifests

## Install `traefik` Chart into `tools` namespace
helm3 install -n tools --create-namespace traefik traefik/traefik --version 10.3.2 -f $MANIFEST_DIR/traefik-values.yaml

## Install `kube-prometheus-stack` Chart into `monitoring` namespace
helm3 install -n monitoring --create-namespace prometheus prometheus-community/kube-prometheus-stack --version 18.0.6 \
--set 'prometheus-node-exporter.hostRootFsMount=false'

## Install `jaeger-operator` Chart into `tracing` namespace
helm3 install -n tracing --create-namespace jaeger-operator jaegertracing/jaeger-operator --version 2.24.0

### Configure OPENTRACING_JAEGER_ENABLED (deployments/spring-boot-tracing/deployment.yaml) to true using daemonset
kubectl apply -n tracing -f $MANIFEST_DIR/jaeger-daemonset.yaml

# Install MongoDB chart into datastore namespace
helm3 install mongo --namespace datastore --create-namespace bitnami/mongodb --version 10.19.0 -f $MANIFEST_DIR/mongodb-values.yaml

```

Check `LoadBalancer` has been assigned too traefik service

```bash
# Check EXTERNAL-IP is not in '<pending>' state.
kubectl get -n tools service
```

### Application

> Following Commands must be executed from current folder

```bash
# Install each chart by providing the initials: booking, car, flight or hotel
./install.sh booking
./install.sh car
./install.sh flight
./install.sh hotel
```

Wait until the microservice has been deployed

## Test

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

Using traefik to propagate the traceId.

```bash
# Get all bookings
curl "http://localhost/booking/bookings" | jq .

# Create Booking.

# Set json data to send into the request
export CREATE_ALL_BOOKING_DATA='{
  "active": false,
  "clientId": "8fb3c723-7851-486e-a369-ba0f9b908198",
  "createdAt": "2021-10-18T08:06:00.391Z",
  "vehicleId": "1",
  "flightId": "1",
  "hotelId": "1",
  "fromDate": "2021-10-18T08:06:00.391Z",
  "id": "b9460d0a-248e-11e9-ab14-d663bd873d93",
  "toDate": "2021-10-18T08:06:00.391Z"
}'

# Perform the Request
curl -X POST "http://localhost/booking/bookings" \
-H  "accept: application/json" \
-H  "Content-Type: application/json" \
-d $CREATE_ALL_BOOKING_DATA \
| jq .

# Set json data to send into the request
export CREATE_CAR_BOOKING_DATA='{
  "active": false,
  "clientId": "8fb3c723-7851-486e-a369-ba0f9b908198",
  "createdAt": "2021-10-18T08:06:00.391Z",
  "vehicleId": "41",
  "fromDate": "2021-10-18T08:06:00.391Z",
  "id": "b9460d0a-248e-11e9-ab14-d663bd873d93",
  "toDate": "2021-10-18T08:06:00.391Z"
}'

# Perform the Request
curl -X POST "http://localhost/booking/bookings" \
-H  "accept: application/json" \
-H  "Content-Type: application/json" \
-d $CREATE_CAR_BOOKING_DATA \
| jq .

```
