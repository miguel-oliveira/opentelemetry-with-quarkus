#!/bin/bash

mvn clean install

docker image rm traced-app:latest --force
docker build . -t traced-app:latest