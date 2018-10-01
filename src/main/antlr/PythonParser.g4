parser grammar PythonParser;

options {
  tokenVocab = PythonLexer;
}

single_input
 : NEWLINE
 | simple_stmt
 | compound_stmt NEWLINE
 ;

file_input
 : ( NEWLINE | stmt )* EOF
 ;

eval_input
 : testlist NEWLINE* EOF
 ;

decorator
 : AT dotted_name (OPEN_PAREN arglist? CLOSE_PAREN)? NEWLINE
 ;

decorators
 : decorator+
 ;

decorated
 : decorators (classdef | funcdef | async_funcdef)
 ;

async_funcdef
 : ASYNC funcdef
 ;

funcdef
 : DEF NAME parameters (ARROW test)? COLON suite
 ;

parameters
 : OPEN_PAREN typedargslist? CLOSE_PAREN
 ;

typedargslist
 : tfpdef (ASSIGN test)? (COMMA tfpdef (ASSIGN test)?)* (COMMA (STAR tfpdef? (COMMA tfpdef (ASSIGN test)?)* (COMMA (POWER tfpdef COMMA?)?)? | POWER tfpdef COMMA?)?)?
  | STAR tfpdef? (COMMA tfpdef (ASSIGN test)?)* (COMMA (POWER tfpdef COMMA?)?)?
  | POWER tfpdef COMMA?
  ;

tfpdef
 : NAME (COLON test)?
 ;

varargslist
 : vfpdef (ASSIGN test)? (COMMA vfpdef (ASSIGN test)?)* (COMMA (STAR vfpdef? (COMMA vfpdef (ASSIGN test)?)* (COMMA (POWER vfpdef COMMA?)?)? | POWER vfpdef COMMA?)?)?
 | STAR vfpdef? (COMMA vfpdef (ASSIGN test)?)* (COMMA (POWER vfpdef COMMA?)?)?
 | POWER vfpdef COMMA?
 ;

vfpdef
 : NAME
 ;

stmt
 : simple_stmt
 | compound_stmt
 ;

simple_stmt
 : small_stmt (SEMI_COLON small_stmt)* SEMI_COLON? NEWLINE
 ;

small_stmt
 : expr_stmt
 | del_stmt
 | pass_stmt
 | flow_stmt
 | import_stmt
 | global_stmt
 | nonlocal_stmt
 | assert_stmt
 ;

expr_stmt
 : testlist_star_expr (annassign | augassign (yield_expr | testlist)
 | (ASSIGN ( yield_expr | testlist_star_expr))*)
 ;

annassign
  : COLON test (ASSIGN test)?
  ;

testlist_star_expr
 : ( test | star_expr) (COMMA (test | star_expr))* COMMA?
 ;

augassign
 : ADD_ASSIGN
 | SUB_ASSIGN
 | MULT_ASSIGN
 | AT_ASSIGN
 | DIV_ASSIGN
 | MOD_ASSIGN
 | AND_ASSIGN
 | OR_ASSIGN
 | XOR_ASSIGN
 | LEFT_SHIFT_ASSIGN
 | RIGHT_SHIFT_ASSIGN
 | POWER_ASSIGN
 | IDIV_ASSIGN
 ;

del_stmt
 : DEL exprlist
 ;

pass_stmt
 : PASS
 ;

flow_stmt
 : break_stmt
 | continue_stmt
 | return_stmt
 | raise_stmt
 | yield_stmt
 ;

break_stmt
 : BREAK
 ;

continue_stmt
 : CONTINUE
 ;

return_stmt
 : RETURN testlist?
 ;

yield_stmt
 : yield_expr
 ;

raise_stmt
 : RAISE (test (FROM test)?)?
 ;

import_stmt
 : import_name
 | import_from
 ;

import_name
 : IMPORT dotted_as_names
 ;

import_from
 : FROM ((DOT | ELLIPSIS)* dotted_name | (DOT | ELLIPSIS)+)
   IMPORT (STAR | OPEN_PAREN import_as_names CLOSE_PAREN | import_as_names)
 ;

import_as_name
 : NAME (AS NAME)?
 ;

dotted_as_name
 : dotted_name (AS NAME)?
 ;

import_as_names
 : import_as_name (COMMA import_as_name)* COMMA?
 ;

dotted_as_names
 : dotted_as_name (COMMA dotted_as_name)*
 ;

dotted_name
 : NAME (DOT NAME)*
 ;

global_stmt
 : GLOBAL NAME (COMMA NAME)*
 ;

nonlocal_stmt
 : NONLOCAL NAME (COMMA NAME)*
 ;

assert_stmt
 : ASSERT test (COMMA test)?
 ;

compound_stmt
 : if_stmt
 | while_stmt
 | for_stmt
 | try_stmt
 | with_stmt
 | funcdef
 | classdef
 | decorated
 | async_stmt;

async_stmt
 : ASYNC (funcdef | with_stmt | for_stmt)
 ;

if_stmt
 : IF test COLON suite (ELIF test COLON suite)* (ELSE COLON suite)?
 ;

while_stmt
 : WHILE test COLON suite (ELSE COLON suite)?
 ;

for_stmt
 : FOR exprlist IN testlist COLON suite (ELSE COLON suite)?
 ;

try_stmt: (TRY COLON suite ((except_clause COLON suite)+
          (ELSE COLON suite)?
          (FINALLY COLON suite)?
          |FINALLY COLON suite))
;

with_stmt
 : WITH with_item (COMMA with_item)* COLON suite
 ;

with_item
 : test (AS expr)?
 ;

except_clause
 : EXCEPT (test (AS NAME)?)?
 ;

suite
 : simple_stmt | NEWLINE INDENT stmt+ DEDENT
 ;

test
 : or_test (IF or_test ELSE test)?
 | lambdef
 ;

test_nocond
 : or_test
 | lambdef_nocond
 ;

lambdef
 : LAMBDA varargslist? COLON test
 ;

lambdef_nocond
 : LAMBDA varargslist? COLON test_nocond
 ;

or_test
 : and_test (OR and_test)*
 ;

and_test
 : not_test (AND not_test)*
 ;

not_test
 : NOT not_test
 | comparison
 ;

comparison
 : expr (comp_op expr)*
 ;

comp_op
 : LESS_THAN
 | GREATER_THAN
 | EQUALS
 | GT_EQ
 | LT_EQ
 | NOT_EQ_1
 | NOT_EQ_2
 | IN
 | NOT IN
 | IS
 | IS NOT
 ;

star_expr
 : STAR expr
 ;

expr
 : xor_expr (OR_OP xor_expr)*
 ;

xor_expr
 : and_expr (XOR and_expr)*
 ;

and_expr
 : shift_expr (AND_OP shift_expr)*
 ;

shift_expr
 : arith_expr ((LEFT_SHIFT | RIGHT_SHIFT) arith_expr)*
 ;

arith_expr
 : term ((ADD | MINUS) term)*
 ;

term
 : factor ((STAR | AT | DIV | MOD | IDIV) factor)*
 ;

factor
 : (ADD | MINUS | NOT_OP) factor | power
 ;

power
 : atom_expr (POWER factor)?
 ;

atom_expr
 : AWAIT? atom trailer*
 ;

atom
 : OPEN_PAREN (yield_expr | testlist_comp)? CLOSE_PAREN
 | OPEN_BRACK testlist_comp? CLOSE_BRACK
 | OPEN_BRACE dictorsetmaker? CLOSE_BRACE
 | NAME
 | number
 | str+
 | ELLIPSIS
 | NONE
 | TRUE
 | FALSE
 ;

testlist_comp
 : (test | star_expr) (comp_for | (COMMA (test | star_expr))* COMMA?)
 ;

trailer
 : OPEN_PAREN arglist? CLOSE_PAREN
 | OPEN_BRACK subscriptlist CLOSE_BRACK
 | DOT NAME
 ;

subscriptlist
 : subscript (COMMA subscript)* COMMA?
 ;

subscript
 : test
 | test? COLON test? sliceop?
 ;

sliceop
 : COLON test?
 ;

exprlist
 : (expr | star_expr) (COMMA (expr | star_expr))* COMMA?
 ;

testlist
 : test (COMMA test)* COMMA?
 ;

dictorsetmaker
 : ((test COLON test | POWER expr)
    (comp_for | (COMMA (test COLON test | POWER expr))* COMMA?)) |
   ((test | star_expr)
    (comp_for | (COMMA (test | star_expr))* COMMA?))
 ;

classdef
 : CLASS NAME (OPEN_PAREN arglist? CLOSE_PAREN)? COLON suite
 ;

arglist
 : argument (COMMA argument)*  COMMA?
 ;

argument
 : test comp_for?
 | test ASSIGN test
 | POWER test
 | STAR test
 ;

comp_iter
 : comp_for
 | comp_if
 ;

comp_for
 : ASYNC? FOR exprlist IN or_test comp_iter?
 ;

comp_if
 : IF test_nocond comp_iter?
 ;

yield_expr
 : YIELD yield_arg?
 ;

yield_arg
 : FROM test
 | testlist
 ;

str
 : STRING_LITERAL
 | BYTES_LITERAL
 ;

number
 : integer
 | FLOAT_NUMBER
 | IMAG_NUMBER
 ;

integer
 : DECIMAL_INTEGER
 | OCT_INTEGER
 | HEX_INTEGER
 | BIN_INTEGER
 ;
