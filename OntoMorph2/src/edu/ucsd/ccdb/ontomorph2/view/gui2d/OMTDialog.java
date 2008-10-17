package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleFactory;

/**
 * Provides basic functions for user interactions such as Dialog boxes
 * Currently this implimentation is just a wrapper for Swing in most cases
 * This class provides the abstration neccessary so it does not have to be changes elsewhere
 * Also, provides the same JFrame context (neccessary on Windows to show up on TOP of the application)
 * @author caprea
 *
 */
public class OMTDialog 
{
	private static OMTDialog instance = null;
	JFrame win = null;
	
	public static OMTDialog getInstance()
	{
		if (instance == null) 
		{
			instance = new OMTDialog();
		}
		return instance;
	}
	
	protected OMTDialog()
	{
		//Create a dummy frame for the dialog boxes to exist inside of, 
		//this forces the dialog boxes to appear 'on top' of the application
		win = new JFrame();
		win.setSize(0,0);
		win.setLocation(100,100);
		win.setVisible(true);
	}
	
	protected void hide()
	{
		win.setVisible(false);
	}
	protected void show()
	{
		win.setVisible(true);
	}
	
	public String inputText(String message, String strDefault)
	{
		show();
		String strReply = JOptionPane.showInputDialog(win, message, strDefault);
		hide();
		return strReply;
	}
	
	/**
	 * Same as inputText, but does some error checking to ensure that the return IS a number
	 * @param message
	 * @param dVal
	 * @return
	 */
	public double inputNumber(String message, double dVal)
	{
		double ret = 0.0;
		String strReply = null;
		show();
		
		//Keep asking the user how much for as long as they don't press Cancel, and what they give is crap
		do
		{
			strReply = JOptionPane.showInputDialog(win, message, dVal);
			ret = Double.parseDouble(strReply);
		}
		while ( strReply != null && ret == 0);
		
		hide();
		return ret;
	}
}
