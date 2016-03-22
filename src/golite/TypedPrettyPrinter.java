package golite;

import golite.symbol.SymbolTable;
import golite.symbol.SymbolTableBuilder;
import golite.type.TypeChecker;
import golite.analysis.*;
import golite.node.*;

import java.io.*;
import java.util.*;


/**
 * GoLite Typed PrettyPrinter
 */
public class TypedPrettyPrinter extends PrettyPrinter {

  /** Stores the type table. */
  private HashMap<Node, PTypeExpr> typeTable;

  /**
   * Constructor.
   */
  public TypedPrettyPrinter(String name) {
    super(name);
  }

  // Initialize type table.
  @Override 
  public void inStart(Start node) {
    SymbolTableBuilder symbolBuilder = new SymbolTableBuilder();
    node.apply(symbolBuilder);
    SymbolTable symbolTable = symbolBuilder.getSymbolTable();

    HashMap<Node, PTypeExpr> typeTable = symbolBuilder.getTypeTable();
    TypeChecker typeChecker = new TypeChecker(symbolTable, typeTable);
    node.apply(typeChecker);

    this.typeTable = typeChecker.getTypeTable();
  }

  @Override
  public void defaultOut(Node node) {
    super.defaultOut(node);


    if (node instanceof PExpr)
      this.annotateType(node);
  }

  private void annotateType(Node node) {
    this.buffer.append(" /* ");

    PTypeExpr typeExpr = null;
    if (node instanceof AIntLitExpr) {
      typeExpr = this.typeTable.get((AIntLitExpr) node);
      (typeExpr.getClass());
    } else {
      typeExpr = this.typeTable.get(node); 

    this.buffer.append(typeExpr == null ? "(None)" : typeExpr.toString());

    this.buffer.append(" */");
  }

  // private String getTypeExprString(PTypeExpr typeExpr) {
  //   if (typeExpr instanceof ABoolTypeExpr)
  //     return this.getBoolTypeExprString()
  // }

  // private String getBoolTypeExprString(ABoolTypeExpr node) {
  //   return node.getBool().getText();
  // }

  // private String getIntTypeExprString(AIntTypeExpr node) {
  //   return node.getInt().getText();
  // }

  // private String getFloatTypeExprString(AFloatTypeExpr node) {
  //   return node.getFloat64().getText();
  // }

  // private String getRuneTypeExprString(ARuneTypeExpr node) {
  //   return node.getRune().getText();
  // }

  // private String getStringTypeExprString(AStringTypeExpr node) {
  //   return node.getString().getText();
  // }

  // private String getCustomTypeExprString(ACustomTypeExpr node) {
  //   return node.getId().getText();
  // }

  // private String getArrayTypeExprString(AArrayTypeExpr node) {
  //   String s = "["
  //   if (node.getExpr() != null) {
  //     node.getExpr().apply(this);
  //   }
  //   s += "]";
  //   if (node.getTypeExpr() != null) {
  //     node.getTypeExpr().apply(this);
  //   }
  // }

}
