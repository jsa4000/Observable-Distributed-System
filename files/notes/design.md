# Design

## Introduction

The idea is to design a basic (but complete) distributed micro-service architecture that allows **Observability in Distributed Systems**.

The main points to focus related with **observability** are:

- **Logging**: it provides detailed information about the application and the operations performed over-time. Depending on its level (debug, info, warning, error, etc...) the information stored in the logs vary. Logs are merely used for troubleshooting and for historical backlog, so logs can be seen later to check correct behavior or auditories. Logging is a mechanism to find problems in applications during and after development.
- **Tracing**: be able to track the different actions a request takes within the system. The output must be a complete physical **path** a request takes from *end-to-end*; since it enters into the system until the response is returned back. Every request has an unique **traceId** and multiple **spanIds**, depending on the different contexts the request pass-through. A **context** could be calls between micro-services, operations to a database, communication between threads, etc..
- **Metrics**: a reliable set of measurements (*timers*, *counters*, *gauge*, *distributions*) to help discover different aspects of the system such as: performances, rate of errors, total requests per service, etc.

The idea with **Observability in Distributed System** also implies that this information, logging, tracing and metrics, must be **visible** and **centralized** so it can be easily consumed by operators and developers. The **instrumentation** and the **infrastructure** used for motorization, must be also as independent as possible on the technologies, languages (polyglot) and frameworks used.

## Conception

- The entry-point on the system is going to be based on **API Gateway**. Using REST-ful definitions for the endpoints.
- Micro-services are going to be **stateless**, so they can be scaled-out as needed. In this P.O.C. there is not going to be *replication* or *load balancing*: neither client or server-proxy side.
- Communication between services is going to be **synchronous**. To simplifies the P.O.C since there is no going to be *events* or *reactive* streams.
- The **service discovery** is going to be based on docker-compose and using **links** to inference the addresses. The purpose of this P.O.C is to be able to sun locally using docker.

## Micro-services

OAuth Service: it authenticates the HTTP requests using JWT and key-pair based authentication.
API-Gateway: entry-point of the system for incoming requests. It performs the OAuth and route the request to the different back-ends.
Order Service: service that manage the orders performed.
Product Service: performs all the operations related to the product management: create, update and delete. It is used to check availability, quantity, price of the products.
Customer Service:

## Infrastructure

> All the version numbers are taken from it ``latest`` tag on 2018/10/15.

- Logging: fluentd v1.2.6, elasticsearch 6.4.2 and Kibana 6.4.2
- Tracing: jaeger/all-in-one 1.7.0, zipkin 2.11.6
- Metrics: prometheus v2.4.3, node-exporter v0.16.0, cadvisor v0.31.0, alertmanager v0.15.2, grafana 5.3.0
- Back-end: mongodb 3.6.8, hazelcast 3.10.6