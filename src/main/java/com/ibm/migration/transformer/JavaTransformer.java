package com.ibm.migration.transformer;

import com.ibm.migration.ast.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Transforms AST nodes into Java code representations
 */
public class JavaTransformer implements ASTVisitor<String> {
    
    private static final Logger logger = LoggerFactory.getLogger(JavaTransformer.class);
    
    private final TypeMapper typeMapper;
    private final StringBuilder codeBuilder;
    private int indentLevel;
    private static final String INDENT = "    ";

    public JavaTransformer() {
        this.typeMapper = new TypeMapper();
        this.codeBuilder = new StringBuilder();
        this.indentLevel = 0;
    }

    /**
     * Transform a procedure to a Java service class
     */
    @Override
    public String visit(ProcedureNode node) {
        logger.info("Transforming procedure: {}", node.getName());
        
        codeBuilder.setLength(0);
        indentLevel = 0;
        
        // Generate class header
        appendLine("package com.ibm.migration.generated;");
        appendLine("");
        appendLine("import org.springframework.beans.factory.annotation.Autowired;");
        appendLine("import org.springframework.jdbc.core.JdbcTemplate;");
        appendLine("import org.springframework.stereotype.Service;");
        appendLine("import org.springframework.dao.EmptyResultDataAccessException;");
        appendLine("import java.math.BigDecimal;");
        appendLine("import java.sql.Timestamp;");
        appendLine("");
        
        String className = toPascalCase(node.getName()) + "Service";
        appendLine("@Service");
        appendLine("public class " + className + " {");
        indentLevel++;
        
        appendLine("");
        appendLine("@Autowired");
        appendLine("private JdbcTemplate jdbcTemplate;");
        appendLine("");
        
        // Generate method
        String methodSignature = generateMethodSignature(node);
        appendLine(methodSignature + " {");
        indentLevel++;
        
        // Generate method body
        for (ASTNode statement : node.getStatements()) {
            statement.accept(this);
        }
        
        indentLevel--;
        appendLine("}");
        
        indentLevel--;
        appendLine("}");
        
        return codeBuilder.toString();
    }

    @Override
    public String visit(FunctionNode node) {
        logger.info("Transforming function: {}", node.getName());
        // Similar to procedure but returns a value
        return "// Function transformation not yet implemented";
    }

    @Override
    public String visit(ParameterNode node) {
        return typeMapper.mapOracleTypeToJava(node.getDataType()) + " " + 
               toCamelCase(node.getName());
    }

    @Override
    public String visit(VariableDeclarationNode node) {
        String javaType = typeMapper.mapOracleTypeToJava(node.getDataType());
        String varName = toCamelCase(node.getName());
        appendLine(javaType + " " + varName + " = null;");
        return null;
    }

    @Override
    public String visit(SelectStatementNode node) {
        appendLine("try {");
        indentLevel++;
        
        // Build SQL query
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(String.join(", ", node.getSelectColumns()));
        sql.append(" FROM ").append(node.getFromTable());
        
        if (node.getWhereCondition() != null) {
            sql.append(" WHERE ").append(node.getWhereCondition().accept(this));
        }
        
        appendLine("// Execute query");
        appendLine("jdbcTemplate.queryForObject(");
        indentLevel++;
        appendLine("\"" + sql.toString() + "\",");
        appendLine("new Object[]{/* parameters */},");
        appendLine("(rs, rowNum) -> {");
        indentLevel++;
        
        // Map result set to variables
        for (int i = 0; i < node.getIntoVariables().size(); i++) {
            String var = node.getIntoVariables().get(i);
            String col = node.getSelectColumns().get(i);
            appendLine(var + " = rs.getObject(\"" + col + "\");");
        }
        appendLine("return null;");
        
        indentLevel--;
        appendLine("}");
        indentLevel--;
        appendLine(");");
        
        indentLevel--;
        appendLine("} catch (EmptyResultDataAccessException e) {");
        indentLevel++;
        appendLine("// Handle NO_DATA_FOUND");
        indentLevel--;
        appendLine("}");
        
        return null;
    }

    @Override
    public String visit(InsertStatementNode node) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(node.getTableName());
        
        if (!node.getColumns().isEmpty()) {
            sql.append(" (").append(String.join(", ", node.getColumns())).append(")");
        }
        
        sql.append(" VALUES (");
        for (int i = 0; i < node.getValues().size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        sql.append(")");
        
        appendLine("jdbcTemplate.update(");
        indentLevel++;
        appendLine("\"" + sql.toString() + "\",");
        appendLine("/* parameters */");
        indentLevel--;
        appendLine(");");
        
        return null;
    }

    @Override
    public String visit(UpdateStatementNode node) {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(node.getTableName()).append(" SET ");
        
        for (int i = 0; i < node.getAssignments().size(); i++) {
            if (i > 0) sql.append(", ");
            AssignmentNode assignment = node.getAssignments().get(i);
            sql.append(assignment.getVariableName()).append(" = ?");
        }
        
        if (node.getWhereCondition() != null) {
            sql.append(" WHERE ").append(node.getWhereCondition().accept(this));
        }
        
        appendLine("jdbcTemplate.update(");
        indentLevel++;
        appendLine("\"" + sql.toString() + "\",");
        appendLine("/* parameters */");
        indentLevel--;
        appendLine(");");
        
        return null;
    }

    @Override
    public String visit(DeleteStatementNode node) {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(node.getTableName());
        
        if (node.getWhereCondition() != null) {
            sql.append(" WHERE ").append(node.getWhereCondition().accept(this));
        }
        
        appendLine("jdbcTemplate.update(\"" + sql.toString() + "\");");
        return null;
    }

    @Override
    public String visit(IfStatementNode node) {
        appendLine("if (" + node.getCondition().accept(this) + ") {");
        indentLevel++;
        
        for (ASTNode stmt : node.getThenStatements()) {
            stmt.accept(this);
        }
        
        indentLevel--;
        
        if (!node.getElseStatements().isEmpty()) {
            appendLine("} else {");
            indentLevel++;
            
            for (ASTNode stmt : node.getElseStatements()) {
                stmt.accept(this);
            }
            
            indentLevel--;
        }
        
        appendLine("}");
        return null;
    }

    @Override
    public String visit(ForLoopNode node) {
        appendLine("// For loop transformation");
        appendLine("for (" + node.getLoopVariable() + " : " + node.getCursorName() + ") {");
        indentLevel++;
        
        for (ASTNode stmt : node.getStatements()) {
            stmt.accept(this);
        }
        
        indentLevel--;
        appendLine("}");
        return null;
    }

    @Override
    public String visit(WhileLoopNode node) {
        appendLine("while (" + node.getCondition().accept(this) + ") {");
        indentLevel++;
        
        for (ASTNode stmt : node.getStatements()) {
            stmt.accept(this);
        }
        
        indentLevel--;
        appendLine("}");
        return null;
    }

    @Override
    public String visit(AssignmentNode node) {
        appendLine(node.getVariableName() + " = " + 
                   node.getExpression().accept(this) + ";");
        return null;
    }

    @Override
    public String visit(ReturnStatementNode node) {
        if (node.getExpression() != null) {
            appendLine("return " + node.getExpression().accept(this) + ";");
        } else {
            appendLine("return;");
        }
        return null;
    }

    @Override
    public String visit(ExceptionHandlerNode node) {
        String exceptionType = mapExceptionType(node.getExceptionName());
        appendLine("} catch (" + exceptionType + " e) {");
        indentLevel++;
        
        for (ASTNode stmt : node.getStatements()) {
            stmt.accept(this);
        }
        
        indentLevel--;
        return null;
    }

    @Override
    public String visit(ExpressionNode node) {
        if (node.getValue() != null) {
            return String.valueOf(node.getValue());
        }
        
        if (node.getOperator() != null) {
            String left = node.getLeft().accept(this);
            String right = node.getRight().accept(this);
            return left + " " + node.getOperator() + " " + right;
        }
        
        return "null";
    }

    @Override
    public String visit(LiteralNode node) {
        if ("STRING".equals(node.getLiteralType())) {
            return "\"" + node.getValue() + "\"";
        }
        return String.valueOf(node.getValue());
    }

    // Helper methods
    
    private void appendLine(String line) {
        for (int i = 0; i < indentLevel; i++) {
            codeBuilder.append(INDENT);
        }
        codeBuilder.append(line).append("\n");
    }

    private String generateMethodSignature(ProcedureNode node) {
        StringBuilder sig = new StringBuilder("public ");
        
        // Determine return type based on OUT parameters
        boolean hasOutParams = node.getParameters().stream()
            .anyMatch(p -> p.getMode() == ParameterNode.ParameterMode.OUT || 
                          p.getMode() == ParameterNode.ParameterMode.IN_OUT);
        
        if (hasOutParams) {
            sig.append("Map<String, Object>");
        } else {
            sig.append("void");
        }
        
        sig.append(" ").append(toCamelCase(node.getName())).append("(");
        
        // Add IN parameters
        boolean first = true;
        for (ParameterNode param : node.getParameters()) {
            if (param.getMode() == ParameterNode.ParameterMode.IN || 
                param.getMode() == ParameterNode.ParameterMode.IN_OUT) {
                if (!first) sig.append(", ");
                sig.append(param.accept(this));
                first = false;
            }
        }
        
        sig.append(")");
        return sig.toString();
    }

    private String toCamelCase(String name) {
        String[] parts = name.toLowerCase().split("_");
        StringBuilder result = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            result.append(Character.toUpperCase(parts[i].charAt(0)))
                  .append(parts[i].substring(1));
        }
        return result.toString();
    }

    private String toPascalCase(String name) {
        String camel = toCamelCase(name);
        return Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
    }

    private String mapExceptionType(String oracleException) {
        Map<String, String> exceptionMap = new HashMap<>();
        exceptionMap.put("NO_DATA_FOUND", "EmptyResultDataAccessException");
        exceptionMap.put("DUP_VAL_ON_INDEX", "DuplicateKeyException");
        exceptionMap.put("OTHERS", "Exception");
        
        return exceptionMap.getOrDefault(oracleException, "Exception");
    }
}

// Made with Bob
