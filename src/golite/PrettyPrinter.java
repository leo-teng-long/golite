package golite;

import golite.analysis.*;
import golite.node.*;
import java.util.*;
import java.io.*;

/**
 * GoLite PrettyPrinter
 *
 * Pretty print "foo.go" to re-formatted "foo.pretty.go"
 */

public class PrettyPrinter extends DepthFirstAdapter {

  /**
   * Class variables
   */
  private StringBuffer buffer;
  private String fileName;
  private int numTabs;

  /**
   * PrettyPrinter Constructor
   */
  public PrettyPrinter(String fileName) {
    this.buffer = new StringBuffer();
    this.fileName = fileName;
    this.numTabs = 0;
  }

  /**
   * @Override public void inAProgProg(AProgProg node)
   *
   * Pretty print package declaration
   */
  public void inAProgProg(AProgProg node) {
    addTabs();
    buffer.append("package " + node.getId().getText());
    addNewLine(1);
  }

  /**
   * @Override public void inAVarsTopDec(AVarsTopDec node)
   * @Override public void outAVarsTopDec(AVarsTopDec node)
   *
   * Pretty print long variable declaration
   */
  public void inAVarsTopDec(AVarsTopDec node) {
    addTabs();
    buffer.append("var ");
    if (node.getVarSpec().size() > 1) {
      buffer.append('(');
      addNewLine(1);
      numTabs++;
    }
  }

  public void outAVarsTopDec(AVarsTopDec node) {
    if (node.getVarSpec().size() > 1) {
      numTabs--;
      addTabs();
      buffer.append(')');
    }
    addNewLine(1);
  }

  /**
   * @Override public void inASpecVarSpec(ASpecVarSpec node)
   * @Override public void outASpecVarSpec(ASpecVarSpec node)
   * @Override public void caseASpecVarSpec(ASpecVarSpec node)
   *
   * Pretty print variable declaration statement
   */
  public void inASpecVarSpec(ASpecVarSpec node) {
    addTabs();
  }

  public void outASpecVarSpec(ASpecVarSpec node) {
    addNewLine(1);
  }

  public void caseASpecVarSpec(ASpecVarSpec node) {
    inASpecVarSpec(node);
    {
      List<TId> copy = new ArrayList<TId>(node.getId());
      for (TId e : copy) {
        buffer.append(e.getText() + ',');
      }
      buffer.deleteCharAt(buffer.length() - 1);
    }
    if (node.getTypeExpr() != null)
    {
      node.getTypeExpr().apply(this);
    }
    {
      List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
      for (PExpr e : copy) {
        e.apply(this);
        buffer.append(',');
      }
      buffer.deleteCharAt(buffer.length() - 1);
    }
    outASpecVarSpec(node);
  }

  /**
   * @Override public void outABoolTypeExpr(ABoolTypeExpr node)
   *
   * Pretty print "bool" variable type
   */
  public void outABoolTypeExpr(ABoolTypeExpr node) {
    buffer.append("bool");
  }

  /**
   * @Override public void outAIntTypeExpr(AIntTypeExpr node)
   *
   * Pretty print "int" variable type
   */
  public void outAIntTypeExpr(AIntTypeExpr node) {
    buffer.append("int");
  }

  /**
   * @Override public void outAFloatTypeExpr(AFloatTypeExpr node)
   *
   * Pretty print "float64" variable type
   */
  public void outAFloatTypeExpr(AFloatTypeExpr node) {
    buffer.append("float64");
  }

  /**
   * @Override public void outARuneTypeExpr(ARuneTypeExpr node)
   *
   * Pretty print "rune" variable type
   */
  public void outARuneTypeExpr(ARuneTypeExpr node) {
    buffer.append("rune");
  }

  /**
   * @Override public void outAStringTypeExpr(AStringTypeExpr node)
   *
   * Pretty print "string" variable type
   */
  public void outAStringTypeExpr(AStringTypeExpr node) {
    buffer.append("string");
  }

  /**
   * @Override public void outACustomTypeExpr(ACustomTypeExpr node)
   *
   * Pretty print custom (id) variable type
   */
  public void outACustomTypeExpr(ACustomTypeExpr node) {
    buffer.append(node.getId().getText());
  }

  /**
   * @Override public void inAArrayTypeExpr(AArrayTypeExpr node)
   *
   * Pretty print array variable type
   */
  public void inAArrayTypeExpr(AArrayTypeExpr node) {
    buffer.append('[' + node.getExpr().toString() + ']');
  }

  /**
   * @Override public void inASliceTypeExpr(ASliceTypeExpr node)
   *
   * Pretty print slice variable type
   */
  public void inASliceTypeExpr(ASliceTypeExpr node) {
    buffer.append("[]");
  }

  /**************************************************
   * Literals                                       *
   **************************************************/

  /**
   * @Override public void outABoolLitExpr(ABoolLitExpr node)
   *
   * Pretty print bool literals
   */
  public void outABoolLitExpr(ABoolLitExpr node) {
    buffer.append(node.getBoolLit().getText());
  }

  /**
   * @Override public void outAIntLitExpr(AIntLitExpr node)
   *
   * Pretty print int literals
   */
  public void outAIntLitExpr(AIntLitExpr node) {
    buffer.append(node.getIntLit().getText());
  }

  /**
   * @Override public void outAOctLitExpr(AOctLitExpr node)
   *
   * Pretty print oct literals
   */
  public void outAOctLitExpr(AOctLitExpr node) {
    buffer.append(node.getOctLit().getText());
  }

  /**
   * @Override public void outAHexLitExpr(AHexLitExpr node)
   *
   * Pretty print hex literals
   */
  public void outAHexLitExpr(AHexLitExpr node) {
    buffer.append(node.getHexLit().getText());
  }

  /**
   * @Override public void outAFloatLitExpr(AFloatLitExpr node)
   *
   * Pretty print float literals
   */
  public void outAFloatLitExpr(AFloatLitExpr node) {
    buffer.append(node.getFloatLit().getText());
  }

  /**
   * @Override public void outARuneLitExpr(ARuneLitExpr node)
   *
   * Pretty print run literals
   */
  public void outARuneLitExpr(ARuneLitExpr node) {
    buffer.append(node.getRuneLit().getText());
  }

  /**
   * @Override public void outAInterpretedStringLitExpr(AInterpretedStringLitExpr node)
   *
   * Pretty print interpreted string literals
   */
  public void outAInterpretedStringLitExpr(AInterpretedStringLitExpr node) {
    buffer.append(node.getInterpretedStringLit().getText());
  }

  /**
   * @Override public void outARawStringLitExpr(ARawStringLitExpr node)
   *
   * Pretty print raw string literals
   */
  public void outARawStringLitExpr(ARawStringLitExpr node) {
    buffer.append(node.getRawStringLit().getText());
  }

  /**************************************************
   * Identifiers                                    *
   **************************************************/

  /**
   * @Override public void outAVariableExpr(AVariableExpr node)
   *
   * Pretty print variables
   */
  public void outAVariableExpr(AVariableExpr node) {
    buffer.append(node.getId().getText());
  }

  /**************************************************
   * Struct fields                                  *
   **************************************************/

  // ......

  /**************************************************
   * Array/slice elements                           *
   **************************************************/

  // ......

  /**************************************************
   * Append function                                *
   **************************************************/

  // ......

  /**************************************************
   * Type casting                                   *
   **************************************************/

  // ......

  /**************************************************
   * Function calls                                 *
   **************************************************/

  // ......

  /**************************************************
   * Unary operators                                *
   **************************************************/

  /**
   * @Override public void inANegExpr(ANegExpr node)
   * @Override public void outANegExpr(ANegExpr node)
   *
   * Pretty print negate ('-') operator
   */
  public void inANegExpr(ANegExpr node) {
    addLeftParen();
    buffer.append('-');
  }

  public void outANegExpr(ANegExpr node) {
    addRightParen();
  }

  /**
   * @Override public void inAPosExpr(APosExpr node)
   * @Override public void outAPosExpr(APosExpr node)
   *
   * Pretty print unary plus ('+') operator
   */
  public void inAPosExpr(APosExpr node) {
    addLeftParen();
    buffer.append('+');
  }

  public void outAPosExpr(APosExpr node) {
    addRightParen();
  }

  /**
   * @Override public void inABitCompExpr(ABitCompExpr node)
   * @Override public void outABitCompExpr(ABitCompExpr node)
   *
   * Pretty print bit complement ('^') operator
   */
  public void inABitCompExpr(ABitCompExpr node) {
    addLeftParen();
    buffer.append('^');
  }

  public void outABitCompExpr(ABitCompExpr node) {
    addRightParen();
  }

  /**
   * @Override public void inANotExpr(ANotExpr node)
   * @Override public void outANotExpr(ANotExpr node)
   *
   * Pretty print not ('!') operator
   */
  public void inANotExpr(ANotExpr node) {
    addLeftParen();
    buffer.append('!');
  }

  public void outANotExpr(ANotExpr node) {
    addRightParen();
  }

  /**************************************************
   * Private methods                                *
   **************************************************/

  /**
   * @Private method
   *
   * Add tabs
   */
  private void addTabs() {
    for (int i = 0; i < numTabs; i++) {
      buffer.append('\t');
    }
  }

  /**
   * @Private method
   *
   * Add new line
   */
  private void addNewLine(int n) {
    for (int i = 0; i < n; i++) {
      buffer.append('\n');
    }
  }

  /**
   * @Private method
   *
   * Add '('
   */
  private void addLeftParen() {
    buffer.append('(');
  }

  /**
   * @Private method
   *
   * Add ')'
   */
  private void addRightParen() {
    buffer.append(')');
  }

  /**
   * @Private method
   *
   * Add '['
   */
  private void addLeftBracket() {
    buffer.append('[');
  }

  /**
   * @Private method
   *
   * Add ']'
   */
  private void addRightBracket() {
    buffer.append(']');
  }

  /**
   * @Private method
   *
   * Add '{'
   */
  private void addLeftBrace() {
    buffer.append('{');
  }

  /**
   * @Private method
   *
   * Add '}'
   */
  private void addRightBrace() {
    buffer.append('}');
  }
}
