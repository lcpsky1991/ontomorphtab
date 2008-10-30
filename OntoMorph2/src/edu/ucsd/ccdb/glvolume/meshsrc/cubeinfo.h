#ifndef _CUBE_INFO_H_
#define _CUBE_INFO_H_

#include "brickinfo.h"
#include <stdio.h>

// Looks like support for ARB extensions is missing under win32. So adding it here - Raj
#if defined(_USE_GLARB_UNDER_WIN32) && !defined(__USEGLARBUNDERWIN32__)
#define __USEGLARBUNDERWIN32__

	#define GL_ARRAY_BUFFER_ARB 0x8892
	#define GL_STATIC_DRAW_ARB 0x88E4

	typedef void (APIENTRY * PFNGLBINDBUFFERARBPROC) (GLenum target, GLuint buffer);
	typedef void (APIENTRY * PFNGLDELETEBUFFERSARBPROC) (GLsizei n, const GLuint *buffers);
	typedef void (APIENTRY * PFNGLGENBUFFERSARBPROC) (GLsizei n, GLuint *buffers);
	typedef void (APIENTRY * PFNGLBUFFERDATAARBPROC) (GLenum target, int size, const GLvoid *data, GLenum usage);
	typedef GLvoid* (APIENTRY * PFNGLMAPBUFFERARBPROC) (GLenum target, GLenum access);
	typedef GLboolean (APIENTRY * PFNGLUNMAPBUFFERARBPROC) (GLenum target);
	typedef void (APIENTRY * PFNGLTEXIMAGE3DEXTPROC) (GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height, GLsizei depth, GLint border, GLenum format, GLenum type, const GLvoid *pixels);
	typedef void (APIENTRY * PFNGLTEXSUBIMAGE3DEXTPROC) (GLenum, GLint, GLint, GLint, GLint, GLsizei, GLsizei, GLsizei, GLenum, GLenum, const GLvoid *);


#endif


namespace MipMapVideoLib
{
  class CubeInfo: public BrickInfo
  {
	public:
	  CubeInfo(vvVector3 _bottomLeftFront,
				  vvVector3 _topRightBack,
				  int _level,
				  int _volNum,
				  int _numFrame,
				  int _numChannel,
				  int _brickSize);

	  virtual ~CubeInfo() {};

	public:
	  void initialize(vvVolDesc* _vd, vvMatrix* transformMatrix, vvVector3 eye, vvVector3 _normal, vvVector3 _volumeCenter, bool isOrtho, int pixelToVoxelRatio);
	  int initializeChannelTexture(int c);
	  int draw(vvGLSL* _shader, GLuint _fragProgram);
	  vvVector3 getCoord(int i);

	protected:

	  // eight corners are saved in the following way:
	  //	[0]: bottom left front
	  //	[1]: top left front
	  //	[2]: top right front
	  //	[3]: bottom right front
	  //	[4]: bottom left back
	  //	[5]: top left back
	  //	[6]: top right back
	  //	[7]: bottom right back
	  vvVector3 coord[8];

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

   private:
	  
#if !defined(_USE_GLARB_UNDER_WIN32)
	  typedef void (glTexImage3DEXT_type)(GLenum, GLint, GLenum, GLsizei, GLsizei, GLsizei, GLint, GLenum, GLenum, const GLvoid*);
	  glTexImage3DEXT_type* glTexImage3DEXT;
#else
	/*
	// VBO Extension Function Pointers
	PFNGLGENBUFFERSARBPROC glGenBuffersARB;					// VBO Name Generation Procedure
	PFNGLBINDBUFFERARBPROC glBindBufferARB;					// VBO Bind Procedure
	PFNGLBUFFERDATAARBPROC glBufferDataARB;					// VBO Data Loading Procedure
	PFNGLDELETEBUFFERSARBPROC glDeleteBuffersARB;
	PFNGLMAPBUFFERARBPROC glMapBufferARB;
	PFNGLUNMAPBUFFERARBPROC glUnmapBufferARB;
	*/
	PFNGLTEXIMAGE3DEXTPROC glTexImage3DEXT;
	PFNGLTEXSUBIMAGE3DEXTPROC glTexSubImage3DEXT;
#endif

  }; // end of CubeInfo

} // end of MipMapVideoLib

#endif

