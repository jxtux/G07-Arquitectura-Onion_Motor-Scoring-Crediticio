-- ============================================================
-- V3__agregar_factor_relacion_cuota_ingreso.sql
--
-- Crea la versión 1.1.0 del modelo de scoring.
-- Incorpora únicamente el factor RELACION_CUOTA_INGRESO.
-- ============================================================

-- ------------------------------------------------------------
-- 1. Desactivar la versión actualmente activa
-- ------------------------------------------------------------

UPDATE versiones_modelo
SET activo = FALSE
WHERE activo = TRUE;


-- ------------------------------------------------------------
-- 2. Crear la versión 1.1.0
-- ------------------------------------------------------------

INSERT INTO versiones_modelo (
    codigo_version,
    descripcion,
    activo,
    fecha_creacion
)
VALUES (
    '1.1.0',
    'Modelo de scoring con relación cuota ingreso',
    TRUE,
    CURRENT_TIMESTAMP
);


-- ------------------------------------------------------------
-- 3. Copiar los factores de la versión 1.0.0
-- ------------------------------------------------------------

INSERT INTO factores_scoring (
    version_modelo_id,
    codigo,
    nombre,
    descripcion,
    peso,
    orden,
    activo
)
SELECT
    nueva_version.id,
    factor.codigo,
    factor.nombre,
    factor.descripcion,

    CASE factor.codigo

        WHEN 'HISTORIAL_PAGOS'
            THEN 22.0000

        WHEN 'RELACION_DEUDA_INGRESO'
            THEN 18.0000

        WHEN 'CAPACIDAD_PAGO'
            THEN 18.0000

        WHEN 'ESTABILIDAD_INGRESOS'
            THEN 14.0000

        WHEN 'ANTIGUEDAD_LABORAL'
            THEN 10.0000

        WHEN 'OBLIGACIONES_ACTIVAS'
            THEN 4.0000

        WHEN 'MONTO_CAPACIDAD'
            THEN 4.0000

        WHEN 'ALERTAS_MORA'
            THEN 0.0000

        ELSE factor.peso

    END AS peso,

    factor.orden,
    factor.activo

FROM factores_scoring factor

INNER JOIN versiones_modelo version_anterior
    ON version_anterior.id = factor.version_modelo_id

CROSS JOIN versiones_modelo nueva_version

WHERE version_anterior.codigo_version = '1.0.0'
  AND nueva_version.codigo_version = '1.1.0';


-- ------------------------------------------------------------
-- 4. Copiar las reglas de los factores existentes
-- ------------------------------------------------------------

INSERT INTO reglas_evaluacion (
    factor_scoring_id,
    codigo,
    descripcion,
    valor_minimo,
    valor_maximo,
    puntaje,
    excluyente,
    motivo_exclusion,
    orden,
    activo
)
SELECT
    factor_nuevo.id,
    regla.codigo,
    regla.descripcion,
    regla.valor_minimo,
    regla.valor_maximo,
    regla.puntaje,
    regla.excluyente,
    regla.motivo_exclusion,
    regla.orden,
    regla.activo

FROM reglas_evaluacion regla

INNER JOIN factores_scoring factor_anterior
    ON factor_anterior.id = regla.factor_scoring_id

INNER JOIN versiones_modelo version_anterior
    ON version_anterior.id = factor_anterior.version_modelo_id

INNER JOIN factores_scoring factor_nuevo
    ON factor_nuevo.codigo = factor_anterior.codigo

INNER JOIN versiones_modelo version_nueva
    ON version_nueva.id = factor_nuevo.version_modelo_id

WHERE version_anterior.codigo_version = '1.0.0'
  AND version_nueva.codigo_version = '1.1.0';


-- ------------------------------------------------------------
-- 5. Agregar RELACION_CUOTA_INGRESO
-- ------------------------------------------------------------

INSERT INTO factores_scoring (
    version_modelo_id,
    codigo,
    nombre,
    descripcion,
    peso,
    orden,
    activo
)
SELECT
    id,
    'RELACION_CUOTA_INGRESO',
    'Relación cuota ingreso',
    'Porcentaje que representa la cuota mensual del crédito respecto de los ingresos mensuales del solicitante',
    10.0000,
    9,
    TRUE
FROM versiones_modelo
WHERE codigo_version = '1.1.0';


-- ------------------------------------------------------------
-- 6. Agregar reglas de RELACION_CUOTA_INGRESO
--
-- Fórmula:
-- cuota = monto solicitado / plazo
-- porcentaje = cuota / ingresos mensuales * 100
-- ------------------------------------------------------------

INSERT INTO reglas_evaluacion (
    factor_scoring_id,
    codigo,
    descripcion,
    valor_minimo,
    valor_maximo,
    puntaje,
    excluyente,
    motivo_exclusion,
    orden,
    activo
)
SELECT
    factor.id,
    regla.codigo,
    regla.descripcion,
    regla.valor_minimo,
    regla.valor_maximo,
    regla.puntaje,
    regla.excluyente,
    regla.motivo_exclusion,
    regla.orden,
    TRUE
FROM factores_scoring factor

INNER JOIN versiones_modelo version_modelo
    ON version_modelo.id = factor.version_modelo_id

CROSS JOIN (
    SELECT
        'RCI_BAJA' AS codigo,
        'La cuota representa como máximo el 20% de los ingresos' AS descripcion,
        0.0000 AS valor_minimo,
        20.0000 AS valor_maximo,
        100 AS puntaje,
        FALSE AS excluyente,
        CAST(NULL AS VARCHAR(255)) AS motivo_exclusion,
        1 AS orden

    UNION ALL

    SELECT
        'RCI_MODERADA',
        'La cuota representa más del 20% y hasta el 30% de los ingresos',
        20.0001,
        30.0000,
        75,
        FALSE,
        CAST(NULL AS VARCHAR(255)),
        2

    UNION ALL

    SELECT
        'RCI_ALTA',
        'La cuota representa más del 30% y hasta el 40% de los ingresos',
        30.0001,
        40.0000,
        40,
        FALSE,
        CAST(NULL AS VARCHAR(255)),
        3

    UNION ALL

    SELECT
        'RCI_MUY_ALTA',
        'La cuota representa más del 40% de los ingresos',
        40.0001,
        CAST(NULL AS DECIMAL(19,4)),
        0,
        FALSE,
        CAST(NULL AS VARCHAR(255)),
        4
) regla

WHERE version_modelo.codigo_version = '1.1.0'
  AND factor.codigo = 'RELACION_CUOTA_INGRESO';