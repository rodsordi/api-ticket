package br.com.cielo.ticket.domain.repository;

import java.util.UUID;

public interface S3StoragePort {
    String uploadInvoicePdf(UUID reservationId, byte[] pdfBytes);
}
