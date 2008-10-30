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


#include "cubeinfo.h"

#include <vvdebugmsg.h>
#include <math.h>

using namespace MipMapVideoLib;


/**
      5____ 6       
     /___ /|        Y
   1| | 2| |        |  / Z
    | 4 -|-/7       | /
    |/___|/         |/____ X
    0    3
*/
CubeInfo::CubeInfo(vvVector3 _bottomLeftFront,
					vvVector3 _topRightBack,
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

  // coordinates
  coord[0] = _bottomLeftFront;
  coord[6] = _topRightBack;

  coord[1].set(coord[0][0], coord[6][1], coord[0][2]);
  coord[2].set(coord[6][0], coord[6][1], coord[0][2]);
  coord[3].set(coord[6][0], coord[0][1], coord[0][2]);

  coord[4].set(coord[0][0], coord[0][1], coord[6][2]);
  coord[5].set(coord[0][0], coord[6][1], coord[6][2]);
  coord[7].set(coord[6][0], coord[0][1], coord[6][2]);

  setCenter();
  actualSize = coord[6] - coord[0];

  // set brickX, brickY, and brickZ
  brickX = (int)(coord[0][0])/brickSize/(int)(powf((float)2.0, (float)level));
  brickY = (int)(coord[0][1])/brickSize/(int)(powf((float)2.0, (float)level));
  brickZ = (int)(coord[0][2])/brickSize/(int)(powf((float)2.0, (float)level));

  // Get the proc addresses for the ARB extensions under windows - Raj
#ifdef _USE_GLARB_UNDER_WIN32


  /* we don't use Pixel Buffer Object any more...
  if((glGenBuffersARB = (PFNGLGENBUFFERSARBPROC)wglGetProcAddress("glGenBuffersARB")) == NULL)
	vvDebugMsg::msg(1, "BrickManager::BrickManager(): Could not get handle to glGenBuffersARB");

  if((glBindBufferARB = (PFNGLBINDBUFFERARBPROC)wglGetProcAddress("glBindBufferARB")) == NULL)
	vvDebugMsg::msg(1, "BrickManager::BrickManager(): Could not get handle to glBindBufferARB");

  if((glBufferDataARB = (PFNGLBUFFERDATAARBPROC)wglGetProcAddress("glBufferDataARB")) == NULL)
	vvDebugMsg::msg(1, "BrickManager::BrickManager(): Could not get handle to glBufferDataARB");

  if((glDeleteBuffersARB = (PFNGLDELETEBUFFERSARBPROC)wglGetProcAddress("glDeleteBuffersARB")) == NULL)
	vvDebugMsg::msg(1, "BrickManager::BrickManager(): Could not get handle to glDeleteBuffersARB");

  if((glMapBufferARB = (PFNGLMAPBUFFERARBPROC)wglGetProcAddress("glMapBufferARB")) == NULL)
	vvDebugMsg::msg(1, "BrickManager::BrickManager(): Could not get handle to glMapBufferARB");

  if((glUnmapBufferARB = (PFNGLUNMAPBUFFERARBPROC)wglGetProcAddress("glUnmapBufferARB")) == NULL)
	vvDebugMsg::msg(1, "BrickManager::BrickManager(): Could not get handle to glUnmapBufferARB");
	*/

  if((glTexImage3DEXT = (PFNGLTEXIMAGE3DEXTPROC)wglGetProcAddress("glTexImage3DEXT")) == NULL)
	vvDebugMsg::msg(1, "BrickManager::BrickManager(): Could not get handle to glTexImage3DEXT");

  if((glTexSubImage3DEXT = (PFNGLTEXSUBIMAGE3DEXTPROC)wglGetProcAddress("glTexSubImage3DEXT")) == NULL)
	vvDebugMsg::msg(1, "BrickManager::BrickManager(): Could not get handle to glTexSubImage3DEXT");

#endif


  vvDebugMsg::msg(3, "CubeInfo created");
}



/*
   This should be called at every frame
   this function updates the values that changes over time

   @param vd provides translation and rotation information
   @param eye used to compute the distance from eye to this brick
   @param _normal normal vector of 2D slices
   @param _volumeCenter all the slices are apart from volumeCenter by a multiple of dt
   @param isOrtho indicates normal vector and eye direction are parallel
   @param pixelToVoxelRatio indicates the ratio of how many voxels are mapped to one pixel
*/
void CubeInfo::initialize(vvVolDesc* _vd, vvMatrix* transformMatrix, vvVector3 eye, vvVector3 _normal, vvVector3 _volumeCenter, bool isOrtho, int pixelToVoxelRatio)
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


/** initializeTexture()
  initialize texture memory by creating texture id. 
  It copies the data in main memory to texture memory
*/  
int CubeInfo::initializeChannelTexture(int c)
{
  assert(c >= 0 && c < numChannel);

  glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

  //assert(texName[c] && pboName[c]);
  assert(texName[c]);

  //vvDebugMsg::msg(1, "textureSize: ", textureSize[0], textureSize[1], textureSize[2]);

  glEnable(GL_TEXTURE_3D_EXT);

  //glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, pboName[c]);
  glBindTexture(GL_TEXTURE_3D_EXT, texName[c]);
  glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_WRAP_S, GL_CLAMP);
  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_WRAP_T, GL_CLAMP);
  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_WRAP_R_EXT, GL_CLAMP);
  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
  glTexSubImage3DEXT(GL_TEXTURE_3D_EXT, 0, 0, 0, 0, brickSize, brickSize, brickSize,
	  			GL_LUMINANCE, GL_UNSIGNED_BYTE, memoryBuf[c]);

  glBindTexture(GL_TEXTURE_3D_EXT, 0);
  //glBindBufferARB(GL_PIXEL_UNPACK_BUFFER_ARB, 0);
  glDisable(GL_TEXTURE_3D_EXT);

  return 0;
}


/*


*/
int CubeInfo::draw(vvGLSL* shader, GLuint fragProgram)
{
  if(!inTexture)
  {
	vvDebugMsg::msg(2, "Use BrickManager::loadBrickToTexture() to load data into texture memory first");
	return -1;
  }

  if(texName == 0 || texName[0] == 0)
  {
	vvDebugMsg::msg(2, "texture name is wrong");
	return -1;
  }

  // long deadly rendering code


  vvVector3 isect[6];				// intersection points, maximum of 6 allowed when intersecting a plane and a volume [object space]
  vvVector3 texcoord[6];			// intersection points in texture coordinate space [0..1]
  vvVector3 farthest;				// volume vertex farthest from the viewer
  vvVector3 texPoint;				// arbitrary point on current texture
  vvVector3 delta;					// distance vector between textures [object space]
  vvVector3 texRange;				// range of texture coordinates
  vvVector3 texMin;					// minimum texture coordinate

  float     diagonal;               // probe diagonal [object space]
  int       isectCnt;               // intersection counter
  int       numSlices;              // number of texture slices along diagonal
  int       i, j, k;                // general counters

  // don't know how to set this value
  //numSlices = int(_renderState._quality * 100.0f);
  //if (numSlices < 1) numSlices = 1;               // make sure that at least one slice is drawn

  numSlices = 100 * (int)powf((float)2.0, (float)level);

  /*
	 delta and numSlices to get slices
	 normal, texPoint for isectPlaneCuboid
	 isectCnt, isectCoord for return of isectPlaneCuboid
	 texRange, texMin to compute texcoord
   */

  diagonal = (coord[6]-coord[0]).length();
  //vvDebugMsg::msg(2, "diagonal: ", diagonal);

  delta.copy(&normal);
  delta.scale(diagonal / ((float)numSlices));

  //vvDebugMsg::msg(2, "delta: ", delta[0], delta[1], delta[2]);
  //vvDebugMsg::msg(2, "normal: ", normal[0], normal[1], normal[2]);
  //vvDebugMsg::msg(2, "vd->pos[0]: ", vd->pos[0], vd->pos[1], vd->pos[2]);
  //vvDebugMsg::msg(2, "coord[6]: ", actualCoord[6][0], actualCoord[6][1], actualCoord[6][2]);


  /*
	 one problem of slicing was, two slices from two different bricks were not aligned each other
	 that is the distance between the two slices were not a multiple of dt.
	 This made resulting rendering have thick overlaps around brick boundaries.

	 In order to prevent it, one solution that we use here is,
	 we use the center of the entire volume, $c$
	 for the center of each brick, $x$
	 we compute the distance, $l$, from $x$ to the plane P $n \cdot w + c = 0$, where $n$ is the normal vector of slices
	 Then, $l$ can be expressed as follows:
	 $$ l = (x - c) \cdot n $$
	 
	 $$\ceil(\frac{l}{dt}) dt + p = \ceil(\frac{l}{dt}) dt + (c + x - l \cdot n)$$

	 where p denotes the point on P such that (x-p) is parallel to n, that is projection of x onto P

	 x = actualCenter;
	 c = volumeCenter;
	 l = l;
  */
  float l = (center - volumeCenter).dot(&normal);

  vvVector3 p = normal;
  p.scale((float)(-1.0) * l);
  p = p + center;

  vvVector3 offset = delta;
  offset.scale(ceilf(l/delta.length()));
  offset = offset + p;



  farthest.copy(&delta);
  farthest.scale((float)(numSlices - 1) / -2.0f);
  farthest.add(&vd->pos);
  farthest.add(&offset);
  //farthest.add(&actualCenter);

  //vvDebugMsg::msg(2, "offset: ", offset[0], offset[1], offset[2]);
  //vvDebugMsg::msg(2, "farthest: ", farthest[0], farthest[1], farthest[2]);
  //vvDebugMsg::msg(2, "vox: ", vd->vox[0], vd->vox[1], vd->vox[2]);

  for(i = 0; i < 3; i++)
  {
	texRange[i] = 1.0f - 1.0f / (float) actualSize[i];
	texMin[i] = 0.5f / (float) actualSize[i];
  }

  shader->resetTextureCount();

  glEnable(GL_TEXTURE_3D_EXT);

  char varName[20];
  for(int c = 0; c < numChannel; c++)
  {
#ifdef WIN32
	_snprintf(varName, sizeof(varName), "gl3dTex%d", c);
#else
	snprintf(varName, sizeof(varName), "gl3dTex%d", c);
#endif
	shader->initializeMultiTexture3D(fragProgram, varName, texName[c]);
  }

  // arbitrary point on the slices
  texPoint.copy(&farthest);

  for(i = 0; i < numSlices; i++)
  {
	// get intersections
	isectCnt = isect->isectPlaneCuboid(&normal, &texPoint, &coord[0], &coord[6]);

	texPoint.add(&delta);

	//vvDebugMsg::msg(2, "#slice, isectCnt: ", i, isectCnt);

	if(isectCnt < 3) continue;

	//if(minSlice != -1 && i < minSlice) continue;
	//if(maxSlice != -1 && i > maxSlice) continue;

	isect->cyclicSort(isectCnt, &normal);

	for(j = 0; j < isectCnt; j++)
	{
	  for(k = 0; k < 3; k++)
	  {
		texcoord[j][k] = (isect[j][k] - coord[0][k]) / actualSize[k];
		// FIXME: Can't understand this part!!!!
		texcoord[j][k] = texcoord[j][k] * texRange[k] + texMin[k];
	  }
	  //vvDebugMsg::msg(2, "texcoord[i]: ",(float)i, texcoord[j][0], texcoord[j][1], texcoord[j][2]);
	}

	glBegin(GL_TRIANGLE_FAN);
	glColor4f(1.0, 1.0, 1.0, 1.0);
	//glColor4f(1.0, 1.0, 1.0, 0.5);
	glNormal3f(normal[0], normal[1], normal[2]);

	for(j = 0; j < isectCnt; j++)
	{
	  //vvDebugMsg::msg(1, "#slice, #isect: ", i, j);
	  //vvDebugMsg::msg(1, "texcoord: ", texcoord[j][0], texcoord[j][1], texcoord[j][2]);
	  //vvDebugMsg::msg(1, "isect: ", isect[j][0], isect[j][1], isect[j][2]);
	  glTexCoord3f(texcoord[j][0], texcoord[j][1], texcoord[j][2]);
	  glVertex3f(isect[j][0], isect[j][1], isect[j][2]);
	}
	glEnd();
  }

  shader->disableMultiTexture3D();
  glBindTexture(GL_TEXTURE_3D_EXT, 0);
  glDisable(GL_TEXTURE_3D_EXT);

  rendered = false;

  return 0;
}

vvVector3 CubeInfo::getCoord(int i)
{
  if(i < 0 || i > 7)
  {
	vvDebugMsg::msg(2, "Wrong index for coordinate, must be 0, 1, 2, or 3");
	return vvVector3(0.0, 0.0, 0.0);
  }

  return coord[i];
}


void CubeInfo::setCenter()
{
  center = coord[0] + coord[6];
  center.scale(0.5);
}

void CubeInfo::setViewPortCorners(vvMatrix* transformMatrix)
{
  // clean up four corners
  viewPortCorners.clear();

  for(int i = 0; i < 8; i++)
  {
	viewPortCorners.push_back(coord[i]);
	viewPortCorners[i].multiply(transformMatrix);
  }
}

void CubeInfo::setBoundingBox()
{
  assert(viewPortCorners.size() == 8);

  float minX, maxX, minY, maxY;

  // find the min,max of this quad coordinate
  minX=maxX=viewPortCorners[0].e[0];
  minY=maxY=viewPortCorners[0].e[1];

  for(int i = 1; i < 8; i++)
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

void CubeInfo::setCost(int pixelToVoxelRatio)
{
  // Cost Function 2.
  // this version computes the bounding box area that will be shown on screen
  // if the area is big on screen, it gets the higher cost
  GLint vp[4];
  glGetIntegerv(GL_VIEWPORT, vp);

  if((boundingBox.e[2] < vp[0]) ||
	  (boundingBox.e[3] < vp[1]) ||
	  (boundingBox.e[0] > vp[0]+vp[2]) ||
	  (boundingBox.e[1] > vp[1]+vp[3]) )
  {
	// if this quad is outside the screen
	// we ignore this quad
	rendered = false;
	cost = 0.0;

	//vvDebugMsg::msg(2, "outside!");
	//vvDebugMsg::msg(2, "outside!", minX, maxX, minY, maxY);
	//vvDebugMsg::msg(2, "in bbox", vp[0], vp[0]+vp[2], vp[1], vp[1]+vp[3]);

  } else {
	// if this quad is inside the screen,
	// we set cost

	rendered = true;

	float area = (boundingBox[2]-boundingBox[0]) * (boundingBox[3]-boundingBox[1]);

	cost = area / distance;
	finer = (area > pixelToVoxelRatio * brickSize * brickSize);

	//vvDebugMsg::msg(2, "inside! ", minX, maxX, minY, maxY);
	//vvDebugMsg::msg(2, "in bbox ", vp[0], vp[0]+vp[2], vp[1], vp[1]+vp[3]);
  }
  //vvDebugMsg::msg(2, "cost is set to : ", cost);

}


/*
void CubeInfo::setFiner(int ratio)
{
  float area = (boundingBox[2]-boundingBox[0]) * (boundingBox[3]-boundingBox[1]);

  finer = (area > ratio * brickSize * brickSize);
}
*/




