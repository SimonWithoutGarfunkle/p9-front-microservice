#!/bin/bash

# wait for patient microservice
dockerize -wait tcp://microservice-patient:9001 -timeout 60s
# wait 10 more seconds for post contruct instructions to setup database
sleep 10

# start front microservice
java -jar medilabo.jar
