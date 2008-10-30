#include "vvvirtexrendmngr.h"
#include <iostream>
#include <iomanip>

#include <string>

#include <stdlib.h>
#include <assert.h>
#include <math.h>
#if defined(__linux) || defined(LINUX)
#define GL_GLEXT_PROTOTYPES 1
#define GL_GLEXT_LEGACY 1
#include <string.h>
#endif

#include "vvopengl.h"
#include "vvglext.h"

#include "vvdynlib.h"
#if !defined(_WIN32) && !defined(__APPLE__)
#include <dlfcn.h>
#include <GL/glx.h>
#endif

#ifdef VV_DEBUG_MEMORY
#include <crtdbg.h>
#define new new(_NORMAL_BLOCK,__FILE__, __LINE__)
#endif

#ifdef _COVISE_PLUGIN
#include <kernel/coVRPluginSupport.h>
#endif


using namespace std;
using namespace MipMapVideoLib;

//vvVirTexMultiRendMngr* g_rendererManager;

vvVirTexMultiRendMngr::vvVirTexMultiRendMngr()
{
  _dim = 0;
  _numVolume = 0;
  _numFrame = 0;
  _currentVolume = 0;
  _currentFrame = 0;

  _playSpeed = 1.0/25.0;
  _isOnPlay = false;
  _currentBrickLimit = 40;

  _sw = new vvStopwatch();
  _sw->start();

  _lastRenderTime = 0.0f;
  _lastComputeTime = 0.0f;
  _lastPlaneSortingTime = 0.0f;
  _lastGLdrawTime = 0.0f;
  _lastChange = _sw->getTime();

  _transformMatrix.identity();

  extMinMax = vvGLTools::isGLextensionSupported("GL_EXT_blend_minmax");
  extBlendEquation = vvGLTools::isGLextensionSupported("GL_EXT_blend_equation");
  if (extBlendEquation) glBlendEquationVV = (glBlendEquationEXT_type*)vvDynLib::glSym("glBlendEquationEXT");
  else glBlendEquationVV = (glBlendEquationEXT_type*)vvDynLib::glSym("glBlendEquation");
}

vvVirTexMultiRendMngr::~vvVirTexMultiRendMngr()
{
  vvDebugMsg::msg(1, "vvVirTexMultiRendMngr::~vvVirTexMultiRendMngr()");
  _fragProgramPathMap.clear();

  for(int v = 0; v < _numVolume; v++)
	delete _rendererList[v];
  delete[] _rendererList;

  delete _pixelToVoxelRatio;
  delete _showTexture;
  delete _showBoundary;
  delete _glslShader;
  delete _brickManager;
  delete _sw;
}

void vvVirTexMultiRendMngr::load(const char* descFile)
{
  assert(descFile);

  _glslShader = new vvGLSL();

  if(loadConfigFile(descFile) < 0)
  {
	vvDebugMsg::msg(2, "loading config file error");
	exit(0);
  }

  _brickManager = new BrickManager(_dim, _numVolume, _numFrame, _volumeInfoList);

  if(_brickManager->initialize() < 0)
  {
	vvDebugMsg::msg(2, "brickmanager initialized failed");
	exit(0);
  }
  vvDebugMsg::msg(2, "_brickManager is successfully created");

  _showBoundary = new bool[_numVolume];
  _showTexture = new bool[_numVolume];
  _pixelToVoxelRatio = new int[_numVolume];

  for(int v = 0; v < _numVolume; v++)
  {
	_showBoundary[v] = true;
	_showTexture[v] = true;
	_pixelToVoxelRatio[v] = 16;
  }

  // load shaders
  for(map<int, std::string, less<int> >::iterator itr = _fragProgramPathMap.begin(); itr != _fragProgramPathMap.end(); ++itr)
  {
	string programPath = (*itr).second;
	GLuint fragProgram = _glslShader->loadShader(programPath.c_str());
	if(fragProgram == 0)
	{
	  vvDebugMsg::msg(1, "Loading shader program failed: ", programPath.c_str());
	  exit(0);
	}
	_fragProgramMap[(*itr).first] = fragProgram;
  }

  // initialize renderers
  _rendererList = new vvVirTexRend**[_numVolume];

  for(int v = 0; v < _numVolume; v++)
  {
	_rendererList[v] = new vvVirTexRend*[_numFrame];

	for(int f = 0; f < _numFrame; f++)
	{
	  vvVirTexRend* aRenderer;
	  vvRenderState renderState;

	  GLuint fragProgram = _fragProgramMap[_brickManager->getVolDesc(v, f)->chan];
	  if(fragProgram == 0)
	  {
		vvDebugMsg::msg(1, "There is no corresponding fragment shader program for this volume");
		exit(0);
	  }

	  aRenderer = new vvVirTexRend(_brickManager->getVolDesc(v, f), _brickManager, renderState, v, f, _glslShader, fragProgram, _currentBrickLimit);
	  aRenderer->_renderState._showBricks = true;

	  _rendererList[v][f] = aRenderer;
	}
  }

  // activate first frames
  //_brickManager->activateReaders(0);
#if 1
  if(_numFrame>10)
	for(int i = 0; i < _numFrame && i < FRAME_PRELOAD; i++)
	  _brickManager->activateReaders(i);
#endif

}


void vvVirTexMultiRendMngr::renderMultipleVolume()
{

  // sets up projection & viewing matrices
  _camera.draw();

  glMatrixMode(GL_MODELVIEW);
  glLoadIdentity();

  // this guarantees one frame is displayed at least 1/30 second
#if 1
  if(_isOnPlay)
  {
	clock_t now = clock();
	float dt = float(now - _lastChange) / float(CLOCKS_PER_SEC);

	if(dt > _playSpeed)
	{
	  nextFrame();
	}
  }
#else
  nextFrame();
#endif

  for(int v = 0; v < _numVolume; v++)
  {
	//vvDebugMsg::msg(1, "rendering: ", _currentFrame);
	_rendererList[v][_currentFrame]->setBrickLimit(_currentBrickLimit);
	_rendererList[v][_currentFrame]->_renderState._showBricks = _showBoundary[v];
	_rendererList[v][_currentFrame]->_renderState._showTexture = _showTexture[v];
	_rendererList[v][_currentFrame]->setPixelToVoxelRatio(_pixelToVoxelRatio[v]);
	_rendererList[v][_currentFrame]->renderMultipleVolume();
  }
  cerr << "Memory Loading: " << _brickManager->getMemoryLoadingTime() << endl;
}

void vvVirTexMultiRendMngr::adjustBrickLimit(float frameRate)
{
  if(frameRate < IDEAL_FRAMERATE - _epsilon)
  {
	if(_currentBrickLimit > 5)
	  _currentBrickLimit = _currentBrickLimit - 1;
  } 
  else if (frameRate > IDEAL_FRAMERATE + _epsilon)
  {
	if(_currentBrickLimit < 80)
	  _currentBrickLimit ++;
  }

  //cerr << "currentBrickLimit: " << _currentBrickLimit << endl;
}

void vvVirTexMultiRendMngr::resetBrickLimit()
{
  _currentBrickLimit = 30;
}

void vvVirTexMultiRendMngr::reset()
{
  _camera.reset();

  /*
  for(int f = 0; f < numFrame; f++)
  {
	rendererList[f]->translation.set(0.0, 0.0, 0.0);
	rendererList[f]->rotation.identity();
  }

  TranslateVolume(-1 * _brickManager->getVolumeSizeX() / 2.0,
	  			  -1 * _brickManager->getVolumeSizeY() / 2.0,
				  0);
  */
}

void vvVirTexMultiRendMngr::translateVolume(int vol, float x, float y, float z)
{
  for(int f = 0; f < _numFrame; f++)
  {
	vvMatrix invPM;
	_rendererList[vol][f]->getProjectionMatrix(&invPM);
	invPM.invert();

	vvVector4 dir;
	dir.set(x, y, z, 0.0f);                 // (0|0|1) is normal on projection plane
	dir.multiply(&invPM);
	
	_rendererList[vol][f]->translation.add(dir[0], dir[1], dir[2]);
  }
}

void vvVirTexMultiRendMngr::rotateVolume(int vol, float angle, float x, float y, float z)
{
  for(int f = 0; f < _numFrame; f++)
  {
	vvMatrix invPM;
	_rendererList[vol][f]->getProjectionMatrix(&invPM);
	invPM.invert();

	vvMatrix volRotation(&_rendererList[vol][f]->rotation);
	volRotation.invertOrtho();

	vvVector4 dir;
	dir.set(x, y, z, 0.0f);
	dir.multiply(&invPM);
	dir.multiply(&volRotation);

	vvVector3 axis(dir[0], dir[1], dir[2]);
	axis.normalize();

	_rendererList[vol][f]->rotation.rotate(angle, &axis);
  }
}


void vvVirTexMultiRendMngr::setCameraAzimuthIncline(float a, float i)
{
  _camera.setAzimuth(a);
  _camera.setIncline(i);
  // update projection matrix
  _camera.draw();
  //updatePlaneEquations();
}

void vvVirTexMultiRendMngr::setCameraDistance(float d)
{
  _camera.setDistance(d);
  _camera.draw();
  //updatePlaneEquations();
}

void vvVirTexMultiRendMngr::setCameraAspect(float r)
{
  _camera.setAspect(r);
  _camera.draw();
  //updatePlaneEquations();
}

void vvVirTexMultiRendMngr::cameraViewFromX()
{
  _camera.viewFromX();
  _camera.draw();
  //updatePlaneEquations();
}

void vvVirTexMultiRendMngr::cameraViewFromY()
{
  _camera.viewFromY();
  _camera.draw();
  //updatePlaneEquations();
}

void vvVirTexMultiRendMngr::cameraViewFromZ()
{
  _camera.viewFromZ();
  _camera.draw();
  //updatePlaneEquations();
}

void vvVirTexMultiRendMngr::setShowBoundary(int vol, int toggle)
{
  _showBoundary[vol] = (bool)toggle;
}

void vvVirTexMultiRendMngr::setShowTexture(int vol, int toggle)
{
  _showTexture[vol] = (bool)toggle;
}

void vvVirTexMultiRendMngr::nextFrame()
{
#if 1
  if(_numFrame > 10)
  {
	// close the current frame readers
	int activateFrame = (_currentFrame + FRAME_PRELOAD + _numFrame) % _numFrame;
	_brickManager->activateReaders(activateFrame);
	_brickManager->deactivateReaders(_currentFrame);
  }
#endif

  _currentFrame = (_currentFrame + 1 + _numFrame) % _numFrame;
  _lastChange = _sw->getTime();

  //cerr << "currentFrame: " << _currentFrame << endl;

 //assert(_currentFrame < _numFrame && _currentFrame >= 0);
}

void vvVirTexMultiRendMngr::prevFrame()
{
  _lastChange = _sw->getTime();;

  _currentFrame = (_currentFrame - 1 + _numFrame) % _numFrame;
  //vvDebugMsg::msg(2, "currentvolume: ", currentFrame);
  //assert(currentFrame < numFrame && currentFrame >= 0);
}

void vvVirTexMultiRendMngr::setCurrentFrame(int frame)
{
  assert(frame < _numFrame && frame >= 0);

  _brickManager->setCurrentFrame(frame);

  // close all readers
  for(int i = 0; i < FRAME_PRELOAD; i++)
  {
	int activeFrame = (_currentFrame + i + _numFrame) % _numFrame;
	//_brickManager->deactivateReaders(activeFrame);
	_brickManager->requestDeactivateReaders(activeFrame);
  }

  _currentFrame = frame;

  // activate readers
  for(int i = 0; i < FRAME_PRELOAD; i++)
  {
	int activeFrame = (_currentFrame + i + _numFrame) % _numFrame;
	//_brickManager->activateReaders(activeFrame);
	_brickManager->requestActivateReaders(activeFrame);
  }
}

void vvVirTexMultiRendMngr::increasePlaySpeed() 
{ 
  if(_playSpeed < 5) 
	_playSpeed += 0.1;
  //vvDebugMsg::msg(2, "_playSpeed:", _playSpeed);
}
	
void vvVirTexMultiRendMngr::decreasePlaySpeed() 
{ 
  if(_playSpeed > 0.1) 
	_playSpeed -= 0.1;

  //vvDebugMsg::msg(2, "_playSpeed:", _playSpeed);
}

float vvVirTexMultiRendMngr::getChannelHue(int vol, int chan)
{
  return _rendererList[vol][_currentFrame]->getChannelHue(chan);
}

void vvVirTexMultiRendMngr::setChannelHue(int vol, int chan, float value)
{
  for(int f = 0; f < _numFrame; f++)
	_rendererList[vol][f]->setChannelHue(chan, value);
}

int vvVirTexMultiRendMngr::getNumChannel(int vol)
{
  return _brickManager->getVolDesc(vol, 0)->chan;
}

int vvVirTexMultiRendMngr::isActiveChannel(int vol, int chan)
{
  return _rendererList[vol][0]->isActiveChannel(chan);
}

void vvVirTexMultiRendMngr::setActiveChannel(int vol, int chan, int value)
{
  for(int f = 0; f < _numFrame; f++)
	_rendererList[vol][f]->setActiveChannel(chan, value);
}

void vvVirTexMultiRendMngr::setPixelToVoxelRatio(int vol, int ratio)
{
  _pixelToVoxelRatio[vol] = ratio;
}

void vvVirTexMultiRendMngr::setBrickLimit(int vol, int limit)
{
  (void)vol;
  _currentBrickLimit = limit;
}


//============================================================
// Misc Methods 
//============================================================
/** loadConfigFiles
* this function loads configuration file to get information about data files
* can be reimplemented reading xml file
* TODO: XML
*/
int vvVirTexMultiRendMngr::loadConfigFile(const char* configFileName)
{
  assert(configFileName != NULL);

  FILE* fp;
  char line[1024];

  if((fp = fopen(configFileName, "r")) == NULL)
  {
	vvDebugMsg::msg(1, "Description file does not exist: ", configFileName);
	return -1;
  }


  // START PARSING ===============================================

  //==============================================================
  // dimension and number of volumes
  do
  {
	// skip comment lines
  } while(fgets(line, sizeof(line), fp) != NULL && (line[0] == '#' || line[0] == '\r' || line[0] == '\n'));

  //printf("read: '%s'\n", line);

  if(sscanf(line, "%d %d %d\n", &_dim, &_numVolume, &_numFrame) < 1)
  {
	vvDebugMsg::msg(1, "Illegal description file format at line: ", line);
	return -1;
  }

  vvDebugMsg::msg(2, "(dim, numVolume, numFrame)=", _dim, _numVolume, _numFrame);


  _volumeInfoList = new VolumeInfo**[_numVolume];
  for(int i = 0; i < _numVolume; i++)
	_volumeInfoList[i] = new VolumeInfo*[_numFrame];

  // we only support 2D and 3D dataset!
  assert(_dim == 2 || _dim == 3);
  //==============================================================

  do
  {
	// skip comment lines
  } while(fgets(line, sizeof(line), fp) != NULL && (line[0] == '#' || line[0] == '\r' || line[0] == '\n'));


  //==============================================================
  // load shaders
  do
  {
	int nchan;
	char buf[1024];
	if(sscanf(line, "%d %s\n", &nchan, buf) < 1)
	{
	  vvDebugMsg::msg(1, "Illegal description file format at line: ", line);
	  return -1;
	}
	//===========================================================
	/*
	GLuint fragProgram = shader->loadShader(&buf[0]);
	if(fragProgram == 0)
	{
	  vvDebugMsg::msg(1, "Loading shader program failed: ", buf);
	  return -1;
	}
	*/

	vvDebugMsg::msg(2, "Shader Program is loaded: ", buf);
	_fragProgramPathMap[nchan] = string(buf);
	//===========================================================

  } while(fgets(line, sizeof(line), fp) != NULL && (line[0] != '#' && line[0] != '\r' && line[0] != '\n'));
  //==============================================================


  // for each volume
  for(int i = 0; i < _numVolume; i++)
  {
	for(int f = 0; f < _numFrame; f++)
	{
	  VolumeInfo* vi = new VolumeInfo();

	  do
	  {
		// skip comment lines
	  } while(fgets(line, sizeof(line), fp) != NULL && (line[0] == '#' || line[0] == '\r' || line[0] == '\n'));

	  // get maxMipLevel, brickSize, numChannel
	  if(sscanf(line, "%d %d %d %d %d", &vi->volIndex, &vi->frameIndex, &vi->brickSize, &vi->maxMipLevel, &vi->numChannel) < 4)
	  {
		vvDebugMsg::msg(1, "Illegal description file format at line: ", line);
		return -1;
	  }

	  //vvDebugMsg::msg(2, "(volIndex, frameIndex, brickSize, numChannel): ", vi->volIndex, vi->frameIndex, vi->brickSize, vi->numChannel);

	  // for each level
	  for(int j = 0; j <= vi->maxMipLevel; j++)
	  {
		FileInfo fi;

		line[0] = '\0';
		do
		{
		  // skip comment lines
		} while(fgets(line, sizeof(line), fp) != NULL && (line[0] == '#' || line[0] == '\n' || line[0] == '\r'));

		//printf("read: '%s'\n", line);

		if(_dim == 3)
		{
		  if(sscanf(line, "%d %s %d %d %d", &fi.level, fi.name, &fi.sizeX, &fi.sizeY, &fi.sizeZ) < 5)
		  {
			vvDebugMsg::msg(1, "***Illegal description file format at line: ", line);
			return -1;
		  }
		} 
		// we will have a separate program for 2D rendering...
		else if(_dim == 2)
		{
		  if(sscanf(line, "%d %s %d %d", &fi.level, fi.name, &fi.sizeX, &fi.sizeY) < 4)
		  {
			vvDebugMsg::msg(1, "***Illegal description file format at line: ", line);
			return -1;
		  }
		  fi.sizeZ = 0;
		} else {
		  vvDebugMsg::msg(1, "*** Dimension must be either 2 or 3");
		  return -1;
		}

		//printf("ConfigFile: %d %s %d %d %d\n", fi.level, fi.name, fi.sizeX, fi.sizeY, fi.sizeZ);
		vi->descList.push_back(fi);
	  }

	  //printf("vi desc list size: %d\n", vi.descList.size());

	  _volumeInfoList[i][f] = vi;
	}
  }

  //printf("volumeInfoList.size: %d\n", volumeInfoList.size());

  fclose(fp);

  return 0;
}

#if 0
void vvVirTexMultiRendMngr::updatePlaneEquations()
{
  GLfloat glmatrix[16];
  vvMatrix mvMatrix;
  vvMatrix projMatrix;
  //vvMatrix transformMatrix;

  // 1. modelview matrix
  glGetFloatv(GL_MODELVIEW_MATRIX, glmatrix);
  mvMatrix.getGL((float*) glmatrix);
  mvMatrix.translate(0.0, 0.0, -_camera.getDistance());
  //mvMatrix.rotate(_camera.getIncline(), 1.0f, 0.0f, 0.0f);
  //mvMatrix.rotate(_camera.getAzimuth(), 0.0f, 1.0f, 0.0f);

  //mvMatrix.print("mvMatrix: ");

  // 2. projection matrix
  glGetFloatv(GL_PROJECTION_MATRIX, glmatrix);
  projMatrix.getGL((float*) glmatrix);

  //projMatrix.print("projMatirx: ");

  // projMatrix * mvMatrix
  _transformMatrix.identity();
  _transformMatrix.multiplyPost(&mvMatrix);
  _transformMatrix.multiplyPost(&projMatrix);

  //_transformMatrix.print("transformMatrix: ");

  planeEqs[0][0] = _transformMatrix.e[0][3] - _transformMatrix.e[0][0];
  planeEqs[0][1] = _transformMatrix.e[1][3] - _transformMatrix.e[1][0];
  planeEqs[0][2] = _transformMatrix.e[2][3] - _transformMatrix.e[2][0];
  planeEqs[0][3] = _transformMatrix.e[3][3] - _transformMatrix.e[3][0];

  planeEqs[1][0] = _transformMatrix.e[0][3] + _transformMatrix.e[0][0];
  planeEqs[1][1] = _transformMatrix.e[1][3] + _transformMatrix.e[1][0];
  planeEqs[1][2] = _transformMatrix.e[2][3] + _transformMatrix.e[2][0];
  planeEqs[1][3] = _transformMatrix.e[3][3] + _transformMatrix.e[3][0];

  planeEqs[2][0] = _transformMatrix.e[0][3] + _transformMatrix.e[0][1];
  planeEqs[2][1] = _transformMatrix.e[1][3] + _transformMatrix.e[1][1];
  planeEqs[2][2] = _transformMatrix.e[2][3] + _transformMatrix.e[2][1];
  planeEqs[2][3] = _transformMatrix.e[3][3] + _transformMatrix.e[3][1];

  planeEqs[3][0] = _transformMatrix.e[0][3] - _transformMatrix.e[0][1];
  planeEqs[3][1] = _transformMatrix.e[1][3] - _transformMatrix.e[1][1];
  planeEqs[3][2] = _transformMatrix.e[2][3] - _transformMatrix.e[2][1];
  planeEqs[3][3] = _transformMatrix.e[3][3] - _transformMatrix.e[3][1];

  planeEqs[4][0] = _transformMatrix.e[0][3] + _transformMatrix.e[0][2];
  planeEqs[4][1] = _transformMatrix.e[1][3] + _transformMatrix.e[1][2];
  planeEqs[4][2] = _transformMatrix.e[2][3] + _transformMatrix.e[2][2];
  planeEqs[4][3] = _transformMatrix.e[3][3] + _transformMatrix.e[3][2];

  planeEqs[5][0] = _transformMatrix.e[0][3] - _transformMatrix.e[0][2];
  planeEqs[5][1] = _transformMatrix.e[1][3] - _transformMatrix.e[1][2];
  planeEqs[5][2] = _transformMatrix.e[2][3] - _transformMatrix.e[2][2];
  planeEqs[5][3] = _transformMatrix.e[3][3] - _transformMatrix.e[3][2];


}

int vvVirTexMultiRendMngr::viewFrustumCullingTest(BrickInfo* brick)
{
  int i, j;
  int culled;
  int numPoints; 
  int mask;
  
  numPoints = (int)powf(2.0, _dim);
  mask = 0xf0;
  if(_dim == 3)
	mask = 0x00;

  vvMatrix transformMatrix;
  transformMatrix.identity();
  transformMatrix.translate(&(_rendererList[0][_currentFrame]->translation));
  //transformMatrix.multiplyPre(&(_rendererList[0][_currentFrame]->rotation));

  for(i = 0; i < 6; i++) {
	culled = 0;

	cerr << "equations " << i << ": " << planeEqs[i][0] << ", " << planeEqs[i][1] << ", " << planeEqs[i][2] << ", " << planeEqs[i][3] << endl;

	for(j = 0; j < numPoints; j++)
	{

	  vvVector4 coord, coord2;
		  
	  coord.set((brick->getCoord(j))[0], (brick->getCoord(j))[1], (brick->getCoord(j))[2], 1.0);
	  coord.multiply(&transformMatrix);

	  //cerr << "cull test " << j << ": " << (brick->getCoord(j))[0] << ", " << (brick->getCoord(j))[1] << ", " << (brick->getCoord(j))[2] << endl;
	  cerr << "cull test " << j << ": " << coord[0] << ", " << coord[1] << ", " << coord[2] << endl;

	  float eval = planeEqs[i][0] * coord[0] + 
		  		   planeEqs[i][1] * coord[1] + 
				   planeEqs[i][2] * coord[2] + 
				   planeEqs[i][3];
	  /*
	  float eval = planeEqs[i][0] * (brick->getCoord(j))[0] + 
		  		   planeEqs[i][1] * (brick->getCoord(j))[1] + 
				   planeEqs[i][2] * (brick->getCoord(j))[2] + 
				   planeEqs[i][3];
				   */

	  if(eval < 0.)
	  /*
	  bool eval = (planeEqs[i][0] * (brick->getCoord(j))[0] + 
		  		   planeEqs[i][1] * (brick->getCoord(j))[1] + 
				   planeEqs[i][2] * (brick->getCoord(j))[2] + 
				   planeEqs[i][3] < 0.);

	  if(eval)
				   */
	  {
		cerr << "culled: " << eval << ": " << i << ", " << j << endl;
		culled |= 1<<j;
	  }
	}

	if((culled | mask) == 0xff)
	{
	  cerr << "culled: " << culled << endl;
	  return 1;
	}
  }
  
  /*
  cerr << "survived" << endl;
  for(j = 0; j < numPoints; j++)
  {
	  cerr << "survived: " << j << ": " << (brick->getCoord(j))[0] << ", " << (brick->getCoord(j))[1] << ", " << (brick->getCoord(j))[2] << endl;
	  for(int i = 0; i < 6; i++)
	  {
		float eval = planeEqs[i][0] * (brick->getCoord(j))[0] + 
		  		   planeEqs[i][1] * (brick->getCoord(j))[1] + 
				   planeEqs[i][2] * (brick->getCoord(j))[2] + 
				   planeEqs[i][3];
		cerr << "eval for " << j << ": " << eval << endl;
	    cerr << "equations " << i << ": " << planeEqs[i][0] << ", " << planeEqs[i][1] << ", " << planeEqs[i][2] << ", " << planeEqs[i][3] << endl;
	  }
  }
  */


  return 0;
}
//#else

int vvVirTexMultiRendMngr::viewFrustumCullingTest(BrickInfo* brick)
{
  int culled[6] = {0, 0, 0, 0, 0, 0};
  vvMatrix objectMatrix;
  objectMatrix.identity();
  objectMatrix.translate(&(_rendererList[0][_currentFrame]->translation));


  for(int j = 0; j < 4; j++)
  {
	vvVector4 coord;
	coord.set((brick->getCoord(j))[0], (brick->getCoord(j))[1], (brick->getCoord(j))[2], 1.0);
	coord.multiply(&objectMatrix);
	//cerr << "cull test " << j << ": " << coord[0] << ", " << coord[1] << ", " << coord[2] << endl;

	coord.multiply(&_transformMatrix);

	if(coord[0]/coord[3] < -1.0)
	{
	  culled[0] |= 1<<j;
	}

	if(coord[0]/coord[3] > 1.0)
	{
	  culled[1] |= 1<<j;
	}

	if(coord[1]/coord[3] < -1.0)
	{
	  culled[2] |= 1<<j;
	}
	if(coord[1]/coord[3] > 1.0)
	{
	  culled[3] |= 1<<j;
	}

	/* too close
	if(coord[2]/coord[3] < -1.0)
	{
	  //cerr << "cull 5" << endl;
	  culled[4] |= 1<<j;
	}
	*/
	if(coord[2]/coord[3] > 1.0)
	{
	  culled[5] |= 1<<j;
	}
  }

  for(int i = 0; i < 5; i++)
  {
	if(culled[i] == 0x0f)
	{
#if 0
	  cerr << "culled: " << endl;
	  for(int j = 0; j < 4; j++)
	  {
	
		vvVector4 coord;
	
		cerr << " Coord " << j << endl;
		cerr << "  OC: " << i << ": " << (brick->getCoord(j))[0] << ", " << (brick->getCoord(j))[1] << ", " << (brick->getCoord(j))[2] << endl;

		coord.set((brick->getCoord(j))[0], (brick->getCoord(j))[1], (brick->getCoord(j))[2], 1.0);
		coord.multiply(&objectMatrix);

		cerr << "  WC: " << coord[0] << ", " << coord[1] << ", " << coord[2] << ", " << coord[3] << endl;

		coord.multiply(&_transformMatrix);

		//cerr << "  PC coord: " << coord[0] << ", " << coord[1] << ", " << coord[2] << ", " << coord[3] << endl;
		cerr << "  Norm coord: " << coord[0]/coord[3] << ", " << coord[1]/coord[3] << ", " << coord[2]/coord[3] << endl << endl;
	  }
#endif
	  return 1;
	}
  }

#if 0
  cerr << "not culled: " << endl;
  for(int j = 0; j < 4; j++)
  {

	cerr << " Coord " << j << endl;
	cerr << "  OC: " << (brick->getCoord(j))[0] << ", " << (brick->getCoord(j))[1] << ", " << (brick->getCoord(j))[2] << endl;

	vvVector4 coord;
	coord.set((brick->getCoord(j))[0], (brick->getCoord(j))[1], (brick->getCoord(j))[2], 1.0);
	coord.multiply(&objectMatrix);

	cerr << "  WC: " << coord[0] << ", " << coord[1] << ", " << coord[2] << ", " << coord[3] << endl;

	coord.multiply(&_transformMatrix);

	cerr << "  Norm coord: " << coord[0]/coord[3] << ", " << coord[1]/coord[3] << ", " << coord[2]/coord[3] << endl << endl;
	
  }
#endif

  return 0;
}

#endif


