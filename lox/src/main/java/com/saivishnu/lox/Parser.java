package com.saivishnu.lox;

import java.util.ArrayList;
import java.util.List;

import static com.saivishnu.lox.TokenType.*;

public class Parser {

    // parser related error handling class
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /*
     * // initial method to start the parser
     * public Expr parse() {
     * initial code used to parse the expression.
     * replaced with a expression + statement parser call below
     * try {
     * return expression();
     * } catch (ParseError error) {
     * return null;
     * }
     * }
     */

    // initial method to start the parser
    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(statement());
        }
        return statements;
    }

    // check which statement is being passed and call the respective
    private Stmt statement() {
        if (match(PRINT))
            return printStatement();
        return expressionStatement();
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
     * ********** Methods that check the equation follow these steps **************
     * first assume that it isn't the current condition and then check for it,
     * if it is a match then make it the operator,
     * then assume the right side of it is the next precedence.
     */

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        // !=, ==
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        // <, <=, >, >=
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {
        // -, +
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        // /, *
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        // !, -
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            // return directly as it is either Unary or primary values
            return new Expr.Unary(operator, right);
        }
        // if not unary, it is primary
        return primary();
    }

    private Expr primary() {
        // NUMBER, STRING, "true", "false", "nil", "(", expression, ")"
        if (match(FALSE))
            return new Expr.Literal(false);
        if (match(TRUE))
            return new Expr.Literal(true);
        if (match(NIL))
            return new Expr.Literal(null);

        if (match(NUMBER, STRING))
            return new Expr.Literal(previous().literal);

        if (match(LEFT_PAREN)) {
            // create another expression inside the (here),
            // then check if it is a ")" and give an error if it isn't.
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        // if none of the above primary or grouping, throw an error
        throw error(peek(), "Expect expression.");
    }

    // used to check if the operator is same as expected
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    // perform a check and send a error if it isn't
    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();

        throw error(peek(), message);
    }

    // gets current and checks with expected one
    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    // go to next one
    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    // check end of token stream
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    // get the current token without going to next one
    private Token peek() {
        return tokens.get(current);
    }

    // get previous token without going to previous one
    private Token previous() {
        return tokens.get(current - 1);
    }

    // error handling
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    /*
     * if an error was found then peek until next statement
     * to reduce the number of errors (cascaded errors)
     */
    private void synchronize() {
        // go to next token
        advance();

        while (!isAtEnd()) {
            // if it is at the end of tokens, stop skipping
            if (previous().type == SEMICOLON)
                return;
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    // if it is the next statement, stop skipping
                    return;
            }
        }
    }

}
