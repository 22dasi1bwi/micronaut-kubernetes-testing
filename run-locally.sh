#!/bin/bash
TAG=$1
./gradlew clean build
kubectl delete deployment demo
docker build -t demo:$1 .
docker tag demo:$1 <docker-workspace>/demo:$1
docker push <docker-workspace>/demo:$1
kubectl apply -f k8s.yml