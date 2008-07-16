
/**************************************************************************************

	Author			:	Raj Singh (rsingh@ncmir.ucsd.edu)

	Description		:	Header file for the pyramidal Tiff loader code. The assumption
						is that the TIFF file being read is stored in tiles at multiple
						resolutions.

	Status			:	Experimental

	Notes			:


**************************************************************************************/


#ifndef _TIFFLOADER_H
#define _TIFFLOADER_H

// Windows specific stuff ---------------------------------------------------------------//
#define WIN32_LEAN_AND_MEAN
#ifdef	WIN32

#define _WIN32_WINNT    				0x0501  // Macro for getting the switchToThread() to work
#include <windows.h>
#include <gl/GL.h>
#include <stdlib.h>
#include <malloc.h>

#endif
// Windows specific stuff ---------------------------------------------------------------//

#ifdef linux
#include <GL/gl.h>
#include <stdlib.h>
#endif

#include "tiffio.h"
#include <string.h>
#include <math.h>
#include "plugin.h"

/*
#ifdef TL_DEBUG 
	#define dPrintf(...)   printf(__VA_ARGS__)
#else
	#define dPrintf(...)	
#endif
*/
#define dPrintf					printf

#define TL_OK					0
#define TL_FAIL					-1000
#define TL_FILEOPEN_ERR			-1001
#define TL_INVALID_VIEWPORT		-1002
#define TL_TIFF_READ_ERR		-1003
#define TL_INSUFF_BUFFER		-1004

// The following structure is used for storing information about the Tiff's directory structure
struct tiffDirInfo
{
	unsigned		level;			// level number (0 is max res, 1 is half, 2 is fourth ...)
	unsigned		imgW, imgH;
	unsigned		tileW, tileH;
	float			xDpi, yDpi;		//pixels / inch
	
	unsigned		Bps;			// Bytes per sample
	unsigned		spp;			// Samples per pixel
	
	unsigned short	compressionScheme;
									// This is a number understood by TIFF standard
};


class tiffLoader {
private:
		unsigned	imgW, imgH, zoomLevels;
		unsigned	tileW, tileH;

		unsigned	Bps;			// Bytes per sample
		unsigned	spp;			// Samples per pixel
		unsigned	bytesPerPixel;
		GLenum		pixelFormat;

		unsigned int screenX, screenY;
		float screenDpi, xDpi, yDpi;
		unsigned int dirCount;
		unsigned int resolutionUnits;
		unsigned short	compressionScheme;

 		TIFF *tPtr;
		struct tiffDirInfo *dirInfo;

		void *tempTileBuffer;
public:
		tiffLoader();
		~tiffLoader();

		virtual int init(char *_file, unsigned _screenX, unsigned _screenY, float _screenDpi);
										// If successful, the tiff file is opened and header information is loaded
										// up. The 1st param is the name of the Tiff file with path.
										// 2nd and 3rd params are the screen resolution of the rendering surface
										// 4th param is the screen DPI

		virtual int extractRGBAImage(double _bl_x, double _bl_y, double _ur_x, double _ur_y, void *_oBuf, unsigned _bufSize,
							unsigned &_W, unsigned &_H);
										// User has to specify the viewport of the sub-image to be extracted in
										// the first 4 parameters (_bl_x, _bl_y, _ur_x, _ur_y). The viewport is a rectangle
										// with coordinates within 0.0 to 1.0. 
										// 5th parameter is the buffer in which the output pixels are returned
										// 6th parameter is the size (in Bytes) of the buffer provided
										// 7th and 8th parameter are the W and H (in pixels) of the buffer on screen. 
										// The user is expected to provide an approximate W and H of the image as a guide to
										// extracting the correct resolution from the TIFF file. When the function 
										// returns the _W and _H parameters might be changed due to rounding off
										//
										// NOTE : This interface is the simplest to use for an application but
										// very inefficient. Needs to improve !!

		unsigned getImageW() {return imgW;};	// Return the width in pixels of the entire image
		unsigned getImageH() {return imgH;};	// Return the height in pixels of the entire image
		unsigned getTileW() {return tileW;};	// Return width of a tile
		unsigned getTileH() {return tileH;};	// Return height of a tile
		GLenum	getPixelFormat() {return pixelFormat;};
										// We will hard code this to return GL_RBGA for now
		unsigned short getCompressionScheme() {return 	compressionScheme;};
		unsigned getBytesPerPixel() {return bytesPerPixel;};
												// This might always return 4 (32 bits) since we are converting 
												// everything to RGBA
};



#endif
