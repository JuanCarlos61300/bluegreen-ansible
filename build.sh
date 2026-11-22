#!/bin/bash
set -e
cd "$(dirname "$0")/app"
./mvnw clean package -DskipTests
for v in v1 v2; do
  docker build --build-arg APP_VERSION=$v -t hola-app:$v .
done
