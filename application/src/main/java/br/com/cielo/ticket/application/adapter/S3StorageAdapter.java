package br.com.cielo.ticket.application.adapter;

import br.com.cielo.commons.exception.InternalErrorException;
import br.com.cielo.ticket.domain.port.S3StoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements S3StoragePort {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.invoices:cielo-invoices-bucket}")
    private String bucketName;

    @Override
    public String uploadInvoicePdf(UUID reservationId, byte[] pdfBytes) {
        if (reservationId == null) {
            throw new InternalErrorException("Invalid reservationId for S3 upload");
        }

        String key = String.format("invoices/invoice-%s.pdf", reservationId);
        int bytesCount = pdfBytes != null ? pdfBytes.length : 0;
        log.info("Uploading invoice PDF to S3 bucket {} with key {} ({} bytes)", bucketName, key, bytesCount);

        try {
            if (pdfBytes != null && pdfBytes.length > 0) {
                var putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("application/pdf")
                        .build();
                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(pdfBytes));
            }
            return String.format("https://s3.amazonaws.com/%s/%s", bucketName, key);
        } catch (Exception e) {
            log.warn("Could not upload invoice PDF for reservation {} to S3 bucket {}: {}", reservationId, bucketName, e.getMessage());
            return String.format("https://s3.amazonaws.com/%s/%s", bucketName, key);
        }
    }
}
