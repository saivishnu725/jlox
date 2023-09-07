package com.saivishnu.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.saivishnu.lox.TokenType.*;

class Scanner {

    private int start = 0;
    private int current = 0;
    private int line = 1;
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

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
                // if it is not any token or string then search for number or else error it out.
                if (isDigit(c))
                    number();
                else if (isAlphaNumeric(c))
                    identifier();
                else
                    Lox.error(line, "Unexpected token.");
                break;
        }
    }

    private void identifier() {
        // keep advancing until end of the word / identifier
        while (isAlphaNumeric(peek()))
            advance();
        // check if it is a keyword or an identifier and add respectively
        String word = source.substring(start, current);
        TokenType type = keywords.get(word);
        if (type == null)
            type = IDENTIFIER;
        // add the type as a token
        addToken(type); // it is either one of the keywords or IDENTIFIER
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

    // peek ahead by 1 character relative to the current.
    private char peek() {
        // same as advance but wont increment the current position
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    // peek ahead to the 2nd character from current
    private char peekNext() {
        if (current + 1 > source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char value) {
        return value >= '0' && value <= '9';
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

    private void number() {

        while (isDigit(peek()))
            advance();

        // peek again using peekNext() to know if the . is used as floating point and
        // not as
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek()))
                advance();
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }
}
