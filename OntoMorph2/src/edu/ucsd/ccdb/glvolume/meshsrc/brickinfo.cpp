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


#include "brickinfo.h"
#include "vvdebugmsg.h"
#include <math.h>


using namespace std;
using namespace MipMapVideoLib;

/////////////////////////////////////////////////////////////////////////
// BrickInfo Class 
/////////////////////////////////////////////////////////////////////////

BrickInfo::BrickInfo(int _level, int _volNum, int _frameIndex, int _numChannel, int _brickSize) :
  level(_level), volNum(_volNum), frameIndex(_frameIndex), numChannel(_numChannel), brickSize(_brickSize)
{
  //=========================================================
  // indices and names for each channel
  //=========================================================
  //pboName = new GLuint[numChannel];
  //pboIndex = new int[numChannel];
  memID = new int[numChannel];
  memoryBuf = new uchar*[numChannel];
  for(int i = 0; i < numChannel; i++)
	memoryBuf[i] = NULL;

  //resetPBO();
  texName = new GLuint[numChannel];
  texIndex = new int[numChannel];
  resetTex();

  //=========================================================
  // LRU caching list
  //=========================================================
  prevInMemory = NULL;
  nextInMemory = NULL;
  prevInTexture = NULL;
  nextInTexture = NULL;

  ///////////////////////////////////////////////////
  // time-dependent values
  // this should be initialized by initialize()
  ///////////////////////////////////////////////////
  inMemory = false;
  inTexture = false;
  rendered = false;
  finer = false;

  boundingBox.set(0.0, 0.0, 0.0, 0.0);
  distance = 0.0;
  cost = 0.0;
  frame = 0;
}

BrickInfo::~BrickInfo()
{
  delete[] texIndex;
  delete[] texName;
  delete[] memID;
  delete[] memoryBuf;
  //delete[] pboIndex;
  //delete[] pboName;
}

  /*
void BrickInfo::resetPBO()
{
  for(int c = 0; c < numChannel; c++)
  {
	pboName[c] = 0;
	pboIndex[c] = -1;
  }
}
  */

void BrickInfo::resetTex()
{
  for(int c = 0; c < numChannel; c++)
  {
	texName[c] = 0;
	texIndex[c] = -1;
  }
}

#if 0
void BrickInfo::printBrickInfo()
{
  cerr << "[BrickInfo] Level: " << level << " X: " << brickX << " Y: " << brickY << " Z: " << brickZ << endl;
  if(inMemory)
	cerr << "[BrickInfo] Data is loaded in Memory: pboName=" << pboName << ", index=" << pboIndex << endl;
  else
	cerr << "[BrickInfo] Data is not loaded in texture" << endl;

  if(inTexture)
	cerr << "[BrickInfo] Data is loaded in Texture: texName=" << texName << ", index=" << texIndex << endl;
  else
	cerr << "[BrickInfo] Data is not loaded in texture" << endl;

  cerr << "[BrickInfo] rendered = " << rendered << endl;

}
#endif

void BrickInfo::setDistance(vvVector3 eye, bool isOrtho)
{
  if(isOrtho)
	distance = -1 * center.dot(&normal);
  else
	distance = center.distance(&eye);
}

/////////////////////////////////////////////////////////////////////////
// End of BrickInfo Class 
/////////////////////////////////////////////////////////////////////////




