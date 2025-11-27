package mini;

/**
 * COSC3127 Programming Languages - Assignment 1
 *
 * Custom exception type for syntax errors in the Mini parser.
 */
public class ParserException extends RuntimeException {
    public ParserException(String message) {
        super(message);
    }
}
