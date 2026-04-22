package com.ibm.migration.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node representing a stored procedure
 */
public class ProcedureNode extends ASTNode {
    private String name;
    private List<ParameterNode> parameters;
    private List<VariableDeclarationNode> declarations;
    private List<ASTNode> statements;
    private List<ExceptionHandlerNode> exceptionHandlers;

    public ProcedureNode(String name) {
        super();
        this.name = name;
        this.parameters = new ArrayList<>();
        this.declarations = new ArrayList<>();
        this.statements = new ArrayList<>();
        this.exceptionHandlers = new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "PROCEDURE";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void addParameter(ParameterNode parameter) {
        parameters.add(parameter);
        addChild(parameter);
    }

    public void addDeclaration(VariableDeclarationNode declaration) {
        declarations.add(declaration);
        addChild(declaration);
    }

    public void addStatement(ASTNode statement) {
        statements.add(statement);
        addChild(statement);
    }

    public void addExceptionHandler(ExceptionHandlerNode handler) {
        exceptionHandlers.add(handler);
        addChild(handler);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParameterNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterNode> parameters) {
        this.parameters = parameters;
    }

    public List<VariableDeclarationNode> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<VariableDeclarationNode> declarations) {
        this.declarations = declarations;
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    public void setStatements(List<ASTNode> statements) {
        this.statements = statements;
    }

    public List<ExceptionHandlerNode> getExceptionHandlers() {
        return exceptionHandlers;
    }

    public void setExceptionHandlers(List<ExceptionHandlerNode> exceptionHandlers) {
        this.exceptionHandlers = exceptionHandlers;
    }

    @Override
    public String toString() {
        return "ProcedureNode{name='" + name + "', parameters=" + parameters.size() + 
               ", statements=" + statements.size() + "}";
    }
}

// Made with Bob
