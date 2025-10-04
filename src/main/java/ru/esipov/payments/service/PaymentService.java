package ru.esipov.payments.service;

import ru.job4j.core.events.PaymentProcessedEvent;
import ru.job4j.core.sagadto.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> findAll();

    PaymentProcessedEvent process(Payment payment);
}
