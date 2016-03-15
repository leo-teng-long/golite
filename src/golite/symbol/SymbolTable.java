package golite.symbol;

import golite.node.*;
import golite.exception.*;
import golite.util.*;
import java.util.*;

public class SymbolTable {

    /* Class attributes */
    private Stack<HashMap<String, Node>> scopes;
    private LineAndPos lineAndPos = new LineAndPos();

    /* Constructor */
    public SymbolTable() {
        scopes = new Stack<HashMap<String, Node>>();
    }

    /*
     * Enter a new scope
     */
    public void enterScope() {
        scopes.push(new HashMap<String, Node>());
        System.out.println("ENTER SCOPE");
    }

    /*
     * Exit a scope, pop it off the stack
     */
    public void exitScope() {
        checkScopesSize();
        scopes.pop();
        System.out.println("EXIT SCOPE");
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
        System.out.println("Name: " + id + " Node: " + node);
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
     *
     */
    private void callSymbolException(Node node, String s) {
        String message = "";
        //if (node != null) {
        //    Integer line = lineAndPos.getLine(node);
        //    Integer pos = lineAndPos.getPos(node);
        //    message += "[" + line + "," + pos + "] ";
        //}
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

    /*
     * Print symbols (for debugging purposes)
     */
    public void printSymbols() {
        for (HashMap<String, Node> scope: scopes) {
            System.out.println("ENTER SCOPE");
            for (String str: scope.keySet()) {
                System.out.print(str + ": ");
                System.out.println(scope.get(str).getClass());
            }
            System.out.println("EXIT SCOPE");
        }
    }

}
