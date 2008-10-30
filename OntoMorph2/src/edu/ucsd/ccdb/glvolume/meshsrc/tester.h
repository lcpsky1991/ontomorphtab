////////////////////////////////////////
// tester.h
////////////////////////////////////////

#ifndef __TESTER_H__
#define __TESTER_H__

#include "camera.h"
#include <vvstopwatch.h>
#include "vvvirtexrendmngr.h"

////////////////////////////////////////////////////////////////////////////////

class Tester 
{
  public:
	Tester(int argc,char **argv);
	~Tester();

	void update();
	void reset();
	void draw();
	void quit();

	// Event handlers
	void resize(int x,int y);
	void keyboard(int key,int x,int y);
	void mouseButton(int btn,int state,int x,int y);
	void mouseMotion(int x,int y);

	// Window management
	int WindowHandle;

	//static unsigned long g_hskim_frame;		///< this is to count frame number

  private:
	int WinX,WinY;

	// Input
	bool LeftDown,MiddleDown,RightDown;
	bool CtrlDown,ShiftDown;
	int MouseX,MouseY;
	int sortSlice;

	// Components
	//Camera Cam;

	vvStopwatch* sw;
	bool printFrameRate;
	float prevRenderTime;
	float renderTime;
	int _lastFrame;

	MipMapVideoLib::vvVirTexMultiRendMngr* g_rendererManager;

};

extern Tester *TESTER;
////////////////////////////////////////////////////////////////////////////////

/*
The 'Tester' is a simple top level application class. It creates and manages a
window with the GLUT extension to OpenGL and it maintains a simple 3D scene
including a camera and some other components.
*/

#endif  // end of __TESTER_H__



