#!/bin/bash

# HL7 parsing details.

PORT=8081

curl -v -X GET http://localhost:$PORT/datareceiver/fhir/parse-details | python3 -m json.tool
