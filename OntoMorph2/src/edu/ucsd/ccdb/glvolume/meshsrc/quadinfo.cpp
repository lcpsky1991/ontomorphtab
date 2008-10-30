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


#include "quadinfo.h"
#include "vvdebugmsg.h"
#include "vvvirtexrendmngr.h"
#include <math.h>


using namespace std;
using namespace MipMapVideoLib;

/////////////////////////////////////////////////////////////////////////
// QuadInfo Class 
/////////////////////////////////////////////////////////////////////////

QuadInfo::QuadInfo(vvVector3 _bottomLeft,
					vvVector3 _topRight,
					int _level,
					int _volNum,
					int _frameIndex,
					int _numChannel,
					int _brickSize)
					: BrickInfo(_level, _volNum, _frameIndex, _numChannel, _brickSize)
{
  ///////////////////////////////////////////////////
  // time-independent values
  ///////////////////////////////////////////////////

  // set coord[]
  coord[0] = _bottomLeft;
  coord[2] = _topRight;
  coord[1].set(coord[0][0], coord[2][1], 0.0); // top left
  coord[3].set(coord[2][0], coord[0][1], 0.0); // bottom right

  assert(coord[0][2] == 0.0 && coord[2][2] == 0.0);

  // set center
  setCenter();
  actualSize = coord[2] - coord[0];

  //vvDebugMsg::msg(2, "***:", (int)coord[0][0], imageOffsetX, brickSize, (int)(pow(2.0, level)));
  brickX = (int)(coord[0][0])/brickSize/(int)(powf((float)2.0, (float)level));
  brickY = (int)(coord[0][1])/brickSize/(int)(powf((float)2.0, (float)level));
  brickZ = 0;

  //vvDebugMsg::msg(2, "QuadInfo: brickX, brickY: ", brickX, brickY);
}

/*
   This should be called at every frame
   this function updates the values that changes over time

   @param vd provides translation and rotation information
   @param eye used to compute the distance from eye to this brick
*/
void QuadInfo::initialize(vvVolDesc* _vd, vvMatrix* transformMatrix, vvVector3 eye, vvVector3 _normal, vvVector3 _volumeCenter, bool isOrtho, int pixelToVoxelRatio)
{
  vd = _vd;
  normal = _normal; 
  volumeCenter = _volumeCenter;

  setViewPortCorners(transformMatrix);
  setBoundingBox();
  setDistance(eye, isOrtho);
  setCost(pixelToVoxelRatio);
  //setFiner();
}


int QuadInfo::initializeChannelTexture(int c)
{
  assert(c < numChannel && c >= 0);

  glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

  //if(texName[c] == 0 || pboName[c] == 0)
  if(texName[c] == 0)
  {
	vvDebugMsg::msg(1, "texture id has not been set");
	return -1;
  }

  glEnable(GL_TEXTURE_2D);
  //glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, pboName[c]);
  glBindTexture(GL_TEXTURE_2D, texName[c]);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
  glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, brickSize, brickSize,
	  			GL_LUMINANCE, GL_UNSIGNED_BYTE, memoryBuf[c]);

  //vvDebugMsg::msg(2, "[cache] texture is loaded: ", texName, level);

  glBindTexture(GL_TEXTURE_2D, 0);
  //glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);
  glDisable(GL_TEXTURE_2D);

  return 0;
}



/*

*/
int QuadInfo::draw(vvGLSL* shader, GLuint fragProgram)
{
  //vvDebugMsg::msg(2, "QuadInfo::draw() ");

  if(!inTexture)
  {
	vvDebugMsg::msg(2, "Use BrickManager::loadBrickToTexture() to load data into texture memory first");
	return -1;
  }

  // check parameters
  //vvDebugMsg::msg(2, "texName: ", (int)texName);


  //vvDebugMsg::msg(2, "texName index: ", current.getLevel()-minimumLevel);
  //vvDebugMsg::msg(2, "texName: ", (int)texNames[current.getLevel()-minimumLevel]);

  //vvDebugMsg::msg(2, "drawing: ", level, brickX, brickY);

  if(texName[0] == 0)
  {
	vvDebugMsg::msg(2, "QuadInfo::texName hasn't been initialized");
	return -1;
  }

  shader->resetTextureCount();

  char varName[20];

  glEnable(GL_TEXTURE_2D);

  for(int c = 0; c < numChannel; c++)
  {
#ifdef WIN32
	_snprintf(varName, sizeof(varName), "gl2dTex%d", c);
#else
	snprintf(varName, sizeof(varName), "gl2dTex%d", c);
#endif
	shader->initializeMultiTexture2D(fragProgram, varName, texName[c]);
  }

  glBegin(GL_QUADS);
	glTexCoord2f(0.0, 0.0); 
	glVertex3f(coord[0][0], coord[0][1], 0.0);

	glTexCoord2f(0.0, 1.0); 
	glVertex3f(coord[1][0], coord[1][1], 0.0);

	glTexCoord2f(1.0, 1.0); 
	glVertex3f(coord[2][0], coord[2][1], 0.0);

	glTexCoord2f(1.0, 0.0); 
	glVertex3f(coord[3][0], coord[3][1], 0.0);
  glEnd();

  shader->disableMultiTexture2D();

  glBindTexture(GL_TEXTURE_2D, 0);
  glDisable(GL_TEXTURE_2D);

  rendered = false;

  return 0;
}

/**

*/
void QuadInfo::setCenter()
{
  center = coord[0] + coord[2];
  center.scale(0.5);
}

void QuadInfo::setViewPortCorners(vvMatrix* transformMatrix)
{
  // clean up four corners
  viewPortCorners.clear();

  //transformMatrix->print("transformMatrix at setVierPortCorners");
  for(int i = 0; i < 4; i++)
  {
	viewPortCorners.push_back(coord[i]);
	viewPortCorners[i].multiply(transformMatrix);
	//vvDebugMsg::msg(1, "coord: ", coord[i][0], coord[i][1], coord[i][2]);
	//vvDebugMsg::msg(1, "corner: ", (float)i, viewPortCorners[i].e[0], viewPortCorners[i].e[1]);

  }
}

void QuadInfo::setBoundingBox()
{
  assert(viewPortCorners.size() == 4);

  float minX, maxX, minY, maxY;

  // find the min,max of this quad coordinate
  minX=maxX=viewPortCorners[0].e[0];
  minY=maxY=viewPortCorners[0].e[1];

  for(int i = 1; i < 4; i++)
  {
	if(viewPortCorners[i].e[0] < minX)
	  minX = viewPortCorners[i].e[0];
	if(viewPortCorners[i].e[0] > maxX)
	  maxX = viewPortCorners[i].e[0];
	if(viewPortCorners[i].e[1] < minY)
	  minY = viewPortCorners[i].e[1];
	if(viewPortCorners[i].e[1] > maxY)
	  maxY = viewPortCorners[i].e[1];
  }

  boundingBox.set(minX, minY, maxX, maxY);

}


void QuadInfo::setCost(int pixelToVoxelRatio)
{
  // Cost Function 2.
  // this version computes the actual area that will be shown on screen
  // if the area is big on screen, it gets the higher cost

  assert(viewPortCorners.size() == 4);

  GLint vp[4];
  glGetIntegerv(GL_VIEWPORT, vp);

  //vvDebugMsg::msg(1, "vp: ", vp[0], vp[1], vp[2], vp[3]);

  //if(g_rendererManager->viewFrustumCullingTest(this))
  if((boundingBox[2] < vp[0]) ||
	  (boundingBox[3] < vp[1]) ||
	  (boundingBox[0] > vp[0]+vp[2]) ||
	  (boundingBox[1] > vp[1]+vp[3]) )
  {
	// if this quad is outside the screen
	// we ignore this quad
	rendered = false;
	cost = 0.0;

	//cerr << "outside!" << endl;
	//vvDebugMsg::msg(1, "outside!", boundingBox[0], boundingBox[2], boundingBox[1], boundingBox[3]);
	//vvDebugMsg::msg(1, "in bbox", vp[0], vp[0]+vp[2], vp[1], vp[1]+vp[3]);

  } else {
	// if this quad is inside the screen,
	// we set cost

	rendered = true;

	// Cost Function 2.2
	// computes the area exactly but without clipping
	vvVector3 x1 = viewPortCorners[1]-viewPortCorners[0];
	vvVector3 x2 = viewPortCorners[1]-viewPortCorners[0];
	vvVector3 y = viewPortCorners[2]-viewPortCorners[0];
	vvVector3 z = viewPortCorners[3]-viewPortCorners[0];

	x1.cross(&y);
	x2.cross(&z);

	float area = 0.5 * (x1.length() + x2.length());

	cost = area / distance;
	finer = (area > pixelToVoxelRatio * brickSize * brickSize);

	//vvDebugMsg::msg(2, "inside! ", minX, maxX, minY, maxY);
	//vvDebugMsg::msg(2, "in bbox ", vp[0], vp[0]+vp[2], vp[1], vp[1]+vp[3]);
  }
  //vvDebugMsg::msg(2, "cost is set to : ", cost);

}


/*
	indicates if this quad can be splitted to four finer quads, 
	if this quad is mapped to a smaller resolution than the size of ratio*brickSize*brickSize, 
	then there is no need to be finer

	@param ratio voxel to pixel ratio. 
void QuadInfo::setFiner(int ratio)
{

  assert(viewPortCorners.size() == 4);

  vvVector3 x1 = viewPortCorners[1]-viewPortCorners[0];
  vvVector3 x2 = viewPortCorners[1]-viewPortCorners[0];
  vvVector3 y = viewPortCorners[2]-viewPortCorners[0];
  vvVector3 z = viewPortCorners[3]-viewPortCorners[0];

  x1.cross(&y);
  x2.cross(&z);

  float area = 0.5 * (x1.length() + x2.length());
  //vvDebugMsg::msg(1, "area of a brick: ", (float)coord[0][0], (float)coord[0][1], area);
  //finer = ((area > 4 * brickSize * brickSize) || (boundingBox[2]-boundingBox[0] > 2*brickSize) || (boundingBox[3]-boundingBox[1] > 2*brickSize));
  finer = (area > ratio * brickSize * brickSize);

#if 0
  if(!finer)
	vvDebugMsg::msg(3, "this block is too small to be finer ", area);
  else
	vvDebugMsg::msg(2, "Yes, more to go ", area);
#endif
}
*/


vvVector3 QuadInfo::getCoord(int i)
{
  if(i < 0 || i > 3)
  {
	vvDebugMsg::msg(2, "Wrong index for coordinate, must be 0, 1, 2, or 3");
	return vvVector3(0.0, 0.0, 0.0);
  }

  return coord[i];
}


/////////////////////////////////////////////////////////////////////////
// End of QuadInfo Class 
/////////////////////////////////////////////////////////////////////////



