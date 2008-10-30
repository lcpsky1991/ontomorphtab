////////////////////////////////////////
// camera.h
// originally from UCSD CSE169 template
////////////////////////////////////////

#ifndef _CAMERA_H_
#define _CAMERA_H_

#include "core.h"

////////////////////////////////////////////////////////////////////////////////

namespace MipMapVideoLib
{
  class Camera 
  {
	public:

	  Camera();
	  virtual ~Camera() {};

	  void update();
	  void reset();
	  void draw();

	  // Access functions
	  void setAspect(float a)	{_aspect=a;}
	  void setDistance(float d)	{_distance=d;}
	  void setAzimuth(float a)	{_azimuth=a;}
	  void setIncline(float i)	{_incline=i;}
  //	void SetShift(float s)		{Shift=s;}

	  void viewFromX();
	  void viewFromY();
	  void viewFromZ();

	  float getDistance()			{return _distance;}
	  float getAzimuth()			{return _azimuth;}
	  float getIncline()			{return _incline;}
	  float getAspect()				{return _aspect; }
	  //float GetShift()			{return Shift;}

  private:
	  // Perspective controls
	  float _FOV;		// Field of View Angle
	  float _aspect;	// Aspect Ratio
	  float _nearClip;	// Near clipping plane distance
	  float _farClip;	// Far clipping plane distance

	  // Polar controls
	  float _distance;	// Distance of the camera eye position to the origin
	  float _azimuth;	// Rotation of the camera eye position around the Y axis
	  float _incline;	// Angle of the camera eye position over the XZ plane
	  //float Shift;	// Shift left or right
  };
}

////////////////////////////////////////////////////////////////////////////////

/*
The Camera class provides a simple means to controlling the 3D camera. It could
be extended to support more interactive controls. Ultimately. the camera sets the
GL projection and viewing matrices.
*/

#endif
