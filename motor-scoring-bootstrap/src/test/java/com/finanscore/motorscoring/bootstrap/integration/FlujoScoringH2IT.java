package com.finanscore.motorscoring.bootstrap.integration;
import com.finanscore.motorscoring.application.command.CrearSolicitudCreditoCommand;
import com.finanscore.motorscoring.application.command.EjecutarEvaluacionScoringCommand;
import com.finanscore.motorscoring.application.usecase.CrearSolicitudCreditoUseCase;
import com.finanscore.motorscoring.application.usecase.EjecutarEvaluacionScoringUseCase;
import com.finanscore.motorscoring.bootstrap.MotorScoringApplication;
import com.finanscore.motorscoring.domain.enums.Moneda;
import com.finanscore.motorscoring.domain.enums.TipoDocumento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest(classes = MotorScoringApplication.class)
@ActiveProfiles("test")
class FlujoScoringH2IT {
    @Autowired
    private CrearSolicitudCreditoUseCase crearSolicitud;
    @Autowired
    private EjecutarEvaluacionScoringUseCase evaluarSolicitud;
    @Test
    void debeMigrarH2RegistrarValidarCalcularYPersistir() {
        String externalId = "IT-" + System.nanoTime();
        String documento = String.valueOf(10_000_000L + Math.floorMod(System.nanoTime(), 89_999_999L));
        var registrada = crearSolicitud.ejecutar(new CrearSolicitudCreditoCommand(
                externalId,
                TipoDocumento.DNI,
                documento,
                "Integración H2",
                new BigDecimal("6000"),
                new BigDecimal("1500"),
                new BigDecimal("800"),
                48,
                2,
                90,
                0,
                "PRESTAMO_PERSONAL",
                new BigDecimal("15000"),
                24,
                Moneda.PEN,
                "Consumo",
                "INTEGRATION_TEST"
        ));
        var evaluada = evaluarSolicitud.ejecutar(new EjecutarEvaluacionScoringCommand(registrada.idSolicitud()));
        assertNotNull(registrada.idSolicitud());
        assertNotNull(evaluada.idEvaluacion());
        assertEquals("REGISTRADA", registrada.estado());
        assertTrue(evaluada.puntajeTotal() >= 0 && evaluada.puntajeTotal() <= 1000);
        assertEquals(8, evaluada.factores().size());
        assertEquals("1.0.0", evaluada.versionModelo());
    }
}
