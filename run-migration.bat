@echo off
REM Oracle SP to Java Migration Runner Script (Windows)
REM This script builds the project and runs the migration tool

echo ==========================================
echo Oracle SP to Java Migration Tool
echo ==========================================
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Maven is not installed. Please install Maven first.
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Java is not installed. Please install Java 11 or higher.
    exit /b 1
)

REM Build the project
echo Building the project...
call mvn clean install -DskipTests

if %errorlevel% neq 0 (
    echo Error: Build failed!
    exit /b 1
)

echo.
echo Build successful!
echo.

REM Run tests
echo Running tests...
call mvn test

if %errorlevel% neq 0 (
    echo Warning: Some tests failed!
) else (
    echo All tests passed!
)

echo.
echo ==========================================
echo Migration tool is ready to use!
echo ==========================================
echo.
echo To run the migration:
echo   java -jar target/oracle-sp-to-java-1.0.0-SNAPSHOT.jar ^
echo     --input examples/input/employee_procedures.sql ^
echo     --output examples/output/
echo.
echo Or use the Java API programmatically.
echo.

pause

@REM Made with Bob
