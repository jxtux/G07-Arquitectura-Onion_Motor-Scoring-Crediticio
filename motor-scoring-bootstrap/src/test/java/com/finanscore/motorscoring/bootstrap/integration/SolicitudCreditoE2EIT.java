package com.finanscore.motorscoring.bootstrap.integration;
import com.finanscore.motorscoring.bootstrap.MotorScoringApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest(
        classes = MotorScoringApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class SolicitudCreditoE2EIT {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Test
    void debeEjecutarRf04Rf05Rf06PorHttp() {
        String externalId = "E2E-" + System.nanoTime();
        String documento = String.valueOf(10_000_000L + Math.floorMod(System.nanoTime(), 89_999_999L));
        Map<String, Object> solicitante = new LinkedHashMap<>();
        solicitante.put("tipoDocumento", "DNI");
        solicitante.put("numeroDocumento", documento);
        solicitante.put("nombresRazonSocial", "Prueba E2E");
        solicitante.put("ingresosMensuales", 6500);
        solicitante.put("gastosMensuales", 1800);
        solicitante.put("obligacionesFinancieras", 700);
        solicitante.put("antiguedadLaboralNegocio", 48);
        solicitante.put("numeroObligacionesActivas", 2);
        solicitante.put("puntajeHistorialPagos", 92);
        solicitante.put("alertasMora", 0);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("identificadorExterno", externalId);
        body.put("solicitante", solicitante);
        body.put("codigoProducto", "PRESTAMO_PERSONAL");
        body.put("montoSolicitado", 18000);
        body.put("plazoSolicitado", 24);
        body.put("moneda", "PEN");
        body.put("finalidadCredito", "Consumo");
        body.put("canalOrigen", "E2E_TEST");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var registro = restTemplate.exchange(
                "http://localhost:" + port + "/api/solicitudes-credito",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );
        assertEquals(HttpStatus.CREATED, registro.getStatusCode());
        assertNotNull(registro.getBody());
        Number idSolicitud = (Number) registro.getBody().get("idSolicitud");
        assertNotNull(idSolicitud);
        var evaluacion = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/solicitudes-credito/" + idSolicitud.longValue() + "/evaluar",
                null,
                Map.class
        );
        assertEquals(HttpStatus.OK, evaluacion.getStatusCode());
        assertNotNull(evaluacion.getBody());
        Number puntaje = (Number) evaluacion.getBody().get("puntajeTotal");
        assertTrue(puntaje.intValue() >= 0 && puntaje.intValue() <= 1000);
        assertEquals("1.0.0", evaluacion.getBody().get("versionModelo"));
    }
}
