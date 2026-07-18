package com.finanscore.motorscoring.presentation.exception;
import com.finanscore.motorscoring.application.exception.*;
import com.finanscore.motorscoring.domain.exception.DomainException;
import com.finanscore.motorscoring.presentation.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.*;
@RestControllerAdvice public class GlobalExceptionHandler{
    @ExceptionHandler(MethodArgumentNotValidException.class)ResponseEntity<ErrorResponse> bean(MethodArgumentNotValidException ex,HttpServletRequest req){
        Map<String,String> v=new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e->v.putIfAbsent(e.getField(),e.getDefaultMessage()));
        return build(400,"INVALID_REQUEST","La solicitud contiene campos inválidos.",req,v);
    }
    @ExceptionHandler({
        IllegalArgumentException.class,HttpMessageNotReadableException.class}
        )ResponseEntity<ErrorResponse> format(Exception ex,HttpServletRequest req){
            return build(400,"INVALID_FORMAT","Formato de solicitud inválido.",req,Map.of());
        }
        @ExceptionHandler(RecursoNoEncontradoException.class)ResponseEntity<ErrorResponse> nf(Exception ex,HttpServletRequest req){
            return build(404,"RESOURCE_NOT_FOUND",ex.getMessage(),req,Map.of());
        }
        @ExceptionHandler(SolicitudDuplicadaException.class)ResponseEntity<ErrorResponse> dup(Exception ex,HttpServletRequest req){
            return build(409,"DUPLICATE_APPLICATION",ex.getMessage(),req,Map.of());
        }
        @ExceptionHandler(DomainException.class)ResponseEntity<ErrorResponse> dom(Exception ex,HttpServletRequest req){
            return build(422,"BUSINESS_VALIDATION_ERROR",ex.getMessage(),req,Map.of());
        }
        @ExceptionHandler(Exception.class)ResponseEntity<ErrorResponse> gen(Exception ex,HttpServletRequest req){
            return build(500,"INTERNAL_ERROR","Ocurrió un error interno.",req,Map.of());
        }
        private ResponseEntity<ErrorResponse> build(int status,String code,String msg,HttpServletRequest req,Map<String,String> v){
            return ResponseEntity.status(status).body(new ErrorResponse(OffsetDateTime.now(),status,code,msg,req.getRequestURI(),v));
        }
    }
