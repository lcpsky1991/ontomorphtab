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

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.BrainRegion;

/**
 * 2D widget that allows interaction with the 3D representation of the atlas. 
 * 
 * Provides a tree interface to see the hierarchy of brain structures, and allows
 * the user to turn different structures on, off, or transparent.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class AtlasBrowser implements ISelectionChangedListener{

	BrainRegion currentSelection = null;
	RadioButton<String> visButton = null;
	RadioButton<String> transpButton = null;
	RadioButton<String> invisButton = null;
	public static final String VISIBLE = "vis.";
	public static final String TRANSPARENT = "transp.";
	public static final String INVISIBLE = "invis.";
	
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
        
        FengGUI.createLabel(radioButtons, "");
        
        final ToggableGroup<String> group = new ToggableGroup<String>();
        
        visButton = FengGUI.createRadioButton(radioButtons, VISIBLE, group);
        visButton.setValue(VISIBLE);
        
        transpButton = FengGUI.createRadioButton(radioButtons, TRANSPARENT, group);
        transpButton.setValue(TRANSPARENT);
        
        invisButton = FengGUI.createRadioButton(radioButtons, INVISIBLE, group);
        invisButton.setValue(INVISIBLE);
        
        group.addSelectionChangedListener(this);
       		
		window.setPosition(new Point(0,100));
		window.layout();
		tree.setModel(new MyTreeModel(root));

		tree.getToggableWidgetGroup().addSelectionChangedListener(this);
	}

	public void selectionChanged(SelectionChangedEvent e) {
		Object v = e.getToggableWidget().getValue();
		
		if (e.isSelected()) {
			if (v instanceof String) { //handle radio buttons
				String value = (String)v;
				//set the visibility on the currently selected brain region model, testing 
				//for the case when no region is selected
				if (AtlasBrowser.VISIBLE.equals(value) && (this.currentSelection != null)) {
					this.currentSelection.setVisibility(BrainRegion.VISIBLE);
				} else if (AtlasBrowser.TRANSPARENT.equals(value) && (this.currentSelection != null)) {
					this.currentSelection.setVisibility(BrainRegion.TRANSPARENT);
				} else if (AtlasBrowser.INVISIBLE.equals(value) && (this.currentSelection != null)) {
					this.currentSelection.setVisibility(BrainRegion.INVISIBLE);
				}
			} else if (v instanceof MyNode) { //handle tree widget
				MyNode n = (MyNode)v;
				BrainRegion br = (BrainRegion)n.value;
				if (br != null) {
					//select the model to let it know to change state
					br.select();
					this.currentSelection = br;
					
					//update state of radio buttons based on visibility 
					//of brain region model
					switch (br.getVisibility()) {
					case BrainRegion.VISIBLE:
						visButton.setSelected(true);
						break;
					case BrainRegion.TRANSPARENT:
						transpButton.setSelected(true);
						break;
					case BrainRegion.INVISIBLE:
						invisButton.setSelected(true);
					}
				}
			}
		} else {
			if (v instanceof MyNode) { //handle tree widget
				MyNode n = (MyNode)e.getToggableWidget().getValue();
				if (n.value != null)
					n.value.unselect();
			}
		}
		
	}
}
