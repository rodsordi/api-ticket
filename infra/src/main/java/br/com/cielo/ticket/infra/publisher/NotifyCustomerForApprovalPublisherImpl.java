package br.com.cielo.ticket.infra.publisher;

import br.com.cielo.ticket.domain.entity.WorkOrder;
import br.com.cielo.ticket.domain.publisher.NotifyCustomerForApprovalPublisher;
import br.com.cielo.ticket.infra.mapper.NotificationEvtMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotifyCustomerForApprovalPublisherImpl implements NotifyCustomerForApprovalPublisher {

    private final NotificationEvtMapper mapper;

    private final SnsTemplate snsTemplate;

    @Value("${message.notification-creation.topic}")
    private String queueName;

    @Override
    public void notify(WorkOrder workOrder) {
        var notificationEvt = mapper.convert(workOrder);
        snsTemplate.convertAndSend(queueName, notificationEvt);
    }
}
