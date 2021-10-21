# Spring Boot Microservices

## Installation

### Pre-Requisites

Go to current folder to start the deployments

```bash
# Go to deployments folder from root
cd kubernetes/deployments/spring-boot-microservices
```

Execute following commands to install Helm Charts

```bash
# Run script to create all the pre-requisites using helm charts.
./pre-requisites.sh 
```

Verify all the pods are running correctly

```bash
# wait until all pods instances are currently in running state 'X/X Running'
kubectl get pods --all-namespaces -w
```

Execute following commands to install operators instances and other kubernetes resources

```bash
# Export manifest path
export MANIFEST_DIR=../../../kubernetes/manifests

## Deploy the prometheus-operator `ServiceMonitor` to monitor trraefik form prometheus
kubectl apply -n tools -f $MANIFEST_DIR/traefik-service-monitor.yaml
kubectl apply -n tools -f $MANIFEST_DIR/traefik-ingress-route.yaml

## Apply ECK deployment using the operator
kubectl apply -n logging -f $MANIFEST_DIR/eck.yaml

### Apply Clusterflow and ClusterOutputs (elasticsearch and s3) using SSL
kubectl apply -n logging -f $MANIFEST_DIR/logging.yaml

### Configure OPENTRACING_JAEGER_ENABLED (deployments/spring-boot-tracing/deployment.yaml) to true using daemonset
kubectl apply -n tracing -f $MANIFEST_DIR/jaeger-daemonset.yaml
```

Verify all the pods are running correctly

```bash
# wait until all pods instances are currently in running state X/X
kubectl get pods --all-namespaces -w
```


Check `LoadBalancer` has been assigned too traefik service.

```bash
# Check EXTERNAL-IP is not in '<pending>' state. 
kubectl get -n tools service

# traefik Service must have EXTERNAL-IP as 'localhost'
NAME                TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)                      AGE
traefik             LoadBalancer   10.111.128.126   localhost     80:31454/TCP,443:32614/TCP   6m58s
traefik-dashboard   ClusterIP      10.97.59.46      <none>        9000/TCP                     35s
traefik-metrics     ClusterIP      10.97.54.48      <none>        9100/TCP                     35s

# Check all the ports opened listen to 80
lsof -nP -iTCP -sTCP:LISTEN
# Kill the process (Docker)
kill -9 517
```

### Application

> Following Commands must be executed from current folder

```bash
# Deploy each helm chart by providing the initials: booking, car, flight or hotel
./deploy.sh booking local
./deploy.sh car local
./deploy.sh flight local
./deploy.sh hotel local
```

Wait until the microservice has been deployed

## Test

### Verification

Verify if microservices are currently running (status)

```bash
# Get all the common resources created from previous chart
kubectl get -n micro pods -w

# Get the logs from the pod u
kubectl logs -n micro car-microservice-8ff49d869-xptgp -f

# Test microservice by using Port-forward(http://localhost:8080/swagger-ui/)
kubectl port-forward --namespace micro svc/car-microservice-srv 8080:80
# Get all the vehicles
curl "http://localhost:8080/vehicles" | jq .

# Test microservice by using Traefik Controller / Ingress (http://localhost/car/swagger-ui/)
curl "http://localhost/car/vehicles" | jq .

```

Verify database migration in mongodb.

> This only creates one Mongo database, in a production environment database must be **isolated** one each other.

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

# Check vehicle, flight and hotels available in databbase 
curl "http://localhost/car/vehicles/13" | jq .
curl "http://localhost/flight/flights/14" | jq .
curl "http://localhost/hotel/hotels/34" | jq .

# Set json data to send into the request
export CREATE_ALL_BOOKING_DATA='{
  "active": false,
  "clientId": "8fb3c723-7851-486e-a369-ba0f9b908198",
  "createdAt": "2021-10-18T08:06:00.391Z",
  "vehicleId": "13",
  "flightId": "14",
  "hotelId": "34",
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
  "vehicleId": "13",
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
  "vehicleId": "13",
  "flightId": "140",
  "hotelId": "34",
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

> Use port-forward if loadbalancer has not been created by traefik: `kubectl port-forward -n tracing svc/jaeger-all-in-one-query 16686`

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

Try to kill a Pod a perform the same create booking.

```bash
# Get all pods and get the id
kubectl get -n micro pods 

# Delete a vehicle, hotel or flight pod
kubectl delete pod -n micro car-microservice-66486874f-nt49v

# Perform the REST call before the pod starts.

# Perform the Request (CREATE_ALL_BOOKING_DATA environment variable must be created)
curl -X POST "http://localhost/booking/bookings" \
-H  "accept: application/json" \
-H  "Content-Type: application/json" \
-d $CREATE_ALL_BOOKING_DATA \
| jq .

# Check the error using jaeger dashboard
# Using all the services
traefik-service
  spring-boot-tracing saveBooking
    [ERROR] spring-boot-tracing GET
    spring-boot-tracing GET
      spring-boot-tracing findFlightById
    spring-boot-tracing GET
      spring-boot-tracing findHotelById

# Check the errors and logs in the jaeger item with the error '[ERROR] spring-boot-tracing GET'
```

### Metrics

Serve prometheus and Grafana dashboards using port-forward

```bash
## Prometheus dashboard (http://localhost:9090)
kubectl port-forward -n monitoring svc/prometheus-kube-prometheus-prometheus 9090

## Grafana dashboard (http://localhost:3000) (`admin/prom-operator`)
kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80
```

Install following dashboards into Grafana.

> Select the same `Prometheus` source for all of them

- Node Exporter Full: 1860
- Traefik: 4475
- Spring Boot Statistics: 6756
- MongoDB Exporter: 2583

In order to use Spring Boot Statistics, use the following data to show app information:

- **Instance**. the `IP:Port` value from `targets` in Prometheus. i.e. `10.1.0.17:8080`
- **Application**. the name of the application (`spring.application.name`) or pod-name in most cases (without the hash). i.e. `car-microservice`.

Instance and Application can be gathered from the tags also:

```bash
com_example_booking_controller_seconds_max{application="booking-microservice", class="com.example.booking.controller.BookingController", container="booking", endpoint="http", exception="none", instance="10.1.0.17:8080", job="booking-microservice-srv", method="findAllBookings", namespace="micro", pod="booking-microservice-65bc7b4694-fdvhl", service="booking-microservice-srv"}
```

### Logs

There are a lot ways to register logs within kubernetes by using kubernetes native tools like loki, fluentd, etc. or using agents for SaaS providers such as DataDog, Splunk, AWS CloudWatch, etc..

#### Kibana

Access to the dashboard using Kibana (https://localhost:5601/)

```bash
# Get the generated password to access kibana with User as 'elastic'
kubectl get secret -n logging elastic-cluster-es-elastic-user -o=jsonpath='{.data.elastic}' | base64 --decode; echo

# Use port-forward to access Kibana (https://localhost:5601/) 
kubectl port-forward -n logging service/kibana-cluster-kb-http 5601
```

#### Loki

Access Grafana loki Logs

```bash
## Get the User and Password
kubectl get secret -n logging loki-grafana -o=jsonpath='{.data.admin-user}' | base64 --decode; echo
kubectl get secret -n logging loki-grafana -o=jsonpath='{.data.admin-password}' | base64 --decode; echo

## Access to Grafana Loki (http://localhost:3000)
kubectl port-forward -n logging svc/loki-grafana 3000:80
```

Get Logs from microservices:

- Open Grafana-Loki at http://localhost:3000
- Select left Menu Item `Explore` -> `Loki` (ComboBox)
- Click into `Log browser` and select `namespace` -> `micro` -> `Show Logs` button, or using `{namespace="micro"}` directly in the search text.
- Select the time range to search for the logs on the top.
- Press `Run Query` to search all results
- Similar to Kibana with the results filters can be added using + or -, column to view (single), etc... i.e. `{app="hotel",namespace="micro"}`

## Istio

Download Istio packages and utils
  
> It is a good practice to always specify the version to be installed.

```bash
# Download and extract Istio using specific version and platform
curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.7.3 TARGET_ARCH=x86_64 sh -

# Global Scope. Change the permissions and copy to user's bin folder
cd istio-1.7.3/bin
chmod +x istioctl
sudo cp istioctl /usr/local/bin/istioctl

# Get Istio version
istioctl version
```

Installing Istio to support Service Mesh

```bash
# Install istio with demo profile
istioctl install --set profile=demo

# Get all the services
kubectl get all -n istio-system
```

```bash
# Export manifest path
export MANIFEST_DIR=../../../kubernetes/manifests

# Install MongoDB chart into datastore namespace
helm3 install mongo --namespace datastore --create-namespace bitnami/mongodb --version 10.19.0 -f $MANIFEST_DIR/mongodb-values.yaml
```

Deploy applications using istio environment.

```bash
# Deploy each helm chart by providing the initials: booking, car, flight or hotel
./deploy.sh booking istio
./deploy.sh car istio
./deploy.sh flight istio
./deploy.sh hotel istio
```
