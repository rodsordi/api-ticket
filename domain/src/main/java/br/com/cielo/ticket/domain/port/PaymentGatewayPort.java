package br.com.cielo.ticket.domain.port;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentGatewayPort {
    byte[] generateInvoicePdf(UUID reservationId, UUID clientId, BigDecimal price);
}
