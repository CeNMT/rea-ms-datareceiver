#!/bin/bash

# Check health API

PORT=8082

curl -v -X GET http://localhost:${PORT}/datareceiver/management/health | python3 -m json.tool
