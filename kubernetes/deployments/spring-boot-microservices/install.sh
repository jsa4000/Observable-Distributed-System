#!/bin/bash

# Create temporary folder to copy configuration files and charts
# $1 can be booking, car, flight or hotel
export APP_NAME=$1
export TEMP_DIR=/tmp/local-deployment
export CHART_DIR=../../../kubernetes/charts/microservice-chart-java
export CONFIG_DIR=../../../kubernetes/deployments/spring-boot-microservices/${APP_NAME}-microservice/LOCAL
mkdir -p $TEMP_DIR
cp -r $CHART_DIR/. $TEMP_DIR
cp -r $CONFIG_DIR/. $TEMP_DIR

# Install or upgrade tthe chart
helm3 install -n micro --create-namespace ${APP_NAME}-microservice $TEMP_DIR -f $TEMP_DIR/values.yaml
# helm3 upgrade -n micro ${APP_NAME}-microservice $TEMP_DIR -f $TEMP_DIR/values.yaml
 
# Remove temp files created
rm -rf $TEMP_DIR