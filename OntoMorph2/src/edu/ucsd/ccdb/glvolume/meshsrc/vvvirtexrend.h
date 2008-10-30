// Virvo - Virtual Reality Volume Rendering
// Copyright (C) 1999-2003 University of Stuttgart, 2004-2005 Brown University
// Contact: Jurgen P. Schulze, jschulze@ucsd.edu
//			Han Suk Kim, hskim@cs.ucsd.edu
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

#ifndef _VVVIRTEXREND_H_
#define _VVVIRTEXREND_H_

#include "brickinfo.h"
#include "brickmanager.h"

#include <vvopengl.h>
#include <vvexport.h>
#include <vvgltools.h>
#include <vvdynlib.h>

#include <vvvoldesc.h>
#include <vvrenderer.h>
#include <vvdebugmsg.h>
#include <queue>

//class BrickInfo;
class MipMapVideoLib::BrickManager;

struct compareBrick {
  bool operator()( MipMapVideoLib::BrickInfo* s1, MipMapVideoLib::BrickInfo* s2) const
  {
	//vvDebugMsg::msg(2, "comparing brickinfo: ", s1->getCost(), s2->getCost());
	return s1->getCost() < s2->getCost();
  }
};

struct sortingBrickPredicate
{
  bool operator()( MipMapVideoLib::BrickInfo* s1,  MipMapVideoLib::BrickInfo* s2) 
  {
	return (s1->getDistance() < s2->getDistance());
  }
};

//============================================================================
// Class Definitions
//============================================================================

/** Volumne rendering engine using bricking algorithm plus out-of-core execution mechanism
  so the texture memory in GPU can be virtualized to get rid of memory limit.
  @author Han Kim (hskim@cs.ucsd.edu) 
  @author Jurgen Schulze (jschulze@ucsd.edu)
  @see vvRenderer
*/
class VIRVOEXPORT vvVirTexRend : public vvRenderer
{
  public:
	vvVirTexRend(vvVolDesc*, vvRenderState);
	vvVirTexRend(vvVolDesc*, MipMapVideoLib::BrickManager* bm, vvRenderState renderState, int _volNum, int _frameNum, vvGLSL* _shader, GLuint _fragProgram, int brickLimit);
	virtual ~vvVirTexRend();

  public:
    // Public methods that should be redefined by subclasses:
	void  renderMultipleVolume();

	float getChannelHue(int chan);
	void setChannelHue(int chan, float value);
	int isActiveChannel(int chan);
	void setActiveChannel(int chan, int value);
	int getPixelToVoxelRatio() { return _pixelToVoxelRatio; }
	void setPixelToVoxelRatio(int ratio) { _pixelToVoxelRatio = ratio; }
	int getBrickLimit() { return _brickLimit; }
	void setBrickLimit(int limit) { _brickLimit = limit; }

	vvVector3 translation;
	vvMatrix rotation;

  protected:
	void init();
	void computeInitialLevel();
	int generateMeshing();
	int meshInit(vvVector3 eye, vvMatrix* transformMatrix);

    void setViewingDirection(const vvVector3*);
    void setObjectDirection(const vvVector3*);

	void setGLenvironment();
	void unsetGLenvironment();

  protected:
	vvVector3 volumeSize;
	int totalMemoryUsage;			///<
	int initLevel;					///<
	int minimumLevel;				///< the greatest lower bound for level

	vvVector3 normal;
    vvVector3 viewDir;				///< user's current viewing direction [object coordinates]
    vvVector3 objDir;				///< direction from viewer to object [object coordinates]
	vvVector3 volumeCenter;			///< the center of volume [world coordinates]
	float* channelHue;				///< color vector for each channel
	int* activeChannel;			///< indicates whether each channel is rendered

    vvVector4 _frustum[6];			///< current planes of view frustum

	std::list < MipMapVideoLib::BrickInfo* > quadList;
	std::priority_queue < MipMapVideoLib::BrickInfo*, std::vector<MipMapVideoLib::BrickInfo*>, compareBrick> quadPQueue;
	MipMapVideoLib::BrickManager* brickManager;

	int volNum;
	int frameNum;

	vvGLSL* shader;
	GLuint fragProgram;
	bool isOrtho;

	int _pixelToVoxelRatio;			///< one brick is subdivided if the area of the brick is larger than _pixelToVoxelRatio*brickSize^2
	int _brickLimit;				///< subdivision of mesh stops if the number of bricks being rendered is larger than _brickLimit

    bool extMinMax;					///< true = maximum/minimum intensity projections supported
    bool extBlendEquation;			///< true = support for blend equation extension

#if defined(WIN32)                                                                                                                     
    PFNGLBLENDEQUATIONEXTPROC glBlendEquationVV;                                                                                       
    PFNGLBLENDCOLORPROC glBlendColor;
#else
    typedef void (glBlendEquationEXT_type)(GLenum);
    glBlendEquationEXT_type* glBlendEquationVV;                                                                                        
#endif  

	//////////////////////////////////////
	// GL state variables
	//////////////////////////////////////
    GLboolean glsCulling;                         ///< stores GL_CULL_FACE
    GLboolean glsBlend;                           ///< stores GL_BLEND
    GLboolean glsColorMaterial;                   ///< stores GL_COLOR_MATERIAL
    GLint glsBlendSrc;                            ///< stores glBlendFunc(source,...)
    GLint glsBlendDst;                            ///< stores glBlendFunc(...,destination)
    GLboolean glsLighting;                        ///< stores GL_LIGHTING
    GLboolean glsDepthTest;                       ///< stores GL_DEPTH_TEST
    GLint glsMatrixMode;                          ///< stores GL_MATRIX_MODE
    GLint glsDepthFunc;                           ///< stores glDepthFunc
    GLint glsBlendEquation;                       ///< stores GL_BLEND_EQUATION_EXT
    GLboolean glsDepthMask;                       ///< stores glDepthMask

}; /// end of vvVirTexRend class definition 

#endif // end of __VVVIRTEXREND_H_


//============================================================================
// End of File
//============================================================================







