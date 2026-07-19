package com.finanscore.motorscoring.presentation.response;

import java.math.BigDecimal;

public record ResultadoFactorResponse(String factor, BigDecimal valorEvaluado, BigDecimal pesoAplicado, int puntajeBase, int puntajeObtenido, String reglaAplicada, String observacion, boolean excluyente) {
}
