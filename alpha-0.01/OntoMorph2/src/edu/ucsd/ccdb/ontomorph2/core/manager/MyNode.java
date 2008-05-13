package edu.ucsd.ccdb.ontomorph2.core.manager;

import java.util.ArrayList;

import edu.ucsd.ccdb.ontomorph2.core.scene.ISelectable;


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
}

