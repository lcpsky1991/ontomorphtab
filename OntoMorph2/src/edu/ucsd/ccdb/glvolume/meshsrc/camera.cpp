////////////////////////////////////////
// camera.cpp
////////////////////////////////////////

#include "camera.h"

using namespace MipMapVideoLib;

////////////////////////////////////////////////////////////////////////////////

Camera::Camera() {
	reset();
}

////////////////////////////////////////////////////////////////////////////////

void Camera::update() {
}

////////////////////////////////////////////////////////////////////////////////

void Camera::reset() {

	//_FOV=60.0f;
  	_FOV = 30.0f;
	_aspect=1.4f;
	_nearClip=1.f;
	//_nearClip = 1.0f;
	_farClip=100000.0f;
	//FarClip=1000000.0f;

	_distance=1000.0f;
	_azimuth=15.0f;
	_incline=30.0f;
}

void Camera::viewFromX()
{
	// down -x axis
	_azimuth=-90.0f;
	_incline=0.0f;
}

void Camera::viewFromY()
{
	// down -y axis
	_azimuth=0.0f;
	_incline=90.0f;
}

void Camera::viewFromZ()
{
	// down -z axis
	_azimuth=0.0f;
	_incline=0.0f;
}

////////////////////////////////////////////////////////////////////////////////

void Camera::draw() 
{
  // Tell GL we are going to adjust the projection matrix
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();

  // Set perspective projection
  gluPerspective(_FOV, _aspect, _nearClip, _farClip);
  

  // Place camera
  glTranslatef(0,0,-_distance);
  //glRotatef(_incline,1.0f,0.0f,0.0f);
  //glRotatef(_azimuth,0.0f,1.0f,0.0f);
  
  //glTranslatef(Shift, 0, 0);
}

////////////////////////////////////////////////////////////////////////////////



