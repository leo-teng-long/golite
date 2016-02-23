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

  /**************************************************
   * Loop Statements                                *
   **************************************************/

  /**
   * @Override public void caseALoopStmt(ALoopStmt node)
   *
   * Pretty print infinite / while / for loop statement
   */
  public void caseALoopStmt(ALoopStmt node) {
    addTabs();
    buffer.append("for");
    if (node.getInit() != null) {
      addSpace();
      node.getInit().apply(this);
      addSemi();
    }
    if (node.getExpr() != null) {
      addSpace();
      node.getExpr().apply(this);
      addSemi();
    }
    if (node.getEnd() != null) {
      addSpace();
      node.getEnd().apply(this);
    }
    addSpace();
    {
      numTabs++;
      addLeftBrace();
      List<PStmt> copy = new ArrayList<PStmt>(node.getBlock());
      for (PStmt e : copy) {
        e.apply(this);
      }
      numTabs--;
      addTabs();
      addRightBrace();
    }
  }

  /**************************************************
   * If-Else Statements                             *
   **************************************************/

  // ......

  /**************************************************
   * Short & Long Declaration Statements            *
   **************************************************/

  /**
   * @Override public void caseAShortAssignStmt(AShortAssignStmt node)
   *
   * Pretty print short assign (':=') statement
   */
  public void caseAShortAssignStmt(AShortAssignStmt node) {
    addTabs();
    {
      List<TId> copy = new ArrayList<TId>(node.getId());
      for (TId e : copy) {
        buffer.append(e.getText());
        addComma();
      }
      if (copy.size() > 0) {
        deleteLastChar();
      }
    }
    buffer.append(" := ");
    {
      List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
      for (PExpr e : copy) {
        e.apply(this);
        addComma();
      }
      if (copy.size() > 0) {
        deleteLastChar();
      }
    }
    addNewLines(1);
  }

  /**
   * @Override public void ......
   *
   * Pretty print long declaration statement
   */

  // ......

  /**************************************************
   * Assignment Statement                           *
   **************************************************/

  /**
   * @Override public void caseAAssignStmt(AAssignStmt node)
   *
   * Pretty print assign statement
   */
  public void caseAAssignStmt(AAssignStmt node) {
    addTabs();
    {
      List<PExpr> copy = new ArrayList<PExpr>(node.getLhs());
      for (PExpr e : copy) {
        e.apply(this);
        addComma();
      }
      if (copy.size() > 0) {
        deleteLastChar();
      }
    }
    buffer.append(" = ");
    {
      List<PExpr> copy = new ArrayList<PExpr>(node.getRhs());
      for (PExpr e : copy) {
        e.apply(this);
        addComma();
      }
      if (copy.size() > 0) {
        deleteLastChar();
      }
    }
    addNewLines(1);
  }

  /**************************************************
   * Op Assignment Statements                       *
   **************************************************/

  /**
   * @Override public void caseAPlusAssignStmt(APlusAssignStmt node)
   *
   * Pretty print plus assign statement
   */
  public void caseAPlusAssignStmt(APlusAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" += ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseAMinusAssignStmt(AMinusAssignStmt node)
   *
   * Pretty print minus assign statement
   */
  public void caseAMinusAssignStmt(AMinusAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" -= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseAStarAssignStmt(AStarAssignStmt node)
   *
   * Pretty print star assign statement
   */
  public void caseAStarAssignStmt(AStarAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" *= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseASlashAssignStmt(ASlashAssignStmt node)
   *
   * Pretty print slash assign statement
   */
  public void caseASlashAssignStmt(ASlashAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" /= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseAPercAssignStmt(APercAssignStmt node)
   *
   * Pretty print mod assign statement
   */
  public void caseAPercAssignStmt(APercAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" %= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseAAndAssignStmt(AAndAssignStmt node)
   *
   * Pretty print bit and assign statement
   */
  public void caseAAndAssignStmt(AAndAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" &= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseAPipeAssignStmt(APipeAssignStmt node)
   *
   * Pretty print bit or assign statement
   */
  public void caseAPipeAssignStmt(APipeAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" |= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseACarotAssignStmt(ACarotAssignStmt node)
   *
   * Pretty print bit xor assign statement
   */
  public void caseACarotAssignStmt(ACarotAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" ^= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseAAmpCarotAssignStmt(AAmpCarotAssignStmt node)
   *
   * Pretty print bit clear assign statement
   */
  public void caseAAmpCarotAssignStmt(AAmpCarotAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" &^= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseALshiftAssignStmt(ALshiftAssignStmt node)
   *
   * Pretty print left shift assign statement
   */
  public void caseALshiftAssignStmt(ALshiftAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" <<= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseARshiftAssignStmt(ARshiftAssignStmt node)
   *
   * Pretty print right shift assign statement
   */
  public void caseARshiftAssignStmt(ARshiftAssignStmt node) {
    addTabs();
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" >>= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
    addNewLines(1);
  }

  /**************************************************
   * Increment, Decrement & Expression Statements   *
   **************************************************/

  /**
   * @Override public void caseAIncrStmt(AIncrStmt node)
   *
   * Pretty print increment ('++') statement
   */
  public void caseAIncrStmt(AIncrStmt node) {
    addTabs();
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
      buffer.append("++");
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseADecrStmt(ADecrStmt node)
   *
   * Pretty print decrement ('--') statement
   */
  public void caseADecrStmt(ADecrStmt node) {
    addTabs();
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
      buffer.append("--");
    }
    addNewLines(1);
  }

  /**
   * @Override public void caseAExprStmt(AExprStmt node)
   *
   * Pretty print expression statement
   */
  public void caseAExprStmt(AExprStmt node) {
    addTabs();
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addNewLines(1);
  }

  /**************************************************
   * Print Statements                               *
   **************************************************/

  /**
   * @Override public void caseAPrintStmt(APrintStmt node)
   *
   * Pretty print print statement
   */
  public void caseAPrintStmt(APrintStmt node) {
    addTabs();
    buffer.append("print");
    addLeftParen();
    {
      List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
      for (PExpr e : copy) {
        e.apply(this);
        addComma();
      }
      if (copy.size() > 0) {
        deleteLastChar();
      }
    }
    addRightParen();
    addNewLines(1);
  }

  /**
   * @Override public void caseAPrintlnStmt(APrintlnStmt Node)
   *
   * Pretty print println statement
   */
  public void caseAPrintlnStmt(APrintlnStmt node) {
    addTabs();
    buffer.append("println");
    addLeftParen();
    {
      List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
      for (PExpr e : copy) {
        e.apply(this);
        addComma();
      }
      if (copy.size() > 0) {
        deleteLastChar();
      }
    }
    addRightParen();
    addNewLines(1);
  }

  /**************************************************
   * Break, Continue & Return Statements            *
   **************************************************/

  /**
   * @Override public void outABreakStmt(ABreakStmt node)
   *
   * Pretty print break statement
   */
  public void outABreakStmt(ABreakStmt node) {
    addTabs();
    buffer.append("break");
    addNewLines(1);
  }

  /**
   * @Override public void outAContinueStmt(AContinueStmt node)
   *
   * Pretty print continue statement
   */
  public void outAContinueStmt(AContinueStmt node) {
    addTabs();
    buffer.append("continue");
    addNewLines(1);
  }

  /**
   * @Override public void caseAReturnStmt(AReturnStmt node)
   *
   * Pretty print return statement
   */
  public void caseAReturnStmt(AReturnStmt node) {
    addTabs();
    buffer.append("return");
    if (node.getExpr() != null) {
      addSpace();
      node.getExpr().apply(this);
    }
    addNewLines(1);
  }

  /**************************************************
   * Types                                          *
   **************************************************/

  /**
   * @Override public void outABoolTypeExpr(ABoolTypeExpr node)
   *
   * Pretty print 'bool' type
   */
  public void outABoolTypeExpr(ABoolTypeExpr node) {
    buffer.append(node.getBool().getText());
  }

  /**
   * @Override public void outAIntTypeExpr(AIntTypeExpr node)
   *
   * Pretty print 'int' type
   */
  public void outAIntTypeExpr(AIntTypeExpr node) {
    buffer.append(node.getInt().getText());
  }

  /**
   * @Override public void outAFloatTypeExpr(AFloatTypeExpr node)
   *
   * Pretty print 'float64' type
   */
  public void outAFloatTypeExpr(AFloatTypeExpr node) {
    buffer.append(node.getFloat64().getText());
  }

  /**
   * @Override public void outARuneTypeExpr(ARuneTypeExpr node)
   *
   * Pretty print 'rune' type
   */
  public void outARuneTypeExpr(ARuneTypeExpr node) {
    buffer.append(node.getRune().getText());
  }

  /**
   * @Override public void outAStringTypeExpr(AStringTypeExpr node)
   *
   * Pretty print 'string' type
   */
  public void outAStringTypeExpr(AStringTypeExpr node) {
    buffer.append(node.getString().getText());
  }

  /**
   * @Override public void outACustomTypeExpr(ACustomTypeExpr node)
   *
   * Pretty print custom (struct or alias) type
   */
  public void outACustomTypeExpr(ACustomTypeExpr node) {
    buffer.append(node.getId().getText());
  }

  /**
   * @Override public void caseAArrayTypeExpr(AArrayTypeExpr node)
   *
   * Pretty print array type
   */
  public void caseAArrayTypeExpr(AArrayTypeExpr node) {
    addLeftBracket();
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addRightBracket();
    if (node.getTypeExpr() != null) {
      node.getTypeExpr().apply(this);
    }
  }

  /**
   * @Override public void caseASliceTypeExpr(ASliceTypeExpr node)
   *
   * Pretty print slice type
   */
  public void caseASliceTypeExpr(ASliceTypeExpr node) {
    addLeftBracket();
    addRightBracket();
    if (node.getTypeExpr() != null) {
      node.getTypeExpr().apply(this);
    }
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
    {
      List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
      for (PExpr e : copy) {
        e.apply(this);
        addComma();
      }
      if (copy.size() > 0) {
        deleteLastChar();
      }
    }
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
   * Add space
   */
  private void addSpace() {
    buffer.append(' ');
  }

  /**
   * @Private method
   *
   * Add tab
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
  private void addNewLines(int n) {
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
   * Add ';'
   */
  private void addSemi() {
    buffer.append(';');
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
