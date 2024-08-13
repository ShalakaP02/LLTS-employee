package com.practice.llts_employee

import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Subject

class EmployeeServiceSpec extends LLTSEmployeeApplicationTests {

    def employeeRepository = Mock(EmployeeRepository)
    def rabbitTemplate = Mock(RabbitTemplate)

    @Subject
    EmployeeService employeeService = new EmployeeService(rabbitTemplate, employeeRepository)


    def setup() {
        // Clear the database before each test
        employeeRepository.deleteAll()
    }


    def "test deleteEmployee checks existence and deletes if exists"() {
        given: "an employee exists with the specified ID"
        1 * employeeRepository.existsById(1L) >> true

        when: "deleteEmployee is called"
        def result = employeeService.deleteEmployee(1L)

        then: "employee is deleted"
        result == true

    }


    def "Should add an employee and send message to RabbitMQ"() {
        given: "an employee details"
        String name = "John Doe"
        String position = "Developer"
        Employee savedEmployee = new Employee(id: 1L, name: name, position: position)
        employeeRepository.save(_) >> savedEmployee  // Mock the repository save method

        when: "addEmployee is called"
        def result = employeeService.addEmployee(name, position)

        then: "the employee is saved in the repository"
        1 * employeeRepository.save({ it.name == name && it.position == position }) >> savedEmployee

        and: "a message is sent to RabbitMQ"
        1 * rabbitTemplate.convertAndSend("employee-payment-exchange", "employee.payment.created", 1L)

        and: "the method returns the saved employee"
        result == savedEmployee
    }

}
