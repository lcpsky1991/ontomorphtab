package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.fenggui.Button;
import org.fenggui.CheckBox;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.Label;
import org.fenggui.LayoutManager;
import org.fenggui.List;
import org.fenggui.ListItem;
import org.fenggui.ScrollContainer;
import org.fenggui.SplitContainer;
import org.fenggui.TextEditor;
import org.fenggui.Widget;
import org.fenggui.background.PlainBackground;
import org.fenggui.composites.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.BorderLayout;
import org.fenggui.layout.RowLayout;
import org.fenggui.layout.StaticLayout;
import org.fenggui.render.Pixmap;
import org.fenggui.text.TextView;
import org.fenggui.util.Color;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.KeyInputAction;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleFactory;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticQuery;
import edu.ucsd.ccdb.ontomorph2.core.tangible.ISelectable;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.View2D;
import edu.ucsd.ccdb.ontomorph2.view.View3DMouseListener;
import edu.ucsd.ccdb.ontomorph2.view.ViewCamera;
import edu.ucsd.ccdb.ontomorph2.view.scene.SphereParticlesView;

/**
 * 2D widget that allows a user to type in keywords and issue a keyword search
 *
 */
public class BasicSearchWidget extends Widget{
    
	private Display d;
	private TreeNode root;
	private String textInput, selected, checkBoxSelection;
	List<Integer> list;
	ScrollContainer sc; 
	HashMap<String, Vector3f> regions;
	Vector3f location,position;
	ViewCamera view = new ViewCamera();
	private CheckBox cells, images, brainRegion;
	private Window window;
	private Button button;
	private Pixmap pixmap = null;

	public BasicSearchWidget(Display d) {
		
		this.d = d;
		//root = ReferenceAtlas.getInstance().getBrainRegionTree();
		
		buildWindowFrame();
		
       
	}

	public BasicSearchWidget() {
		// TODO Auto-generated constructor stub
	}

	public void buildWindowFrame(){
		
		window = new Window(true, false, false, true);
		//window.getAppearance().removeAll();
		window.getContentContainer().setLayoutManager(new RowLayout(false));
		window.getContentContainer().getAppearance().setPadding(new Spacing(0, 5));
		sc = FengGUI.createScrollContainer(window.getContentContainer());
		//sc.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		//FengGUI.getTheme().setUp(sc.getVerticalScrollBar());

 		list = FengGUI.createList(sc);
 		window.setPosition(new Point(0, 100));
		window.setSize(200, 300);
    	//window.getContentContainer().setLayoutManager(new BorderLayout());
		window.setTitle("Search Query"); 
		window.layout();
		
		DB();
        final TextEditor textArea = FengGUI.createTextArea(window.getContentContainer());
        //textArea.setText("Enter Keyword");
        textArea.setSize(100, 20);

        
        brainRegion = FengGUI.createCheckBox(window.getContentContainer(), "Brain Region");
        brainRegion.setPosition(new Point(0,210));
        brainRegion.setSize(80,20);
        images = FengGUI.createCheckBox(window.getContentContainer(), "Images");
        images.setPosition(new Point(85,210));
        images.setSize(50,20);
        cells = FengGUI.createCheckBox(window.getContentContainer(), "Cells");
        cells.setPosition(new Point(140,210));
        cells.setSize(50,20);
        
        button = new Button( "Start Search" );
        button.setSize(80, 30);
        button.setPosition(new Point(45, 180));
        button.addButtonPressedListener( new IButtonPressedListener() {

        	
        public void buttonPressed( ButtonPressedEvent arg0 ) {
        			textInput = textArea.getText();
        			search(textInput);
        	}
        } );
        
        window.getContentContainer().addWidget( button );
        textArea.setPosition(new Point(35,240));
        d.addWidget(window);
        sc.layout();
	}
	
	
	public void search(String searchInput){
		
		textInput = searchInput;
		
		list.clear();
        list.setSize(100, 130);
        list.setPosition(new Point(40,00));
        
 		if(regions.containsKey(textInput)){
 			ListItem<String> item = FengGUI.createListItem(list);
 			item.setText(textInput);
 			item.setPixmap(pixmap);
 			//slideHide();
 			//View.getInstance().indicator(regions.get(textInput));
 		}

 		if(images.isSelected()){
 			list.clear();
 			Iterator i = regions.keySet().iterator();
 			while(i.hasNext()) {
 				ListItem<Tangible> item = FengGUI.createListItem(list);
 				checkBoxSelection = (String)i.next();
 				item.setText(checkBoxSelection);
 				item.setPixmap(pixmap);
 				TangibleFactory.getInstance().createParticles(checkBoxSelection,regions.get(checkBoxSelection));
 			}
 		}
 		
 		list.getToggableWidgetGroup().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
			{
				//System.out.println("selection");
				selected = (String)selectionChangedEvent.getToggableWidget().getText();
				location = regions.get(selected);
				//System.out.println(location +" "  + selected);
		 		if(selected.equals("Cerebellum")){
		 			//System.out.println("cerebellum");
		 			//slideHide();
		 			View.getInstance().getCameraView().smoothlyZoomToSlideCerebellumView();}
		 		if(selected.equals("Hippocampus")){
		 			//System.out.println("hippo");
		 			//slideHide();
		 			View.getInstance().getCameraView().smoothlyZoomToSlideView();}
		 		if(selected.equals("Cells")){
		 			//System.out.println("cells");
		 			//slideHide();
		 			View.getInstance().getCameraView().smoothlyZoomToCellView();}
		 		selected = null;
		 		
		 		//System.out.println(returnObjectPosition());
		 		}}
			);
	}
	
	public void DB(){
		regions = new HashMap<String, Vector3f>();
		regions.put("Hippocampus", new Vector3f(-300f, -118f, -180f));
		//regions.put("Cell", new Vector3f(300f, 180f, -300f));
		regions.put("Cerebellum", new Vector3f(458.9234f, -118.0f, -253.11566f));
		regions.put("Cells" , new Vector3f(190f, -118f, -180f));
				
	}
	
	public void absolutePosition(Vector3f position){
		this.position = position;
	}
	
	public Vector3f returnObjectPosition(){
		return this.position;
	}
	
	

}
