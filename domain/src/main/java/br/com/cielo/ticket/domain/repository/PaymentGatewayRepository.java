package br.com.cielo.ticket.domain.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentGatewayRepository {
    byte[] generateInvoicePdf(UUID reservationId, UUID clientId, BigDecimal amount);
}
