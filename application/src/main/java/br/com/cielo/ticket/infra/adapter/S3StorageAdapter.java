package br.com.cielo.ticket.infra.adapter;

import br.com.cielo.ticket.domain.port.S3StoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class S3StorageAdapter implements S3StoragePort {

    @Value("${aws.s3.bucket.invoices:cielo-invoices-bucket}")
    private String bucketName;

    @Override
    public String uploadInvoicePdf(UUID reservationId, byte[] pdfBytes) {
        log.info("Uploading invoice PDF to S3 bucket {} for reservation {} ({} bytes)", bucketName, reservationId, pdfBytes != null ? pdfBytes.length : 0);
        return String.format("https://s3.amazonaws.com/%s/invoices/invoice-%s.pdf", bucketName, reservationId);
    }
}
