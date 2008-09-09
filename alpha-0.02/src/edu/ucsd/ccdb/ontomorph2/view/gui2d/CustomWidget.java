package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.Container;
import org.fenggui.IAppearance;
import org.fenggui.ObservableWidget;
import org.fenggui.composites.Window;
import org.fenggui.layout.BorderLayout;
import org.fenggui.util.Point;


public class CustomWidget extends ObservableWidget{

	public Window window;
	
	public CustomWidget(Point size, String title){
		this.window = new Window(true, false, false, true);
		this.window.setSize(size.getX(), size.getY());
    	this.window.getContentContainer().setLayoutManager(new BorderLayout());
		this.window.setTitle(title); 
	}
	public CustomWidget(Point position, Point size, String title){
		this.window = new Window(true, false, false, true);
		this.window.setPosition(position);
		this.window.setSize(size.getX(), size.getY());
    	this.window.getContentContainer().setLayoutManager(new BorderLayout());
		this.window.setTitle(title); 
	}
	
	public Container getContainer(){
		Container container = new Container();
		container = window.getContentContainer();
		return container;
	}
	@Override
	public IAppearance getAppearance() {
		// TODO Auto-generated method stub
		return null;
	}

}
