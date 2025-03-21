#!/bin/bash

VERSION=0.1
NAME=datareceiver
IMAGE_NAME=${NAME}_${VERSION}

docker stop ${NAME}:${VERSION}
docker rmi -f ${NAME}:${VERSION}

docker build . -t ${NAME}:${VERSION} || exit 1
docker save -o ${NAME}_${VERSION}.tar ${NAME}:${VERSION} || exit 1
rm  ${IMAGE_NAME}.tar.gz
gzip ${IMAGE_NAME}.tar || exit 1
printf "Docker image created: %s\n" "$(pwd)/${IMAGE_NAME}.tar.gz"
