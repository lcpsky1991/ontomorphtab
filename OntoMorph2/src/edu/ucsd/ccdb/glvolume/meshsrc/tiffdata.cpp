#include "tiffdata.h"

#include <stdio.h>
#include <string.h>
#include <math.h>

#include "vvdebugmsg.h"
#include "vvtoolshed.h"

using namespace std;
using namespace MipMapVideoLib;

TIFFData::TIFFData(char *filename, int brickSize) : VolumeData()
{
  assert(filename != NULL);

  strncpy(_fileName, filename, sizeof(_fileName));
  //cerr << "!!!!!!!!!!!!!!!!!!!!!!!!!!!" << _fileName << endl;

  if((_fpVol = loadHeaders(filename)) == NULL) 
  {
	vvDebugMsg::msg(1, "Error: cannot open file: ", filename);
	return;
  }

  _whereData = ftell(_fpVol);
  _dimension = 2;
  vd->vox[2] = 1;
  setBrickSize(brickSize);
}

TIFFData::~TIFFData()
{
}

FILE* TIFFData::loadHeaders(char* fileName)
{
  const ushort BIG_ENDIAN_ID  = 0x4d4d;           // TIF endianness for big-endian (Unix) style
  const ushort LITTLE_ENDIAN_ID = 0x4949;         // TIF endianness for little-endian (Intel) style
  const ushort MAGICNUMBER = 42;                  // TIF magic number

  vvToolshed::EndianType endian;				  // file endianness
  FILE* fp;										  // volume file pointer
  ushort endianID, magicID;                       // file format test values
  ulong ifdpos;                                   // position of first IFD
  int numEntries;                                 // number of entries in IFD
  uint32_t i;                                     // counter
  ushort tag;                                     // IFD-tag
  ushort dataType;                                // IFD data type: 1=8bit uint, 2=8bit ASCII, 3=16bit uint, 4=32bit uint, 5=64bit fixed point
  uint32_t    numValues;                          // IFD: number of data values
  uint32_t    value;                              // IFD data value or offset
  int    nextIFD;                                 // pointer to next IFD

  ushort tileWidth=0;                             // tile width in voxels
  ushort tileHeight=0;                            // tile height in voxels
  ulong  tileOffset=0;                            // tile offset in file
  int    numTiles=0;                              // total number of tiles in file

  int*   stripOffsets=NULL;                       // array of strip offsets
  int*   stripByteCounts=NULL;                    // bytes per strip
  int    rowsPerStrip=0;                          // rows per strip
  int planarConfiguration = 1;                    // 1=RGBRGB, 2=RRGGBB
  int where;                                      // current position in file
  int strips=1;                                   // number of strips
  int ifd;                                        // current IFD ID

  vd->setFilename(fileName);
  //cerr << "TIFFData::loadHeaders: vd->getFilename()=" << vd->getFilename() << endl;
  //cerr << "TIFFData::loadHeaders: fileName=" << fileName << endl;

  if ( (fp = fopen(fileName, "rb")) == NULL) {
	vvDebugMsg::msg( 1, "Error: Cannot open file.");
	return false;
  }

  // Check file format:
  endianID = vvToolshed::read16(fp);
  if      (endianID == BIG_ENDIAN_ID)    endian = vvToolshed::VV_BIG_END;
  else if (endianID == LITTLE_ENDIAN_ID) endian = vvToolshed::VV_LITTLE_END;
  else {
	fclose(fp);
	vvDebugMsg::msg( 1, "TIFF: wrong header ID");
	return false;
  }

  // magic ID
  magicID = vvToolshed::read16(fp, endian);
  if (magicID != MAGICNUMBER){
	fclose(fp);
	vvDebugMsg::msg( 1,  "TIFF: wrong magic number");
	return false;
  }

  // Find and process first IFD:
  ifdpos = vvToolshed::read32(fp, endian);
  fseek(fp, ifdpos, SEEK_SET);

  numEntries = vvToolshed::read16(fp, endian);	

  // for each IFD entry
  for (ifd=0; ifd<numEntries; ++ifd) 
  {		
	// process all IFD entries
	tag       = vvToolshed::read16(fp, endian);
	dataType  = vvToolshed::read16(fp, endian);
	numValues = vvToolshed::read32(fp, endian);
	value     = vvToolshed::read32(fp, endian);

	//vvDebugMsg::msg( 3, "tag, dataType: ", tag, dataType );
	//vvDebugMsg::msg( 3, "numValues, value: ", (int)numValues, (int)value );

	// 16 bit values are left aligned
	if (endian==vvToolshed::VV_BIG_END && dataType==3 && tag!=0x102) 
	  value = value >> 16;

	switch (tag)
	{
	  case 0x0FE: 
		// NewSubfileType
		break;			

	  case 0x100: 
		// ImageWidth
		vd->vox[0] = value; 
		break; 	

	  case 0x101: 
		// ImageLength
		vd->vox[1] = value; 
		break;	
						  
	  case 0x102:  
		// BitsPerSample (=bits per channel)
		if (numValues==1) 
		  vd->bpc = value / 8;
		else 
		{
		  where = ftell(fp);
		  fseek(fp, value, SEEK_SET);
		  int bitsPerSample = 0;

		  for (i=0; i<numValues; ++i)
		  {
			if (dataType == 4) 
			  bitsPerSample = vvToolshed::read32(fp, endian);
			else if (dataType == 3) 
			  bitsPerSample = vvToolshed::read16(fp, endian);
			else 
			  vvDebugMsg::msg( 1, "File format unsupported!");

			if (i == 0) 
			  vd->bpc = bitsPerSample / 8;
			else if (vd->bpc != bitsPerSample / 8) 
			{
			  vvDebugMsg::msg( 1, "Error: TIFF reader needs same number of bits for each sample.");
			  fclose(fp);
			  delete[] stripOffsets;
			  delete[] stripByteCounts;
			  return false;
			}
		  }
		  fseek(fp, where, SEEK_SET);
		}
		break;

	  case 0x103: 
		// Compression; must be uncompressed
		if (value != 1) 
		{
		  fclose(fp);
		  vvDebugMsg::msg( 1, "Cannot read compressed TIFF.\n");
		}
		break;				

	  case 0x106: 
		// PhotometricInterpretation; ignore
		break;			

	  case 0x111: 
		// StripOffsets
		delete[] stripOffsets;	
		stripOffsets = new int[numValues];

		if (numValues == 1) 
		  stripOffsets[0] = value;
		else 
		{
		  where = ftell(fp);
		  fseek(fp, value, SEEK_SET);
		  for (i=0; i<numValues; ++i) 
		  {
			if (dataType==4) 
			  stripOffsets[i] = vvToolshed::read32(fp, endian);
			else if (dataType==3) 
			  stripOffsets[i] = vvToolshed::read16(fp, endian);
			else 
			  vvDebugMsg::msg( 1, "datatype unsupported!");
		  }
		  fseek(fp, where, SEEK_SET);
		}
		break;

	  case 0x115: 
		// SamplesPerPixel (=channels)
		vd->chan = value;               
		break;

	  case 0x116: 
		// RowsPerStrip
		rowsPerStrip = value; 
		break;    

	  case 0x117: 
		// StripByteCounts
		delete[] stripByteCounts;       
		stripByteCounts = new int[numValues];

		if (numValues == 1) 
		  stripByteCounts[0] = value;
		else 
		{
		  where = ftell(fp);
		  fseek(fp, value, SEEK_SET);

		  for (i=0; i<numValues; ++i) 
		  {
			  if (dataType==4) 
				stripByteCounts[i] = vvToolshed::read32(fp, endian);
			  else if (dataType==3) 
				stripByteCounts[i] = vvToolshed::read16(fp, endian);
			  else 
				vvDebugMsg::msg( 1, "datatype unsupported!");
		  }
		  fseek(fp, where, SEEK_SET);
		}
		break;
	                                        
	  case 0x11c: 
		// PlanarConfiguration
		planarConfiguration = value; 
		break;

	  // custom tags////
	  case 0x142: 
		tileWidth  = (ushort)value; 
		break;

	  case 0x143: 
		tileHeight = (ushort)value; 
		break;

	  case 0x144: 
		numTiles = numValues; 
		tileOffset = value; 
		break;

	  case VOL_ORG_TAG:
		setVolumeFormat((VolFormat)value);
		//vvDebugMsg::msg(1, "Image organization: ", (int)getVolumeFormat() );
		break;

	  default: 
		break;
	}
  }

  strips = int(ceilf(float(vd->vox[1]) / float(rowsPerStrip)));

  nextIFD = vvToolshed::read32(fp, endian);       // check for further IFDs

  if (nextIFD!=0) 
	  vvDebugMsg::msg( 3, "Warning: Multipage file is not supported. Only the first one is read." );

  // load 2D TIFF
  if (stripOffsets[0]>0) 
  {				
	  fseek(fp, stripOffsets[0], SEEK_SET);
	  //vvDebugMsg::msg(1, "****************** stripOffsets[0]: ", stripOffsets[0]);
	  //setBrickSize(getBrickSize());
	  return fp;
  }

  return NULL;
}


	
bool TIFFData::getOneChannelData_sep(int x, int y, int z, int c, uchar *raw)
{
  //vvDebugMsg::msg( 2, "TIFFData::getOneChannelData_sep()==");
  //vvDebugMsg::msg(2, "TIFFData::getOneChannelData_sep: vd->getFilename: ", vd->getFilename());

  //fclose(_fpVol);
  //_fpVol = fopen(_fileName, "rb");
  assert(_fpVol != NULL);
  //vvDebugMsg::msg(2, "_fileName: ", _fileName);

  (void)z; // ignore z coordinate

  if( _fpVol == NULL) 
  {
	vvDebugMsg::msg(1, "Error: file not opened: ", _fileName);
	return false;
	/*
	vvDebugMsg::msg(1, "Error: file not opened: ", vd->getFilename());

	_fpVol = fopen(vd->getFilename(), "rb");
	if(_fpVol == NULL)
	{
	  vvDebugMsg::msg(1, "Yes, it's still NULL");
	  return false;
	}
	vvDebugMsg::msg(1, "so I reopened");
	*/
  }

  if( c>vd->chan ) 
  {
	vvDebugMsg::msg(1, "Error: this channel does not exist. # channels: ", c, vd->chan);
	return false;
  }

  if( x >= _nBricks[0] || x < 0 || y >= _nBricks[1] || y < 0 ) 
  {
	vvDebugMsg::msg( 1, "Error: Asking for a brick that is out of boundaries\n" );
	return false;
  }

  //printf("get a brick from a new file == row: %d col: %d\n", x, y);

  long nReadPixelsX = (x == (_nBricks[0]-1) && _remainingPixels[0] != 0 )? _remainingPixels[0]: _brickSize;
  long nReadPixelsY = (y == (_nBricks[1]-1) && _remainingPixels[1] != 0 )? _remainingPixels[1]: _brickSize;

  // added by Han
  nReadPixelsX = _brickSize;
  nReadPixelsY = _brickSize;

  long chunkStartPt = _brickSize*vd->chan*vd->bpc*( nReadPixelsY*x + vd->vox[0]*y );

  long nReadBytes = nReadPixelsX * nReadPixelsY * vd->bpc;

  fseek( _fpVol, _whereData + chunkStartPt + c * nReadBytes, SEEK_SET );
  //vvDebugMsg::msg(2, "Offset from the start position of data: ", (int)chunkStartPt);
  //vvDebugMsg::msg(2, "# read bytes: ", (int)nReadBytes );
  //cerr << "TIFFData::filename: " << vd->getFilename() << endl;
  //cerr << "TIFFData::_fileName: " << _fileName << endl;

  if( fread( raw, nReadBytes, 1, _fpVol ) != 1) 
  {
	vvDebugMsg::msg( 1,"Error read chunked images!\n");
	return false;
  }

  return true;
}
	
bool TIFFData::getOneChannelData_mix(int x, int y, int z, int c, uchar *raw)
{
  vvDebugMsg::msg( 2, "==getOneChannelData_mix()==");

  (void)z; // ignore z coordinate

  if( _fpVol == NULL) 
  {
	vvDebugMsg::msg( 1, "Error: file not opened.");
	return false;
  }

  if( c>vd->chan ) 
  {
	vvDebugMsg::msg( 1, "Error: no channel %d. # channels: %d", c, vd->chan);
	return false;
  }

  if(x >= _nBricks[0] || x < 0 || y >= _nBricks[1] || y < 0) 
  {
	vvDebugMsg::msg( 1, "Error: Asking for a brick that is out of boundaries" );
	return false;
  }

  //printf("get a brick from a new file == row: %d col: %d\n", x, y);

  long nReadPixelsX = ( x == (_nBricks[0]-1) && _remainingPixels[0] != 0 )? _remainingPixels[0]: _brickSize;
  long nReadPixelsY = ( y == (_nBricks[1]-1) && _remainingPixels[1] != 0 )? _remainingPixels[1]: _brickSize;

  // added by Han
  nReadPixelsX = _brickSize;
  nReadPixelsY = _brickSize;

  long chunkStartPt = _brickSize*vd->chan*vd->bpc*( nReadPixelsY*x + vd->vox[0]*y );

  long nReadBytes = nReadPixelsX * nReadPixelsY * vd->bpc;
  long nReadBytes_nC = nReadBytes*vd->chan;
  unsigned char *bufAllChannel = new unsigned char [nReadBytes_nC];

  //memset( buf, 0, nReadBytes );
  //memset( bufAllChannel, 0, nReadBytes_nC);

  fseek( _fpVol, _whereData+chunkStartPt, SEEK_SET );
  //vvDebugMsg::msg( 2, "Offset from the start position of data: ", (int)chunkStartPt);
  //vvDebugMsg::msg( 2, "# read bytes: ", (int)nReadBytes_nC );

  if( fread( bufAllChannel, nReadBytes_nC, 1, _fpVol ) != 1) {
	  vvDebugMsg::msg( 1,"Error read chunked images!\n");
	  return false;
  }

  // reorganize the data
  int inc = vd->bpc*vd->chan;

  for(int i=c*vd->bpc, j=0; i<nReadBytes_nC; i+=inc, j+=vd->bpc)
	for(int p=0; p<vd->bpc; p++)
	  raw[j+p] = bufAllChannel[i+p];

  delete [] bufAllChannel;

  return true;
}


	
bool TIFFData::getMultiChannelData_sep(int x, int y, int z, uchar *raw)
{
  //vvDebugMsg::msg( 2, "==getMultiChannelData_sep()==");

  (void)z; // ignore z coordinate

  if( _fpVol == NULL) {
	vvDebugMsg::msg( 1, "Error: file not opened.\n");
	return false;
  }
  if( x >= _nBricks[0] || x < 0 || y >= _nBricks[1] || y < 0 ) {
	vvDebugMsg::msg( 1, "Error: Asking for a brick that is out of boundaries\n" );
	return false;
  }
  
  //printf("get a brick from a new file == row: %d col: %d\n", x, y);

  long nReadPixelsX = ( x == (_nBricks[0]-1) && _remainingPixels[0] != 0 )? _remainingPixels[0]: _brickSize;
  long nReadPixelsY = ( y == (_nBricks[1]-1) && _remainingPixels[1] != 0 )? _remainingPixels[1]: _brickSize;

  // added by Han
  nReadPixelsX = _brickSize;
  nReadPixelsY = _brickSize;

  long chunkStartPt = _brickSize*vd->chan*vd->bpc*( nReadPixelsY*x + vd->vox[0]*y );

  long nReadBytes = nReadPixelsX * nReadPixelsY * vd->bpc;
  long nReadBytes_nC = nReadBytes * vd->chan;
  unsigned char *bufAllChannel = new unsigned char [nReadBytes_nC];

  //memset( buf, 0, nReadBytes_nC );
  //memset( bufAllChannel, 0, nReadBytes_nC);

  fseek( _fpVol, _whereData+chunkStartPt, SEEK_SET );
  //vvDebugMsg::msg( 2, "Offset from the start position of data: ", (int)chunkStartPt);
  //vvDebugMsg::msg( 2, "# read bytes: ", (int)nReadBytes_nC );

  if( fread( bufAllChannel, nReadBytes_nC, 1, _fpVol ) != 1) {
	vvDebugMsg::msg( 1, "Error read chunked images!");
	return false;
  }

  // reorganize the data
  long pixelBytes = vd->chan*vd->bpc;
  for(int c=0; c<vd->chan; c++)
	for(int j=c*vd->bpc, i=c*(int)nReadBytes; j<nReadBytes_nC; j+=pixelBytes, i+=vd->bpc)
	  for(int p=0; p<vd->bpc; p++)
		raw[j+p] = bufAllChannel[i+p];

  delete [] bufAllChannel;

  return true;
}
	
bool TIFFData::getMultiChannelData_mix(int x, int y, int z, uchar *raw)
{
  //vvDebugMsg::msg( 2, "==getMultiChannelData_mix()==");

  (void)z; // ignore z coordinate

  if( _fpVol == NULL) 
  {
	vvDebugMsg::msg( 1, "Error: file not opened.\n");
	return false;
  }

  if( x >= _nBricks[0] || x < 0 || y >= _nBricks[1] || y < 0 ) 
  {
	vvDebugMsg::msg( 1, "Error: Asking for a brick that is out of boundaries\n" );
	return false;
  }
  
  //printf("get a brick from a new file == row: %d col: %d\n", x, y);

  long nReadPixelsX = ( x == (_nBricks[0]-1) && _remainingPixels[0] != 0 )? _remainingPixels[0]: _brickSize;
  long nReadPixelsY = ( y == (_nBricks[1]-1) && _remainingPixels[1] != 0 )? _remainingPixels[1]: _brickSize;

  // added by Han
  nReadPixelsX = _brickSize;
  nReadPixelsY = _brickSize;

  //vvDebugMsg::msg(2, "nReadPixelsX, nReadPixelsY: ", (int)nReadPixelsX, (int)nReadPixelsY);

  //long chunkStartPt = _brickSize*vd->chan*vd->bpc*( nReadPixelsY*x + vd->vox[0]*y );

  long chunkStartPt;
  
  //vvDebugMsg::msg(2, "vd->vox[0]: ", vd->vox[0]);
  if(x == _nBricks[0] - 1)
  {
	if(y == _nBricks[1] - 1) // upper right corner
	  chunkStartPt = _brickSize * vd->chan * vd->bpc * (nReadPixelsY * x + vd->vox[0] * y);
	else // right side
	  chunkStartPt = _brickSize * vd->chan * vd->bpc * (_brickSize * x + vd->vox[0] * y);
  }else{
	if(y == _nBricks[1] - 1) // upper side
	  chunkStartPt = _brickSize * vd->chan * vd->bpc * (nReadPixelsY * x + vd->vox[0] * y);
	else // in the middle
	  chunkStartPt = _brickSize * vd->chan * vd->bpc * (_brickSize * x + vd->vox[0] * y);
  }

  long nReadBytes = nReadPixelsX * nReadPixelsY * vd->bpc * vd->chan;

  //memset( buf, 0, nReadBytes );

  fseek( _fpVol, _whereData+chunkStartPt, SEEK_SET );
  //vvDebugMsg::msg( 2, "Base Offset: ", (int)_whereData);
  //vvDebugMsg::msg( 2, "Offset from the start position of data: ", (int)chunkStartPt);
  //vvDebugMsg::msg( 2, "# read bytes: ", (int)nReadBytes );

  if( fread( raw, nReadBytes, 1, _fpVol ) != 1) 
  {
	  vvDebugMsg::msg( 1, "Error read chunked images!");
	  return false;
  }

  return true;
}


void TIFFData::setBrickSize(int bs) 
{
  //vvDebugMsg::msg(1, "setBrickSize(): bs=", bs);

  _brickSize = bs;

  if(vd != NULL)
  {
	_nBricks[0] = vd->vox[0]/_brickSize;
	_nBricks[1] = vd->vox[1]/_brickSize;
	_nBricks[2] = 1;

	_remainingPixels[0] = vd->vox[0] % _brickSize;
	_remainingPixels[1] = vd->vox[1] % _brickSize;
	_remainingPixels[2] = 0;

	if( _remainingPixels[0] != 0 ) _nBricks[0]++;
	if( _remainingPixels[1] != 0 ) _nBricks[1]++;

	//vvDebugMsg::msg(2, "chuckSize, vd->vox[0], vd->vox[1]: ", (int)_brickSize, (int)vd->vox[0], (int)vd->vox[1]);
	//vvDebugMsg::msg(2, "_nBricks[0], _nBricks[1]: ",  (int)_nBricks[0], (int)_nBricks[1]);
	//vvDebugMsg::msg(2, "_remainingPixels: ", _remainingPixels[0], _remainingPixels[1]);

  } else {
	_nBricks[0] = _nBricks[1] = _nBricks[2] = 0;
	_remainingPixels[0] = _remainingPixels[1] = _remainingPixels[2] = 0;
  }
}


