# Corrección de Maven Failsafe para las pruebas de integración

## Síntoma

Durante `mvn clean verify`, las pruebas `*IT` fallaban antes de iniciar el contexto de Spring:

```text
Failed to find merged annotation for
@org.springframework.test.context.BootstrapWith(
  org.springframework.boot.test.context.SpringBootTestContextBootstrapper.class
)
```

## Causa

El proyecto usa el BOM de Spring Boot, pero deliberadamente no hereda de
`spring-boot-starter-parent`. En la fase `package`, `spring-boot:repackage`
reemplaza el JAR normal por un JAR ejecutable con clases dentro de `BOOT-INF/classes`.

Por defecto, Maven Failsafe puede intentar usar ese JAR reempaquetado como salida
del proyecto. Las pruebas de Spring necesitan las clases compiladas normales de
`target/classes`. Al no encontrarlas de la forma esperada, la lectura de
`@SpringBootTest` y su meta-anotación `@BootstrapWith` falla.

No era un error de H2 ni del código de dominio.

## Corrección aplicada

En `motor-scoring-bootstrap/pom.xml`:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-failsafe-plugin</artifactId>
  <configuration>
    <classesDirectory>${project.build.outputDirectory}</classesDirectory>
    <useModulePath>false</useModulePath>
  </configuration>
  <executions>
    <execution>
      <goals>
        <goal>integration-test</goal>
        <goal>verify</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

`classesDirectory` obliga a Failsafe a cargar `target/classes` en vez del JAR
ejecutable reempaquetado.

## Reconstrucción limpia

Para evitar reutilizar una capa antigua de Docker:

```bash
docker compose down -v
docker builder prune -f
docker compose build --no-cache
docker compose up -d
```

También puede probarse sin iniciar el contenedor final:

```bash
docker compose build --no-cache
```

El Dockerfile ejecuta `mvn -B -ntp clean verify`; por tanto, la imagen solo se
construye cuando pasan las pruebas unitarias, de arquitectura, integración y E2E.
