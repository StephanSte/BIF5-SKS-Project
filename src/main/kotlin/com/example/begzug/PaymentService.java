package com.example.begzug;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @KafkaListener(topics = "payment", groupId = "group.id")
    public void processPayment(String message) {
        System.out.println(message);
    }

    public String showStatistics(String authorName) {
        return "null";
    }
}
