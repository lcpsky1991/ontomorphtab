package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.ArrayList;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ISelectable;


/**
 * Defines a node for a tree-hierarchy.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class MyNode
{
	public MyNode(String string, ISelectable value)
	{
		this.text = string;
		this.value = value;
	}
	
	public ISelectable value = null;
	public ArrayList<MyNode> children = new ArrayList<MyNode>();
	public String text = null;
	
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

