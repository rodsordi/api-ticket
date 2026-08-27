package br.com.cielo.ticket.infra.publisher;

import br.com.cielo.ticket.domain.entity.Client;
import br.com.cielo.ticket.domain.entity.Reservation;
import br.com.cielo.ticket.infra.mapper.NotificationEvtMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotifyCustomerForApprovalPublisherImpl {

    private final NotificationEvtMapper mapper;
    private final NotificationKafkaPublisher kafkaPublisher;

    public void notify(Reservation reservation, Client client) {
        var notificationEvt = mapper.convert(reservation, client);
        log.info("Sending notification event for reservation {}", reservation.getId());
        kafkaPublisher.sendNotification(notificationEvt);
    }
}
