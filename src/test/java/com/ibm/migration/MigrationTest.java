package com.ibm.migration;

import com.ibm.migration.ast.ASTNode;
import com.ibm.migration.ast.ProcedureNode;
import com.ibm.migration.parser.PlSqlASTBuilder;
import com.ibm.migration.transformer.JavaTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Oracle SP to Java migration
 */
public class MigrationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(MigrationTest.class);
    
    private PlSqlASTBuilder astBuilder;
    private JavaTransformer javaTransformer;

    @BeforeEach
    public void setUp() {
        astBuilder = new PlSqlASTBuilder();
        javaTransformer = new JavaTransformer();
    }

    @Test
    public void testSimpleProcedureParsing() {
        logger.info("Testing simple procedure parsing");
        
        String procedureSource = 
            "CREATE OR REPLACE PROCEDURE get_employee_details(\n" +
            "    p_emp_id IN NUMBER,\n" +
            "    p_emp_name OUT VARCHAR2,\n" +
            "    p_salary OUT NUMBER\n" +
            ") AS\n" +
            "BEGIN\n" +
            "    SELECT employee_name, salary\n" +
            "    INTO p_emp_name, p_salary\n" +
            "    FROM employees\n" +
            "    WHERE employee_id = p_emp_id;\n" +
            "END;";
        
        ASTNode ast = astBuilder.buildAST(procedureSource);
        
        assertNotNull(ast, "AST should not be null");
        assertTrue(ast instanceof ProcedureNode, "Root should be a ProcedureNode");
        
        ProcedureNode procedure = (ProcedureNode) ast;
        assertEquals("get_employee_details", procedure.getName());
        assertEquals(3, procedure.getParameters().size());
        
        logger.info("Simple procedure parsing test passed");
    }

    @Test
    public void testASTValidation() {
        logger.info("Testing AST validation");
        
        String procedureSource = "CREATE PROCEDURE test_proc AS BEGIN NULL; END;";
        ASTNode ast = astBuilder.buildAST(procedureSource);
        
        boolean isValid = astBuilder.validateAST(ast);
        assertTrue(isValid, "AST should be valid");
        
        logger.info("AST validation test passed");
    }

    @Test
    public void testJavaTransformation() {
        logger.info("Testing Java transformation");
        
        ProcedureNode procedure = (ProcedureNode) astBuilder.buildAST("");
        String javaCode = javaTransformer.visit(procedure);
        
        assertNotNull(javaCode, "Generated Java code should not be null");
        assertTrue(javaCode.contains("public class"), "Should contain class declaration");
        assertTrue(javaCode.contains("@Service"), "Should contain @Service annotation");
        assertTrue(javaCode.contains("JdbcTemplate"), "Should use JdbcTemplate");
        
        logger.info("Java transformation test passed");
        logger.debug("Generated code:\n{}", javaCode);
    }

    @Test
    public void testParameterMapping() {
        logger.info("Testing parameter mapping");
        
        ProcedureNode procedure = (ProcedureNode) astBuilder.buildAST("");
        
        assertEquals(3, procedure.getParameters().size());
        assertEquals("p_emp_id", procedure.getParameters().get(0).getName());
        assertEquals("NUMBER", procedure.getParameters().get(0).getDataType());
        
        logger.info("Parameter mapping test passed");
    }

    @Test
    public void testCompleteWorkflow() {
        logger.info("Testing complete migration workflow");
        
        // Step 1: Parse Oracle SP
        String oracleSP = 
            "CREATE OR REPLACE PROCEDURE get_employee_details(\n" +
            "    p_emp_id IN NUMBER,\n" +
            "    p_emp_name OUT VARCHAR2,\n" +
            "    p_salary OUT NUMBER\n" +
            ") AS\n" +
            "BEGIN\n" +
            "    SELECT employee_name, salary\n" +
            "    INTO p_emp_name, p_salary\n" +
            "    FROM employees\n" +
            "    WHERE employee_id = p_emp_id;\n" +
            "EXCEPTION\n" +
            "    WHEN NO_DATA_FOUND THEN\n" +
            "        p_emp_name := NULL;\n" +
            "        p_salary := 0;\n" +
            "END;";
        
        // Step 2: Build AST
        ASTNode ast = astBuilder.buildAST(oracleSP);
        assertNotNull(ast);
        
        // Step 3: Validate AST
        assertTrue(astBuilder.validateAST(ast));
        
        // Step 4: Transform to Java
        String javaCode = javaTransformer.visit((ProcedureNode) ast);
        assertNotNull(javaCode);
        
        // Step 5: Verify Java code structure
        assertTrue(javaCode.contains("package com.ibm.migration.generated"));
        assertTrue(javaCode.contains("import org.springframework"));
        assertTrue(javaCode.contains("public class GetEmployeeDetailsService"));
        assertTrue(javaCode.contains("public"));
        assertTrue(javaCode.contains("getEmployeeDetails"));
        
        logger.info("Complete workflow test passed");
        logger.info("Generated Java Service:\n{}", javaCode);
    }

    @Test
    public void testExceptionHandling() {
        logger.info("Testing exception handling transformation");
        
        ProcedureNode procedure = (ProcedureNode) astBuilder.buildAST("");
        String javaCode = javaTransformer.visit(procedure);
        
        // Verify exception handling is present
        assertTrue(javaCode.contains("try") || javaCode.contains("catch"), 
                   "Should contain exception handling");
        
        logger.info("Exception handling test passed");
    }
}

// Made with Bob
