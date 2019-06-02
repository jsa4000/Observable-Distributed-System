# README

## Chart Installation

- Install the chart

        helm install --name catalog-service --namespace dev-micro -f values.yaml ../charts/microservice-template

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