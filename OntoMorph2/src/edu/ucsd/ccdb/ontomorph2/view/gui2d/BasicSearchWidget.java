package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.Button;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.List;
import org.fenggui.ScrollContainer;
import org.fenggui.TextEditor;
import org.fenggui.Widget;
import org.fenggui.composites.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.layout.BorderLayout;
import org.fenggui.util.Point;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.KeyInputAction;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
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
	public BasicSearchWidget(Display d) {
		
		this.d = d;
		root = ReferenceAtlas.getInstance().getBrainRegionTree();
		
		buildWindowFrame();
		
       
	}
	
	public void search(String searchInput){
		
		textInput = searchInput;
		button.removedFromWidgetTree();
		System.out.println(textInput);
		
	}
	
	public void buildWindowFrame(){
		
		window = new Window(true, false, false, true);
		window.setPosition(new Point(0, 100));
		window.setSize(200, 300);
    	window.getContentContainer().setLayoutManager(new BorderLayout());
		window.setTitle("Search Query"); 
		
		//window.addMouseExitedListener(FocusManager.focusManager);
        final TextEditor textArea = FengGUI.createTextArea(window.getContentContainer());
        textArea.setText("Enter Keyword");
        textArea.setSize(100, 20);

        /*input = new InputHandler();
        fengGui = new FengJMEInputHandler(d);
        input.addToAttachedHandlers(fengGui);*/
        //System.out.println("writing state " + textArea.isInWritingState());
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
        d.layout();

        d.addWidget(window);
		
	}
	
	 
	public void displayList(){
	
		Window listFrame = new Window(true, false, false, true);
		
		listFrame.setTitle("List with icons");
		
		
		ScrollContainer sc = FengGUI.createScrollContainer(listFrame.getContentContainer());
		List<Tangible> list = FengGUI.createList(sc);

	}
}


