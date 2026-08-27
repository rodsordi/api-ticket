package br.com.cielo.ticket.application.adapter;

import br.com.cielo.ticket.domain.port.PaymentGatewayPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class PaymentGatewayAdapter implements PaymentGatewayPort {

    @Override
    public byte[] generateInvoicePdf(UUID reservationId, UUID clientId, BigDecimal price) {
        log.info("Generating invoice PDF via Payment Gateway for reservation {} (clientId: {}, price: {})", reservationId, clientId, price);
        var mockPdfContent = String.format("INVOICE PDF FOR RESERVATION %s - CLIENT %s - PRICE: R$ %s", reservationId, clientId, price);
        return mockPdfContent.getBytes(StandardCharsets.UTF_8);
    }
}
