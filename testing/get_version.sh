#!/bin/bash

# Check REST API controller

PORT=8081

curl -v -X GET http://localhost:$PORT/datareceiver/fhir/version -H 'Accept: */*'
