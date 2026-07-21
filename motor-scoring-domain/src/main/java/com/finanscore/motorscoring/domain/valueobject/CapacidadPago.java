package com.finanscore.motorscoring.domain.valueobject;

import com.finanscore.motorscoring.domain.enums.Moneda;
import java.math.BigDecimal;


public record CapacidadPago(Dinero disponible) {
	
	public static CapacidadPago calcular(BigDecimal ingresos, BigDecimal gastos, BigDecimal obligaciones, Moneda moneda) {
		BigDecimal v = ingresos.subtract(gastos).subtract(obligaciones);
		
		if (v.signum() < 0)
			v = BigDecimal.ZERO;
		
		return new CapacidadPago(new Dinero(v, moneda));
	}
}
