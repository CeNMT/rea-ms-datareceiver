FROM gradle:8.10.2-jdk17-alpine AS build

LABEL com.cnmt.dockerimage.author="euggor"

WORKDIR /gradle/app
COPY --chown=gradle:gradle . ./
RUN gradle --no-daemon --parallel build -x test

FROM openjdk:17-slim-buster

# For health check
RUN set -eux \
    && apt-get update \
    && apt-get install --yes --no-install-recommends curl \
    && rm --recursive --force /var/lib/apt/lists/* \
    && which curl

WORKDIR /app
COPY --from=build /gradle/app/build/libs/rea-ms-datareceiver-0.1-all.jar ./datareceiver.jar
COPY wait-for-it.sh /wait-for-it.sh
