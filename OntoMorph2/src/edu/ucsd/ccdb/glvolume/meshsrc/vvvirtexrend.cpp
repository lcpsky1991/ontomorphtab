// Virvo - Virtual Reality Volume Rendering
// Copyright (C) 1999-2003 University of Stuttgart, 2004-2005 Brown University
// Contact: Jurgen P. Schulze, jschulze@ucsd.edu
// 			Han S Kim, hskim@cs.ucsd.edu
//
// This file is part of Virvo.
//
// Virvo is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library (see license.txt); if not, write to the
// Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

#include "vvvirtexrend.h"
#include <vvstopwatch.h>

#include <math.h>
#include <list>
#include <queue>
#include <vector>

using namespace std;
using namespace MipMapVideoLib;

/** vvVirTexRend Constructor
  Virtual Texture Renderer

*/
vvVirTexRend::vvVirTexRend(vvVolDesc* vd, vvRenderState renderState) : vvRenderer(vd, renderState)
{
  vvDebugMsg::msg(2, "vvVirTexRend::vvVirTexRend()");
  vvDebugMsg::msg(2, "vvVirTexRend::vvVirTexRend(): ", vd->getFilename());

  init();
}

vvVirTexRend::vvVirTexRend(vvVolDesc* vd, BrickManager* bm, vvRenderState renderState, int _volNum, int _frameNum, vvGLSL* _shader, GLuint _fragProgram, int brickLimit) : vvRenderer(vd, renderState)
{
  brickManager = bm;
  assert(brickManager != NULL);

  volNum = _volNum;
  frameNum = _frameNum;
  shader = _shader;
  fragProgram = _fragProgram;
  _pixelToVoxelRatio = 16;
  _brickLimit = brickLimit;

  vd = brickManager->getVolDesc(volNum, frameNum);
  assert(vd != NULL);

  // these are correct values
  //cerr << "vvVirTexRend::vvVirTexRend: volNum: " << volNum << " frameNum: " << frameNum << endl;
  //cerr << "vvVirTexRend::vvVirTexRend: " << vd->getFilename() << endl;

  rotation.identity();
  translation.set(0.0, 0.0, 0.0);
  
  channelHue = new float[vd->chan];

  // TODO: initial value for each channel?
  if(vd->chan < 4)
  {
	channelHue[0] = (float)0.0;
	channelHue[1] = (float)0.3333;
	channelHue[2] = (float)0.6666;
  }

  for(int i = 4; i < vd->chan; i++)
	channelHue[i] = (float)rand()/RAND_MAX;

  activeChannel = new int[vd->chan];
  for(int i = 0; i < vd->chan; i++)
	activeChannel[i] = 1;


  init();
}

/** vvVirTexRend Destructor

*/
vvVirTexRend::~vvVirTexRend()
{
  vvDebugMsg::msg(2, "vvVirTexRend::~vvVirTexRend()");
  delete[] channelHue;
}

/**


*/
void vvVirTexRend::init()
{
  vvRenderer::init();

  rendererType = VIRTEXREND;
  _renderState._boundaries=true;
  totalMemoryUsage = 0;

  volumeSize = vd->getSize();

  // FIXME: we assume the origin of this volume is at the corner....
  volumeCenter = vd->getSize();
  volumeCenter.scale(0.5);

  vvDebugMsg::msg(2, "vvVirTexRend::init(): volumeSize=", volumeSize[0], volumeSize[1], volumeSize[2]);

  computeInitialLevel();

  //vvDebugMsg::msg(2, "vvVirTexRend::init(): location ", vd->pos[0], vd->pos[1], vd->pos[2]);
  //vvDebugMsg::msg(2, "vvVirTexRend::init(): ", origSize[0], origSize[1], origSize[2]);

  extMinMax = vvGLTools::isGLextensionSupported("GL_EXT_blend_minmax");
  extBlendEquation = vvGLTools::isGLextensionSupported("GL_EXT_blend_equation");

#ifdef WIN32
  if (extBlendEquation) glBlendEquationVV = (PFNGLBLENDEQUATIONEXTPROC)vvDynLib::glSym("glBlendEquationEXT");
  else glBlendEquationVV = (PFNGLBLENDEQUATIONPROC)vvDynLib::glSym("glBlendEquation");

  glBlendColor = (PFNGLBLENDCOLORPROC)wglGetProcAddress("glBlendColor");
#else
  if (extBlendEquation) glBlendEquationVV = (glBlendEquationEXT_type*)vvDynLib::glSym("glBlendEquationEXT");
    else glBlendEquationVV = (glBlendEquationEXT_type*)vvDynLib::glSym("glBlendEquation");
#endif

}



/**
 Arbitrary size image-safe
 conservatively computes initial level 
*/
void vvVirTexRend::computeInitialLevel()
{
  // we stop if one mipmap block is equal to a brick size.
  minimumLevel = (int)(logf((float)brickManager->getBrickSize(volNum, frameNum)) / logf((float)2.0));

  // which is 10 for 1024x1024 images
  // round up to the closest power of 2, to be safe
  int initLevelX = (int)ceilf(logf((float)volumeSize[0])/logf((float)2.0));
  int initLevelY = (int)ceilf(logf((float)volumeSize[1])/logf((float)2.0));

  // initLevelZ == 0 if dim == 2
  int initLevelZ = (int)ceilf(logf((float)volumeSize[2])/logf((float)2.0));
  assert( (brickManager->getDataDimension() == 2 && initLevelZ == 0) || 
	  		(brickManager->getDataDimension() == 3));

  vvDebugMsg::msg(1, "initLevel X, Y, Z=", initLevelX, initLevelY, initLevelZ);


  // pick max(initLevelX, initLevelY, initLevelZ), to be safe
  // "to be safe" means we starts meshing from as high level (low resolution) as possible
  // so as to prevent from memory overflow
  // the drawback will be it takes more time to find the most appropriate meshing 
  // if it starts from a higher level
  initLevel = (initLevelX < initLevelY ? initLevelY : initLevelX);
  initLevel = (initLevel < initLevelZ ? initLevelZ : initLevel);
  //initLevel -= minimumLevel;

#if 0
  // commented out as we always computes the meshing from the highest level
  while(initLevel > minimumLevel)
  {
	// TODO: you may need to reformulate this.... e.g. multichannel
	int blockSize = (int)pow(2.0, (float)(initLevel - 1));
	int blockNumX = (int)ceil((float)origImgSizeX / blockSize);
	int blockNumY = (int)ceil((float)origImgSizeY / blockSize);

	int mipMapMemoryUsage = blockNumX * blockNumY * (int)pow((float)BrickInfo::BRICK_SIZE, 2.0);

	//int mipMapMemoryUsage = (int)(origImgSizeX * origImgSizeY /pow(4.0, (float)(initLevel - 1))) ;
	vvDebugMsg::msg(2, "mipMemoryUsage at level ", initLevel-1, mipMapMemoryUsage);
	vvDebugMsg::msg(2, "# of blocks in this level ", blockNumX, blockNumY, blockNumX * blockNumY);

	if(mipMapMemoryUsage < MEMORY_LIMIT)
	  initLevel--;
	else
	  break;
  }
#endif

  //vvDebugMsg::msg(2, "initial level: ", initLevel);
  //vvDebugMsg::msg(2, "minimum level: ", minimumLevel);
  
  assert(initLevel >= minimumLevel);
}

/**


*/
void vvVirTexRend::renderMultipleVolume()
{
  //vvDebugMsg::msg(2, "vvVirTexRend::renderMultipleVolume()");
  //vvRenderer::renderMultipleVolume();

  float quadColors[3][3] = {
	{1.0f, 0.0f, 0.0f},
	{0.0f, 1.0f, 0.0f},
	{0.0f, 0.0f, 1.0f},
  };

  vvMatrix mv;
  vvMatrix invMV;					// inverse of model-view matrix
  vvMatrix pm;						// OpenGL projection matrix
  float glMV[16];
  vvVector3 eye;
  vvVector3 origin;
  int dataDimension = brickManager->getDataDimension();

  //assert(dataDimension == 2 || dataDimension == 3);

  // model view matrix
  getModelviewMatrix(&mv);
  mv.translate(&translation);
  mv.multiplyPre(&rotation);
  mv.makeGL(glMV);

  // inversed model view matrix
  invMV.copy(&mv);
  invMV.invert();

  // projection matrix 
  getProjectionMatrix(&pm);
  isOrtho = pm.isProjOrtho();

  // eye position [object coordinate]
  getEyePosition(&eye);
  eye.multiply(&invMV);

  // TODO: check this part again...
  // set normal
  if(isOrtho || (viewDir.e[0] == 0.0f && viewDir.e[1] == 0.0f && viewDir.e[2] == 0.0f))
  {
	normal.set(0.0f, 0.0f, 1.0f);
	normal.multiply(&invMV);
	origin.zero();
	origin.multiply(&invMV);
	normal.sub(&origin);
  }
  else if(isInVolume(&eye))
  {
	normal.copy(&viewDir);
	normal.negate();
  }
  else
  {
	normal.copy(&objDir);
	normal.negate();
  }

  normal.normalize();

  vvStopwatch* sw = new vvStopwatch();

  sw->start();

  float start = sw->getTime();
  // generate a mesh
  if(generateMeshing() < 0)
  {
	vvDebugMsg::msg(2, "vvVirTexRend::generateMeshing() failed");
	exit(0);
	//return;
  }
  cerr << "Mesh Generation: " << sw->getTime() - start << endl;
  

  //time_t t0 = clock();

  //brickManager->resetMemoryLoadingTime();
  //brickManager->resetTextureLoadingTime();

  // you may need to sort quadList
  // sort by distance from eye
  quadList.sort(sortingBrickPredicate());

  //////////////////////////////////////////////////////////
  // prefetching should be here
  // load data if needed
  /*
  int brickMinX = INT_MAX;
  int brickMaxX = INT_MIN;
  int brickMinY = INT_MAX;
  int brickMaxY = INT_MIN;
  */
  /////////////////////////////////////////////////////////
	
  brickManager->resetMemoryLoadingTime();

  if(_renderState._showTexture)
  {
	for(list<BrickInfo*>::iterator itr = quadList.begin(); itr != quadList.end(); itr++)
	{
	  BrickInfo* current = (BrickInfo*)(*itr);
	
	  if(brickManager->preRendering(current) < 0)
	  {
		vvDebugMsg::msg(2, "Preparing rendering step failed");
		return;
	  }

	  /*
	  // get min, max of X, Y, and Z among all bricks
	  if(brickMaxX < current->getBrickX()) brickMaxX = current->getBrickX();
	  if(brickMinX > current->getBrickX()) brickMinX = current->getBrickX();
	  if(brickMaxY < current->getBrickY()) brickMaxY = current->getBrickY();
	  if(brickMinY > current->getBrickY()) brickMinY = current->getBrickY();
	  */
	}

	//vvDebugMsg::msg(1, "current bounding box: ", brickMinX, brickMaxX, brickMinY, brickMaxY);
	// get diff of bounding box from the previous
	// then we can identify the current movement X_{n-1}
	// feed
  }


#if 0
  if(brickManager->getMemoryLoadingTime() > 0.01 ||
	  brickManager->getTextureLoadingTime() > 0.01)
  {
	vvDebugMsg::msg(1, "memory loading time: ", brickManager->getMemoryLoadingTime());
	vvDebugMsg::msg(1, "texture loading time: ", brickManager->getTextureLoadingTime());
	brickManager->resetMemoryLoadingTime();
	brickManager->resetTextureLoadingTime();
  }
#endif

  //float elapsed = float(clock() - t0)/float(CLOCKS_PER_SEC);
  //if(elapsed > 0.0001)
	//printf("loading time: %f\n", elapsed);

  if(_renderState._showBricks)
  {

	// load model view matrix
	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
	glLoadMatrixf(glMV);

	if(dataDimension == 3)
	  glDisable(GL_TEXTURE_3D_EXT);
	else
	  glDisable(GL_TEXTURE_2D);

	for(list<BrickInfo*>::iterator itr = quadList.begin(); itr != quadList.end(); itr++)
	{
	  BrickInfo* current = (BrickInfo*)(*itr);

	  vvVector3 quadSize = current->getActualSize();
	  vvVector3 quadCenter = current->getCenter();

	  //vvDebugMsg::msg(2, "quadSize: ", quadSize[0], quadSize[1], quadSize[2]);
	  //vvDebugMsg::msg(2, "current->getLevel() ", current->getLevel(), current->getLevel()%3);
	  drawBoundingBox(&quadSize, &quadCenter, quadColors[(current->getLevel())%3]);
	}

	if(dataDimension == 3)
	  glEnable(GL_TEXTURE_3D_EXT);
	else // dim == 2
	  glEnable(GL_TEXTURE_2D);

	glMatrixMode(GL_MODELVIEW);
	glPopMatrix();
  }

  start = sw->getTime();

  // setting GL environment variables;
  setGLenvironment();


  glMatrixMode(GL_MODELVIEW);
  glPushMatrix();
  glLoadMatrixf(glMV);

  //t0 = clock();

  //cerr << "fragProgram: " << fragProgram << endl;
  shader->useProgram(fragProgram);

  while(!quadList.empty())
  {
	BrickInfo* current = (BrickInfo*)quadList.front();

	if(_renderState._showTexture)
	{
	  for(int i = 0; i < vd->chan; i++)
	  {
		char varName[20];
#ifdef WIN32
		_snprintf(varName, sizeof(varName), "channelColor[%d]", i);
#else
		snprintf(varName, sizeof(varName), "channelColor[%d]", i);
#endif

		float channelColor[3];
		vvToolshed::HSBtoRGB(channelHue[i], (float)0.8, (float)0.7, &channelColor[0], &channelColor[1], &channelColor[2]);
		//cerr << "channelColor value_" << i << ": " << channelColor[0] << ", " << channelColor[1] << ", " << channelColor[2] << endl;

		shader->setValue(fragProgram, varName, 3, 1, channelColor);

#ifdef WIN32
		_snprintf(varName, sizeof(varName), "active[%d]", i);
#else
		snprintf(varName, sizeof(varName), "active[%d]", i);
#endif
		shader->setValue(fragProgram, varName, 1, &activeChannel[i]);
	  }

	  if(brickManager->renderBrick(current, shader, fragProgram) < 0)
	  {
		vvDebugMsg::msg(2, "Drawing a brick failed");
		return;
	  }
	}

	//vvDebugMsg::msg(1, "current level: ", current->getLevel());

	quadList.pop_front();
  }

  //elapsed = float(clock() - t0)/float(CLOCKS_PER_SEC);
  //if(elapsed > 0.001)
	//printf("rendering time: %f\n", elapsed);

  shader->disable();
  glMatrixMode(GL_MODELVIEW);
  glPopMatrix();

  unsetGLenvironment();
  glFinish(); 

  cerr << "Rendering Time: " << sw->getTime() - start << endl;
  delete sw;
}




//////////////////////////////////////////////////////////////////////////
// protected methods
//////////////////////////////////////////////////////////////////////////



//////////////////////////////////////////////////////////////////////////


float vvVirTexRend::getChannelHue(int chan)
{
  assert(chan < vd->chan && chan >= 0);
  return channelHue[chan];
}

void vvVirTexRend::setChannelHue(int chan, float value)
{
  assert(chan < vd->chan && chan >= 0);
  channelHue[chan] = value;
  //cerr << "Channel hue for channel #" << chan << " is set to " << value << endl;
}

int vvVirTexRend::isActiveChannel(int chan)
{
  assert(chan < vd->chan && chan >= 0);
  return activeChannel[chan];
}

void vvVirTexRend::setActiveChannel(int chan, int value)
{
  assert(chan < vd->chan && chan >= 0);
  activeChannel[chan] = value;
  if(value)
	cerr << "Channel #" << chan << " is activated" << endl;
  else
	cerr << "Channel #" << chan << " is deactivated" << endl;
}

/**

*/
int vvVirTexRend::generateMeshing()
{
  vvMatrix mvMatrix;
  vvMatrix invMV;
  vvMatrix projMatrix;
  vvMatrix vpMatrix;
  vvMatrix transformMatrix;
  vvVector3 eye;

  //GLfloat mv[16];
  //GLfloat proj[16];
  GLint vp[4];

  // 1. modelview matrix
  getModelviewMatrix(&mvMatrix);
  mvMatrix.translate(&translation);
  mvMatrix.multiplyPre(&rotation);

  //mvMatrix.print("mvMatrix in generateMeshing: ");

  // 2. inverse modelview matrix
  invMV.copy(&mvMatrix);
  invMV.invert();

  // 3. projection matrix
  getProjectionMatrix(&projMatrix);
  /*
  glGetFloatv(GL_PROJECTION_MATRIX, proj);
  projMatrix.getGL((float*)proj);
  projMatrix.set(proj);
  projMatrix.transpose();
  */

  // 4. viewport matrix
  glGetIntegerv(GL_VIEWPORT, vp);
  float vpElmt[16] = {
	(float)vp[2]/2, 0, 	  0,   (float)vp[0]+(float)vp[2]/2,
	0, 		 (float)vp[3]/2, 0,   (float)vp[1]+(float)vp[3]/2,
	0, 		 0, 	  (float)0.5, (float)0.5,
	0, 		 0, 	  0,   (float)1.0
  };
  vpMatrix.set(vpElmt);

  // 5. Find eye position
  getEyePosition(&eye);
  eye.multiply(&invMV);

  //vpMatrix * projMatrix * mvMatrix
  transformMatrix.identity();
  //transformMatrix.copy(&vpMatrix);
  transformMatrix.multiplyPost(&mvMatrix);
  transformMatrix.multiplyPost(&projMatrix);
  transformMatrix.multiplyPost(&vpMatrix);

  /*
  transformMatrix.multiplyPre(&vpMatrix);
  transformMatrix.multiplyPre(&projMatrix);
  transformMatrix.multiplyPre(&mvMatrix);
  */

  //mvMatrix.print("ModelView matrix: ");
  //projMatrix.print("Projection Matrix: ");
  //vpMatrix.print("Viewport Matrix: ");
  //transformMatrix.print("Transform matrix: ");

  int numBrickRendered = 0;

  // mesh is initialized here and all the quads initialized here are stored in quadPQueue
  if((numBrickRendered = meshInit(eye, &transformMatrix)) < 0)
  {
	vvDebugMsg::msg(2, "vvVirTexRend::meshInit failed");
	return -1;
  }

  //cerr << "numBrickRendered after meshInit: " << numBrickRendered << endl;

  //vvDebugMsg::msg(2, "vvVirTexRend::generateMeshing(): quadPQueue.size()=", (int)quadPQueue.size());

  //totalMemoryUsage = brickManager->getTotalMemoryUsage();

  // build a finer mesh until it reaches the maximum memory size
  //while(totalMemoryUsage < BrickManager::TEXTURE_LIMIT && numBrickRendered < _brickLimit)
  while(numBrickRendered < _brickLimit)
  {
	//vvDebugMsg::msg(2, "quadPQueue size: ", (int)quadPQueue.size());

	// if all quads are in quadList
	if(quadPQueue.empty())
	{
	  //vvDebugMsg::msg(2, "vvVirTexRend::generateMeshing(): quadPQueue.empty()");
	  break;
	}

	BrickInfo* current = (BrickInfo*)quadPQueue.top();
	if(current == NULL)
	{
	  vvDebugMsg::msg(2, "Element is NULL in quadPQueue");
	  return -1;
	}
	//vvDebugMsg::msg(2, "the cost at top: ", current.getCost());

	// TODO: check if this code really needs
	if(!current->isRendered())
	{
	  cerr << "here" << endl;
	  totalMemoryUsage -= brickManager->getMemoryUsagePerBrick();
	  numBrickRendered--;
	  quadPQueue.pop();
	  continue;
	}

	if((powf(2.0, brickManager->getDataDimension()) - 1) * brickManager->getMemoryUsagePerBrick() + totalMemoryUsage < BrickManager::TEXTURE_LIMIT)
	{
	  // pop lower resolution brick
	  quadPQueue.pop();

	  if(current->isRendered() && (current->getLevel() == 0 || !current->getFiner()))
	  {
		// stop because we can't have more finer quad even if we still have enough memory
		// move the quad into the final list, quadList
		//vvDebugMsg::msg(3, "pushing into quadList");
		//if(!current.getFiner())
		  //vvDebugMsg::msg(3, "mesh is too small to have a finer quad");
		quadList.push_back(current);
		//vvDebugMsg::msg(1, "pushed: ", (int)quadList.size());
		//cerr << "level: " << current->getLevel() << " finer: " << current->getFiner() << " rendered: " << current->isRendered() << endl;

	  } else {

		// we don't render current
		numBrickRendered--;
		//cerr << "numBrickRendered decreased" << endl;

		// let's go down one level
		//totalMemoryUsage += brickManager->getMemoryUsagePerBrick() * ((int)powf((float)2.0, (float)brickManager->getDataDimension()) - 1);
		//vvDebugMsg::msg(2, "totalMemoryUsage so far: ", totalMemoryUsage);

		vvVector3 bottomLeftFrontCoord = current->getCoord(0);
		vvVector3 centerCoord = current->getCenter();
		vvVector3 newBottomLeftFrontCoord; 
		
		// 0. SWF - this quad always overlaps with the image we are rendering
		newBottomLeftFrontCoord = bottomLeftFrontCoord;

		BrickInfo* newQuad1 = brickManager->getBrickInfo(volNum, frameNum, current->getLevel()-1, newBottomLeftFrontCoord);
		if(newQuad1 == NULL)
		{
		  vvDebugMsg::msg(1, "vvVirTexRend::generateMeshing(): wrong index for BrickManager::getBrickInfo(), newQuad1");
		  vvDebugMsg::msg(1, "error on (l, x, y, z): ", (float)current->getLevel()-1, newBottomLeftFrontCoord[0], newBottomLeftFrontCoord[1], newBottomLeftFrontCoord[2]);
		  return -1;
		}
		newQuad1->initialize(vd, &transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);

		if(newQuad1->isRendered())
		{
		  totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
		  quadPQueue.push(newQuad1);
		  numBrickRendered++;
		}
		/*
		else
		{
		  totalMemoryUsage -= brickManager->getMemoryUsagePerBrick();
		}
		*/

		// 1. SEF
		if(centerCoord[0] < volumeSize[0])
		{
		  newBottomLeftFrontCoord.set(centerCoord[0], bottomLeftFrontCoord[1], bottomLeftFrontCoord[2]);

		  BrickInfo* newQuad2 = brickManager->getBrickInfo(volNum, frameNum, current->getLevel()-1, newBottomLeftFrontCoord);

		  if(newQuad2 == NULL)
		  {
			vvDebugMsg::msg(1, "vvVirTexRend::generateMeshing(): wrong index for BrickManager::getBrickInfo(), newQuad2");
		  vvDebugMsg::msg(1, "error on (l, x, y, z): ", (float)current->getLevel()-1, newBottomLeftFrontCoord[0], newBottomLeftFrontCoord[1], newBottomLeftFrontCoord[2]);
			return -1;
		  }
		  newQuad2->initialize(vd, &transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);

		  if(newQuad2->isRendered())
		  {
			totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
			quadPQueue.push(newQuad2);
			numBrickRendered++;
		  }
		  /*
		  else
		  {
			totalMemoryUsage -= brickManager->getMemoryUsagePerBrick();
			//newQuad2.deleteNodes();
		  }
		  */
		}

		// 2. NWF
		if(centerCoord[1] < volumeSize[1])
		{
		  newBottomLeftFrontCoord.set(bottomLeftFrontCoord[0], centerCoord[1], bottomLeftFrontCoord[2]);
		  
		  BrickInfo* newQuad3 = brickManager->getBrickInfo(volNum, frameNum, current->getLevel()-1, newBottomLeftFrontCoord);
		  if(newQuad3 == NULL)
		  {
			vvDebugMsg::msg(1, "vvVirTexRend::generateMeshing(): wrong index for BrickManager::getBrickInfo(), newQuad3");
		  vvDebugMsg::msg(1, "error on (l, x, y, z): ", (float)current->getLevel()-1, newBottomLeftFrontCoord[0], newBottomLeftFrontCoord[1], newBottomLeftFrontCoord[2]);
			return -1;
		  }

		  newQuad3->initialize(vd, &transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);


		  if(newQuad3->isRendered())
		  {
			totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
			quadPQueue.push(newQuad3);
			numBrickRendered++;
		  }
		  /*
		  else
		  {
			totalMemoryUsage -= brickManager->getMemoryUsagePerBrick();
			//newQuad3.deleteNodes();
		  }
		  */
		}

		// 3. NEF
		if(centerCoord[0] < volumeSize[0] && centerCoord[1] < volumeSize[1])
		{
		  newBottomLeftFrontCoord.set(centerCoord[0], centerCoord[1], bottomLeftFrontCoord[2]);

		  BrickInfo* newQuad4 = brickManager->getBrickInfo(volNum, frameNum, current->getLevel()-1, newBottomLeftFrontCoord);
		  if(newQuad4 == NULL)
		  {
			vvDebugMsg::msg(1, "vvVirTexRend::generateMeshing(): wrong index for BrickManager::getBrickInfo(), newQuad4");
		  vvDebugMsg::msg(1, "error on (l, x, y, z): ", (float)current->getLevel()-1, newBottomLeftFrontCoord[0], newBottomLeftFrontCoord[1], newBottomLeftFrontCoord[2]);
			return -1;
		  }
		  newQuad4->initialize(vd, &transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);

		  if(newQuad4->isRendered())
		  {
			totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
			quadPQueue.push(newQuad4);
			numBrickRendered++;
		  } 
		  /*
		  else {
			totalMemoryUsage -= brickManager->getMemoryUsagePerBrick();
			//newQuad4.deleteNodes();
		  }
		  */
		}

		// the following four new bricks are only created for 3D data

		// 4. SWB - this quad always overlaps with the image we are rendering
		if(brickManager->getDataDimension() == 3 &&
			centerCoord[2] < volumeSize[2])
		{
		  newBottomLeftFrontCoord.set(bottomLeftFrontCoord[0], bottomLeftFrontCoord[1], centerCoord[2]);

		  BrickInfo* newQuad5 = brickManager->getBrickInfo(volNum, frameNum, current->getLevel()-1, newBottomLeftFrontCoord);
		  if(newQuad5 == NULL)
		  {
			vvDebugMsg::msg(1, "vvVirTexRend::generateMeshing(): wrong index for BrickManager::getBrickInfo(), newQuad5");
			vvDebugMsg::msg(1, "error on (l, x, y, z): ", (float)current->getLevel()-1, newBottomLeftFrontCoord[0], newBottomLeftFrontCoord[1], newBottomLeftFrontCoord[2]);
			return -1;
		  }
		  newQuad5->initialize(vd, &transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);

		  if(newQuad5->isRendered())
		  {
			totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
			quadPQueue.push(newQuad5);
			numBrickRendered++;
		  }
		  /*
		  else
		  {
			totalMemoryUsage -= brickManager->getMemoryUsagePerBrick();
			//newQuad1.deleteNodes();
		  }
		  */

		  // 5. SEB
		  if(centerCoord[0] < volumeSize[0])
		  {
			newBottomLeftFrontCoord.set(centerCoord[0], bottomLeftFrontCoord[1], centerCoord[2]);

			BrickInfo* newQuad6 = brickManager->getBrickInfo(volNum, frameNum, current->getLevel()-1, newBottomLeftFrontCoord);

			if(newQuad6 == NULL)
			{
			  vvDebugMsg::msg(1, "vvVirTexRend::generateMeshing(): wrong index for BrickManager::getBrickInfo(), newQuad6");
			  vvDebugMsg::msg(1, "error on (l, x, y, z): ", (float)current->getLevel()-1, newBottomLeftFrontCoord[0], newBottomLeftFrontCoord[1], newBottomLeftFrontCoord[2]);
			  return -1;
			}
			newQuad6->initialize(vd, &transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);

			if(newQuad6->isRendered())
			{
			  totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
			  quadPQueue.push(newQuad6);
			  numBrickRendered++;
			}
			/*
			else
			{
			  totalMemoryUsage -= brickManager->getMemoryUsagePerBrick();
			  //newQuad2.deleteNodes();
			}
			*/
		  }

		  // 6. NWB
		  if(centerCoord[1] < volumeSize[1])
		  {
			newBottomLeftFrontCoord.set(bottomLeftFrontCoord[0], centerCoord[1], centerCoord[2]);
			
			BrickInfo* newQuad7 = brickManager->getBrickInfo(volNum, frameNum, current->getLevel()-1, newBottomLeftFrontCoord);
			if(newQuad7 == NULL)
			{
			  vvDebugMsg::msg(1, "vvVirTexRend::generateMeshing(): wrong index for BrickManager::getBrickInfo(), newQuad7");
			  vvDebugMsg::msg(1, "error on (l, x, y, z): ", (float)current->getLevel()-1, newBottomLeftFrontCoord[0], newBottomLeftFrontCoord[1], newBottomLeftFrontCoord[2]);
			  return -1;
			}

			newQuad7->initialize(vd, &transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);

			if(newQuad7->isRendered())
			{
			  totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
			  quadPQueue.push(newQuad7);
			  numBrickRendered++;
			}
			/*
			else
			{
			  totalMemoryUsage -= brickManager->getMemoryUsagePerBrick();
			  //newQuad3.deleteNodes();
			}
			*/
		  }

		  // 7. NEB
		  if(centerCoord[0] < volumeSize[0] && centerCoord[1] < volumeSize[1])
		  {
			newBottomLeftFrontCoord = centerCoord;

			BrickInfo* newQuad8 = brickManager->getBrickInfo(volNum, frameNum, current->getLevel()-1, newBottomLeftFrontCoord);
			if(newQuad8 == NULL)
			{
			  vvDebugMsg::msg(1, "vvVirTexRend::generateMeshing(): wrong index for BrickManager::getBrickInfo(), newQuad8");
			  vvDebugMsg::msg(1, "error on (l, x, y, z): ", (float)current->getLevel()-1, newBottomLeftFrontCoord[0], newBottomLeftFrontCoord[1], newBottomLeftFrontCoord[2]);
			  return -1;
			}
			newQuad8->initialize(vd, &transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);

			if(newQuad8->isRendered())
			{
			  totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
			  quadPQueue.push(newQuad8);
			  numBrickRendered++;
			} 
			/*
			else 
			{
			  totalMemoryUsage -= brickManager->getMemoryUsagePerBrick();
			  //newQuad4.deleteNodes();
			}
			*/
		  }
		} // end of if(brickManager->getDataDimension() == 3 && centerCoord[2] < volumeSize[2])
	  }

	} else {
	  //vvDebugMsg::msg(2, "current quad memory usage(): ", current->getMemoryUsage());
	  //vvDebugMsg::msg(2, "totalMemoryUsage so far: ", totalMemoryUsage);
	  //vvDebugMsg::msg(2, "Reached memory Limit");
	  break;
	}
  }

  // break statements jump to here

  // move all the leftovers in PQ to list
  //cerr << "quadPQueue size: " << quadPQueue.size() << endl;
  while(!quadPQueue.empty())
  {
	BrickInfo* aQuad = (BrickInfo*)quadPQueue.top();
	if(aQuad->isRendered())
	  quadList.push_back(aQuad);
	quadPQueue.pop();
  }

  //vvDebugMsg::msg(3, "vvVirTexRend::generateMeshing(): a mesh is generated");
  //vvDebugMsg::msg(3, "vvVirTexRend::generateMeshing(): the size of final PQueue: ", (int)quadPQueue.size());
  //vvDebugMsg::msg(1, "vvVirTexRend::generateMeshing(): the size of final List: ", (int)quadList.size());
  cerr << "numBrickRendered: " << numBrickRendered << endl;

  return 0;
}


/**
  initialize meshing with the initial mip-map level
  Arbitrary size-safe!
*/
int vvVirTexRend::meshInit(vvVector3 eye, vvMatrix* transformMatrix)
{

  int brickRendered = 0;
  int blockSize = (int)powf((float)2.0, (float)initLevel);

  vvDebugMsg::msg(3, "vvVirTexRend::meshInit(): intial blockSize: ", blockSize);
  vvDebugMsg::msg(3, "vvVirTexRend::meshInit(): initial level: ", initLevel);

  // clean up quadPQueue. 
  // we may be able to optimize this by updating previous information adaptively...
  while(!quadPQueue.empty())
  {
	//BrickInfo* current = (BrickInfo*)quadPQueue.top();
	//current->deleteNodes();
	quadPQueue.pop();
  }

  // clean up quadList
  while(!quadList.empty())
  {
	//BrickInfo current = (BrickInfo)quadList.front();
	//current.deleteNodes();
	quadList.pop_front();
  }

  totalMemoryUsage = 0;

  for(int i = 0; i < volumeSize[0]; i+= blockSize)
  {
	for(int j = 0; j < volumeSize[1]; j+= blockSize)
	{
	  // 3D volume
	  if(brickManager->getDataDimension() == 3)
	  {
		for(int k = 0; k < volumeSize[2]; k+= blockSize)
		{
		  //cout << "Node(" << i << ", " << j << "), (" << (i+blockSize) << ", " << (j+blockSize) << ")" << endl;

		  vvVector3 bottomLeft((float)i, (float)j, (float)k);

		  BrickInfo* aCube = brickManager->getBrickInfo(volNum, frameNum, initLevel-minimumLevel, bottomLeft);
		  if(aCube == NULL)
		  {
			vvDebugMsg::msg(1, "wrong index for getBrickInfo()");
			return -1;
		  }

		  aCube->initialize(vd, transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);

		  // only if the quad is inside our viewport
		  if(aCube->isRendered())
		  {
			totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
			quadPQueue.push(aCube);
			brickRendered ++;
		  }

		  //vvDebugMsg::msg(2, "brickInfo in meshInit: ", aCube->getLevel(), aCube->getBrickX(), aCube->getBrickY(), aCube->getBrickZ());
		}

	  } else { // 2D image

		vvVector3 bottomLeft((float)i, (float)j, 0);

		//cerr << "vvVirTexRend::meshInit: volNum=" << volNum << ", frameNum=" << frameNum << endl;

		BrickInfo* aQuad = brickManager->getBrickInfo(volNum, frameNum, initLevel-minimumLevel, bottomLeft);
		if(aQuad == NULL)
		{
		  vvDebugMsg::msg(2, "wrong index for getBrickInfo()");
		  return -1;
		}

		aQuad->initialize(vd, transformMatrix, eye, normal, volumeCenter, isOrtho, _pixelToVoxelRatio);

		// only if the quad is inside our viewport
		if(aQuad->isRendered())
		{
		  //vvDebugMsg::msg(2, "initial bricks is inserted in the mesh, ", i, j);
		  totalMemoryUsage += brickManager->getMemoryUsagePerBrick();
		  quadPQueue.push(aQuad);
		  brickRendered++;
		}
	  }
	}
  }

  //vvDebugMsg::msg(2, "totalMemoryUsage after meshInit() ", totalMemoryUsage);
  //vvDebugMsg::msg(2, "quadPQueue size after meshInit() ", (int)quadPQueue.size());

  return brickRendered;
}


//////////////////////////////////////////////////////////////////////////
// Chih's code, un/setting environmental variables
//////////////////////////////////////////////////////////////////////////

//----------------------------------------------------------------------------
/// Set GL environment for texture rendering.
void vvVirTexRend::setGLenvironment()
{
  vvDebugMsg::msg(3, "vvTexRend::setGLenvironment()");

  // Save current GL state:
  glGetBooleanv(GL_CULL_FACE, &glsCulling);
  glGetBooleanv(GL_BLEND, &glsBlend);
  glGetBooleanv(GL_COLOR_MATERIAL, &glsColorMaterial);
  glGetIntegerv(GL_BLEND_SRC, &glsBlendSrc);
  glGetIntegerv(GL_BLEND_DST, &glsBlendDst);
  glGetBooleanv(GL_LIGHTING, &glsLighting);
  glGetBooleanv(GL_DEPTH_TEST, &glsDepthTest);
  glGetIntegerv(GL_MATRIX_MODE, &glsMatrixMode);
  glGetIntegerv(GL_DEPTH_FUNC, &glsDepthFunc);

  if (extMinMax) glGetIntegerv(GL_BLEND_EQUATION_EXT, &glsBlendEquation);
  glGetBooleanv(GL_DEPTH_WRITEMASK, &glsDepthMask);

  // Set new GL state:
  glDisable(GL_CULL_FACE);
  glDisable(GL_LIGHTING);
  glEnable(GL_DEPTH_TEST);
  glDepthFunc(GL_LESS);                           // default depth function
  glEnable(GL_COLOR_MATERIAL);
  glEnable(GL_BLEND);

  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  //glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_COLOR);
  //glBlendFunc(GL_ONE_MINUS_DST_ALPHA, GL_DST_ALPHA);

  //glBlendFunc(GL_CONSTANT_ALPHA, GL_ONE);
  //glBlendFunc(GL_ONE, GL_CONSTANT_ALPHA);
  //glBlendColor(1.f, 1.f, 1.f, 0.5f);

  glMatrixMode(GL_TEXTURE);
  glLoadIdentity();
  glMatrixMode(GL_MODELVIEW);
  glDepthMask(GL_FALSE);

  _renderState._mipMode = 1;

  if (glBlendEquationVV)
  {
    switch (_renderState._mipMode)
    {
                                                  // alpha compositing
      case 0: glBlendEquationVV(GL_FUNC_ADD); break;
      case 1: glBlendEquationVV(GL_MAX); break;   // maximum intensity projection
      case 2: glBlendEquationVV(GL_MIN); break;   // minimum intensity projection

      default:
		  glBlendEquationVV(_renderState._mipMode);
		  break;
    }
  }

  vvDebugMsg::msg(3, "vvTexRend::setGLenvironment() done");
}


//----------------------------------------------------------------------------
/// Unset GL environment for texture rendering.
void vvVirTexRend::unsetGLenvironment()
{
  vvDebugMsg::msg(3, "vvTexRend::unsetGLenvironment()");

  if (glsCulling==(GLboolean)true) glEnable(GL_CULL_FACE);
  else glDisable(GL_CULL_FACE);

  if (glsBlend==(GLboolean)true) glEnable(GL_BLEND);
  else glDisable(GL_BLEND);

  if (glsColorMaterial==(GLboolean)true) glEnable(GL_COLOR_MATERIAL);
  else glDisable(GL_COLOR_MATERIAL);

  if (glsDepthTest==(GLboolean)true) glEnable(GL_DEPTH_TEST);
  else glDisable(GL_DEPTH_TEST);

  if (glsLighting==(GLboolean)true) glEnable(GL_LIGHTING);
  else glDisable(GL_LIGHTING);

  glDepthMask(glsDepthMask);
  glDepthFunc(glsDepthFunc);
  glBlendFunc(glsBlendSrc, glsBlendDst);
  if (glBlendEquationVV) glBlendEquationVV(glsBlendEquation);
  glMatrixMode(glsMatrixMode);
  vvDebugMsg::msg(3, "vvTexRend::unsetGLenvironment() done");
}



//----------------------------------------------------------------------------
/** Set user's viewing direction.
  This information is needed to correctly orientate the texture slices
  in 3D texturing mode if the user is inside the volume.
  @param vd  viewing direction in object coordinates
*/
void vvVirTexRend::setViewingDirection(const vvVector3* vd)
{
  vvDebugMsg::msg(3, "vvTexRend::setViewingDirection()");
  viewDir.copy(vd);
}

//----------------------------------------------------------------------------
/** Set the direction from the viewer to the object.
  This information is needed to correctly orientate the texture slices
  in 3D texturing mode if the viewer is outside of the volume.
  @param vd  object direction in object coordinates
*/
void vvVirTexRend::setObjectDirection(const vvVector3* vd)
{
  vvDebugMsg::msg(3, "vvTexRend::setObjectDirection()");
  objDir.copy(vd);
}




