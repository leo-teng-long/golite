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
  public TypedPrettyPrinter() {
    super();
  }

  /*
   * Prints the type table to stdout (for debugging).
   */
  private void printTypeTable() {
    for (Node n: this.typeTable.keySet())
      System.out.println(n + " -> " + this.typeTable.get(n));  
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

    PTypeExpr typeExpr = this.typeTable.get(node);
    this.buffer.append(typeExpr == null ? "(None)" : typeExpr.toString());

    this.buffer.append(" */ ");
  }

}
