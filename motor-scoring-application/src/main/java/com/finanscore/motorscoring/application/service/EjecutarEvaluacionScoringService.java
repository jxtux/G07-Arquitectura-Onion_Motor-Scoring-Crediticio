package com.finanscore.motorscoring.application.service;

import com.finanscore.motorscoring.application.command.EjecutarEvaluacionScoringCommand;
import com.finanscore.motorscoring.application.dto.*;
import com.finanscore.motorscoring.application.exception.*;
import com.finanscore.motorscoring.application.usecase.EjecutarEvaluacionScoringUseCase;
import com.finanscore.motorscoring.domain.entity.*;
import com.finanscore.motorscoring.domain.exception.SolicitudNoEvaluableException;
import com.finanscore.motorscoring.domain.repository.*;
import com.finanscore.motorscoring.domain.service.CalculadorScoring;
import java.time.*;

public final class EjecutarEvaluacionScoringService implements EjecutarEvaluacionScoringUseCase {
	private final SolicitudCreditoRepository solicitudes;
	private final SolicitanteRepository solicitantes;
	private final ProductoCrediticioRepository productos;
	private final ModeloScoringRepository modelos;
	private final EvaluacionCrediticiaRepository evaluaciones;
	private final CalculadorScoring calculador;
	private final Clock clock;

	public EjecutarEvaluacionScoringService(SolicitudCreditoRepository q, SolicitanteRepository s,
			ProductoCrediticioRepository p, ModeloScoringRepository m, EvaluacionCrediticiaRepository e,
			CalculadorScoring c, Clock clock) {
		solicitudes = q;
		solicitantes = s;
		productos = p;
		modelos = m;
		evaluaciones = e;
		calculador = c;
		this.clock = clock;
	}

	public EvaluacionScoringDto ejecutar(EjecutarEvaluacionScoringCommand cmd) {
		SolicitudCredito solicitud = solicitudes.buscarPorId(cmd.idSolicitud())
				.orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada: " + cmd.idSolicitud()));
		if (!solicitud.estaRegistrada())
			throw new SolicitudNoEvaluableException("La solicitud ya fue evaluada o no está registrada.");
		Solicitante solicitante = solicitantes.buscarPorId(solicitud.idSolicitante())
				.orElseThrow(() -> new RecursoNoEncontradoException("Solicitante no encontrado."));
		if (!solicitante.tieneDatosFinancierosCompletos() || !solicitante.tieneIngresosValidos())
			throw new SolicitudNoEvaluableException("Información financiera incompleta o inválida.");
		ProductoCrediticio producto = productos.buscarPorId(solicitud.idProducto())
				.orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado."));
		producto.validarSolicitud(solicitud.montoSolicitado(), solicitud.plazoSolicitado());
		LocalDateTime ahora = LocalDateTime.now(clock);
		ModeloScoring modelo = modelos.buscarCompletoPorId(producto.idModeloScoring())
				.orElseThrow(() -> new RecursoNoEncontradoException("Modelo no encontrado."));
		VersionModelo version = modelo.versionActiva(ahora.toLocalDate());
		version.validarPesos();
		if (evaluaciones.existePorSolicitudYVersion(solicitud.id(), version.id()))
			throw new SolicitudDuplicadaException("La solicitud ya fue evaluada con la versión activa.");
		EvaluacionCrediticia evaluacion = evaluaciones
				.guardar(calculador.calcular(solicitud, solicitante, version, ahora));
		solicitud.marcarEvaluada();
		solicitudes.guardar(solicitud);
		var factores = evaluacion.resultadosFactor().stream()
				.map(r -> new ResultadoFactorDto(r.codigoFactor(), r.valorEvaluado(), r.pesoAplicado(), r.puntajeBase(),
						r.puntajeObtenido(), r.reglaAplicada(), r.observacion(), r.reglaExcluyente()))
				.toList();
		return new EvaluacionScoringDto(evaluacion.id(), evaluacion.idSolicitud(), evaluacion.puntajeTotal().valor(),
				evaluacion.resultado().name(), evaluacion.estado().name(), version.numeroVersion(),
				evaluacion.fechaEvaluacion(), factores);
	}
}
