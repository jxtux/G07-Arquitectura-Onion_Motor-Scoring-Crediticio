package com.finanscore.motorscoring.application.service;
import com.finanscore.motorscoring.application.command.CrearSolicitudCreditoCommand;
import com.finanscore.motorscoring.application.exception.SolicitudDuplicadaException;
import com.finanscore.motorscoring.domain.enums.Moneda;
import com.finanscore.motorscoring.domain.enums.TipoDocumento;
import com.finanscore.motorscoring.domain.repository.ProductoCrediticioRepository;
import com.finanscore.motorscoring.domain.repository.SolicitanteRepository;
import com.finanscore.motorscoring.domain.repository.SolicitudCreditoRepository;
import com.finanscore.motorscoring.domain.valueobject.IdentificadorExterno;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Clock;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class CrearSolicitudCreditoServiceTest {
    @Mock
    private SolicitanteRepository solicitantes;
    @Mock
    private SolicitudCreditoRepository solicitudes;
    @Mock
    private ProductoCrediticioRepository productos;
    @Test
    void debeRechazarIdentificadorExternoDuplicado() {
        when(solicitudes.existePorIdentificadorExterno(any(IdentificadorExterno.class))).thenReturn(true);
        CrearSolicitudCreditoService service = new CrearSolicitudCreditoService(
                solicitantes,
                solicitudes,
                productos,
                Clock.systemUTC()
        );
        CrearSolicitudCreditoCommand command = new CrearSolicitudCreditoCommand(
                "EXT-DUPLICADO",
                TipoDocumento.DNI,
                "12345678",
                "Persona",
                new BigDecimal("5000"),
                new BigDecimal("1000"),
                new BigDecimal("500"),
                24,
                1,
                90,
                0,
                "PRESTAMO_PERSONAL",
                new BigDecimal("10000"),
                12,
                Moneda.PEN,
                "Consumo",
                "API"
        );
        assertThrows(SolicitudDuplicadaException.class, () -> service.ejecutar(command));
    }
}
