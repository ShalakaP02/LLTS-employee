package com.practice.latest_tech_stack

import com.netflix.graphql.dgs.DgsQueryExecutor
import org.springframework.beans.factory.annotation.Autowired

class EmployeeResolverSpec extends LatestTechStackApplicationTests{

    @Autowired
    DgsQueryExecutor dgsQueryExecutor

    def "Should return empty list when no employees are present"() {
        when:
        def result = dgsQueryExecutor.executeAndExtractJsonPath('''
            {
                employees {
                    id
                    name
                    position
                }
            }
        ''', 'data.employees')

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

}
