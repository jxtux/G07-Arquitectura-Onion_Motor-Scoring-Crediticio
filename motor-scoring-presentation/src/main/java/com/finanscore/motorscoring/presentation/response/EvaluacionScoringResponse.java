package com.finanscore.motorscoring.presentation.response;
import java.time.LocalDateTime;
import java.util.List;
public record EvaluacionScoringResponse(Long idEvaluacion,Long idSolicitud,int puntajeTotal,String resultado,String estado,String versionModelo,LocalDateTime fechaEvaluacion,List<ResultadoFactorResponse> factores){
}
