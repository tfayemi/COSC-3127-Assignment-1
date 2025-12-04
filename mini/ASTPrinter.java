package mini;

/**
 * Utility class to pretty-print the AST as an indented tree.
 * Very useful for debugging and for your class presentation.
 */
public class ASTPrinter {

    public static void print(ProgramNode program) {
        printNode(program, 0);
    }

    private static void printNode(ASTNode node, int indent) {
        String prefix = " ".repeat(indent);

        if (node instanceof ProgramNode) {
            ProgramNode p = (ProgramNode) node;
            System.out.println(prefix + "Program");
            for (StatementNode stmt : p.getStatements()) {
                printNode(stmt, indent + 2);
            }

        } else if (node instanceof AssignmentNode) {
            AssignmentNode a = (AssignmentNode) node;
            System.out.println(prefix + "Assignment: " + a.getIdentifier());
            printNode(a.getExpression(), indent + 2);

        } else if (node instanceof BinaryExpressionNode) {
            BinaryExpressionNode b = (BinaryExpressionNode) node;
            System.out.println(prefix + "BinaryExpr '" + b.getOperator() + "'");
            printNode(b.getLeft(), indent + 2);
            printNode(b.getRight(), indent + 2);

        } else if (node instanceof NumberLiteralNode) {
            NumberLiteralNode n = (NumberLiteralNode) node;
            String type = n.isReal() ? "RealLiteral" : "IntegerLiteral";
            System.out.println(prefix + type + ": " + n.getLexeme());

        } else if (node instanceof IdentifierExpressionNode) {
            IdentifierExpressionNode id = (IdentifierExpressionNode) node;
            System.out.println(prefix + "IdentifierExpr: " + id.getName());

        } else {
            System.out.println(prefix + "Unknown node type: " + node.getClass().getSimpleName());
        }
    }
}
