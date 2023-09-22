package com.saivishnu.lox;

import java.util.List;

import static com.saivishnu.lox.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
}
