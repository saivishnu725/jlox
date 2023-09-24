package com.saivishnu.lox;

import java.util.List;

import static com.saivishnu.lox.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /*
     * it follows the precedency rule.
     * equality
     * comparison
     * addition, subtraction -> term
     * division, multiplication -> factor
     * unary operators
     * primary
     */

    /*
     * expr -> equality
     * equality -> comparison ( ( "!=" | "==" ) comparison )*
     * comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )*
     * term -> factor ( ( "-" | "+" ) factor )*
     * factor -> unary ( ( "/" | "*" ) unary )*
     * unary -> ( "!" | "-" ) unary | primary
     * primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")"
     */

    /*
     * first assume that it isn't the current condition and then check for it,
     * if it is a match then make it the operator,
     * then assume the right side of it is the next precedence.
     */

    private Expr expression() {
        // 1. check for equality
        return equality();
    }

    private Expr equality() {
        // !=, ==
        // just assume that it isn't equal
        Expr expr = comparison();

        // check for equality
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token opeator = previous();
            // if it is equal then assume the rest is comparison and call it.
            Expr right = comparison();
            expr = new Expr.Binary(expr, opeator, right);
        }
        return expr;
    }

    private Expr comparison() {
        // <, <=, >, >=
        // assume that it is isn't comparison
        Expr expr = term();

        // check for comparison
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            // next in precedence is +, -
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private Token isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
