package com.ibm.migration.ast;

/**
 * AST node representing a procedure/function parameter
 */
public class ParameterNode extends ASTNode {
    
    public enum ParameterMode {
        IN, OUT, IN_OUT
    }
    
    private String name;
    private String dataType;
    private ParameterMode mode;
    private ExpressionNode defaultValue;

    public ParameterNode(String name, String dataType, ParameterMode mode) {
        super();
        this.name = name;
        this.dataType = dataType;
        this.mode = mode;
    }

    @Override
    public String getNodeType() {
        return "PARAMETER";
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public ParameterMode getMode() {
        return mode;
    }

    public void setMode(ParameterMode mode) {
        this.mode = mode;
    }

    public ExpressionNode getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(ExpressionNode defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "ParameterNode{name='" + name + "', type='" + dataType + 
               "', mode=" + mode + "}";
    }
}

// Made with Bob
