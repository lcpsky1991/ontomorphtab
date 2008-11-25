package edu.ucsd.ccdb.glvolume;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GLContext;

import com.jme.system.DisplaySystem;
import com.jmex.awt.JMECanvas;
import com.jmex.awt.JMECanvasImplementor;
import com.jmex.awt.lwjgl.LWJGLCanvas;



public class TestSimpleGL extends Frame
{
	GLContext gc = null;
	static TestSimpleGL app = null;
    
    
	public static void main(String[] args) throws LWJGLException 
	  {
	
			JMRVCanvas jniCanv = new JMRVCanvas();
			
			app = new TestSimpleGL();	
			
			//jniCanv = new JMRVCanvas();
			Canvas nc = new Canvas();
			
			
		    //get good coords for size and location
		    Toolkit sysTools = Toolkit.getDefaultToolkit();	    
		    int mid = (int)sysTools.getScreenSize().getHeight()/2;
		     
		    //display the window
		    
		    app.setVisible(true);
		    app.setSize(1200, 1024);
		    app.setLocation(0,0 );
		    app.setBackground(Color.WHITE);
		    app.setLayout(new GridLayout(1, 2));
		    
		    
		   // jniCanv.setSize(300, 300);
		    nc.setSize(300, 300);
		    
		    nc.setBackground(Color.red);
		    
		   // app.add(jniCanv);
		    app.add(nc);
		    
		    //Make a seperate thread for the rendering to do
	    		    
    		boolean okgo=true;
		    double x= 0;

		    //jniCanv.init();
		    jniCanv.initFor(nc);
		    jniCanv.load("/home/caprea/Documents/meshTester/meshData/config.txt");
		    
		    do
		    {
		    	
		    	//jniCanv.rotate(0, 1, 0, 0, 500);
		    	jniCanv.translate(0, -1, 0, 0);
		    	jniCanv.renderAll();
		    	
		    	jniCanv.test();
		    	
		    	jniCanv.showGLError();
		   // 	jniCanv.repaint();
		    	nc.repaint();
		    	app.repaint();	
		    }
		    while(okgo);

	  }
	
	@Override
	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		super.setVisible(b);
		
		if (!b) System.exit(0);
	}
	

}
