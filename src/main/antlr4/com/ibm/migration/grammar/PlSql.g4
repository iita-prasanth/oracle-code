grammar PlSql;

// Parser Rules

compilationUnit
    : (createProcedure | createFunction)+ EOF
    ;

createProcedure
    : CREATE (OR REPLACE)? PROCEDURE procedureName
      LPAREN parameterList? RPAREN
      (IS | AS)
      declarationSection?
      BEGIN
      statementList
      exceptionSection?
      END procedureName? SEMICOLON
      SLASH?
    ;

createFunction
    : CREATE (OR REPLACE)? FUNCTION functionName
      LPAREN parameterList? RPAREN
      RETURN dataType
      (IS | AS)
      declarationSection?
      BEGIN
      statementList
      exceptionSection?
      END functionName? SEMICOLON
      SLASH?
    ;

parameterList
    : parameter (COMMA parameter)*
    ;

parameter
    : parameterName (IN | OUT | IN_OUT)? dataType (DEFAULT expression)?
    ;

dataType
    : NUMBER (LPAREN NUMBER (COMMA NUMBER)? RPAREN)?
    | VARCHAR2 LPAREN NUMBER RPAREN
    | DATE
    | TIMESTAMP
    | BOOLEAN
    | identifier (PERCENT (TYPE | ROWTYPE))?
    ;

declarationSection
    : variableDeclaration+
    ;

variableDeclaration
    : variableName dataType (DEFAULT expression)? SEMICOLON
    | CURSOR cursorName IS selectStatement SEMICOLON
    ;

statementList
    : statement+
    ;

statement
    : assignmentStatement
    | selectIntoStatement
    | insertStatement
    | updateStatement
    | deleteStatement
    | ifStatement
    | forLoopStatement
    | whileLoopStatement
    | returnStatement
    | commitStatement
    | rollbackStatement
    | raiseStatement
    | nullStatement
    ;

assignmentStatement
    : variableName ASSIGN expression SEMICOLON
    ;

selectIntoStatement
    : SELECT selectList
      INTO variableList
      FROM tableReference
      whereClause?
      SEMICOLON
    ;

insertStatement
    : INSERT INTO tableName (LPAREN columnList RPAREN)?
      VALUES LPAREN expressionList RPAREN
      (RETURNING columnList INTO variableList)?
      SEMICOLON
    ;

updateStatement
    : UPDATE tableName
      SET assignmentList
      whereClause?
      SEMICOLON
    ;

deleteStatement
    : DELETE FROM tableName
      whereClause?
      SEMICOLON
    ;

ifStatement
    : IF condition THEN
      statementList
      (ELSIF condition THEN statementList)*
      (ELSE statementList)?
      END IF SEMICOLON
    ;

forLoopStatement
    : FOR loopVariable IN cursorName LOOP
      statementList
      END LOOP SEMICOLON
    ;

whileLoopStatement
    : WHILE condition LOOP
      statementList
      END LOOP SEMICOLON
    ;

returnStatement
    : RETURN expression? SEMICOLON
    ;

commitStatement
    : COMMIT SEMICOLON
    ;

rollbackStatement
    : ROLLBACK SEMICOLON
    ;

raiseStatement
    : RAISE (exceptionName | RAISE_APPLICATION_ERROR LPAREN expression COMMA expression RPAREN)? SEMICOLON
    ;

nullStatement
    : NULL SEMICOLON
    ;

exceptionSection
    : EXCEPTION
      exceptionHandler+
    ;

exceptionHandler
    : WHEN (exceptionName | OTHERS) THEN
      statementList
    ;

selectStatement
    : SELECT selectList
      FROM tableReference
      (LEFT? JOIN tableReference ON condition)?
      whereClause?
    ;

selectList
    : STAR
    | expression (COMMA expression)*
    ;

variableList
    : variableName (COMMA variableName)*
    ;

columnList
    : columnName (COMMA columnName)*
    ;

expressionList
    : expression (COMMA expression)*
    ;

assignmentList
    : columnName ASSIGN expression (COMMA columnName ASSIGN expression)*
    ;

tableReference
    : tableName (identifier)?
    ;

whereClause
    : WHERE condition
    ;

condition
    : expression comparisonOperator expression
    | expression IS (NOT)? NULL
    | expression (AND | OR) condition
    | NOT condition
    | LPAREN condition RPAREN
    ;

expression
    : literal
    | variableName
    | functionCall
    | expression arithmeticOperator expression
    | LPAREN expression RPAREN
    | expression PERCENT NUMBER
    ;

functionCall
    : functionName LPAREN expressionList? RPAREN
    ;

comparisonOperator
    : EQ | NEQ | LT | LTE | GT | GTE
    ;

arithmeticOperator
    : PLUS | MINUS | STAR | SLASH
    ;

literal
    : NUMBER
    | STRING_LITERAL
    | NULL
    | SYSDATE
    ;

procedureName : identifier ;
functionName : identifier ;
parameterName : identifier ;
variableName : identifier ;
cursorName : identifier ;
tableName : identifier ;
columnName : identifier ;
exceptionName : identifier ;
loopVariable : identifier ;

identifier
    : IDENTIFIER
    | QUOTED_IDENTIFIER
    ;

// Lexer Rules

// Keywords
CREATE : C R E A T E ;
OR : O R ;
REPLACE : R E P L A C E ;
PROCEDURE : P R O C E D U R E ;
FUNCTION : F U N C T I O N ;
RETURN : R E T U R N ;
IS : I S ;
AS : A S ;
BEGIN : B E G I N ;
END : E N D ;
IN : I N ;
OUT : O U T ;
IN_OUT : I N ' ' O U T ;
DEFAULT : D E F A U L T ;
NUMBER : N U M B E R ;
VARCHAR2 : V A R C H A R '2' ;
DATE : D A T E ;
TIMESTAMP : T I M E S T A M P ;
BOOLEAN : B O O L E A N ;
CURSOR : C U R S O R ;
SELECT : S E L E C T ;
INTO : I N T O ;
FROM : F R O M ;
WHERE : W H E R E ;
INSERT : I N S E R T ;
VALUES : V A L U E S ;
UPDATE : U P D A T E ;
SET : S E T ;
DELETE : D E L E T E ;
IF : I F ;
THEN : T H E N ;
ELSIF : E L S I F ;
ELSE : E L S E ;
FOR : F O R ;
LOOP : L O O P ;
WHILE : W H I L E ;
COMMIT : C O M M I T ;
ROLLBACK : R O L L B A C K ;
RAISE : R A I S E ;
EXCEPTION : E X C E P T I O N ;
WHEN : W H E N ;
OTHERS : O T H E R S ;
NULL : N U L L ;
AND : A N D ;
OR_OP : O R ;
NOT : N O T ;
LEFT : L E F T ;
JOIN : J O I N ;
ON : O N ;
RETURNING : R E T U R N I N G ;
RAISE_APPLICATION_ERROR : R A I S E '_' A P P L I C A T I O N '_' E R R O R ;
NO_DATA_FOUND : N O '_' D A T A '_' F O U N D ;
DUP_VAL_ON_INDEX : D U P '_' V A L '_' O N '_' I N D E X ;
SYSDATE : S Y S D A T E ;
COUNT : C O U N T ;
TYPE : T Y P E ;
ROWTYPE : R O W T Y P E ;

// Operators
LPAREN : '(' ;
RPAREN : ')' ;
COMMA : ',' ;
SEMICOLON : ';' ;
SLASH : '/' ;
ASSIGN : ':=' ;
EQ : '=' ;
NEQ : '!=' | '<>' ;
LT : '<' ;
LTE : '<=' ;
GT : '>' ;
GTE : '>=' ;
PLUS : '+' ;
MINUS : '-' ;
STAR : '*' ;
PERCENT : '%' ;

// Literals
STRING_LITERAL : '\'' (~'\'' | '\'\'')* '\'' ;

// Identifiers
IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]* ;
QUOTED_IDENTIFIER : '"' ~'"'+ '"' ;

// Whitespace and Comments
WS : [ \t\r\n]+ -> skip ;
LINE_COMMENT : '--' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;

// Case-insensitive fragments
fragment A : [aA] ;
fragment B : [bB] ;
fragment C : [cC] ;
fragment D : [dD] ;
fragment E : [eE] ;
fragment F : [fF] ;
fragment G : [gG] ;
fragment H : [hH] ;
fragment I : [iI] ;
fragment J : [jJ] ;
fragment K : [kK] ;
fragment L : [lL] ;
fragment M : [mM] ;
fragment N : [nN] ;
fragment O : [oO] ;
fragment P : [pP] ;
fragment Q : [qQ] ;
fragment R : [rR] ;
fragment S : [sS] ;
fragment T : [tT] ;
fragment U : [uU] ;
fragment V : [vV] ;
fragment W : [wW] ;
fragment X : [xX] ;
fragment Y : [yY] ;
fragment Z : [zZ] ;