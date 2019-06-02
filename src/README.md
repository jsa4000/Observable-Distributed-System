# Ecommerce Project

## Build

In order to build and packages run the following command.

```
./mvnw clean package
```

## Testing

To launch your application's tests, run:

```
mvn verify
```

For more information, refer to the [Running tests page][].

## Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f docker/sonar.yml up -d
```

> You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis inside a project with `sonar.properties` file and maven plugin:

```
./mvnw clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

or

For more information, refer to the [Code quality page][].


## Kubernetes

- Install the catalog-service
    
        helm install --dep-up --name catalog-service --namespace dev-micro -f catalog-service/values.yaml ../charts/microservice-template

- Install the ordering-service
    
        helm install --dep-up --name ordering-service --namespace dev-micro -f ordering-service/values.yaml ../charts/microservice-template
        
- Remove helm charts created
  
  ```bash
  helm delete catalog-service --purge
  helm delete ordering-service --purge
  
  # Remove all the charts
  helm delete $(helm list -q) --purge
  ```