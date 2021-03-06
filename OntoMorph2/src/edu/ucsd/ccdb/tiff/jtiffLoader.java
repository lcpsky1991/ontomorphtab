package edu.ucsd.ccdb.tiff;

/**
 * @author Christopher Aprea (caprea)
 * 
 * Date: 	2008-07-08
 * 
 * Purpose: Creates a wrapper to call native C++ code performed by tiffLoader, see it's documentation
 * 			getRGBA() is the main functionality, which wraps extractImage() but does not require user to specify buffersize or a buffer
 * 			
 * 
 * Dev Environment:	Kubuntu 8
 * 					jdk 1.6
 * 					x86_64
 * 					dual-core
 */


public class jtiffLoader 
{
	/**
	 * System.load takes a fully qualified filename, eg C:\dlls\mjjni.dll
	 * System.loadLibrary takes unqualified name and appens .dll or .so
	 * 
	 * WIN: The library path is supposed to contain: 
	 * 		windows sys dir, current working dir, entries is in PATH (not CLASSPATH), and root of .jars
	 * 
	 * LINUX: libjtiffLoader.so must be available somewhere on LD_LIBRARY_PATH
	 * 
	 * MAC: put libjtiffLoader.jnilib somewhere on the library path. With JWS you can put .jnilib in the .jar 
	 * 		but dont give a package name
	 */
	
	static 
	{
		try
		{
			System.loadLibrary("jtiffLoader");
		}
		catch (Exception e)
		{
			System.out.println("Problem loading the jtiffLoader library. Searched in:");
			System.out.println("Java Library path is: " + System.getProperty("java.library.path")); //most browsers do not support this, except Opera	
		}
	}

	
	public native int getImageW();	//returns the size of the max resolution image
	public native int getImageH();	//returns the size of the max resolution image
	public native int getH();		//returns the Height of the image that was last called by getRGBA()
	public native int getW();		//returns the Width of the image that was last called by getRGBA()
	public native int getTileW();	//returns the size of the tiles that make up the image
	public native int getTileH();	//returns the size of the tiles that make up the image
	public native void init(String filename);
	public native int getCompressionSceme();
	public native int getBytesPerPixel();
	public native int[] getRGBA(double bottomLeft_x, double bottomLeft_y, double upperRight_x, double upperRight_y, int aprox_W, int aprox_H);




}