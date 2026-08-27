package br.com.cielo.ticket.domain.port;

import java.util.UUID;

public interface S3StoragePort {
    String uploadInvoicePdf(UUID reservationId, byte[] pdfBytes);
}
