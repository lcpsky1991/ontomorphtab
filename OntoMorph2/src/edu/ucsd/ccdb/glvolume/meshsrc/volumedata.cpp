
#include "volumedata.h"
#include "vvdebugmsg.h"

using namespace std;
using namespace MipMapVideoLib;

VolumeData::VolumeData()
{
  _volFormat = SEPARATED;
  vd = new vvVolDesc();
}

VolumeData::~VolumeData()
{
  delete vd;

  if(_fpVol != NULL)
	fclose(_fpVol);
}

void VolumeData::activate()
{
  if(_fpVol != NULL)
	return;
	//vvDebugMsg::msg(1, "VolumeData::_fpVol is not NULL");

  if((_fpVol = fopen(vd->getFilename(), "rb")) == NULL) 
	vvDebugMsg::msg(1, "Error: cannot open file: ", vd->getFilename());
  
}

void VolumeData::deactivate()
{
  if(_fpVol != NULL)
	fclose(_fpVol);
  _fpVol = NULL;
}

bool VolumeData::getOneChannelData(int x, int y, int z, int c, uchar* raw)
{
  switch(_volFormat) {
	case SEPARATED:
	  return getOneChannelData_sep(x, y, z, c, raw);
	case INTERMIXED:
	  return getOneChannelData_mix(x, y, z, c, raw);
	default:
	  vvDebugMsg::msg(1, "Unsupported file format: must be either VolFormat::SEPARATED or VolFormat::INTERMIXED");
	  assert(0);
  }
  return false; // can't reach here
}

bool VolumeData::getMultiChannelData(int x, int y, int z, uchar* raw)
{
  switch(_volFormat) {
	case SEPARATED:
	  return getMultiChannelData_sep(x, y, z, raw);
	case INTERMIXED:
	  return getMultiChannelData_mix(x, y, z, raw);
	default:
	  vvDebugMsg::msg(1, "Unsupported file format: must be either VolFormat::SEPARATED or VolFormat::INTERMIXED");
	  assert(0);
  }
  return false; // can't reach here
}


