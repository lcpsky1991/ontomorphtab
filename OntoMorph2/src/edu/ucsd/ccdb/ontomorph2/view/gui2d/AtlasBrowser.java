package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.Button;
import org.fenggui.Container;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.RadioButton;
import org.fenggui.ScrollContainer;
import org.fenggui.ToggableGroup;
import org.fenggui.background.PlainBackground;
import org.fenggui.composites.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.BorderLayoutData;
import org.fenggui.layout.RowLayout;
import org.fenggui.tree.Tree;
import org.fenggui.util.Color;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;

import edu.ucsd.ccdb.ontomorph2.core.atlas.ReferenceAtlas;

/**
 * 2D widget that allows interaction with the 3D representation of the atlas. 
 * 
 * Provides a tree interface to see the hierarchy of brain structures, and allows
 * the user to turn different structures on, off, or transparent.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class AtlasBrowser {

	
	public AtlasBrowser(Display d) {
		MyNode root = ReferenceAtlas.getInstance().getBrainRegionTree();
		
		Window window = FengGUI.createWindow(d, true, false, false, true);
		window.setSize(200, 300);
		window.getAppearance().removeAll();
		window.getContentContainer().setLayoutManager(new BorderLayout());
		
		window.setTitle("Brain Regions...");
		
		ScrollContainer sc = FengGUI.createScrollContainer(window.getContentContainer());
		sc.setLayoutData(BorderLayoutData.CENTER);
		sc.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		
		Tree<MyNode> tree = MyTreeModel.<MyNode>createTree(sc);
		
		Container radioButtons = FengGUI.createContainer(window.getContentContainer());
		radioButtons.getAppearance().add(new PlainBackground(new Color(255f, 255f, 255f, 255f)));
		radioButtons.setLayoutData(BorderLayoutData.NORTH);
		radioButtons.setLayoutManager(new RowLayout(true));
        radioButtons.getAppearance().setPadding(new Spacing(10, 10));
        Button btn = FengGUI.createButton(radioButtons, "Apply");
        
        btn.updateMinSize();
        btn.setSizeToMinSize();
        btn.setExpandable(false);
        
        FengGUI.createLabel(radioButtons, "");
        
        final ToggableGroup<String> group = new ToggableGroup<String>();
        
        RadioButton<String> threeLegs = FengGUI.createRadioButton(radioButtons, "vis.", group);
        threeLegs.setValue("visible");
        
        RadioButton<String> fourLegs = FengGUI.createRadioButton(radioButtons, "transp.", group);
        fourLegs.setValue("transparent");
        
        RadioButton<String> oneLeg = FengGUI.createRadioButton(radioButtons, "invis.", group);
        oneLeg.setValue("invisible");
        
        group.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent arg0) {
				
				//based on which button is pressed, change the state of the 
				//currently selected brain region
			}
        	
        });
       		
		window.setPosition(new Point(0,100));
		window.layout();
		tree.setModel(new MyTreeModel(root));

		tree.getToggableWidgetGroup().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
			{
				if (!selectionChangedEvent.isSelected()) {
					MyNode n = (MyNode)selectionChangedEvent.getToggableWidget().getValue();
					n.value.unselect();
					return;
				}
				MyNode n = (MyNode)selectionChangedEvent.getToggableWidget().getValue();
				if (n.value != null) {
					n.value.select();
				}
				
			}
			
		});
	}
}
