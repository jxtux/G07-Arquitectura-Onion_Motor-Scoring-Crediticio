# Detalle de implementación

## RF04 — Registro de solicitud de crédito

**Endpoint**

```http
POST /api/solicitudes-credito
```

**Flujo**

1. `SolicitudCreditoController` recibe y valida el JSON.
2. Convierte el request en `CrearSolicitudCreditoCommand`.
3. `CrearSolicitudCreditoService` verifica idempotencia por `IdentificadorExterno`.
4. Obtiene el producto por código.
5. Construye Value Objects (`NumeroDocumento`, `Dinero`, `IdentificadorExterno`).
6. Registra o actualiza el perfil del `Solicitante`.
7. Crea `SolicitudCredito` en estado `REGISTRADA`.
8. Los puertos del dominio son implementados por adaptadores Spring Data JPA.
9. Devuelve `201 Created` y la ubicación del recurso.

## RF05 — Validación de información

Se ejecuta al solicitar la evaluación:

1. La solicitud debe existir y estar `REGISTRADA`.
2. El solicitante debe tener ingresos válidos y datos financieros completos.
3. El producto debe existir y estar activo.
4. La moneda debe coincidir con la del producto.
5. Monto y plazo deben estar dentro de los límites.
6. Debe existir un modelo con versión activa para la fecha actual.
7. Los pesos de factores activos deben sumar exactamente 100 %.
8. La solicitud no puede haber sido evaluada con la misma versión.

Las invariantes propias viven dentro de entidades y Value Objects. Las reglas que involucran varios agregados se ejecutan en entidades/servicios del dominio. La capa de aplicación solo orquesta.

## RF06 — Cálculo del scoring

`CalculadorScoring` pertenece al dominio y no utiliza frameworks.

Factores implementados:

1. Historial de pagos.
2. Relación deuda-ingreso.
3. Capacidad de pago disponible.
4. Estabilidad de ingresos.
5. Antigüedad laboral o del negocio.
6. Número de obligaciones activas.
7. Monto solicitado frente a capacidad estimada.
8. Alertas de mora como regla excluyente.

Cada factor obtiene una regla aplicable, calcula un aporte ponderado y genera un `ResultadoFactor`. El puntaje queda limitado por el Value Object `PuntajeCrediticio` al rango 0–1000.

Clasificación inicial:

- 750–1000: `PREAPROBADA`.
- 600–749: `REVISION_MANUAL`.
- 0–599: `RECHAZADA`.

Una regla excluyente puede forzar `RECHAZADA` aunque el puntaje ponderado sea alto.

## Persistencia

Se guardan en una misma transacción:

- Cabecera de evaluación.
- Resultado final.
- Resultado de los ocho factores.
- Versión del modelo utilizada.
- Cambio de estado de la solicitud a `EVALUADA`.

## Manejo de errores

- `400`: formato o validación HTTP.
- `404`: solicitud, producto o modelo inexistente.
- `409`: identificador externo o evaluación duplicada.
- `422`: regla de dominio incumplida.
- `500`: error no controlado.

## Pruebas

- **Unitarias:** Value Objects, entidad `SolicitudCredito`, cálculo y regla excluyente.
- **Aplicación:** idempotencia con Mockito.
- **Integración:** Spring + Flyway + H2 + JPA + casos de uso.
- **E2E:** llamadas HTTP reales en puerto aleatorio.
- **Arquitectura:** ArchUnit impide dependencias desde el núcleo hacia capas externas.
