#!/usr/bin/env bash

# build the docker image using the dockerfile-maven-plugin
mvn package
mvn dockerfile:build
# then run the container
docker run -p 5555:8080 -t ghcr.io/oescal/mtd-test-app:java-zip-slip

# python2 evilarc.py example.war -o=unix -p /usr/local/tomcat/deploy
