# Istio - Service Mesh

## Service Mesh

Cloud platforms provide a wealth of benefits for the organizations that use them. However, there’s no denying that adopting the cloud can put strains on DevOps teams. Developers must use microservices to architect for portability, meanwhile operators are managing extremely large hybrid and multi-cloud deployments. A **Service Mesh** lets you connect, secure, control, and observe services.

A **Service Mesh** is a configurable, low‑latency infrastructure layer designed to handle a high volume of network‑based interprocess communication among application infrastructure services using application programming interfaces (APIs). A service mesh ensures that communication among containerized and often ephemeral application infrastructure services is fast, reliable, and secure. The mesh provides critical capabilities including service discovery, load balancing, encryption, observability, traceability, authentication and authorization, and support for the circuit breaker pattern.

![Istio](images/istio-comunication.png)

The service mesh is usually implemented by providing a proxy instance, called a sidecar, for each service instance. **Sidecars** handle interservice communications, monitoring, and security‑related concerns – indeed, anything that can be abstracted away from the individual services. This way, developers can handle development, support, and maintenance for the application code in the services; operations teams can maintain the service mesh and run the app.

There are some differences between a Service Mesh and API Gateway.

![Istio](images/istio-service-mesh-api-gateway.png)

## Istio

At a high level, **Istio** helps reduce the complexity of these deployments, and eases the strain on your development teams. It is a completely open source **Service Mesh** that layers transparently onto existing distributed applications. It is also a platform, including APIs that let it integrate into any logging platform, or telemetry or policy system. Istio’s diverse feature set lets you successfully, and efficiently, run a distributed microservice architecture, and provides a uniform way to secure, connect, and monitor microservices.

**Istio** makes it easy to create a network of deployed services with load balancing, service-to-service authentication, monitoring, and more, with few or no code changes in service code. You add Istio support to services by deploying a special sidecar proxy throughout your environment that intercepts all network communication between microservices, then configure and manage Istio using its control plane functionality, which includes:

- Automatic load balancing for HTTP, gRPC, WebSocket, and TCP traffic.
- Fine-grained control of traffic behavior with rich routing rules, retries, failovers, and fault injection.
- A pluggable policy layer and configuration API supporting access controls, rate limits and quotas.
- Automatic metrics, logs, and traces for all traffic within a cluster, including cluster ingress and egress.
- Secure service-to-service communication in a cluster with strong identity-based authentication and authorization.

**Istio** is designed for extensibility and meets diverse deployment needs. It does this by intercepting and configuring mesh traffic as shown in the following diagram:

![Istio](images/istio-arch.svg)

## Pre-Requisites

- Container Engine (Docker, Containerd, CRI-O, etc.) -> `Docker Engine 19.03.13`
  
  ```bash
  # Verify docker version installed
  docker version

  Client: Docker Engine - Community
  Cloud integration  0.1.18
  Version:           19.03.13

  Server: Docker Engine - Community
  Engine:
  Version:          19.03.13
  ```

- Kubernetes (Docker Desktop, Minikube, K3S, Kind) -> `Kubernetes v1.18.8`

  ```bash
  # Verify kubernetes version installed
  kubectl version

  Client Version: version.Info{Major:"1", Minor:"18", GitVersion:"v1.18.8", GitCommit:"9f2892aab98fe339f3bd70e3c470144299398ace", GitTreeState:"clean", BuildDate:"2020-08-13T16:12:48Z", GoVersion:"go1.13.15", Compiler:"gc", Platform:"darwin/amd64"}
  Server Version: version.Info{Major:"1", Minor:"18", GitVersion:"v1.18.8", GitCommit:"9f2892aab98fe339f3bd70e3c470144299398ace", GitTreeState:"clean", BuildDate:"2020-08-13T16:04:18Z", GoVersion:"go1.13.15", Compiler:"gc", Platform:"linux/amd64"}
  ```

- Helm 3 -> `Helm v3.3.4`
  
  > [Download link](https://github.com/helm/helm/releases)

  ```bash
  # Change the permissions and copy to user's bin folder
  chmod +x helm
  sudo cp helm /usr/local/bin/helm3

  # Check the helm version
  helm3 version

  version.BuildInfo{Version:"v3.3.4", GitCommit:"a61ce5633af99708171414353ed49547cf05013d", GitTreeState:"clean", GoVersion:"go1.14.9"}
  ```

## Demo

The actual demo uses the default sample application that comes with istio installation. This demo shows the **benefits** of a **Service Mesh** starting from a default kubernetes installation.

1. Create a `namespace` to deploy the sample application

   > In the following example we will create a new namespace `microservices` to be used with istio.

  ```bash
  # Create the namespace where microservices will be deployed
  kubectl create ns microservices

  namespace/microservices created

  # Show all the namespaces
  kubectl get ns

  NAME              STATUS   AGE
  default           Active   4d6h
  kube-node-lease   Active   4d6h
  kube-public       Active   4d6h
  kube-system       Active   4d6h
  microservices     Active   36s
  ```

2. Switch to current namespace
  
  ```bash
  # Switch to current context from ddefault
  kubectl config set-context --current --namespace=microservices

  Context "docker-desktop" modified.

  # Get the current context info and namespace
  kubectl config get-contexts

  CURRENT          NAME             CLUSTER          AUTHINFO         NAMESPACE
  *                docker-desktop   docker-desktop   docker-desktop   microservices
  ```

3.  Deploy the **sample** application based on kubernetes **native** resources  

  The application displays information about a book, similar to a single catalog entry of an online book store. Displayed on the page is a description of the book, book details (ISBN, number of pages, and so on), and a few book reviews.

  The **BookInfo** application is broken into **four** separate microservices:

  - **productpage**. The productpage microservice calls the details and reviews microservices to populate the page.
  - **details**. The details microservice contains book information.
  - **reviews**. The reviews microservice contains book reviews. It also calls the ratings microservice.
  - **ratings**. The ratings microservice contains book ranking information that accompanies a book review.

  There are 3 different versions of the **reviews** microservice:

  - **Version v1** doesn’t call the ratings service.
  - **Version v2** calls the ratings service, and displays each rating as 1 to 5 black stars.
  - **Version v3** calls the ratings service, and displays each rating as 1 to 5 red stars.

  The end-to-end architecture of the application is shown below.

  ![noistio](images/istio-noistio.svg)

  ```bash
  # Check the resources to be created (just the v1)
  code manifests/istio/01_bookinfo_deployment/

  # Create the  book info application
  kubectl apply -f manifests/istio/01_bookinfo_deployment/

  service/details created
  serviceaccount/bookinfo-details created
  deployment.apps/details-v1 created
  service/ratings created
  serviceaccount/bookinfo-ratings created
  deployment.apps/ratings-v1 created
  service/reviews created
  serviceaccount/bookinfo-reviews created
  deployment.apps/reviews-v1 created
  service/productpage created
  serviceaccount/bookinfo-productpage created
  ```

4. Wait until all the pods are currently`running`.

  > Actually istio is **NOT** currently working. Pods have only one pod running 1/1,  so the SideCar is not active yet.

  ```bash
  # Create the  book info application
  kubectl get pods -w

  NAME                              READY   STATUS    RESTARTS   AGE
  details-v1-558b8b4b76-6snwg       1/1     Running   0          85s
  productpage-v1-6987489c74-ft6pf   1/1     Running   0          84s
  ratings-v1-7dc98c7588-sqqnx       1/1     Running   0          84s
  reviews-v1-7f99cc4496-vgj4t       1/1     Running   0          84s

  # Get the services.
  kubectl get svc
  NAME          TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
  details       ClusterIP   10.99.0.151     <none>        9080/TCP   3m33s
  productpage   ClusterIP   10.96.164.128   <none>        9080/TCP   3m32s
  ratings       ClusterIP   10.99.104.182   <none>        9080/TCP   3m33s
  reviews       ClusterIP   10.99.131.90    <none>        9080/TCP   3m32s
  ```

5. Verify the deployment (v1) using `port-forward` and accessing to the `prodcutpage`


  ```bash
  # Create the  book info application
  kubectl port-forward svc/productpage 9080:9080

  # Verify using a Web Browser.
  #Reload the page and check the web is showing the same version every time (v1).
  http://localhost:9080/productpage

  ```

6. Deploy multiple versions for the review service: `v2` and `v3`

  > Kubernetes does not support multiple version for the same service. So it will balance between the versions without any logic.  

```bash
  # Check the resources to be created (v2 and v3). The service is the same as the v2, it must be the same.
  code manifests/istio/01_bookinfo_multiple_versions/

  # Same selector -> matchLabels -> app, different versions: v2 and v3
  #
  # replicas: 1
  # selector:
  #   matchLabels:
  #     app: reviews
  #     version: v2

  # Create the  book info application
  kubectl apply -f manifests/istio/01_bookinfo_multiple_versions/

  deployment.apps/reviews-v2 created
  deployment.apps/reviews-v3 created

  # Create the  book info application
  kubectl get pods -w

  NAME                              READY   STATUS    RESTARTS   AGE
  details-v1-558b8b4b76-txgnh       1/1     Running   0          3m40s
  productpage-v1-6987489c74-tgm4r   1/1     Running   0          3m39s
  ratings-v1-7dc98c7588-94pl9       1/1     Running   0          3m39s
  reviews-v1-7f99cc4496-69j28       1/1     Running   0          3m40s
  reviews-v2-7d79d5bd5d-4b2xg       1/1     Running   0          5s
  reviews-v3-7dbcdcbc56-hsx9l       1/1     Running   0          5s

  # Get the services. No changes at all.
  kubectl get svc
  NAME          TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
  details       ClusterIP   10.99.0.151     <none>        9080/TCP   3m33s
  productpage   ClusterIP   10.96.164.128   <none>        9080/TCP   3m32s
  ratings       ClusterIP   10.99.104.182   <none>        9080/TCP   3m33s
  reviews       ClusterIP   10.99.131.90    <none>        9080/TCP   3m32s

  # Create the  book info application
  kubectl port-forward svc/productpage 9080:9080

  # Verify using any Web Browser.
  # Reload the browser so it can be seen different versions are displayed since kubernetes use random strategy. Check versions without reviews and starts with different colors are shown because the internal kubernetes balancer.
  http://localhost:9080/productpage

  ```

7. Installing **Istio** to support **Service Mesh**.
  
  Go to the Istio release [page](https://github.com/istio/istio/releases/) to download the installation file for your OS, or download and extract the latest release automatically (Linux or macOS). Installation process is highly detailed in the official [Istio website](https://istio.io/latest/docs/setup/getting-started/)

  > It is a good practice to **always** specify the version to be installed.

  ```bash
  # Download and extract Istio using specific version and platform
  curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.11.6 TARGET_ARCH=x86_64 sh -
  ```

8. Move to the Istio package directory and add the istioctl client to your path (Linux or macOS) or copy it to global scope.

  ```bash
  # Export variable
  cd istio-1.11.6
  export PATH=$PWD/bin:$PATH

  # Global Scope. Change the permissions and copy to user's bin folder
  cd istio-1.11.6/bin
  chmod +x istioctl
  sudo cp istioctl /usr/local/bin/istioctl
  ```

9. Check current version

  ```bash
  istioctl version

  no running Istio pods in "istio-system"
  1.11.6
  ```

10. Install Istio.

    There are several default [profiles](https://istio.io/latest/docs/setup/additional-setup/config-profiles/) to install istio, depending on the requirements and tools to be used. It is useful to start from one of those profiles already created and perform the necessary modifications and tweaks.

    - **default**: enables components according to the default settings of the IstioOperator API. This profile is recommended for production deployments and for primary clusters in a multicluster mesh. You can display the default setting by running the command istioctl profile dump.
    - **demo**: configuration designed to showcase Istio functionality with modest resource requirements. It is suitable to run the Bookinfo application and associated tasks. This is the configuration that is installed with the quick start instructions.
    This profile enables high levels of tracing and access logging so it is not suitable for performance tests.
    - **minimal**: the minimal set of components necessary to use Istio’s traffic management features.
    - **remote**: used for configuring remote clusters of a multicluster mesh.
    empty: deploys nothing. This can be useful as a base profile for custom configuration.
    - **preview**: the preview profile contains features that are experimental. This is intended to explore new features coming to Istio. Stability, security, and performance are not guaranteed - use at your own risk.

    We use the `demo` configuration profile. It’s selected to have a good set of defaults for testing, but there are other profiles for production or performance testing.

    > Following operation will take several minutes depending on the internet connection and resources.

  ```bash
  # Check the profiles configuration values that will be used for istio operator
  code manifests/profiles/
  # Check the helm charts used by the operator ([operator-sdk](https://github.com/operator-framework/operator-sdk))
  code manifests/charts/

  # Install istio with demo profile
  istioctl install --set profile=demo

  ✔ Istio core installed
  ✔ Istiod installed
  ✔ Egress gateways installed
  ✔ Ingress gateways installed
  ✔ Installation complete
  ```

11. Verify the installation.

  > Istio has created a new namespace `istio-system`

  ```bash
  # Check istio-system namespace has been created
  kubectl get ns

  NAME              STATUS   AGE
  default           Active   95m
  istio-system      Active   48m
  kube-node-lease   Active   95m
  kube-public       Active   95m
  kube-system       Active   95m
  microservices     Active   58m

  # Check for the new CRDs created
  kubectl get crd -n istio-system

  NAME                                       CREATED AT
  adapters.config.istio.io                   2020-10-14T09:15:39Z
  attributemanifests.config.istio.io         2020-10-14T09:15:39Z
  authorizationpolicies.security.istio.io    2020-10-14T09:15:39Z
  destinationrules.networking.istio.io       2020-10-14T09:15:39Z
  envoyfilters.networking.istio.io           2020-10-14T09:15:39Z
  gateways.networking.istio.io               2020-10-14T09:15:39Z
  ...

  # Check all pods installed are current running: istiod, istio-egressgateway and istio-ingressgateway.
  kubectl get pods -n istio-system

  NAME                                    READY   STATUS    RESTARTS   AGE
  istio-egressgateway-66f8f6d69c-qwlpl    1/1     Running   0          5m24s
  istio-ingressgateway-758d8b79bd-blw74   1/1     Running   0          5m25s
  istiod-7556f7fddf-v4sl4                 1/1     Running   0          5m45s
  ```

12. Install the **addons+*

  > This will install the addons included with istio: `Kiali`, `Prometheus`, `Grafana`, `Jaeger`, etc..

  ```bash
  # Install istio addons
  kubectl apply -f samples/addons

  serviceaccount/grafana created
  configmap/grafana created
  service/grafana created
  deployment.apps/grafana created
  configmap/istio-grafana-dashboards created
  configmap/istio-services-grafana-dashboards created
  deployment.apps/jaeger created
  service/tracing created
  service/zipkin created
  customresourcedefinition.apiextensions.k8s.io/monitoringdashboards.monitoring.kiali.io created
  serviceaccount/kiali created
  configmap/kiali created
  clusterrole.rbac.authorization.k8s.io/kiali-viewer created
  clusterrole.rbac.authorization.k8s.io/kiali created
  clusterrolebinding.rbac.authorization.k8s.io/kiali created
  service/kiali created
  deployment.apps/kiali created
  serviceaccount/prometheus created
  configmap/prometheus created
  clusterrole.rbac.authorization.k8s.io/prometheus created
  clusterrolebinding.rbac.authorization.k8s.io/prometheus created
  service/prometheus created
  deployment.apps/prometheus created

  # [Optional]
  # Zipkin can be also installed and replaced by Jaeger
  kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.7/samples/addons/extras/zipkin.yaml

  deployment.apps/zipkin created
  service/tracing configured
  service/zipkin configured
  ```

13.  Verify the addons has been **installed** and **running**
  
  ```bash
  # Check all pods installed are current running:  grafana, prometheus, kiali, jaeger, etc..
  kubectl get pods -n istio-system

  NAME                                    READY   STATUS    RESTARTS   AGE
  grafana-75b5cddb4d-xdcqp                1/1     Running   0          5m8s
  istio-egressgateway-66f8f6d69c-zjg44    1/1     Running   0          8m31s
  istio-ingressgateway-758d8b79bd-sdrdp   1/1     Running   0          8m32s
  istiod-7556f7fddf-bj76n                 1/1     Running   0          8m38s
  jaeger-5795c4cf99-x884f                 1/1     Running   0          5m8s
  kiali-6c49c7d566-xgzvk                  1/1     Running   0          5m8s
  prometheus-9d5676d95-p795n              2/2     Running   0          5m7s

  # Check all services installed are current running:  grafana, prometheus, kiali, jaeger, zipkin
  kubectl get svc -n istio-system
  NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                                                                      AGE
  grafana                ClusterIP      10.98.120.248   <none>        3000/TCP                                                                     6m3s
  istio-egressgateway    ClusterIP      10.99.110.55    <none>        80/TCP,443/TCP,15443/TCP                                                     9m27s
  istio-ingressgateway   LoadBalancer   10.102.89.11    localhost     15021:32749/TCP,80:32607/TCP,443:31412/TCP,31400:31448/TCP,15443:32618/TCP   9m27s
  istiod                 ClusterIP      10.111.234.39   <none>        15010/TCP,15012/TCP,443/TCP,15014/TCP,853/TCP                                9m33s
  kiali                  ClusterIP      10.98.97.159    <none>        20001/TCP,9090/TCP                                                           6m3s
  prometheus             ClusterIP      10.107.5.242    <none>        9090/TCP                                                                     6m2s
  tracing                ClusterIP      10.99.145.194   <none>        80/TCP                                                                       6m3s
  ```

14.  Open `grafana` and `Kiali` application using `istioctl` tool.

  > Once `grafana` and `Kiali` are opened there is nothing to show yet by Istio in the graph
 
  ```bash
  # GEt the pods microservice namespace (still 1/1 pods)
  kubectl get pods

  NAME                              READY   STATUS    RESTARTS   AGE
  details-v1-558b8b4b76-txgnh       1/1     Running   0          59m
  productpage-v1-6987489c74-tgm4r   1/1     Running   0          59m
  ratings-v1-7dc98c7588-94pl9       1/1     Running   0          59m
  reviews-v1-7f99cc4496-69j28       1/1     Running   0          59m
  reviews-v2-7d79d5bd5d-4b2xg       1/1     Running   0          56m
  reviews-v3-7dbcdcbc56-hsx9l       1/1     Running   0          56m

  # Open kiali dashboard -> Graph -> Empty Graph
  # > Select "microservice" namespace in the combo and select different options
  #   App Graph, Service Graph, Versioned App Graph
  istioctl dashboard kiali

  # Open grafana dashboard -> Go to Istio Service Dashboard -> Select productage microservice
  # There is no service yet nor workload
  istioctl dashboard grafana

  # Open jaeger dashboard -> Search for productpage
  # Nothing  in services
  istioctl dashboard jaeger
  ```

15. Create Istio `Gateway` and `VirtualService` for the `productpage` service to open the connection to the outside

  ```bash
  # Check the resources to be created
  code manifests/istio/02_bookinfo_gateway/

  # Create the  book info application
  kubectl apply -f manifests/istio/02_bookinfo_gateway/

  gateway.networking.istio.io/bookinfo-gateway created
  virtualservice.networking.istio.io/bookinfo created
  ```

16. Verify the connection using the load balancer (`ServiceType -> LoadBalancer`) created by istio ingress controller

  > It is working as a normal ingress-controller such as `nginx` or `traefik`

  ```bash
  # Get the EXTERNAL-IP and Ports available. 80/443
  kubectl get svc -n istio-system
  kubectl get svc -n istio-system | grep istio-ingressgateway

  NAME                   TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                                                                      AGE
  istio-ingressgateway   LoadBalancer   10.102.89.11    localhost     15021:32749/TCP,80:32607/TCP,443:31412/TCP,31400:31448/TCP,15443:32618/TCP   32m

  # Use the EXTERNAL-IP and Port s 80:32607/TCP,443:31412
  # Reload to check if the ingress is working as using port-forward multiple being balanced between multiple versions
  http://localhost:80/productpage
  ```

17. Open `grafana` and `Kiali` application using `istioctl` tool.

  > Once `grafana` and `Kiali` are opened there is **still** nothing to show yet by Istio in the graph
 
  ```bash
  # Open kiali dashboard -> Graph -> Empty Graph
  # > Select "microservice" namespace in the combo
  istioctl dashboard kiali

  # Check in IstioConfig the VirtualServie and Gateway habe been created and both are ok.
  http://localhost:20001/kiali/console/istio?namespaces=microservices

  # Open grafana dashboard -> Go to Istio Service Dashboard -> Select productage microservice
  istioctl dashboard grafana

  # Open jaeger dashboard -> Search for productpage
  istioctl dashboard jaeger
  ```

18. Create a **label** into the namespace created to automatically inject **Envoy Sidecar Proxies** into Pods.

  ```bash
  # Set the auto injection label so istio knows the namespace to monitor
  kubectl label namespace microservices istio-injection=enabled

  namespace/default labeled

  # Verify the label has been created
  kubectl get ns --show-labels  

  NAME              STATUS   AGE    LABELS
  default           Active   4d7h   <none>
  istio-system      Active   42m    istio-injection=disabled
  kube-node-lease   Active   4d7h   <none>
  kube-public       Active   4d7h   <none>
  kube-system       Active   4d7h   <none>
  microservices     Active   31m    istio-injection=enabled
  ```

19. Verify the Pods currently running.

  > There is no Sidecar Proxy running yet `1/1 Running`

  ```bash
  # Get the pods in microservice namespace
  kubectl get pods

  NAME                              READY   STATUS    RESTARTS   AGE
  details-v1-558b8b4b76-6snwg       1/1     Running   0          28m
  productpage-v1-6987489c74-ft6pf   1/1     Running   0          28m
  ratings-v1-7dc98c7588-sqqnx       1/1     Running   0          28m
  reviews-v1-7f99cc4496-vgj4t       1/1     Running   0          28m
  reviews-v2-7d79d5bd5d-qqfhn       1/1     Running   0          28m

  # There is no Sidecar yet configured
  ```

20. Force to **stop** all the pods and verify again the status

  > There is no Sidecar Proxy running yet `1/1 Running`

  ```bash
  # Delete all the pods
  kubectl get pods | awk '{print $1}' | xargs kubectl delete pod
  kubectl get pods -o=name | xargs kubectl delete

  # Waint until the pods in microservice namespace are running
  kubectl get pods -w

  NAME                              READY   STATUS    RESTARTS   AGE
  details-v1-558b8b4b76-64pbq       2/2     Running   0          57s
  productpage-v1-6987489c74-nh7f6   2/2     Running   0          57s
  ratings-v1-7dc98c7588-qs64j       2/2     Running   0          57s
  reviews-v1-7f99cc4496-p85kw       2/2     Running   0          56s
  reviews-v2-7d79d5bd5d-lrqnd       2/2     Running   0          56s
  reviews-v3-7dbcdcbc56-qprm2       2/2     Running   0          56s
  ```

21. Open `grafana` and `Kiali` application using `istioctl` tool.
 
  ```bash
  # Open kiali dashboard -> Graph -> Empty Graph (Still nothing ?)
  # > Select "microservice" namespace in the combo
  istioctl dashboard kiali

  # Force a request to the productpage  
  http://localhost:80/productpage

  # Check kiali
  http://localhost:20001/kiali/console/graph/namespaces/

  # Perform calls sequentially and return to kiali
  watch curl -s -o /dev/null 'http://localhost:80/productpage'
  watch -n 0.5 curl -s -o /dev/null 'http://localhost:80/productpage'  # Per 0.5 seg

  # Refresh kiali dashboard -> Graph (Wait a moment or refresh the page)
  #  1, Select "microservice" namespace in the combo
  #  2. Select "versioned app graph". It can be selected others Service, Workload, etc..
  #  3. Select Display -> "Request Percentage" to add the percentage in the edges on the graph.
  #                    -> "Traffic Animation" to add the percentage in the edges on the graph.

  http://localhost:20001/kiali/console/graph/namespaces/

  # In display options it can be filtered information such as Circuit Breakers, Virtual SErvice, Security, etc..

  # Open grafana dashboard -> Go to Istio Service Dashboard -> Select productage microservice
  #                        -> Go to Istio workload Dashboard -> Select microservice namespace and productage 
  istioctl dashboard grafana

  # Open jaeger dashboard -> Search for productpage
  # Service -> Operations -> Find Traces
  istioctl dashboard jaeger
  ```

22. Verify istio ingress controller is balancing the traffic equalliy between the services (round-robin)

  > Kiali must show 33.3% of traffic among all review microservice versions: v1, v2 and v3

 ```bash
  # Open kiali dashboard -> Graph -> Empty Graph (Still nothing ?)
  # > Select "microservice" namespace in the combo
  istioctl dashboard kiali

  # Perform calls sequentially and return to kiali
  watch curl -s -o /dev/null 'http://localhost:80/productpage'
  watch -n 0.5 curl -s -o /dev/null 'http://localhost:80/productpage'  # Per 0.5 seg

  # Check the Request Percentage for productpage in kiali around 33.3%
  ```

23. Define the available `versions`, called `subsets`, in destination rules.
  
 > Before you can use Istio to control the Bookinfo version routing, you need to define the available versions, called subsets, in destination rules.

  ```bash
  # Check how subset are defined
  code manifests/istio/03_destination_rules_all

  # Apply detination rules
  kubectl apply -f manifests/istio/03_destination_rules_all/
  
  destinationrule.networking.istio.io/productpage created
  destinationrule.networking.istio.io/reviews created
  destinationrule.networking.istio.io/ratings created
  destinationrule.networking.istio.io/details created

  # In this case destination rules are used to map the subsets with the `version` label in `deployments`
  ```

24. Change the **Service Mesh** so it only routes traffic to `v1`.

  > Perform changes Without stopping sending request to productpage.

   ```bash
  # Check the modifications made to the virtual services
  code manifests/istio/04_route_all_traffic_v1/

  # Redefined kubernetes "Services" to istio "VirtualServices", so the proxies willl use this definition instead
  # The Virtual Service uses v1 versions/subsets for the routing, so other versions are excluded.

  # Update Virtual Services to route the traffic to v1 only
  kubectl apply -f manifests/istio/04_route_all_traffic_v1/

  virtualservice.networking.istio.io/productpage created
  virtualservice.networking.istio.io/reviews created
  virtualservice.networking.istio.io/ratings created
  virtualservice.networking.istio.io/details created

  # Check also in kiali the percentages changes and move towards v1.
  # Reloading the page it only showing the v1 verion wothout calling the review service.
  http://localhost/productpage  # Refresh several times
  ```

25. Change routing based on `user identity`

  > The traffic will be routed depending on the headers (match)

   ```bash
  # Check the modifications made to the virtual services
  code manifests/istio/05_route_based_headers/

  # Update Virtual Services to route the traffic to v1 except user 'jason'
  kubectl apply -f manifests/istio/05_route_based_headers/

  virtualservice.networking.istio.io/reviews configured

  # 1. Log in as another user (pick any name you wish).
  # 2. Refresh the browser.
  # 3. Now the stars are gone. This is because traffic is routed to reviews:v1 for all users except "jason".
  ```

26. Injecting an HTTP delay **fault**

  > The connection will be delayed using the `jason` user

   ```bash
  # Check the modifications made to the virtual services
  code manifests/istio/06_http_delay_fault/

  # Similar to 04_route_base_headers but aadding fault "fixedDelay" 7s
  # - match:
  #   - headers:
  #       end-user:
  #         exact: jason
  #   fault:
  #     delay:
  #       percentage:
  #         value: 100.0
  #       fixedDelay: 7s
  #   route:
  #   - destination:
  #       host: ratings
  #       subset: v1

  # Update with the current definition.
  kubectl apply -f manifests/istio/06_http_delay_fault/

  virtualservice.networking.istio.io/ratings configured

  # Login with jason user and test the productpage and wait for the web to show, about 7 seconds
  # This delay is not shown in jaeger since it is not inside the trace itself.
  http://localhost/productpage  # Refresh several times

  # Login with another user and test the productpage, the web shows inmediately
  http://localhost/productpage  # Refresh several times

  ```

27. HTTP abort **fault**.

  > The connection will be delayed using the `jason` user

   ```bash
  # Check the modifications made to the virtual services
  code manifests/istio/07_http_abort_fault/

  # Similar to 04_route_base_headers but aadding fault "fixedDelay" 7s
  # - match:
  #   - headers:
  #       end-user:
  #         exact: jason
  #   fault:
  #     abort:
  #       percentage:
  #         value: 100.0
  #       httpStatus: 500
  #   route:
  #   - destination:
  #       host: ratings
  #       subset: v1

  # Update with the current definition.
  kubectl apply -f manifests/istio/07_http_abort_fault/

  virtualservice.networking.istio.io/ratings configured

  # Login with jason user and test the productpage error
  http://localhost/productpage  # Refresh several times

  # Login with another user and test the productpage, the web shows inmediately
  http://localhost/productpage  # Refresh several times

  ```
