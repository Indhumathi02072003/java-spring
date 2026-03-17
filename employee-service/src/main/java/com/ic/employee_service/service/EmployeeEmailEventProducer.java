package com.ic.employee_service.service;

import com.ic.notification.contract.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeeEmailEventProducer {

    private static final String TOPIC = "notifications.email";

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public EmployeeEmailEventProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmailEvent(NotificationEvent event) {
        kafkaTemplate.send(TOPIC,  event.getMetadata().getEventId(), event);
        
        log.info("📧 Email notification event sent. eventId={}",
                event.getMetadata().getEventId());

    }

}

