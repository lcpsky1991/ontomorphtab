#ifndef __VOLUME_DATA_H__
#define __VOLUME_DATA_H__

#include "vvvoldesc.h"

#define VOL_ORG_TAG		32000	// define how image is orgnized:
#define VOL_ORG_TYPE		4	// LONG

namespace MipMapVideoLib
{
  class VolumeData
  {
	public:
	  enum ShrinkMode{
		EAREST = 0,
		ILINEAR
	  };

	  // file format parameter
	  enum VolFormat {
		SEPARATED = 0,	// saving each channel of volume separately
		INTERMIXED		// saving all channels of one pixel together ( as normal image format does/ readable by human eye )
	  };

	public:
	  /** Constructor and destructor
	  */
	  VolumeData();
	  virtual ~VolumeData();

	  vvVolDesc* getVolDesc() { return vd; }

	  //=========================================================================
	  // public member functions
	  //=========================================================================
	  virtual FILE* loadHeaders(char* fileName) = 0;
	  virtual void setBrickSize(int bS) = 0;

	  virtual bool getOneChannelData(int x, int y, int z, int c, uchar *raw);
	  virtual bool getMultiChannelData(int x, int y, int z, uchar *raw);

	  /**
	   due to the limit of maximum number of files opened at the same time
	   we add these two methods to dynamically open and close files
	   however, all the data calcuated in constructor will remain intact
	  */
	  void activate();
	  void deactivate();

	  void closeFile() { if(_fpVol != NULL) fclose(_fpVol); }

	  // volume properties
	  int getBrickSize() { return _brickSize; }
	  unsigned int getVolumeFormat() { return _volFormat; }
	  void setVolumeFormat(VolFormat volFormat) { _volFormat = volFormat; }

	  //int getBytesPerChannel() { return _bpc; }
	  //int getNumChannel() { return _numChannel; }


	protected:
	  virtual bool getOneChannelData_sep(int x, int y, int z, int c, uchar *raw) = 0;
	  virtual bool getOneChannelData_mix(int x, int y, int z, int c, uchar *raw) = 0;

	  virtual bool getMultiChannelData_sep(int x, int y, int z, uchar *raw) = 0;
	  virtual bool getMultiChannelData_mix(int x, int y, int z, uchar *raw) = 0;

	  vvVolDesc* vd;
	  int _dimension;
	  int _brickSize;
	  int _nBricks[3];
	  int _remainingPixels[3];
	  VolFormat _volFormat;

	  FILE* _fpVol;
	  int _whereData;
	  char _fileName[512];
  }; // end of class definition
} // end of namespace MipMapVideoLib

#endif

