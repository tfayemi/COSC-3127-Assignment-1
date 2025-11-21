package mini;

/**
 * COSC3127 Programming Languages - Assignment 1
 * Lexical Analyzer
 * Token class representing a lexical token with type, value, and position.
 */
public class Token {
    public enum Type {
        IDENTIFIER,
        INTEGER,
        REAL,
        OPERATOR,
        KEYWORD,
        ASSIGNMENT,
        //Add other token types as needed
    }

    private final Type type;
    private final String value;
    private final int line;
    private final int column;

    public Token(Type type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("Token[type=%s, value=%s, line=%d, column=%d]", type, value, line, column);
    }
    
}
