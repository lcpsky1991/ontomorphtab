package edu.ucsd.ccdb.glvolume;


import java.awt.Canvas;

/**
 * @author Christopher Aprea (caprea)
 * 
 * Date: 	2008.10.27
 * 
 * Purpose: Creates a wrapper to call native C++ code performed by 'meshTester' and VIRVO
 * 			Main functionality is simply to provide an openGL context on which to draw
 * 			
 * Dev Environment:	Kubuntu 8.10
 * 					jdk 1.6
 * 					x86_64
 * 					dual-core
 */



public class JMultiResolutionVolume
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3613482352429075789L;
	/**
	 * System.load takes a fully qualified filename, eg C:\dlls\mjjni.dll
	 * System.loadLibrary takes unqualified name and appens .dll or .so
	 * 
	 * WIN: The library path is supposed to contain: 
	 * 		windows sys dir, current working dir, entries is in PATH (not CLASSPATH), and root of .jars
	 * 
	 * LINUX: lib*.so must be available somewhere on LD_LIBRARY_PATH
	 * 
	 * MAC: put lib*.jnilib somewhere on the library path. With JWS you can put .jnilib in the .jar 
	 * 		but dont give a package name
	 */
	
	private static String VLIB = "jmrv";
	
	static 
	{
		try
		{
			System.out.println("Loading '" + VLIB + "' Library");
			System.loadLibrary(VLIB);
		}
		catch(SecurityException e)
		{
			System.err.println("Unable to open library (Possibly due to security violation)");
		}
		catch(UnsatisfiedLinkError e)
		{
			System.err.println("Unable to load the library. (File not found, check the path)");
			System.err.println("Java Library path is: " + System.getProperty("java.library.path")); //most browsers do not support this, except Opera
			e.printStackTrace(System.err);
		}
		catch (Exception e)
		{
			System.err.println("Problem loading the library. Unable to resolve source of error.");
		}
	}
	
	//General
	public native void initFor(Canvas targetCanvas); 	//Creates GL context, must be initialized before loading object or anything else happens
	public native int load(String filename);			//retreives the volume to store in the object
	public native void purge();							//uses GL to empty the frame buffer (display nothing)
	public native void display(boolean asComposite);	//If asComposite is false will clear buffer before drawing
	public native void showGLError();
	
	//Volume
	public native void translate(int vol, double x, double y, double z);
	public native void rotate(int vol, 	double angleRadians, double x, double y, double z);
	public native void reset();							//resets the location parameters, location, translation and rotation matrix
	public native void setCurrentVolume(int vol);
	public native int getCurrentVolume();
	public native void setTextureVisiblity(int vol, boolean show);
	public native void setBoundryVisiblity(int vol, boolean show);
	public native int getNumChannels(int vol);			//returns the number of channels that vol has
	public native int setActiveChannel(int vol, int chan, boolean active);
	public native boolean isActiveChannel(int vol, int chan);
	public native void setPixelToVoxelRatio(int vol, int ratio);	//determines image quality
	public native boolean setBrickLimit(int vol, int value);
	public native void resetBrickLimit();
	
	//Camera
	public native void setCameraAspect(float ratio);
	public native void setCameraDistance(double dis);
	public native void viewFromX();		//adjusts the camera to view from Xaxis
	public native void viewFromY();		//adjusts the camera to view from Yaxis
	public native void viewFromZ();		//adjusts the camera to view from Zaxis
	
	
	/**
	 * Calls display(true);
	 * Conveiniance wrapper for display that default as a composite image (does not clear the GL buffer before drawing)
	 * @see display(bool)
	 */
	public void display()
	{
		display(true);
	}
	
	

	
}