package golite;

import golite.analysis.*;
import golite.node.*;


/*
 * Encodes the AST as a string, using the common treebank format.
 */
public class ASTEncoder extends DepthFirstAdapter {

	private StringBuilder sb;

	/**
	 * Constructor.
	 */
	public ASTEncoder() {
		this.sb = new StringBuilder();
	}
	
	/**
	 * Returns the encoding as a string.
	 *
	 * @return Encoding
	 * @precondition Adapter must be applied to the AST
	 */
	public String getEncoding() {
		return this.sb.toString();
	}

	@Override
	public void defaultIn(Node node) {
		int lastIdx = this.sb.length() - 1;

		// Separate out nodes at the same level with a space.
		if (lastIdx > -1 && this.sb.charAt(lastIdx) == ')')
			this.sb.append(" ");

		this.sb.append("(" + node.getClass().getSimpleName() + " ");
	}

	@Override
	public void defaultOut(Node node) {
		int lastIdx = this.sb.length() - 1;

		// For nodes with no children, delete the space for separating the node
		// from it's children.
		if (this.sb.charAt(lastIdx) == ' ')
			this.sb.deleteCharAt(lastIdx);

		this.sb.append(")");
	}

	@Override
	public void inAIdOptId(AIdOptId node) {
		this.defaultIn(node);

		// Encode the specific Id.
		this.sb.append(node.getId().getText());
	}

	@Override
	public void inAVariableExpr(AVariableExpr node) {
		this.defaultIn(node);

		// Encode the specific Id.
		this.sb.append(node.getId().getText());
	}

}
