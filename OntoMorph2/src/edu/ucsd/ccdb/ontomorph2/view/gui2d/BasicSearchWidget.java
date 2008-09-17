package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.Button;
import org.fenggui.Display;
import org.fenggui.FengGUI;
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
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.util.FocusManager;

/**
 * 2D widget that allows a user to type in keywords and issue a keyword search
 *
 */
public class BasicSearchWidget extends InputHandler{
    
	private FengJMEInputHandler fengGui;
	InputHandler input;
	private boolean add;

	public BasicSearchWidget(Display d) {
		
		TreeNode root = ReferenceAtlas.getInstance().getBrainRegionTree();
		Window window = new Window(true, false, false, true);
		window.setPosition(new Point(100, 100));
		window.setSize(200, 300);
    	window.getContentContainer().setLayoutManager(new BorderLayout());
		window.setTitle("Search Query"); 
		
		//window.addMouseExitedListener(FocusManager.focusManager);
        TextEditor textArea = FengGUI.createTextArea(window.getContentContainer());
        textArea.setText("Enter Keyword");
        textArea.setSize(100, 20);
        
        /*input = new InputHandler();
        fengGui = new FengJMEInputHandler(d);
        input.addToAttachedHandlers(fengGui);*/
        System.out.println(add + " add");
        textArea.addMouseEnteredListener(FocusManager.get());
        textArea.addMouseExitedListener(FocusManager.get());
        //System.out.println("writing state " + textArea.isInWritingState());
        
        Button button = new Button( "Start Search" );
        button.setSize(80, 30);
        button.setPosition(new Point(45, 180));
        button.addButtonPressedListener( new IButtonPressedListener() {

        	
        public void buttonPressed( ButtonPressedEvent arg0 ) {
            		//search();
        			System.out.println("Button Pressed");
            }
        } );

        button.addMouseEnteredListener(FocusManager.get());
        button.addMouseExitedListener(FocusManager.get());
        textArea.addMouseEnteredListener(FocusManager.get());
        textArea.addMouseExitedListener(FocusManager.get());
        window.getContentContainer().addWidget( button );
        textArea.setPosition(new Point(30,220));
        d.layout();

        d.addWidget(window);
       
	}
}


