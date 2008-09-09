package edu.ucsd.ccdb.ontomorph2.view.gui2d;

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

import edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository;

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
		if (!selectionChangedEvent.isSelected()) {
			TreeNode n = (TreeNode)selectionChangedEvent.getToggableWidget().getValue();
			if (n.value != null) {
				n.value.unselect();
			}
			return;
		}
		TreeNode n = (TreeNode)selectionChangedEvent.getToggableWidget().getValue();
		if (n.value != null) {
			n.value.select();
		}
	}

}
