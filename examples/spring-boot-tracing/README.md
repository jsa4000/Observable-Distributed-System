# Spring Boot Tracing

## Build and Publish Container Image

In order to build the container image, spring boot maven plugin is used instead using docker engine. This way there is no need to install Docker or any other dependency.
The default builder is [Paketo Buildpacks](https://paketo.io) and it can be configured using additional parameters as environment variables described into this repository [Paketo Labels](https://github.com/paketo-buildpacks/image-labels)

To package and build the image use the following command.

`mvn clean spring-boot:build-image`

To inspect the content of the image use 

`docker image inspect jsa4000/spring-boot-tracing:0.0.1-SNAPSHOT`

Once the container image has been created it can be published into the registry

> Spring Boot Maven Plugin also allows to publish the image, however this will be done manually

`docker push jsa4000/spring-boot-tracing:0.0.1-SNAPSHOT`

Run and test the container image

`docker run -p 8081:8080 jsa4000/spring-boot-tracing:0.0.1-SNAPSHOT`

## Deploy into kubernetes

Create a default namespace to deploy microservices

`kubectl create namespace micro`

Deploy a simple example into kubernetes and `micro` namespace using manifest files

`kubectl apply -n micro -f ../deployments/01-simple-spring-boot-tracing/`

Use port forward to test if the application is running at `http://localhost:8081/trace`

`kubectl port-forward -n micro svc/simple-spring-boot-tracing  8081:8080`

Delete previous example from kubernetes

`kubectl delete -n micro -f ../deployments/01-simple-spring-boot-tracing/`