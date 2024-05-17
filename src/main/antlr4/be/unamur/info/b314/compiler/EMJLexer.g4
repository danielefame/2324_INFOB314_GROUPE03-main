lexer grammar EMJLexer;

// Tokens
VIRGULE : ',';
QUOTE : '"';
APOSTROPHE : '\'';
ACCOLADE1 : '[';
ACCOLADE2 : ']';
PAR1 : '(';
PAR2 : ')';
EQUAL : '=';
SEMICOLON : ';';
MINUS : '-';
EPSILON : 'ε';
CROCHET1 : '{';
CROCHET2 : '}';
PLUS : '+';
TIMES : '*';
DIVIDE : '/' | '%';
EXC : '!';
BIGGER : '>';
SMALLER : '<';
BIGGEREQUAL : '>=';
SMALLEREQUAL : '<=';
EQUALS : '==';
VARIOUS : '!=';
AND : '&&';
OR : '||';
LETTER : [a-zA-Z.];
LETTERS : [.A-Za-z]+;
NUMBER : [0-9]+;

//Declarations fonction
RETURN : '\u{21A9}' '\u{FE0F}'; //↩️
VOID : '\u{1F300}'; //🌀

//Carte
PLAYER : '\u{1F694}';//🚔
ROAD : '\u{1F6E3}' '\u{FE0F}';//🛣

//Obstacle
VOLCANO : '\u{1F30B}';//🌋
HOUSES : '\u{1F3D8}' '\u{FE0F}';//🏘️
WORKS : '\u{1F6A7}'; //🚧
TRACTOR : '\u{1F69C}';//🚜
WATERPOINT : '\u{1F30A}';//🌊

//Voleur
THIEF : '\u{1F9B9}';//🦹

//Commentaire
ONELINECOMMENT: '\u{1F4E2}';//📢
STARTCOMMENT: '\u{1F50A}';//🔊
ENDCOMMENT: '\u{1F508}';//🔈

//Identifiant
CAR : '\u{1F697}';//🚗
DASHINGAWAY : '\u{1F4A8}' '\u{FE0F}';//💨️
POLICEMAN : '\u{1F46E}';//👮
MAP : '\u{1F5FA}''\u{FE0F}';//🗺️

// Type variable
INT : '\u{1F522}';//🔢
CHAR : '\u{1F523}';//ℹ
STR : '\u{1F521}';//
BOOL : '\u{1F51F}';//🔟
TUPLE : '\u{1F465}'; //👥

//Valeur basique
//TRUE : '\u{2714}';//✔️cette version rajoute une erreur
TRUE : '\u{2705}';//✅
FALSE :'\u{274C}';//❌

// Instructions
IF : '\u{1F914}';//🤔
ELSE :'\u{1F928}';//🤨
SKIPELSE : '\u{1F447}';//👇
WHILE : '\u{267E}' '\u{FE0F}';//♾️
FOR : '\u{1F501}'; //🔁
UP : '\u{2B06}' '\u{FE0F}';//⬆️
DOWN :'\u{2B07}' '\u{FE0F}';//⬇️
RIGHT : '\u{27A1}' '\u{FE0F}';//➡️
LEFT : '\u{2B05}''\u{FE0F}';//⬅️
LUX : '\u{1F6A8}'; //🚨
SOUND : '\u{1F4FB}'; //📻
STOPTHIEF : '\u{270B}';//✋

//Emojis additionnels
IMPORT : '\u{1F4E6}'; //📦
MAIN : '\u{1F3E0}';//🏠

//Expression gauche
EMOJIZERO :  '\u{30}' '\u{FE0F}';  //0️
EMOJIONE : '\u{31}' '\u{FE0F}';// ️1️

// identifier emoji ??
Identifier: [\p{Emoji}]+;

// Add your lexer grammar here
SPACE : [\t\r\n ] -> skip;
NEWLINE : '\r'? '\n' -> skip;

// Comment
COMMENT: ((STARTCOMMENT ~('\r' | '\n' | '\u{1F508}')* ENDCOMMENT) | (ONELINECOMMENT ~('\r' | '\n')*)) -> skip; // Skip ignores COMMENT in grammar
COMMENTEMPTY: (ONELINECOMMENT ('\r' | '\n')*) -> skip; // Skip ignores COMMENT EMPTY in grammar
COMMENT_CONTENT: STARTCOMMENT .*? ENDCOMMENT -> skip;
