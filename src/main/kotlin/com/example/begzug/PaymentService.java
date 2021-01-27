package com.example.begzug;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    /**
     * Listens to Kafka to see if any payment has happened.
     * In a production environment, this would either handle the actual payment,
     * or would connect to a third party payment service like PayPal.
     */
    @KafkaListener(topics = "payment", groupId = "group.id")
    public void processPayment(String message) {
        System.out.println(message);
    }
}
