package com.finanscore.motorscoring.presentation.response;
import java.time.OffsetDateTime;
import java.util.Map;
public record ErrorResponse(OffsetDateTime timestamp,int status,String code,String message,String path,Map<String,String> violations){
}
