package edu.ucsd.ccdb.glvolume;

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



public class JNIResolutionVolume
{
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
	
	private static String VIRVOLIB = "jmrv";
	
	static 
	{
		try
		{
			System.out.println("Loading '" + VIRVOLIB + "' Library");
			System.loadLibrary(VIRVOLIB);
		}
		catch(SecurityException e)
		{
			System.err.println("Unable to open library (Check for security violation)");
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

	public native int getVolume(String filename);	//retreives the volume to store in the object
	public native void init();
	public native void redrawp();
}