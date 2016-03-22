package golite.util;

import java.util.*;
import golite.node.*;
import golite.analysis.*;

/* This was basically copied verbatim from:
 * http://lists.sablecc.org/pipermail/sablecc-discussion/msg00144.html
 */

public class LineAndPos extends ReversedDepthFirstAdapter
{
  private Map<Node, Integer> lines = new HashMap<Node, Integer>();
  private Map<Node, Integer> positions = new HashMap<Node, Integer>();

  public int getLine(Node node) { return lines.get(node); }
  public int getPos(Node node) { return positions.get(node); }

  private int line, pos;

  // called on each token
  @Override
  public void defaultCase(Node node) {
    Token token = (Token) node;
    line = token.getLine();
    pos = token.getPos();
    lines.put(node, line);
    positions.put(node, pos);
  }

  // called for each alternative
  @Override
  public void defaultOut(Node node) {
    // use the line/pos of the last seen token
    lines.put(node, line);
    positions.put(node, pos);
  }
}   