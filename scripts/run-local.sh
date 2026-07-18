#!/usr/bin/env sh
set -eu
mvn clean verify
mvn -pl motor-scoring-bootstrap spring-boot:run
