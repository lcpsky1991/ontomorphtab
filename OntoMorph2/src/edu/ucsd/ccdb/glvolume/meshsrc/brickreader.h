// Virvo - Virtual Reality Volume Rendering
// Copyright (C) 1999-2003 University of Stuttgart, 2004-2005 Brown University
// Contact: Jurgen P. Schulze, jschulze@ucsd.edu
//			Quei-Chun (Nancy) Hsu, ???
//			Han Suk Kim, hskim@cs.ucsd.edu
//
// This file is part of Virvo.
//
// Virvo is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library (see license.txt); if not, write to the
// Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA


#ifndef __BRICK_READER_H__
#define __BRICK_READER_H__

#include <stdio.h>
#include <vector>

#include "vvtoolshed.h"
#include "vvvoldesc.h"

#include "volumedata.h"


namespace MipMapVideoLib
{
  class FileInfo
  {
	public:
	  FileInfo(){};
	  ~FileInfo(){};

	public:
	  int level;
	  char name[1024];
	  int sizeX;
	  int sizeY;
	  int sizeZ;
  };


  class BrickReader
  {
	public:
	  /**
	  
		*/
	  BrickReader() {}

	  /**
		close files if opened
	  */
	  ~BrickReader();

	  /** initialize file pointers 
		call fopen(), if any of the files does not exist, raise an error
		@param dim specifies the dimension of dataset, either 2D or 3D
		@param maxMipLevel specifies the maximum mipmap level
		@param brickSize specifies the size of a brick
		@param fileDescList specifies the information about files to be read
		@return 0 if OK, -1 otherwise
	  */
	  int init(int dim, int maxMipLevel, int brickSize, std::vector<FileInfo>& fileDescList);

	  /** read data to buf
		find an appropriate file data and read BRICK_SIZE x BRICK_SIZE data

		@param level specifies mipmap level
		@param x specifies the relative coordinate of the brick
		@param y specifies the relative coordinate of the brick
		@return buf stores the dara read from file
		@return 0 if OK, -1 otherwise
	  */
	  //int getOneChannelData(int level, int x, int y, uchar* buf);
	  int getOneChannelData(int level, int x, int y, int z, int c, uchar* buf);

	  /** read multi channel data to buf
		find an appropriate file data and read c * BRICK_SIZE x BRICK_SIZE data

		@param level specifies mipmap level
		@param x specifies the relative coordinate of the brick
		@param y specifies the relative coordinate of the brick
		@param c specifies how many channelds to be read
		@return buf stores the dara read from file
		@return 0 if OK, -1 otherwise
	  */
	  //int getMultiChannelData(int level, int x, int y, int c, uchar* buf);
	  int getMultiChannelData(int level, int x, int y, int z, uchar* buf);

	  /**
		returns volume description of the finest resolution, i.e., original resolution

		@return vvVolDesc*
	  */
	  vvVolDesc* getVolDesc() { return _vd; };

	  /** activates this reader
		Due to the limit of maximum number of files that can be opened at the same time (255),
		having to many files opened may cause errors.
		However, all the information computed will remain intact. 
		activate() call reopens the files associated with this reader
	  */
	  void activate();

	  /** deactivates this reader
		Due to the limit of maximum number of files that can be opened at the same time (255),
		having to many files opened may cause errors.
		However, all the information computed will remain intact. 
		deactivate() call closes all the files associated with this reader
		and activate() call has to be called prior to calling any getXXXChannelData()
	  */
	  void deactivate();

	private:
	  
	  VolumeData **_mipmaps;	
	  std::vector<FileInfo> _fileDescList;
	  vvVolDesc* _vd;
	  int _maxMipLevel;

  };	// end of class definition

} // end of namespace

#endif


