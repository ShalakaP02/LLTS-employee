package com.practice.llts_employee;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final RabbitTemplate rabbitTemplate;
    private final EmployeeRepository employeeRepository;


    Logger log = LoggerFactory.getLogger(EmployeeService.class);

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public Employee addEmployee(String name, String position) {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setPosition(position);
        Employee newEmp = employeeRepository.save(employee);
        sendEmployeePaymentMessage(newEmp.getId());
        return newEmp;
    }

    public boolean deleteEmployee(Long id) {
        log.info("Checking if employee exists with ID: {}", id);
        if (employeeRepository.existsById(id)) {
            log.info("Employee exists, deleting...");
            employeeRepository.deleteById(id);
            return true;
        }
        log.info("Employee does not exist, nothing to delete.");
        return false;
    }


    public void sendEmployeePaymentMessage(Long employeeId) {
        rabbitTemplate.convertAndSend("employee-payment-exchange", "employee.payment.created", employeeId);
        log.info("Employee payment message sent to employee: {}", employeeId);
    }
}