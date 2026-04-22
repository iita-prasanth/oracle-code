package com.ibm.migration.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all AST nodes in the language-neutral representation
 */
public abstract class ASTNode {
    private int lineNumber;
    private int columnNumber;
    private ASTNode parent;
    private List<ASTNode> children;

    public ASTNode() {
        this.children = new ArrayList<>();
    }

    public ASTNode(int lineNumber, int columnNumber) {
        this();
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public void addChild(ASTNode child) {
        if (child != null) {
            children.add(child);
            child.setParent(this);
        }
    }

    public void addChildren(List<ASTNode> nodes) {
        if (nodes != null) {
            for (ASTNode node : nodes) {
                addChild(node);
            }
        }
    }

    public abstract String getNodeType();

    public abstract <T> T accept(ASTVisitor<T> visitor);

    // Getters and Setters
    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public void setChildren(List<ASTNode> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return getNodeType() + " [line=" + lineNumber + ", col=" + columnNumber + "]";
    }
}

// Made with Bob
