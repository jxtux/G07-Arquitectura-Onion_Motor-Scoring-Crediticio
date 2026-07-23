package com.finanscore.motorscoring.presentation.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record SolicitanteRequest(
		@NotBlank 
		String tipoDocumento, 
		
		@NotBlank 
		String numeroDocumento,
		
		@NotBlank @Size(max = 150) 
		String nombresRazonSocial,
		
		@NotNull @DecimalMin("0.01") 
		BigDecimal ingresosMensuales,
		
		@NotNull @DecimalMin("0.00") 
		BigDecimal gastosMensuales,
		
		@NotNull @DecimalMin("0.00") 
		BigDecimal obligacionesFinancieras,
		
		@PositiveOrZero 
		int antiguedadLaboralNegocio,
		
		@PositiveOrZero 
		int numeroObligacionesActivas,
		
		@Min(0) @Max(100) 
		int puntajeHistorialPagos,
		
		@PositiveOrZero 
		int alertasMora
		
		) {
}
