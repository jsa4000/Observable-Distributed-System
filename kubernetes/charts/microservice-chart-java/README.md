# Helm Chart for Microservives

## Manual Deployment

- Copy the following content from microservice-chart into a folder

```txt
├── Chart.yaml
├── README.md
├── config
│   └── ..
├── secrets
│   └── ..
├── templates
│   ├── NOTES.txt
│   ├── _helpers.tpl
│   ├── configmap.yaml
│   ├── deployment.yaml
│   ├── destination-rule-orig-tls.yaml
│   ├── ingress.yaml
│   ├── secrets.yaml
│   ├── service-entry.yaml
│   └── service.yaml
└── values.yaml
```

- Copy the specific configuration for this microservice/environment.

```txt
├── config
│   └── application.yml
├── secrets
│   ├── database.username
│   ├── database.password
│   └── jwt.publicKey
└── values.yaml
```

- Create namespace

```
kubectl create namespace micro
```

- Switch to namespace

```
kubectl config set-context --current --namespace=micro
```

- Install the Chart

```bash
helm3 install microservice -f values.yaml .

# Or into specific namespace already created
helm3 install microservice -f values.yaml --namespace micro .
```

- List installed charts into current namespace

```bash
helm3 list
```

- Upgrade the chart

```bash
helm3 upgrade microservice -f values.yaml .
```

- Delete the chart

```bash
helm3 delete microservice
```