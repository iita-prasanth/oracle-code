package com.ibm.migration.transformer;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps Oracle data types to Java types
 */
public class TypeMapper {
    
    private final Map<String, String> typeMap;

    public TypeMapper() {
        this.typeMap = new HashMap<>();
        initializeTypeMap();
    }

    private void initializeTypeMap() {
        // Numeric types
        typeMap.put("NUMBER", "BigDecimal");
        typeMap.put("INTEGER", "Integer");
        typeMap.put("INT", "Integer");
        typeMap.put("SMALLINT", "Short");
        typeMap.put("FLOAT", "Double");
        typeMap.put("DOUBLE", "Double");
        typeMap.put("DECIMAL", "BigDecimal");
        typeMap.put("NUMERIC", "BigDecimal");
        
        // String types
        typeMap.put("VARCHAR", "String");
        typeMap.put("VARCHAR2", "String");
        typeMap.put("CHAR", "String");
        typeMap.put("NCHAR", "String");
        typeMap.put("NVARCHAR2", "String");
        typeMap.put("CLOB", "String");
        typeMap.put("NCLOB", "String");
        
        // Date/Time types
        typeMap.put("DATE", "java.sql.Date");
        typeMap.put("TIMESTAMP", "java.sql.Timestamp");
        typeMap.put("TIMESTAMP WITH TIME ZONE", "java.sql.Timestamp");
        typeMap.put("TIMESTAMP WITH LOCAL TIME ZONE", "java.sql.Timestamp");
        
        // Binary types
        typeMap.put("BLOB", "byte[]");
        typeMap.put("RAW", "byte[]");
        typeMap.put("LONG RAW", "byte[]");
        
        // Boolean (Oracle 23c+)
        typeMap.put("BOOLEAN", "Boolean");
        
        // Other types
        typeMap.put("ROWID", "String");
        typeMap.put("UROWID", "String");
    }

    /**
     * Map Oracle type to Java type
     * @param oracleType Oracle data type (e.g., "VARCHAR2", "NUMBER(10,2)")
     * @return Corresponding Java type
     */
    public String mapOracleTypeToJava(String oracleType) {
        if (oracleType == null || oracleType.isEmpty()) {
            return "Object";
        }
        
        // Remove precision/scale information
        String baseType = oracleType.toUpperCase();
        int parenIndex = baseType.indexOf('(');
        if (parenIndex > 0) {
            baseType = baseType.substring(0, parenIndex).trim();
        }
        
        // Special handling for NUMBER with precision
        if (oracleType.toUpperCase().startsWith("NUMBER")) {
            return mapNumberType(oracleType);
        }
        
        return typeMap.getOrDefault(baseType, "Object");
    }

    /**
     * Map Oracle NUMBER type to appropriate Java type based on precision and scale
     */
    private String mapNumberType(String numberType) {
        // Extract precision and scale if present
        if (numberType.contains("(")) {
            String params = numberType.substring(numberType.indexOf('(') + 1, numberType.indexOf(')'));
            String[] parts = params.split(",");
            
            if (parts.length == 1) {
                // NUMBER(p) - no decimal places
                int precision = Integer.parseInt(parts[0].trim());
                if (precision <= 2) return "Byte";
                if (precision <= 4) return "Short";
                if (precision <= 9) return "Integer";
                if (precision <= 18) return "Long";
                return "BigDecimal";
            } else if (parts.length == 2) {
                // NUMBER(p,s) - with decimal places
                int scale = Integer.parseInt(parts[1].trim());
                if (scale > 0) {
                    return "BigDecimal";
                } else {
                    int precision = Integer.parseInt(parts[0].trim());
                    if (precision <= 9) return "Integer";
                    if (precision <= 18) return "Long";
                    return "BigDecimal";
                }
            }
        }
        
        // Default NUMBER without precision
        return "BigDecimal";
    }

    /**
     * Map Java type back to Oracle type (for reverse engineering)
     */
    public String mapJavaTypeToOracle(String javaType) {
        switch (javaType) {
            case "String":
                return "VARCHAR2";
            case "Integer":
            case "Long":
            case "Short":
            case "Byte":
                return "NUMBER";
            case "BigDecimal":
            case "Double":
            case "Float":
                return "NUMBER";
            case "Boolean":
                return "NUMBER(1)";
            case "java.sql.Date":
                return "DATE";
            case "java.sql.Timestamp":
                return "TIMESTAMP";
            case "byte[]":
                return "BLOB";
            default:
                return "VARCHAR2";
        }
    }

    /**
     * Get JDBC type code for Oracle type
     */
    public int getJdbcTypeCode(String oracleType) {
        String baseType = oracleType.toUpperCase();
        int parenIndex = baseType.indexOf('(');
        if (parenIndex > 0) {
            baseType = baseType.substring(0, parenIndex).trim();
        }
        
        switch (baseType) {
            case "VARCHAR":
            case "VARCHAR2":
            case "CHAR":
            case "NCHAR":
            case "NVARCHAR2":
                return java.sql.Types.VARCHAR;
            case "NUMBER":
            case "INTEGER":
            case "INT":
                return java.sql.Types.NUMERIC;
            case "DATE":
                return java.sql.Types.DATE;
            case "TIMESTAMP":
                return java.sql.Types.TIMESTAMP;
            case "BLOB":
                return java.sql.Types.BLOB;
            case "CLOB":
                return java.sql.Types.CLOB;
            default:
                return java.sql.Types.OTHER;
        }
    }
}

// Made with Bob
