////////////////////////////////////////
// tester.cpp
////////////////////////////////////////

#include "tester.h"

#include <GL/glui.h>

#include <vvdebugmsg.h>
#include <vvgltools.h>

#include "gluibar.h"
#include "vvvirtexrendmngr.h"

#define WINDOWTITLE	"Volume Rendering"

using namespace MipMapVideoLib;

////////////////////////////////////////////////////////////////////////////////

Tester *TESTER;

int main(int argc, char **argv) 
{
  // load one image
  if(argc < 2)
  {
	fprintf(stderr, "Usage: %s <image description file>\n", argv[0]);
	exit(0);
  }


  vvDebugMsg::setDebugLevel(1);

  glutInit(&argc, argv);
  TESTER = new Tester(argc,argv);	

  //vvGLTools::checkOpenGLextensions();

  glutMainLoop();

  return 0;
}

////////////////////////////////////////////////////////////////////////////////

// These are really HACKS to make glut call member functions instead of static functions
static void Display()									{TESTER->draw();}
static void Idle()										{TESTER->update();}
static void Resize(int x,int y)							{TESTER->resize(x,y);}
static void Keyboard(unsigned char key,int x,int y)		{TESTER->keyboard(key,x,y);}
static void Mousebutton(int btn,int state,int x,int y)	{TESTER->mouseButton(btn,state,x,y);}
static void Mousemotion(int x, int y)					{TESTER->mouseMotion(x,y);}

////////////////////////////////////////////////////////////////////////////////

Tester::Tester(int argc,char **argv) 
{
  (void)argc;

  WinX=1400;
  WinY=1000;
  LeftDown=MiddleDown=RightDown=false;
  MouseX=MouseY=0;
  printFrameRate = true;
  sw = new vvStopwatch();
  sw->start();
  prevRenderTime = sw->getTime();
  renderTime = sw->getTime();
  _lastFrame = 0;

  // Create the window
  glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH);
  glutInitWindowSize( WinX, WinY );
  glutInitWindowPosition( 0, 0 );
  WindowHandle = glutCreateWindow( WINDOWTITLE );
  glutSetWindowTitle( WINDOWTITLE );
  glutSetWindow( WindowHandle );

  // Background color
  glClearColor( 0.0, 0.0, 0.0, 0.0 );

  // Callbacks
  glutDisplayFunc( Display );
  glutIdleFunc( Idle );
  glutKeyboardFunc( Keyboard );
  glutMouseFunc( Mousebutton );
  glutMotionFunc( Mousemotion );
  glutPassiveMotionFunc( Mousemotion );
  glutReshapeFunc( Resize );

  // Initialize components
  g_rendererManager = new vvVirTexMultiRendMngr();

  g_rendererManager->setCameraAspect(float(WinX)/float(WinY));
  //Cam.SetAspect(float(WinX)/float(WinY));

  vvDebugMsg::msg(2, "g_rendererManager->Load()");
  g_rendererManager->load(argv[1]);


  /****************************************/
  /*				GLUI Menu				*/
  /****************************************/
  GluiBar.setRenderer(g_rendererManager);
  GluiBar.SetUpGlui(WindowHandle);
  GLUI_Master.set_glutIdleFunc( Idle ); 
  GLUI_Master.set_glutDisplayFunc( Display );
  GLUI_Master.set_glutReshapeFunc( Resize );
  GLUI_Master.set_glutKeyboardFunc( Keyboard );

  GluiBar.CallBack(0);
}

////////////////////////////////////////////////////////////////////////////////
Tester::~Tester() {
	glFinish();
	glutDestroyWindow(WindowHandle);
}

////////////////////////////////////////////////////////////////////////////////

void Tester::update() {
	// Update the components in the world
	//Cam.Update();

	// Tell glut to re-display the scene
	glutSetWindow(WindowHandle);
	glutPostRedisplay();
}

////////////////////////////////////////////////////////////////////////////////

void Tester::reset() {
	//Cam.Reset();
	//Cam.SetAspect(float(WinX)/float(WinY));

	g_rendererManager->reset();
}

////////////////////////////////////////////////////////////////////////////////

void Tester::draw() {

  float frameRate;

  // Begin drawing scene
  glViewport(0, 0, WinX, WinY);

  glDrawBuffer(GL_BACK);

  float *bg = GluiBar.GetBGcolor();
  glClearColor(bg[0], bg[1], bg[2], 0.0f);
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	

  // 
  g_rendererManager->renderMultipleVolume();

  //if (GluiBar.GetAxisOn())
	  //drawAxis(1000.0f);

  // Finish drawing scene
  glFinish();
  glutSwapBuffers();

  // frame rate calculation
  renderTime = sw->getTime();

  frameRate = (float)1.0/(renderTime - prevRenderTime);

  /*
  if(g_rendererManager->getCurrentFrame() != _lastFrame)
  {
	g_rendererManager->adjustBrickLimit(frameRate);
	_lastFrame = g_rendererManager->getCurrentFrame();
  }
  */

  if(printFrameRate)
  {
	//vvDebugMsg::msg(1, "RenderTime: ", renderTime, prevRenderTime);
	cerr << "Total Time: " << (renderTime - prevRenderTime) << endl;
	cerr << "Frame Rate: " << frameRate << ", Frame #: " << g_rendererManager->getCurrentFrame() << endl;
  }

  char a;
  //cin >> a;

  prevRenderTime = sw->getTime();

}

////////////////////////////////////////////////////////////////////////////////

void Tester::quit() {
	glFinish();
	delete g_rendererManager;
	delete sw;

	glutDestroyWindow(WindowHandle);

	exit(0);
}

////////////////////////////////////////////////////////////////////////////////

void Tester::resize(int x,int y) {
	WinX = x;
	WinY = y;
	//Cam.SetAspect(float(WinX)/float(WinY));
	g_rendererManager->setCameraAspect(float(WinX)/float(WinY));

	glDrawBuffer(GL_FRONT_AND_BACK);
	float *bg = GluiBar.GetBGcolor();
	glClearColor(bg[0], bg[1], bg[2], 0.0f);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


	//Cam.Draw();
	//g_rendererManager->updatePlaneEquations();
}

////////////////////////////////////////////////////////////////////////////////

void Tester::keyboard(int key,int x,int y) {
  (void)x;
  (void)y;
	static bool hide = false;

	switch(key) {
		case 0x1b:		// Escape
			quit();
			break;
		case 'r':
			reset();
			glutPostRedisplay();
			break;
		case 'f':
			printFrameRate = (printFrameRate == true ? false : true);
			vvDebugMsg::msg(1, "printFrameRate: ", printFrameRate == true ? "true" : "false");
			break;
		case 'x':
			g_rendererManager->cameraViewFromX();
			//Cam.ViewFromX();
			//Cam.Draw();
			//g_rendererManager->updatePlaneEquations();
			glutPostRedisplay();
			break;
		case 'y':
			g_rendererManager->cameraViewFromY();
			//Cam.ViewFromY();
			//Cam.Draw();
			//g_rendererManager->updatePlaneEquations();
			glutPostRedisplay();
			break;
		case 'z':
			g_rendererManager->cameraViewFromZ();
			//Cam.ViewFromZ();
			//Cam.Draw();
			//g_rendererManager->updatePlaneEquations();
			glutPostRedisplay();
			break;
		case 'k':
			g_rendererManager->nextFrame();
			glutPostRedisplay();
			break;
		case 'j':
			g_rendererManager->prevFrame();
			glutPostRedisplay();
			break;
		case 'p':
			g_rendererManager->pausePlay();
			glutPostRedisplay();
			break;
		case 's':
			g_rendererManager->startPlay();
			glutPostRedisplay();
			break;

		//case 'f':
		//	g_rendererManager->PrintFrameRate();
		//	break;
		//case 's':
			//g_rendererManager->PrintNumSlices();
			//break;
		//case 'F':	//reset
			//g_rendererManager->PrintFrameRate(2);
			//break;
		//case 'd':
			//sortSlice = !sortSlice;
			//break;
		case 'h':
			hide? GluiBar.CallBack(1): GluiBar.CallBack(2);
			hide = !hide;
			break;
		default:
			break;
	}
}

////////////////////////////////////////////////////////////////////////////////

void Tester::mouseButton(int btn,int state,int x,int y) 
{
  (void)x;
  (void)y;

  CtrlDown =  (glutGetModifiers() == GLUT_ACTIVE_CTRL);
  ShiftDown =  (glutGetModifiers() == GLUT_ACTIVE_SHIFT);

  if(btn==GLUT_LEFT_BUTTON) 
  {
	LeftDown = (state==GLUT_DOWN);
  }
  else if(btn==GLUT_MIDDLE_BUTTON) 
  {
	MiddleDown = (state==GLUT_DOWN);
  }
  else if(btn==GLUT_RIGHT_BUTTON) 
  {
	RightDown = (state==GLUT_DOWN);
  }

  //printf("ctrlDown: %d, shitfDown:%d\n", CtrlDown, ShiftDown);

}

////////////////////////////////////////////////////////////////////////////////

void Tester::mouseMotion(int nx,int ny) 
{
  int dx = nx - MouseX;
  int dy = -(ny - MouseY);

  MouseX = nx;
  MouseY = ny;

  //printf("LeftDown: %d, rightDown: %d, ctrlDown: %d, shitfDown:%d\n", LeftDown, RightDown, CtrlDown, ShiftDown);

  double rate = GluiBar.GetMouserate();
  //vvDebugMsg::msg(2, "rate: ", (float)rate);
  rate = rate/10;

  if(LeftDown)
  {
	if(CtrlDown)
	{
	  // Left Mouse + Ctrl
	  // moving camera
	  g_rendererManager->setCameraAzimuthIncline( g_rendererManager->getCameraAzimuth() + dx * rate, g_rendererManager->getCameraIncline() - dy * rate);
	  //g_rendererManager->setCameraAzimuth(g_rendererManager->getCameraAzimuth() + dx * rate);
	  //g_rendererManager->setCameraIncline(g_rendererManager->getCameraIncline() - dy * rate);
	  //g_rendererManager->updatePlaneEquations();

	  //Cam.SetAzimuth(Cam.GetAzimuth()+dx*rate);
	  //Cam.SetIncline(Cam.GetIncline()-dy*rate);

	  //Cam.Draw();


	} else {
	  if(ShiftDown)
	  {
		// Left Mouse + Shift
		// rotating volumes in y direction
		g_rendererManager->rotateVolume(g_rendererManager->getCurrentVolume(), -dy*rate*0.1f, 0.0f, 0.0f, 1.0f);
	  }else{

		// Left Mouse
		// translate volume in arbitrary direction (in x and y direction)
		g_rendererManager->translateVolume(g_rendererManager->getCurrentVolume(), dx*rate*30, dy*rate*30, 0.0f);

	  }
	}
  }

  if(RightDown)
  {
	if(CtrlDown)
	{
	  // Right mouse + ctrl
	  // 
	  g_rendererManager->setCameraDistance(g_rendererManager->getCameraDistance() * (1.0f - dy * rate * 0.1f));

	  //Cam.SetDistance(Cam.GetDistance()*(1.0f-dy*rate*0.1f));

	  //Cam.Draw();
	  //g_rendererManager->updatePlaneEquations();

	} else {
	  if(ShiftDown)
	  {
		// Right mouse + shift
		// translate volume in z direction, move forward or backward
		g_rendererManager->translateVolume(g_rendererManager->getCurrentVolume(), 0.0f, 0.0f, -dy*rate*0.1f);
	  }
	  else
	  {
		// right mouse
		g_rendererManager->rotateVolume(g_rendererManager->getCurrentVolume(), dx*rate*0.1f, 0.0f, 1.0f, 0.0f);
		g_rendererManager->rotateVolume(g_rendererManager->getCurrentVolume(), -dy*rate*0.1f, 1.0f, 0.0f, 0.0f);

	  }
	}

  }

  glutPostRedisplay();
}

////////////////////////////////////////////////////////////////////////////////

#if 0
void Tester::LoadSettings(const char *filename)
{
	FILE *fp = fopen(filename, "rt");
	if (fp == NULL)
	{
		cerr << "File NOT found: " << filename << endl;
		return;
	}

	char name[256];
	
	while ( fscanf(fp, "%s", name) > 0 && name[0] != '#')
		g_rendererManager->Load(name);

	// camera
	float az, dis, inc;
	fscanf(fp, "%f %f %f\n", &az, &dis, &inc);
	Cam.SetAzimuth(az);
	Cam.SetDistance(dis);
	Cam.SetIncline(inc);

	// background color
	float* color = GluiBar.GetBGcolor();
	fscanf(fp, "%f %f %f\n", color, color+1, color+2);

	float rot[16];
	float trans[3];
	float tfpos, tfopa;
	float dist[3];
	for (int vn = 0; vn < g_rendererManager->GetNumVol(); vn++)
	{
		int volnum;
		fscanf(fp, "\n#%d", &volnum);
		if (vn != volnum)
		{
			cerr << "Settings File is corrupt!\n";
			break;
		}
		// rotation & translation
		for (int i = 0; i < 4; i++)
		{
			float *entry = rot+i*4;
			fscanf(fp, "%f %f %f %f ;", entry, entry+1, entry+2, entry+3);
		}
		fscanf(fp, "%f %f %f\n", trans, trans+1, trans+2);
		g_rendererManager->SetVolumeMatrix(vn, rot, trans);
		
		// vox distance
		fscanf(fp, "%f %f %f\n", dist, dist+1, dist+2);
		g_rendererManager->SetVoxDist(vn, dist);
		
		// 1-channel transfer function
		fscanf(fp, "%f %f\n", &tfpos, &tfopa);
		g_rendererManager->SetTFparam(vn, tfpos, tfopa);

		/** multi-channel transfer function **/
		fscanf(fp, "%d\n", &g_rendererManager->GetTFmode(vn));

		float gamma=0, hporder=0, hpcutoff=0, tfoffset=0;
		// channel tf
		for (int c = 0; c < g_rendererManager->GetNumChan(vn); c++)
		{	
			float color[3];
			float w = 0.0f;
			fscanf(fp, "%f\n", &w);
			fscanf(fp, "%f %f %f\n", color, color+1, color+2);
			g_rendererManager->SetChanColor(vn, c, color);
			g_rendererManager->SetWeight(vn, c, w);

			fscanf(fp, "%f %f %f %f\n", &gamma, &hporder, &hpcutoff, &tfoffset);
			g_rendererManager->SetTFparam(vn, c, gamma, hporder, hpcutoff, tfoffset);
			
		}
		// opacity tf
		fscanf(fp, "%f %f %f %f\n", &gamma, &hporder, &hpcutoff, &tfoffset);
		g_rendererManager->SetTFparam(vn, gamma, hporder, hpcutoff, tfoffset);
		g_rendererManager->UpdateTransferFunction(vn);
	}
	fclose(fp);
}


void Tester::SaveSettings(const char* filename)
{
	FILE *fp = fopen(filename,"wt");

	if (fp == NULL)
	{
		cerr << "File NOT accessible: " << filename << endl;
		return;
	}

	for (int i = 0; i < g_rendererManager->GetNumVol(); i++)
	{
		char* name = g_rendererManager->GetFilename(i);
		fprintf(fp, "%s ", name);
	}

	// camera
	fprintf(fp, "\n#\n%f %f %f\n", 
		Cam.GetAzimuth(), 
		Cam.GetDistance(),
		Cam.GetIncline()
		);

	// background Color
	float* color = GluiBar.GetBGcolor();
	fprintf(fp, "%f %f %f\n", color[0], color[1], color[2]);

	float rot_trans[19];	
	float dist[3];
	for (int vn = 0; vn < g_rendererManager->GetNumVol(); vn++)
	{
		fprintf(fp, "\n#%i\n", vn);

		// rotation & translation
		g_rendererManager->GetVolumeMatrix(vn, rot_trans, rot_trans+16);
		for (int i = 0; i < 19; i++)
		{
			fprintf(fp, "%f ", rot_trans[i]);
			if (i%4 == 3)
				fprintf(fp, ";\n");
		}

		// vox distance
		g_rendererManager->GetVoxDist(vn, dist);
		fprintf(fp, "\n%f %f %f\n", dist[0], dist[1], dist[2]);

		// 1-channel transfer function
		float tfpos, tfopa;
		g_rendererManager->GetTFparam(vn, tfpos, tfopa);
		fprintf(fp, "%f %f\n", tfpos, tfopa);

		/** multi-channel transfer function **/
		fprintf(fp, "%d\n", g_rendererManager->GetTFmode(vn));

		float gamma, hporder, hpcutoff, tfoffset;
		// channel tf
		for (int c = 0; c < g_rendererManager->GetNumChan(vn); c++)
		{	
			float color[3];
			g_rendererManager->GetChanColor(vn, c, color);
			fprintf(fp, "%f\n", g_rendererManager->GetWeight(vn, c));
			fprintf(fp, "%f %f %f\n", color[0], color[1], color[2]);

			g_rendererManager->GetTFparam(vn, c, gamma, hporder, hpcutoff, tfoffset);
			fprintf(fp, "%f %f %f %f\n", gamma, hporder, hpcutoff, tfoffset);
		}
		// opacity tf
		g_rendererManager->GetTFparam(vn, gamma, hporder, hpcutoff, tfoffset);
		fprintf(fp, "%f %f %f %f\n", gamma, hporder, hpcutoff, tfoffset);

	}
	
	fclose(fp);
}
#endif

