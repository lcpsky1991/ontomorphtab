
#include "brickreader.h"
#include "vvdebugmsg.h"
#include "tiffdata.h"
#include "mvfdata.h"

#include <vector>

using namespace std;
using namespace MipMapVideoLib;

BrickReader::~BrickReader()
{
  for(unsigned int i = 0; i <= _fileDescList.size(); i++)
    if(_mipmaps[i] != NULL)
      delete [] _mipmaps[i];
} 

int BrickReader::init(int dim, int maxMipLevel, int brickSize, vector<FileInfo>& fileDescList)
{
  vvDebugMsg::msg(2, "BrickReader::init()");

  assert(!fileDescList.empty());
  _maxMipLevel = maxMipLevel;

  _fileDescList = vector<FileInfo>(fileDescList);

  vvDebugMsg::msg(2, "origSizeX, origSizY, origSizeZ: ", _fileDescList[0].sizeX, _fileDescList[0].sizeY, _fileDescList[0].sizeZ);
  
  _mipmaps = new VolumeData * [maxMipLevel + 1];

  if(dim == 2)
  {
	for(int i = 0; i <= maxMipLevel; i++)
	{
	  vvDebugMsg::msg(2, "Loading file in BrickReader: ", _fileDescList[i].name);
	  _mipmaps[i] = new TIFFData(fileDescList[i].name, brickSize);
	}
  }
  else if(dim == 3)
  {
	for(int i = 0; i <= maxMipLevel; i++)
	{
	  vvDebugMsg::msg(2, "Loading file in BrickReader: ", _fileDescList[i].name);
	  _mipmaps[i] = new MVFData(fileDescList[i].name, brickSize);
	}
  }
  else
  {
	vvDebugMsg::msg(1, "Only 2D and 3D dataset is supported");
	return -1;
  }

  _vd = _mipmaps[0]->getVolDesc();
  _vd->frames = 0;

  vvDebugMsg::msg(2, "BrickReader initialized successfully");

  return 0;
}

void BrickReader::activate()
{
  for(int l = 0; l <= _maxMipLevel; l++)
  {
	//vvDebugMsg::msg(2, "BrickReader activated: ", _fileDescList[l].name);
	_mipmaps[l]->activate();
  }
}

void BrickReader::deactivate()
{
  for(int l = 0; l <= _maxMipLevel; l++)
  {
	//vvDebugMsg::msg(2, "BrickReader deactivated: ", _fileDescList[l].name);
	_mipmaps[l]->deactivate();
  }
}


int BrickReader::getOneChannelData(int level, int x, int y, int z, int c, uchar* buf)
{
  // find the location of the data and read it
  if ( _mipmaps[level]->getOneChannelData( x, y, z, c, buf ) == false ) return -1;
  return 0;
}

int BrickReader::getMultiChannelData(int level, int x, int y, int z, uchar* buf)
{
  if ( _mipmaps[level]->getMultiChannelData( x, y, z, buf ) == false ) return -1;
  return 0;
}

