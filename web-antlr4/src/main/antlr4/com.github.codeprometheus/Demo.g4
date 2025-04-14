grammar Demo;

root:   expr ;
expr: expr(PLUS|MINUS)expr
    | INT
    | LE expr RE
    ;

PLUS:   '+';
MINUS:  '-';
INT:    [0-9]+;
LE: '(';
RE: ')';

// 1+2+3
// INT PLUS INT PLUS INT