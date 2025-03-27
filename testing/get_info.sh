#!/bin/bash

# Check management API: info

PORT=8082

curl -v -X GET http://localhost:${PORT}/datareceiver/management/info | python3 -m json.tool
