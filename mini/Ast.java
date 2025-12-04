package mini;

import java.util.List;

/**
 * Base class for all AST nodes.
 * Stores source position (line, column) for better error reporting.
 */
public abstract class ASTNode {
    private final int line;
    private final int column;

    protected ASTNode(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}

/**
 * Base class for all statement nodes.
 */
abstract class StatementNode extends ASTNode {
    protected StatementNode(int line, int column) {
        super(line, column);
    }
}

/**
 * Root node: represents a complete Mini program.
 */
class ProgramNode extends ASTNode {
    private final List<StatementNode> statements;

    public ProgramNode(List<StatementNode> statements, int line, int column) {
        super(line, column);
        this.statements = statements;
    }

    public List<StatementNode> getStatements() {
        return statements;
    }
}

/**
 * Base class for all expressions.
 */
abstract class ExpressionNode extends ASTNode {
    protected ExpressionNode(int line, int column) {
        super(line, column);
    }
}

/**
 * Assignment: identifier := expression
 */
class AssignmentNode extends StatementNode {
    private final String identifier;
    private final ExpressionNode expression;

    public AssignmentNode(String identifier,
                          ExpressionNode expression,
                          int line,
                          int column) {
        super(line, column);
        this.identifier = identifier;
        this.expression = expression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ExpressionNode getExpression() {
        return expression;
    }
}

/**
 * Binary expression: left (operator) right
 * For example: a + b, x * y, 2 ^ 3.
 */
class BinaryExpressionNode extends ExpressionNode {
    private final ExpressionNode left;
    private final String operator;
    private final ExpressionNode right;

    public BinaryExpressionNode(ExpressionNode left,
                                String operator,
                                ExpressionNode right,
                                int line,
                                int column) {
        super(line, column);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public ExpressionNode getRight() {
        return right;
    }
}

/**
 * Number literal (integer or real).
 */
class NumberLiteralNode extends ExpressionNode {
    private final String lexeme;
    private final boolean isReal;

    public NumberLiteralNode(String lexeme, boolean isReal, int line, int column) {
        super(line, column);
        this.lexeme = lexeme;
        this.isReal = isReal;
    }

    public String getLexeme() {
        return lexeme;
    }

    public boolean isReal() {
        return isReal;
    }
}

/**
 * Identifier expression: use of a variable name in an expression.
 */
class IdentifierExpressionNode extends ExpressionNode {
    private final String name;

    public IdentifierExpressionNode(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
