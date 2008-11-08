package edu.ucsd.ccdb.glvolume;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jmex.awt.JMECanvas;

public class TestSimpleGL extends JFrame
{

	public static void main(String[] args) 
	  {
		    TestSimpleGL app = new TestSimpleGL();
		    JNIResolutionVolume jniCanv = new JNIResolutionVolume();
		    
		    boolean okgo = true;
		    //get good coords for size and location
		    Toolkit sysTools = Toolkit.getDefaultToolkit();	    
		    int mid = (int)sysTools.getScreenSize().getHeight()/2;
		     
		    //display the window
		    app.setVisible(true);
		    app.setSize(mid, mid);
		    app.setBackground(Color.blue);
		    app.setLocation(mid, mid );
		    app.repaint();
		    
		    jniCanv.setBackground(Color.DARK_GRAY);
		    jniCanv.repaint();
		    
		    app.add(jniCanv);
		    
		    jniCanv.init();
		    //jniCanv.dummy(jniCanv.getGraphics());
		    
		    while (okgo)
		    {
			    jniCanv.redrawp();
			    jniCanv.repaint();
		    }
		    
		    
	  }
	  
	 @Override
	public void hide() 
	 {
		// TODO Auto-generated method stub
		super.hide();
		System.exit(0);
	} 
}
