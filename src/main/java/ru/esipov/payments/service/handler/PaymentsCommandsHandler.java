package ru.esipov.payments.service.handler;

import ru.job4j.core.command.ProcessPaymentCommand;
import ru.job4j.core.events.PaymentFailedEvent;
import ru.job4j.core.events.PaymentProcessedEvent;
import ru.job4j.core.exceptions.CreditCardProcessorUnavailableException;
import ru.esipov.payments.mapper.PaymentsMapper;
import ru.esipov.payments.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.job4j.core.sagadto.Payment;

@Component
@KafkaListener(topics = "${payments.command.topic.name}")
@Slf4j
public class PaymentsCommandsHandler {
    @Value("${payments.events.topic.name}")
    private String paymentEventsTopicName;
    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentsMapper paymentsMapper;

    public PaymentsCommandsHandler(PaymentService paymentService, KafkaTemplate<String, Object> kafkaTemplate, PaymentsMapper paymentsMapper) {
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentsMapper = paymentsMapper;
    }


    @KafkaHandler
    public void handleCommand(@Payload ProcessPaymentCommand command) {
        try {
            Payment payment = paymentsMapper.commandToPayment(command);
            PaymentProcessedEvent paymentProcessedEvent = paymentService.process(payment);
            kafkaTemplate.send(paymentEventsTopicName, paymentProcessedEvent);
        } catch (CreditCardProcessorUnavailableException e) {
            log.error(e.getLocalizedMessage(), e);
            PaymentFailedEvent paymentFailedEvent = paymentsMapper.commandToFailedEvent(command);
            kafkaTemplate.send(paymentEventsTopicName, paymentFailedEvent);
        }
    }
}
