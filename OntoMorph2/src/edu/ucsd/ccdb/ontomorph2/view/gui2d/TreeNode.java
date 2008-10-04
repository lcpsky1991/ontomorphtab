package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.ArrayList;

import edu.ucsd.ccdb.ontomorph2.core.tangible.ISelectable;


/**
 * Defines a node for a tree-hierarchy.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class TreeNode {
	
	public ISelectable value = null;
	public ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	public String text = null;
	
	public TreeNode(String string, ISelectable value)
	{
		this.text = string;
		this.value = value;
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof TreeNode) {
			TreeNode t = (TreeNode)o;
			return (this.text.equals(t.text) && this.value.equals(t.value));
		}
		return false;
	}
	
	public int hashCode() {
		int hashCode = 0;
		if (this.text != null) {
			hashCode += this.text.hashCode();
		}
		if (this.value != null) {
			hashCode += this.value.hashCode();
		}
		return hashCode;
	}
}

