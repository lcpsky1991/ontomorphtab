
#ifndef _QUAD_INFO_H_
#define _QUAD_INFO_H_

#include "brickinfo.h"

namespace MipMapVideoLib
{
  class QuadInfo: public BrickInfo
  {
	public:
	  QuadInfo(vvVector3 _bottomLeft,
				  vvVector3 _topRight,
				  int _level,
				  int _volNum,
				  int _frameIndex,
				  int _numChannel,
				  int _brickSize);

	  virtual ~QuadInfo() {};

	public:
	  void initialize(vvVolDesc* _vd, vvMatrix* transformMatrix, vvVector3 eye, vvVector3 _normal, vvVector3 _volumeCenter, bool isOrtho, int pixelToVoxelRatio);
	  int initializeChannelTexture(int c);
	  int draw(vvGLSL* _shader, GLuint _fragProgram);
	  vvVector3 getCoord(int i);

	protected:
	  //four corners are saved in the following way:
	  //	[0]: bottom left
	  //	[1]: top left
	  //	[2]: top right
	  //	[3]: bottom right
	  vvVector3 coord[4];

	protected:

	  ///////////////////////////////////////////////
	  // setters
	  // all variables are set inside the constructor
	  // no need to be called from outside
	  ///////////////////////////////////////////////
	  void setCenter();
	  void setViewPortCorners(vvMatrix* transformMatrix);
	  void setBoundingBox();
	  void setCost(int pixelToVoxelRatio);
	  //void setFiner();
  }; // end of QuadInfo Class

} // end of namespace MipMapVideoLib


#endif

