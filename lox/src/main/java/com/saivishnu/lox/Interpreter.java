package com.saivishnu.lox;

/**
 * The Interpreter class implements the Expr.Visitor interface and is
 * responsible for evaluating expressions.
 * It contains methods to evaluate literal, grouping, unary and binary
 * expressions.
 */
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
                // check if right is a number or not
                checkNumberOperand(expr.operator, right);
                // negate the value by converting it to a double
                return -(double) right;
            default:
                break;
        }
        return null;
    }

    // check if one operand is a number or not
    private void checkNumberOperand(Token operator, Object operand) {
        // if it is a number, then return nothing
        if (operand instanceof Double)
            return;
        // else throw a runtime error
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    // check if both left and right are a number or not
    private void checkNumberOperands(Token operator, Object left, Object right) {
        // if both are numbers, then return nothing
        if (left instanceof Number && right instanceof Number)
            return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private boolean isTruthy(Object object) {
        // everything that is a not a null or bool false is true.
        if (object == null)
            return false;
        // if it is a boolean, just convert it from object to bool and return
        if (object instanceof Boolean)
            return (boolean) object;
        // anything else is true
        return true;
    }

    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        /*
         * comparison >, >=, <, <=
         * arithmetic -, + (num, str), /, *
         * not-equals or equals comparison !=, ==
         * in the same order to preserve precedence
         */
        switch (expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                    return (double) left + (double) right;
                if (left instanceof String && right instanceof String)
                    return (String) left + (String) right;
                return null;
            case SLASH:
                return (double) left / (double) right;
            case STAR:
                return (double) left * (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            default:
                break;
        }
        return null;
    }

    private boolean isEqual(Object a, Object b) {
        // if both are null, then return true
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;

        // if both are not null then compare
        return a.equals(b);
    }
}
