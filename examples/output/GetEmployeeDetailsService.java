package com.ibm.migration.generated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.dao.EmptyResultDataAccessException;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated from Oracle Stored Procedure: get_employee_details
 * 
 * Original Procedure:
 * CREATE OR REPLACE PROCEDURE get_employee_details(
 *     p_emp_id IN NUMBER,
 *     p_emp_name OUT VARCHAR2,
 *     p_salary OUT NUMBER
 * ) AS
 * BEGIN
 *     SELECT employee_name, salary
 *     INTO p_emp_name, p_salary
 *     FROM employees
 *     WHERE employee_id = p_emp_id;
 * EXCEPTION
 *     WHEN NO_DATA_FOUND THEN
 *         p_emp_name := NULL;
 *         p_salary := 0;
 * END;
 */
@Service
public class GetEmployeeDetailsService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Get employee details by employee ID
     * 
     * @param empId Employee ID
     * @return EmployeeDetails object containing name and salary
     */
    public EmployeeDetails getEmployeeDetails(Integer empId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT employee_name, salary FROM employees WHERE employee_id = ?",
                new Object[]{empId},
                (rs, rowNum) -> new EmployeeDetails(
                    rs.getString("employee_name"),
                    rs.getBigDecimal("salary")
                )
            );
        } catch (EmptyResultDataAccessException e) {
            // Handle NO_DATA_FOUND exception
            return new EmployeeDetails(null, BigDecimal.ZERO);
        }
    }
    
    /**
     * Inner class to hold employee details
     */
    public static class EmployeeDetails {
        private String employeeName;
        private BigDecimal salary;
        
        public EmployeeDetails(String employeeName, BigDecimal salary) {
            this.employeeName = employeeName;
            this.salary = salary;
        }
        
        public String getEmployeeName() {
            return employeeName;
        }
        
        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }
        
        public BigDecimal getSalary() {
            return salary;
        }
        
        public void setSalary(BigDecimal salary) {
            this.salary = salary;
        }
        
        @Override
        public String toString() {
            return "EmployeeDetails{" +
                   "employeeName='" + employeeName + '\'' +
                   ", salary=" + salary +
                   '}';
        }
    }
}

// Made with Bob
