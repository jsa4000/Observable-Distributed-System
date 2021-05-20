# Traefik Controller

## Deploy Traefik Controller

1. Install Traefik Controller in the cluster

    Add Traefik repo to Heml

    `helm3 repo add traefik https://helm.traefik.io/traefik`

    `helm3 repo update`

    Install Traefik into the `tools` namespace

    > Get all the services within tracing namespace to get jaeger services (agent and collector)

    ```bash
    helm3 install -n tools --create-namespace traefik traefik/traefik --version 9.19.1 \
    --set 'additionalArguments[0]=--api.insecure' \
    --set 'additionalArguments[1]=--metrics.prometheus=true' \
    --set 'additionalArguments[2]=--tracing.jaeger=true' \
    --set 'additionalArguments[3]=--tracing.serviceName=traefik-service' \
    --set 'additionalArguments[4]=--tracing.jaeger.samplingParam=1.0' \
    --set 'additionalArguments[5]=--tracing.jaeger.disableAttemptReconnecting=false' \
    --set 'additionalArguments[6]=--tracing.jaeger.samplingType=const' \
    --set 'additionalArguments[7]=--tracing.jaeger.samplingServerURL=http://jaeger-all-in-one-inmemory-agent.tracing.svc:5778/sampling' \
    --set 'additionalArguments[8]=--tracing.jaeger.localAgentHostPort=jaeger-all-in-one-inmemory-agent.tracing.svc:6831'
    ```

2. Verify the installation by accessing to Jeager UI via traefik (loadbalancer) at http://localhost/search

    `kubectl get svc -n tools`

3. Deploy prometheus-operator `ServiceMonitor` resource using the following command.

    > Note: Prometheus operator is required. Set the label `release` set to match with the prometheus-operator `release: prometheus`

    `kubectl apply -n tools -f Kubernetes/files/traefik-service-monitor.yaml`

4. Access to the *dashboard* and *metrics* exposed by traefik

    `kubectl port-forward -n tools svc/traefik-dashboard 9000`

   * [Dashboard](localhost:9000/dashboard)
   * [Prometheus Metrics](http://localhost:9000/metrics)

5. Delete Traefik Controller in the cluster

    `helm3 uninstall -n tools traefik`

## References

There are several articles to how configure `traefik` to be used with **Jaeger** and **Prometheus**.

* Prometheus

  * [Capture Prometheus Metrics](https://traefik.io/blog/capture-traefik-metrics-for-apps-on-kubernetes-with-prometheus/)
  * [Github Example](https://github.com/traefik-tech-blog/traefik-sre-metrics)
