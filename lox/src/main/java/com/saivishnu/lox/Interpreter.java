package com.saivishnu.lox;

class Interpreter implements Expr.Visitor<Object> {

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        // return the value which is already stored
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        // since the inside of a group () is another expression, call the entire thing
        // again
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        // call the visitor again to evaluate the inside expression
        return expr.accept(this);
    }

}
