# Motor de Scoring Crediticio

## Documentación técnica para presentación

---

## 1. Nombre del proyecto

**Motor de Scoring Crediticio**

El proyecto consiste en una aplicación que registra solicitudes de crédito, valida si la información necesaria se encuentra completa y ejecuta el cálculo de un puntaje crediticio mediante un modelo de scoring.

La solución se implementa utilizando **Arquitectura Onion**, de forma que la lógica de negocio permanezca independiente de la API, la base de datos, Spring Boot y cualquier servicio externo.

---

## 2. Alcance del proyecto

El proyecto implementará únicamente los siguientes requerimientos funcionales:

### RF04. Registro de solicitud de crédito

El sistema permitirá registrar una solicitud indicando:

- Solicitante.
- Producto crediticio.
- Monto solicitado.
- Plazo solicitado.
- Moneda.
- Finalidad del crédito.
- Canal de origen.
- Fecha y hora de registro.
- Identificador externo para evitar duplicados.

La solicitud se registrará inicialmente con estado:

```text
REGISTRADA
```

### RF05. Validación de información

Antes de calcular el score, el sistema validará:

- Que los datos obligatorios estén completos.
- Que los ingresos, gastos, deudas, monto y plazo tengan valores válidos.
- Que el producto crediticio se encuentre activo.
- Que el monto solicitado se encuentre dentro de los límites del producto.
- Que el plazo solicitado se encuentre dentro de los límites del producto.
- Que exista una versión activa del modelo de scoring.
- Que la solicitud no haya sido evaluada previamente con la misma versión y los mismos datos.

### RF06. Cálculo del scoring crediticio

El sistema calculará el puntaje utilizando los factores definidos en el documento:

- Comportamiento e historial de pagos.
- Relación entre obligaciones e ingresos.
- Capacidad de pago disponible.
- Estabilidad de ingresos.
- Antigüedad laboral o del negocio.
- Número de obligaciones activas.
- Relación entre el monto solicitado y la capacidad estimada.
- Alertas de mora o incumplimiento registradas en fuentes autorizadas.

El resultado deberá contener:

- Puntaje total.
- Puntaje por factor.
- Reglas aplicadas.
- Versión del modelo utilizada.

---

## 3. Objetivo técnico

Construir un motor de scoring en el que:

- La lógica del cálculo se encuentre en el dominio.
- Los controladores no calculen el score.
- La capa de aplicación coordine los casos de uso.
- La infraestructura implemente la persistencia.
- Las dependencias apunten hacia el núcleo.
- El dominio pueda probarse sin base de datos ni servidor web.
- La base de datos pueda cambiarse con un impacto mínimo.
- Las evaluaciones sean reproducibles y trazables.

---

## 4. Tecnologías seleccionadas

La solución utilizará la alternativa Java indicada en el documento:

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot |
| API | Spring Web |
| Persistencia | Spring Data JPA |
| ORM | Hibernate |
| Base de datos | PostgreSQL |
| Documentación API | Swagger / OpenAPI |
| Pruebas unitarias | JUnit 5 |
| Dobles de prueba | Mockito |
| Gestión de dependencias | Maven |
| Contenedores | Docker y Docker Compose |

---

## 5. Arquitectura Onion

La aplicación se divide en cuatro capas:

```text
Presentation
      ↓
Application
      ↓
Domain

Infrastructure
      ↓
Application / Domain
```

Las dependencias siempre apuntan hacia el centro.

### 5.1. Capa de dominio

Es el núcleo de la aplicación.

Contiene:

- Entidades.
- Objetos de valor.
- Enumeraciones.
- Reglas de negocio.
- Servicios de dominio.
- Interfaces de repositorios.
- Excepciones de dominio.

No depende de:

- Spring Boot.
- JPA.
- Hibernate.
- PostgreSQL.
- HTTP.
- Swagger.
- Controladores.

### 5.2. Capa de aplicación

Coordina los casos de uso.

Contiene:

- Casos de uso.
- Comandos.
- DTO.
- Validadores de aplicación.
- Mapeadores.
- Coordinación de transacciones.
- Uso de interfaces de repositorios.

Esta capa puede depender del dominio, pero no debe depender directamente de infraestructura.

### 5.3. Capa de infraestructura

Implementa los contratos definidos en las capas internas.

Contiene:

- Entidades JPA.
- Repositorios Spring Data.
- Adaptadores de repositorios.
- Configuración de base de datos.
- Migraciones.
- Implementaciones técnicas.

### 5.4. Capa de presentación

Expone los casos de uso mediante API REST.

Contiene:

- Controladores.
- Request.
- Response.
- Manejo global de errores.
- Validaciones básicas de formato.
- Swagger/OpenAPI.

No contiene la lógica de scoring.

---

## 6. APIs del proyecto

Se implementarán dos endpoints principales.

### 6.1. Registrar solicitud de crédito

```http
POST /api/solicitudes-credito
```

Este endpoint implementa RF04.

#### Ejemplo de request

```json
{
  "solicitanteId": 1,
  "productoCrediticioId": 2,
  "montoSolicitado": 15000.00,
  "plazoSolicitado": 24,
  "moneda": "PEN",
  "finalidadCredito": "Capital de trabajo",
  "canalOrigen": "WEB",
  "identificadorExterno": "WEB-2026-000001"
}
```

#### Flujo

```text
Cliente
  ↓
SolicitudCreditoController
  ↓
CrearSolicitudCreditoUseCase
  ↓
Validar datos de registro
  ↓
Verificar identificador externo
  ↓
Guardar solicitud
  ↓
Estado REGISTRADA
```

#### Respuesta esperada

```json
{
  "solicitudId": 1001,
  "estado": "REGISTRADA",
  "fechaRegistro": "2026-07-17T20:00:00"
}
```

#### Código HTTP

```http
201 Created
```

### 6.2. Validar y ejecutar scoring

```http
POST /api/solicitudes-credito/{id}/evaluar
```

Este endpoint implementa RF05 y RF06.

#### Flujo

```text
Cliente
  ↓
SolicitudCreditoController
  ↓
EjecutarEvaluacionScoringUseCase
  ↓
Buscar solicitud
  ↓
Validar información
  ↓
Buscar versión activa del modelo
  ↓
Obtener factores y reglas
  ↓
Calcular puntaje por factor
  ↓
Calcular puntaje total
  ↓
Guardar evaluación
  ↓
Guardar resultados por factor
  ↓
Retornar resultado
```

#### Respuesta esperada

```json
{
  "evaluacionId": 501,
  "solicitudId": 1001,
  "versionModelo": "1.0",
  "puntajeTotal": 780,
  "resultado": "PREAPROBADA",
  "factores": [
    {
      "factor": "RELACION_DEUDA_INGRESO",
      "valorEvaluado": "0.35",
      "pesoAplicado": 20.00,
      "puntajeObtenido": 150,
      "reglaAplicada": "RANGO_CONFIGURADO"
    }
  ]
}
```

---

## 7. Validaciones del sistema

Las validaciones se dividen en dos niveles.

### 7.1. Validaciones de entrada

Se ejecutan en la capa de presentación o aplicación:

- Campos obligatorios.
- Tipos de datos.
- Formato de moneda.
- Monto mayor que cero.
- Plazo mayor que cero.
- Identificador externo obligatorio.

### 7.2. Validaciones de negocio

Se ejecutan antes del scoring:

- Solicitud existente.
- Solicitud en estado permitido.
- Solicitante existente.
- Producto existente y activo.
- Monto dentro de rango.
- Plazo dentro de rango.
- Versión activa del modelo.
- Factores configurados.
- Pesos válidos.
- Evaluación no duplicada.
- Datos financieros completos.

La validación RF05 no se expone como una API independiente. Se ejecuta obligatoriamente dentro del caso de uso de evaluación.

---

## 8. Reglas de negocio aplicables

Para el alcance RF04, RF05 y RF06 se aplican las siguientes reglas del documento:

### RN01. Información mínima

No se puede ejecutar una evaluación si faltan datos obligatorios.

### RN02. Rango del score

El puntaje final debe encontrarse entre:

```text
0 y 1000 puntos
```

### RN03. Modelo activo

Cada producto debe tener una versión activa del modelo para la fecha de evaluación.

### RN04. Pesos del modelo

La suma de pesos de los factores debe ser igual a:

```text
100 %
```

### RN05. Datos no permitidos

No se utilizarán atributos sensibles o no autorizados como factores de scoring.

### RN06. Capacidad de pago

La capacidad de pago se calcula con ingresos, gastos y obligaciones vigentes.

### RN07. Relación deuda-ingreso

La relación deuda-ingreso se calcula utilizando obligaciones mensuales e ingresos mensuales válidos.

### RN09. Reglas excluyentes

Una regla excluyente puede modificar el resultado aun cuando el puntaje sea alto.

### RN10. Resultado reproducible

La misma entrada y la misma versión del modelo deben producir el mismo resultado.

### RN11. Inmutabilidad

Una evaluación finalizada no debe modificarse.

### RN14. Idempotencia

El mismo identificador externo no debe generar solicitudes duplicadas.

### RN15. Trazabilidad

La evaluación debe conservar factores, reglas, pesos y versión aplicados.

---

## 9. Modelo de datos

El modelo utiliza las siguientes tablas:

```text
solicitantes
productos_crediticios
solicitudes_credito
modelos_scoring
versiones_modelo
factores_scoring
reglas_evaluacion
evaluaciones_crediticias
resultados_factor
```

### 9.1. Relaciones principales

```text
Solicitante 1 ────── * SolicitudCredito

ProductoCrediticio 1 ────── * SolicitudCredito

ModeloScoring 1 ────── * ProductoCrediticio

ModeloScoring 1 ────── * VersionModelo

VersionModelo 1 ────── * FactorScoring

FactorScoring 1 ────── * ReglaEvaluacion

SolicitudCredito 1 ────── * EvaluacionCrediticia

VersionModelo 1 ────── * EvaluacionCrediticia

EvaluacionCrediticia 1 ────── * ResultadoFactor

FactorScoring 1 ────── * ResultadoFactor
```

### 9.2. Responsabilidad de cada tabla

| Tabla | Responsabilidad |
|---|---|
| solicitantes | Almacena información personal y financiera |
| productos_crediticios | Define límites de monto, plazo, moneda y modelo |
| solicitudes_credito | Registra la solicitud del cliente |
| modelos_scoring | Identifica el modelo asociado al producto |
| versiones_modelo | Conserva versiones y vigencia |
| factores_scoring | Define factores y pesos |
| reglas_evaluacion | Define rangos, puntajes y reglas excluyentes |
| evaluaciones_crediticias | Registra el resultado general |
| resultados_factor | Conserva el detalle por factor |

---

## 10. Modelo de dominio

Las clases principales del dominio son:

```text
Solicitante
SolicitudCredito
ProductoCrediticio
ModeloScoring
VersionModelo
FactorScoring
ReglaEvaluacion
EvaluacionCrediticia
ResultadoFactor
```

### 10.1. SolicitudCredito

Responsabilidades:

- Mantener la información de la solicitud.
- Verificar si se encuentra registrada.
- Controlar su estado.
- Mantener el identificador externo.

### 10.2. ProductoCrediticio

Responsabilidades:

- Validar el monto solicitado.
- Validar el plazo solicitado.
- Verificar si se encuentra activo.
- Asociar el modelo de scoring.

### 10.3. VersionModelo

Responsabilidades:

- Identificar la versión utilizada.
- Validar su vigencia.
- Mantener los factores de scoring.
- Garantizar trazabilidad histórica.

### 10.4. FactorScoring

Responsabilidades:

- Representar un criterio evaluado.
- Mantener el peso configurado.
- Relacionar las reglas de evaluación.
- Validar el peso.

### 10.5. ReglaEvaluacion

Responsabilidades:

- Determinar si un valor cumple un rango.
- Asignar el puntaje configurado.
- Indicar si una regla es excluyente.
- Registrar el resultado excluyente.

### 10.6. EvaluacionCrediticia

Responsabilidades:

- Asociar solicitud y versión del modelo.
- Guardar el puntaje total.
- Mantener el resultado.
- Conservar el detalle por factor.
- Permanecer inmutable al finalizar.

---

## 11. Servicios de dominio

### CalculadorScoring

Responsable de:

- Evaluar cada factor.
- Aplicar las reglas.
- Calcular el puntaje por factor.
- Calcular el puntaje total.
- Verificar el rango de 0 a 1000.

### CalculadorCapacidadPago

Responsable de calcular la capacidad de pago utilizando:

- Ingresos mensuales.
- Gastos mensuales.
- Obligaciones financieras.

### CalculadorRelacionDeudaIngreso

Responsable de calcular la relación entre obligaciones e ingresos válidos.

### EvaluadorReglasExcluyentes

Responsable de detectar reglas que prevalecen sobre el puntaje ponderado.

---

## 12. Casos de uso

### CrearSolicitudCreditoUseCase

Implementa RF04.

Responsabilidades:

- Recibir el comando.
- Validar datos.
- Verificar idempotencia.
- Consultar solicitante y producto.
- Crear la solicitud.
- Guardar la solicitud.
- Retornar la respuesta.

### EjecutarEvaluacionScoringUseCase

Implementa RF05 y RF06.

Responsabilidades:

- Buscar la solicitud.
- Ejecutar las validaciones.
- Obtener la versión activa.
- Obtener factores y reglas.
- Invocar los servicios del dominio.
- Guardar la evaluación.
- Guardar resultados por factor.
- Retornar puntaje y detalle.

---

## 13. Interfaces de repositorio

Las interfaces se definen en las capas internas.

```java
public interface SolicitudCreditoRepository {
    SolicitudCredito guardar(SolicitudCredito solicitud);
    Optional<SolicitudCredito> buscarPorId(Long id);
    boolean existePorIdentificadorExterno(String identificadorExterno);
}
```

```java
public interface ModeloScoringRepository {
    Optional<VersionModelo> buscarVersionActiva(
        Long productoId,
        LocalDate fecha
    );
}
```

```java
public interface EvaluacionCrediticiaRepository {
    EvaluacionCrediticia guardar(EvaluacionCrediticia evaluacion);
    boolean existeEvaluacionEquivalente(
        Long solicitudId,
        Long versionModeloId
    );
}
```

Las implementaciones se ubican en infraestructura y utilizan Spring Data JPA.

---

## 14. Estructura del proyecto

```text
motor-scoring-crediticio/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/finanscore/motorscoring/
    │   │   ├── MotorScoringApplication.java
    │   │   ├── domain/
    │   │   │   ├── entity/
    │   │   │   ├── valueobject/
    │   │   │   ├── enums/
    │   │   │   ├── service/
    │   │   │   ├── repository/
    │   │   │   └── exception/
    │   │   ├── application/
    │   │   │   ├── usecase/
    │   │   │   ├── command/
    │   │   │   ├── dto/
    │   │   │   ├── validator/
    │   │   │   └── mapper/
    │   │   ├── infrastructure/
    │   │   │   ├── persistence/
    │   │   │   │   ├── entity/
    │   │   │   │   ├── springdata/
    │   │   │   │   └── adapter/
    │   │   │   └── config/
    │   │   └── presentation/
    │   │       ├── controller/
    │   │       ├── request/
    │   │       ├── response/
    │   │       └── exception/
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/
    └── test/
        └── java/com/finanscore/motorscoring/
            ├── domain/
            ├── application/
            └── integration/
```

---

## 15. Dirección de dependencias

### Dependencias permitidas

```text
presentation → application
application → domain
infrastructure → application
infrastructure → domain
```

### Dependencias no permitidas

```text
domain → infrastructure
domain → presentation
domain → Spring
application → JPA
controller → repository JPA
controller → base de datos
```

---

## 16. Persistencia con JPA

Las clases del dominio no deben ser obligatoriamente las mismas clases utilizadas por Hibernate.

Se recomienda separar:

```text
SolicitudCredito
```

de:

```text
SolicitudCreditoJpaEntity
```

El adaptador realiza el mapeo entre ambos modelos.

Esto evita que el dominio dependa de anotaciones como:

```java
@Entity
@Table
@Column
@ManyToOne
```

---

## 17. Manejo de errores

La API debe utilizar códigos HTTP apropiados.

| Código | Uso |
|---|---|
| 201 Created | Solicitud registrada |
| 200 OK | Evaluación ejecutada |
| 400 Bad Request | Formato inválido |
| 404 Not Found | Solicitud o producto inexistente |
| 409 Conflict | Identificador externo duplicado |
| 422 Unprocessable Entity | Solicitud no evaluable |
| 500 Internal Server Error | Error no controlado |

Ejemplo de error:

```json
{
  "codigo": "SOLICITUD_NO_EVALUABLE",
  "mensaje": "La solicitud no cumple las condiciones para ser evaluada.",
  "errores": [
    {
      "campo": "montoSolicitado",
      "mensaje": "El monto está fuera del rango del producto."
    }
  ]
}
```

---

## 18. Pruebas requeridas

### 18.1. Pruebas unitarias del dominio

- No evaluar datos incompletos.
- Score entre 0 y 1000.
- Pesos que sumen 100 %.
- Relación deuda-ingreso aplicada correctamente.
- Historial favorable incrementa la puntuación según configuración.
- Regla excluyente prevalece.
- Evaluación finalizada inmutable.
- Misma entrada y versión producen el mismo resultado.
- Identificador externo duplicado no crea otra solicitud.

### 18.2. Pruebas de aplicación

- Registrar solicitud correctamente.
- Rechazar identificador externo duplicado.
- No evaluar producto inactivo.
- No evaluar sin versión activa.
- Guardar evaluación y resultados por factor.

### 18.3. Pruebas de integración

- Persistencia de solicitudes.
- Consulta de versión activa.
- Persistencia de evaluación.
- Persistencia del detalle por factor.
- Respuesta de los endpoints.

---

## 19. Dockerización

La solución podrá ejecutarse con:

```bash
docker compose up -d
```

Servicios mínimos:

```text
motor-scoring-api
postgres
```

Ejemplo conceptual:

```yaml
services:
  database:
    image: postgres
    environment:
      POSTGRES_DB: scoring
      POSTGRES_USER: scoring_user
      POSTGRES_PASSWORD: scoring_password

  api:
    build: .
    depends_on:
      - database
    ports:
      - "8080:8080"
```

Las credenciales reales no deben almacenarse en el repositorio.

---

## 20. Flujo completo del sistema

```text
1. El cliente registra una solicitud.

2. La API recibe la petición.

3. La capa de aplicación verifica el identificador externo.

4. El dominio crea la solicitud en estado REGISTRADA.

5. Infraestructura guarda la solicitud.

6. El cliente solicita la evaluación.

7. La aplicación busca la solicitud.

8. Se ejecutan las validaciones de RF05.

9. Se obtiene la versión activa del modelo.

10. Se cargan factores y reglas.

11. El dominio calcula los puntajes por factor.

12. El dominio calcula el puntaje total.

13. Se aplican reglas excluyentes.

14. Se crea la evaluación.

15. Infraestructura guarda la evaluación y su detalle.

16. La API devuelve el resultado.
```

---

## 21. Justificación de Arquitectura Onion

La Arquitectura Onion se utiliza porque la parte más importante del sistema no es la API ni la base de datos, sino la lógica que valida y calcula el scoring.

El dominio se coloca en el centro para proteger:

- Las reglas de puntuación.
- Las reglas excluyentes.
- Los cálculos financieros.
- La validación de pesos.
- El rango del score.
- La reproducibilidad.
- La trazabilidad.

Si PostgreSQL se reemplaza por otra base de datos, el dominio no cambia.

Si la entrada REST se reemplaza por mensajería, el dominio no cambia.

Si Spring Data JPA se reemplaza por otra tecnología, los casos de negocio continúan funcionando.

Esto demuestra inversión de dependencias, bajo acoplamiento y separación de responsabilidades.

---

## 22. Diferencia con una arquitectura tradicional en capas

En una arquitectura tradicional es común que:

```text
Controller → Service → Repository → Database
```

y que las reglas terminen mezcladas en servicios dependientes del framework.

En Onion:

```text
Presentation → Application → Domain
Infrastructure → Interfaces del núcleo
```

El dominio no conoce la tecnología externa.

---

## 23. Principios SOLID relacionados

### Single Responsibility Principle

Cada clase tiene una responsabilidad concreta.

### Open/Closed Principle

El modelo puede incorporar nuevas reglas sin modificar los controladores.

### Dependency Inversion Principle

Las capas internas definen interfaces y la infraestructura las implementa.

Este último principio es el más relacionado directamente con Arquitectura Onion.

---

## 24. Resultado esperado

Al finalizar el proyecto se debe demostrar que:

- Se registran solicitudes de crédito.
- Se evita el registro duplicado.
- Se valida la información antes del scoring.
- Se obtiene una versión activa del modelo.
- Se calculan puntajes por factor.
- Se calcula un score total de 0 a 1000.
- Se guardan la evaluación y el detalle.
- El resultado conserva la versión utilizada.
- El dominio funciona sin depender de Spring ni PostgreSQL.
- Las dependencias apuntan hacia el centro.
- La API puede ejecutarse mediante Docker.
- Las reglas pueden probarse de manera aislada.

---

## 25. Resumen para exposición

El proyecto implementa un Motor de Scoring Crediticio utilizando Java 21, Spring Boot, Spring Data JPA y PostgreSQL. Su alcance se limita al registro de solicitudes, la validación de información y el cálculo del scoring.

Se exponen dos endpoints: uno para registrar la solicitud y otro para validarla y ejecutar la evaluación. La lógica de scoring se encuentra en el dominio, mientras que la capa de aplicación coordina los casos de uso. La infraestructura implementa la persistencia y la presentación expone la API REST.

La principal ventaja de Onion es que las reglas crediticias permanecen independientes del framework, la base de datos y la interfaz. De esta manera, el sistema es más mantenible, comprobable, reproducible y adaptable a cambios tecnológicos.
