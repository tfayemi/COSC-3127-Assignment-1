package mini;

import java.util.ArrayList;
import java.util.List;

/**
 * COSC3127 Programming Languages - Assignment 1
 *
 * Syntax Analyzer (Parser) for the Mini language.
 *
 * Grammar:
 *
 *   Program        -> StatementList
 *   StatementList  -> Statement StatementList | ε
 *   Statement      -> IDENTIFIER ASSIGNMENT Expression
 *
 *   Expression     -> Term Expression'
 *   Expression'    -> (+ | -) Term Expression' | ε
 *
 *   Term           -> Factor Term'
 *   Term'          -> (* | /) Factor Term' | ε
 *
 *   Factor         -> Primary Factor'
 *   Factor'        -> ^ Factor | ε
 *
 *   Primary        -> IDENTIFIER | INTEGER | REAL
 */
public class Parser {

    private final List<Token> tokens;
    private int position = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Entry point: parse the entire program.
     */
    public ProgramNode parseProgram() {
        int line = 1;
        int column = 1;

        if (!tokens.isEmpty()) {
            line = tokens.get(0).getLine();
            column = tokens.get(0).getColumn();
        }

        List<StatementNode> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(parseStatement());
        }

        return new ProgramNode(statements, line, column);
    }

    /**
     * Statement -> IDENTIFIER ASSIGNMENT Expression
     */
    private StatementNode parseStatement() {
        Token identifier = consume(Token.Type.IDENTIFIER,
                "Expected identifier at the start of a statement");

        consume(Token.Type.ASSIGNMENT,
                "Expected ':=' after identifier '" + identifier.getValue() + "'");

        ExpressionNode expr = parseExpression();

        return new AssignmentNode(
                identifier.getValue(),
                expr,
                identifier.getLine(),
                identifier.getColumn()
        );
    }

    /**
     * Expression -> Term ((+ | -) Term)*
     */
    private ExpressionNode parseExpression() {
        ExpressionNode left = parseTerm();

        while (matchOperator("+", "-")) {
            Token op = previous();
            ExpressionNode right = parseTerm();
            left = new BinaryExpressionNode(
                    left,
                    op.getValue(),
                    right,
                    op.getLine(),
                    op.getColumn()
            );
        }

        return left;
    }

    /**
     * Term -> Factor ((* | /) Factor)*
     */
    private ExpressionNode parseTerm() {
        ExpressionNode left = parseFactor();

        while (matchOperator("*", "/")) {
            Token op = previous();
            ExpressionNode right = parseFactor();
            left = new BinaryExpressionNode(
                    left,
                    op.getValue(),
                    right,
                    op.getLine(),
                    op.getColumn()
            );
        }

        return left;
    }

    /**
     * Factor -> Primary (^ Factor)?
     *
     * Exponentiation is right-associative, so we recursively call parseFactor().
     */
    private ExpressionNode parseFactor() {
        ExpressionNode base = parsePrimary();

        if (matchOperator("^")) {
            Token op = previous();
            ExpressionNode exponent = parseFactor();
            return new BinaryExpressionNode(
                    base,
                    op.getValue(),
                    exponent,
                    op.getLine(),
                    op.getColumn()
            );
        }

        return base;
    }

    /**
     * Primary -> IDENTIFIER | INTEGER | REAL
     */
    private ExpressionNode parsePrimary() {
        if (match(Token.Type.INTEGER)) {
            Token number = previous();
            return new NumberLiteralNode(
                    number.getValue(),
                    false,
                    number.getLine(),
                    number.getColumn()
            );
        }

        if (match(Token.Type.REAL)) {
            Token number = previous();
            return new NumberLiteralNode(
                    number.getValue(),
                    true,
                    number.getLine(),
                    number.getColumn()
            );
        }

        if (match(Token.Type.IDENTIFIER)) {
            Token id = previous();
            return new IdentifierExpressionNode(
                    id.getValue(),
                    id.getLine(),
                    id.getColumn()
            );
        }

        // If we reach here we have no valid primary
        if (isAtEnd()) {
            throw new ParserException("Unexpected end of input while parsing expression");
        }

        throw error(current(), "Expected expression (identifier or number)");
    }

    // ---------- Helper methods ----------

    private boolean isAtEnd() {
        return position >= tokens.size();
    }

    private Token current() {
        if (tokens.isEmpty()) {
            throw new ParserException("Unexpected end of input at start of file");
        }
        if (isAtEnd()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(position);
    }

    private Token previous() {
        return tokens.get(position - 1);
    }

    private boolean match(Token.Type type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean check(Token.Type type) {
        if (isAtEnd()) return false;
        return tokens.get(position).getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) position++;
        return previous();
    }

    private Token consume(Token.Type type, String message) {
        if (check(type)) return advance();
        throw error(current(), message);
    }

    /**
     * Match an OPERATOR token whose lexeme is one of the provided strings.
     */
    private boolean matchOperator(String... ops) {
        if (isAtEnd()) return false;

        Token tok = tokens.get(position);
        if (tok.getType() != Token.Type.OPERATOR) {
            return false;
        }

        for (String op : ops) {
            if (tok.getValue().equals(op)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private ParserException error(Token token, String message) {
        String location = " at line " + token.getLine() + ", column " + token.getColumn();
        return new ParserException(message + location);
    }
}
