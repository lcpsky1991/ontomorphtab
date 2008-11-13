package edu.ucsd.ccdb.glvolume;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Toolkit;


import com.jme.system.DisplaySystem;

public class TestSimpleGL extends Frame
{

	public static void main(String[] args) 
	  {
		    TestSimpleGL app = new TestSimpleGL();
		    JNIResolutionVolume jniCanv = new JNIResolutionVolume();
		    
		    //get good coords for size and location
		    Toolkit sysTools = Toolkit.getDefaultToolkit();	    
		    int mid = (int)sysTools.getScreenSize().getHeight()/2;
		     
		    //display the window
		    
		    app.setVisible(true);
		    app.setSize(500, 500);
		    app.setLocation(0,0 );
		    app.setBackground(Color.BLUE);
		    
		    jniCanv.setSize(300, 300);
		    jniCanv.setLocation(1, 1);
		    jniCanv.setBackground(Color.RED);
		    
		    app.add(jniCanv);
		    
		    
		    jniCanv.init();
		    jniCanv.redrawp();
		    jniCanv.repaint();
		    
	  }
	  
	 @Override
	public void hide() 
	 {
		// TODO Auto-generated method stub
		super.hide();
		System.exit(0);
	}
	 
	@Override
	public void move(int x, int y) {
		// TODO Auto-generated method stub
		super.move(x, y);
		System.out.println("moved");
	}
}
