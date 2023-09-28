package com.saivishnu.lox;

class RuntimeError extends RuntimeException {

    // final Token token;
    final transient Token token;

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
