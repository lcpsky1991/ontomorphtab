package edu.ucsd.ccdb.glvolume;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import org.lwjgl.LWJGLException;

import com.jme.system.DisplaySystem;



public class TestSimpleGL extends Frame
{

	static TestSimpleGL app = null;
    static JMRVCanvas jniCanv = null;
    
	public static void main(String[] args) throws LWJGLException 
	  {
		    
		app = new TestSimpleGL();	
		jniCanv = new JMRVCanvas();

		
		    //get good coords for size and location
		    Toolkit sysTools = Toolkit.getDefaultToolkit();	    
		    int mid = (int)sysTools.getScreenSize().getHeight()/2;
		     
		    //display the window
		    
		    app.setVisible(true);
		    app.setSize(1200, 1024);
		    app.setLocation(0,0 );
		    app.setBackground(Color.WHITE);
		    
		    jniCanv.setSize(app.getSize());
		    jniCanv.setLocation(0, 0);
		    
		    app.add(jniCanv);
		    
		    
		    jniCanv.init();
		    
		    boolean okgo=true;
		    
		    double x= 0;
		    do
		    {
		    	jniCanv.rotate(0, 1, 0, 0, 500);
		    	jniCanv.renderAll();
		    }
		    while(okgo);
		    
		    
	  }
	  
	 @Override
	public void hide() 
	 {
		// TODO Auto-generated method stub
		super.hide();
		System.exit(0);
	}
	

}
