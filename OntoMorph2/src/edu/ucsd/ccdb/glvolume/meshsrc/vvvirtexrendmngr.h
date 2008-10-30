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

#ifndef _VVVIRTEXREND_MNGR_H_
#define _VVVIRTEXREND_MNGR_H_

#include <time.h>
#include <map>

#include <vvvoldesc.h>
#include <vvrenderer.h>
#include <vvglsl.h>
#include <vvstopwatch.h>

#include "camera.h"
#include "brickmanager.h"
#include "vvvirtexrend.h"
#include "brickinfo.h"


namespace MipMapVideoLib
{
  //============================================================================
  // Class Definitions
  //============================================================================
  class vvVirTexMultiRendMngr
  {
	public:
	  // =============================================================
	  // Constructor and destructor
	  // =============================================================
	  vvVirTexMultiRendMngr();
	  ~vvVirTexMultiRendMngr();

	  // =============================================================
	  // main methods

	  /** load
		initializes MipMapVideo library
		@param configFile the name of configuration file. For the format of this file, see ??
	  */
	  void load(const char* configFile);

	  /** renderMultipleVolume()
		renders multiple volumes with multiple level of details.
		needs to be called at every frame.
	  */
	  void renderMultipleVolume();

	  /** reset
		resets all the location parameters, the location of volume and camera, translation and rotation matrix
	  */
	  void reset();

	  vvVirTexRend* getRenderer(int vol, int frame);

	  /** getNumVolume
		returns the total number of volumes loaded in this system
	  */
	  int getNumVolume() { return _numVolume; }

	  /** setCurrentVolume
	  */
	  void setCurrentVolume(int vol) { _currentVolume = vol; }

	  /** getCurrentVolume
	  */
	  int getCurrentVolume() { return _currentVolume; }
		

	  /** setShowTexture
		activates textures if true, deactivates otherwise

		@param vol the volume number
		@param toggle 0 (false) or 1 (true)
	  */
	  void setShowTexture(int vol, int toggle);

	  /** setShowBoundary
		displays boundaries of bricks if true
		skip rendering boundaries otherwise

		@param vol the volume number
		@param toggle 0 (false) or 1 (true)
	  */
	  void setShowBoundary(int vol, int toggle);


	  // ============================================================
	  // Volume location
	  // these methods provide the way to pass the volume location information 
	  // from UI to library. Whenever users move/rotate volumes, these functions need to be called
	  // ============================================================

	  /** translateVolume
		moves the volume to <x, y, z> direction
		i.e. if $l$ is current location vector then after this call, 
		the center of the volume is at $l$ + <x, y, z>

		@param vol volume number to be translated
		@param x translation in x direction [projection coordinate]
		@param y translation in y direction [projection coordinate]
		@param z translation in z direction [projection coordinate]
	  */
	  void translateVolume(int vol, float x, float y, float z);

	  /** rotateVolume
		rotates the volume by angle degree along with <x, y, z> axis

		@param vol volume number to be rotated
		@param angle the degree of rotation
		@param x axis coordinate in x direction [projection coordinate]
		@param y axis coordinate in y direction [projection coordinate]
		@param z axis coordinate in z direction [projection coordinate]
	  */
	  void rotateVolume(int vol, float angle, float x, float y, float z);

	  /** resetTransformation()
		resets all the trnasformation parameter.
		translation vector is set to 0,
		rotation matrix is set to identity
	  */
	  void resetTransformation();


	  // ============================================================
	  // Frame Control
	  // we assume all volumes have the same number of frames
	  // frame operations affects all of them
	  // ============================================================

	  /** setCurrentFrame
		sets the frame number
	  */
	  void setCurrentFrame(int frame);

	  /** getCurrentFrame
		returns the current frame number
	  */
	  int getCurrentFrame() { return _currentFrame; }

	  /** getNumFrame
		returns the total number of frames loaded in this system
	  */
	  int getNumFrame() { return _numFrame; }

	  /** nextFrame
		displays the next frame
	  */
	  void nextFrame();

	  /** prevFrame
		diplsays the previous frame
	  */
	  void prevFrame();

	  /** increasePlaySpeed
		increases the play speed of this rendering system
		renderer displays the next frame after a fixed interval (default is 1.0 second)
		increasing the play speed means increasing the time interval, which causes
		the frames to switch slower
	  */
	  void increasePlaySpeed();

	  /** decreasePlaySpeed
		decreases the play speed of this rendering system
		renderer displays the next frame after a fixed interval (default is 1.0 second)
		decreasing the play speed means decreasing the time interval, which causes
		the frames to switch faster
	  */
	  void decreasePlaySpeed();

	  /** pausePlay
		stops video playing and renders only one frame
	  */
	  void pausePlay() { _isOnPlay = false; }

	  /** startPlay
		start video playing and renders multiple frames one by one
	  */
	  void startPlay() { _isOnPlay = true; }

	  // ============================================================
	  // Channel control
	  // ============================================================
	  /** getNumChannel
		returns the number of channels that volume <emph>vol</emph> has
		@param vol specifies the volume
		@return int the number of channels
	  */
	  int getNumChannel(int vol);

	  /** isActiveChannel
		returns whether or not channel <emph>chan</emph> of volume <emph>vol</emph>
		is set to active

		@param vol
		@param chan
		@return true if chan is active, false otherwise
	  */
	  int isActiveChannel(int vol, int chan);

	  /** setAvtiveChannel
		de/activates channel <emph>chan</emph>. 
		if one channel is activated, the channel is rendered on screen
		if deactivated, the channel is ignored

		@param value 0 (deactivate) or 1 (activate)
	  */
	  void setActiveChannel(int vol, int chan, int value);

	  /** getChannelHue
		returns the hue value in the HSV color scheme

		@param vol the volume number
		@param chan the channel number
		@return hue [0.0, 1.0]
	  */
	  float getChannelHue(int vol, int chan);

	  /** setChannelHue
		a color is assigned to each channel. 
		setChannelHue provides an interface to change the color

		@param vol the volume number
		@param chan the channel number
		@param value hue value in the HSV color scheme [0.0, 1.0]
	  */
	  void setChannelHue(int vol, int chan, float value);


	  /** setPixelToVoxelRatio
		determines image quality
		ask han kim for detail
	  */
	  void setPixelToVoxelRatio(int vol, int ratio);

	  /** setBrickLimit
		determines image quality
		ask han kim for detail
	  */
	  void setBrickLimit(int vol, int value);

	  /** dynamic adjustment of brick limit
	  */
	  void adjustBrickLimit(float currentFrameRate);

	  /**
		reset brick limit
	  */
	  void resetBrickLimit();


	  /*=============================================================
		Camera operation
		=============================================================*/
	  //void setCameraAzimuth(float a) { _camera.setAzimuth(a); }
	  //void setCameraIncline(float i) { _camera.setIncline(i); }
	  void setCameraAzimuthIncline(float a, float i);
	  void setCameraDistance(float d);
	  void setCameraAspect(float r);

	  float getCameraAzimuth() { return _camera.getAzimuth(); }
	  float getCameraIncline() { return _camera.getIncline(); }
	  float getCameraDistance() { return _camera.getDistance(); }
	  float getCameraAspect() { return _camera.getAspect(); }

	  void cameraViewFromX();
	  void cameraViewFromY();
	  void cameraViewFromZ();


	  /** computes equations for six view frustum planes
	  */
	  //void updatePlaneEquations();

	  /** view frustum culling test
	  */
	  //int viewFrustumCullingTest(MipMapVideoLib::BrickInfo* brick);





	  // ============================================================
	  // Rendering time
	  // ============================================================
	  float getLastRenderTime() { return _lastRenderTime; }
	  float getLastComputeTime() { return _lastComputeTime; }
	  float getLastGLDrawTime() { return _lastGLdrawTime; }
	  float getLastPlaneSortingTime() { return _lastPlaneSortingTime; }


	protected:
	  void setGLenvironment();
	  void unsetGLenvironment();
	  int loadConfigFile(const char* configFileName);

	public:
	  // constant
	  static const int FRAME_PRELOAD = 5;
	  static const int IDEAL_FRAMERATE = 30;
	  static const int _epsilon = 5;

	protected:
	  
	  

	  int _dim;
	  int _numVolume;
	  int _currentVolume;
	  int _numFrame;
	  int _currentFrame;

	  MipMapVideoLib::BrickManager* _brickManager;
	  MipMapVideoLib::VolumeInfo*** _volumeInfoList;
	  vvVirTexRend*** _rendererList;

	  map<int, std::string, std::less<int> > _fragProgramPathMap;
	  map<int, GLuint, std::less<int> > _fragProgramMap;

	  vvGLSL* _glslShader;
	  GLuint* _shaderProgram;
	  bool _isShaderLoaded;

	  bool* _showBoundary;						///< show-brick-boundary control for each volume
	  bool* _showTexture;						///< show-texture control for each volume
	  //int* _brickLimit;							///< the upper limit for rendering each volume
	  int* _pixelToVoxelRatio;					///< pixel to voxel ratio for each volume

	  float _playSpeed;
	  bool _isOnPlay;
	  int _currentBrickLimit;

	  vvStopwatch* _sw;

	  float _lastRenderTime;                   ///< time it took to render the previous frame (seconds)
	  float _lastComputeTime;
	  float _lastPlaneSortingTime;
	  float _lastGLdrawTime;

	  float _lastChange;

	  MipMapVideoLib::Camera _camera;
	  vvMatrix _transformMatrix;


	  bool extMinMax;                               ///< true = maximum/minimum intensity projections supported
	  bool extBlendEquation;                        ///< true = support for blend equation extension
	  typedef void (glBlendEquationEXT_type)(GLenum);
	  glBlendEquationEXT_type* glBlendEquationVV;

	  // GL state variables:
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
	  GLboolean glsTexColTable;                     ///< stores GL_TEXTURE_COLOR_TABLE_SGI
	  GLboolean glsSharedTexPal;                    ///< stores GL_SHARED_TEXTURE_PALETTE_EXT

  }; // end of class definition

} // end of namespace MipMapVideoLib

//extern MipMapVideoLib::vvVirTexMultiRendMngr* g_rendererManager;

#endif

//============================================================================
// End of File
//============================================================================

