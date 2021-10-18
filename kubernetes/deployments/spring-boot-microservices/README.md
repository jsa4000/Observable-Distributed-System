# Spring Boot Microservices

## Installation

### Pre-Requisites

```bash
# Go to deployments folder from root
cd kubernetes/deployments/spring-boot-microservices

# Export manifest path
export MANIFEST_DIR=../../../kubernetes/manifests

## Install `traefik` Chart into `tools` namespace
helm3 install -n tools --create-namespace traefik traefik/traefik --version 10.3.2 -f $MANIFEST_DIR/traefik-values.yaml

## Deploy the prometheus-operator `ServiceMonitor` to monitor trraefik form prometheus
kubectl apply -n tools -f $MANIFEST_DIR/traefik-service-monitor.yaml
kubectl apply -n tools -f $MANIFEST_DIR/traefik-ingress-route.yaml

## Install `kube-prometheus-stack` Chart into `monitoring` namespace
helm3 install -n monitoring --create-namespace prometheus prometheus-community/kube-prometheus-stack --version 18.0.6 \
--set 'prometheus-node-exporter.hostRootFsMount=false'

## Install `jaeger-operator` Chart into `tracing` namespace
helm3 install -n tracing --create-namespace jaeger-operator jaegertracing/jaeger-operator --version 2.24.0

### Configure OPENTRACING_JAEGER_ENABLED (deployments/spring-boot-tracing/deployment.yaml) to true using daemonset
kubectl apply -n tracing -f $MANIFEST_DIR/jaeger-daemonset.yaml

# Install MongoDB chart into datastore namespace
helm3 install mongo --namespace datastore --create-namespace bitnami/mongodb --version 10.19.0 -f $MANIFEST_DIR/mongodb-values.yaml

## Instal MongoDB Exporterr into datastore using previous prometheus release installed (kube-prometheus-stack)
helm3 install prometheus-mongodb-exporter --namespace datastore prometheus-community/prometheus-mongodb-exporter --version 2.8.1 \
--set 'mongodb.uri=mongodb://monitor:password@mongo-mongodb.datastore.svc.cluster.local:27017/admin' \
--set 'serviceMonitor.additionalLabels.release=prometheus'
```

Check `LoadBalancer` has been assigned too traefik service.

```bash
# Check EXTERNAL-IP is not in '<pending>' state.
kubectl get -n tools service

# Check all the ports opened listen to 80
lsof -nP -iTCP -sTCP:LISTEN
# Kill the process (Docker)
kill -9 517
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

### Verification

Verify if microservices are currently running (status)

```bash
# Get all the common resources created from previous chart
kubectl get -n micro all 

# Get the logs from the pod u
kubectl logs -n micro car-microservice-8ff49d869-xptgp -f

# Test microservice by using Port-forward(http://localhost:8080/swagger-ui/)
kubectl port-forward --namespace micro svc/car-microservice-srv 8080:80
# Get all the vehicles
curl "http://localhost:8080/vehicles" | jq .

# Test microservice by using Traefik Controller / Ingress (http://localhost/car/swagger-ui/)
curl "http://localhost/car/vehicles" | jq .

```

Verify database migration in mongodb

```bash
# Test (mongodb://user:password@localhost:27017/db_1)
kubectl port-forward --namespace datastore svc/mongo-mongodb 27017:27017
```

Verify Traefik dashboard

```bash
## Port-Forward to traefik dashboard. http://locahost:9000
kubectl port-forward -n tools svc/traefik-dashboard 9000
```

### Examples

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
  "flightId": "14",
  "hotelId": "12",
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
  "vehicleId": "91",
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

# Set json data to send into the request
export CREATE_ERROR_BOOKING_DATA='{
  "active": false,
  "clientId": "8fb3c723-7851-486e-a369-ba0f9b908198",
  "createdAt": "2021-10-18T08:06:00.391Z",
  "vehicleId": "1",
  "flightId": "140",
  "hotelId": "12",
  "fromDate": "2021-10-18T08:06:00.391Z",
  "id": "b9460d0a-248e-11e9-ab14-d663bd873d93",
  "toDate": "2021-10-18T08:06:00.391Z"
}'

# Perform the Request
curl -X POST "http://localhost/booking/bookings" \
-H  "accept: application/json" \
-H  "Content-Type: application/json" \
-d $CREATE_ERROR_BOOKING_DATA \
| jq .
```

### Traces

Verify jaeger traces using [http://localhost](http://localhost) and search for:

- Service: `spring-boot-tracing`
- Operation: `saveBooking`

It could be seen the traces being send by the request depending on the params send into the request: `vehicleId`, `flightId` and `hotelId`.

```bash
# Using all the services
traefik-service
  spring-boot-tracing saveBooking
    spring-boot-tracing GET
      spring-boot-tracing findVehicleById
    spring-boot-tracing GET
      spring-boot-tracing findFlightById
    spring-boot-tracing GET
      spring-boot-tracing findHotelById

# Using only vehicle service
traefik-service
  spring-boot-tracing saveBooking
    spring-boot-tracing GET
      spring-boot-tracing findVehicleById
```

### Metrics

Serve prometheus and Grafana dashboards using port-forweard

```bash
## Prometheus dashboard (http://localhost:9090)
kubectl port-forward -n monitoring svc/prometheus-kube-prometheus-prometheus 9090

## Grafana dashboard (http://localhost:3000) (`admin/prom-operator`)
kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80
```

Install following dashboards into Grafana.

- Node Exporter Full: 1860
- Traefik: 4475
- Spring Boot Statistics: 6756
- MongoDB Exporter: 2583

In order to use Spring Boot Statistics, use the following data to show app information:

- **Instance**. the `IP:Port` value from `targets` in Prometheus. i.e. `10.1.0.17:8080`
- **Application**. the name of the application (`spring.application.name`). i.e. `car-microservice`.

Instance and Application can be gathered from the tags also:

```bash
com_example_booking_controller_seconds_max{application="booking-microservice", class="com.example.booking.controller.BookingController", container="booking", endpoint="http", exception="none", instance="10.1.0.17:8080", job="booking-microservice-srv", method="findAllBookings", namespace="micro", pod="booking-microservice-65bc7b4694-fdvhl", service="booking-microservice-srv"}
```

### Logs