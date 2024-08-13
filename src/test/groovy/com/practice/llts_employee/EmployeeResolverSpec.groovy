package com.practice.llts_employee

import com.netflix.graphql.dgs.DgsQueryExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean


class EmployeeResolverSpec extends LLTSEmployeeApplicationTests{

    @Autowired
    DgsQueryExecutor dgsQueryExecutor

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeService employeeService;

    @MockBean
    EmployeePaymentService employeePaymentService;

    def setup() {
        // Clear the database before each test
        employeeRepository.deleteAll()
    }

    def "Should return empty list when no employees are present"() {
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
        result == []
        result.size() == 0
    }

    def "should add an employee and return it"() {
        when:
        def result = dgsQueryExecutor.executeAndExtractJsonPath('''
            mutation {
                addEmployee(name: "John Doe", position: "Developer") {
                    id
                    name
                    position
                }
            }
        ''', 'data.addEmployee')

        then:
        result.name == "John Doe"
        result.position == "Developer"
        result.id != null
    }


    def "Should be able to delete employee and verify it is deleted"() {\

        given:
        def emp1 = dgsQueryExecutor.executeAndExtractJsonPath('''
            mutation {
                addEmployee(name: "John Doe", position: "Developer") {
                    id
                    name
                    position
                }
            }
        ''', 'data.addEmployee')

        def emp2 = dgsQueryExecutor.executeAndExtractJsonPath('''
            mutation {
                addEmployee(name: "Shalaka Doe", position: "QA") {
                    id
                    name
                    position
                }
            }
        ''', 'data.addEmployee')

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

    def "Should list employees after adding them"(){
        given:
        employeeService.addEmployee("Shalaka","Java Dev")
        employeeService.addEmployee("John", "QA")
        when:
        def result = employeeService.getAllEmployees()

        then:
        result.size() == 2
        result.find { it.name == "Shalaka" && it.position == "Java Dev" }
        result.find { it.name == "John" && it.position == "QA" }
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
