// TODO: comments everywhere
package com.saivishnu.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.saivishnu.lox.TokenType.*;

public class Scanner {

    private int start = 0;
    private int current = 0;
    private int line = 1;
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            // match because of logical operators.
            // This will check for next character and then decide whether it is a special
            // single char lexeme or two char.
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            // comments or division expression.
            case '/':
                if (match('/')) {
                    // if it is a "//" then go until the end of the line
                    while (peek() != '\n' && !isAtEnd())
                        // then ignore it without adding.
                        advance();
                } else
                    addToken(SLASH); // it is a division.
                break;
            // ignore things like ' ', '\r' and '\t'
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                // increase line count. useful for showing line number in error messages.
                line++;
                break;
            case '"':
                string();
                break;
            default:
                Lox.error(line, "Unexpected token.");
                break;
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        // returns next character and increments the current position.
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    private char peek() {
        // same as advance but wont increment the current position
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            // allows multi-line strings, it increments the line number to account for that.
            if (peek() == '\n')
                line++;
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "String not closed properly");
            return;
        }

        // advance the closing "
        advance();

        // save value without the leading and trailing quote " "
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }
}
