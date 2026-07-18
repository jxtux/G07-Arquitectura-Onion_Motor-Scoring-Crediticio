$ErrorActionPreference = "Stop"
mvn clean verify
mvn -pl motor-scoring-bootstrap spring-boot:run
