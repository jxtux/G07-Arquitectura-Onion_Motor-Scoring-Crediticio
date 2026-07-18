package com.finanscore.motorscoring.presentation.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record SolicitudCreditoResponse(Long idSolicitud,Long idSolicitante,String identificadorExterno,String codigoProducto,BigDecimal montoSolicitado,int plazoSolicitado,String moneda,String estado,LocalDateTime fechaRegistro){
}
