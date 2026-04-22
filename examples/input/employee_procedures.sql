-- Sample Oracle Stored Procedures for Testing Migration

-- 1. Simple SELECT procedure with IN and OUT parameters
CREATE OR REPLACE PROCEDURE get_employee_details(
    p_emp_id IN NUMBER,
    p_emp_name OUT VARCHAR2,
    p_salary OUT NUMBER
) AS
BEGIN
    SELECT employee_name, salary
    INTO p_emp_name, p_salary
    FROM employees
    WHERE employee_id = p_emp_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_emp_name := NULL;
        p_salary := 0;
    WHEN OTHERS THEN
        RAISE;
END get_employee_details;
/

-- 2. INSERT procedure with transaction handling
CREATE OR REPLACE PROCEDURE add_employee(
    p_emp_name IN VARCHAR2,
    p_email IN VARCHAR2,
    p_salary IN NUMBER,
    p_dept_id IN NUMBER,
    p_emp_id OUT NUMBER
) AS
BEGIN
    INSERT INTO employees (employee_name, email, salary, department_id)
    VALUES (p_emp_name, p_email, p_salary, p_dept_id)
    RETURNING employee_id INTO p_emp_id;
    
    COMMIT;
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Employee email already exists');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END add_employee;
/

-- 3. UPDATE procedure with conditional logic
CREATE OR REPLACE PROCEDURE update_employee_salary(
    p_emp_id IN NUMBER,
    p_percentage IN NUMBER,
    p_new_salary OUT NUMBER
) AS
    v_current_salary NUMBER;
    v_max_salary NUMBER := 200000;
BEGIN
    -- Get current salary
    SELECT salary INTO v_current_salary
    FROM employees
    WHERE employee_id = p_emp_id;
    
    -- Calculate new salary
    p_new_salary := v_current_salary * (1 + p_percentage / 100);
    
    -- Check if new salary exceeds maximum
    IF p_new_salary > v_max_salary THEN
        p_new_salary := v_max_salary;
    END IF;
    
    -- Update the salary
    UPDATE employees
    SET salary = p_new_salary,
        last_updated = SYSDATE
    WHERE employee_id = p_emp_id;
    
    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20002, 'Employee not found');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END update_employee_salary;
/

-- 4. DELETE procedure with validation
CREATE OR REPLACE PROCEDURE delete_employee(
    p_emp_id IN NUMBER,
    p_success OUT NUMBER
) AS
    v_count NUMBER;
BEGIN
    -- Check if employee exists
    SELECT COUNT(*) INTO v_count
    FROM employees
    WHERE employee_id = p_emp_id;
    
    IF v_count = 0 THEN
        p_success := 0;
        RETURN;
    END IF;
    
    -- Delete employee
    DELETE FROM employees
    WHERE employee_id = p_emp_id;
    
    COMMIT;
    p_success := 1;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_success := -1;
        RAISE;
END delete_employee;
/

-- 5. Complex procedure with cursor and loop
CREATE OR REPLACE PROCEDURE calculate_department_bonus(
    p_dept_id IN NUMBER,
    p_bonus_percentage IN NUMBER,
    p_total_bonus OUT NUMBER
) AS
    CURSOR emp_cursor IS
        SELECT employee_id, salary
        FROM employees
        WHERE department_id = p_dept_id;
    
    v_bonus NUMBER;
BEGIN
    p_total_bonus := 0;
    
    FOR emp_rec IN emp_cursor LOOP
        v_bonus := emp_rec.salary * (p_bonus_percentage / 100);
        
        UPDATE employees
        SET bonus = v_bonus
        WHERE employee_id = emp_rec.employee_id;
        
        p_total_bonus := p_total_bonus + v_bonus;
    END LOOP;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END calculate_department_bonus;
/

-- 6. Function returning a value
CREATE OR REPLACE FUNCTION get_employee_count(
    p_dept_id IN NUMBER
) RETURN NUMBER AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM employees
    WHERE department_id = p_dept_id;
    
    RETURN v_count;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END get_employee_count;
/

-- 7. Procedure with multiple queries
CREATE OR REPLACE PROCEDURE get_employee_summary(
    p_emp_id IN NUMBER,
    p_emp_name OUT VARCHAR2,
    p_dept_name OUT VARCHAR2,
    p_manager_name OUT VARCHAR2,
    p_salary OUT NUMBER
) AS
BEGIN
    SELECT e.employee_name, d.department_name, m.employee_name, e.salary
    INTO p_emp_name, p_dept_name, p_manager_name, p_salary
    FROM employees e
    LEFT JOIN departments d ON e.department_id = d.department_id
    LEFT JOIN employees m ON e.manager_id = m.employee_id
    WHERE e.employee_id = p_emp_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_emp_name := NULL;
        p_dept_name := NULL;
        p_manager_name := NULL;
        p_salary := 0;
END get_employee_summary;
/

-- Made with Bob
