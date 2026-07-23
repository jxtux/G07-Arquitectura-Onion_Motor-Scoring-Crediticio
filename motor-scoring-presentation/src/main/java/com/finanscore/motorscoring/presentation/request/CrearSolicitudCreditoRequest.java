package com.finanscore.motorscoring.presentation.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public record CrearSolicitudCreditoRequest(
		
		@NotBlank @Size(max = 100) 
		String identificadorExterno,
		
		@NotNull @Valid 
		SolicitanteRequest solicitante, 
		
		@NotBlank @Size(max = 30) 
		String codigoProducto,
		
		@NotNull @DecimalMin("0.01") 
		BigDecimal montoSolicitado, 
		
		@Positive 
		int plazoSolicitado, 
		
		@NotBlank 
		String moneda,
		
		@NotBlank @Size(max = 150) 
		String finalidadCredito, 
		
		@NotBlank @Size(max = 50) 
		String canalOrigen
		
		) {
}
