# Motor de Scoring Crediticio — Arquitectura Onion v2

Implementación de los requerimientos:

- **RF04:** registrar una solicitud de crédito.
- **RF05:** validar la información antes del cálculo.
- **RF06:** calcular el scoring crediticio y persistir el detalle por factor.

Tecnologías: Java 21, Spring Boot, Spring Data JPA, H2, Flyway, Swagger/OpenAPI, JUnit 5, Mockito, ArchUnit y Docker Compose.

## Arquitectura Onion estricta

El proyecto es Maven multimódulo:

```text
motor-scoring-crediticio-onion-v2/
├── motor-scoring-domain/          # Núcleo Java puro
├── motor-scoring-application/     # Casos de uso
├── motor-scoring-infrastructure/  # JPA, repositorios y transacciones
├── motor-scoring-presentation/    # REST, DTO HTTP y Swagger
└── motor-scoring-bootstrap/       # Spring Boot, beans, H2, Flyway y pruebas E2E
```

`motor-scoring-domain` no tiene dependencia de Spring, Jakarta, JPA, Hibernate ni H2. Los Value Objects validan sus invariantes y una prueba ArchUnit comprueba la dirección de dependencias.

Más detalle: [docs/ARQUITECTURA_ONION.md](docs/ARQUITECTURA_ONION.md).

## Endpoints

### RF04 — Registrar solicitud

```http
POST /api/solicitudes-credito
Content-Type: application/json
```

```json
{
  "identificadorExterno": "WEB-2026-000001",
  "solicitante": {
    "tipoDocumento": "DNI",
    "numeroDocumento": "12345678",
    "nombresRazonSocial": "Persona de prueba",
    "ingresosMensuales": 5500,
    "gastosMensuales": 1800,
    "obligacionesFinancieras": 700,
    "antiguedadLaboralNegocio": 36,
    "numeroObligacionesActivas": 2,
    "puntajeHistorialPagos": 85,
    "alertasMora": 0
  },
  "codigoProducto": "PRESTAMO_PERSONAL",
  "montoSolicitado": 15000,
  "plazoSolicitado": 24,
  "moneda": "PEN",
  "finalidadCredito": "Consumo",
  "canalOrigen": "WEB"
}
```

Respuesta: `201 Created`, con estado `REGISTRADA`.

### RF05 y RF06 — Validar y calcular

```http
POST /api/solicitudes-credito/{idSolicitud}/evaluar
```

El endpoint:

1. Comprueba que la solicitud esté registrada.
2. Valida los datos financieros del solicitante.
3. Valida producto activo, monto, plazo y moneda.
4. Obtiene la versión vigente del modelo.
5. Verifica que los pesos activos sumen 100 %.
6. Impide evaluaciones duplicadas con la misma versión.
7. Calcula el score de 0 a 1000.
8. Aplica reglas excluyentes.
9. Guarda evaluación, resultado final y detalle de factores.

## Modelo inicial

```text
Producto: PRESTAMO_PERSONAL
Moneda: PEN
Monto: 1 000 a 50 000
Plazo: 6 a 48 meses
Modelo: MODELO_PERSONAL, versión 1.0.0
```

Factores: historial de pagos, relación deuda-ingreso, capacidad de pago, estabilidad de ingresos, antigüedad, obligaciones activas, monto frente a capacidad y alertas de mora.

Los valores son ficticios y se usan únicamente con fines académicos.

## Levantar con Docker Compose

Requisito: Docker Desktop o Docker Engine con Compose.

```bash
docker compose up --build -d
```

El Dockerfile ejecuta `mvn clean verify` durante la construcción. Si alguna prueba unitaria, de integración, E2E o arquitectura falla, la imagen no se genera.

Ver logs:

```bash
docker compose logs -f motor-scoring-api
```

Detener:

```bash
docker compose down
```

Recrear la base H2 desde cero:

```bash
docker compose down -v
docker compose up --build -d
```

## Levantar con Java y Maven

Requisitos: JDK 21 y Maven 3.9+.

```bash
mvn clean verify
mvn -pl motor-scoring-bootstrap spring-boot:run
```

Windows PowerShell:

```powershell
.\scripts\run-local.ps1
```

Linux/macOS:

```bash
./scripts/run-local.sh
```

## URLs

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`
- H2 Console: `http://localhost:8080/h2-console`

Consola H2 cuando se ejecuta con Maven:

```text
JDBC URL: jdbc:h2:file:./data/motor_scoring;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_ON_EXIT=FALSE
Usuario: sa
Contraseña: vacía
```

Con Docker:

```text
JDBC URL: jdbc:h2:file:/app/data/motor_scoring;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_ON_EXIT=FALSE
Usuario: sa
Contraseña: vacía
```

## Corrección de H2

Esta versión no mezcla `ddl-auto=create`, `schema.sql` y Flyway. Solo Flyway crea el esquema:

```yaml
spring.jpa.hibernate.ddl-auto: validate
spring.flyway.enabled: true
spring.sql.init.mode: never
```

Consulte [docs/CORRECCION_H2.md](docs/CORRECCION_H2.md).

## Pruebas

```bash
mvn test      # pruebas unitarias
mvn verify    # unitarias + integración + E2E + ArchUnit
```

Incluye:

- Pruebas de Value Objects e invariantes.
- Pruebas de entidad y servicios del dominio.
- Prueba de caso de uso con Mockito.
- Integración real con H2 + Flyway + JPA.
- Punto a punto por HTTP en puerto aleatorio.
- Pruebas ArchUnit de la regla de dependencias Onion.

## Postman

Importe:

- `postman/Motor-Scoring-Onion-v2.postman_collection.json`
- `postman/Motor-Scoring-Local.postman_environment.json`

Ejecute en este orden: Health, RF04 y RF05/RF06.

## Documentación incluida

- Arquitectura Onion detallada.
- Modelo de base de datos.
- Corrección del error H2.
- Detalle de implementación RF04–RF06.
- OpenAPI estático.
- Diagramas originales de arquitectura, datos, casos de uso, clases y secuencias.
- Colección Postman.

## Corrección v2.0.1: Maven Failsafe

La versión 2.0.1 agrega explícitamente:

```xml
<classesDirectory>${project.build.outputDirectory}</classesDirectory>
```

en el plugin Maven Failsafe del módulo `motor-scoring-bootstrap`. Esto corrige
el error `Failed to find merged annotation for @BootstrapWith` que aparecía al
ejecutar las pruebas `*IT` después de `spring-boot:repackage`.

Para reconstruir sin caché:

```bash
docker compose down -v
docker builder prune -f
docker compose build --no-cache
docker compose up -d
```

Más detalles: `docs/CORRECCION_FAILSAFE.md`.

## Corrección v2.0.2: escaneo de JPA

La versión 2.0.2 registra explícitamente los paquetes de repositorios Spring
Data y entidades JPA mediante `@EnableJpaRepositories` y `@EntityScan` en el
módulo de infraestructura. Esto corrige el error:

```text
No qualifying bean of type 'EvaluacionCrediticiaSpringDataRepository'
```

También se estabilizó el nombre del JAR ejecutable como
`motor-scoring-app.jar`, evitando que el Dockerfile dependa de la versión.

Después de reemplazar la versión anterior, reconstruya sin caché:

```bash
docker compose down -v
docker compose build --no-cache
docker compose up -d
```

Detalle: `docs/CORRECCION_JPA_REPOSITORIES.md`.
