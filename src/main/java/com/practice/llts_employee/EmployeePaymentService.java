package com.practice.llts_employee;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmployeePaymentService {

    private final RabbitTemplate rabbitTemplate;

    public static final String EXCHANGE_NAME = "employee-payment-exchange";
    public static final String QUEUE_NAME = "payment-queue-response";
    public static final String ROUTING_KEY = "employee.payment";

    private  final Logger logg = LoggerFactory.getLogger(EmployeePaymentService.class);

    public  void sendEmployeeForPayment(Long employeeId) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY.concat(".created"), employeeId);
        logg.info("Employee payment message sent to employee: {}", employeeId);
    }

    //@RabbitListener(queues = QUEUE_NAME)
    public void receivePaymentStatusMessage(Message message) {
        logg.info("####### Employee payment message received: {} ######", message);
        // Extract headers
        MessageProperties properties = message.getMessageProperties();
        String status = (String) properties.getHeaders().get("payment-status");
        Long employeeId = (Long) properties.getHeaders().get("employee-id");

        // Handle the message based on the header values
        if (status != null && employeeId != null) {
            switch (status) {
                case "PROCESSED":
                    logg.info("Employee payment processed: {}", employeeId);
                    break;
                case "PENDING":
                    logg.info("Employee payment pending: {}", employeeId);
                    break;
                case "BLOCKED":
                    logg.info("Employee payment failed: {}", employeeId);
                    break;
                default:
                   logg.info("Employee payment unknown: {}", employeeId);
            }
        } else {
           logg.info("Employee payment error, headers missing : {}", employeeId);
        }
    }
}
