package mini;

import java.util.List;
import java.util.Scanner;

/**
 * COSC3127 Programming Languages - Assignment 1
 *
 * Simple driver program:
 *  - reads Mini source code
 *  - runs the Lexer (Phase 1)
 *  - runs the Parser (Phase 2)
 *  - prints the resulting AST
 */
public class MiniCompiler {

    public static void main(String[] args) {
        String sourceCode;

        if (args.length > 0) {
            // If code is passed via command-line arguments, join them with spaces.
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(" ");
                sb.append(args[i]);
            }
            sourceCode = sb.toString();
        } else {
            // Otherwise, read from standard input.
            System.out.println("Enter Mini program (Ctrl+D to finish):");
            Scanner scanner = new Scanner(System.in);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append("\n");
            }
            sourceCode = sb.toString();
        }

        try {
            // Phase 1: Lexical Analysis (your existing Lexer)
            Lexer lexer = new Lexer(sourceCode);
            List<Token> tokens = lexer.tokenize();

            // Phase 2: Syntax Analysis (new Parser)
            Parser parser = new Parser(tokens);
            ProgramNode program = parser.parseProgram();

            // Output AST
            System.out.println("=== Abstract Syntax Tree ===");
            ASTPrinter.print(program);
            System.out.println("\nProgram is syntactically correct.");

        } catch (RuntimeException e) {
            System.err.println("Compilation error: " + e.getMessage());
        }
    }
}
