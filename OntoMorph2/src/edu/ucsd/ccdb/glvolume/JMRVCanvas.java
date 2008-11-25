package edu.ucsd.ccdb.glvolume;


import java.awt.Canvas;

/**
 * @author Christopher Aprea (caprea)
 * 
 * Date: 	2008.10.27
 * 
 * Purpose: Creates a wrapper to call native C++ code performed by 'meshTester'
 * 			Main functionality is simply to provide an openGL context on which to draw
 * 			
 * Dev Environment:	Kubuntu 8.10
 * 					jdk 1.6
 * 					x86_64
 * 					dual-core
 */



public class JMRVCanvas
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

	public native int load(String filename);	//retreives the volume to store in the object
	public native void init();	//Creates Gl context or gets it, must be initialized before loading object or anything else happens
	public native void purge();	//uses GL to empty the frame buffer (display nothing)
	public native void renderAll();
	public native void translate(int vol, double x, double y, double z);
	public native void rotate(int vol, 	double angle, double x, double y, double z);
	public native void showGLError();
	public native void test();
	public native void initFor(Canvas targetCanvas);
	public native void setCameraDistance(double dis);
	

	
}