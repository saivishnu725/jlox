package com.saivishnu.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    // interpreter object
    private static final Interpreter interpreter = new Interpreter();
    // syntax error flag
    static boolean hadError = false;
    // runtime error flag
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        // check if there there than more than one input file
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) // if only one file is sent, parse it
            runFile(args[0]);
        else
            runPrompt(); // if not, open a interactive prompt
    }

    // read the file and run it
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        // syntax error in file
        if (hadError)
            System.exit(65);
        // runtime error in file
        if (hadRuntimeError)
            System.exit(70);
    }

    // open a interactive prompt
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            // a way to come out of the interactive prompt
            if (line == null || line.equals("exit()"))
                break;
            run(line);
            hadError = false;
            /*
             * nothing is done about the runtime error
             * as we do not want to stop interactive prompt for a runtime error.
             */
        }
    }

    // run the source code
    private static void run(String source) {
        Scanner sc = new Scanner(source);
        List<Token> tokens = sc.scanTokens();

        // two sample code to either print all the token or the syntax tree
        // METHOD 1:
        // print the tokens
        // for (Token token : tokens)
        // System.out.println(token);

        // METHOD 2:
        // print the expression using AstPrinter
        // System.out.println(new AstPrinter().print(expression));

        // parse the tokens
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        // if there was a syntax error, stop execution
        if (hadError)
            return;

        // call the interpreter to interpret the expression
        interpreter.interpret(expression);
    }

    // error handling for individual lines
    static void error(int line, String message) {
        report(line, "", message);
    }

    // error handling with line number and where the error is
    static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error " + where + ": " + message);
        hadError = true;
    }

    // error handling for tokens
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF)
            report(token.line, " at end", message);
        else
            report(token.line, " at '" + token.lexeme + "'", message);
    }

    // error handling for runtime errors
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}