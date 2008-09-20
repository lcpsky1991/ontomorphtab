package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.Button;
import org.fenggui.Display;
import org.fenggui.FengGUI;
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
import org.fenggui.layout.BorderLayout;
import org.fenggui.render.Pixmap;
import org.fenggui.util.Color;
import org.fenggui.util.Point;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.KeyInputAction;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticQuery;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;

/**
 * 2D widget that allows a user to type in keywords and issue a keyword search
 *
 */
public class BasicSearchWidget extends Widget{
    
	private Display d;
	private TreeNode root;
	private String textInput
	;
	private Window window;
	private Button button;
	private Pixmap pixmap = null;

	public BasicSearchWidget(Display d) {
		
		this.d = d;
		root = ReferenceAtlas.getInstance().getBrainRegionTree();
		
		buildWindowFrame();
		
       
	}

	public void buildWindowFrame(){
		
		window = new Window(true, false, false, true);
		window.getAppearance().removeAll();
		ScrollContainer sc = FengGUI.createScrollContainer(window.getContentContainer());
		sc.getAppearance().add(new PlainBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)));
		window.setPosition(new Point(0, 100));
		window.setSize(200, 300);
    	//window.getContentContainer().setLayoutManager(new BorderLayout());
		window.setTitle("Search Query"); 
		window.layout();
		
		//window.addMouseExitedListener(FocusManager.focusManager);
        final TextEditor textArea = FengGUI.createTextArea(window.getContentContainer());
        textArea.setText("Enter Keyword");
        textArea.setSize(100, 20);


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
        textArea.setPosition(new Point(30,220));
        //d.layout();

        d.addWidget(window);
		/*
		window = FengGUI.createDialog(d);
		window.setPosition(new Point(0, 100));
		window.setSize(200,300);
		window.setTitle("Search Query");
		
		SplitContainer centerSC = FengGUI.createSplitContainer(window.getContentContainer(), true);
		SplitContainer northSC = FengGUI.createSplitContainer(false);
		SplitContainer southSC = FengGUI.createSplitContainer(false);

		final TextEditor textArea = FengGUI.createTextArea(window.getContentContainer());
        textArea.setText("Enter Keyword");
        textArea.setSize(100, 20);
        
		button = new Button("Start Search");
		button.setSize(80,30);
		button.setPosition(new Point(45, 180));
		button.addButtonPressedListener( new IButtonPressedListener() {

        	
	        public void buttonPressed( ButtonPressedEvent arg0 ) {
	        			textInput = textArea.getText();
	        			search(textInput);
	        	}
	        } );
		
		centerSC.setFirstWidget(southSC);
		centerSC.setFirstWidget(northSC);
		
		window.layout();*/

	}
	
	
	public void search(String searchInput){
		
		textInput = searchInput;
		
		//call to the Semantic Repository
		SemanticQuery searchQuery = new SemanticQuery();
		searchQuery.createSimpleQuery(textInput);
		
		
		button.removedFromWidgetTree();
		System.out.println(textInput);
		displayList();
	}
	
	public void displayList(){
	
		
		Window listFrame = FengGUI.createDialog(d);
		listFrame.getAppearance().removeAll();
		listFrame.setPosition(new Point(0,100));
		listFrame.setSize(200, 180);
		 
		listFrame.setTitle("List with icons");
		
 		ScrollContainer sc = FengGUI.createScrollContainer(listFrame.getContentContainer());

 		List<Integer> list = FengGUI.createList(sc);
 		list.setSize(100, 100);
 
 		 for (int i = 0; i < 15; i++)
 		{
 			ListItem<Integer> item = FengGUI.createListItem(list);
 			item.setText((i + 1) + ". Item");
 			item.setPixmap(pixmap);
 		}

 		listFrame.layout();
 		
	}
}


