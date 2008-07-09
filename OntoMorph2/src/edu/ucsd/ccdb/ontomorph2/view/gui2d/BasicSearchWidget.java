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

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.atlas.ReferenceAtlas;

/**
 * 2D widget that allows a user to type in keywords and issue a keyword search
 *
 */
public class BasicSearchWidget {

	
	
	public BasicSearchWidget(Display d) {
		MyNode root = ReferenceAtlas.getInstance().getBrainRegionTree();
		
		Window window = FengGUI.createWindow(d, true, false, false, true);
		window.setSize(200, 300);
		
	}


}
