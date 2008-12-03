package edu.ucsd.ccdb.glvolume;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.RenderTexture;
import org.lwjgl.opengl.glu.Cylinder;




public class TestSimpleGL extends Frame
{
	GLContext gc = null;
	static TestSimpleGL app = null;
    
    
	public static void main(String[] args) throws LWJGLException 
	  {
	
			JMultiResolutionVolume jniCanv = new JMultiResolutionVolume();
			
			app = new TestSimpleGL();	
			
			//jniCanv = new JMRVCanvas();
			AWTGLCanvas nc = new AWTGLCanvas();
			
			
		    //get good coords for size and location
		    Toolkit sysTools = Toolkit.getDefaultToolkit();	    
		    int mid = (int)sysTools.getScreenSize().getHeight()/2;
		     
		    //display the window
		    
		    app.setVisible(true);
		    app.setSize(1200, 1024);
		    app.setLocation(0,0 );
		    app.setBackground(Color.WHITE);
		    app.setLayout(new GridLayout(1, 2));
		    
		    nc.setSize(800, 800);
		    
		    nc.setBackground(Color.red);
		    

		    app.add(nc);
		    
		    //Make a seperate thread for the rendering to do
	    		    
    		boolean okgo=true;
		    double x= 0;

		    jniCanv.initFor(nc);
		    jniCanv.load("/home/caprea/Documents/meshTester/meshData/config.txt");
		    jniCanv.setCameraDistance(50);
		    jniCanv.translate(0, -100, -1000, 500);
		    
		    
		    Cylinder cyl = new Cylinder();
		    
		    
		    
		    cyl.draw(50, 50, 300, 50, 50);
		    
		    do
		    {
		   
		    	jniCanv.translate(0, 0, 0, -1);
		    	jniCanv.display();
		    	
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
