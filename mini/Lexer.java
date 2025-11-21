package mini;

import java.util.ArrayList;
import java.util.List;

/**
 * COSC3127 Programming Languages - Assignment 1
 * Lexical Analyzer
 * Utilizes Explicit DFA object implimentation to tokenize
 * input source code strings. 
 * Variable names must start with an alphabetic character followed by
 * alphanumeric characters or underscores.
 * No whitespace is allowed in the variable names.
 * Variable names must not be reserved words or keywords.
 */

public class Lexer {

    private final String src; // Source code to be tokenized
    private int idx = 0; // Current index in the source code
    private int line = 1; // Current line number for error reporting
    private int col = 1; // Current column number for error reporting

    //DFA Suite
    private final DFA identifierDFA;
    private final DFA integerDFA;
    private final DFA realDFA;
    private final DFA operatorDFA;
    private final DFA assignmentDFA;

    /**
     * Constructor for Lexer
     * @param src Source code string to be tokenized
     */

    public Lexer(String src) {
        this.src = src;

        // Initialize DFAs
        this.identifierDFA = DFA.createIdentifierDFA();
        this.integerDFA = DFA.createIntegerDFA();
        this.realDFA = DFA.createRealDFA();
        this.operatorDFA = DFA.createOperatorDFA();
        this.assignmentDFA = DFA.createAssignmentOperatorDFA();

    }
    // Peeks at the current character without advancing the index
    private char peek() {
        return idx < src.length() ? src.charAt(idx) : '\0';
        }

    private char peekNext() {
        return (idx + 1) < src.length() ? src.charAt(idx + 1) : '\0';
    }

    private char advance() {
        char current = peek();
        idx++;
        if (current == '\n') {
            line++;
            col = 1;
        } else{
            col++;
        }
        return current;
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(peek())) {
            advance();
        }
    }
/**
 *  Main tokenize method using DFAs
 * @return List of tokens extracted from the source code
 */

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (idx < src.length()) {
            skipWhitespace();
            int startIdx = idx;
            int startLine = line;
            int startCol = col;

            Token token = null;

            /**
             * Try matching each DFA in order of precedence
             */
            token = tryMatchDFA(identifierDFA, Token.Type.IDENTIFIER, startIdx, startLine, startCol);
            if (token == null) {
                token = tryMatchDFA(realDFA, Token.Type.REAL, startIdx, startLine, startCol);
            }
            if (token == null) {
                token = tryMatchDFA(integerDFA, Token.Type.INTEGER, startIdx, startLine, startCol);
            }
            if (token == null) {
                token = tryMatchDFA(assignmentDFA, Token.Type.ASSIGNMENT, startIdx, startLine, startCol);
            }
            if (token == null) {
                token = tryMatchDFA(operatorDFA, Token.Type.OPERATOR, startIdx, startLine, startCol);
            }

            if (token != null) {
                tokens.add(token);
            } else {
                throw new RuntimeException(String.format("Lexical error at " + line + ":" + col + 
                                     " -> Illegal character: '" + peek() + "'")); //No DFA matched - illegal character error
            }
        }

        return tokens;
    }

    /**
     * Try to match a DFA starting at the current position
     * @param dfa The DFA to use for matching
     * @param type The token type to create if match is successful
     * @param startIdx The starting index in the source code
     * @param startLine The starting line number
     * @param startCol The starting column number
     * @return A Token if match is successful, null otherwise
     */
    private Token tryMatchDFA(DFA dfa, Token.Type type, int startIdx, int startLine, int startCol) {
        int matchLen = dfa.matchLength(src, startIdx);
        
        if (matchLen > 0) {
            String lexeme = src.substring(startIdx, startIdx + matchLen);
            idx = startIdx + matchLen; // Advance index by match length
            
            // Update line and column tracking
            for (int i = 0; i < matchLen; i++) {
                char c = src.charAt(startIdx + i);
                if (c == '\n') {
                    line++;
                    col = 1;
                } else {
                    col++;
                }
            }
            
            return new Token(type, lexeme, startLine, startCol);
        }
        
        return null; // No match found
    }
}