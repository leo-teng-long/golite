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
   * Arithmetic Operators                           *
   **************************************************/

   /**
    * @Override public void caseAAddExpr(AAddExpr node)
    *
    * Pretty print add ('+') operator
    */
  public void caseAAddExpr(AAddExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" + ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseASubtractExpr(ASubtractExpr node)
   *
   * Pretty print subtract ('-') operator
   */
  public void caseASubtractExpr(ASubtractExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" - ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseAMultExpr(AMultExpr node)
   *
   * Pretty print multiply ('*') operator
   */
  public void caseAMultExpr(AMultExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" * ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseADivExpr(ADivExpr node)
   *
   * Pretty print divide ('/') operator
   */
  public void caseADivExpr(ADivExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" / ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseAModExpr(AModExpr node)
   *
   * Pretty print module ('%') operator
   */
  public void caseAModExpr(AModExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" % ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**************************************************
   * Bit Operators                                  *
   **************************************************/

  /**
   * @Override public void caseABitAndExpr(ABitAndExpr node)
   *
   * Pretty print bit and ('&') operator
   */
  public void caseABitAndExpr(ABitAndExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" & ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseABitOrExpr(ABitOrExpr node)
   *
   * Pretty print bit or ('|') operator
   */
  public void caseABitOrExpr(ABitOrExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" | ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseABitXorExpr(ABitXorExpr node)
   *
   * Pretty print bit xor ('^') operator
   */
  public void caseABitXorExpr(ABitXorExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" ^ ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseABitClearExpr(ABitClearExpr node)
   *
   * Pretty print bit clear ('&^') operator
   */
  public void caseABitClearExpr(ABitClearExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" &^ ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseABitLshiftExpr(ABitLshiftExpr node)
   *
   * Pretty print bit left shift ('<<') operator
   */
  public void caseABitLshiftExpr(ABitLshiftExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" << ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseABitRshiftExpr(ABitRshiftExpr node)
   *
   * Pretty print bit right shift ('>>') operator
   */
  public void caseABitRshiftExpr(ABitRshiftExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" >> ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**************************************************
   * Unary Operators                                *
   **************************************************/

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
   * @Override public void inANegExpr(ANegExpr node)
   * @Override public void outANegExpr(ANegExpr node)
   *
   * Pretty print unary minus ('-') operator
   */
  public void inANegExpr(ANegExpr node) {
    addLeftParen();
    buffer.append('-');
  }

  public void outANegExpr(ANegExpr node) {
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
   * Pretty print negate ('!') operator
   */
  public void inANotExpr(ANotExpr node) {
    addLeftParen();
    buffer.append('!');
  }

  public void outANotExpr(ANotExpr node) {
    addRightParen();
  }

  /**************************************************
   * Relational Operators                           *
   **************************************************/

  /**
   * @Override public void caseAEqExpr(AEqExpr node)
   *
   * Pretty print equal ('==') operator
   */
  public void caseAEqExpr(AEqExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" == ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseANeqExpr(ANeqExpr node)
   *
   * Pretty print not equal ('!=') operator
   */
  public void caseANeqExpr(ANeqExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" != ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseALtExpr(ALtExpr node)
   *
   * Pretty print less than ('<') operator
   */
  public void caseALtExpr(ALtExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" < ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseALteExpr(ALteExpr node)
   *
   * Pretty print less than or equal to ('<=') operator
   */
  public void caseALteExpr(ALteExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" <= ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseAGtExpr(AGtExpr node)
   *
   * Pretty print greater than ('>') operator
   */
  public void caseAGtExpr(AGtExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" > ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseAGteExpr(AGteExpr node)
   *
   * Pretty print greater than or equal to ('>=') operator
   */
  public void caseAGteExpr(AGteExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" >= ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**************************************************
   * Conditional Operators                          *
   **************************************************/

  /**
   * @Override public void caseAAndExpr(AAndExpr node)
   *
   * Pretty print AND ('&&') operator
   */
  public void caseAAndExpr(AAndExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" && ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseAOrExpr(AOrExpr node)
   *
   * Pretty print OR ('||') operator
   */
  public void caseAOrExpr(AOrExpr node) {
    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" || ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();
  }

  /**************************************************
   * Function Calls                                 *
   **************************************************/

  /**
   * @Override public void caseAFuncCallExpr(AFuncCallExpr node)
   *
   * Pretty print function calls
   */
  public void caseAFuncCallExpr(AFuncCallExpr node) {
    if (node.getId() != null) {
      buffer.append(node.getId().getText());
    }
    addLeftParen();
    List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
    for (PExpr e : copy) {
      e.apply(this);
      addComma();
    }
    deleteLastChar();
    addRightParen();
  }

  /**************************************************
   * Append & Type Casting                          *
   **************************************************/

  /**
   * @Override public void caseAAppendExpr(AAppendExpr node)
   *
   * Pretty print append function
   */
  public void caseAAppendExpr(AAppendExpr node) {
    buffer.append("append");
    addLeftParen();
    if (node.getId() != null) {
      buffer.append(node.getId().getText());
    }
    addComma();
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addRightParen();
  }

  /**
   * @Override public void caseAPrimTypeCastExpr(APrimTypeCastExpr node)
   *
   * Pretty print type casting
   */
  public void caseAPrimTypeCastExpr(APrimTypeCastExpr node) {
    if (node.getTypeExpr() != null) {
      node.getTypeExpr().apply(this);
    }
    addLeftParen();
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addRightParen();
  }

  /**************************************************
   * Struct Fileds                                  *
   **************************************************/

  /**
   * @Override public void caseAFieldExpr(AFieldExpr node)
   *
   * Pretty print struct field
   */
  public void caseAFieldExpr(AFieldExpr node) {
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addDot();
    if (node.getId() != null) {
      buffer.append(node.getId().getText());
    }
  }

  /**************************************************
   * Array/Slice Elements                           *
   **************************************************/

  /**
   * @Override public void caseAArrayElemExpr(AArrayElemExpr node)
   *
   * Pretty print array/slice elements
   */
  public void caseAArrayElemExpr(AArrayElemExpr node) {
    if (node.getId() != null) {
      buffer.append(node.getId().getText());
    }
    addLeftBracket();
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addRightBracket();
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

  /**
   * @Private method
   *
   * Add ','
   */
  private void addComma() {
    buffer.append(',');
  }

  /**
   * @Private method
   *
   * Add '.'
   */
  private void addDot() {
    buffer.append('.');
  }

  /**
   * @Private method
   *
   * Delete the last charcater in the buffer
   */
  private void deleteLastChar() {
    buffer.deleteCharAt(buffer.length() - 1);
  }

}
