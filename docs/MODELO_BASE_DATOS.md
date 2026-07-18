# Modelo de base de datos

El modelo está alineado con el diagrama entregado y se amplía con tres atributos necesarios para RF06 en `solicitantes`: `puntaje_historial_pagos`, `alertas_mora` y `moneda`.

## Tablas

1. `solicitantes`: perfil financiero y comportamiento del solicitante.
2. `productos_crediticios`: límites de monto/plazo y modelo asociado.
3. `solicitudes_credito`: solicitud registrada por RF04.
4. `modelos_scoring`: definición lógica del modelo.
5. `versiones_modelo`: versión vigente usada para reproducibilidad.
6. `factores_scoring`: factores y pesos.
7. `reglas_evaluacion`: rangos y puntuaciones, incluidas reglas excluyentes.
8. `evaluaciones_crediticias`: cabecera del cálculo RF05/RF06.
9. `resultados_factor`: resultado auditable de cada factor.
10. `resultados_scoring`: resultado final de la evaluación.

## Relaciones principales

```text
solicitantes 1 ── * solicitudes_credito * ── 1 productos_crediticios
productos_crediticios * ── 1 modelos_scoring
modelos_scoring 1 ── * versiones_modelo
versiones_modelo 1 ── * factores_scoring
factores_scoring 1 ── * reglas_evaluacion
solicitudes_credito 1 ── * evaluaciones_crediticias
versiones_modelo 1 ── * evaluaciones_crediticias
evaluaciones_crediticias 1 ── * resultados_factor
evaluaciones_crediticias 1 ── 1 resultados_scoring
```

El script definitivo está en:

- `motor-scoring-bootstrap/src/main/resources/db/migration/V1__crear_modelo_datos.sql`
- `motor-scoring-bootstrap/src/main/resources/db/migration/V2__insertar_modelo_scoring_inicial.sql`
