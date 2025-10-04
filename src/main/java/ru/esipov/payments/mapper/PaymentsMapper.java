package ru.esipov.payments.mapper;

import ru.esipov.payments.dao.entity.PaymentEntity;
import org.mapstruct.Mapper;
import ru.job4j.core.command.ProcessPaymentCommand;
import ru.job4j.core.events.PaymentFailedEvent;
import ru.job4j.core.events.PaymentProcessedEvent;
import ru.job4j.core.sagadto.Payment;

@Mapper(componentModel = "spring")
public interface PaymentsMapper {
    Payment commandToPayment(ProcessPaymentCommand command);
    PaymentEntity paymentToEntity(Payment payment);
    PaymentProcessedEvent entityToEvent(PaymentEntity paymentEntity);
    PaymentFailedEvent commandToFailedEvent(ProcessPaymentCommand command);

}
