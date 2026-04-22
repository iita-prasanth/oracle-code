# Oracle Stored Procedure to Java Migration Guide

## Overview

This guide explains how to use the Oracle SP to Java migration tool to modernize your Oracle stored procedures into idiomatic Java code using Spring JDBC or JPA.

## Migration Process

### 1. Parse Oracle Stored Procedure

The tool uses ANTLR4 to parse PL/SQL syntax and build an Abstract Syntax Tree (AST).

```java
PlSqlASTBuilder builder = new PlSqlASTBuilder();
ASTNode ast = builder.buildAST(oracleSPSource);
```

### 2. Build Language-Neutral AST

The AST represents the stored procedure logic in a language-neutral format:

- **ProcedureNode**: Root node containing procedure metadata
- **ParameterNode**: IN, OUT, or IN OUT parameters
- **StatementNodes**: SQL statements (SELECT, INSERT, UPDATE, DELETE)
- **ControlFlowNodes**: IF, FOR, WHILE loops
- **ExceptionHandlerNode**: Exception handling logic

### 3. Transform to Java

The JavaTransformer visits each AST node and generates equivalent Java code:

```java
JavaTransformer transformer = new JavaTransformer();
String javaCode = transformer.visit(procedureNode);
```

## Type Mapping

### Oracle to Java Type Conversion

| Oracle Type | Java Type | Notes |
|------------|-----------|-------|
| NUMBER | BigDecimal | Default for NUMBER without precision |
| NUMBER(p) | Integer/Long | Based on precision |
| NUMBER(p,s) | BigDecimal | When scale > 0 |
| VARCHAR2 | String | |
| DATE | java.sql.Date | |
| TIMESTAMP | java.sql.Timestamp | |
| BLOB | byte[] | |
| BOOLEAN | Boolean | Oracle 23c+ |

## Transformation Examples

### Example 1: Simple SELECT Procedure

**Oracle SP:**
```sql
CREATE OR REPLACE PROCEDURE get_employee_details(
    p_emp_id IN NUMBER,
    p_emp_name OUT VARCHAR2,
    p_salary OUT NUMBER
) AS
BEGIN
    SELECT employee_name, salary
    INTO p_emp_name, p_salary
    FROM employees
    WHERE employee_id = p_emp_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_emp_name := NULL;
        p_salary := 0;
END;
```

**Generated Java:**
```java
@Service
public class GetEmployeeDetailsService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
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
            return new EmployeeDetails(null, BigDecimal.ZERO);
        }
    }
}
```

### Example 2: INSERT with Transaction

**Oracle SP:**
```sql
CREATE OR REPLACE PROCEDURE add_employee(
    p_emp_name IN VARCHAR2,
    p_email IN VARCHAR2,
    p_salary IN NUMBER,
    p_dept_id IN NUMBER,
    p_emp_id OUT NUMBER
) AS
BEGIN
    INSERT INTO employees (employee_name, email, salary, department_id)
    VALUES (p_emp_name, p_email, p_salary, p_dept_id)
    RETURNING employee_id INTO p_emp_id;
    
    COMMIT;
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Employee email already exists');
END;
```

**Generated Java:**
```java
@Service
public class AddEmployeeService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Transactional
    public Integer addEmployee(String empName, String email, 
                               BigDecimal salary, Integer deptId) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO employees (employee_name, email, salary, department_id) " +
                    "VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, empName);
                ps.setString(2, email);
                ps.setBigDecimal(3, salary);
                ps.setInt(4, deptId);
                return ps;
            }, keyHolder);
            
            return keyHolder.getKey().intValue();
        } catch (DuplicateKeyException e) {
            throw new BusinessException("Employee email already exists");
        }
    }
}
```

### Example 3: UPDATE with Conditional Logic

**Oracle SP:**
```sql
CREATE OR REPLACE PROCEDURE update_employee_salary(
    p_emp_id IN NUMBER,
    p_percentage IN NUMBER,
    p_new_salary OUT NUMBER
) AS
    v_current_salary NUMBER;
    v_max_salary NUMBER := 200000;
BEGIN
    SELECT salary INTO v_current_salary
    FROM employees
    WHERE employee_id = p_emp_id;
    
    p_new_salary := v_current_salary * (1 + p_percentage / 100);
    
    IF p_new_salary > v_max_salary THEN
        p_new_salary := v_max_salary;
    END IF;
    
    UPDATE employees
    SET salary = p_new_salary
    WHERE employee_id = p_emp_id;
    
    COMMIT;
END;
```

**Generated Java:**
```java
@Service
public class UpdateEmployeeSalaryService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final BigDecimal MAX_SALARY = new BigDecimal("200000");
    
    @Transactional
    public BigDecimal updateEmployeeSalary(Integer empId, BigDecimal percentage) {
        BigDecimal currentSalary = jdbcTemplate.queryForObject(
            "SELECT salary FROM employees WHERE employee_id = ?",
            BigDecimal.class,
            empId
        );
        
        BigDecimal newSalary = currentSalary.multiply(
            BigDecimal.ONE.add(percentage.divide(new BigDecimal("100")))
        );
        
        if (newSalary.compareTo(MAX_SALARY) > 0) {
            newSalary = MAX_SALARY;
        }
        
        jdbcTemplate.update(
            "UPDATE employees SET salary = ? WHERE employee_id = ?",
            newSalary, empId
        );
        
        return newSalary;
    }
}
```

## Exception Mapping

| Oracle Exception | Java Exception |
|-----------------|----------------|
| NO_DATA_FOUND | EmptyResultDataAccessException |
| DUP_VAL_ON_INDEX | DuplicateKeyException |
| TOO_MANY_ROWS | IncorrectResultSizeDataAccessException |
| OTHERS | DataAccessException |

## Best Practices

1. **Use @Transactional**: Replace COMMIT/ROLLBACK with Spring's declarative transactions
2. **Return Objects**: Convert OUT parameters to return types or DTOs
3. **Use RowMapper**: For complex result set mapping
4. **Handle Exceptions**: Map Oracle exceptions to Spring's DataAccessException hierarchy
5. **Parameterize Queries**: Always use PreparedStatement to prevent SQL injection

## Running the Migration

### Command Line

```bash
mvn clean install
java -jar target/oracle-sp-to-java-1.0.0-SNAPSHOT.jar \
  --input examples/input/employee_procedures.sql \
  --output examples/output/
```

### Programmatic API

```java
// Parse Oracle SP
PlSqlASTBuilder parser = new PlSqlASTBuilder();
ASTNode ast = parser.buildAST(sqlSource);

// Validate AST
if (!parser.validateAST(ast)) {
    throw new IllegalStateException("Invalid AST");
}

// Transform to Java
JavaTransformer transformer = new JavaTransformer();
String javaCode = transformer.visit((ProcedureNode) ast);

// Write to file
Files.writeString(Path.of("output/Service.java"), javaCode);
```

## Limitations

- Complex cursor operations may require manual review
- Dynamic SQL (EXECUTE IMMEDIATE) needs special handling
- Package-level variables require refactoring to class fields
- Autonomous transactions need explicit transaction management

## Next Steps

1. Review generated code for correctness
2. Add unit tests for each service method
3. Configure Spring Data Source and JdbcTemplate
4. Test with actual database
5. Optimize queries if needed

## Support

For issues or questions, please refer to the project documentation or contact the development team.