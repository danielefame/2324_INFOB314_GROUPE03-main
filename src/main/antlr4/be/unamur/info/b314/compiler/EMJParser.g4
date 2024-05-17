
parser grammar EMJParser;
    options { tokenVocab = EMJLexer; }

// Add your parser grammar here
// Everything must emerge from "root"
root : (programme | carte) EOF;

//identifier emoji
tag: Identifier*;

//Programme
programme : IMPORT string run;
run : funcdecl funcdecls*;
funcdecls : SEMICOLON? funcdecl SEMICOLON?;

//Carte
carte : MAP  nbline  nbcolumn direction  elements;
direction : UP | DOWN | LEFT | RIGHT;
nbline : integer;
nbcolumn : integer;
elements : element elements | element+;
element : PLAYER | ROAD | VOLCANO | HOUSES | WORKS | TRACTOR | WATERPOINT | THIEF;

//Commentaire
comment : STARTCOMMENT text ENDCOMMENT | ONELINECOMMENT text;
text : .+?;

//Valeur basique
basicvalue : integer | string | boolean1;
integer : PAR1? MINUS? NUMBER PAR2?;
charvalue : LETTER | NUMBER;
string : QUOTE (characters SPACE*)+ QUOTE | APOSTROPHE charvalue APOSTROPHE;
characters : LETTERS | NUMBER | LETTER;
boolean1 : TRUE | FALSE;

//Tuple
tuple :  EXC EXC? integer EQUALS integer |EXC? PAR1 EXC? EXC? integer VIRGULE integer PAR2  | EXC? PAR1 EXC? EXC? string VIRGULE string PAR2 | EXC? EXC? PAR1 EXC? boolean1 VIRGULE boolean1 PAR2 | EXC? EXC? PAR1 EXC? integer comparator integer PAR2 | PAR1 characters VIRGULE characters PAR2;

//Identifiant de variable
id : ACCOLADE1 emojis ACCOLADE2 | MAIN | SEMICOLON;
emojis : emoji emojis?;
emoji : CAR | DASHINGAWAY | POLICEMAN | Identifier+;

//Type variable
type : emojitype | TUPLE PAR1 emojitype PAR2;
emojitype: INT | CHAR | STR | BOOL | TRUE | FALSE;

//Declaration variable
vardecl : type id  EQUAL varvalue SEMICOLON | type id SEMICOLON | rightexp ;

varvalue : basicvalue | tuple | rightexp;

//Expression droite
rightexp : intexp | callfunc | string | tuple | boolean1;

//Apell de fonction
callfunc : id  PAR1 callfuncargs? PAR2;
callfuncargs : callfuncarg callfuncargscomma | callfuncarg;
callfuncargscomma : VIRGULE callfuncarg callfuncargscomma | VIRGULE callfuncarg;
callfuncarg : rightexp | leftexp;

// Expression entiere
intexp :  intexp operator PAR1? intexp PAR2? | MINUS  intexp  | integer | leftexp | intexp operator PAR1? boolexp PAR2? | PAR1 intexp PAR2;

operator : PLUS | MINUS | TIMES | DIVIDE;

//Epressions booleenne
boolexp :  boolexp comparator boolexp | boolexp connector boolexp  | EXC boolexp | PAR1 boolexp PAR2 | boolean1 | rightexp;
comparator : BIGGER | SMALLER | BIGGEREQUAL | SMALLEREQUAL | EQUALS | VARIOUS | EXC;
connector : AND | OR;

//Expression gauche
leftexp : id | id EMOJIZERO  | id EMOJIONE | type id;

//Instructions
//instruction : ifinstr | loopinstr | callfunc SEMICOLON | prefunc SEMICOLON | affectinstr SEMICOLON ;
instruction : ifinstr | loopinstr | callfunc SEMICOLON | prefunc SEMICOLON | affectinstr SEMICOLON | vardecl;
//Affectation
affectinstr : leftexp EQUAL rightexp | rightexp;

//Selection
ifinstr : IF PAR1 boolexp PAR2 CROCHET1 instruction* CROCHET2 ELSE CROCHET1 elseinstr CROCHET2;
elseinstr: instruction | SKIPELSE SEMICOLON;

//Boucle
loopinstr : WHILE PAR1 boolexp PAR2 CROCHET1 (instruction)+ CROCHET2 | FOR PAR1 intexp PAR2 CROCHET1 (instruction)+ CROCHET2;

//Fonction prédéfinies
prefunc : UP PAR1 intexp PAR2 | DOWN PAR1 intexp PAR2 | RIGHT PAR1 intexp PAR2 | LEFT PAR1 intexp PAR2 | LUX PAR1 PAR2  | SOUND PAR1 PAR2 | STOPTHIEF PAR1 PAR2;

//Declaration de fonction
funcdecl : functype id PAR1 funcparams* PAR2 CROCHET1 instructions* RETURN funcreturn SEMICOLON CROCHET2;
functype : type | VOID;
funcparams : VIRGULE? funcparamsnotempty;
funcparamsnotempty : type id VIRGULE funcparamsnotempty | type id | rightexp;
instructions : vardecl instructions? | instruction SEMICOLON? instructions?;
funcreturn : id | VOID | rightexp | boolmath;
boolmath : PAR1? (expression comparator? expression?)+ PAR2?;
expression : id | integer | boolean1 | PAR1 expression PAR2;
