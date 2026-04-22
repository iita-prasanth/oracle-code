package com.ibm.migration.ast;

/**
 * Visitor interface for traversing AST nodes
 */
public interface ASTVisitor<T> {
    T visit(ProcedureNode node);
    T visit(FunctionNode node);
    T visit(ParameterNode node);
    T visit(VariableDeclarationNode node);
    T visit(SelectStatementNode node);
    T visit(InsertStatementNode node);
    T visit(UpdateStatementNode node);
    T visit(DeleteStatementNode node);
    T visit(IfStatementNode node);
    T visit(ForLoopNode node);
    T visit(WhileLoopNode node);
    T visit(AssignmentNode node);
    T visit(ReturnStatementNode node);
    T visit(ExceptionHandlerNode node);
    T visit(ExpressionNode node);
    T visit(LiteralNode node);
}

// Made with Bob
