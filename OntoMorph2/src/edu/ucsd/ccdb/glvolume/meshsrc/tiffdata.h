#ifndef __TIFF_DATA_H__
#define __TIFF_DATA_H__

#include "volumedata.h"

namespace MipMapVideoLib
{
  class TIFFData : public VolumeData
  {
	public:
	  TIFFData(char* filename, int brickSize);
	  ~TIFFData();

	  FILE* loadHeaders(char* fileName);

	  bool getOneChannelData_sep(int x, int y, int z, int c, uchar *raw);
	  bool getOneChannelData_mix(int x, int y, int z, int c, uchar *raw);

	  bool getMultiChannelData_sep(int x, int y, int z, uchar *raw);
	  bool getMultiChannelData_mix(int x, int y, int z, uchar *raw);

	  void setBrickSize(int bs);
  }; // end of class definition
} // end of namespace MipMapVideoLib

#endif

