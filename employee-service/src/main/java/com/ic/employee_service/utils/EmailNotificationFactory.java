package com.ic.employee_service.utils;

import com.ic.notification.contract.*;

import java.time.Instant;
import java.util.UUID;

public class EmailNotificationFactory {

    private static final String SOURCE_SERVICE = "employee-service";

    //  STATIC EMAIL FOR NOW
    private static final String STATIC_EMAIL = "jeganlingam1804@gmail.com";

    //  Employee Created Notification
    public static NotificationEvent employeeCreated(
                                                        String html) {
        return createEmailNotification(
                STATIC_EMAIL,
                "Employee Created Successfully",
                        html,
                true
        );
    }

    // 🔒 Reused common creator (DO NOT CHANGE)
    private static NotificationEvent createEmailNotification(
            String email,
            String subject,
            String message,
            boolean isHtml
    ) {
        Recipient recipient = new Recipient();
        recipient.setEmail(email);

        Content content = new Content();
        content.setSubject(subject);
        content.setMessage(message);
        content.setHtml(isHtml);

        Metadata metadata = new Metadata();
        metadata.setEventId(UUID.randomUUID().toString());
        metadata.setSourceService(SOURCE_SERVICE);
        metadata.setCreatedAt(Instant.now());

        NotificationEvent event = new NotificationEvent();
        event.setType(NotificationType.EMAIL);
        event.setRecipient(recipient);
        event.setContent(content);
        event.setMetadata(metadata);

        return event;
    }
}
