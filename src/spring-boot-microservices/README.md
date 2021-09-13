# Spring Boot Microservices

This is an example to demonstrate a Booking System developed with microservices.

## Build

In order to build the container images use tthe following commands

```bash
# Build and create Far Jar files for the microservices
mvn clean install 

# Build the images using maven spring boot plugin
mvn spring-boot:build-image
```

To scan for vulnerabilities docker has introduced an option for scanning the image, using **Snyk** provider by default.

```bash
# Scan using the name of the image created priously
docker scan --severity high jsa4000/car-microservice:1.0.0-SNAPSHOT

Testing jsa4000/car-microservice:1.0.0-SNAPSHOT...

Package manager:   deb
Project name:      docker-image|jsa4000/car-microservice
Docker image:      jsa4000/car-microservice:1.0.0-SNAPSHOT
Platform:          linux/amd64

✓ Tested 97 dependencies for known vulnerabilities, no vulnerable paths found.

For more free scans that keep your images secure, sign up to Snyk at https://dockr.ly/3ePqVcp

```