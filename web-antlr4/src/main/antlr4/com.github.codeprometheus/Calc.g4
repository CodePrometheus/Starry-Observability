grammar Calc;
// service_relation_client_cpm = from(ServiceRelation.*).filter(detectPoint == DetectPoint.CLIENT).cpm();
// variable                    EQ  FROM LR fromSource RE filterStatement DOT aggregateFunction

root
    : aggregationStatement
    ;

aggregationStatement
    : variable (SPACE)? EQ (SPACE)? metricStatement
    ;
    
variable
    : LETTER
    ;

metricStatement
    : FROM LE fromSource RE filterStatement DOT aggregateFunction
    ;
    
fromSource
    : LETTER DOT ALL
    ;

filterStatement
    : DOT FILTER LE filterExpr RE
    ;
    
filterExpr
    : conditionAttr ((DOT conditionAttr)*) (SPACE)? BAL (SPACE)? conditionAttr ((DOT conditionAttr)*)
    ;

conditionAttr
    : LETTER
    ;

aggregateFunction
    : LETTER LE RE;

DOT: '.';
BAL: '==';
FILTER: 'filter';
ALL: '*';
FROM: 'from';
LE: '(';
RE: ')';
EQ: '=';
SPACE: [ \t\r\n]+ -> skip;
LETTER: [a-zA-Z_]*;