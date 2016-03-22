package golite.util;

import java.util.*;
import golite.node.*;
import golite.analysis.*;


/**
 * Program line and position tracker.
 *
 * Basically copied verbatim from:
 * <a href="http://lists.sablecc.org/pipermail/sablecc-discussion/msg00144.html">http://lists.sablecc.org/pipermail/sablecc-discussion/msg00144.html</a>
 */
public class LineAndPosTracker extends ReversedDepthFirstAdapter {
    
    /** Tracks the line number for every AST token node. */
    private Map<Node, Integer> lines = new HashMap<Node, Integer>();
    /** Tracks the starting position number for every AST token node. */
    private Map<Node, Integer> positions = new HashMap<Node, Integer>();

    /** Tracks current line and position number. */
    private int line, pos;

    /**
     * Returns the line number for a given token node.
     *
     * @param node - AST node token
     * @return Line number
     */
    public int getLine(Node node) {
        return this.lines.get(node);
    }

    /**
     * Returns the start position number for a given node token.
     *
     * @param node - AST node token
     * @return Start position number
     */
    public int getPos(Node node) {
        return this.positions.get(node);
    }

    /**
     * Wrapper called on every AST token node to track line and position information.
     *
     * @param node - AST node
     */
    @Override
    public void defaultCase(Node node) {
      Token token = (Token) node;
      this.line = token.getLine();
      this.pos = token.getPos();
      lines.put(node, this.line);
      positions.put(node, this.pos);
    }

    /**
     * Wrapper called on exiting every AST token node to update line and position information.
     *
     * @param node - AST node
     */
    @Override
    public void defaultOut(Node node) {
      // use the line/pos of the last seen token
      lines.put(node, this.line);
      positions.put(node, this.pos);
    }

}