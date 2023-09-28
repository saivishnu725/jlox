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

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case BANG:
                // if the right is truthy, return false, else return true
                return !isTruthy(right);
            case MINUS:
                // negate the value by converting it to a double
                return -(double) right;
        }
        return null;
    }

    private boolean isTruthy(Object object) {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (boolean) object;
        return true;
    }
}
