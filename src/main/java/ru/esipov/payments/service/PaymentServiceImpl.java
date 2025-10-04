package ru.esipov.payments.service;

import ru.esipov.payments.dao.entity.PaymentEntity;
import ru.esipov.payments.dao.repository.PaymentRepository;
import ru.esipov.payments.mapper.PaymentsMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.core.events.PaymentProcessedEvent;
import ru.job4j.core.sagadto.Payment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final String SAMPLE_CREDIT_CARD_NUMBER = "3015886955400126";
    private final PaymentRepository paymentRepository;
    private final CreditCardProcessorRemoteService ccpRemoteService;
    private final PaymentsMapper paymentsMapper;

    @Override
    public PaymentProcessedEvent process(Payment payment) {
        BigDecimal totalPrice = payment.getProductPrice()
                .multiply(new BigDecimal(payment.getProductQuantity()));
        ccpRemoteService.process(new BigInteger(SAMPLE_CREDIT_CARD_NUMBER), totalPrice);
        PaymentEntity paymentEntity = paymentsMapper.paymentToEntity(payment);
        paymentRepository.save(paymentEntity);
        return paymentsMapper.entityToEvent(paymentEntity);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll().stream().map(entity ->
                new Payment(entity.getId(),
                        entity.getOrderId(),
                        entity.getProductId(),
                        entity.getProductPrice(),
                        entity.getProductQuantity())
        ).toList();
    }
}
