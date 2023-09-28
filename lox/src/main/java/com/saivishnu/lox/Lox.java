package com.saivishnu.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError = false;

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
        if (hadError)
            System.exit(65);
    }

    // open a interactive prompt
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null || line.equals("exitConsole"))
                break;
            run(line);
            hadError = false;
        }
    }

    // run the source code
    private static void run(String source) {
        Scanner sc = new Scanner(source);
        List<Token> tokens = sc.scanTokens();

        // print the tokens
        // for (Token token : tokens)
        // System.out.println(token);

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        // if there was a syntax error, stop execution
        if (hadError)
            return;

        // print the expression using AstPrinter
        System.out.println(new AstPrinter().print(expression));
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
}