package com.finanscore.motorscoring.bootstrap;

import com.finanscore.motorscoring.application.service.*;
import com.finanscore.motorscoring.application.usecase.*;
import com.finanscore.motorscoring.domain.repository.*;
import com.finanscore.motorscoring.domain.service.*;
import com.finanscore.motorscoring.infrastructure.transaction.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.Clock;


@Configuration
public class OnionBeanConfiguration {
	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}

	@Bean
	CalculadorCapacidadPago capacidad() {
		return new CalculadorCapacidadPago();
	}

	@Bean
	CalculadorRelacionDeudaIngreso relacion() {
		return new CalculadorRelacionDeudaIngreso();
	}

	@Bean
	CalculadorRelacionCuotaIngreso relacionCuotaIngreso() {
		return new CalculadorRelacionCuotaIngreso();
	}

	@Bean
	EvaluadorReglasExcluyentes excluyentes() {
		return new EvaluadorReglasExcluyentes();
	}

	@Bean
	CalculadorScoring calculador(CalculadorCapacidadPago c, CalculadorRelacionDeudaIngreso r,
		    CalculadorRelacionCuotaIngreso rci,
			EvaluadorReglasExcluyentes e) {
		return new CalculadorScoring(c, r,rci, e);
	}

	@Bean(name = "crearCore")
	CrearSolicitudCreditoUseCase crearCore(SolicitanteRepository s, SolicitudCreditoRepository q, ProductoCrediticioRepository p, Clock c) {
		return new CrearSolicitudCreditoService(s, q, p, c);
	}

	@Bean(name = "evaluarCore")
	EjecutarEvaluacionScoringUseCase evaluarCore(SolicitudCreditoRepository q, SolicitanteRepository s, ProductoCrediticioRepository p, ModeloScoringRepository m, EvaluacionCrediticiaRepository e, CalculadorScoring c, Clock clock) {
		return new EjecutarEvaluacionScoringService(q, s, p, m, e, c, clock);
	}

	@Bean
	@Primary
	CrearSolicitudCreditoUseCase crear(@Qualifier("crearCore") CrearSolicitudCreditoUseCase core, PlatformTransactionManager tm) {
		return new TransactionalCrearSolicitudCreditoUseCase(core, new TransactionTemplate(tm));
	}

	@Bean
	@Primary
	EjecutarEvaluacionScoringUseCase evaluar(@Qualifier("evaluarCore") EjecutarEvaluacionScoringUseCase core, PlatformTransactionManager tm) {
		return new TransactionalEjecutarEvaluacionScoringUseCase(core, new TransactionTemplate(tm));
	}
}
