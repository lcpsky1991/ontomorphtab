package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.ScrollContainer;
import org.fenggui.background.PlainBackground;
import org.fenggui.composites.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.tree.Tree;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.view.View;

public class InstanceBrowser implements ISelectionChangedListener {
	
	public InstanceBrowser(Display d) {
		Display display = d;
		TreeNode root = SemanticRepository.getAvailableInstance().getInstanceTree();
		
		Window window = FengGUI.createWindow(display, true, false, false, true);
		window.getAppearance().removeAll();
		
		window.setTitle("Instances..");
		
		ScrollContainer sc = FengGUI.createScrollContainer(window.getContentContainer());
		sc.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		
		Tree<TreeNode> tree = MyTreeModel.<TreeNode>createTree(sc);
		
		window.setSize(400, 300);
		//StaticLayout.center(window, display);
		window.setPosition(new Point(0,100));
		window.layout();
		tree.setModel(new MyTreeModel(root));
		
		tree.getToggableWidgetGroup().addSelectionChangedListener(this);	

	}
	
	public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
		TreeNode n = (TreeNode)selectionChangedEvent.getToggableWidget().getValue();
		if (n.value != null) {
			if (!selectionChangedEvent.isSelected()) {		
				n.value.unselect();
			} else {
				n.value.select();/*
				JFrame frmDialog = new JFrame();
				frmDialog.setSize(0,0);
				frmDialog.setLocation(100,100);
				frmDialog.setVisible(true);
				int ival = JOptionPane.showConfirmDialog(frmDialog, "Would you like to zoom to this item? ", "Zoom to item?", JOptionPane.YES_NO_OPTION);
				if (ival == JOptionPane.YES_OPTION) 
				{
					
					try {
						SemanticInstance s = (SemanticInstance)n.value;
						ISemanticsAware sa = s.getSemanticsAwareAssociation();
						if (sa instanceof Tangible) {
							View.getInstance().getCameraView().searchZoomTo(((Tangible)sa).getAbsolutePosition());
						} else {
							JOptionPane.showMessageDialog(null, "Unable to find object's position!");
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Unable to find object's position!");
					}
				}

				frmDialog.setVisible(false);
				frmDialog.dispose(); //destroy the frame that holds the dialogs
				*/
			}
		}	
	}

}
