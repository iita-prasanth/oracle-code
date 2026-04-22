# Oracle Stored Procedure to Java Function Migration

This project demonstrates modernizing Oracle Stored Procedures to Java Functions using AST (Abstract Syntax Tree) transformation.

## Overview

This migration recipe:
1. Generates an ANTLR4-based PL/SQL parser
2. Builds a language-neutral AST from Oracle SP source
3. Transforms the AST into idiomatic Java classes (JDBC or JPA)

## Project Structure

```
oracle-sp-to-java-migration/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ibm/migration/
│   │   │       ├── ast/          # AST model classes
│   │   │       ├── parser/       # ANTLR4 parser integration
│   │   │       ├── transformer/  # AST to Java transformation
│   │   │       └── generator/    # Java code generation
│   │   ├── antlr4/
│   │   │   └── com/ibm/migration/grammar/
│   │   │       └── PlSql.g4      # PL/SQL ANTLR4 grammar
│   │   └── resources/
│   │       └── templates/        # Java code templates
│   └── test/
│       ├── java/                 # Unit tests
│       └── resources/
│           └── oracle-samples/   # Sample Oracle SPs
├── examples/
│   ├── input/                    # Sample Oracle stored procedures
│   └── output/                   # Generated Java code
├── pom.xml                       # Maven build configuration
└── README.md
```

## Features

- **PL/SQL Parsing**: ANTLR4-based parser for Oracle PL/SQL syntax
- **AST Generation**: Language-neutral intermediate representation
- **Java Generation**: Produces idiomatic Java with JDBC or JPA
- **Type Mapping**: Oracle types to Java types conversion
- **Error Handling**: Comprehensive error handling and logging

## Requirements

- Java 11 or higher
- Maven 3.6+
- ANTLR4 4.9+

## Building

```bash
mvn clean install
```

## Usage

```java
// Parse Oracle SP
PlSqlParser parser = new PlSqlParser("path/to/stored_procedure.sql");
ASTNode ast = parser.parse();

// Transform to Java
JavaTransformer transformer = new JavaTransformer();
JavaClass javaClass = transformer.transform(ast);

// Generate code
JavaCodeGenerator generator = new JavaCodeGenerator();
String javaCode = generator.generate(javaClass);
```

## Example

### Input: Oracle Stored Procedure
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

### Output: Java Function
```java
public class EmployeeService {
    
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

## License

IBM Internal Use