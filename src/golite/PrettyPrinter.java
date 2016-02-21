package golite;

import golite.analysis.*;
import golite.node.*;
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

}
