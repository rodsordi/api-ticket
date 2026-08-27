package br.com.cielo.ticket.domain.repository;

import java.util.UUID;

public interface S3StorageRepository {
    String uploadInvoicePdf(UUID reservationId, byte[] pdfContent);
}
