# Corrección del error de H2

La versión anterior podía fallar cuando Hibernate y otro mecanismo intentaban crear o inicializar el mismo esquema, o cuando el orden de creación no estaba garantizado.

La nueva versión aplica una única estrategia:

1. **Flyway es el único responsable de crear y poblar las tablas.**
2. `spring.jpa.hibernate.ddl-auto=validate`: Hibernate nunca crea ni modifica el esquema; solo comprueba que coincide con las entidades JPA.
3. `spring.sql.init.mode=never`: se desactiva la inicialización paralela con `schema.sql`/`data.sql`.
4. Las migraciones usan sintaxis compatible con H2 2.x y `MODE=PostgreSQL`.
5. Las pruebas de integración usan una base H2 en memoria nueva y ejecutan Flyway antes de levantar JPA.
6. Docker usa una base H2 de archivo en `/app/data`, conservada en un volumen.

Configuración principal:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/motor_scoring;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
  sql:
    init:
      mode: never
```

Para reiniciar completamente la base Docker:

```bash
docker compose down -v
docker compose up --build -d
```
