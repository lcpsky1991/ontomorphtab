package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.Button;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.TextEditor;
import org.fenggui.Widget;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.util.Point;

import com.jme.input.InputHandler;
import com.jme.input.action.KeyInputAction;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.util.FocusManager;

/**
 * 2D widget that allows a user to type in keywords and issue a keyword search
 *
 */
public class BasicSearchWidget extends Widget{
    
	InputHandler input = null;
	//MouseManager mouse;
	
	private float displayX, displayY;
	private KeyInputAction keyAction;
	public BasicSearchWidget(Display d) {
		
		displayX = d.getDisplayX();
		displayY = d.getDisplayY();
		input = new FengJMEInputHandler(d);
		MyNode root = ReferenceAtlas.getInstance().getBrainRegionTree();
		CustomWidget window = new CustomWidget(new Point(0,100),new Point(200,300), "Search");
		
		//window.addMouseExitedListener(FocusManager.focusManager);
        TextEditor textArea = FengGUI.createTextArea(window.getContainer());
        textArea.setText("Enter Keyword");
        textArea.setSize(100, 20);
        
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
        window.getContainer().addWidget( button );
        textArea.setPosition(new Point(30,220));
        d.layout();

        d.addWidget(window);
       
	}
}


