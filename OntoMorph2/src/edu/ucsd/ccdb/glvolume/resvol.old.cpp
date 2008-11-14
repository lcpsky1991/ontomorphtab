
#ifdef WIN32
  #include <windows.h>
#else
  #include <GL/glx.h>
#endif
#include <GL/gl.h>
#include <GL/glu.h>
#include <jni.h>

#include <iostream>


#include "JNIResolutionVolume.h"
#include "vvvirtexrendmngr.h"
#include <stdio.h>
#include <vvgltools.h>
#include "jawt_md.h"

//#include "vvcanvas.h"
#include "tester.h"

using namespace MipMapVideoLib;
using namespace std;
//using namespace vox;

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

	printf("0");
      // Get the drawing surface:
      ds = awt.GetDrawingSurface(env, panel);
      if (ds==NULL)
      {
        cerr << "Error: NULL drawing surface" << endl;
        return;
      }

	printf("1");

      // Lock the drawing surface:
      if ((ds->Lock(ds) & JAWT_LOCK_ERROR) != 0)
      {
        cerr << "Error locking surface" << endl;
        awt.FreeDrawingSurface(ds);
        return;
      }

	printf("2");

      // Get the drawing surface info:
      dsi = ds->GetDrawingSurfaceInfo(ds);
      if (dsi==NULL)
      {
        cerr << "Error getting surface info" << endl;
        ds->Unlock(ds);
        awt.FreeDrawingSurface(ds);
        return;
      }

	printf("3");
	
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

printf("numReturns: %d\n", numReturns);

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
        cerr << "shown\n";
}

//********************************************************
// END GL HELPERS
//********************************************************



//$$$$$$$$==========================================================$$$$$$$$$$$
//$$$$$$$$==================		BEGIN JNI	====================$$$$$$$$$$$
//$$$$$$$$==========================================================$$$$$$$$$$$


void createGLWindow()
{

	printf("a ");
  visual = findVisualDirect();
  if (visual==NULL)
  {
    cerr << "Fatal error: cannot find visual" << endl;
    return;
  }
	printf("b ");

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

  
  cout << "Everything ok\n";

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

///////// DUMMY
JNIEXPORT void JNICALL Java_edu_ucsd_ccdb_glvolume_JNIResolutionVolume_dummy (JNIEnv *env, jobject obj, jobject graphics)
{

	printf("Dummy\n");
}


JNIEXPORT jint JNICALL Java_edu_ucsd_ccdb_glvolume_JNIResolutionVolume_getVolume (JNIEnv *env, jobject obj, jstring )
{

	
}

JNIEXPORT void JNICALL Java_edu_ucsd_ccdb_glvolume_JNIResolutionVolume_init (JNIEnv *env, jobject obj)
{

	visual = NULL;
	
	if(infoJAWT == NULL)	
	{
		infoJAWT = new JawtInfo(env, obj);
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
	
	printf("got info\n");	
	

	makeCurrent();
	
	g_rendererManager = new vvVirTexMultiRendMngr();
	g_rendererManager->setCameraAspect(float(1)/float(1));
    g_rendererManager->load("/home/caprea/Documents/meshTester/meshData2/config.txt"); 	//name of file	
	showError();	
	
	printf("initialized\n");
		

}

void initVolume()
{

}




JNIEXPORT void JNICALL Java_edu_ucsd_ccdb_glvolume_JNIResolutionVolume_redrawp (JNIEnv *env, jobject obj)
{
  	
	makeCurrent();



	initGLEnvironment();

	
// Initialize components

	//glClearColor(0.0, 1.0, 0.0, 0.0);
	glClear(GL_COLOR_BUFFER_BIT);
	
	/*
	glBegin(GL_QUADS);
		glColor3f(0.0, 1.0, 1.0); glVertex3f(0.0, -20.0, 0.0);
		glColor3f(0.0, 1.0, 1.0); glVertex3f(0.0, 20.0, 0.0);
		glColor3f(0.0, 1.0, 1.0); glVertex3f(20.0, 20.0, 0.0);
		glColor3f(0.0, 1.0, 1.0); glVertex3f(20.0, -20.0, 0.0);
	glEnd();
	*/


	g_rendererManager->translateVolume(0,0,0,0);
	g_rendererManager->renderMultipleVolume();
	

	
	glXSwapBuffers(infoJAWT->getDisplay(), infoJAWT->getDrawable());  // implicitly calls glFlush()
	cerr << "redraw" << endl;
}



