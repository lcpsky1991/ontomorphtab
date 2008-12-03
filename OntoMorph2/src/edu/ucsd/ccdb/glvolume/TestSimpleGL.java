package edu.ucsd.ccdb.glvolume;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;


public class TestSimpleGL extends Frame
{
	
	static TestSimpleGL app = null;
    
    
	public static void main(String[] args)  
	  {
	
			JMultiResolutionVolume volume = new JMultiResolutionVolume();
			
			app = new TestSimpleGL();	
			
			AWTGLCanvas canv = null;	//canv can be substituted for any class that extends Canvas
			try 
			{
				canv = new AWTGLCanvas();
			} 
			catch (LWJGLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		    //get good coords for size and location
		    Toolkit sysTools = Toolkit.getDefaultToolkit();	    
		    int mid = (int)sysTools.getScreenSize().getHeight()/2;
		     
		    //display the window
		    
		    app.setVisible(true);
		    app.setSize(1200, 1024);
		    app.setLocation(0,0 );
		    app.setBackground(Color.WHITE);
		    app.setLayout(new GridLayout(1, 2));
		    
		    canv.setSize(800, 800);
		    
		    canv.setBackground(Color.red);
		    

		    app.add(canv);
		    
	    		    
    		boolean okgo=false;

		    volume.initFor(canv);
		    volume.load("/home/caprea/Documents/meshTester/meshData/config.txt");
		    volume.setCameraDistance(50);
		    volume.translate(0, -100, -1000, 500);
		    
		    
		    do
		    {
		   
		    	volume.translate(0, 0, 0, -1);
		    	volume.display(false);		//display the canvas and don't keep the previous render
		    	
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
