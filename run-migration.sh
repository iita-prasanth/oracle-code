#!/bin/bash

# Oracle SP to Java Migration Runner Script
# This script builds the project and runs the migration tool

set -e

echo "=========================================="
echo "Oracle SP to Java Migration Tool"
echo "=========================================="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 11 or higher."
    exit 1
fi

# Build the project
echo "Building the project..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "Error: Build failed!"
    exit 1
fi

echo ""
echo "Build successful!"
echo ""

# Run tests
echo "Running tests..."
mvn test

if [ $? -ne 0 ]; then
    echo "Warning: Some tests failed!"
else
    echo "All tests passed!"
fi

echo ""
echo "=========================================="
echo "Migration tool is ready to use!"
echo "=========================================="
echo ""
echo "To run the migration:"
echo "  java -jar target/oracle-sp-to-java-1.0.0-SNAPSHOT.jar \\"
echo "    --input examples/input/employee_procedures.sql \\"
echo "    --output examples/output/"
echo ""
echo "Or use the Java API programmatically."
echo ""

# Made with Bob
