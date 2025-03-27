#!/bin/bash

# HL7 message statistics.

PORT=8081

curl -v -X GET http://localhost:$PORT/datareceiver/fhir/statistics | python3 -m json.tool
