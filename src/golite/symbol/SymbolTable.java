package golite.symbol;

import golite.node.*;
import golite.exception.*;
import golite.util.*;
import java.util.*;
import java.lang.*;

public class SymbolTable {

    /* Class attributes */
    private Stack<HashMap<String, Node>> scopes;
    private LineAndPos lineAndPos = new LineAndPos();
    private StringBuilder buffer;

    /* Constructor */
    public SymbolTable() {
        scopes = new Stack<HashMap<String, Node>>();
        buffer = new StringBuilder();
    }

    private void addLine()
    {
        buffer.append(System.getProperty("line.separator"));
    }

    /*
     * Enter a new scope
     */
    public void enterScope() {
        scopes.push(new HashMap<String, Node>());
        buffer.append("ENTER SCOPE");
        addLine();
    }

    /*
     * Exit a scope, pop it off the stack
     */
    public void exitScope() {
        checkScopesSize();
        scopes.pop();
        buffer.append("EXIT SCOPE");
        addLine();
    }

    /*
     * Add identifier to the current scope
     */
    public void addSymbol(String id, Node node) {
        checkScopesSize();
        if (scopes.peek().containsKey(id)) {
            callSymbolException(node, "Identifier " + id + " cannot be re-declared");
        }
        scopes.peek().put(id, node);
        buffer.append("Key: " + id + " Value: " + node.getClass());
        addLine();
    }

    /*
     * Remove identifier from the current scope (needed for short assign)
     */
    public void removeSymbol(String id) {
        checkScopesSize();
        scopes.peek().remove(id);
    }

    /*
     * Get the symbol associated with the given identifier
     */
    public Node getSymbol(String id, Node node) {
        checkScopesSize();
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Node symbol = scopes.elementAt(i).get(id);
            if (symbol != null) {
                return symbol;
            }
        }
        callSymbolException(node, "Identifier " + id + " is not defined");
        return null;
    }

    /*
     * Check if given identifier exists in the current scrope
     */
    public boolean containsId(String id) {
        checkScopesSize();
        return scopes.peek().containsKey(id);
    }

    /*
     * Throw SymbolException with given message, then exit
     */
    private void callSymbolException(Node node, String s) {
        String message = "";
        if (node != null) {
            node.apply(lineAndPos);
            Integer line = lineAndPos.getLine(node);
            Integer pos = lineAndPos.getPos(node);
            message += "[" + line + "," + pos + "] ";
        }
        message += s;
        SymbolException e = new SymbolException(message);
        e.printStackTrace();
        System.exit(1);
    }

    /*
     * Check if there are any scopes on the stack
     */
    private void checkScopesSize() {
        if (scopes.isEmpty()) {
            callSymbolException(null, "Scope stack is empty");
        }
    }

    public void printSymbols() {
        System.out.println(buffer.toString());
    }

}
