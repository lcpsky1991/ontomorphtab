
/**************************************************************************************

	Author			:	Raj Singh (rsingh@ncmir.ucsd.edu)

	Description		:	Cpp file for the pyramidal Tiff loader code
	Status			:	Experimental

	Notes			:	[1] Though the TIFF standard supports resolution in cms and inches
						for the sake of sanity, we will convert everything to inches.

						[2] Libtiff provides a function to read and convert tiles to RGBA
						buffer. We will be using this function to convert all tiles to a 
						standard RGBA format irrespective of how the image pixel format
						actually is.

						[3] Level 0 is the biggest resolution .. level 1 is 1/2 .. level 2 is 1/4

**************************************************************************************/

#include "tiffLoader.h"

tiffLoader::tiffLoader()
{

	// Init stuff
	imgW = imgH = zoomLevels = tileW = tileH = 0;
	bytesPerPixel = 0;
	pixelFormat = GL_RGBA;
	tPtr = NULL;
	dirCount = 0;
	dirInfo = NULL;
	resolutionUnits =  RESUNIT_INCH;	// defined in tiffio.h

	screenX = screenY = 0;
	screenDpi = xDpi = yDpi = 0.0f;
	tempTileBuffer = NULL;
	
}

tiffLoader::~tiffLoader()
{

	// free memory and cleanup
	if(tPtr) {
		TIFFClose(tPtr);
		tPtr = NULL;
	}

	if(dirInfo){
		free(dirInfo);
		dirInfo = NULL;
	}

	if(tempTileBuffer){
		free(tempTileBuffer);
		tempTileBuffer = NULL;
	}

}


int tiffLoader::init(char *_file, unsigned _screenX, unsigned _screenY, float _screenDpi)
{
	
	unsigned int i; 

	// not a very strong err check here
	screenX = _screenX;
	screenY = _screenY;
	screenDpi = _screenDpi;

	if(_file == NULL || strcmp(_file, "") == 0) 
	{
		dPrintf("\ntiffLoader::init: File name empty.");
		return TL_FILEOPEN_ERR;
	}

	// try to open the file here
	if((tPtr = TIFFOpen(_file, "r")) == NULL)
	{
		dPrintf("\ntiffLoader::init: Error opening TIFF file %s", _file);
		return TL_FILEOPEN_ERR;
	}

	// Read some Tiff Tags that the class needs to provide
	TIFFGetField(tPtr, TIFFTAG_IMAGEWIDTH, &imgW);
	TIFFGetField(tPtr, TIFFTAG_IMAGELENGTH, &imgH);
	TIFFGetField(tPtr, TIFFTAG_TILEWIDTH, &tileW);
	TIFFGetField(tPtr, TIFFTAG_TILELENGTH, &tileH);
	TIFFGetField(tPtr, TIFFTAG_BITSPERSAMPLE, &Bps);	Bps /= 8;
	TIFFGetField(tPtr, TIFFTAG_SAMPLESPERPIXEL, &spp);
	//bytesPerPixel = Bps * spp;
	bytesPerPixel = 4; // We will convert all image buffers to RGBA internally

	TIFFGetField(tPtr, TIFFTAG_COMPRESSION, &compressionScheme);

	// get the resolution units. We will convert everything to inches
	TIFFGetField(tPtr, TIFFTAG_RESOLUTIONUNIT, &resolutionUnits);
	TIFFGetField(tPtr, TIFFTAG_XRESOLUTION, &xDpi);
	TIFFGetField(tPtr, TIFFTAG_YRESOLUTION, &yDpi);

	switch(resolutionUnits)
	{
	case  RESUNIT_NONE:
		dPrintf("\ntiffLoader::init: No resolution units in TIFF file. Expect failure !!");
		break;
	case  RESUNIT_INCH: // do nothing here. All units are as we need
		break;
	case  RESUNIT_CENTIMETER:
		xDpi *= 2.54f;
		yDpi *= 2.54f;
		break;

	default:
		break;
	}


	// find out the number of directories in this file. Each dir represents a zoom level
	dirCount = 1;
	while (TIFFReadDirectory(tPtr))	dirCount++;
	// Reset dir index to the first one
	TIFFSetDirectory(tPtr, 0);
	
	//Allocate memory for storing information about these directories
	dirInfo = (struct tiffDirInfo *)malloc (dirCount * sizeof(struct tiffDirInfo));
	memset((void *)dirInfo, 0, dirCount * sizeof(struct tiffDirInfo));

	// Load up information about the directories
	for(i = 0; i < dirCount; i++)
	{
		TIFFSetDirectory(tPtr, i);

		// populate the dir info structure
		dirInfo[i].level = i;
		TIFFGetField(tPtr, TIFFTAG_IMAGEWIDTH, &dirInfo[i].imgW);
		TIFFGetField(tPtr, TIFFTAG_IMAGELENGTH, &dirInfo[i].imgH);
		TIFFGetField(tPtr, TIFFTAG_TILEWIDTH, &dirInfo[i].tileW);
		TIFFGetField(tPtr, TIFFTAG_TILELENGTH, &dirInfo[i].tileH);
		TIFFGetField(tPtr, TIFFTAG_BITSPERSAMPLE, &dirInfo[i].Bps);	dirInfo[i].Bps /= 8;
		TIFFGetField(tPtr, TIFFTAG_SAMPLESPERPIXEL, &dirInfo[i].spp);

		TIFFGetField(tPtr, TIFFTAG_COMPRESSION, &dirInfo[i].compressionScheme);

		TIFFGetField(tPtr, TIFFTAG_XRESOLUTION, &dirInfo[i].xDpi);
		TIFFGetField(tPtr, TIFFTAG_YRESOLUTION, &dirInfo[i].yDpi);

		switch(resolutionUnits)
		{
		case  RESUNIT_NONE:
			dPrintf("\ntiffLoader::init: No resolution units in TIFF file. Expect failure !!");
			break;
		case  RESUNIT_INCH: // do nothing here. All units are as we need
			break;
		case  RESUNIT_CENTIMETER:
			dirInfo[i].xDpi *= 2.54f;
			dirInfo[i].yDpi *= 2.54f;
			break;

		default:
			break;
		}

		// Print information about the TIFF
		dPrintf("\n\n\n DIR # %d \n==============================", dirInfo[i].level);
		dPrintf("\nImage W x H = %d x %d", dirInfo[i].imgW, dirInfo[i].imgH);
		dPrintf("\nTile W x H = %d x %d", dirInfo[i].tileW, dirInfo[i].tileH);
		dPrintf("\nBytes / sample = %d ", dirInfo[i].Bps);
		dPrintf("\nSamples / pixel = %d ", dirInfo[i].spp);
		dPrintf("\nxDpi = %f  \tyDpi = %f ", dirInfo[i].xDpi, dirInfo[i].yDpi);
		dPrintf("\nCompression scheme = %d ", dirInfo[i].compressionScheme);

	}

	// Reset dir index to the first one
	TIFFSetDirectory(tPtr, 0);

	// allocate some buffers that we will need
	tempTileBuffer = malloc ( tileW * tileH * 4);

	// if everything went ok, return
	return TL_OK;
	
}

//
int tiffLoader::extractRGBAImage(double _bl_x, double _bl_y, double _ur_x, double _ur_y, void *_oBuf, unsigned _bufSize,
					 unsigned &_W, unsigned &_H)
{
	
	int x1, y1, x2, y2, i, j, m, n;
	double estImgW;
	unsigned int bestLevel = 0;
	int tileIndexX, tileIndexY, tileRowLen, tileColLen;
	int tileXOff, tileYOff, tileXLen, tileYLen, bufXOff, bufYOff, bufXLen, bufYLen;

	//do bound checks
	if(_bl_x > _ur_x) return TL_INVALID_VIEWPORT;
	if(_bl_y > _ur_y) return TL_INVALID_VIEWPORT;
	
	if(_bl_x < 0.0f || _bl_x > 1.0f || _bl_y < 0.0f || _bl_y > 1.0f 
		|| _ur_x < 0.0f || _ur_x > 1.0f || _ur_y < 0.0f || _ur_x > 1.0f)
	return TL_INVALID_VIEWPORT;

	if(_W == 0 || _H == 0) 
	{
		dPrintf("\ntiffLoader::extractRGBAImage(): Please specify correct width and height parameters");
		return TL_INVALID_VIEWPORT;
	}

	// calculate the approx width of the entire image
	estImgW = (double)_W / (_ur_x - _bl_x);

	// Now figure out which level best fits this width. We are looking for the level that is just higher
	// in pixel data that the requested viewport
	for(i = dirCount - 1; i >= 0; i--)
	{
		bestLevel = i;
		if(estImgW < dirInfo[i].imgW) break;
	} // 

	// calculate the pixel equivalents for the viewports. (x1, y1) is the BL corner and (x2, y2) is UR.
	// However since for later calculations since we need all points to be calculated wrt top left
	// (pixel calculations), we shift the origin. x1y1 and x2y2 however still represent the BL and UR 
	// corners. LibTiff also works with Top Left corner as origin
	x1 = (unsigned)(_bl_x * dirInfo[bestLevel].imgW);
	y1 = (unsigned)((1.0f - _bl_y) * dirInfo[bestLevel].imgH);
	x2 = (unsigned)(_ur_x * dirInfo[bestLevel].imgW);
	y2 = (unsigned)((1.0f - _ur_y) * dirInfo[bestLevel].imgH);

	// set the tiff library to read the level we 've picked
	TIFFSetDirectory(tPtr, bestLevel);
	
	// calculate the dimensions of the image
	bufXLen = x2 - x1;
	bufYLen = y1 - y2;

	// check to make sure the image will fit the supplied buffer
	if((unsigned)(bufXLen * bufYLen * 4) > _bufSize)
	{
		dPrintf("\ntiffLoader::extractRGBAImage(): Supplied buffer too small");
		return TL_INSUFF_BUFFER;
	}

	// calculate the index of the first tile (top left) to extract
	tileIndexX = x1 / tileW;
	tileIndexY = y2 / tileH;

	// calulate the num of rows and columns of tiles we need to extract
	tileRowLen = (unsigned)(ceil((double)x2 / (double)tileW) - floor((double)x1 / (double)tileW));
	tileColLen = (unsigned)(ceil((double)y1 / (double)tileH) - floor((double)y2 / (double)tileH));

	// for every tile we extract, copy the correct portions into the output buffer
	bufXOff = bufYOff = 0;
	for (i = 0; i < tileColLen; i++)
	{
		bufXOff = 0;
		tileIndexY = (y2 / tileH) + i;

		for (j = 0; j < tileRowLen; j++)
		{
			tileIndexX = (x1 / tileW) + j; 
			
			// extract the tile to a temp buffer
			if( TIFFReadRGBATile(tPtr, (tileIndexX * tileW), (tileIndexY * tileH), (uint32 *)tempTileBuffer) == 0)
			{
				dPrintf("\ntiffLoader::extractRGBAImage(): Error reading tile");
				return TL_TIFF_READ_ERR;
			}

			// copy the correct sub-section of the tile to the correct section of output buffer
			// All the Offsets are from the top left corner
			tileXOff = (x1 - tileIndexX * tileW);
			if(tileXOff < 0) tileXOff = 0;

			if(((tileIndexX + 1) * (int)tileW - x2) < 0) tileXLen = tileW - tileXOff;
			else
				tileXLen = x2 - tileIndexX * tileW;

			tileYOff = (y2 - tileIndexY * tileH);
			if(tileYOff < 0) tileYOff = 0;

			if(((tileIndexY + 1) * (int)tileH - y1) < 0) tileYLen = tileH - tileYOff;
			else
				tileYLen = y1 - tileIndexY * tileH;

			// copy the pixels .. row wise from the tile to the output buffer.
			// IMP ** The tiles seem to be stored bottom to top so we flip the order when copying the rows
			// of pixels.
			n = 0;

			for(m = (int)tileH - (int)tileYOff - 1; m >= (int)tileH - ((int)tileYOff + (int)tileYLen); m--)
			{
				memcpy((void *)((unsigned *)_oBuf + ((bufYOff + n) * bufXLen) + bufXOff),
					(void *)((unsigned *)tempTileBuffer + (m * tileW) + tileXOff),
					tileXLen * 4);
				n++;
			}

			// adjust the X offset in the output frame buffer	
			bufXOff += tileXLen;
	
		}
		// adjust the Y offset in the output frame buffer
		bufYOff += tileYLen;
	} // for(i

	_W = bufXLen;
	_H = bufYLen;
	
	// if everything went ok, return
	return TL_OK;
	
}



extern "C"
{
TV_EXPORT_DIRECTIVES void getObjHandle(void *&objHandle)
{
	objHandle = (void *)(new tiffLoader());
}

TV_EXPORT_DIRECTIVES void delObj(void *&objHandle)
{
	tiffLoader *obj = (tiffLoader *)objHandle;
	delete obj;
}

}
