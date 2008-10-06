package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.FengGUI;
import org.fenggui.IContainer;
import org.fenggui.background.PlainBackground;
import org.fenggui.border.PlainBorder;
import org.fenggui.render.Pixmap;
import org.fenggui.tree.ITreeModel;
import org.fenggui.tree.Tree;
import org.fenggui.util.Color;


/**
 * Implements a tree model for the 2D tree widgets.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class MyTreeModel implements ITreeModel<TreeNode>
{
	TreeNode root = null;
	
	public MyTreeModel(TreeNode root) {
		this.root = root;
	}
	public int getNumberOfChildren(TreeNode node)
	{
		return node.children.size();
	}

	public Pixmap getPixmap(TreeNode node)
	{
		return null;
	}

	public String getText(TreeNode node)
	{
		return node.text;
	}

	public TreeNode getRoot()
	{
		return root;
	}

	public TreeNode getNode(TreeNode parent, int index)
	{
		return parent.children.get(index);
	}
	
	/**
	 * Create a Tree widget.
	 * @param <T> type parameter
	 * @param parent the parent container
	 * @return new tree widget.
	 */
	public static <T> Tree<T> createTree(IContainer parent)
	{
		Tree<T> result = new Tree<T>();
		FengGUI.setUpAppearance(result);
		result.getAppearance().removeAll();
		result.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		result.getAppearance().setTextColor(Color.WHITE);
		result.getAppearance().add(new PlainBorder(Color.WHITE_HALF_OPAQUE));
		
		parent.addWidget(result);
		return result;
	}
}
