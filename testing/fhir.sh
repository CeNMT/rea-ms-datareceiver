#!/bin/bash

# Script to start/stop a test FHIR server
#    Start: up --force-recreate
#    Stop: down --remove-orphans

docker compose -f fhir-compose.yml $1
