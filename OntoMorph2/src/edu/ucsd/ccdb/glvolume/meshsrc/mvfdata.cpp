#include "mvfdata.h"

#include <stdio.h>
#include <string.h>

#include "vvdebugmsg.h"
#include "vvtoolshed.h"
#include "vvtokenizer.h"

using namespace std;
using namespace MipMapVideoLib;

MVFData::MVFData(char* filename, int brickSize) : VolumeData()
{
  assert(filename != NULL);

  if((_fpVol = loadHeaders(filename)) == NULL) {
	vvDebugMsg::msg(1, "Error: cannot open file: ", filename);
	return;
  }
	
  _whereData = ftell(_fpVol);
  _dimension = 3;
  setBrickSize(brickSize);
}

MVFData::~MVFData() 
{
}

FILE* MVFData::loadHeaders(char *filename) 
{
  const char xvfID[4] = "XVF";
  FILE* fp;                                       // volume file pointer
  vvTokenizer* tok;
  vvTokenizer::TokenType ttype;                   // currently processed token type
  int i;                                          // counters
  int frameSize;                                  // size of a frame in bytes
  bool done;
  int encodedSize;                                // size of encoded data array
  vvToolshed::EndianType endian;

  vvDebugMsg::msg(1, "vvFileIO::loadXVFFile()");

  vd->setFilename(filename);
  if (vd->getFilename()==NULL) return NULL;

  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL) 
  {
	vvDebugMsg::msg(1, "Error: Cannot open file.");
	return NULL;
  }

  // Read magic code:
  for (i=0; i<int(strlen(xvfID)); ++i)
  {
	if (fgetc(fp) != xvfID[i])
	{
	  fclose(fp);
	  // vvDebugMsg::msg(1, "Trying to load old style XVF format.");
	  // return loadXVFFileOld(vd);
	  vvDebugMsg::msg(1, "Old style XVF format unsupported.");
	  return NULL;
	}
  }
  fseek(fp, 0, SEEK_SET);

  vd->removeSequence();                           // delete previous volume sequence

  // Read header:
  tok = new vvTokenizer(fp);
  tok->setCommentCharacter('#');
  tok->setEOLisSignificant(false);
  tok->setCaseConversion(vvTokenizer::VV_UPPER);
  tok->setParseNumbers(true);
  tok->setWhitespaceCharacter(' ');

  done = false;
  while (!done)
  {
	// Read a token:
	ttype = tok->nextToken();
	if (ttype != vvTokenizer::VV_WORD)
	{
	  cerr << "Error in line " << tok->getLineNumber() << " of XVF file" << endl;
	  tok->nextLine();
	}
	else
	{
	  if (strcmp(tok->sval, "XVF")==0)
	  {
		tok->nextLine();
	  }
	  else if (strcmp(tok->sval, "VERSION")==0)
	  {
		ttype = tok->nextToken();
		assert(ttype == vvTokenizer::VV_NUMBER);
		cerr << "Reading XVF file version " << tok->nval << endl;
	  }
	  else if (strcmp(tok->sval, "VOXELS")==0)
	  {
		for (i=0; i<3; ++i)
		{
		  ttype = tok->nextToken();
		  assert(ttype == vvTokenizer::VV_NUMBER);
		  vd->vox[i] = int(tok->nval);
		}
	  }
	  else if (strcmp(tok->sval, "TIMESTEPS")==0)
	  {
		ttype = tok->nextToken();
		assert(ttype == vvTokenizer::VV_NUMBER);
		vd->frames = int(tok->nval);
	  }
	  else if (strcmp(tok->sval, "BPC")==0)
	  {
		ttype = tok->nextToken();
		assert(ttype == vvTokenizer::VV_NUMBER);
		vd->bpc = int(tok->nval);
	  }
	  else if (strcmp(tok->sval, "CHANNELS")==0)
	  {
		ttype = tok->nextToken();
		assert(ttype == vvTokenizer::VV_NUMBER);
		vd->chan = int(tok->nval);
	  }
	  else if (strcmp(tok->sval, "DIST")==0)
	  {
		for (i=0; i<3; ++i)
		{
		  ttype = tok->nextToken();
		  assert(ttype == vvTokenizer::VV_NUMBER);
		  vd->dist[i] = tok->nval;
		}
	  }
	  else if (strcmp(tok->sval, "ENDIAN")==0)
	  {
		ttype = tok->nextToken();
		assert(ttype == vvTokenizer::VV_WORD);
		if (strcmp(tok->sval, "LITTLE")==0) endian = vvToolshed::VV_LITTLE_END;
		else endian = vvToolshed::VV_BIG_END;
	  }
	  else if (strcmp(tok->sval, "DTIME")==0)
	  {
		ttype = tok->nextToken();
		assert(ttype == vvTokenizer::VV_NUMBER);
		vd->dt = tok->nval;
	  }
	  else if (strcmp(tok->sval, "MINMAX")==0)
	  {
		for (i=0; i<2; ++i)
		{
		  ttype = tok->nextToken();
		  assert(ttype == vvTokenizer::VV_NUMBER);
		  vd->real[i] = tok->nval;
		}
	  }
	  else if (strcmp(tok->sval, "POS")==0)
	  {
		for (i=0; i<3; ++i)
		{
		  ttype = tok->nextToken();
		  assert(ttype == vvTokenizer::VV_NUMBER);
		  vd->pos[i] = tok->nval;
		}
	  }
	  else if (strcmp(tok->sval, "CHANNELNAMES")==0)
	  {
		if (vd->chan<1) tok->nextLine();
		else
		{
		  for (i=0; i<vd->chan; ++i)
		  {
			ttype = tok->nextToken();
			assert(ttype == vvTokenizer::VV_WORD);
			vd->setChannelName(i, tok->sval);
		  }
		}
	  }
	  else if (strcmp(tok->sval, "TF_PYRAMID")==0)
	  {
		fseek(fp, tok->getFilePos(), SEEK_SET);
		vd->tf._widgets.append(new vvTFPyramid(fp), vvSLNode<vvTFWidget*>::NORMAL_DELETE);
		tok->setFilePos(fp);
	  }
	  else if (strcmp(tok->sval, "TF_BELL")==0)
	  {
		fseek(fp, tok->getFilePos(), SEEK_SET);
		vd->tf._widgets.append(new vvTFBell(fp), vvSLNode<vvTFWidget*>::NORMAL_DELETE);
		tok->setFilePos(fp);
	  }
	  else if (strcmp(tok->sval, "TF_COLOR")==0)
	  {
		fseek(fp, tok->getFilePos(), SEEK_SET);
		vd->tf._widgets.append(new vvTFColor(fp), vvSLNode<vvTFWidget*>::NORMAL_DELETE);
		tok->setFilePos(fp);
	  }
	  else if (strcmp(tok->sval, "TF_CUSTOM")==0)
	  {
		fseek(fp, tok->getFilePos(), SEEK_SET);
		vd->tf._widgets.append(new vvTFCustom(fp), vvSLNode<vvTFWidget*>::NORMAL_DELETE);
		tok->setFilePos(fp);
	  }
	  else if (strcmp(tok->sval, "ICON")==0)
	  {
		ttype = tok->nextToken();
		assert(ttype == vvTokenizer::VV_NUMBER);
		vd->iconSize = int(tok->nval);
		tok->nextLine();

		// Read icon:
		if (vd->iconSize>0)
		{
		  delete[] vd->iconData;
		  int iconBytes = vd->iconSize * vd->iconSize * vvVolDesc::ICON_BPP;
		  vd->iconData = new uchar[iconBytes];
		  fseek(fp, tok->getFilePos(), SEEK_SET);
		  int encodedSize = vvToolshed::read32(fp);
		  if (encodedSize>0)                      // compressed icon?
		  {
			uchar* encoded = new uchar[encodedSize];
			if ((int)fread(encoded, 1, encodedSize, fp) != encodedSize)
			{
			  cerr << "Error: Insuffient compressed icon data in file." << endl;
			  fclose(fp);
			  delete[] vd->iconData;
			  vd->iconData = NULL;
			  delete[] encoded;
			  return NULL;
			}
			if (vvToolshed::decodeRLE(vd->iconData, encoded, encodedSize, vvVolDesc::ICON_BPP, iconBytes) < 0)
			{
			  cerr << "Error: Decoding exceeds icon size." << endl;
			  fclose(fp);
			  delete[] vd->iconData;
			  vd->iconData = NULL;
			  delete[] encoded;
			  return NULL;
			}
			delete[] encoded;
		  }
		  else                                    // uncompressed icon
		  {
			if ((int)fread(vd->iconData, 1, iconBytes, fp) != iconBytes)
			{
			  cerr << "Error: Insuffient uncompressed icon data in file." << endl;
			  fclose(fp);
			  delete[] vd->iconData;
			  vd->iconData = NULL;
			  return NULL;
			}
		  }
		  tok->setFilePos(fp);
		}
	  }
	  else if (strcmp(tok->sval, "VOL_ORG")==0)
	  {
		tok->nextToken();
		setVolumeFormat((VolFormat)((int)tok->nval));
	  }
	  else if (strcmp(tok->sval, "VOXELDATA")==0)
	  {
		tok->nextLine();
		done = true;
	  }
	  else
	  {
		if (ttype == vvTokenizer::VV_WORD)
		{
			cerr << "Ignoring unknown header entry: " << tok->sval << endl;
		}
		tok->nextLine();
	  }
	}
  }

  frameSize = vd->getFrameBytes();

  fseek(fp, tok->getFilePos(), SEEK_SET);
  encodedSize = vvToolshed::read32(fp);

  if(encodedSize>0) 
  {
	vvDebugMsg::msg(1, "Encoded volume file unsupported!");
	fclose(fp);
	return NULL;
  }

  //setBrickSize(_brickSize);

  _whereData = ftell(fp);

  //_fpVol = fp;

  return fp;
}




// get one channel data from a file with RGB saved separately (sep)
bool MVFData::getOneChannelData_sep(int x, int y, int z, int c, uchar *raw) 
{
  vvDebugMsg::msg( 2, "==getOneChannelData_sep()==");

  if( _fpVol == NULL) 
  {
	vvDebugMsg::msg( 1, "Error: file not opened: ", vd->getFilename());
	return false;
  }

  if( c>vd->chan ) 
  {
	vvDebugMsg::msg( 1, "Error: this channel does not exist. # channels: ", c, vd->chan);
	return false;
  }

  if(x >= _nBricks[0] || y >= _nBricks[1] || z >= _nBricks[2] ||
	  x < 0 || y < 0 || z < 0) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getMultiChannelData() - out of range.");
	return false;
  }

  int rawSize[3] = { 
			(x == _nBricks[0]-1 && _remainingPixels[0] != 0)? _remainingPixels[0]: _brickSize,
			(y == _nBricks[1]-1 && _remainingPixels[1] != 0)? _remainingPixels[1]: _brickSize,
			(z == _nBricks[2]-1 && _remainingPixels[2] != 0)? _remainingPixels[2]: _brickSize 
  };
  // added by Han
  rawSize[0] = _brickSize;
  rawSize[1] = _brickSize;
  rawSize[2] = _brickSize;

  int startPtr = _whereData + vd->getBPV()*_brickSize*(x*vd->vox[1]*vd->vox[2] + rawSize[0]*y*vd->vox[2] + rawSize[0]*rawSize[1]*z);
  int readBytes = rawSize[0] * rawSize[1] * rawSize[2] * vd->bpc;

  fseek(_fpVol, startPtr+c*readBytes, SEEK_SET);
  if ((int)fread(raw, 1, readBytes, _fpVol) != readBytes) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getOneChannelData_sep() - read brick data.");
	vvDebugMsg::msg(2, "                                     - readBytes: ", readBytes);
	vvDebugMsg::msg(2, "                                     - startPtr: ", startPtr);
	return false;
  }
  vvDebugMsg::msg(1, "MVFData::getOneChannelData_sep(): ", readBytes, startPtr + c * readBytes);

  return true;

}

// not checked yet
// get one channel data from a file with RGB saved separately (sep)
bool MVFData::getOneChannelData_mix(int x, int y, int z, int c, uchar *raw) 
{
  vvDebugMsg::msg( 2, "==getOneChannelData_mix()==");

  if( _fpVol == NULL) 
  {
	vvDebugMsg::msg( 1, "Error: file not opened.\n");
	return false;
  }

  if( c>vd->chan ) 
  {
	vvDebugMsg::msg( 1, "Error: this channel does not exist. # channels: ", c, vd->chan);
	return false;
  }

  if(x >= _nBricks[0] || y >= _nBricks[1] || z >= _nBricks[2] ||
	  x < 0 || y < 0 || z < 0) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getOneChannelData_mix() - out of range.");
	return false;
  }

  int rawSize[3] = { 
			(x == _nBricks[0]-1 && _remainingPixels[0] != 0)? _remainingPixels[0]: _brickSize,
			(y == _nBricks[1]-1 && _remainingPixels[1] != 0)? _remainingPixels[1]: _brickSize,
			(z == _nBricks[2]-1 && _remainingPixels[2] != 0)? _remainingPixels[2]: _brickSize 
  };

  // added by Han
  rawSize[0] = _brickSize;
  rawSize[1] = _brickSize;
  rawSize[2] = _brickSize;

  int startPtr = _whereData + vd->getBPV()*_brickSize*(x*vd->vox[1]*vd->vox[2] + rawSize[0]*y*vd->vox[2] + rawSize[0]*rawSize[1]*z);
  int readBytes = rawSize[0]*rawSize[1]*rawSize[2]*vd->bpc;
  int readBytes_nC = readBytes*vd->chan;

  uchar *bufAllChannel = new uchar [readBytes_nC];
  //memset( raw, 0, readBytes );
  //memset( bufAllChannel, 0, readBytes_nC);

  fseek(_fpVol, startPtr, SEEK_SET);
  if ((int)fread(bufAllChannel, 1, readBytes_nC, _fpVol) != readBytes_nC) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getOneChannelData_mix() - read brick data.");
	vvDebugMsg::msg(2, "                                     - readBytes: ", readBytes);
	vvDebugMsg::msg(2, "                                     - startPtr: ", startPtr);
	return false;
  }

  // reorganizae the data
  for(int i=c*vd->bpc, j=0; i<readBytes_nC; i+=vd->getBPV(), j+=vd->bpc)
	for(int p=0; p<vd->bpc; p++)
	  raw[j+p] = bufAllChannel[i+p];

  delete [] bufAllChannel;
  return true;
}

// get a brick in our file format
bool MVFData::getMultiChannelData_mix(int x, int y, int z, uchar *raw) 
{
  if(x >= _nBricks[0] || y >= _nBricks[1] || z >= _nBricks[2] ||
	 x < 0 || y < 0 || z < 0) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getMultiChannelData() - out of range.");
	return false;
  }
  if(raw == NULL) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getMultiChannelData() - raw = NULL.");
	return false;
  }

  int rawSize[3] = { 
			(x == _nBricks[0]-1 && _remainingPixels[0] != 0)? _remainingPixels[0]: _brickSize,
			(y == _nBricks[1]-1 && _remainingPixels[1] != 0)? _remainingPixels[1]: _brickSize,
			(z == _nBricks[2]-1 && _remainingPixels[2] != 0)? _remainingPixels[2]: _brickSize 
  };

  // added by Han
  rawSize[0] = _brickSize;
  rawSize[1] = _brickSize;
  rawSize[2] = _brickSize;
  
  int startPtr = _whereData + vd->getBPV()*_brickSize*( x*vd->vox[1]*vd->vox[2] + rawSize[0]*y*vd->vox[2] + rawSize[0]*rawSize[1]*z);
  int readBytes = rawSize[0]*rawSize[1]*rawSize[2]*vd->getBPV();

  fseek(_fpVol, startPtr, SEEK_SET);
  if ((int)fread(raw, 1, readBytes, _fpVol) != readBytes) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getMultiChannelData_mix() - read brick data.");
	vvDebugMsg::msg(2, "                                     - readBytes: ", readBytes);
	vvDebugMsg::msg(2, "                                     - startPtr: ", startPtr);
	return false;
  }
  return true;
}
		
// not checked yet
// get a brick in our file format
bool MVFData::getMultiChannelData_sep(int x, int y, int z, uchar *raw) 
{
  if(x >= _nBricks[0] || y >= _nBricks[1] || z >= _nBricks[2] ||
	 x < 0 || y < 0 || z < 0) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getMultiChannelData_sep() - out of range.");
	return false;
  }
  if(raw == NULL) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getMultiChannelData_sep() - raw = NULL.");
	return false;
  }

  int rawSize[3] = { 
			(x == _nBricks[0]-1 && _remainingPixels[0] != 0)? _remainingPixels[0]: _brickSize,
			(y == _nBricks[1]-1 && _remainingPixels[1] != 0)? _remainingPixels[1]: _brickSize,
			(z == _nBricks[2]-1 && _remainingPixels[2] != 0)? _remainingPixels[2]: _brickSize 
  };

  // added by Han
  rawSize[0] = _brickSize;
  rawSize[1] = _brickSize;
  rawSize[2] = _brickSize;
  
  int startPtr = _whereData + vd->getBPV()*_brickSize*( x*vd->vox[1]*vd->vox[2] + rawSize[0]*y*vd->vox[2] + rawSize[0]*rawSize[1]*z);
  int readBytes = rawSize[0]*rawSize[1]*rawSize[2]*vd->bpc;
  int readBytes_nC = readBytes * vd->chan;
	  
  uchar *bufAllChannel = new uchar [readBytes_nC];
  //memset( raw, 0, readBytes_nC );
  //memset( bufAllChannel, 0, readBytes_nC);

  fseek(_fpVol, startPtr, SEEK_SET);
  if ((int)fread(bufAllChannel, 1, readBytes_nC, _fpVol) != readBytes_nC) 
  {
	vvDebugMsg::msg(1, "Error: MVFData::getMultiChannelData() - read brick data.");
	vvDebugMsg::msg(2, "                                     - readBytes: ", readBytes);
	vvDebugMsg::msg(2, "                                     - startPtr: ", startPtr);
	return false;
  }

  // reorganize the data
  for(int c=0; c<vd->chan; c++)
	for(int j=c*vd->bpc, i=c*readBytes; j<readBytes_nC; j+=vd->getBPV(), i+=vd->bpc)
	  for(int p=0; p<vd->bpc; p++)
		raw[j+p] = bufAllChannel[i+p];

  delete [] bufAllChannel;

  return true;
}


void MVFData::setBrickSize(int bs) 
{
	_brickSize = bs;

	if(vd != NULL) 
	{
		for(int i=0; i<3; i++) {
			_nBricks[i] = vd->vox[i]/_brickSize;
			_remainingPixels[i] = vd->vox[i] % _brickSize;
			if(_remainingPixels[i] != 0) _nBricks[i]++;
		}
	} 
	else 
	{
		_nBricks[0] =  _nBricks[1] =  _nBricks[2] = 0;
		_remainingPixels[0] = _remainingPixels[1] = _remainingPixels[2] = 0;
	}
}


