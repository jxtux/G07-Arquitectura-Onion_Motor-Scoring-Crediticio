# Arquitectura Onion implementada

El proyecto utiliza cinco módulos Maven. Esta separación física impide que el núcleo importe accidentalmente Spring, JPA, HTTP o H2.

```text
motor-scoring-domain
        ↑
motor-scoring-application
        ↑                    ↑
motor-scoring-presentation  motor-scoring-infrastructure
        \                    /
         motor-scoring-bootstrap
```

## 1. `motor-scoring-domain`

Núcleo Java puro:

- Entidades: `Solicitante`, `SolicitudCredito`, `ProductoCrediticio`, `ModeloScoring`, `VersionModelo`, `FactorScoring`, `ReglaEvaluacion`, `EvaluacionCrediticia`.
- Value Objects: `Dinero`, `Porcentaje`, `PuntajeCrediticio`, `NumeroDocumento`, `IdentificadorExterno`, `RelacionDeudaIngreso`, `CapacidadPago`.
- Servicios de dominio: cálculo de capacidad de pago, relación deuda-ingreso, reglas excluyentes y score.
- Puertos de repositorio.
- Excepciones e invariantes.

Este módulo no declara dependencias de Spring, Jakarta, JPA, Hibernate ni H2.

## 2. `motor-scoring-application`

Orquesta RF04, RF05 y RF06 mediante casos de uso:

- `CrearSolicitudCreditoUseCase`.
- `EjecutarEvaluacionScoringUseCase`.

Depende únicamente de `motor-scoring-domain`.

## 3. `motor-scoring-infrastructure`

Contiene adaptadores externos:

- Entidades JPA separadas de las entidades del dominio.
- Spring Data repositories.
- Adaptadores que implementan los puertos del dominio.
- Decoradores transaccionales con `TransactionTemplate`.

## 4. `motor-scoring-presentation`

Adaptador REST:

- Requests y responses HTTP.
- Validaciones de formato con Jakarta Validation.
- Controlador.
- Manejador global de errores.
- Swagger/OpenAPI.

No calcula el score ni accede a JPA.

## 5. `motor-scoring-bootstrap`

Composition root:

- Inicia Spring Boot.
- Conecta casos de uso, dominio y adaptadores mediante beans.
- Contiene configuración, migraciones Flyway y pruebas integrales.

## Regla de dependencias

Todas las dependencias de código apuntan hacia el dominio. El dominio no conoce ninguna capa exterior. Se incluye una prueba ArchUnit para comprobar esta regla automáticamente.
