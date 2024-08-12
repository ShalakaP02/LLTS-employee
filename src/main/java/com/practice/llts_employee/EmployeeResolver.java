package com.practice.llts_employee;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;

import java.util.List;

@DgsComponent
public class EmployeeResolver {

    private final EmployeeService employeeService;

    public EmployeeResolver(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @DgsQuery(field = "getAllEmployees")
    public List<Employee> getEmployees() {
        return employeeService.getAllEmployees();
    }

    @DgsQuery(field = "getEmployeeById")
    public Employee getEmployee(Long id) {
        return employeeService.getEmployeeById(id);
    }

    @DgsMutation(field = "addEmployee")
    public Employee addEmployee(String name, String position) {
        return employeeService.addEmployee(name, position);
    }

    @DgsMutation(field = "deleteEmployee")
    public Boolean deleteEmployee(Long id) {
        return employeeService.deleteEmployee(id);
    }
}
