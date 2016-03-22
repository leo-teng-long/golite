package golite;

import golite.analysis.*;
import golite.node.*;
import java.util.*;
import java.io.*;

/**
 * GoLite PrettyPrinter
 *
 */
// TODO: Convert to 4-space tabs.
// TODO: Fix Override designations.
// TODO: Create superclass for pretty printers.
public class PrettyPrinter extends DepthFirstAdapter {

  /** Buffer storing pretty printed program. */
  protected StringBuffer buffer;
  private String fileName;
  /** Stores the tab depth. */
  private int tabDepth;

  /**
   * PrettyPrinter Constructor
   */
  public PrettyPrinter(String fileName) {
    this.buffer = new StringBuffer();
    this.fileName = fileName;
    this.tabDepth = 0;
  }

  /**
   * Returns the buffer string.
   * 
   * @return Buffer string
   **/
  public String getBufferString() {
    return this.buffer.toString();
  }

  /**************************************************
   * Write printy-print program to file             *
   **************************************************/

  /**
   * @Override public void outAProgProg(AProgProg node)
   *
   * Write to file
   */
  public void outAProgProg(AProgProg node) {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(fileName + ".pretty.go"));
      out.println(buffer.toString());
      out.close();
    } catch (Exception ex) {
      System.err.println("ERROR: Failed to pretty print");
    }

    super.defaultOut(node);
  }

  /**************************************************
   * Program                                        *
   **************************************************/

  /**
   * @Override public void caseAProgProg(AProgProg node)
   *
   * Pretty print program
   */
  public void caseAProgProg(AProgProg node) {
    this.inAProgProg(node);

    if (node.getId() != null) {
      buffer.append("package " + node.getId().getText());
      addNewLines(2);
    }
    {
      List<PTopDec> copy = new ArrayList<PTopDec>(node.getTopDec());
      for (PTopDec e : copy) {
        if (e instanceof AFuncTopDec) {
          prettyPrintFuncDec(e);
        } else {
          e.apply(this);
        }
      }
    }
    
    this.outAProgProg(node);
  }

  /**************************************************
   * Function Declarations                          *
   **************************************************/

  /**
   * @Override public void caseAFuncTopDec(AFuncTopDec node)
   *
   * Pretty print function declaration
   */
  public void caseAFuncTopDec(AFuncTopDec node) {
    buffer.append("func");
    if (node.getId() != null) {
      addSpace();
      buffer.append(node.getId().getText());
    }
    addLeftParen();
    {
      List<PArgGroup> copy = new ArrayList<PArgGroup>(node.getArgGroup());
      for (PArgGroup e : copy) {
        addSpace();
        e.apply(this);
        addComma();
      }
      if (copy.size() > 0) {
        deleteLastChar();
      }
    }
    addRightParen();
    if (node.getTypeExpr() != null) {
      addSpace();
      node.getTypeExpr().apply(this);
    }
    {
      beforeCodeBlock();
      List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
      for (PStmt e : copy) {
        prettyPrintStatement(e);
      }
      afterCodeBlock();
    }
  }

  /**************************************************
   * Variable Declarations                          *
   **************************************************/

  /**
   * @Override public void caseAVarsTopDec(AVarsTopDec node)
   *
   * Pretty print top-level variable declarations
   */
  public void caseAVarsTopDec(AVarsTopDec node) {
    List<PVarSpec> copy = new ArrayList<PVarSpec>(node.getVarSpec());
    for (PVarSpec e : copy) {
      prettyPrintVarSpec(e);
    }
  }

  /**
   * @Override public void caseASpecVarSpec(ASpecVarSpec node)
   *
   * Pretty print top level variable declaration
   */
  public void caseASpecVarSpec(ASpecVarSpec node) {
    buffer.append("var");
    {
      addSpace();
      List<POptId> copy = new ArrayList<POptId>(node.getOptId());
      for (POptId e : copy) {
        // buffer.append(e.getText()); // TODO
        e.apply(this);
        addComma();
      }
      if (copy.size() > 0) {
        deleteLastChar();
      }
    }
    if (node.getTypeExpr() != null) {
      addSpace();
      node.getTypeExpr().apply(this);
    }
    if (node.getExpr().size() == 0) {
      return;
    }
    buffer.append(" = ");
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
  }

  /**************************************************
   * Type Declarations                              *
   **************************************************/

  /**
   * @Override public void caseATypesTopDec(ATypesTopDec node)
   *
   * Pretty print top-level type declarations
   */
  public void caseATypesTopDec(ATypesTopDec node) {
    List<PTypeSpec> copy = new ArrayList<PTypeSpec>(node.getTypeSpec());
    for (PTypeSpec e : copy) {
      prettyPrintTypeSpec(e);
    }
    //addNewLines(1);
  }

  /**
   * @Override public void caseASpecTypeSpec(ASpecTypeSpec node)
   *
   * Pretty print top level type declaration
   */
  public void caseASpecTypeSpec(ASpecTypeSpec node) {
    buffer.append("type");
    if (node.getOptId() != null) {
      addSpace();
      node.getOptId().apply(this);
    }
    if (node.getTypeExpr() != null) {
      addSpace();
      // if (node.getTypeExpr() instanceof AStructTypeExpr) {
      //   buffer.append("struct");
      // }

      node.getTypeExpr().apply(this);
    }
  }

  /**************************************************
   * Argument Groups                                *
   **************************************************/

  /**
   * @Override public void caseAArgArgGroup(AArgArgGroup node)
   *
   * Pretty print argument group
   */
  public void caseAArgArgGroup(AArgArgGroup node) {
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
    addSpace();
    if (node.getTypeExpr() != null) {
      node.getTypeExpr().apply(this);
    }
  }

  /**************************************************
   * Empty Statements                               *
   **************************************************/

  /**
   * @Override public void caseAEmptyStmt(AEmptyStmt node)
   *
   * Pretty print empty line
   */
  public void caseAEmptyStmt(AEmptyStmt node) {
    // (do nothing)
  }

  /**************************************************
   * Variable & Type Declarations                   *
   **************************************************/

  /**
   * @Override public void caseAVarDecStmt(AVarDecStmt node)
   *
   * Pretty print long variable declaration
   */
  public void caseAVarDecStmt(AVarDecStmt node) {
    List<PVarSpec> copy = new ArrayList<PVarSpec>(node.getVarSpec());
    for (PVarSpec e : copy) {
      prettyPrintVarSpec(e);
    }
  }

  /**
   * @Override public void caseAShortAssignStmt(AShortAssignStmt node)
   *
   * Pretty print short variable declaration
   */
  public void caseAShortAssignStmt(AShortAssignStmt node) {
    {
      List<POptId> copy = new ArrayList<POptId>(node.getOptId());
      for (POptId e : copy) {
        // buffer.append(e.getText()); // TODO
        e.apply(this);
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
  }

  /**
   * @Override public void caseATypeDecStmt(ATypeDecStmt node)
   *
   * Pretty print type declaration
   */
  public void caseATypeDecStmt(ATypeDecStmt node) {
    List<PTypeSpec> copy = new ArrayList<PTypeSpec>(node.getTypeSpec());
    for (PTypeSpec e : copy) {
      prettyPrintTypeSpec(e);
    }
  }

  /**************************************************
   * Assignment Statements                          *
   **************************************************/

  /**
   * @Override public void caseAAssignStmt(AAssignStmt node)
   *
   * Pretty print assign statement
   */
  public void caseAAssignStmt(AAssignStmt node) {
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
  }

  /**
   * @Override public void caseAPlusAssignStmt(APlusAssignStmt node)
   *
   * Pretty print '+=' statement
   */
  public void caseAPlusAssignStmt(APlusAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" += ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseAMinusAssignStmt(AMinusAssignStmt node)
   *
   * Pretty print '-=' statement
   */
  public void caseAMinusAssignStmt(AMinusAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" -= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseAStarAssignStmt(AStarAssignStmt node)
   *
   * Pretty print '*=' statement
   */
  public void caseAStarAssignStmt(AStarAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" *= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseASlashAssignStmt(ASlashAssignStmt node)
   *
   * Pretty print '/=' statement
   */
  public void caseASlashAssignStmt(ASlashAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" /= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseAPercAssignStmt(APercAssignStmt node)
   *
   * Pretty print '%=' statement
   */
  public void caseAPercAssignStmt(APercAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" %= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseAAndAssignStmt(AAndAssignStmt node)
   *
   * Pretty print '&=' statement
   */
  public void caseAAndAssignStmt(AAndAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" &= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseAPipeAssignStmt(APipeAssignStmt node)
   *
   * Pretty print '|=' statement
   */
  public void caseAPipeAssignStmt(APipeAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" |= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseACarotAssignStmt(ACarotAssignStmt node)
   *
   * Pretty print '^=' statement
   */
  public void caseACarotAssignStmt(ACarotAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" ^= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseAAmpCarotAssignStmt(AAmpCarotAssignStmt node)
   *
   * Pretty print '&^=' statement
   */
  public void caseAAmpCarotAssignStmt(AAmpCarotAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" &^= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseALshiftAssignStmt(ALshiftAssignStmt node)
   *
   * Pretty print '<<=' statement
   */
  public void caseALshiftAssignStmt(ALshiftAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" <<= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseARshiftAssignStmt(ARshiftAssignStmt node)
   *
   * Pretty print '>>=' statement
   */
  public void caseARshiftAssignStmt(ARshiftAssignStmt node) {
    if (node.getLhs() != null) {
      node.getLhs().apply(this);
    }
    buffer.append(" >>= ");
    if (node.getRhs() != null) {
      node.getRhs().apply(this);
    }
  }

  /**
   * @Override public void caseAIncrStmt(AIncrStmt node)
   *
   * Pretty print '++' statement
   */
  public void caseAIncrStmt(AIncrStmt node) {
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    buffer.append("++");
  }

  /**
   * @Override public void caseADecrStmt(ADecrStmt node)
   *
   * Pretty print '--' statement
   */
  public void caseADecrStmt(ADecrStmt node) {
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    buffer.append("--");
  }

  /**************************************************
   * Expression Statements                          *
   **************************************************/

  /**
   * @Override public void caseAExprStmt(AExprStmt node)
   *
   * Pretty print expression statement
   */
  public void caseAExprStmt(AExprStmt node) {
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
  }

  /**************************************************
   * Print & Println                                *
   **************************************************/

  /**
   * @Override public void caseAPrintStmt(APrintStmt node)
   *
   * Pretty print print statement
   */
  public void caseAPrintStmt(APrintStmt node) {
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
  }

  /**
   * @Override public void caseAPrintlnStmt(APrintlnStmt node)
   *
   * Pretty print println statement
   */
  public void caseAPrintlnStmt(APrintlnStmt node) {
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
  }

  /**************************************************
   * Continue, Break & Return                       *
   **************************************************/

  /**
   * @Override public void caseAContinueStmt(AContinueStmt node)
   *
   * Pretty print continue statement
   */
  public void caseAContinueStmt(AContinueStmt node) {
    buffer.append("continue");
  }

  /**
   * @Override public void caseABreakStmt(ABreakStmt node)
   *
   * Pretty print break statement
   */
  public void caseABreakStmt(ABreakStmt node) {
    buffer.append("break");
  }

  /**
   * @Override public void caseAReturnStmt(AReturnStmt node)
   *
   * Pretty print return statement
   */
  public void caseAReturnStmt(AReturnStmt node) {
    buffer.append("return");
    if (node.getExpr() != null) {
      addSpace();
      node.getExpr().apply(this);
    }
  }

  /**************************************************
   * If-Else Statements                             *
   **************************************************/

  /**
   * @Override public void caseAIfElseStmt(AIfElseStmt node)
   *
   * Pretty print if-else statement
   */
  public void caseAIfElseStmt(AIfElseStmt node) {
    buffer.append("if");
    if (node.getCondition() != null) {
      addSpace();
      node.getCondition().apply(this);
    }
    {
      beforeCodeBlock();
      List<PStmt> copy = new ArrayList<PStmt>(node.getIfBlock());
      for (PStmt e : copy) {
        prettyPrintStatement(e);
      }
      afterCodeBlock();
    }
    
    List<PStmt> copy = new ArrayList<PStmt>(node.getElseBlock());

    // Return if block is empty.
    boolean isEmpty = true;
    for (PStmt e : copy) {
      if (!(e instanceof AEmptyStmt)) {
        isEmpty = false;
        break;
      }
    }

    if (isEmpty)
      return;

    buffer.append("else");
    {
      beforeCodeBlock();
      for (PStmt e : copy) {
        prettyPrintStatement(e);
      }
      afterCodeBlock();
    }
  }

  /**
   * @Override public void caseAElifElseif(AElifElseif node)
   *
   * Pretty PRINT elif statement
   */
  // public void caseAElifElseif(AElifElseif node) {
  //   buffer.append("else if");
  //   if (node.getCondition() != null) {
  //     addSpace();
  //     node.getCondition().apply(this);
  //   }
  //   {
  //     beforeCodeBlock();
  //     List<PStmt> copy = new ArrayList<PStmt>(node.getBlock());
  //     for (PStmt e : copy) {
  //       prettyPrintStatement(e);
  //     }
  //     afterCodeBlock();
  //   }
  // }

  /**
   * @Override public void caseAConditionCondition(AConditionCondition node)
   *
   * Pretty print condition for if-else
   */
  public void caseAConditionCondition(AConditionCondition node) {
    if (node.getStmt() != null) {
      node.getStmt().apply(this);
    }
    addSemi();
    addSpace();
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
  }

  /**************************************************
   * Switch Statements                              *
   **************************************************/

  /**
   * @Override public void caseASwitchStmt(ASwitchStmt node)
   *
   * Pretty print switch statement
   */
  public void caseASwitchStmt(ASwitchStmt node) {
    buffer.append("switch");
    if (node.getStmt() != null) {
      addSpace();
      node.getStmt().apply(this);
      addSemi();
    }
    if (node.getExpr() != null) {
      addSpace();
      node.getExpr().apply(this);
    }
    {
      beforeCodeBlock();
      List<PCaseBlock> copy = new ArrayList<PCaseBlock>(node.getCaseBlock());
      for (PCaseBlock e : copy) {
        e.apply(this);
      }
      afterCodeBlock();
    }
  }

  /**
   * @Override public void caseABlockCaseBlock(ABlockCaseBlock node)
   *
   * Pretty print case block for switch
   */
  public void caseABlockCaseBlock(ABlockCaseBlock node) {
    addTabs();
    if (node.getCaseCondition() != null) {
      node.getCaseCondition().apply(this);
    }
    {
      beforeCaseBlock();
      List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
      for (PStmt e : copy) {
        prettyPrintStatement(e);
      }
      afterCaseBlock();
    }
  }

  /**
   * @Override public void caseAExprsCaseCondition(AExprsCaseCondition node)
   *
   * Pretty print expression case condition
   */
  public void caseAExprsCaseCondition(AExprsCaseCondition node) {
    buffer.append("case");
    addSpace();
    List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
    for (PExpr e : copy) {
      e.apply(this);
      addComma();
    }
    if (copy.size() > 0) {
      deleteLastChar();
    }
    addColon();
  }

  /**
   * @Override public void caseADefaultCaseCondition(ADefaultCaseCondition node)
   *
   * Pretty print default case condition
   */
  public void caseADefaultCaseCondition(ADefaultCaseCondition node) {
    buffer.append("default");
    addColon();
  }

  /**************************************************
   * For Loop                                       *
   **************************************************/

  /**
   * @Override public void caseAForLoopStmt(AForLoopStmt node)
   *
   * Pretty print for loop
   */
  public void caseALoopStmt(ALoopStmt node) {
    buffer.append("for");
    addSpace();

    if (node.getInit() != null)
      node.getInit().apply(this);

    addSemi();
    addSpace();
    
    if (node.getExpr() != null)
      node.getExpr().apply(this);

    addSemi();
    addSpace();
    
    if (node.getEnd() != null && !(node.getEnd() instanceof AEmptyStmt)) {
      node.getEnd().apply(this);
      addSpace();
    }

    beforeBlock();
    List<PStmt> copy = new ArrayList<PStmt>(node.getBlock());
    for (PStmt e : copy) {
      prettyPrintStatement(e);
    }
    afterCodeBlock();
  }

  /**************************************************
   * Block                                          *
   **************************************************/

  /**
   * @Override public void caseABlockStmt(ABlockStmt node)
   *
   * Pretty print block statement
   */
  public void caseABlockStmt(ABlockStmt node) {
    {
      beforeBlock();
      List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
      for (PStmt e : copy) {
        prettyPrintStatement(e);
      }
      afterCodeBlock();
    }
  }

  /**************************************************
   * Types                                          *
   **************************************************/

  /**
   * @Override public void caseABoolTypeExpr(ABoolTypeExpr node)
   *
   * Pretty print 'bool' type
   */
  public void caseABoolTypeExpr(ABoolTypeExpr node) {
    if (node.getBool() != null) {
      buffer.append(node.getBool().getText());
    }
  }

  /**
   * @Override public void caseAIntTypeExpr(AIntTypeExpr node)
   *
   * Pretty print 'int' type
   */
  public void caseAIntTypeExpr(AIntTypeExpr node) {
    if (node.getInt() != null) {
      buffer.append(node.getInt().getText());
    }
  }

  /**
   * @Override public void caseAFloatTypeExpr(AFloatTypeExpr node)
   *
   * Pretty print 'float64' type
   */
  public void caseAFloatTypeExpr(AFloatTypeExpr node) {
    if (node.getFloat64() != null) {
      buffer.append(node.getFloat64().getText());
    }
  }

  /**
   * @Override public void caseARuneTypeExpr(ARuneTypeExpr node)
   *
   * Pretty print 'rune' type
   */
  public void caseARuneTypeExpr(ARuneTypeExpr node) {
    if (node.getRune() != null) {
      buffer.append(node.getRune().getText());
    }
  }

  /**
   * @Override public void caseAStringTypeExpr(AStringTypeExpr node)
   *
   * Pretty print 'string' type
   */
  public void caseAStringTypeExpr(AStringTypeExpr node) {
    if (node.getString() != null) {
      buffer.append(node.getString().getText());
    }
  }

  /**
   * @Override public void caseACustomTypeExpr(ACustomTypeExpr node)
   *
   * Pretty print custom (struct or alias) type
   */
  public void caseACustomTypeExpr(ACustomTypeExpr node) {
    if (node.getId() != null) {
      buffer.append(node.getId().getText());
    }
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

  /**
   * @Override public void caseAStructTypeExpr(AStructTypeExpr node)
   *
   * Pretty print struct type
   */
  public void caseAStructTypeExpr(AStructTypeExpr node) {
    buffer.append("struct");
    beforeCodeBlock();
    List<PStructSub> copy = new ArrayList<PStructSub>(node.getStructSub());
    for (PStructSub e : copy) {
      prettyPrintStructSub(e);
    }
    afterCodeBlock();
  }

  /**
   * @Override public void caseAStructSubStructSub(AStructSubStructSub node)
   *
   * Pretty print struct sub
   */
  public void caseAStructSubStructSub(AStructSubStructSub node) {
    List<POptId> copy = node.getOptId();
    for (POptId e : copy) {
      e.apply(this);
      addComma();
      addSpace();
    }

    if (copy.size() > 0) {
      deleteLastChar();
      deleteLastChar();
    }

    addSpace();
    node.getTypeExpr().apply(this);
  }

  /**************************************************
   * Empty Expressions                              *
   **************************************************/

  /**
   * @Override public void caseAEmptyExpr(AEmptyExpr node)
   *
   * Pretty print empty expression
   */
  public void caseAEmptyExpr(AEmptyExpr node) {
    // (do nothing)
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
    this.inAAddExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" + ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outAAddExpr(node);
  }

  /**
   * @Override public void caseASubtractExpr(ASubtractExpr node)
   *
   * Pretty print subtract ('-') operator
   */
  public void caseASubtractExpr(ASubtractExpr node) {
    this.inASubtractExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" - ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outASubtractExpr(node);
  }

  /**
   * @Override public void caseAMultExpr(AMultExpr node)
   *
   * Pretty print multiply ('*') operator
   */
  public void caseAMultExpr(AMultExpr node) {
    this.inAMultExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" * ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outAMultExpr(node);
  }

  /**
   * @Override public void caseADivExpr(ADivExpr node)
   *
   * Pretty print divide ('/') operator
   */
  public void caseADivExpr(ADivExpr node) {
    this.inADivExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" / ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outADivExpr(node);
  }

  /**
   * @Override public void caseAModExpr(AModExpr node)
   *
   * Pretty print module ('%') operator
   */
  public void caseAModExpr(AModExpr node) {
    this.inAModExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" % ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outAModExpr(node);
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
    this.inABitAndExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" & ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outABitAndExpr(node);
  }

  /**
   * @Override public void caseABitOrExpr(ABitOrExpr node)
   *
   * Pretty print bit or ('|') operator
   */
  public void caseABitOrExpr(ABitOrExpr node) {
    this.inABitOrExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" | ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outABitOrExpr(node);
  }

  /**
   * @Override public void caseABitXorExpr(ABitXorExpr node)
   *
   * Pretty print bit xor ('^') operator
   */
  public void caseABitXorExpr(ABitXorExpr node) {
    this.inABitXorExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" ^ ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outABitXorExpr(node);
  }

  /**
   * @Override public void caseABitClearExpr(ABitClearExpr node)
   *
   * Pretty print bit clear ('&^') operator
   */
  public void caseABitClearExpr(ABitClearExpr node) {
    this.inABitClearExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" &^ ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outABitClearExpr(node);
  }

  /**
   * @Override public void caseABitLshiftExpr(ABitLshiftExpr node)
   *
   * Pretty print bit left shift ('<<') operator
   */
  public void caseABitLshiftExpr(ABitLshiftExpr node) {
    this.inABitLshiftExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" << ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outABitLshiftExpr(node);
  }

  /**
   * @Override public void caseABitRshiftExpr(ABitRshiftExpr node)
   *
   * Pretty print bit right shift ('>>') operator
   */
  public void caseABitRshiftExpr(ABitRshiftExpr node) {
    this.inABitRshiftExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" >> ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outABitRshiftExpr(node);
  }

  /**************************************************
   * Unary Operators                                *
   **************************************************/

  /**
   * @Override public void caseAPosExpr(APosExpr node)
   *
   * Pretty print unary plus ('+') operator
   */
  public void caseAPosExpr(APosExpr node) {
    this.inAPosExpr(node);

    addLeftParen();
    buffer.append('+');
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addRightParen();

    this.outAPosExpr(node);
  }

  /**
   * @Override public void caseANegExpr(ANegExpr node)
   *
   * Pretty print unary minus ('-') operator
   */
  public void caseANegExpr(ANegExpr node) {
    this.inANegExpr(node);

    addLeftParen();
    buffer.append('-');
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addRightParen();

    this.outANegExpr(node);
  }

  /**
   * @Override public void caseABitCompExpr(ABitCompExpr node)
   *
   * Pretty print bit complement ('^') operator
   */
  public void caseABitCompExpr(ABitCompExpr node) {
    this.inABitCompExpr(node);

    addLeftParen();
    buffer.append('^');
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addRightParen();

    this.outABitCompExpr(node);
  }

  /**
   * @Override public void caseANotExpr(ANotExpr node)
   *
   * Pretty print negate ('!') operator
   */
  public void caseANotExpr(ANotExpr node) {
    this.inANotExpr(node);

    addLeftParen();
    buffer.append('!');
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addRightParen();

    this.outANotExpr(node);
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
    this.inAEqExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" == ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outAEqExpr(node);
  }

  /**
   * @Override public void caseANeqExpr(ANeqExpr node)
   *
   * Pretty print not equal ('!=') operator
   */
  public void caseANeqExpr(ANeqExpr node) {
    this.inANeqExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" != ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outANeqExpr(node);
  }

  /**
   * @Override public void caseALtExpr(ALtExpr node)
   *
   * Pretty print less than ('<') operator
   */
  public void caseALtExpr(ALtExpr node) {
    this.inALtExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" < ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outALtExpr(node);
  }

  /**
   * @Override public void caseALteExpr(ALteExpr node)
   *
   * Pretty print less than or equal to ('<=') operator
   */
  public void caseALteExpr(ALteExpr node) {
    this.inALteExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" <= ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outALteExpr(node);
  }

  /**
   * @Override public void caseAGtExpr(AGtExpr node)
   *
   * Pretty print greater than ('>') operator
   */
  public void caseAGtExpr(AGtExpr node) {
    this.inAGtExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" > ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outAGtExpr(node);
  }

  /**
   * @Override public void caseAGteExpr(AGteExpr node)
   *
   * Pretty print greater than or equal to ('>=') operator
   */
  public void caseAGteExpr(AGteExpr node) {
    this.inAGteExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" >= ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outAGteExpr(node);
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
    this.inAAndExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" && ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outAAndExpr(node);
  }

  /**
   * @Override public void caseAOrExpr(AOrExpr node)
   *
   * Pretty print OR ('||') operator
   */
  public void caseAOrExpr(AOrExpr node) {
    this.inAOrExpr(node);

    addLeftParen();
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    buffer.append(" || ");
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    addRightParen();

    this.outAOrExpr(node);
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
    this.inAFuncCallExpr(node);

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

    this.outAFuncCallExpr(node);
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
    this.inAAppendExpr(node);

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

    this.outAAppendExpr(node);
  }

  /**
   * @Override public void caseATypeCastExpr(ATypeCastExpr node)
   *
   * Pretty print type casting expression
   */
  public void caseATypeCastExpr(ATypeCastExpr node) {
    this.inATypeCastExpr(node);

    if (node.getTypeExpr() != null) {
      node.getTypeExpr().apply(this);
    }
    addLeftParen();
    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addRightParen();

    this.outATypeCastExpr(node);
  }

  /**************************************************
   * Array, Slice & Struct                          *
   **************************************************/

  /**
   * @Override public void caseAArrayElemExpr(AArrayElemExpr node)
   *
   * Pretty print array/slice elements
   */
  public void caseAArrayElemExpr(AArrayElemExpr node) {
    this.inAArrayElemExpr(node);

    if (node.getArray() != null) {
      node.getArray().apply(this);
    }
    addLeftBracket();
    if (node.getIndex() != null) {
      node.getIndex().apply(this);
    }
    addRightBracket();

    this.outAArrayElemExpr(node);
  }

  /**
   * @Override public void caseAFieldExpr(AFieldExpr node)
   *
   * Pretty print struct fields
   */
  public void caseAFieldExpr(AFieldExpr node) {
    this.inAFieldExpr(node);

    if (node.getExpr() != null) {
      node.getExpr().apply(this);
    }
    addDot();
    if (node.getId() != null) {
      buffer.append(node.getId().getText());
    }

    this.outAFieldExpr(node);
  }

  /**************************************************
   * Identifiers                                    *
   **************************************************/

  /**
   * @Override public void caseAVariableExpr(AVariableExpr node)
   *
   * Pretty print variables
   */
  public void caseAVariableExpr(AVariableExpr node) {
    this.inAVariableExpr(node);

    if (node.getId() != null) {
      buffer.append(node.getId().getText());
    }

    this.outAVariableExpr(node);
  }

  /**
   * @Override public void caseABlankExpr(ABlankExpr node)
   *
   * Pretty print blank expression
   */
  public void caseABlankExpr(ABlankExpr node) {
    this.inABlankExpr(node);

    buffer.append("_");

    this.outABlankExpr(node);
  }

  /**
   * @Override public void caseABlankOptId(ABlankOptId node)
   *
   * Pretty print blank Id
   */
  public void caseABlankOptId(ABlankOptId node) {
    buffer.append("_");
  }

  /**
   * @Override public void caseAIdOptId(AIdOptId node)
   *
   * Pretty print Id
   */
  public void caseAIdOptId(AIdOptId node) {
    if (node.getId() != null) {
      buffer.append(node.getId().getText()); 
    }
  }

  /**************************************************
   * Literals                                       *
   **************************************************/

  /**
   * @Override public void caseAIntLitExpr(AIntLitExpr node)
   *
   * Pretty print int literals
   */
  public void caseAIntLitExpr(AIntLitExpr node) {
    this.inAIntLitExpr(node);

    if (node.getIntLit() != null) {
      buffer.append(node.getIntLit().getText());
    }

    this.outAIntLitExpr(node);
  }

  /**
   * @Override public void caseAOctLitExpr(AOctLitExpr node)
   *
   * Pretty print oct literals
   */
  public void caseAOctLitExpr(AOctLitExpr node) {
    this.inAOctLitExpr(node);

    if (node.getOctLit() != null) {
      buffer.append(node.getOctLit().getText());
    }

    this.outAOctLitExpr(node);
  }

  /**
   * @Override public void caseAHexLitExpr(AHexLitExpr node)
   *
   * Pretty print hex literals
   */
  public void caseAHexLitExpr(AHexLitExpr node) {
    if (node.getHexLit() != null) {
      buffer.append(node.getHexLit().getText());
    }
  }

  /**
   * @Override public void caseAFloatLitExpr(AFloatLitExpr node)
   *
   * Pretty print float literals
   */
  public void caseAFloatLitExpr(AFloatLitExpr node) {
    this.inAFloatLitExpr(node);

    if (node.getFloatLit() != null) {
      buffer.append(node.getFloatLit().getText());
    }

    this.outAFloatLitExpr(node);
  }

  /**
   * @Override public void caseARuneLitExpr(ARuneLitExpr node)
   *
   * Pretty print run literals
   */
  public void caseARuneLitExpr(ARuneLitExpr node) {
    this.inARuneLitExpr(node);

    if (node.getRuneLit() != null) {
      buffer.append(node.getRuneLit().getText());
    }

    this.outARuneLitExpr(node);
  }

  /**
   * @Override public void caseAInterpretedStringLitExpr(AInterpretedStringLitExpr node)
   *
   * Pretty print interpreted string literals
   */
  public void caseAInterpretedStringLitExpr(AInterpretedStringLitExpr node) {
    this.inAInterpretedStringLitExpr(node);

    if (node.getInterpretedStringLit() != null) {
      buffer.append(node.getInterpretedStringLit().getText());
    }

    this.outAInterpretedStringLitExpr(node);
  }

  /**
   * @Override public void caseARawStringLitExpr(ARawStringLitExpr node)
   *
   * Pretty print raw string literals
   */
  public void caseARawStringLitExpr(ARawStringLitExpr node) {
    this.inARawStringLitExpr(node);

    if (node.getRawStringLit() != null) {
      buffer.append(node.getRawStringLit().getText());
    }

    this.outARawStringLitExpr(node);
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
    for (int i = 0; i < this.tabDepth; i++) {
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
   * Add ':'
   */
  private void addColon() {
    buffer.append(':');
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

  /**
   * @Private method
   *
   * Formatting before entering statement block
   */
  private void beforeCodeBlock() {
    addSpace();
    addLeftBrace();
    addNewLines(1);
    this.tabDepth++;
  }

  /**
   * @Private method
   *
   * Formatting before entering statement block
   */
  private void beforeBlock() {
    addLeftBrace();
    addNewLines(1);
    this.tabDepth++;
  }

  /**
   * @Private method
   *
   * Formatting after exiting statement block
   */
  private void afterCodeBlock() {
    this.tabDepth--;
    addTabs();
    addRightBrace();
    addSpace();
  }

  /**
   * @Private method
   *
   * Formatting before entering case block
   */
  private void beforeCaseBlock() {
    addNewLines(1);
    this.tabDepth++;
  }

  /**
   * @Private method
   *
   * Formatting after exiting case block
   */
  private void afterCaseBlock() {
    this.tabDepth--;
  }

  /**
   * @Private method
   *
   * Formatting before entering declaration block
   */
  private void beforeDecBlock() {
    addSpace();
    addLeftParen();
    addNewLines(1);
    this.tabDepth++;
  }

  /**
   * @Private method
   *
   * Formatting after exiting declaration block
   */
  private void afterDecBlock() {
    this.tabDepth--;
    addTabs();
    addRightParen();
    addSpace();
  }

  /**
   * @Private method
   *
   * Formatting top-level function declaration
   */
  private void prettyPrintFuncDec(PTopDec e) {
    addTabs();
    e.apply(this);
    addNewLines(2);
  }

  /**
   * @Private method
   *
   * Formatting top-level variable declaration
   */
  private void prettyPrintVarSpec(PVarSpec e) {
    addTabs();
    e.apply(this);
    addNewLines(2);
  }

  /**
   * @Private method
   *
   * Formatting top-level type declaration
   */
  private void prettyPrintTypeSpec(PTypeSpec e) {
    addTabs();
    e.apply(this);
    addNewLines(2);
  }

  /**
   * @Private method
   *
   * Formatting argument group for struct declaration
   */
  public void prettyPrintArgGroup(PArgGroup e) {
    addTabs();
    e.apply(this);
    addNewLines(1);
  }

  /**
   * @Private method
   *
   * Formatting argument group for struct declaration
   */
  public void prettyPrintStructSub(PStructSub e) {
    addTabs();
    e.apply(this);
    addNewLines(1);
  }

  /**
   * @Private method
   *
   * Formatting statement
   */
  private void prettyPrintStatement(PStmt e) {
    if (e instanceof AVarDecStmt || e instanceof ATypeDecStmt || e instanceof AEmptyStmt) {
      e.apply(this);
    } else {
      addTabs();
      e.apply(this);
      addNewLines(1);
    }
  }

}
