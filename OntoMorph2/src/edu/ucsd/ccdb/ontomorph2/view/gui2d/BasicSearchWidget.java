package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.fenggui.Button;
import org.fenggui.CheckBox;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.List;
import org.fenggui.ListItem;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.Widget;
import org.fenggui.composites.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.RowLayout;
import org.fenggui.render.Pixmap;
import org.fenggui.util.Point;
import org.fenggui.util.Spacing;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.scene.ParticlesFactory;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticQuery;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.ViewCamera;

/**
 * 2D widget that allows a user to type in keywords and issue a keyword search
 *
 * @author jrmartin
 */
public class BasicSearchWidget extends Widget{
    
	private Display d;
	private String textInput, checkBoxSelection;
	List<Integer> list;
	ScrollContainer sc; 
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
	
	
	public void search(String textInput){
		
		SemanticQuery query = new SemanticQuery();
		//perform the query and get the results
		Set<SemanticInstance> results = query.createSimpleQuery(textInput);
		
		//initialize the widget
		list.clear();
        list.setSize(100, 130);
        list.setPosition(new Point(40,00));
        //populate the widget with the results of the query
        //also create the 3D representation of the query results with SphereParticles
        for (SemanticInstance result : results){
        	ListItem<SemanticInstance> item = FengGUI.createListItem(list);
 			item.setText(result.getLabel());
 			item.setValue(result);
 			item.setPixmap(pixmap);
 			ISemanticsAware instance = result.getSemanticsAwareAssociation();
 			if (instance != null && instance instanceof Tangible) {
 				ParticlesFactory.getInstance().createParticles(((Tangible)instance).getAbsolutePosition());
 			}
        }
        /*
 		if(images.isSelected()){
 			list.clear();
 			for (String region : regions.keySet()) {
 				ListItem<Tangible> item = FengGUI.createListItem(list);
 				checkBoxSelection = region;
 				item.setText(checkBoxSelection);
 				item.setPixmap(pixmap);
 				ParticlesFactory.getInstance().createParticles(regions.get(checkBoxSelection));
 			}
 		}*/
 		
 		list.getToggableWidgetGroup().addSelectionChangedListener(new ISelectionChangedListener() {
 			//this method is called every time a different result in the search widget list is clicked.
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
			{
				SemanticInstance ins = (SemanticInstance)selectionChangedEvent.getToggableWidget().getValue();
				ISemanticsAware instance = ins.getSemanticsAwareAssociation();
				if (instance != null && instance instanceof Tangible) {
					View.getInstance().getCameraView().searchZoomTo(((Tangible)instance).getAbsolutePosition());
				}
			}
 		});
	}
	
	public void absolutePosition(Vector3f position){
		this.position = position;
	}
	
	public Vector3f returnObjectPosition(){
		return this.position;
	}

}
