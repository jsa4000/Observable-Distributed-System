# README

## Chart Installation

- Install the chart

        helm install --name order-service --namespace dev-micro -f values.yaml ../charts/microservice-template

 - Remove the helm chart
 
        helm delete order-service --purge
       
## Create orders 
        
```json
{
  "invoiceId": 12,
  "orderDate": "2019-05-24T13:59:07.903Z",
  "totalCost": 10.3,
  "totalCostWithoutVap": 9.78,
  "agency": "ecommerce",
  "method": "online",
  "provider": "servired",
  "reportDate": "2019-05-24T08:21:07.904Z",
  "clientId": 1234
}
```