package com.practice.llts_employee

import org.slf4j.Logger
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Specification
import spock.lang.Subject

class EmployeePaymentServiceSpec extends Specification{

    RabbitTemplate rabbitTemplate = Mock()
    Logger logg = Mock(Logger)

    @Subject
    EmployeePaymentService employeePaymentService = new EmployeePaymentService(rabbitTemplate)


    def "should send employee payment message"() {
        given:
        Long employeeId = 123L
        String expectedRoutingKey = "employee.payment.created"
        String exchangeName = "employee-payment-exchange"

        when:
        employeePaymentService.sendEmployeeForPayment(employeeId)

        then:
        1 * rabbitTemplate.convertAndSend(exchangeName, expectedRoutingKey, employeeId)
    }
}
