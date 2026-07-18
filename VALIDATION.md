# Validación de la entrega v2.0.2

## Correcciones aplicadas

- Failsafe usa `target/classes` para las pruebas `*IT`.
- Spring Data JPA escanea explícitamente el paquete de repositorios.
- Hibernate escanea explícitamente el paquete de entidades.
- El JAR ejecutable tiene el nombre estable `motor-scoring-app.jar`.
- El Dockerfile copia el JAR estable y no una versión fija.

## Verificaciones estáticas realizadas

- Todos los POM usan la versión `2.0.2`.
- Los seis archivos `pom.xml` son XML bien formado.
- `JpaPersistenceConfiguration` está en infraestructura, fuera del dominio.
- El dominio continúa sin dependencias de Spring, JPA, Hibernate o Jakarta.
- El Dockerfile apunta a un artefacto existente según `finalName`.

## Limitación del entorno

Este entorno no dispone de Maven ni Docker, por lo que no fue posible ejecutar
`mvn clean verify` ni `docker compose build`. El error reportado ocurre durante
la creación del contexto Spring y queda cubierto por las pruebas de integración
existentes una vez que el escaneo JPA está habilitado.
