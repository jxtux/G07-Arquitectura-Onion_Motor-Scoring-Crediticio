package com.finanscore.motorscoring.presentation.controller;
import com.finanscore.motorscoring.application.command.*;
import com.finanscore.motorscoring.application.usecase.*;
import com.finanscore.motorscoring.domain.enums.*;
import com.finanscore.motorscoring.presentation.request.CrearSolicitudCreditoRequest;
import com.finanscore.motorscoring.presentation.response.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
@RestController @RequestMapping("/api/solicitudes-credito")public class SolicitudCreditoController{
    private final CrearSolicitudCreditoUseCase crear;
    private final EjecutarEvaluacionScoringUseCase evaluar;
    public SolicitudCreditoController(CrearSolicitudCreditoUseCase c,EjecutarEvaluacionScoringUseCase e){
        crear=c;
        evaluar=e;
    }
    @PostMapping @Operation(summary="RF04 - Registrar solicitud de crédito")public ResponseEntity<SolicitudCreditoResponse> registrar(@Valid @RequestBody CrearSolicitudCreditoRequest r){
        var s=r.solicitante();
        var d=crear.ejecutar(new CrearSolicitudCreditoCommand(r.identificadorExterno(),TipoDocumento.valueOf(s.tipoDocumento().toUpperCase()),s.numeroDocumento(),s.nombresRazonSocial(),s.ingresosMensuales(),s.gastosMensuales(),s.obligacionesFinancieras(),s.antiguedadLaboralNegocio(),s.numeroObligacionesActivas(),s.puntajeHistorialPagos(),s.alertasMora(),r.codigoProducto(),r.montoSolicitado(),r.plazoSolicitado(),Moneda.valueOf(r.moneda().toUpperCase()),r.finalidadCredito(),r.canalOrigen()));
        var response=new SolicitudCreditoResponse(d.idSolicitud(),d.idSolicitante(),d.identificadorExterno(),d.codigoProducto(),d.montoSolicitado(),d.plazoSolicitado(),d.moneda(),d.estado(),d.fechaRegistro());
        return ResponseEntity.created(URI.create("/api/solicitudes-credito/"+d.idSolicitud())).body(response);
    }
    @PostMapping("/{id}/evaluar") @Operation(summary="RF05/RF06 - Validar información y calcular scoring")public EvaluacionScoringResponse evaluar(@PathVariable Long id){
        var d=evaluar.ejecutar(new EjecutarEvaluacionScoringCommand(id));
        var f=d.factores().stream().map(x->new ResultadoFactorResponse(x.factor(),x.valorEvaluado(),x.pesoAplicado(),x.puntajeBase(),x.puntajeObtenido(),x.reglaAplicada(),x.observacion(),x.excluyente())).toList();
        return new EvaluacionScoringResponse(d.idEvaluacion(),d.idSolicitud(),d.puntajeTotal(),d.resultado(),d.estado(),d.versionModelo(),d.fechaEvaluacion(),f);
    }
}
