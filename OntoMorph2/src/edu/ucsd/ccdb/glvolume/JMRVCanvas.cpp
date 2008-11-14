// 
//	Author: Christopher Aprea
//	
// 
// Contact: Christopher Aprea, caprea@ucsd.edu
// 			Han S Kim, hskim@cs.ucsd.edu
//			Jurgen P. Schulze, jschulze@ucsd.edu
//

#ifdef WIN32
  #include <windows.h>
#else
  #include <GL/glx.h>
#endif
#include <GL/gl.h>
#include <GL/glu.h>
#include <jni.h>

#include <iostream>


#include "JMRVCanvas.h"
#include "vvvirtexrendmngr.h"
#include <stdio.h>
#include <vvgltools.h>
#include "jawt_md.h"


using namespace MipMapVideoLib;
using namespace std;


//*******************************************************
//			BEGIN JAWT
//*******************************************************
 // Helper class for accessing JAWT Information.
class JawtInfo
{
  private:
    JAWT awt;
    JAWT_DrawingSurface* ds;
    JAWT_DrawingSurfaceInfo* dsi;
#ifdef WIN32
    JAWT_Win32DrawingSurfaceInfo* dsi_win;
#else
    JAWT_X11DrawingSurfaceInfo* dsi_x11;
#endif

  public:
    /// Constructor
    JawtInfo(JNIEnv *env, jobject panel)
    {
      // Initialize class attributes:
      ds = NULL;
      dsi = NULL;
#ifdef WIN32
      dsi_win = NULL;
#else
      dsi_x11 = NULL;
#endif

      // Get the AWT:
      awt.version = JAWT_VERSION_1_3;
      if (JAWT_GetAWT(env, &awt) == JNI_FALSE)
      {
        cerr << "Error: AWT not found" << endl;
        return;
      }

      // Get the drawing surface:
      ds = awt.GetDrawingSurface(env, panel);
      if (ds==NULL)
      {
        cerr << "Error: NULL drawing surface" << endl;
        return;
      }


      // Lock the drawing surface:
      if ((ds->Lock(ds) & JAWT_LOCK_ERROR) != 0)
      {
        cerr << "Error locking surface" << endl;
        awt.FreeDrawingSurface(ds);
        return;
      }


      // Get the drawing surface info:
      dsi = ds->GetDrawingSurfaceInfo(ds);
      if (dsi==NULL)
      {
        cerr << "Error getting surface info" << endl;
        ds->Unlock(ds);
        awt.FreeDrawingSurface(ds);
        return;
      }

	
      // Get the platform-specific drawing info:
#ifdef WIN32
      dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;
#else
      dsi_x11 = (JAWT_X11DrawingSurfaceInfo*)dsi->platformInfo;
#endif
    }
    
    /// Destructor
    virtual ~JawtInfo()
    {
      if(ds != NULL)
      {
        // Free the drawing surface info:
        ds->FreeDrawingSurfaceInfo(dsi);

        // Unlock the drawing surface:
        ds->Unlock(ds);

        // Free the drawing surface
        //awt.FreeDrawingSurface(ds);  // TODO: why does this generate break point stops in debug mode?

        ds = NULL;
      }
    }

#ifdef WIN32
    /// Return window handle.
    HWND getHWND()
    {
      if (dsi_win) return dsi_win->hwnd;
      else return NULL;
    }

    /// Return device context handle.
    HDC getHDC()
    {
      if (dsi_win) return dsi_win->hdc;
      else return NULL;
    }
#else
    /// Return display pointer
    Display* getDisplay()
    {
      if (dsi_x11) return dsi_x11->display;
      else return NULL;
    }

    /// Return drawable
    Window getDrawable()
    {
      if (dsi_x11) return dsi_x11->drawable;
      else return 0;
    }
#endif

    void print()
    {
      cerr << "ds  = " << ds << endl;
      cerr << "dsi = " << dsi << endl;
#ifdef WIN32
      cerr << "dsi_win = " << dsi_win << endl;
      if (dsi_win)
      {
        cerr << "dsi_win->hdc = " << dsi_win->hdc << endl;
        cerr << "dsi_win->hwnd = " << dsi_win->hwnd << endl;
      }
#else
      cerr << "dsi_x11 = " << dsi_x11 << endl;
      if (dsi_x11)
      {
        cerr << "dsi_x11->display  = " << dsi_x11->display << endl;
        cerr << "dsi_x11->drawable = " << dsi_x11->drawable << endl;
      }
#endif
    }
};


//*******************************************************
//			END JAWT
//*******************************************************


/////// GLOBALS


JawtInfo *infoJAWT = NULL;
XVisualInfo *visual = NULL;
vvVirTexMultiRendMngr *g_rendererManager;
GLXContext gc = NULL;

//********************************************************
// 	BEGIN GL HELPERS
//********************************************************
XVisualInfo* findVisualDirect()
{

  XVisualInfo* visualInfo = NULL;
  XWindowAttributes xwa;
  XVisualInfo matcher;
  int i, numReturns;
  
  if (XGetWindowAttributes(infoJAWT->getDisplay(), infoJAWT->getDrawable(), &xwa) == 0)
  {
    cerr << "XGetWindowAttributes() failed" << endl;
    return NULL;
  }
  // Set same visual as Java window: (otherwise glXCreateContext() fails)
  matcher.visualid = XVisualIDFromVisual(xwa.visual);   // get visual ID from Java canvas
  matcher.screen = DefaultScreen(infoJAWT->getDisplay());   // set desired screen to current screen
  matcher.depth = xwa.depth;
  visualInfo = XGetVisualInfo(infoJAWT->getDisplay(), VisualIDMask | VisualScreenMask, &matcher, &numReturns); // get the matching visual


  for(i=0; i<numReturns; i++)
  {
    if ((xwa.visual)->visualid == visualInfo[i].visualid)
    {
      return &(visualInfo[i]);
    }
  }

  if (numReturns==0)
    cerr << "No available visuals. Exiting..." << endl;
  else if (i>=numReturns)
    cerr << "No matching visual found ..." << endl;

  return NULL;
}

void initGLEnvironment()
{


  glClearColor(0.0, 0.0, 0.0, 0.0);
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  glOrtho(-1.0f, 1.0f, -1.0f, 1.0f, 100.0f, -100.0f);
  glMatrixMode(GL_MODELVIEW);
  glLoadIdentity();
  glDrawBuffer(GL_BACK);
  printf("finished init\n");
}

void showError()
{

	GLenum err = glGetError(); 
        while (err != GL_NO_ERROR) 
        { 
                fprintf(stderr, "glError: %s caught at %s:%u\n", (char *)gluErrorString(err), __FILE__, __LINE__); \
                err = glGetError(); 
        } 
        cerr << "no err shown\n";
}

//----------------------------------------------------------------------------
/** Conclude drawing of the OpenGL scene in the OpenGL canvas by
  showing the back buffer and sending glFlush().
*/
void swapBuffers()
{

  if(infoJAWT == NULL) return;

#ifdef WIN32
  SwapBuffers(infoJAWT->getHDC());
#else
  glXSwapBuffers(infoJAWT->getDisplay(), infoJAWT->getDrawable());  // implicitly calls glFlush()
#endif

  glFlush();
  glFinish(); // needed to prevent program from painting after mouse move stopped 
}

//********************************************************
// END GL HELPERS
//********************************************************



//$$$$$$$$==========================================================$$$$$$$$$$$
//$$$$$$$$==================		BEGIN JNI	====================$$$$$$$$$$$
//$$$$$$$$==========================================================$$$$$$$$$$$


void createGLWindow()
{

  visual = findVisualDirect();
  if (visual==NULL)
  {
    cerr << "Fatal error: cannot find visual" << endl;
    return;
  }

  gc = glXCreateContext(infoJAWT->getDisplay(), visual, NULL, GL_TRUE);    // also try GL_FALSE
  if (gc == NULL) 
  {
    cerr << "Cannot create GLX context" << endl;
    return;
  }
  else
  {
  	cout << "glXContext created\n";
  }
}


void makeCurrent()
{
	if (glXMakeCurrent(infoJAWT->getDisplay(), infoJAWT->getDrawable(), gc) == false)
	{
		cerr << "Error in glXMakeCurrent" << endl;
		infoJAWT->print();
	}
  
	XMapWindow(infoJAWT->getDisplay(), infoJAWT->getDrawable());
	XSync(infoJAWT->getDisplay(), false);
}

JNIEXPORT jint JNICALL Java_edu_ucsd_ccdb_glvolume_JMRVCanvas_load (JNIEnv *env, jobject, jstring)
{

	
}

JNIEXPORT void JNICALL Java_edu_ucsd_ccdb_glvolume_JMRVCanvas_init (JNIEnv *env, jobject canvas)
{

	visual = NULL;
	
	if(infoJAWT == NULL)	
	{
		infoJAWT = new JawtInfo(env, canvas);
	}
	

  	// Check if system supports OpenGL:
  	if(!glXQueryExtension(infoJAWT->getDisplay(), NULL, NULL))
  	{
    	cerr << "Fatal error: window server does not support OpenGL" << endl;
    	return;
 	}
  	else 
  	{
   		cout << "Window server supports OpenGL" << endl;
	}

	printf("print JAWT info\n");
	infoJAWT->print();
		
	createGLWindow();	//make context current glXMakeCurrent
	

	makeCurrent();
	
	g_rendererManager = new vvVirTexMultiRendMngr();
	g_rendererManager->setCameraAspect(float(1)/float(1));
    g_rendererManager->load("/home/caprea/Documents/meshTester/meshData/config.txt"); 	//name of file	
	
	printf("initialized\n");
		
}


JNIEXPORT void JNICALL Java_edu_ucsd_ccdb_glvolume_JMRVCanvas_renderAll (JNIEnv *env, jobject)
{
	  	showError();
	makeCurrent();
	  	showError();

	// Initialize components
	glClear(GL_COLOR_BUFFER_BIT);
	  	showError();

	g_rendererManager->renderMultipleVolume();
	  	showError();
	
	swapBuffers(); 
	  	showError();
}


JNIEXPORT void JNICALL Java_edu_ucsd_ccdb_glvolume_JMRVCanvas_purge (JNIEnv *env, jobject)
{
	initGLEnvironment();
}


JNIEXPORT void JNICALL Java_edu_ucsd_ccdb_glvolume_JMRVCanvas_translate (JNIEnv *env, jobject obj, jint v, jdouble x, jdouble y, jdouble z)
{
	//public native void translate(int vol, double x, double y, double z);
	
	g_rendererManager->translateVolume(v,x,y,z);
}



JNIEXPORT void JNICALL Java_edu_ucsd_ccdb_glvolume_JMRVCanvas_rotate (JNIEnv *env, jobject obj, jint v, jdouble a, jdouble x, jdouble y, jdouble z)
{
	//public native void rotate(int vol, 	double angle, double x, double y, double z);
	g_rendererManager->rotateVolume(v,a,x,y,z);
}


