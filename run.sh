#!/usr/bin/env bash

set -e

echo "Build all project"
mvn clean package -U -Dmaven.test.skip=true
echo "Down all"
docker-compose down
echo "Start Full System "
docker-compose  up -d --build
echo "Show backend logs"
docker logs --follow iot
#mvn clean install -pl backend -am -DskipTests=true






