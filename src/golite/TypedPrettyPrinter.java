package golite;

import golite.type.GoLiteType;
import golite.analysis.*;
import golite.node.*;

import java.util.HashMap;


/**
 * GoLite typed pretty printer.
 */
public class TypedPrettyPrinter extends PrettyPrinter {

    /** Type table. */
    private HashMap<Node, GoLiteType> typeTable;

    /**
     * Constructor.
     */
    public TypedPrettyPrinter(HashMap<Node, GoLiteType> typeTable) {
        this.typeTable = typeTable;
    }

    /*
     * Prints the type table to stdout (for debugging).
     */
    private void printTypeTable() {
        for (Node n : this.typeTable.keySet())
            System.out.println(n + " -> " + this.typeTable.get(n));
    }

    @Override
    public void defaultOut(Node node) {
        super.defaultOut(node);
        if (node instanceof PExpr)
            this.annotateType(node);
    }

    /**
     * Annotates the output of a given AST node with its type in pretty printing.
     *
     * @param node - AST node
     */
    private void annotateType(Node node) {
        this.buffer.append(" /* ");

        GoLiteType type = this.typeTable.get(node);
        this.buffer.append(type == null ? "(None)" : type.toString());

        this.buffer.append(" */ ");
    }

}