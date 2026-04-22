package com.ibm.migration.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of AST nodes representing various SQL and PL/SQL statements
 */

// Function Node
class FunctionNode extends ASTNode {
    private String name;
    private List<ParameterNode> parameters;
    private String returnType;
    private List<ASTNode> statements;

    public FunctionNode(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.statements = new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "FUNCTION";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getName() { return name; }
    public String getReturnType() { return returnType; }
    public List<ParameterNode> getParameters() { return parameters; }
    public List<ASTNode> getStatements() { return statements; }
}

// Variable Declaration Node
class VariableDeclarationNode extends ASTNode {
    private String name;
    private String dataType;
    private ExpressionNode initialValue;

    public VariableDeclarationNode(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    @Override
    public String getNodeType() {
        return "VARIABLE_DECLARATION";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getName() { return name; }
    public String getDataType() { return dataType; }
    public ExpressionNode getInitialValue() { return initialValue; }
    public void setInitialValue(ExpressionNode value) { this.initialValue = value; }
}

// Select Statement Node
class SelectStatementNode extends ASTNode {
    private List<String> selectColumns;
    private List<String> intoVariables;
    private String fromTable;
    private ExpressionNode whereCondition;

    public SelectStatementNode() {
        this.selectColumns = new ArrayList<>();
        this.intoVariables = new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "SELECT_STATEMENT";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public List<String> getSelectColumns() { return selectColumns; }
    public List<String> getIntoVariables() { return intoVariables; }
    public String getFromTable() { return fromTable; }
    public void setFromTable(String table) { this.fromTable = table; }
    public ExpressionNode getWhereCondition() { return whereCondition; }
    public void setWhereCondition(ExpressionNode condition) { this.whereCondition = condition; }
}

// Insert Statement Node
class InsertStatementNode extends ASTNode {
    private String tableName;
    private List<String> columns;
    private List<ExpressionNode> values;
    private List<String> returningColumns;
    private List<String> returningIntoVariables;

    public InsertStatementNode(String tableName) {
        this.tableName = tableName;
        this.columns = new ArrayList<>();
        this.values = new ArrayList<>();
        this.returningColumns = new ArrayList<>();
        this.returningIntoVariables = new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "INSERT_STATEMENT";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getTableName() { return tableName; }
    public List<String> getColumns() { return columns; }
    public List<ExpressionNode> getValues() { return values; }
    public List<String> getReturningColumns() { return returningColumns; }
    public List<String> getReturningIntoVariables() { return returningIntoVariables; }
}

// Update Statement Node
class UpdateStatementNode extends ASTNode {
    private String tableName;
    private List<AssignmentNode> assignments;
    private ExpressionNode whereCondition;

    public UpdateStatementNode(String tableName) {
        this.tableName = tableName;
        this.assignments = new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "UPDATE_STATEMENT";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getTableName() { return tableName; }
    public List<AssignmentNode> getAssignments() { return assignments; }
    public ExpressionNode getWhereCondition() { return whereCondition; }
    public void setWhereCondition(ExpressionNode condition) { this.whereCondition = condition; }
}

// Delete Statement Node
class DeleteStatementNode extends ASTNode {
    private String tableName;
    private ExpressionNode whereCondition;

    public DeleteStatementNode(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getNodeType() {
        return "DELETE_STATEMENT";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getTableName() { return tableName; }
    public ExpressionNode getWhereCondition() { return whereCondition; }
    public void setWhereCondition(ExpressionNode condition) { this.whereCondition = condition; }
}

// If Statement Node
class IfStatementNode extends ASTNode {
    private ExpressionNode condition;
    private List<ASTNode> thenStatements;
    private List<ASTNode> elseStatements;

    public IfStatementNode(ExpressionNode condition) {
        this.condition = condition;
        this.thenStatements = new ArrayList<>();
        this.elseStatements = new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "IF_STATEMENT";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public ExpressionNode getCondition() { return condition; }
    public List<ASTNode> getThenStatements() { return thenStatements; }
    public List<ASTNode> getElseStatements() { return elseStatements; }
}

// For Loop Node
class ForLoopNode extends ASTNode {
    private String loopVariable;
    private String cursorName;
    private List<ASTNode> statements;

    public ForLoopNode(String loopVariable, String cursorName) {
        this.loopVariable = loopVariable;
        this.cursorName = cursorName;
        this.statements = new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "FOR_LOOP";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getLoopVariable() { return loopVariable; }
    public String getCursorName() { return cursorName; }
    public List<ASTNode> getStatements() { return statements; }
}

// While Loop Node
class WhileLoopNode extends ASTNode {
    private ExpressionNode condition;
    private List<ASTNode> statements;

    public WhileLoopNode(ExpressionNode condition) {
        this.condition = condition;
        this.statements = new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "WHILE_LOOP";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public ExpressionNode getCondition() { return condition; }
    public List<ASTNode> getStatements() { return statements; }
}

// Assignment Node
class AssignmentNode extends ASTNode {
    private String variableName;
    private ExpressionNode expression;

    public AssignmentNode(String variableName, ExpressionNode expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    @Override
    public String getNodeType() {
        return "ASSIGNMENT";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getVariableName() { return variableName; }
    public ExpressionNode getExpression() { return expression; }
}

// Return Statement Node
class ReturnStatementNode extends ASTNode {
    private ExpressionNode expression;

    public ReturnStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public String getNodeType() {
        return "RETURN_STATEMENT";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public ExpressionNode getExpression() { return expression; }
}

// Exception Handler Node
class ExceptionHandlerNode extends ASTNode {
    private String exceptionName;
    private List<ASTNode> statements;

    public ExceptionHandlerNode(String exceptionName) {
        this.exceptionName = exceptionName;
        this.statements = new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "EXCEPTION_HANDLER";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getExceptionName() { return exceptionName; }
    public List<ASTNode> getStatements() { return statements; }
}

// Expression Node
class ExpressionNode extends ASTNode {
    private String operator;
    private ExpressionNode left;
    private ExpressionNode right;
    private Object value;

    public ExpressionNode() {}

    public ExpressionNode(Object value) {
        this.value = value;
    }

    @Override
    public String getNodeType() {
        return "EXPRESSION";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public ExpressionNode getLeft() { return left; }
    public void setLeft(ExpressionNode left) { this.left = left; }
    public ExpressionNode getRight() { return right; }
    public void setRight(ExpressionNode right) { this.right = right; }
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
}

// Literal Node
class LiteralNode extends ASTNode {
    private Object value;
    private String literalType;

    public LiteralNode(Object value, String literalType) {
        this.value = value;
        this.literalType = literalType;
    }

    @Override
    public String getNodeType() {
        return "LITERAL";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Object getValue() { return value; }
    public String getLiteralType() { return literalType; }
}

// Made with Bob
