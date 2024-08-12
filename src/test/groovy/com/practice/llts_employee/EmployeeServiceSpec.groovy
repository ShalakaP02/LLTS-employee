package com.practice.llts_employee

import com.netflix.graphql.dgs.DgsQueryExecutor
import org.springframework.beans.factory.annotation.Autowired

class EmployeeServiceSpec extends LLTSEmployeeApplicationTests {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeRepository employeeRepository // Assuming you have a repository for accessing the database directly

    def setup() {
        // Clear the database before each test
        employeeRepository.deleteAll()
    }

    def "Should list employees after adding them"(){
        given:
        employeeService.addEmployee("Shalaka","Java Dev")
        employeeService.addEmployee("John", "QA")
        when:
        def result = dgsQueryExecutor.executeAndExtractJsonPath('''
            {
                getAllEmployees {
                    id
                    name
                    position
                }
            }
        ''', 'data.getAllEmployees')

        then:
        result.size() == 2
        result.find { it.name == "Shalaka" && it.position == "Java Dev" }
        result.find { it.name == "John" && it.position == "QA" }
    }

    def "Should be able to delete employee and verify it is deleted"() {
       given:
       def emp1 = employeeService.addEmployee("John Doe", "Developer")
       def emp2 = employeeService.addEmployee("Shalaka Doe", "QA")

        when:
        def deleteResult = dgsQueryExecutor.executeAndExtractJsonPath("""
            mutation {
                deleteEmployee(id: ${emp1.id}) 
            }
        """, 'data.deleteEmployee')

        def listResult = dgsQueryExecutor.executeAndExtractJsonPath('''
            {
                getAllEmployees {
                    id
                    name
                    position
                }
            }
        ''', 'data.getAllEmployees')

        then:
        deleteResult == true
        listResult.size() == 1
    }

    def "Should get employee by id"() {
        given:
        def emp1 = employeeService.addEmployee("John Doe", "Developer")
        def emp2 = employeeService.addEmployee("Shalaka Doe", "QA")

        when:
        def res = employeeService.getEmployeeById(emp1.id);

        then:
        res.name == "John Doe"

    }
}
