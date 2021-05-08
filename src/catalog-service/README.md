# README

## Chart Installation

- Install the chart

        helm install --name catalog-service --namespace dev-micro -f values.yaml ../../charts/microservice-template

        helm install --name catalog-service --namespace dev-micro -f values.yaml ../../charts/microservice-template --dry-run --debug

 - Remove the helm chart
 
        helm delete catalog-service --purge
       
## Create orders 
        
```json
{
  "name": "Xbox One",
  "description": "Game console to play videgames and media",
  "entryDate": "2019-06-02T08:32:59.441Z",
  "price": 359.99,
  "provider": "Microsoft"
}
```

## Liquibasae

### Update

        mvn liquibase:update

### Rollout

There are two ways to do roll-out using liquibase, **manual** and **automatic**. 

- Manual: those changes must be implicitly added into the changelog.
- Automatic: inferred automatically by changelogs.

Also, there are three ways to get to a previous state.

- By Count

        # Rollout 1 step back 
        mvn liquibase:rollback -Dliquibase.rollbackCount=1
