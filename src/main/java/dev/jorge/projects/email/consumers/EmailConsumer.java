package dev.jorge.projects.email.consumers;

import dev.jorge.projects.email.dtos.requests.EmailRequest;
import dev.jorge.projects.email.entities.Email;
import dev.jorge.projects.email.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private final EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${broker.queue.email.name}")
    public void listenEmailQueue(@Payload EmailRequest request) {
        Email email = new Email();
        BeanUtils.copyProperties(request, email);
        emailService.sendEmail(email);
    }
}
