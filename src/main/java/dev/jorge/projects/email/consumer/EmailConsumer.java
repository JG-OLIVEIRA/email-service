package dev.jorge.projects.email.consumer;

import dev.jorge.projects.email.dto.EmailRequest;
import dev.jorge.projects.email.model.Email;
import dev.jorge.projects.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${broker.queue.email.name}")
    public void listenEmailQueue(@Payload EmailRequest request) {
        Email email = new Email();
        BeanUtils.copyProperties(request, email);
        emailService.sendEmail(email);
    }
}
