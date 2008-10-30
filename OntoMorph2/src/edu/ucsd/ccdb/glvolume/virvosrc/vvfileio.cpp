// Virvo - Virtual Reality Volume Rendering
// Copyright (C) 1999-2003 University of Stuttgart, 2004-2005 Brown University
// Contact: Jurgen P. Schulze, jschulze@ucsd.edu
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

#include <iostream>
#include <iomanip>

#ifdef VV_DEBUG_MEMORY
#include <crtdbg.h>
#define new new(_NORMAL_BLOCK,__FILE__, __LINE__)
#endif

#include <math.h>
#include <limits.h>
#include <ctype.h>

#include "vvvirvo.h"
#include "vvfileio.h"
#include "vvtoolshed.h"
#include "vvdebugmsg.h"
#include "vvtokenizer.h"
#include "vvdicom.h"
#include "vvarray.h"

#ifdef __sun
#define powf pow
#endif

using namespace std;

//----------------------------------------------------------------------------
/// Constructor
vvFileIO::vvFileIO()
{
  vvDebugMsg::msg(1, "vvFileIO::vvFileIO()");
  assert(sizeof(float) == 4);
  strcpy(_xvfID, "VIRVO-XVF");
  strcpy(_nrrdID, "NRRD0001");
  _sections = ALL_DATA;
  _compression = true;
}

//----------------------------------------------------------------------------
void vvFileIO::setDefaultValues(vvVolDesc* vd)
{
  vd->vox[0]  = 0;
  vd->vox[1]  = 0;
  vd->vox[2]  = 0;
  vd->frames  = 0;
  vd->bpc     = 1;
  vd->chan    = 1;
  vd->dist[0] = 1.0f;
  vd->dist[1] = 1.0f;
  vd->dist[2] = 1.0f;
  vd->dt      = 1.0f;
  vd->real[0] = 0.0f;
  vd->real[1] = 1.0f;
}

//----------------------------------------------------------------------------
/** Read the next ASCII integer character from a file.
  Ignores all other characters (as well as CR, LF etc.).
  @return 0 if no integer string could be found
*/
int vvFileIO::readASCIIint(FILE* src)
{
  char intString[64];                             // string to hold integer value
  int  i;                                         // index to current letter in integer string
  char c;                                         // most recently read character
  bool done = false;                              // true if integer reading done
  bool found = false;                             // true if ASCII integer reading has begun

  i = 0;
  while (!done && !feof(src))
  {
    c = (char)fgetc(src);
    if (c>='0' && c<='9')
    {
      found = true;
      intString[i] = c;
      ++i;
    }
    else if (found==true) done = true;
  }
  intString[i] = '\0';
  return atoi(intString);
}

//----------------------------------------------------------------------------
/// Loader for voxel files in worldlines format.
vvFileIO::ErrorType vvFileIO::loadWLFile(vvVolDesc* vd)
{
  FILE* fp;
  int val[3];                                     // scalar voxel value ([0] = x, [1] = y, [2] = z)
  int i;                                          // index counter
  int col = 0;                                    // voxel scalar value
  int max[3]=                                     // maximum values in each dimension
  {
    1, 1, 1
  };
  bool done;                                      // true if done
  uchar* raw;                                     // raw volume data

  vvDebugMsg::msg(1, "vvFileIO::loadWLFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;
  fp = fopen(vd->getFilename(), "r");
  if (fp==NULL) return FILE_ERROR;

  vd->removeSequence();

  // Find maximum dimension values:
  done = false;
  while (!done)
  {
    for (i=0; i<3; ++i)
    {
      if (feof(fp)) done = true;
      else
      {
        val[i] = readASCIIint(fp);
                                                  // adjust maximum value. +1 needs to be there to include index 0
        if ((val[i]+1) > max[i]) max[i] = val[i] + 1;
      }
    }
  }

  vd->vox[0] = max[0];
  vd->vox[1] = max[1];
  vd->vox[2] = max[2];
  vd->frames = 1;

  // Allocate memory for volume:
  raw = new uchar[vd->getFrameBytes()];

  // Initialize volume data:
  for (i=0; i<vd->getFrameBytes(); ++i)
  {
    raw[i] = (uchar)0;
  }

  // Read volume data:
  fseek(fp, 0, SEEK_SET);
  done = false;
  while (!done)
  {
    for (i=0; i<3; ++i)
    {
      if (feof(fp)) done = true;
      else
      {
        val[i] = readASCIIint(fp);
        if (val[i] > max[i])                      // safety check
        {
          vvDebugMsg::msg(1, "Error: Voxel coordinate exceeds limit.");
          fclose(fp);
          delete[] raw;
          return FILE_ERROR;
        }
      }
    }
    raw[val[0] + val[1] * max[0] + val[2] * max[0] * max[1]] = uchar(col);
    ++col;
    if (col > 255) col = 0;
  }

  vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for voxel file in ASCII format.
  3 integers in first line indicating size of 3 dimensions (XSIZE, YSIZE, ZSIZE),
  then quadruples of integers indicating visible voxel position (0..XSIZE-1 etc.)
  and opacity (0..255).<BR>
  1 byte opacity value per voxel, 0=transparent, 255=opaque
*/
vvFileIO::ErrorType vvFileIO::loadASCFile(vvVolDesc* vd)
{
  FILE* fp;
  int x, y, z, op, i;
  uchar* raw;                                     // raw volume data
  size_t retval;

  vvDebugMsg::msg(1, "vvFileIO::loadASCFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;
  fp = fopen(vd->getFilename(), "rt");
  if (fp==NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  vd->removeSequence();

  retval=fscanf(fp, "%d %d %d", &x, &y, &z);
  if (retval==3)
  {
    vd->vox[0] = x;
    vd->vox[1] = y;
    vd->vox[2] = z;
    vd->frames = 1;
  }
  else                                            // invalid volume dimensions
  {
    fclose(fp);
    vvDebugMsg::msg(1, "Error: Invalid header in ASC file.");
    return FILE_ERROR;
  }

  raw = new uchar[vd->getFrameBytes()];

  for (i=0; i<vd->getFrameBytes(); ++i)
    raw[i] = (uchar)0;                            // initialize with opacity 0

  while (!feof(fp))
  {
    retval=fscanf(fp, "%d %d %d %d", &x, &y, &z, &op);
    if (retval!=4)
    {
      vvDebugMsg::msg(1, "vvFileIO::loadASCFile: fscanf failed");
      fclose(fp);
      delete[] raw;
      return FILE_ERROR;
    }
    if (x>vd->vox[0]-1 || y>vd->vox[1]-1 || z>vd->vox[2]-1 || x<0 || y<0 || z<0 || op<0 || op>255)
    {
      vvDebugMsg::msg(1, "Error: Invalid value in ASC file.");
      fclose(fp);
      delete[] raw;
      return FILE_ERROR;
    }
    raw[x + y * vd->vox[0] + z * vd->vox[0] * vd->vox[1]] = (uchar)op;
  }
  fclose(fp);

  vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
  return OK;
}

//----------------------------------------------------------------------------
/** Save current frame to a .RVF (raw volume data) file.
 Only volume dimensions and 8 bit raw data is saved in this format.
*/
vvFileIO::ErrorType vvFileIO::saveRVFFile(vvVolDesc* vd)
{
  FILE* fp;                                       // volume file pointer
  int frameSize;                                  // size of a frame in bytes
  uchar* raw;                                     // raw volume data
  vvVolDesc* v;                                   // temporary volume description

  vvDebugMsg::msg(1, "vvFileIO::saveRVFFile()");

  // Save volume data:
  v = new vvVolDesc(vd, vd->getCurrentFrame());   // copy current frame to a new VD
  if (vd->bpc!=1)
  {
    cerr << "Converting data to 1 bpc" << endl;
    v->convertBPC(1);
  }
  if (vd->chan!=1)
  {
    cerr << "Converting data to 1 channel" << endl;
    v->convertChannels(1);
  }
  frameSize = v->getFrameBytes();
  raw = v->getRaw();                              // save only current frame of loaded sequence
  if (frameSize==0 || raw==NULL)
  {
    delete v;
    return VD_ERROR;
  }

                                                  // now open file to write
  if ( (fp = fopen(v->getFilename(), "wb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file to write.");
    delete v;
    return FILE_ERROR;
  }

  vvToolshed::write16(fp, (ushort)v->vox[0]);
  vvToolshed::write16(fp, (ushort)v->vox[1]);
  vvToolshed::write16(fp, (ushort)v->vox[2]);

  // Write volume data:
  if ((int)fwrite(raw, 1, frameSize, fp) != frameSize)
  {
    vvDebugMsg::msg(1, "Error: Cannot write voxel data to file.");
    fclose(fp);
    delete v;
    return FILE_ERROR;
  }

  fclose(fp);
  delete v;
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for voxel file in rvf (raw volume file) format.
  File specification (byte order: most significant first = big endian):<P>
  Header: 3 x 16 bit for width, height, slices<BR>
  Data: width x height x slices bytes 8 bit voxel data;
        order: top left front first, then to right,
        then to bottom, then to back
*/
vvFileIO::ErrorType vvFileIO::loadRVFFile(vvVolDesc* vd)
{
  FILE* fp;                                       // volume file pointer
  int frameSize;                                  // size of a frame in bytes
  uchar* raw;                                     // raw volume data

  vvDebugMsg::msg(1, "vvFileIO::loadRVFFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;
  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  vd->removeSequence();

  // Read header:
  vd->vox[0] = vvToolshed::read16(fp);
  vd->vox[1] = vvToolshed::read16(fp);
  vd->vox[2] = vvToolshed::read16(fp);
  vd->frames = 1;
  vd->bpc    = 1;
  vd->chan    = 1;

  // Create new data space for volume data:
  if ((_sections & RAW_DATA) != 0)
  {
    frameSize = vd->getFrameBytes();
    raw = new uchar[frameSize];

    // Load volume data:
    if ((int)fread(raw, 1, frameSize, fp) != frameSize)
    {
      vvDebugMsg::msg(1, "Error: Insufficient voxel data in RVF file.");
      fclose(fp);
      delete[] raw;
      return FILE_ERROR;
    }

    vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
  }
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for voxel file in old xvf (extended volume file) format.
 File specification (byte order: most significant first = big endian):
 <PRE>
 Header:
   Offset Bytes Data Type       Description
 ---------------------------------------------------------------
      0   9     char            file ID string: "VIRVO-XVF"
      9   2     unsigned short  offset to beginning of data area, from top of file [bytes]
     11   2 x 4 unsigned int    width and height of volume [voxels]
     19   4     unsigned int    number of slices per time step
     23   4     unsigned int    number of frames in volume animation (time steps)
27   1     unsigned char   bytes per channel (for details see vvvoldesc.h)
28   3 x 4 float           real world voxel size (width, height, depth) [mm]
40   4     float           length of a time step in the volume animation [seconds]
44   2 x 4 float           physical data range covered by voxel data (minimum, maximum)
52   3 x 4 float           real world location of volume center (x,y,z) [mm]
64   1     unsigned char   storage type (for details see vvvoldesc.h)
65   1     unsigned char   compression type (0=uncompressed, 1=RLE)
66   2     unsigned short  number of transfer functions
68   2     unsigned short  type of transfer function: 0 = 4 x 256 Byte,
1 = list of control pins
70   2     unsigned int    icon size: width (=height) [pixels]

Data area:
Data starts at "offset to data area".
Voxel order: voxel at top left front first, then to right,
then to bottom, then to back, then frames. All bytes of each voxel
are stored successively.
In RLE encoding mode, a 4 byte big endian value precedes each frame,
telling the number of RLE encoded bytes that will follow. If this
value is zero, the frame is stored without encoding.

Now follow the transfer functions.
Each transfer function of type 0 consists of:
- Zero terminated description string
- Transfer function data in RGBA format:
First all R's, then all G's, etc.
Each R/G/B/A entry is coded as one unsigned byte.
The table length depends on the number of bits per voxel:
8 bits per channel => 4 * 256 bytes (RGBA)
16 bits per channel => 4 * 4096 bytes (RGBA)
- Transfer function in pin format:
Each pin consists of 9 float values. The list is terminated by
a set of nine values of -1.0.

Now come the channel names.
Each channel name is stored as a zero terminated string. If a channel does
not have a name only the terminating zero is stored.

Hints:
The big endian hexadecimal representations of some important floating point values are:
1.0 = 3F 80 00 00
-1.0 = BF 80 00 00
</PRE><P>

Up to now, the following header sizes (=offset to data area) were used in files:<BR>
28: ID string + offset + width + height + slices + frames + bpv<BR>
48: + dist + dt + num Transf + type Transf<BR>
69: + realMin + realMax + position + compression
70: + storage type
72: + icon size
*/
vvFileIO::ErrorType vvFileIO::loadXVFFileOld(vvVolDesc* vd)
{
  FILE* fp;                                       // volume file pointer
  uchar serialized[vvVolDesc::SERIAL_ATTRIB_SIZE];// space for serialized volume data
  char tfName[257];                               // transfer function name
  int f;                                          // counter for frames
  int c, i;                                       // counters
  int frameSize;                                  // size of a frame in bytes
  uchar* raw;                                     // raw volume data
  uchar* encoded = NULL;                          // encoded volume data
  int headerSize;                                 // total header size in bytes, including ID string
  int ctype;                                      // compression type
  int tnum;                                       // number of transfer functions
  int ttype;                                      // type of transfer function
  bool done;
  int serializedSize;                             // size of serialized part of header
  int encodedSize;                                // size of encoded data array
  int offset;                                     // byte offset into file
  float v[9];                                     // TF elements
  size_t retval;

  vvDebugMsg::msg(1, "vvFileIO::loadXVFFileOld()");

  if (vd->getFilename()==NULL) return FILE_ERROR;

  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  vd->removeSequence();                           // delete previous volume sequence

  // Read header:
  for (i=0; i<(int)strlen(_xvfID); ++i)
  {
    if (fgetc(fp) != _xvfID[i])
    {
      vvDebugMsg::msg(1, "Error: Invalid file ID string. Expected: ", _xvfID);
      fclose(fp);
      return DATA_ERROR;
    }
  }
  headerSize = vvToolshed::read16(fp);            // total header size in bytes
  if (headerSize<=28)                             // early files didn't have transfer function info
    serializedSize = headerSize - strlen(_xvfID);
  else if (headerSize<=70)                        // after this version icon size was introduced
    serializedSize = headerSize - strlen(_xvfID) - 7;
  else serializedSize = headerSize - strlen(_xvfID) - 9;

  retval=fread(serialized, serializedSize, 1, fp);
  if (retval!=1)
  {
    std::cerr<<"vvFileIO::loadXVFFile fread failed"<<std::endl;
    fclose(fp);
    return FILE_ERROR;
  }
  vd->deserializeAttributes(serialized, serializedSize);

  // Allow either bit or byte per voxel in vd->bpv:
  if (vd->bpc==8 || vd->bpc==16 || vd->bpc==24 || vd->bpc==32) vd->bpc /= 8;

  // Interpret old style without channels correctly:
  if (vd->chan==0)
  {
    if (vd->bpc==3 || vd->bpc==4)
    {
      vd->chan = vd->bpc;
      vd->bpc = 1;
    }
    else vd->chan = 1;
  }

  // Verify bpc:
  assert(vd->bpc==1 || vd->bpc==2 || vd->bpc==4);

  if (headerSize <= 28)                           // early file format?
  {
    ctype = 0;
    tnum  = 0;
    ttype = 0;
  }
  else
  {
    ctype  = vvToolshed::read8(fp);
    tnum   = vvToolshed::read16(fp);
    ttype  = vvToolshed::read16(fp);
  }
  if (headerSize >= 72)                           // icon info?
  {
    vd->iconSize = vvToolshed::read16(fp);
  }

  frameSize = vd->getFrameBytes();

  // Print file information:
  if (vvDebugMsg::isActive(1))
  {
    cerr << "XVF header size:                  " << headerSize << endl;
    cerr << "XVF compression type:             " << ctype << endl;
    cerr << "XVF number of transfer functions: " << tnum << endl;
    cerr << "XVF type of transfer function(s): " << ttype << endl;
  }

  // Load volume data:
  if ((_sections & RAW_DATA) != 0)
  {
    fseek(fp, headerSize, SEEK_SET);
    if (ctype==1) encoded = new uchar[frameSize];
    for (f=0; f<vd->frames; ++f)
    {
      raw = new uchar[frameSize];                 // create new data space for volume data
      switch (ctype)
      {
        default:
        case 0:                                   // no compression
          if ((int)fread(raw, 1, frameSize, fp) != frameSize)
          {
            vvDebugMsg::msg(1, "Error: Insuffient voxel data in file.");
            fclose(fp);
            delete[] raw;
            delete[] encoded;
            return DATA_ERROR;
          }
          break;
        case 1:                                   // RLE encoding
          encodedSize = vvToolshed::read32(fp);
          if (encodedSize>0)
          {
            if ((int)fread(encoded, 1, encodedSize, fp) != encodedSize)
            {
              vvDebugMsg::msg(1, "Error: Insuffient voxel data in file.");
              fclose(fp);
              delete[] raw;
              delete[] encoded;
              return DATA_ERROR;
            }
            if (vvToolshed::decodeRLE(raw, encoded, encodedSize, vd->getBPV(), frameSize) < 0)
            {
              vvDebugMsg::msg(1, "Error: Decoding exceeds frame size.");
              fclose(fp);
              delete[] raw;
              delete[] encoded;
              return DATA_ERROR;
            }
          }
          else                                    // no encoding
          {
            if ((int)fread(raw, 1, frameSize, fp) != frameSize)
            {
              vvDebugMsg::msg(1, "Error: Insuffient voxel data in file.");
              fclose(fp);
              delete[] raw;
              delete[] encoded;
              return DATA_ERROR;
            }
          }
          break;
      }
      vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
    }
    delete[] encoded;
  }

  // Read transfer function(s):
  offset = headerSize;
  fseek(fp, offset, SEEK_SET);
  for (f=0; f<vd->frames; ++f)
  {
    switch (ctype)
    {
      default:
      case 0: offset += frameSize;
      break;
      case 1: encodedSize = vvToolshed::read32(fp);
      offset += 4;                                // add 4 bytes for encoded size value
      if (encodedSize==0) offset += frameSize;
      else offset += encodedSize;
      break;
    }
    fseek(fp, offset, SEEK_SET);
  }
  for (i=0; i<tnum; ++i)
  {
    // Read zero terminated TF name:
    c = 0;
    do
    {
      tfName[c] = char(fgetc(fp));
      if (feof(fp)) break;
      ++c;
    } while (tfName[c-1] != 0);

    if (!feof(fp))
    {
      if (ttype != 1) break;                      // only accept pin lists

      done = false;
      while (done==false)
      {
        for (c=0; c<9; ++c)
        {
          v[c] = vvToolshed::readFloat(fp);
        }
        if (v[0]==-1.0f || feof(fp)) done = true;
      }
    }
  }

  // Read icon:
  if (!feof(fp) && vd->iconSize>0)
  {
    delete[] vd->iconData;
    int iconBytes = vd->iconSize * vd->iconSize * vvVolDesc::ICON_BPP;
    vd->iconData = new uchar[iconBytes];
    int encodedSize = vvToolshed::read32(fp);
    if (encodedSize>0)                            // compressed icon?
    {
      uchar* encoded = new uchar[encodedSize];
      if ((int)fread(encoded, 1, encodedSize, fp) != encodedSize)
      {
        cerr << "Error: Insuffient compressed icon data in file." << endl;
        fclose(fp);
        delete[] vd->iconData;
        vd->iconData = NULL;
        delete[] encoded;
        return DATA_ERROR;
      }
      if (vvToolshed::decodeRLE(vd->iconData, encoded, encodedSize, vvVolDesc::ICON_BPP, iconBytes) < 0)
      {
        cerr << "Error: Decoding exceeds icon size." << endl;
        fclose(fp);
        delete[] vd->iconData;
        vd->iconData = NULL;
        delete[] encoded;
        return DATA_ERROR;
      }
      delete[] encoded;
    }
    else                                          // uncompressed icon
    {
      if ((int)fread(vd->iconData, 1, iconBytes, fp) != iconBytes)
      {
        cerr << "Error: Insuffient uncompressed icon data in file." << endl;
        fclose(fp);
        delete[] vd->iconData;
        vd->iconData = NULL;
        return DATA_ERROR;
      }
    }
  }

  // Clean up:
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Save volume data to a .XVF (extended volume data) file.
 <PRE>Example:

 VIRVO-XVF 2.0     # ID and version/release number [version.release]
 VOXELS 64 64 64   # width, height, slices per time step [voxels]
 TIMESTEPS 6       # time steps
 BPC 3             # number of bypes per channel
 CHANNELS 1        # number of channels
 DIST 1.0 1.0 5.0  # sample distance [mm]
 ENDIAN BIG        # endianness of all binary numbers in file [BIG or LITTLE]
 DTIME 0.1         # time to display each time step [sec]
MINMAX -10.0 22.0 # integer data types: physical data range
# float data types:   min/max of range for color mapping
POS 0.0 0.0 0.0   # real-world location of volume center (x,y,z) [mm]
ICON 32 32        # beginning of icon data (width, height) [pixels],
# followed by width*height 24-bit RGB pixels
CHANNELNAMES      # ASCII channel names, separated by space characters.
TF_COLOR          # color transfer function widget
TF_BELL Bone ...  # Gaussian transfer function widget
TF_PYRAMID tissue # pyramidal transfer function widget
VOXELDATA         # beginning of voxel data, followed by binary voxel data

Comments after the '#' sign are ignored.

Storage of transfer functions (TF):
Example:

Data area after VOXELDATA:
Voxel order: voxel at top left front first, then to right,
then to bottom, then to back, then frames. All bytes of each voxel
are stored successively and in big endian format.
In RLE encoding mode, a 4 byte value precedes each frame,
telling the number of RLE encoded bytes that will follow. If this
value is zero, the frame is unencoded.
</PRE>
*/
vvFileIO::ErrorType vvFileIO::saveXVFFile(vvVolDesc* vd)
{
  FILE* fp;                                       // volume file pointer
  uchar* raw;                                     // raw volume data
  uchar* encoded = NULL;                          // encoded volume data
  int f, i;                                       // counters
  int frames;                                     // volume animation frames
  int frameSize;                                  // frame size
  int numTF;                                      // number of transfer functions to save
  int encodedSize;                                // number of bytes in encoded array

  vvDebugMsg::msg(1, "vvFileIO::saveXVFFile()");

  // Prepare variables:
  frames = vd->frames;
  if (frames==0) return VD_ERROR;
  frameSize = vd->getFrameBytes();

  // Open file:
                                                  // now open file to write
  if ( (fp = fopen(vd->getFilename(), "wb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file to write.");
    return FILE_ERROR;
  }

  // Force icon to be present:
  if (vd->iconSize==0) vd->makeIcon(vvVolDesc::DEFAULT_ICON_SIZE);

  // Write header:
  fprintf(fp, "XVF\n");
  fprintf(fp, "VERSION %2.1f\n", 2.0f);
  fprintf(fp, "VOXELS %d %d %d\n", vd->vox[0], vd->vox[1], vd->vox[2]);
  fprintf(fp, "TIMESTEPS %d\n", vd->frames);
  fprintf(fp, "BPC %d\n", vd->bpc);
  fprintf(fp, "CHANNELS %d\n", vd->chan);
  fprintf(fp, "DIST %g %g %g\n", vd->dist[0], vd->dist[1], vd->dist[2]);
  fprintf(fp, "ENDIAN %s\n", (vvToolshed::getEndianness()==vvToolshed::VV_LITTLE_END) ? "LITTLE" : "BIG");
  fprintf(fp, "DTIME %g\n", vd->dt);
  fprintf(fp, "MINMAX %g %g\n", vd->real[0], vd->real[1]);
  fprintf(fp, "POS %g %g %g\n", vd->pos[0], vd->pos[1], vd->pos[2]);

  // Write channel names:
  fprintf(fp, "CHANNELNAMES");
  for (i=0; i<vd->chan; ++i)
  {
    if (vd->getChannelName(i)==NULL) fprintf(fp, " UNNAMED");
    else fprintf(fp, " %s", vd->getChannelName(i));
  }
  fprintf(fp, "\n");

  // Write transfer function widgets:
  numTF = vd->tf._widgets.count();
  vd->tf._widgets.first();
  for (i=0; i<numTF; ++i)
  {
    vd->tf._widgets.getData()->write(fp);
    vd->tf._widgets.next();
  }

  // Write icon:
  fprintf(fp, "ICON %d %d\n", vd->iconSize, vd->iconSize);
  if (vd->iconSize>0)
  {
    int iconBytes = vd->iconSize * vd->iconSize * vvVolDesc::ICON_BPP;
    uchar* encodedIcon = new uchar[iconBytes];
    encodedSize = vvToolshed::encodeRLE(encodedIcon, vd->iconData, iconBytes, vvVolDesc::ICON_BPP, iconBytes);
    if (encodedSize>=0)                           // compression possible?
    {
      vvToolshed::write32(fp, encodedSize);       // write length of encoded icon
      if ((int)fwrite(encodedIcon, 1, encodedSize, fp) != encodedSize)
      {
        cerr << "Error: Cannot write compressed icon data to file." << endl;
        fclose(fp);
        delete[] encodedIcon;
        return FILE_ERROR;
      }
    }
    else
    {
      vvToolshed::write32(fp, 0);                 // write zero to indicate unencoded icon
      if ((int)fwrite(vd->iconData, 1, iconBytes, fp) != iconBytes)
      {
        cerr << "Error: Cannot write uncompressed icon data to file." << endl;
        fclose(fp);
        delete[] encodedIcon;
        return FILE_ERROR;
      }
    }
    delete[] encodedIcon;
  }

  // Write volume data frame by frame:
  fprintf(fp, "VOXELDATA\n");
  if (_compression==1) encoded = new uchar[frameSize];
  for (f=0; f<frames; ++f)
  {
    raw = vd->getRaw(f);
    if (raw==NULL)
    {
      vvDebugMsg::msg(1, "Error: no data available for frame", f);
      fclose(fp);
      delete[] encoded;
      return VD_ERROR;
    }
    if (_compression)
    {
      encodedSize = vvToolshed::encodeRLE(encoded, raw, frameSize, vd->bpc * vd->chan, frameSize);
      if (encodedSize>=0)                         // compression possible?
      {
        vvToolshed::write32(fp, encodedSize);     // write length of encoded frame
        if ((int)fwrite(encoded, 1, encodedSize, fp) != encodedSize)
        {
          cerr << "Error: Cannot write compressed voxel data to file." << endl;
          fclose(fp);
          delete[] encoded;
          return FILE_ERROR;
        }
      }
      else                                        // no compression possible -> store unencoded
      {
        vvToolshed::write32(fp, 0);               // write zero to mark as unencoded
        if ((int)fwrite(raw, 1, frameSize, fp) != frameSize)
        {
          cerr << "Error: Cannot write uncompressed voxel data to file." << endl;
          fclose(fp);
          delete[] encoded;
          return FILE_ERROR;
        }
      }
    }
    else                                          // no compression
    {
      if ((int)fwrite(raw, 1, frameSize, fp) != frameSize)
      {
        cerr << "Error: Cannot write voxel data to file." << endl;
        fclose(fp);
        delete[] encoded;
        return FILE_ERROR;
      }
    }
  }
  delete[] encoded;

  // Clean up:
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for voxel file in xvf (extended volume file) format.
 File format: see saveXVFFile()
*/
vvFileIO::ErrorType vvFileIO::loadXVFFile(vvVolDesc* vd)
{
  const char xvfID[4] = "XVF";
  FILE* fp;                                       // volume file pointer
  vvTokenizer* tok;
  vvTokenizer::TokenType ttype;                   // currently processed token type
  int f, i;                                       // counters
  int frameSize;                                  // size of a frame in bytes
  uchar* raw;                                     // raw volume data
  uchar* encoded = NULL;                          // encoded volume data
  bool done;
  int encodedSize;                                // size of encoded data array
  vvToolshed::EndianType endian;

  vvDebugMsg::msg(1, "vvFileIO::loadXVFFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;

  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  // Read magic code:
  for (i=0; i<int(strlen(xvfID)); ++i)
  {
    if (fgetc(fp) != xvfID[i])
    {
      fclose(fp);
      vvDebugMsg::msg(1, "Trying to load old style XVF format.");
      return loadXVFFileOld(vd);
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
              return DATA_ERROR;
            }
            if (vvToolshed::decodeRLE(vd->iconData, encoded, encodedSize, vvVolDesc::ICON_BPP, iconBytes) < 0)
            {
              cerr << "Error: Decoding exceeds icon size." << endl;
              fclose(fp);
              delete[] vd->iconData;
              vd->iconData = NULL;
              delete[] encoded;
              return DATA_ERROR;
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
              return DATA_ERROR;
            }
          }
          tok->setFilePos(fp);
        }
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

  // Load volume data:
  if ((_sections & RAW_DATA) != 0)
  {
    fseek(fp, tok->getFilePos(), SEEK_SET);
    encoded = new uchar[frameSize];
    for (f=0; f<vd->frames; ++f)
    {
      raw = new uchar[frameSize];                 // create new data space for volume data
      encodedSize = vvToolshed::read32(fp);
      if (encodedSize>0)
      {
        if ((int)fread(encoded, 1, encodedSize, fp) != encodedSize)
        {
          vvDebugMsg::msg(1, "Error: Insuffient voxel data in file.");
          fclose(fp);
          delete[] raw;
          delete[] encoded;
          return DATA_ERROR;
        }
        if (vvToolshed::decodeRLE(raw, encoded, encodedSize, vd->getBPV(), frameSize) < 0)
        {
          vvDebugMsg::msg(1, "Error: Decoding exceeds frame size.");
          fclose(fp);
          delete[] raw;
          delete[] encoded;
          return DATA_ERROR;
        }
      }
      else                                        // no encoding
      {
        if ((int)fread(raw, 1, frameSize, fp) != frameSize)
        {
          vvDebugMsg::msg(1, "Error: Insuffient voxel data in file.");
          fclose(fp);
          delete[] raw;
          delete[] encoded;
          return DATA_ERROR;
        }
      }
      vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
    }
    delete[] encoded;
  }

  // Clean up:
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Save volume data to a Nrrd file (Gordon Kindlmann's proprietary format).
  See http://www.cs.utah.edu/~gk/teem/nrrd/ for more information.
  This file format cannot save transfer functions in the Virvo format.
*/
vvFileIO::ErrorType vvFileIO::saveNrrdFile(vvVolDesc* vd)
{
  char buf[256];                                  // text buffer
  FILE* fp;                                       // volume file pointer
  uchar* raw;                                     // raw volume data
  int frameSize;                                  // frame size

  vvDebugMsg::msg(1, "vvFileIO::saveNrrdFile()");

  if (vd->bpc>2)
  {
    cerr << "Can only save 8 and 16 bit per voxel data in nrrd format." << endl;
    return FORMAT_ERROR;
  }

  // Prepare variables:
  if (vd->frames==0) return VD_ERROR;
  if (vd->frames>1)
    cerr << "The nrrd writer will only write the first animation frame." << endl;
  frameSize = vd->getFrameBytes();

  // Open file:
                                                  // now open file to write
  if ( (fp = fopen(vd->getFilename(), "wb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file to write.");
    return FILE_ERROR;
  }

  // Write magic string:
  fputs(_nrrdID, fp);
  fputc('\n', fp);

  // Write content:
  vvToolshed::extractBasename(buf, vd->getFilename());
  fprintf(fp, "content: %s\n", buf);

  // Write data type:
  fputs("type: ", fp);
  switch (vd->bpc)
  {
    case 1: fputs("unsigned char\n", fp); break;
    case 2: fputs("unsigned short\n", fp); break;
    default: assert(0); break;
  }

  // Write dimension:
  fputs("dimension: 3\n", fp);

  // Write sizes:
  fprintf(fp, "sizes: %d %d %d\n", vd->vox[0], vd->vox[1], vd->vox[2]);

  // Write encoding:
  fputs("encoding: raw\n", fp);

  // Write spacings:
  fprintf(fp, "spacings: %f %f %f\n", vd->dist[0], vd->dist[1], vd->dist[2]);

  // Write endianness:
  fputs("endian: big\n", fp);

  // Write scalar data:
  fputc('\n', fp);
  raw = vd->getRaw(0);
  if (raw==NULL)
  {
    cerr << "Error: no data available for frame 0" << endl;
    fclose(fp);
    return VD_ERROR;
  }
  if ((int)fwrite(raw, 1, frameSize, fp) != frameSize)
  {
    vvDebugMsg::msg(1, "Error: Cannot write voxel data to file.");
    fclose(fp);
    return FILE_ERROR;
  }

  // Clean up:
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Writer for voxel file in avf (ASCII volume file) format.
  For file format specification see loadAVFFile.
*/
vvFileIO::ErrorType vvFileIO::saveAVFFile(vvVolDesc* vd)
{
  FILE* fp;                                       // volume file pointer
  uchar* raw;                                     // raw volume data
  int f;                                          // counter for frames
  int x, y, z, c;                                 // counters

  vvDebugMsg::msg(1, "vvFileIO::saveAVFFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;

  if ( (fp = fopen(vd->getFilename(), "wb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open avf file to write.");
    return FILE_ERROR;
  }

  // Write header:
  fprintf(fp, "WIDTH %d\n", vd->vox[0]);
  fprintf(fp, "HEIGHT %d\n", vd->vox[1]);
  fprintf(fp, "SLICES %d\n", vd->vox[2]);
  fprintf(fp, "FRAMES %d\n", vd->frames);
  fprintf(fp, "MIN %g\n", vd->real[0]);
  fprintf(fp, "MAX %g\n", vd->real[1]);
  fprintf(fp, "XDIST %g\n", vd->dist[0]);
  fprintf(fp, "YDIST %g\n", vd->dist[1]);
  fprintf(fp, "ZDIST %g\n", vd->dist[2]);
  fprintf(fp, "XPOS %g\n", vd->pos[0]);
  fprintf(fp, "YPOS %g\n", vd->pos[1]);
  fprintf(fp, "ZPOS %g\n", vd->pos[2]);
  fprintf(fp, "TIME %g\n", vd->dt);
  fprintf(fp, "BPC %d\n", vd->bpc);
  fprintf(fp, "CHANNELS %d\n", vd->chan);

  // Write voxel data:
  for (f=0; f<vd->frames; ++f)
  {
    raw = vd->getRaw(f);
    for (z=0; z<vd->vox[2]; ++z)
    {
      if (vd->frames > 1) fprintf(fp, "# Frame %d, slice %d\n", f, z);
      else fprintf(fp, "# Slice %d\n", z);
      for (y=0; y<vd->vox[1]; ++y)
      {
        for (x=0; x<vd->vox[0]; ++x)
        {
          for (c=0; c<vd->chan; ++c)
          {
            switch(vd->bpc)
            {
              case 1:
                fprintf(fp, "%d", int(*(raw++)));
                break;
              case 2:
              {
                int d = *raw++;
                d <<= 8;
                d += *raw++;
                fprintf(fp, "%d", d);
                break;
              }
              case 4:
                fprintf(fp, "%g", *((float*)raw++));
                break;
            }
            fputc(' ', fp);
          }
        }
        fprintf(fp, "\n");
      }
    }
  }

  // Clean up:
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for voxel file in avf (ASCII volume file) format.
 File specification:
 <PRE>
 Header:

   The header consists of several identifier and value pairs to specify
   the data format. Identifier and a value are separated by whitespace.
   This file format cannot store transfer functions.
   Unix-style comments starting with '#' are permitted.

   The following abbreviations are used:
<int>            for integer values
<float>          for floating point values

The following lines are required:
WIDTH <int>      the width of the volume [voxels]
HEIGHT <int>     the height of the volume [voxels]
SLICES <int>     the number of slices in the volume [voxels]

The following lines are optional.
If they are missing, default values are used:
FRAMES <int>     the number of data sets contained in the file
(default: 1)
MIN <float>      the minimum data value, smaller values will be constrained
to this value (default: 0.0)
MAX <float>      the maximum data value, larger values will be constrained
to this value (default: 1.0)
XDIST <float>    the sample distance in x direction (-> width) [mm]
(default: 1.0)
YDIST <float>    the sample distance in y direction (-> height) [mm]
(default: 1.0)
ZDIST <float>    the sample distance in z direction (-> slices) [mm]
(default: 1.0)
TIME <float>     the length of each time step for transient data [s]
(default: 1.0)
BPC <int>        bytes per channel (1=8bit, 2=16bit, 4=float)
CHANNELS <int>   number of data channels per voxel
XPOS <float>     x position of data set center [mm] (default: 0.0)
YPOS <float>     y position of data set center [mm] (default: 0.0)
ZPOS <float>     z position of data set center [mm] (default: 0.0)

Voxel data:

The voxel data starts right after the header. The data values
are separated by whitespace and/or end-of-line characters.
float and integer values are accepted.
Voxel order: voxel at top left front first, then to right,
then to bottom, then to back, then frames.
All channels of each voxel are stored consecutively (interleaved).

Sample file:

WIDTH    4
HEIGHT   3
SLICES   2
FRAMES   1
MIN      0.0
MAX      1.0
XDIST    1.0
YDIST    1.0
ZDIST    1.0
XPOS     0.0
YPOS     0.0
ZPOS     0.0
TIME     1.0
BPC      4
CHANNELS 1
0.9 0.9 0.9 0.9
0.9 0.2 0.3 0.9
0.9 0.2 0.4 0.9
0.8 0.8 0.8 0.8
0.8 0.1 0.1 0.8
0.8 0.0 0.0 0.8
</PRE>
*/
vvFileIO::ErrorType vvFileIO::loadAVFFile(vvVolDesc* vd)
{
  vvTokenizer* tokenizer;                         // ASCII file tokenizer
  vvTokenizer::TokenType ttype;                   // currently processed token type
  FILE* fp;                                       // volume file pointer
  uchar* raw;                                     // raw volume data
  int ival=0;                                     // integer data value
  int f;                                          // counter for frames
  int i, x, y, z, c;                              // counters
  int frameSize;                                  // size of a frame in bytes
  int identifier;                                 // ID of string identifier in file header
  bool done;
  bool error;

  vvDebugMsg::msg(1, "vvFileIO::loadAVFFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;

  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  // Delete previous volume sequence
  vd->removeSequence();

  // Set default values:
  setDefaultValues(vd);

  // Read header data:
  tokenizer = new vvTokenizer(fp);
  tokenizer->setCommentCharacter('#');
  tokenizer->setEOLisSignificant(false);
  tokenizer->setCaseConversion(vvTokenizer::VV_UPPER);
  tokenizer->setParseNumbers(true);
  tokenizer->setWhitespaceCharacter('=');
  done = error = false;
  while (!done)
  {
    // Read identifier:
    ttype = tokenizer->nextToken();
    if (ttype != vvTokenizer::VV_WORD)
    {
      done = true;
      tokenizer->pushBack();
      continue;
    }
    else if (strcmp(tokenizer->sval, "WIDTH")==0)
      identifier = 0;
    else if (strcmp(tokenizer->sval, "HEIGHT")==0)
      identifier = 1;
    else if (strcmp(tokenizer->sval, "SLICES")==0)
      identifier = 2;
    else if (strcmp(tokenizer->sval, "FRAMES")==0)
      identifier = 3;
    else if (strcmp(tokenizer->sval, "MIN")==0)
      identifier = 4;
    else if (strcmp(tokenizer->sval, "MAX")==0)
      identifier = 5;
    else if (strcmp(tokenizer->sval, "XDIST")==0)
      identifier = 6;
    else if (strcmp(tokenizer->sval, "YDIST")==0)
      identifier = 7;
    else if (strcmp(tokenizer->sval, "ZDIST")==0)
      identifier = 8;
    else if (strcmp(tokenizer->sval, "TIME")==0)
      identifier = 9;
    else if (strcmp(tokenizer->sval, "BPC")==0)
      identifier = 10;
    else if (strcmp(tokenizer->sval, "CHANNELS")==0)
      identifier = 11;
    else if (strcmp(tokenizer->sval, "XPOS")==0)
      identifier = 12;
    else if (strcmp(tokenizer->sval, "YPOS")==0)
      identifier = 13;
    else if (strcmp(tokenizer->sval, "ZPOS")==0)
      identifier = 14;
    else
    {
      done = error = true;
      continue;
    }

    // Read assigned value:
    ttype = tokenizer->nextToken();
    if (ttype == vvTokenizer::VV_NUMBER)
    {
      switch (identifier)
      {
        case  0: vd->vox[0]  = int(tokenizer->nval); break;
        case  1: vd->vox[1]  = int(tokenizer->nval); break;
        case  2: vd->vox[2]  = int(tokenizer->nval); break;
        case  3: vd->frames  = int(tokenizer->nval); break;
        case  4: vd->real[0] = tokenizer->nval; break;
        case  5: vd->real[1] = tokenizer->nval; break;
        case  6: vd->dist[0] = tokenizer->nval; break;
        case  7: vd->dist[1] = tokenizer->nval; break;
        case  8: vd->dist[2] = tokenizer->nval; break;
        case  9: vd->dt      = tokenizer->nval; break;
        case 10: vd->bpc     = int(tokenizer->nval); break;
        case 11: vd->chan    = int(tokenizer->nval); break;
        case 12: vd->pos[0]  = tokenizer->nval; break;
        case 13: vd->pos[1]  = tokenizer->nval; break;
        case 14: vd->pos[2]  = tokenizer->nval; break;
        default: break;
      }
    }
    else error = done = true;
  }
  if (error)
  {
    cerr << "Read error in line " << tokenizer->getLineNumber() << " of file " <<
      vd->getFilename() << endl;
    delete tokenizer;
    fclose(fp);
    return DATA_ERROR;
  }

  // Check for consistence:
  if (vd->vox[0]<=0 || vd->vox[1]<=0 || vd->vox[2]<=0 ||
    vd->frames<=0 || vd->real[0]>=vd->real[1])
  {
    vvDebugMsg::msg(1, "Error: Invalid file information in header");
    delete tokenizer;
    fclose(fp);
    return DATA_ERROR;
  }

  // Load voxel data:
  frameSize = vd->getFrameBytes();
  if ((_sections & RAW_DATA) != 0)
  {
    for (f=0; f<vd->frames; ++f)
    {
      raw = new uchar[frameSize];                 // create new data space for volume data
      i = 0;
      for (z=0; z<vd->vox[2]; ++z)
      {
        for (y=0; y<vd->vox[1]; ++y)
        {
          for (x=0; x<vd->vox[0]; ++x)
          {
            for (c=0; c<vd->chan; ++c)
            {
              ttype = tokenizer->nextToken();
              if (ttype != vvTokenizer::VV_NUMBER)
              {
                cerr << "Error parsing frame " << f << ", slice " << z << ", line " << y << ", voxel " << x << ", channel " << c << ":" << endl;
                cerr << "Number expected in line " << tokenizer->getLineNumber() << endl;
                delete tokenizer;
                fclose(fp);
                return DATA_ERROR;
              }
              if (vd->bpc==1 || vd->bpc==2)
              {
                ival = int(tokenizer->nval);
                if (ival < 0 || (vd->bpc==1 && ival>255) || (vd->bpc==2 && ival>65535))
                {
                  cerr << "Integer out of range in line " << tokenizer->getLineNumber() << endl;
                  delete tokenizer;
                  fclose(fp);
                  return DATA_ERROR;
                }
              }
              switch(vd->bpc)
              {
                case 1:
                  raw[i++] = uchar(ival);
                  break;
                case 2:
                  raw[i++] = uchar(ival >> 8);
                  raw[i++] = uchar(ival & 0xFF);
                  break;
                case 4:
                  *((float*)raw) = tokenizer->nval;
                  i += 3;
                  break;
              }
            }
          }
        }
      }
      vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
    }
  }

  // Clean up:
  delete tokenizer;
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for xb7 files. These files are used in SFB 382, project C15 (Prof.
  Herrmann, Stefan Luding, Stefan Miller, Sean McNamara).<br>
  xb7-Format:<br>
  Series of snapshots at times t in blocks of N+1 lines each (header + N particles)
  with 8 numbers, separated by whitespace.<p>

  line 1:  N  t  x1 y1 z1  x2 y2 z2 <br>
  line 2 - line N+1:  x y z  vx vy vz  r  i <p>

  N: number of particles<br>
  t: time<br>
x1/y1/z1-x2/y2/z2  size of simulation box (hier x1=y1=z1=0, x2=y2=z2)<br>
x/y/z: coordinates (particle center is within box, periodic boundaries)<br>
vx/vy/vz: speed<br>
r: diameter<br>
i: collision frequency<p>

An example file with 3 particles is:
<pre>
3 40.9594 0 0 0 0.102355 0.102355 0.102355
0.0914023 0.0886842 0.0880599	-0.000187777 -4.58716e-05 -0.000202447	0.0005	219
0.0183272 0.0727637 0.0348822	4.57354e-05 -0.000339601 0.000512404	0.0005	259
0.0955405 0.0885498 0.00429593	-0.000176341 -0.000405909 -0.000278665	0.0005	1487
</pre>
For time dependent data, the voxel grid is sized to the simulation box of the first
time step. In subsequent time steps particles which are not within the first box will
be discarded. Also, the value range fitting will be done only with the particles of the
first time step.
@param vd volume description
@param maxEdgeLength volume will be shaped like the simulation box with this maximum edge length [voxels]
@param densityParam parameter to use for voxel density: 0=x, 1=y, 2=z, 3=vx, 4=vy, 5=vz, 6=r, 7=i, 8=sqrt(vx*vx+vy*vy+vz*vz)
@param useGlobalMinMax true if min and max scalar values should be determined globally, false for within time steps only
*/
vvFileIO::ErrorType vvFileIO::loadXB7File(vvVolDesc* vd, int maxEdgeLength, int densityParam, bool useGlobalMinMax)
{
  vvTokenizer* tokenizer;                         // ASCII file tokenizer
  vvTokenizer::TokenType ttype;                   // currently processed token type
  vvSLList<ParticleTimestep*> timesteps;          // particle storage for all time steps
  FILE* fp;                                       // volume file pointer
  uchar* raw;                                     // raw volume data
  float boxMin[3];                                // simulation box min values
  float boxMax[3];                                // simulation box max values
  float boxSize[3];                               // simulation box size
  float maxBoxSize;                               // maximum box size
  float globalMin,globalMax;                      // real min and max values over all time steps
  float minVal,maxVal;                            // real min and max values
  float param[9];                                 // atom parameters of one line
  float val;                                      // particle value
  int numParticles=0;                             // number of particles in current time step
  int numTimesteps;                               // number of time steps in file
  int frameSize;                                  // number of bytes per frame
  int iVal;                                       // integer density
  int iPos[3];                                    // position of particle in volume
  int index;
  int i,j,t;
  bool error = false;

  vvDebugMsg::msg(1, "vvFileIO::loadXB7File()");

  if (vvToolshed::isFile(vd->getFilename())==false)
    return FILE_NOT_FOUND;

  if ((fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  cerr << "Reading XB7 file: edge length=" << maxEdgeLength << ", density parameter=" << densityParam <<
    ", global min/max=" << useGlobalMinMax << endl;

  // Delete previous volume sequence
  vd->removeSequence();

  // Set default values:
  vd->bpc = 2;
  vd->chan = 1;
  vd->dt = 0.1f;

  // Initialize tokenizer:
  tokenizer = new vvTokenizer(fp);
  tokenizer->setEOLisSignificant(true);
  tokenizer->setCaseConversion(vvTokenizer::VV_LOWER);
  tokenizer->setParseNumbers(true);

  // Read all time step data but don't create volume yet:
  for (i=0; i<3; ++i)
  {
    boxMin[i] =  VV_FLT_MAX;
    boxMax[i] = -VV_FLT_MAX;
  }
  globalMin =  VV_FLT_MAX;
  globalMax = -VV_FLT_MAX;
  for(;;)
  {
    // Parse header:
    for(i=0; i<8; ++i)
    {
      // Read identifier:
      ttype = tokenizer->nextToken();
      if (ttype != vvTokenizer::VV_NUMBER)
      {
        error = true;
        break;
      }
      switch (i)
      {
        case 0: numParticles = int(tokenizer->nval); break;
        case 1: break;                            // ignore time value
        case 2: if (tokenizer->nval < boxMin[0]) boxMin[0] = tokenizer->nval; break;
        case 3: if (tokenizer->nval < boxMin[1]) boxMin[1] = tokenizer->nval; break;
        case 4: if (tokenizer->nval < boxMin[2]) boxMin[2] = tokenizer->nval; break;
        case 5: if (tokenizer->nval > boxMax[0]) boxMax[0] = tokenizer->nval; break;
        case 6: if (tokenizer->nval > boxMax[1]) boxMax[1] = tokenizer->nval; break;
        case 7: if (tokenizer->nval > boxMax[2]) boxMax[2] = tokenizer->nval; break;
        default: break;
      }
    }
    ttype = tokenizer->nextToken();
    if (ttype!=vvTokenizer::VV_EOL) error = true;
    if (error)
    {
      cerr << "Parse error in line " << tokenizer->getLineNumber() << endl;
      delete tokenizer;
      timesteps.removeAll();
      fclose(fp);
      return DATA_ERROR;
    }

    // Create new time step:
    timesteps.append(new ParticleTimestep(numParticles), vvSLNode<ParticleTimestep*>::NORMAL_DELETE);
    cerr << "Reading " << numParticles << " particles" << endl;

    timesteps.getData()->min = VV_FLT_MAX;
    timesteps.getData()->max = -VV_FLT_MAX;

    // Load particles, but don't create the volume yet:
    for (i=0; i<numParticles; ++i)
    {
      // Read an entire line including new line character:
      for (j=0; j<9; ++j)
      {
        if (i==numParticles-1 && j==8) continue;  // ignore last EOL
        ttype = tokenizer->nextToken();
        if ((j<8 && ttype != vvTokenizer::VV_NUMBER) || (j==8 && ttype!=vvTokenizer::VV_EOL))
        {
          cerr << "Parse error in line " << tokenizer->getLineNumber() << endl;
          delete tokenizer;
          timesteps.removeAll();
          fclose(fp);
          return DATA_ERROR;
        }
        param[j] = tokenizer->nval;
      }
      for (j=0; j<3; ++j)                         // memorize particle position
      {
        timesteps.getData()->pos[j][i] = param[j];
      }
      if (densityParam<8)                         // memorize particle value
      {
        val = param[densityParam];
      }
      else                                        // speed
      {
        val = sqrtf(param[3] * param[3] + param[4] * param[4] + param[5] * param[5]);
      }
      timesteps.getData()->val[i] = val;
      if (val < timesteps.getData()->min) timesteps.getData()->min = val;
      if (val > timesteps.getData()->max) timesteps.getData()->max = val;
    }
    cerr << "Timestep: scalar min,max: " << timesteps.getData()->min << "," << timesteps.getData()->max << endl;
    if (timesteps.getData()->min < globalMin) globalMin = timesteps.getData()->min;
    if (timesteps.getData()->max > globalMax) globalMax = timesteps.getData()->max;

    // Look for another time step:
    do
    {
      ttype = tokenizer->nextToken();
    } while (ttype != vvTokenizer::VV_EOF && ttype != vvTokenizer::VV_NUMBER);
    if (ttype==vvTokenizer::VV_EOF) break;
    else tokenizer->pushBack();
  }
  delete tokenizer;
  fclose(fp);
  numTimesteps = timesteps.count();
  cerr << numTimesteps << " time steps read" << endl;
  cerr << "Global min,max: " << globalMin << "," << globalMax << endl;

  // Check for consistency:
  if (boxMin[0] >= boxMax[0] || boxMin[1] >= boxMax[1] || boxMin[2] >= boxMax[2])
  {
    cerr << "Error: invalid box size in header: " << boxMin[0] << " " << boxMin[1] << " " << boxMin[2] <<
      " " << boxMax[0] << " " << boxMax[1] << " " << boxMax[2] << endl;
    timesteps.removeAll();
    return DATA_ERROR;
  }
  vd->real[0] = globalMin;
  vd->real[1] = globalMax;

  // Now that all particles are read from all time steps, the volumes can be generated.

  // Compute header values:
  for(i=0; i<3; ++i)
  {
    boxSize[i] = boxMax[i] - boxMin[i];
  }
  maxBoxSize = ts_max(boxSize[0], boxSize[1], boxSize[2]);
  for(i=0; i<3; ++i)
  {
    vd->vox[i] = int(float(maxEdgeLength) * boxSize[i] / maxBoxSize);
    vd->vox[i] = ts_clamp(vd->vox[i], 1, maxEdgeLength);
  }
  for (i=0; i<3; ++i)
  {
    vd->dist[i] = boxSize[i] / float(vd->vox[i]);
  }

  frameSize = vd->getFrameBytes();
  timesteps.first();
  for(t=0; t<numTimesteps; ++t)
  {
    raw = new uchar[frameSize];
    assert(raw);
    memset(raw, 0, frameSize);
    numParticles = timesteps.getData()->numParticles;

    for (i=0; i<numParticles; ++i)
    {
      for (j=0; j<3; ++j)
      {
        iPos[j] = int(float(vd->vox[j] - 1) * (timesteps.getData()->pos[j][i] - boxMin[j]) / (boxMax[j] - boxMin[j]));
        iPos[j] = ts_clamp(iPos[j], 0, vd->vox[j] - 1);
      }
      // Allow values from 1 to MAX_16BIT:
      if (useGlobalMinMax)
      {
        minVal = globalMin;
        maxVal = globalMax;
      }
      else
      {
        minVal = timesteps.getData()->min;
        maxVal = timesteps.getData()->max;
      }
      if (maxVal > minVal) iVal = int(65534.0f * (timesteps.getData()->val[i] - minVal) / (maxVal - minVal)) + 1;
      else iVal = 65535;
      iVal = ts_clamp(iVal, 1, 65535);
      index = 2 * (iPos[0] + iPos[1] * vd->vox[0] + iPos[2] * vd->vox[0] * vd->vox[1]);
      raw[index] = uchar(iVal >> 8);
      raw[index + 1] = uchar(iVal & 0xff);
    }
    vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
    timesteps.next();
  }

  vd->frames = vd->getStoredFrames();
  assert(vd->frames == timesteps.count());
  timesteps.removeAll();
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for IMD checkpoint files with particle data.
  These files are used at ITAP in SFB 382, project C14
  (Prof. Trebin, Gunther Schaaf, Franz Gaehler, Silvia Hocker).<br>
  cpt-Format:<br>
  This is an example file with three atoms:
  <PRE>
  #F A 1 1 1 3 3 1
  #C number type mass x y z vx vy vz Epot
  #X      3.0766609395000000e+02 0.0000000000000000e+00 0.0000000000000000e+00
  #Y      0.0000000000000000e+00 1.0442535916900000e+02 0.0000000000000000e+00
  #Z      0.0000000000000000e+00 0.0000000000000000e+00 1.4357751050999990e+01
## Generated by /hwwt3e/rus/ita/pof30/bin/cray-t3e/imd_mpi_nve_stress_ordpar_efilter on Thu Jun 27 23:56:46 2002
#E
18368 0     1.000000    12.311201    48.337746     1.031030    -0.032552     0.047432    -0.014428   -17.623187
18800 0     1.000000    12.310159    48.341527     3.080848    -0.024594     0.040695    -0.009033   -17.630691
15772 1     1.000000    10.766565    47.946747     2.054420    -0.009312    -0.063240    -0.027128   -21.059210
</PRE>
For time dependent data, multiple files are stored on disk with increasing filename numbers,
e.g., timestep001.cpt, timestep002.cpt, etc.
@param vd volume description
@param maxEdgeLength volume will be shaped like the simulation box with this maximum edge length [voxels]
@param densityParam value index to use for voxel density, use -1 for speed (vx*vx+vy*vy+vz*vz)
@param useGlobalMinMax true if min and max scalar values should be determined globally, false for within time steps only
*/
vvFileIO::ErrorType vvFileIO::loadCPTFile(vvVolDesc* vd, int maxEdgeLength, int densityParam, bool useGlobalMinMax)
{
  vvTokenizer* tokenizer;                         // ASCII file tokenizer
  vvTokenizer::TokenType ttype;                   // currently processed token type
  vvSLList<ParticleTimestep*> timesteps;          // particle storage for all time steps
  vvArray<float> particles;                       // densities of current time step
  vvArray<float> xpos;                            // x positions of current time step
  vvArray<float> ypos;                            // y positions of current time step
  vvArray<float> zpos;                            // z positions of current time step
  FILE* fp;                                       // volume file pointer
  uchar* raw;                                     // raw volume data
  char* filename;                                 // current particles file name
  float boxMin[3];                                // simulation box min values
  float boxMax[3];                                // simulation box max values
  float boxSize[3];                               // simulation box size
  float maxBoxSize;                               // maximum box size
  float globalMin,globalMax;                      // density min and max values over all time steps
  float minVal,maxVal;                            // density min and max values of current time step
  float val = 0.f;                                // particle value
  int numParticles=0;                             // number of particles in current time step
  int numTimesteps;                               // number of time steps in file
  int frameSize;                                  // number of bytes per frame
  int iVal;                                       // integer density
  int iPos[3];                                    // position of particle in volume
  int index;
  int i,j,t;
  float speed[3];

  vvDebugMsg::msg(1, "vvFileIO::loadCPTFile()");

  filename = new char[strlen(vd->getFilename()) + 1];
  strcpy(filename, vd->getFilename());

  if (vvToolshed::isFile(filename)==false)
    return FILE_NOT_FOUND;

  cerr << "Checkpoint reader parameters: edge length=" << maxEdgeLength << ", density parameter=" << densityParam <<
    ", global min/max=" << useGlobalMinMax << endl;

  // Delete previous volume sequence
  vd->removeSequence();

  // Set default values:
  vd->bpc = 2;
  vd->chan = 1;
  vd->dt = 0.1f;

  // Initialize variables:
  for (i=0; i<3; ++i)
  {
    boxMin[i] =  VV_FLT_MAX;
    boxMax[i] = -VV_FLT_MAX;
  }
  globalMin =  VV_FLT_MAX;
  globalMax = -VV_FLT_MAX;

  // Loop thru time steps:
  for(;;)
  {
    if ((fp = fopen(filename, "rb")) == NULL)
    {
      vvDebugMsg::msg(1, "Error: Cannot open file: ", filename);
      return FILE_ERROR;
    }

    // Initialize tokenizer:
    tokenizer = new vvTokenizer(fp);
    tokenizer->setEOLisSignificant(true);
    tokenizer->setCaseConversion(vvTokenizer::VV_LOWER);
    tokenizer->setParseNumbers(true);
    tokenizer->setCommentCharacter('#');

    particles.clear();
    xpos.clear();
    ypos.clear();
    zpos.clear();
    minVal =  VV_FLT_MAX;
    maxVal = -VV_FLT_MAX;
    val = 0.f;

    // Load particles, but don't create the volume yet:
    for (;;)                                      // loop thru particles in one time step
    {
      // Read an entire line of numbers:
      for(i=0; tokenizer->nextToken() == vvTokenizer::VV_NUMBER; ++i)
      {
        // Memorize position and adjust simulation box:
        if (i>=0 && i<=2)
        {
          if (i==0) xpos.append(tokenizer->nval);
          else if (i==1) ypos.append(tokenizer->nval);
          else if (i==2) zpos.append(tokenizer->nval);
          if (tokenizer->nval < boxMin[i]) boxMin[i] = tokenizer->nval;
          if (tokenizer->nval > boxMax[i]) boxMax[i] = tokenizer->nval;
        }

        // Memorize density value:
        if (densityParam==-1)
        {
          if (i>=3 && i<=5) speed[i-3] = tokenizer->nval;
          if (i==5) val = sqrtf(speed[0] * speed[0] + speed[1] * speed[1] + speed[2] * speed[2]);
        }
        else if (i==densityParam) val = tokenizer->nval;
      }
      particles.append(val);
      if (val < minVal) minVal = val;
      if (val > maxVal) maxVal = val;

      // Look for another particle:
      do
      {
        ttype = tokenizer->nextToken();
      } while (ttype != vvTokenizer::VV_EOF && ttype != vvTokenizer::VV_NUMBER);
      if (ttype==vvTokenizer::VV_EOF) break;
      else tokenizer->pushBack();
    }
    delete tokenizer;
    fclose(fp);
    cerr << "Timestep: scalar min,max: " << minVal << "," << maxVal << endl;
    if (minVal < globalMin) globalMin = minVal;
    if (maxVal > globalMax) globalMax = maxVal;

    // Create new time step and copy data to it:
    timesteps.append(new ParticleTimestep(particles.count()), vvSLNode<ParticleTimestep*>::NORMAL_DELETE);
    timesteps.getData()->max = maxVal;
    timesteps.getData()->min = minVal;
    memcpy(timesteps.getData()->val, particles.getArrayPtr(), particles.count());
    memcpy(timesteps.getData()->pos[0], xpos.getArrayPtr(), xpos.count());
    memcpy(timesteps.getData()->pos[1], ypos.getArrayPtr(), ypos.count());
    memcpy(timesteps.getData()->pos[2], zpos.getArrayPtr(), zpos.count());

    // Look for another time step:
    if (!vvToolshed::increaseFilename(filename)) break;
    if (vvToolshed::isFile(filename)==false) break;
  }
  numTimesteps = timesteps.count();
  cerr << numTimesteps << " time steps read" << endl;
  cerr << "Global: scalar min,max: " << globalMin << "," << globalMax << endl;

  vd->real[0] = globalMin;
  vd->real[1] = globalMax;

  // Now that all particles are read from all time steps, the volumes can be generated.

  // Compute header values:
  for(i=0; i<3; ++i)
  {
    boxSize[i] = boxMax[i] - boxMin[i];
  }
  maxBoxSize = ts_max(boxSize[0], boxSize[1], boxSize[2]);
  for(i=0; i<3; ++i)
  {
    vd->vox[i] = int(float(maxEdgeLength) * boxSize[i] / maxBoxSize);
    vd->vox[i] = ts_clamp(vd->vox[i], 1, maxEdgeLength);
  }
  for (i=0; i<3; ++i)
  {
    vd->dist[i] = boxSize[i] / float(vd->vox[i]);
  }

  frameSize = vd->getFrameBytes();
  timesteps.first();
  for(t=0; t<numTimesteps; ++t)
  {
    raw = new uchar[frameSize];
    assert(raw);
    memset(raw, 0, frameSize);
    numParticles = timesteps.getData()->numParticles;

    for (i=0; i<numParticles; ++i)
    {
      for (j=0; j<3; ++j)
      {
        iPos[j] = int(float(vd->vox[j] - 1) * (timesteps.getData()->pos[j][i] - boxMin[j]) / (boxMax[j] - boxMin[j]));
        iPos[j] = ts_clamp(iPos[j], 0, vd->vox[j] - 1);
      }
      // Allow values from 1 to MAX_16BIT:
      if (useGlobalMinMax)
      {
        minVal = globalMin;
        maxVal = globalMax;
      }
      else
      {
        minVal = timesteps.getData()->min;
        maxVal = timesteps.getData()->max;
      }
      if (maxVal > minVal) iVal = int(65534.0f * (timesteps.getData()->val[i] - minVal) / (maxVal - minVal)) + 1;
      else iVal = 65535;
      iVal = ts_clamp(iVal, 1, 65535);
      index = 2 * (iPos[0] + iPos[1] * vd->vox[0] + iPos[2] * vd->vox[0] * vd->vox[1]);
      raw[index] = uchar(iVal >> 8);
      raw[index + 1] = uchar(iVal & 0xff);
    }
    vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
    timesteps.next();
  }

  vd->frames = vd->getStoredFrames();
  assert(vd->frames == timesteps.count());
  timesteps.removeAll();
  delete[] filename;
  return OK;
}

//----------------------------------------------------------------------------
/// Loader for voxel file in tif (Tagged Image File) format.
vvFileIO::ErrorType vvFileIO::loadTIFFile(vvVolDesc* vd, bool addFrames)
{
  const ushort BIG_ENDIAN_ID  = 0x4d4d;           // TIF endianness for big-endian (Unix) style
  const ushort LITTLE_ENDIAN_ID = 0x4949;         // TIF endianness for little-endian (Intel) style
  const ushort MAGICNUMBER = 42;                  // TIF magic number
  vvToolshed::EndianType endian;                  // file endianness
  FILE* fp;                                       // volume file pointer
  ushort endianID, magicID;                       // file format test values
  ulong ifdpos;                                   // position of first IFD
  int numEntries;                                 // number of entries in IFD
  int i;                                          // counter
  ushort tag;                                     // IFD-tag
  ushort dataType;                                // IFD data type: 1=8bit uint, 2=8bit ASCII, 3=16bit uint, 4=32bit uint, 5=64bit fixed point
  int    numValues;                               // IFD: number of data values
  int    value;                                   // IFD data value or offset
  int    nextIFD;                                 // pointer to next IFD
  ushort tileWidth=0;                             // tile width in voxels
  ushort tileHeight=0;                            // tile height in voxels
  ulong  tileOffset=0;                            // tile offset in file
  ulong* tilePos;                                 // array of tile positions
  int    numTiles=0;                              // total number of tiles in file
  int    numTilesX;                               // number of tiles horizontally
  int    numTilesY;                               // number of tiles vertically
  int    tpx, tpy, tpz;                           // tile starting position in volume data space
  int    y;                                       // counter for voxel lines
  int    offset;                                  // volume data offset to first byte of tile
  ErrorType err = OK;                             // error
  uchar* raw;                                     // raw volume data
  int    rawOffset;                               // offset into raw data
  int*   stripOffsets=NULL;                       // array of strip offsets
  int*   stripByteCounts=NULL;                    // bytes per strip
  int    rowsPerStrip=0;                          // rows per strip
  int planarConfiguration = 1;                    // 1=RGBRGB, 2=RRGGBB
  int where;                                      // current position in file
  int strips=1;                                   // number of strips
  int readBytes;                                  // number of bytes read
  int bytesToRead;                                // bytes left to read from file
  int ifd;                                        // current IFD ID

  vvDebugMsg::msg(1, "vvFileIO::loadTIFFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;
  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  // Check file format:
  endianID = vvToolshed::read16(fp);
  if      (endianID == BIG_ENDIAN_ID)    endian = vvToolshed::VV_BIG_END;
  else if (endianID == LITTLE_ENDIAN_ID) endian = vvToolshed::VV_LITTLE_END;
  else
  {
    fclose(fp);
    cerr << "TIFF: wrong header ID" << endl;
    return FORMAT_ERROR;
  }
  magicID = vvToolshed::read16(fp, endian);
  if (magicID != MAGICNUMBER)
  {
    fclose(fp);
    cerr << "TIFF: wrong magic number" << endl;
    return FORMAT_ERROR;
  }

  if(!addFrames)
  {
    vd->removeSequence();
    vd->vox[0] = vd->vox[1] = 0;
    vd->vox[2] = 1;
    vd->frames = 0;
    vd->bpc = 1;
    vd->chan = 1;
  }

  // Find and process first IFD:
  ifdpos = vvToolshed::read32(fp, endian);
  fseek(fp, ifdpos, SEEK_SET);
  numEntries = vvToolshed::read16(fp, endian);

  vvDebugMsg::msg(2, "TIFF IFD Tags: ", numEntries);
  for (ifd=0; ifd<numEntries; ++ifd)              // process all IFD entries
  {
    tag       = vvToolshed::read16(fp, endian);
    dataType  = vvToolshed::read16(fp, endian);
    numValues = vvToolshed::read32(fp, endian);
    value     = vvToolshed::read32(fp, endian);

                                                  // 16 bit values are left aligned
	if (endian==vvToolshed::VV_BIG_END && dataType==3 && tag!=0x102) value = value >> 16;

    if (vvDebugMsg::isActive(2))
    {
      cerr << "Tag: " << hex << setw(4) << tag << ", Data Type: " << dataType <<
        ", Data Entries: " << setw(3) << numValues << ", Value: " << setw(8)
        << value << dec << endl;
    }

    switch (tag)
    {
      case 0x0FE: break;                          // NewSubfileType
      case 0x100: vd->vox[0] = value; break;      // ImageWidth
      case 0x101: vd->vox[1] = value; break;      // ImageLength
                                                  // BitsPerSample (=bits per channel)
      case 0x102: if (numValues==1) vd->bpc = value / 8;
      else
      {
        where = ftell(fp);
        fseek(fp, value, SEEK_SET);
        int bitsPerSample = 0;
        for (i=0; i<numValues; ++i)
        {
          if (dataType==4) bitsPerSample = vvToolshed::read32(fp, endian);
          else if (dataType==3) bitsPerSample = vvToolshed::read16(fp, endian);
          else assert(0);
          if (i==0) vd->bpc = bitsPerSample / 8;
          else if (vd->bpc != bitsPerSample / 8)
          {
            cerr << "Error: TIFF reader needs same number of bits for each sample." << endl;
            fclose(fp);
            delete[] stripOffsets;
            delete[] stripByteCounts;
            return DATA_ERROR;
          }
        }
        fseek(fp, where, SEEK_SET);
      }
      break;
      case 0x103: if (value != 1)
      {
        err = DATA_ERROR;
        cerr << "Cannot read compressed TIFF." << endl;
      }
      break;                                      // Compression; must be uncompressed
      case 0x106: break;                          // PhotometricInterpretation; ignore
      case 0x111: delete[] stripOffsets;          // StripOffsets
      stripOffsets = new int[numValues];
      if (numValues==1) stripOffsets[0] = value;
      else
      {
        where = ftell(fp);
        fseek(fp, value, SEEK_SET);
        for (i=0; i<numValues; ++i)
        {
          if (dataType==4) stripOffsets[i] = vvToolshed::read32(fp, endian);
          else if (dataType==3) stripOffsets[i] = vvToolshed::read16(fp, endian);
          else assert(0);
        }
        fseek(fp, where, SEEK_SET);
      }
      break;
      case 0x115: vd->chan = value;               // SamplesPerPixel (=channels)
      break;
      case 0x116: rowsPerStrip = value; break;    // RowsPerStrip
      case 0x117: delete[] stripByteCounts;       // StripByteCounts
      stripByteCounts = new int[numValues];
      if (numValues==1) stripByteCounts[0] = value;
      else
      {
        where = ftell(fp);
        fseek(fp, value, SEEK_SET);
        for (i=0; i<numValues; ++i)
        {
          if (dataType==4) stripByteCounts[i] = vvToolshed::read32(fp, endian);
          else if (dataType==3) stripByteCounts[i] = vvToolshed::read16(fp, endian);
          else assert(0);
        }
        fseek(fp, where, SEEK_SET);
      }
      break;
                                                  // PlanarConfiguration
      case 0x11c: planarConfiguration = value; break;
      case 0x142: tileWidth  = (ushort)value; break;
      case 0x143: tileHeight = (ushort)value; break;
      case 0x144: numTiles = numValues; tileOffset = value; break;
      case 0x80e5: vd->vox[2] = value; break;
      default: break;
    }
  }

  strips = int(ceilf(float(vd->vox[1]) / float(rowsPerStrip)));

  nextIFD = vvToolshed::read32(fp, endian);       // check for further IFDs
  if (nextIFD==0) vvDebugMsg::msg(3, "No more IFDs in file.");
  else vvDebugMsg::msg(1, "There are more IFDs in the file.");

  if (vd->getFrameBytes()==0 || err!=OK)          // check for plausibility
  {
    cerr << "Error: Invalid volume dimensions or file error." << endl;
    fclose(fp);
    delete[] stripOffsets;
    delete[] stripByteCounts;
    return DATA_ERROR;
  }

  // Allocate memory for volume data:
  raw = new uchar[vd->getFrameBytes()];

  if (stripOffsets[0]>0)                          // load 2D TIFF?
  {
    rawOffset = 0;
    for (i=0; i<strips; ++i)
    {
      fseek(fp, stripOffsets[i], SEEK_SET);
      bytesToRead = ((planarConfiguration == 2) ? 3 : 1) * stripByteCounts[i];
      readBytes = int(fread(raw+rawOffset, 1, bytesToRead, fp));
      if (readBytes != bytesToRead)
      {
        cerr << "Error: reached end of TIFF file while reading." << endl;
        fclose(fp);
        delete[] raw;
        delete[] stripOffsets;
        delete[] stripByteCounts;
        return DATA_ERROR;
      }
      else rawOffset += bytesToRead;
    }
    vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
    vd->frames++;
    if (planarConfiguration==2) vd->convertRGBPlanarToRGBInterleaved();
    if (vd->chan==4 && !vd->isChannelUsed(3))     // is alpha not used in a RGBA volume?
    {
      // Preset alpha:
      vd->convertChannels(3, vd->frames-1);       // convert to RGB (drops alpha)
      vd->convertChannels(4, vd->frames-1);       // convert back to RGBA
    }
  }
  else                                            // load 3D TIFF
  {
    // Load tile offsets:
    tilePos = new ulong[numTiles];
    fseek(fp, tileOffset, SEEK_SET);
    for (i=0; i<(int)numTiles; ++i)
    {
      tilePos[i] = vvToolshed::read32(fp, endian);
    }

    // Compute tiles distribution (in z direction there are as many tiles as slices):
    numTilesX = int((double)vd->vox[0] / (double)tileWidth) + 1;
    numTilesY = int((double)vd->vox[1] / (double)tileHeight) + 1;

    // Load volume data:
    for (i=0; i<(int)numTiles; ++i)
    {
      fseek(fp, tilePos[i], SEEK_SET);
      tpx = i % numTilesX;
      tpy = (i / numTilesX) % numTilesY;
      tpz = i / (numTilesX * numTilesY);
      offset = tpx * tileWidth + tpy * tileHeight * vd->vox[0] + tpz * vd->getSliceBytes();
      for (y=0; y<tileHeight; ++y)
      {
        if (fread(raw + offset + y * vd->vox[0], 1, tileWidth, fp) != tileWidth)
        {
          cerr << "Error: TIFF file too short for volume size." << endl;
          fclose(fp);
          delete[] raw;
          delete[] stripOffsets;
          delete[] stripByteCounts;
          return DATA_ERROR;
        }
      }
    }
    delete[] tilePos;
    vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
    vd->frames++;
  }
  fclose(fp);
  delete[] stripOffsets;
  delete[] stripByteCounts;
  return OK;
}

//----------------------------------------------------------------------------
/** Writer for TIFF (Tagged Image File) format. Creates one TIFF image for
  each volume slice. Up to three channels can be saved, if there are more
  the ones above three will be ignored.
  @param vd volume descriptor to save
  @param overwrite true = overwrite existing files
*/
vvFileIO::ErrorType vvFileIO::saveTIFSlices(vvVolDesc* vd, bool overwrite)
{
  const ushort LITTLE_ENDIAN_ID = 0x4949;         // TIF endianness for little-endian (Intel) style
  const ushort BIG_ENDIAN_ID  = 0x4d4d;           // TIF endianness for big-endian (Unix) style
  const ushort MAGICNUMBER = 42;                  // TIF magic number
  const int DEFAULT_DPI = 72;                     // DPI stored with image
                                                  // endianness
  const vvToolshed::EndianType ENDIAN_TYPE = vvToolshed::VV_BIG_END;
  FILE* fp;
  vvVolDesc* tmpVD;                               // temporary VD to modify pixel format
  ErrorType err = OK;
  int digits;                                     // number of digits used for file numbering
  char** filenames;                               // list of filenames
  int len;                                        // filename length
  int i;
  int sliceSize;
  char buffer[1024];
  int imgBytes;                                   // bytes in TIFF image
  int imgOffset;                                  // offset of image data
  int ifdOffset;                                  // offset of IFD in file
  int rgbOffset;                                  // offset of RGB bit depths
  int dpiOffset;                                  // offset of DPI value

  vvDebugMsg::msg(1, "vvFileIO::saveTIFSlices()");

  assert(vd->frames>0 && vd->vox[2]>0);

  // Generate file names:
  digits = 1 + int(log((double)vd->vox[2]) / log(10.0));
  filenames = new char*[vd->vox[2]];
  len = strlen(vd->getFilename());
  for (i=0; i<vd->vox[2]; ++i)
  {
    filenames[i] = new char[len + digits + 2];    // add 2 for '-' and '\0'
    vvToolshed::extractDirname(buffer, vd->getFilename());
    strcpy(filenames[i], buffer);
    vvToolshed::extractBasename(buffer, vd->getFilename());
    strcat(filenames[i], buffer);
    if (vd->vox[2] > 1)
    {
      sprintf(buffer, "-%0*d.tif", digits, i);
      strcat(filenames[i], buffer);
    }
    else strcat(filenames[i], ".tif");
  }

  // Normalize voxel data:
  tmpVD = new vvVolDesc(vd, 0);                   // create temporary VD with first frame only
  if (tmpVD->chan != 1) tmpVD->convertChannels(3);
  if (tmpVD->bpc != 1) tmpVD->convertBPC(1);

  // Write files:
  for (i=0; i<tmpVD->vox[2] && err==OK; ++i)
  {
    if (!overwrite)
    {
      if (vvToolshed::isFile(filenames[i]))       // check if file exists
      {
        cerr << "Skipping file (exists): " << filenames[i] << endl;
        continue;
      }
    }

    // Open file to write:
    if ( (fp = fopen(filenames[i], "wb")) == NULL)
    {
      err = FILE_ERROR;
      cerr << "Cannot open file " << filenames[i] << " to write." << endl;
      continue;
    }

    // Write header:
    vvToolshed::write16(fp, (ENDIAN_TYPE==vvToolshed::VV_BIG_END) ? BIG_ENDIAN_ID : LITTLE_ENDIAN_ID, vvToolshed::VV_BIG_END);
    vvToolshed::write16(fp, MAGICNUMBER, vvToolshed::VV_BIG_END);

    // Compute IFD location:
                                                  // grayscale image
    if (tmpVD->chan==1) imgBytes = tmpVD->getSliceVoxels();
    else imgBytes = tmpVD->getSliceVoxels() * 3;  // RGB image
    ifdOffset = imgBytes + 8 + 6 + 8;             // 8 for header; 6 for 8,8,8; 8 for RESOLUTION
                                                  // advance to next word boundary
    if ((ifdOffset % 4) != 0) ifdOffset += 4 - (ifdOffset % 4);
                                                  // IFD location: right after image data
    vvToolshed::write32(fp, ifdOffset, vvToolshed::VV_BIG_END);

    // Write 8,8,8 as SHORT for RGB:
    rgbOffset = ftell(fp);
    vvToolshed::write16(fp, 8, ENDIAN_TYPE);
    vvToolshed::write16(fp, 8, ENDIAN_TYPE);
    vvToolshed::write16(fp, 8, ENDIAN_TYPE);

    // Write dpi:
    dpiOffset = ftell(fp);
    vvToolshed::write32(fp, DEFAULT_DPI, ENDIAN_TYPE);
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);

    // Write image data:
    imgOffset = ftell(fp);
    sliceSize = tmpVD->getSliceBytes();
    if ((int)fwrite(tmpVD->getRaw() + i * sliceSize, sliceSize, 1, fp) != 1)
    {
      cerr << "Error writing file: " << filenames[i] << endl;
      err = FILE_ERROR;
    }

    // Write IFD:
    fseek(fp, ifdOffset, SEEK_SET);
    vvToolshed::write16(fp, ushort((tmpVD->chan==1) ? 11 : 12), vvToolshed::VV_BIG_END);

    // ImageWidth:
    vvToolshed::write16(fp, 0x100, ENDIAN_TYPE);
    vvToolshed::write16(fp, 4, ENDIAN_TYPE);      // LONG
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    vvToolshed::write32(fp, tmpVD->vox[0], ENDIAN_TYPE);

    // ImageLength:
    vvToolshed::write16(fp, 0x101, ENDIAN_TYPE);
    vvToolshed::write16(fp, 4, ENDIAN_TYPE);      // LONG
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    vvToolshed::write32(fp, tmpVD->vox[1], ENDIAN_TYPE);

    // BitsPerSample:
    vvToolshed::write16(fp, 0x102, ENDIAN_TYPE);
    if (tmpVD->chan==1)
    {
      vvToolshed::write16(fp, 3, ENDIAN_TYPE);    // SHORT
      vvToolshed::write32(fp, 1, ENDIAN_TYPE);
      vvToolshed::write16(fp, 8, ENDIAN_TYPE);    // 8 bits per sample
      vvToolshed::write16(fp, 0, ENDIAN_TYPE);    // pad
    }
    else
    {
      vvToolshed::write16(fp, 3, ENDIAN_TYPE);    // SHORT
      vvToolshed::write32(fp, 3, ENDIAN_TYPE);    // 3 numbers required
      vvToolshed::write32(fp, rgbOffset, ENDIAN_TYPE);
    }

    // Compression:
    vvToolshed::write16(fp, 0x103, ENDIAN_TYPE);
    vvToolshed::write16(fp, 3, ENDIAN_TYPE);      // SHORT
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    vvToolshed::write16(fp, 1, ENDIAN_TYPE);      // no compression
    vvToolshed::write16(fp, 0, ENDIAN_TYPE);      // pad

    // PhotometricInterpretation:
    vvToolshed::write16(fp, 0x106, ENDIAN_TYPE);
    vvToolshed::write16(fp, 3, ENDIAN_TYPE);      // SHORT
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    if (tmpVD->chan > 1)
    {
      vvToolshed::write16(fp, 2, ENDIAN_TYPE);    // RGB
    }
    else
    {
      vvToolshed::write16(fp, 1, ENDIAN_TYPE);    // black is zero
    }
    vvToolshed::write16(fp, 0, ENDIAN_TYPE);      // pad

    // StripOffsets:
    vvToolshed::write16(fp, 0x111, ENDIAN_TYPE);
    vvToolshed::write16(fp, 4, ENDIAN_TYPE);      // LONG
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    vvToolshed::write32(fp, imgOffset, ENDIAN_TYPE);

    // SamplesPerPixel:
    if (tmpVD->chan>1)
    {
      vvToolshed::write16(fp, 0x115, ENDIAN_TYPE);
      vvToolshed::write16(fp, 3, ENDIAN_TYPE);    // SHORT
      vvToolshed::write32(fp, 1, ENDIAN_TYPE);
      vvToolshed::write16(fp, 3, ENDIAN_TYPE);    // 3 for RGB
      vvToolshed::write16(fp, 0, ENDIAN_TYPE);    // pad
    }

    // RowsPerStrip:
    vvToolshed::write16(fp, 0x116, ENDIAN_TYPE);
    vvToolshed::write16(fp, 4, ENDIAN_TYPE);      // LONG
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    vvToolshed::write32(fp, tmpVD->vox[1], ENDIAN_TYPE);

    // StripByteCounts:
    vvToolshed::write16(fp, 0x117, ENDIAN_TYPE);
    vvToolshed::write16(fp, 4, ENDIAN_TYPE);      // LONG
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    vvToolshed::write32(fp, tmpVD->vox[0] * tmpVD->vox[1] * tmpVD->chan, ENDIAN_TYPE);

    // XResolution:
    vvToolshed::write16(fp, 0x11a, ENDIAN_TYPE);
    vvToolshed::write16(fp, 5, ENDIAN_TYPE);      // RATIONAL
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    vvToolshed::write32(fp, dpiOffset, ENDIAN_TYPE);

    // YResolution:
    vvToolshed::write16(fp, 0x11b, ENDIAN_TYPE);
    vvToolshed::write16(fp, 5, ENDIAN_TYPE);      // RATIONAL
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    vvToolshed::write32(fp, dpiOffset, ENDIAN_TYPE);

    // ResolutionUnit:
    vvToolshed::write16(fp, 0x128, ENDIAN_TYPE);
    vvToolshed::write16(fp, 3, ENDIAN_TYPE);      // SHORT
    vvToolshed::write32(fp, 1, ENDIAN_TYPE);
    vvToolshed::write16(fp, 2, ENDIAN_TYPE);      // inch
    vvToolshed::write16(fp, 0, ENDIAN_TYPE);      // pad

    // Offset to next IFD:
    vvToolshed::write32(fp, 0, ENDIAN_TYPE);      // end of IFD

    fclose(fp);
  }

  // Free memory:
  for (i=0; i<tmpVD->vox[2]; ++i) delete[] filenames[i];
  delete[] filenames;
  delete tmpVD;

  return err;
}

//----------------------------------------------------------------------------
/// Loads an rgb image file as a one-sliced volume.
vvFileIO::ErrorType vvFileIO::loadRGBFile(vvVolDesc* vd)
{
  const int DIMENSIONS_OFFSET = 6;
  const int DATA_OFFSET = 512;
  FILE* fp;
  uint read;
  uchar* rawData;

  vvDebugMsg::msg(1, "vvFileIO::loadRGBFile()");
  if ( (fp=fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open RGB file.");
    return FILE_ERROR;
  }

  // Check magic number:
  fseek(fp, 0, SEEK_SET);
  if (vvToolshed::read16(fp) != 474)
  {
    vvDebugMsg::msg(1, "Error: Invalid magic number in RGB file.");
    fclose(fp);
    return FORMAT_ERROR;
  }

  // Read dimensions:
  fseek(fp, DIMENSIONS_OFFSET, SEEK_SET);
  vd->vox[0] = vvToolshed::read16(fp);
  vd->vox[1] = vvToolshed::read16(fp);
  vd->bpc    = 1;
  vd->chan    = 1;
  vd->vox[2] = 1;

  // Read data:
  fseek(fp, DATA_OFFSET, SEEK_SET);
  rawData = new uchar[vd->getSliceBytes()];
  read = fread(rawData, vd->getSliceBytes(), 1, fp);
  if (read != 1)
  {
    vvDebugMsg::msg(1, "Error: RGB file corrupt.");
    fclose(fp);
    delete[] rawData;
    return FILE_ERROR;
  }

  vd->addFrame(rawData, vvVolDesc::ARRAY_DELETE);
  ++vd->frames;
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loads a TGA image file as a one-sliced volume.
  Only uncompressed, non-indexed image formats are supported.
*/
vvFileIO::ErrorType vvFileIO::loadTGAFile(vvVolDesc* vd)
{
  int offset_imageSpec = 8;
  int offset_idBlock = 18;
  int offset_data;

  FILE* fp;
  uint read;
  uchar* rawData;

  // TGA header
  uchar idLength;
  uchar colorMapType;
  uchar imageType;
  ushort colorMapOrigin;
  ushort colorMapLength;
  uchar colorMapEntrySize;

  ushort imageOriginX;
  ushort imageOriginY;
  ushort imageWidth;
  ushort imageHeigth;
  uchar imagePixelSize;
  uchar imageDescriptorByte;

  char * idBlock;

  uchar aux;
  int i;
  int total;
  size_t retval;

  vvDebugMsg::msg(1, "FileIO::loadTGAFile()");
  if ( (fp=fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open TGA file.");
    return FILE_ERROR;
  }

  vvDebugMsg::msg(1, "TGA file header:");
  // read ID block length
  retval=fread(&idLength, 1, 1, fp);
  if (retval!=1)
  {
    std::cerr<<"vvFileIO::loadTGAFile fread failed"<<std::endl;
    fclose(fp);
    return FILE_ERROR;
  }
  vvDebugMsg::msg(1, " ID block length: ", idLength);

  // read color map type
  retval=fread(&colorMapType, 1, 1, fp);
  if (retval!=1)
  {
    std::cerr<<"vvFileIO::loadTGAFile fread failed"<<std::endl;
    fclose(fp);
    return FILE_ERROR;
  }
  vvDebugMsg::msg(1, " Color map type: ", colorMapType);

  // read image type
  retval=fread(&imageType, 1, 1, fp);
  if (retval!=1)
  {
    std::cerr<<"vvFileIO::loadTGAFile fread failed"<<std::endl;
    fclose(fp);
    return FILE_ERROR;
  }
  vvDebugMsg::msg(1, " Image type: ", imageType);

  // read color map infos
  if ( 0 != colorMapType)
  {
    colorMapOrigin = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
    colorMapLength = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
    retval=fread(&colorMapEntrySize, 1, 1, fp);
    if (retval!=1)
    {
      std::cerr<<"vvFileIO::loadTGAFile fread failed"<<std::endl;
      fclose(fp);
      return FILE_ERROR;
    }
    vvDebugMsg::msg(1, " Color map origin: ", colorMapOrigin);
    vvDebugMsg::msg(1, " Color map length: ", colorMapLength);
    vvDebugMsg::msg(1, " Color map entry size: ", colorMapEntrySize);
  }

  // read image specification block
  fseek(fp, offset_imageSpec, SEEK_SET);
  imageOriginX = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  imageOriginY = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  imageWidth   = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  imageHeigth  = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  retval=fread(&imagePixelSize, 1, 1, fp);
  retval+=fread(&imageDescriptorByte, 1, 1, fp);
  if (retval!=2)
  {
    std::cerr<<"vvFileIO::loadTGAFile fread failed"<<std::endl;
    fclose(fp);
    return FILE_ERROR;
  }

  vvDebugMsg::msg(1, " Origin X: ", imageOriginX);
  vvDebugMsg::msg(1, " Origin Y: ", imageOriginY);
  vvDebugMsg::msg(1, " Width: ", imageWidth);
  vvDebugMsg::msg(1, " Height: ", imageHeigth);
  vvDebugMsg::msg(1, " Pixel size: ", imagePixelSize);
  vvDebugMsg::msg(1, " Descriptor byte: ", imageDescriptorByte);
  vvDebugMsg::msg(1, "  Number of attribute bits per pixel: ", (imageDescriptorByte & 7));
  vvDebugMsg::msg(1, "  Reserved: ", (imageDescriptorByte & 8)>>3);
  vvDebugMsg::msg(1, "  Screen origin bit: ", (imageDescriptorByte & 16)>>4);
  vvDebugMsg::msg(1, "  Data storage interleaving flag: ", (imageDescriptorByte & 96)>>5);

  // read ID block
  fseek(fp, offset_idBlock, SEEK_SET);
  if (0 < idLength)
  {
    idBlock = new char[idLength+1];
    retval=fread(idBlock, 1, idLength, fp);
    if (retval!=idLength)
    {
      std::cerr<<"vvFileIO::loadTGAFile fread failed"<<std::endl;
      fclose(fp);
      delete idBlock;
      return FILE_ERROR;
    }
    idBlock[idLength]='\0';
    vvDebugMsg::msg(1, " Image ID block: ", idBlock);
    delete(idBlock);
  }

  if (2!=imageType)
  {
    vvDebugMsg::msg(1, "Error: Image type not supported,");
    vvDebugMsg::msg(1, "please use uncompressed RGB(A) only!");
    fclose(fp);
    return FILE_ERROR;
  }

  // assign image params to volume
  vd->vox[0] = imageWidth;
  vd->vox[1] = imageHeigth;
  vd->vox[2] = 1;
  switch(imagePixelSize/8)
  {
    case 1:
    case 2:
      vd->bpc = imagePixelSize/8;
      vd->chan = 1;
      break;
    case 3:
    case 4:
      vd->bpc = 1;
      vd->chan = imagePixelSize/8;
      break;
    default: assert(0); break;
  }
  vd->frames = 1;

  total = vd->getSliceBytes();

  // compute data block offset
  offset_data = offset_idBlock + idLength;

  // ### TODO: include color map offset (colormaps not supported anyways)

  // Read data:
  fseek(fp, offset_data, SEEK_SET);
  rawData = new uchar[total];

  read = fread(rawData, total, 1, fp);
  if (read != 1)
  {
    vvDebugMsg::msg(1, "Error: TGA file corrupt.");
    fclose(fp);
    delete[] rawData;
    return FILE_ERROR;
  }

  // Byte per pixel value of 3 or 4 implies that the image is RGB(A).
  // However TGA stores it as BGR(A) so we'll have to swap R and B.
  if (vd->chan >= 3)
  {
    for (i=0; i < total; i += vd->chan)
    {
      aux = rawData[i];
      rawData[i] = rawData[i+2];
      rawData[i+2] = aux;
    }
  }

  vd->addFrame(rawData, vvVolDesc::ARRAY_DELETE);
  vd->flip(vvVolDesc::Y_AXIS);

  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loads a raw volume file w/o knowing its structure
 Several automatic detection algorithms are tried.
 If filename is of format:
 <PRE>
    <filename|width|x|height|x|slices|.dat>
    (example: "cthead256x256x128.dat")
 </PRE>
 a volume size of |width| x |height| x |slices| voxels is tried
 in addition to the automatic detection algorithms.
*/
vvFileIO::ErrorType vvFileIO::loadRawFile(vvVolDesc* vd)
{
  const int NUM_ALGORITHMS = 4;                   // number of different size detection algorithms
  char* ptr;                                      // pointer to current character
  long lSize;
  char filename[1024];                            // buffer for filename
  int size, voxels;
                                                  // volume parameters
  int width, height, slices, bpc, chan, components;
  int remainder;
  int cubRoot;                                    // cubic root
  int sqrRoot;                                    // square root
  int attempt;
  int factor;
  int i;

  vvDebugMsg::msg(1, "vvFileIO::loadRawFile(0)");

  lSize = vvToolshed::getFileSize(vd->getFilename());
  if (lSize <= 0) return FILE_ERROR;
  if (lSize > INT_MAX) return FORMAT_ERROR;
  size = (int)lSize;

                                                  // try different ways to find the volume dimensions
  for (attempt=0; attempt<NUM_ALGORITHMS; ++attempt)
  {
    for (components=1; components<=4; ++components)
    {
      if ((size % components) != 0) continue;
      else voxels = size / components;
      cubRoot = (int)powf((float)voxels, 1.0f/3.0f);
      width = height = slices = 0;
      switch (attempt)
      {
        case 0:                                   // check for dimensions given in filename
          vvToolshed::extractFilename(filename, vd->getFilename());

          // Search for beginning of size information string:
          ptr = strchr(filename, '.');
          *ptr = '\0';
          i = 0;
          while (ptr > filename && i < 3)
          {
            --ptr;
            if (!isdigit(*ptr))
            {
              switch (i)
              {
                case 0: slices = atoi(ptr+1); break;
                case 1: height = atoi(ptr+1); break;
                case 2: width  = atoi(ptr+1); break;
                default: break;
              }
              ++i;
              *ptr = '\0';                        // convert delimiters to string terminators
            }
            else if (ptr==filename && i==2) width = atoi(ptr);
          }
          break;

        case 1:
        default:                                  // Check for cubic volume:
          width = height = slices = cubRoot;
          break;

        case 2:                                   // Check for slices being powers of 2:
          width = vvToolshed::getTextureSize(cubRoot);
          sqrRoot = (int)sqrt((double)voxels / (double)width);
          height = vvToolshed::getTextureSize(sqrRoot);
          slices = voxels / width / height;
          break;

        case 3:                                   // Check for square slices and slice edge length greater than volume depth:
          width = slices = 1;
          remainder = size;
          while ((factor = vvToolshed::getLargestPrimeFactor(remainder)) > 1 && width < cubRoot)
          {
                                                  // is factor contained twice?
            if ((remainder % (factor*factor)) == 0)
            {
              width *= factor;
              remainder /= (factor * factor);
            }
            else
            {
              slices *= factor;
              remainder /= factor;
            }
          }
          slices *= remainder;
          height = width;
          break;
      }
      if (components * width * height * slices == size)
      {
        switch(components)
        {
          case 1:
          case 2:
            bpc = components;
            chan = 1;
            break;
          default:
            bpc = 1;
            chan = components;
            break;
        }
        return loadRawFile(vd, width, height, slices, bpc, chan, 0);
      }
    }
  }
  return FORMAT_ERROR;
}

//----------------------------------------------------------------------------
/** Saves a raw data file: no header information is written.
 The number of bytes per voxel is similar to memory format.
 Only one frame of a time dependent dataset is written.
*/
vvFileIO::ErrorType vvFileIO::saveRawFile(vvVolDesc* vd)
{
  FILE* fp;                                       // volume file pointer
  int frameSize;                                  // size of a frame in bytes
  uchar* raw;                                     // raw volume data

  vvDebugMsg::msg(1, "vvFileIO::saveRawFile()");

  // Save volume data:
  frameSize = vd->getFrameBytes();
  raw = vd->getRaw();                             // save only current frame of loaded sequence
  if (frameSize==0 || raw==NULL)
  {
    return VD_ERROR;
  }

                                                  // now open file to write
  if ( (fp = fopen(vd->getFilename(), "wb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file to write.");
    return FILE_ERROR;
  }

  // Write volume data:
  if ((int)fwrite(raw, 1, frameSize, fp) != frameSize)
  {
    vvDebugMsg::msg(1, "Error: Cannot write voxel data to file.");
    fclose(fp);
    return FILE_ERROR;
  }

  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loads a raw volume file of which the structure is known.
  @param w      width
  @param h      height
  @param s      slices (use 1 for 2D image files)
  @param b      bytes per channel
  @param c      channels
  @param header header size in bytes (= number of bytes to skip at beginning of file)
*/
vvFileIO::ErrorType vvFileIO::loadRawFile(vvVolDesc* vd, int w, int h, int s, int b, int c, int header)
{
  FILE* fp;
  uint read;
  uchar* rawData;

  if (b<1 || b>4) return FORMAT_ERROR;

  vvDebugMsg::msg(1, "vvFileIO::loadRawFile(1)");
  if ( (fp=fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open raw file.");
    return FILE_ERROR;
  }

  vd->vox[0] = w;
  vd->vox[1] = h;
  vd->vox[2] = s;
  vd->bpc    = b;
  vd->chan   = c;

  fseek(fp, header, SEEK_SET);                    // skip header
  rawData = new uchar[vd->getFrameBytes()];
  read = fread(rawData, vd->getFrameBytes(), 1, fp);
  if (read != 1)
  {
    cerr << "Error: raw file corrupt (fread returns 0)" << endl;
    fclose(fp);
    delete[] rawData;
    return FILE_ERROR;
  }

  fclose(fp);
  vd->addFrame(rawData, vvVolDesc::ARRAY_DELETE);
  ++vd->frames;
  return OK;
}

//----------------------------------------------------------------------------
/// Loads a PGM or PPM binary image file.
vvFileIO::ErrorType vvFileIO::loadPXMRawImage(vvVolDesc* vd)
{
  const int BUFSIZE = 128;
  FILE* fp;
  uint read;
  uchar* rawData;
  char buf[3][BUFSIZE];
  bool isPGM;                                     // true=PGM, false=PPM
  char* retval_fgets;
  size_t retval_sscanf;

  vvDebugMsg::msg(1, "vvFileIO::loadPXMRawImage()");
  if ( (fp=fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open PGM/PPM file.");
    return FILE_ERROR;
  }

  // Read magic number:
  retval_fgets=fgets(buf[0], BUFSIZE, fp);
  if (retval_fgets==NULL)
  {
    std::cerr<<"vvFileIO::loadPXMRawImage fread failed"<<std::endl;
    fclose(fp);
    return FILE_ERROR;
  }
  if (vvToolshed::strCompare("P5", buf[0], 2) == 0)
    isPGM = true;
  else if (vvToolshed::strCompare("P6", buf[0], 2) == 0)
    isPGM = false;
  else
  {
    fclose(fp);
    vvDebugMsg::msg(1, "Error: Wrong magic number in PGM/PPM file. Use binary format.");
    return DATA_ERROR;
  }

  // Read width and height:
  do
  {
    retval_fgets=fgets(buf[0], BUFSIZE, fp);
    if (retval_fgets==NULL)
    {
      std::cerr<<"vvFileIO::loadPXMRawImage fread failed"<<std::endl;
      fclose(fp);
      return FILE_ERROR;
    }
  } while (buf[0][0] == '#');
  retval_sscanf=sscanf(buf[0], "%s %s", buf[1], buf[2]);
  if (retval_sscanf!=2)
  {
    std::cerr<<"vvFileIO::loadPXMRawImage sscanf failed"<<std::endl;
    fclose(fp);
    return FILE_ERROR;
  }
  vd->vox[0] = atoi(buf[1]);
  vd->vox[1] = atoi(buf[2]);

  // Read maxval:
  do
  {
    retval_fgets=fgets(buf[0], BUFSIZE, fp);
    if (retval_fgets==NULL)
    {
      std::cerr<<"vvFileIO::loadPXMRawImage fread failed"<<std::endl;
      fclose(fp);
      return FILE_ERROR;
    }
  } while (buf[0][0] == '#');

  // Read image data:
  vd->vox[2] = 1;
  if (isPGM) { vd->bpc = vd->chan = 1; }
  else       { vd->bpc = 1; vd->chan = 3; }
  rawData = new uchar[vd->getFrameBytes()];
  read = fread(rawData, vd->getFrameBytes(), 1, fp);
  if (read != 1)
  {
    vvDebugMsg::msg(1, "Error: PGM/PPM file corrupt.");
    fclose(fp);
    delete[] rawData;
    return DATA_ERROR;
  }

  fclose(fp);
  vd->addFrame(rawData, vvVolDesc::ARRAY_DELETE);
  ++vd->frames;
  return OK;
}

//----------------------------------------------------------------------------
/** Loads a DICOM 3.0 image file
 (DICOM = Digital Imaging COmmunications in Medicine)
 @param vd        volume description
 @param dcmSeq    DICOM sequence ID (NULL if not required)
 @param dcmSlice  DICOM slice ID (NULL if not required)
 @param dcmSPos   DICOM slice location (NULL if not required)
*/
vvFileIO::ErrorType vvFileIO::loadDicomFile(vvVolDesc* vd, int* dcmSeq, int* dcmSlice, float* dcmSPos)
{
  vvDicom* dicomReader;
  vvDicomProperties prop;
  int i;

  dicomReader = new vvDicom(&prop);
  if (!dicomReader->readDicomFile((char*)vd->getFilename()))
  {
    delete dicomReader;
    vvDebugMsg::msg(1, "Error: Cannot open Dicom file.");
    return FILE_ERROR;
  }

  if (vvDebugMsg::isActive(1)) prop.print();

  // Make sure variables are tested for NULL because they might be default:
  if (dcmSeq   != NULL) *dcmSeq   = prop.sequence;
  if (dcmSlice != NULL) *dcmSlice = prop.image;
  if (dcmSPos  != NULL) *dcmSPos  = prop.slicePos;

  vd->vox[0] = prop.width;
  vd->vox[1] = prop.height;
  vd->vox[2] = 1;
  for (i=0; i<3; ++i)
  {
    vd->dist[i] = prop.dist[i];
  }
  switch(prop.bpp)
  {
    case 1:
    case 2:
      vd->bpc = prop.bpp;
      vd->chan = 1;
      break;
    case 3:
    case 4:
      vd->bpc = 1;
      vd->chan = prop.bpp;
      break;
    default: assert(0); break;
  }
  vd->addFrame(prop.raw, vvVolDesc::ARRAY_DELETE);
  ++vd->frames;

  // Make big endian data:
  if (prop.littleEndian) vd->toggleEndianness(vd->frames-1);

  // Shift bits so that most significant used bit is leftmost:
  vd->bitShiftData(prop.highBit - (prop.bpp * 8 - 1), vd->frames-1);

  // Make unsigned data:
  if (prop.isSigned) vd->toggleSign(vd->frames-1);

  delete dicomReader;
  return OK;
}

//----------------------------------------------------------------------------
/** Loads a Visible Human anatomic (photo) slice file.
 */
vvFileIO::ErrorType vvFileIO::loadVHDAnatomicFile(vvVolDesc* vd)
{
  ErrorType err;

  err = loadRawFile(vd, 2048, 1216, 1, 1, 3, 0);
  if (err != OK) return err;
  vd->convertRGBPlanarToRGBInterleaved();
  vd->crop(300, 100, 0, 1400, 950, 1);            // images are 2048 x 1216 but contain unnecessary information in the border region
  return OK;
}

//----------------------------------------------------------------------------
/** Loads a Visible Human MRI slice file.
 */
vvFileIO::ErrorType vvFileIO::loadVHDMRIFile(vvVolDesc* vd)
{
  ErrorType err;

  err = loadRawFile(vd, 256, 256, 1, 2, 1, 7900);
  if (err != OK) return err;
  vd->bitShiftData(-4);                           // image is (about) 12 bit, shift it to be in correct 16 bit representation
  return OK;
}

//----------------------------------------------------------------------------
/** Loads a Visible Human CT slice file.
 */
vvFileIO::ErrorType vvFileIO::loadVHDCTFile(vvVolDesc* vd)
{
  ErrorType err;

  err = loadRawFile(vd, 512, 512, 1, 2, 1, 3416);
  if (err != OK) return err;
  vd->bitShiftData(-4);                           // image is 12 bit, shift to appear as 16 bit
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for BrainVoyager VMR files.
  VMR files contain anatomical 3D data stored as bytes.
  The format is very simple consisting of a small header prior to the
  actual data. The only important point is that you understand how the
  three axes are ordered (see below).<P>
  VMR header:<BR>
  <PRE>
  BYTES	 DATA TYPE	            DESCRIPTION
  2      16 bit little endian  DimX, dimension of X axis
  2	     16 bit little endian  DimY, dimension of Y axis
  2	     16 bit little endian  DimZ, dimension of Z axis
</PRE>
Each data element (intensity value) is represented in 1 byte.
The data is organized in three loops: DimZ, DimY, DimX
*/
vvFileIO::ErrorType vvFileIO::loadVMRFile(vvVolDesc* vd)
{
  FILE* fp;                                       // volume file pointer
  int frameSize;                                  // size of a frame in bytes
  uchar* raw;                                     // raw volume data

  vvDebugMsg::msg(1, "vvFileIO::loadVMRFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;
  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  vd->removeSequence();

  // Read header:
  vd->vox[0] = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  vd->vox[1] = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  vd->vox[2] = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  vd->frames = 1;
  vd->bpc    = 1;
  vd->chan    = 1;

  // Create new data space for volume data:
  if ((_sections & RAW_DATA) != 0)
  {
    frameSize = vd->getFrameBytes();
    raw = new uchar[frameSize];

    // Load volume data:
    if ((int)fread(raw, 1, frameSize, fp) != frameSize)
    {
      vvDebugMsg::msg(1, "Error: Insufficient voxel data in VMR file.");
      fclose(fp);
      delete[] raw;
      return FILE_ERROR;
    }

    vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
  }
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for BrainVoyager VTC files.
  A VTC file contains the functional data (time series) of one experimental
  run in a 3D format, i.e. in Talairach space. The binary file contains a
  variable-length header followed by the actual 4D data.<P>
  Header:
  <PRE>
  BYTES DATA TYPE   DESCRIPTION
  2     short int   version number
  N     byte	 	    name of FMR file whose STC data has been transformed
  M     byte        name of the linked protocol (PRT) file
  2     short int   NrOfVolumes (number of volumes, measurements, time points)
2     short int   VTC-resolution, i.e. 3 -> one voxel = 3 x 3 x 3 mm
2     short int   XStart
2     short int   XEnd
2     short int   YStart
2     short int   YEnd
2     short int   ZStart
2     short int   ZEnd
2     short int	 	Hemodynamic delay, simple shift value
4     float	 	    TR [ms]
4     float       Hemodynamic function, delta parameter
4     float       Hemodynamic function, tau parameter
2     short int   Segment size, used for time course separation
2     short int   Segment offset, used for time course separation
</PRE>
The order of voxels in the file is:<BR>
timesteps, voxels/line, lines, slices
*/
vvFileIO::ErrorType vvFileIO::loadVTCFile(vvVolDesc* vd)
{
  FILE* fp;                                       // volume file pointer
  uchar* raw;                                     // raw volume data
  uchar* buf;                                     // buffer for data from file
  uchar* bufPtr;                                  // pointer to data in file buffer
  uchar bak;                                      // backup value
  uchar** frameRaw;                               // pointer to beginning of raw data in each animation frame
  int frameSize;                                  // size of a frame in bytes
  int vtcSliceSize;                               // slice size of VTC file (contains all time step data)
  int start, end;
  int version;                                    // file version
  int f,i,x,y;

  vvDebugMsg::msg(1, "vvFileIO::loadVTCFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;
  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  vd->removeSequence();

  // Read header:
  vd->bpc = 2;
  vd->chan = 1;
                                                  // read version number
  version = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  // ignore FMR file name string
  while (fgetc(fp)!=0)
     ;
  // ignore PRT file name string
  while (fgetc(fp)!=0)
     ;
  vd->frames = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  vd->dist[0] = vd->dist[1] = vd->dist[2] = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  for (i=0; i<3; ++i)
  {
    start      = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
    end        = vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
    vd->vox[i] = int((end - start) / vd->dist[0]);
  }
  if (version==2)                                 // the following parameters are only in the header if version equals 2
  {
    // Ignore the extra header information:
    vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
    vvToolshed::readFloat(fp, vvToolshed::VV_LITTLE_END);
    vvToolshed::readFloat(fp, vvToolshed::VV_LITTLE_END);
    vvToolshed::readFloat(fp, vvToolshed::VV_LITTLE_END);
    vvToolshed::read16(fp, vvToolshed::VV_LITTLE_END);
  }

  if ((_sections & RAW_DATA) != 0)
  {
    frameSize = vd->getFrameBytes();

    // First allocate space for entire volume animation:
    for (f=0; f<vd->frames; ++f)
    {
      raw = new uchar[frameSize];                 // create new data space for volume data
      assert(raw);
      vd->addFrame(raw, vvVolDesc::ARRAY_DELETE); // add uninitialized data to volume
    }

    // Now we can fill the frames with the data from disk:
    vtcSliceSize = vd->getMovieBytes() / vd->vox[2];
    buf = new uchar[vtcSliceSize];
    frameRaw = new uchar*[vd->frames];

    for (f=0; f<vd->frames; ++f)                  // store pointers to frame data in array for speed
      frameRaw[f] = vd->getRaw(f);

    for (i=0; i<vd->vox[2]; ++i)
    {
      if ((int)fread(buf, 1, vtcSliceSize, fp) != vtcSliceSize)
      {
        vvDebugMsg::msg(1, "Error: Insufficient voxel data in file.");
        fclose(fp);
        vd->removeSequence();
        return FILE_ERROR;
      }
      bufPtr = buf;

      // Copy data from buffer to actual volume storage:
      for (y=0; y<vd->vox[1]; ++y)
        for (x=0; x<vd->vox[0]; ++x)
          for (f=0; f<vd->frames; ++f)
          {
        // Swap the bytes because they are stored as little endian:
            bak = *bufPtr;
            *bufPtr = *(bufPtr+1);
            *(bufPtr+1) = bak;

            memcpy(frameRaw[f], bufPtr, 2);
            frameRaw[f] += 2;
            bufPtr += 2;
          }
    }
    delete[] frameRaw;
    delete[] buf;
  }
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Loader for voxel file in nrrd (teem volume file) format.
 */
vvFileIO::ErrorType vvFileIO::loadNrrdFile(vvVolDesc* vd)
{
  FILE* fp;                                       // volume file pointer
  vvTokenizer* tokenizer;                         // stream tokenizer
                                                  // token type
  vvTokenizer::TokenType ttype = vvTokenizer::VV_NOTHING;
                                                  // previous token type
  vvTokenizer::TokenType prevTT = vvTokenizer::VV_NOTHING;
  uchar* raw;                                     // raw volume data
  int f, i;                                       // counters
  int frameSize;                                  // size of a frame in bytes
  int dimension = 0;                              // dimension of the volume dataset
  bool bigEnd = true;                             // true = big endian

  vvDebugMsg::msg(1, "vvFileIO::loadNrrdFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;

  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }
  vd->removeSequence();                           // delete previous volume sequence

  for (i=0; i<(int)strlen(_nrrdID); ++i)
  {
    if (fgetc(fp) != _nrrdID[i])
    {
      cerr << "Error: Invalid file ID string." << endl;
      fclose(fp);
      return DATA_ERROR;
    }
  }

  // Create tokenizer:
  tokenizer = new vvTokenizer(fp);
  tokenizer->setCommentCharacter('#');
  tokenizer->setEOLisSignificant(true);
  tokenizer->setCaseConversion(vvTokenizer::VV_LOWER);
  tokenizer->setParseNumbers(true);
  tokenizer->setWhitespaceCharacter(':');

  // Parse header:
  vd->vox[2] = 1;
  vd->frames = 1;                                 // default values
  do
  {
    prevTT = ttype;
    ttype = tokenizer->nextToken();
    if (ttype == vvTokenizer::VV_EOF || ttype == vvTokenizer::VV_NUMBER)
    {
      cerr << "Invalid nrrd file format." << endl;
      delete tokenizer;
      fclose(fp);
      return FORMAT_ERROR;
    }
    else if (ttype == vvTokenizer::VV_EOL)
    {                                             // do nothing
    }
    else if (strcmp(tokenizer->sval, "content")==0)
    {
      // ignore content information
      tokenizer->nextLine();
    }
    else if (strcmp(tokenizer->sval, "type")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype != vvTokenizer::VV_WORD || strcmp(tokenizer->sval, "unsigned")!=0)
        cerr << "unknown type" << endl;
      else
      {
        ttype = tokenizer->nextToken();
        if (strcmp(tokenizer->sval, "char")==0) { vd->bpc = vd->chan = 1; }
        else if (strcmp(tokenizer->sval, "short")==0) { vd->bpc = 2; vd->chan = 1; }
        else cerr << "unknown type" << endl;
      }
    }
    else if (strcmp(tokenizer->sval, "dimension")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype == vvTokenizer::VV_NUMBER)
      {
        dimension = int(tokenizer->nval);
        if (dimension < 1 || dimension > 4) cerr << "dimension must be 1 to 4" << endl;
      }
      else cerr << "invalid dimension" << endl;
    }
    else if (strcmp(tokenizer->sval, "sizes")==0)
    {
      for (i=0; i<dimension; ++i)
      {
        ttype = tokenizer->nextToken();
        if (i==0)
        {
          // Guess if first entry is number of channels or width.
          // Assume number of channels if first size is 2, 3, or 4.
          switch (int(tokenizer->nval))
          {
            case 2:
            case 3:
            case 4: vd->bpc = 1;
            vd->chan = int(tokenizer->nval);
            break;
            default: vd->vox[0] = int(tokenizer->nval); break;
          }
        }
        else
        {
          if (vd->chan>=2 && vd->chan<=4) vd->vox[i-1] = int(tokenizer->nval);
          else if (i==3) vd->frames = int(tokenizer->nval);
          else vd->vox[i] = int(tokenizer->nval);
        }
      }
    }
    else if (strcmp(tokenizer->sval, "spacings")==0)
    {
      bool multiModal = false;
      for (i=0; i<dimension; ++i)
      {
        ttype = tokenizer->nextToken();
        if (i==0 && ttype==vvTokenizer::VV_WORD)  // if first value is NaN, expect multi-modal data
        {
          vd->dt = 0.0f;
          multiModal = true;
        }
        else if (i>0 && multiModal)               // still multi-modal data
        {
          vd->dist[i-1] = tokenizer->nval;
        }
        else                                      // only one channel
        {
          if (i==3) vd->dt = tokenizer->nval;
          else vd->dist[i] = tokenizer->nval;
        }
      }
    }
    else if (strcmp(tokenizer->sval, "endian")==0)
    {
      ttype = tokenizer->nextToken();
      if (strcmp(tokenizer->sval, "little") == 0) bigEnd = false;
      else bigEnd = true;
    }
    else if (strcmp(tokenizer->sval, "encoding")==0)
    {
      ttype = tokenizer->nextToken();
      if (strcmp(tokenizer->sval, "raw") != 0)
      {
        cerr << "Can only process raw data." << endl;
        delete tokenizer;
        fclose(fp);
        return FORMAT_ERROR;
      }
    }
    else
    {
      tokenizer->nextLine();
    }
                                                  // stop when two EOL in a row
  } while (ttype != vvTokenizer::VV_EOL || prevTT != vvTokenizer::VV_EOL);
  delete tokenizer;

  frameSize = vd->getFrameBytes();

  // Load volume data:
  if ((_sections & RAW_DATA) != 0)
  {
    for (f=0; f<vd->frames; ++f)
    {
      raw = new uchar[frameSize];                 // create new data space for volume data
      if ((int)fread(raw, 1, frameSize, fp) != frameSize)
      {
        vvDebugMsg::msg(1, "Error: Insuffient voxel data in file.");
        fclose(fp);
        delete[] raw;
        return DATA_ERROR;
      }
      vd->addFrame(raw, vvVolDesc::ARRAY_DELETE);
    }
  }

  if (!bigEnd) vd->toggleEndianness();

  // Clean up:
  fclose(fp);
  return OK;
}

//----------------------------------------------------------------------------
/** Load XIMG image file. This file format was created by General Electric.
 */
vvFileIO::ErrorType vvFileIO::loadXIMGFile(vvVolDesc* vd)
{
  FILE* fp;
  uchar* rawData;
  uint read;
  char magic[4];                                  // XIMG magic number
  int offset;                                     // offset to data area
  int compression;                                // compression format
  int i;

  vvDebugMsg::msg(1, "vvFileIO::loadXIMGFile()");
  if ( (fp=fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open XIMG file.");
    return FILE_ERROR;
  }

  // Read magic number:
  for (i=0; i<4; ++i)
  {
    magic[i] = vvToolshed::read8(fp);
  }
  if (magic[0]!='I' || magic[1]!='M' || magic[2]!='G' || magic[3]!='F')
  {
    fclose(fp);
    cerr << "Wrong magic number in XIMG file." << endl;
    return DATA_ERROR;
  }

  // Read offset to data area:
  offset = vvToolshed::read32(fp);

  // Read image size:
  vd->vox[0] = vvToolshed::read32(fp);
  vd->vox[1] = vvToolshed::read32(fp);

  // Read bpv:
  int bytes = vvToolshed::read32(fp) / 8;
  switch(bytes)
  {
    case 1:
    case 2:
      vd->bpc = bytes;
      vd->chan = 1;
      break;
    case 3:
    case 4:
      vd->bpc = 1;
      vd->chan = bytes;
      break;
    default: assert(0); break;
  }

  // Read compression:
  compression = vvToolshed::read32(fp);
  if (compression!=1)
  {
    fclose(fp);
    cerr << "Compression type must be 'rectangular'." << endl;
    return DATA_ERROR;
  }

  // Read image data:
  fseek(fp, offset, SEEK_SET);
  vd->vox[2] = 1;
  rawData = new uchar[vd->getFrameBytes()];
  read = fread(rawData, vd->getFrameBytes(), 1, fp);
  if (read != 1)
  {
    vvDebugMsg::msg(1, "Error: XIMG file corrupt.");
    fclose(fp);
    delete[] rawData;
    return DATA_ERROR;
  }

  fclose(fp);
  vd->addFrame(rawData, vvVolDesc::ARRAY_DELETE);
  ++vd->frames;
  return OK;
}

//----------------------------------------------------------------------------
/** Loads an IEEE Visualization 2004 contest file.
 */
vvFileIO::ErrorType vvFileIO::loadVis04File(vvVolDesc* vd)
{
  ErrorType err;

  vvDebugMsg::msg(1, "vvFileIO::loadVis04File()");

  err = loadRawFile(vd, 500, 500, 100, 4, 1, 0);
  if (err != OK) return err;
  vd->toggleEndianness();                         // file is big endian

  // Set real min and max:
  vd->real[0] = 0.0f;
  if (vvToolshed::strCompare(vd->getFilename(), "QCLOUD", 6) == 0)      vd->real[1] = 0.00332f;
  else if (vvToolshed::strCompare(vd->getFilename(), "QGRAUP", 6) == 0) vd->real[1] = 0.01638f;
  else if (vvToolshed::strCompare(vd->getFilename(), "QICE", 4) == 0)   vd->real[1] = 0.00099f;
  else if (vvToolshed::strCompare(vd->getFilename(), "QRAIN", 5) == 0)  vd->real[1] = 0.01132f;
  else if (vvToolshed::strCompare(vd->getFilename(), "QSNOW", 5) == 0)  vd->real[1] = 0.00135f;
  else if (vvToolshed::strCompare(vd->getFilename(), "QRAIN", 5) == 0)  vd->real[1] = 0.01132f;
  else if (vvToolshed::strCompare(vd->getFilename(), "QVAPOR", 6) == 0) vd->real[1] = 0.02368f;
  else if (vvToolshed::strCompare(vd->getFilename(), "CLOUD", 5) == 0)  vd->real[1] = 0.00332f;
  else if (vvToolshed::strCompare(vd->getFilename(), "PRECIP", 6) == 0) vd->real[1] = 0.01672f;
  else if (vvToolshed::strCompare(vd->getFilename(), "Pf", 2) == 0)
  {
    vd->real[0] = -5471.85791f;
    vd->real[1] =  3225.42578f;
  }
  else if (vvToolshed::strCompare(vd->getFilename(), "TCf", 3) == 0)
  {
    vd->real[0] = -83.00402f;
    vd->real[1] =  31.51576f;
  }
  else if (vvToolshed::strCompare(vd->getFilename(), "Uf", 2) == 0)
  {
    vd->real[0] = -79.47297f;
    vd->real[1] =  85.17703f;
  }
  else if (vvToolshed::strCompare(vd->getFilename(), "Vf", 2) == 0)
  {
    vd->real[0] = -76.03391f;
    vd->real[1] =  82.95293f;
  }
  else if (vvToolshed::strCompare(vd->getFilename(), "Wf", 2) == 0)
  {
    vd->real[0] = -9.06026f;
    vd->real[1] = 28.61434f;
  }

  return OK;
}

//----------------------------------------------------------------------------
/** Loads a MeshViewer file.
  See: http://vistools.npaci.edu/meshviewer/MeshViewer.htm
*/
vvFileIO::ErrorType vvFileIO::loadHDRFile(vvVolDesc* vd)
{
  vvTokenizer* tokenizer;
  FILE* fp;
  ErrorType err;
  vvTokenizer::TokenType ttype;                   // currently processed token type
  int skipBytes = 0;
  int i;
  bool done = false;
  bool bigEnd = true;
  bool rightHanded = true;
  bool error = false;
  char* filenameBak = NULL;

  vvDebugMsg::msg(1, "vvFileIO::loadHDRFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;

  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  // Backup file name:
  filenameBak = new char[strlen(vd->getFilename()) + 1];
  strcpy(filenameBak, vd->getFilename());

  // Delete previous volume sequence
  vd->removeSequence();

  setDefaultValues(vd);

  // Read header data:
  tokenizer = new vvTokenizer(fp);
  tokenizer->setCommentCharacter('#');
  tokenizer->setEOLisSignificant(true);
  tokenizer->setCaseConversion(vvTokenizer::VV_NONE);
  tokenizer->setParseNumbers(true);
  tokenizer->setWhitespaceCharacter('=');
  done = error = false;
  while (!done)
  {
    // Read identifier:
    ttype = tokenizer->nextToken();
    if (ttype == vvTokenizer::VV_EOL) continue;
    else if (ttype != vvTokenizer::VV_WORD)
    {
      done = true;
      tokenizer->pushBack();
      continue;
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "DATAFILE:")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype == vvTokenizer::VV_WORD) vd->setFilename(tokenizer->sval);
      cerr << "hdr file: Datafile=" << vd->getFilename() << endl;
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "RESOLUTION:")==0)
    {
      for (i=0; i<3; ++i)
      {
        ttype = tokenizer->nextToken();
        if (ttype == vvTokenizer::VV_NUMBER) vd->vox[i] = int(tokenizer->nval);
      }
      cerr << "hdr file: Resolution=" << vd->vox[0] << " x " << vd->vox[1] << " x " << vd->vox[2] << endl;
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "SPACING:")==0)
    {
      for (i=0; i<3; ++i)
      {
        ttype = tokenizer->nextToken();
        if (ttype == vvTokenizer::VV_NUMBER) vd->dist[i] = tokenizer->nval;
      }
      cerr << "hdr file: Spacing=" << vd->dist[0] << " x " << vd->dist[1] << " x " << vd->dist[2] << endl;
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "VOXELTYPE:")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype == vvTokenizer::VV_WORD)
      {
        if (vvToolshed::strCompare(tokenizer->sval, "SCALAR")==0)
        {
          vd->chan = 1;
          cerr << "hdr file: Voxeltype=" << vd->chan << endl;
        }
        else cerr << "hdr file: unknown Voxeltype" << endl;
      }
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "FIELDTYPE:")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype == vvTokenizer::VV_WORD)
      {
        if (vvToolshed::strCompare(tokenizer->sval, "FLOAT")==0)
        {
          vd->bpc = 4;
          cerr << "hdr file: Fieldtype=" << vd->bpc << endl;
        }
        else cerr << "hdr file: unknown Fieldtype" << endl;
      }
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "MINVAL:")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype == vvTokenizer::VV_NUMBER) vd->real[0] = tokenizer->nval;
      cerr << "hdr file: MinVal=" << vd->real[0] << endl;
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "MAXVAL:")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype == vvTokenizer::VV_NUMBER) vd->real[1] = tokenizer->nval;
      cerr << "hdr file: MaxVal=" << vd->real[1] << endl;
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "BYTEORDER:")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype == vvTokenizer::VV_WORD)
      {
        if (vvToolshed::strCompare(tokenizer->sval, "LSB")==0) bigEnd = false;
        cerr << "hdr file: ByteOrder=" << ((bigEnd) ? "big endian" : "little endian") << endl;
      }
      else cerr << "hdr file: Invalid ByteOrder" << endl;
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "SKIPBYTES:")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype == vvTokenizer::VV_NUMBER) skipBytes = int(tokenizer->nval);
      cerr << "hdr file: SkipBytes=" << skipBytes << endl;
    }
    else if (vvToolshed::strCompare(tokenizer->sval, "COORDSYSTEM:")==0)
    {
      ttype = tokenizer->nextToken();
      if (ttype == vvTokenizer::VV_WORD)
      {
        if (vvToolshed::strCompare(tokenizer->sval, "LEFT_HANDED")==0) rightHanded = false;
        cerr << "hdr file: CoordSystem=" << ((rightHanded) ? "right handed" : "left handed") << endl;
      }
      else cerr << "hdr file: Invalid value for CoordSystem" << endl;
    }
    else
    {
      done = error = true;
      continue;
    }
  }
  if (error)
  {
    cerr << "Read error in line " << tokenizer->getLineNumber() << " of MeshViewer file." << endl;
    delete tokenizer;
    vd->setFilename(filenameBak);
    delete[] filenameBak;
    fclose(fp);
    return DATA_ERROR;
  }

  err = loadRawFile(vd, vd->vox[0], vd->vox[1], vd->vox[2], vd->bpc, vd->chan, skipBytes);
  vd->setFilename(filenameBak);
  delete[] filenameBak;
  if (err != OK) return err;
  if (!rightHanded) vd->convertVoxelOrder();
  if (bigEnd) vd->toggleEndianness();

  return OK;
}

//----------------------------------------------------------------------------
/** Loads a MeshViewer VOLB file. Both V1 and V2 are recognized. Chunks are not supported at this point.
  For details see: http://visservices.sdsc.edu/vistools/Documentation/JavaDoc/Formats/VOLFormat.html
  <pre>
  VOLB V1
	  [file]   := "VOLB\n" [header] [data]
	  [header] := [width] [height] [depth]
  </pre>
  [width], [height], and [depth] are 32-bit MBF integers. 
  [data] is a stream of width*height*depth*4 8-bit tuples, where each tuple has 8-bits each of red, 
  green, blue, and alpha, in that order. 
  <pre>
  VOLB V2
	[file]   := "Volb2\n" [header] [chunks] [axes] [data]
	[header] := [width] [height] [depth]
	[chunks] := [chunk_width] [chunk_height] [chunk_depth]
	[axes]   := [width_name] [height_name] [depth_name]
	[width_name]  := [len] [string]
	[height_name] := [len] [string]
	[depth_name]  := [len] [string]
  </pre>
  [width], [height], and [depth] are 32-bit MBF integers. 
  [chunk_width], [chunk_height], and [chunk_depth] are 32-bit MBF integers. 
  If all three are less than or equal to 1, then the format is not chunked. 

  Axis names are given with a 32-bit MBF integer string length (not including a null terminator) 
  followed by that number of ASCII characters (not including a null terminator). 

  [data] is a stream of width*height*depth*4 8-bit values, where each tuple has 8-bits each of red, 
  green, blue, and alpha, in that order. 
*/
vvFileIO::ErrorType vvFileIO::loadVOLBFile(vvVolDesc* vd)
{
  const char* VOLB_V1_STRING = "VOLB\n";
  const char* VOLB_V2_STRING = "Volb2";
  FILE* fp;
  int i;
  int blocksRead;
  char buf[128];
  int chunkSize;
  int axisNameLength;
  uchar* rawData;
  int version;    // VolB version: 1 or 2

  vvDebugMsg::msg(1, "vvFileIO::loadVOLBFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;

  if ( (fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  vd->removeSequence();
  setDefaultValues(vd);
  vd->bpc = 1;
  vd->chan = 4;   // files are RGBA

  // Read header:
  blocksRead = fread(buf, strlen(VOLB_V1_STRING), 1, fp);
  if (vvToolshed::strCompare(buf, VOLB_V1_STRING, strlen(VOLB_V1_STRING))==0) version = 1;
  else if (vvToolshed::strCompare(buf, VOLB_V2_STRING, strlen(VOLB_V2_STRING))==0)
  {
    version = 2;
    fgetc(fp);  // skip \n character
  }
  else
  {
    cerr << "Error: unknown file type" << endl;
    fclose(fp);
    return DATA_ERROR;
  }
  cerr << "Volb file is version " << version << endl;
  for (i=0; i<3; ++i)
  {
    vd->vox[i] = vvToolshed::read32(fp, vvToolshed::VV_BIG_END);
  }
  if (version==2)
  {
    for (i=0; i<3; ++i)
    {
      chunkSize = vvToolshed::read32(fp, vvToolshed::VV_BIG_END);
      if (chunkSize>1) 
      {
        cerr << "Error: chunks not supported" << endl;
        fclose(fp);
        return DATA_ERROR;
      }    
    }
    for (i=0; i<3; ++i)
    {
      axisNameLength = vvToolshed::read32(fp, vvToolshed::VV_BIG_END);
      fseek(fp, axisNameLength, SEEK_CUR);  // skip axis name
    }
  }

  if ((_sections & RAW_DATA) != 0)    // read volume data
  {  
    rawData = new uchar[vd->getFrameBytes()];
    blocksRead = fread(rawData, vd->getFrameBytes(), 1, fp);
    if (blocksRead != 1)
    {
      cerr << "Error: file corrupt" << endl;
      fclose(fp);
      delete[] rawData;
      return DATA_ERROR;
    }
    fclose(fp);
    vd->addFrame(rawData, vvVolDesc::ARRAY_DELETE);
    ++vd->frames;
    vd->convertVoxelOrder();
  }

  return OK;
}

//----------------------------------------------------------------------------
/** Loads a Microsoft DirectDraw Surface file.
  See: http://msdn.microsoft.com/library/en-us/directx9_c/directx/graphics/reference/DDSFileReference/ddsfileformat.asp
*/
vvFileIO::ErrorType vvFileIO::loadDDSFile(vvVolDesc* vd)
{
  enum dwFlagsType
  {
    DDSD_CAPS = 0x00000001,
    DDSD_HEIGHT = 0x00000002,
    DDSD_WIDTH = 0x00000004,
    DDSD_PITCH = 0x00000008,
    DDSD_PIXELFORMAT = 0x00001000,
    DDSD_MIPMAPCOUNT = 0x00020000,
    DDSD_LINEARSIZE = 0x00080000,
    DDSD_DEPTH = 0x00800000
  };
  enum ddpfPixelFormatType
  {
    DDPF_ALPHAPIXELS = 0x00000001,
    DDPF_FOURCC = 0x00000004,
    DDPF_RGB = 0x00000040
  };
  enum dwCaps1Type
  {
    DDSCAPS_COMPLEX = 0x00000008,
    DDSCAPS_TEXTURE = 0x00001000,
    DDSCAPS_MIPMAP = 0x00400000
  };
  enum dwCaps2Type
  {
    DDSCAPS2_CUBEMAP = 0x00000200,
    DDSCAPS2_CUBEMAP_POSITIVEX = 0x00000400,
    DDSCAPS2_CUBEMAP_NEGATIVEX = 0x00000800,
    DDSCAPS2_CUBEMAP_POSITIVEY = 0x00001000,
    DDSCAPS2_CUBEMAP_NEGATIVEY = 0x00002000,
    DDSCAPS2_CUBEMAP_POSITIVEZ = 0x00004000,
    DDSCAPS2_CUBEMAP_NEGATIVEZ = 0x00008000,
    DDSCAPS2_VOLUME = 0x00200000
  };
  const long MAGIC_NUMBER = 0x20534444;
  const long STRUCTURE_SIZE = 124;
  FILE* fp;
  long dwMagic, dwSize, dwFlags, dwHeight, dwWidth, dwPitchOrLinearSize, dwDepth,
    dwMipMapCount, dwReserved1, dwReserved2, ddpfPixelFormat;
  int ddsCaps, blocksRead;
  uchar* rawData;

  vvDebugMsg::msg(1, "vvFileIO::loadDDSFile()");

  if (vd->getFilename()==NULL) return FILE_ERROR;

  if ((fp = fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open file.");
    return FILE_ERROR;
  }

  // Delete previous volume sequence
  vd->removeSequence();

  setDefaultValues(vd);

  // Validate file format:
  dwMagic = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  dwSize = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  if (dwMagic != MAGIC_NUMBER || dwSize != STRUCTURE_SIZE)
  {
    cerr << "Error: Invalid DDS file header." << endl;
    fclose(fp);
    return FORMAT_ERROR;
  }

  // Parse DDSURFACEDESC2 structure:
  dwFlags = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  dwHeight = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  dwWidth = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  dwPitchOrLinearSize = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  dwDepth = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  dwMipMapCount = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  dwReserved1 = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  ddpfPixelFormat = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  ddsCaps = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);
  dwReserved2 = vvToolshed::read32(fp, vvToolshed::VV_LITTLE_END);

  // Jump to data area:
  fseek(fp, STRUCTURE_SIZE + 4, SEEK_SET);

  // Read image data:
  vd->vox[0] = dwWidth;
  vd->vox[1] = dwHeight;
  vd->vox[2] = (dwDepth==0) ? 1 : dwDepth;
  vd->bpc = 1;
  vd->chan = dwPitchOrLinearSize / dwWidth;
  rawData = new uchar[vd->getFrameBytes()];
  blocksRead = fread(rawData, vd->getFrameBytes(), 1, fp);
  if (blocksRead != 1)
  {
    cerr << "Error: DDS file corrupt." << endl;
    fclose(fp);
    delete[] rawData;
    return DATA_ERROR;
  }

  fclose(fp);
  vd->addFrame(rawData, vvVolDesc::ARRAY_DELETE);
  ++vd->frames;

  return OK;
}

//----------------------------------------------------------------------------
/// Loads an image file from Graham Kent, Scripps Institution
vvFileIO::ErrorType vvFileIO::loadGKentFile(vvVolDesc* vd)
{
  const int WIDTH = 1001;
  const int HEIGHT = 801;
  FILE* fp;
  uint read;
  uchar* rawData;

  vvDebugMsg::msg(1, "vvFileIO::loadGKentFile()");
  if ( (fp=fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open gkent file.");
    return FILE_ERROR;
  }

  vd->vox[0] = WIDTH;
  vd->vox[1] = HEIGHT;
  vd->vox[2] = 1;
  vd->chan = 1;
  vd->bpc = 4;

  // Read image data:
  rawData = new uchar[vd->getFrameBytes()];
  read = fread(rawData, vd->getFrameBytes(), 1, fp);
  if (read != 1)
  {
    vvDebugMsg::msg(1, "Error: file corrupt.");
    fclose(fp);
    delete[] rawData;
    return DATA_ERROR;
  }

  fclose(fp);
  vd->addFrame(rawData, vvVolDesc::ARRAY_DELETE);
  ++vd->frames;
  vd->toggleEndianness();
  return OK;
}


/**----------------------------------------------------------------------------
  Loads an image file from David Dowell/UCAR, Larry Frank/UCSD

  Header format:

  nx ny nz 
  dx dy dz 
  xmin ymin zmin 
  rho 
  u(1,1,1) 
  u(2,1,1) 
  ... 
  u(nx,ny,nz) 
  v(1,1,1) 
  v(2,1,1) 
  ... 
  v(nx,ny,nz) 
  w(1,1,1) 
  w(2,1,1) 
  ... 
  w(nx,ny,nz) 
  r(1,1,1) 
  r(2,1,1) 
  ... 
  r(nx,ny,nz) 

  Definitions of the variables: 
  nx = number of gridpoints in the x (east) direction 
  ny = " " y (north) direction 
  nz = " " z (up) direction 
  dx = distance (m) between gridpoints in x direction 
  dy = " " y direction 
  dz = " " z direction 
  xmin = x coordinate (m) of lower southwest corner of grid 
  ymin = y " " 
  zmin = z " " 
  rho = air density at the surface (kg / m^3) 
  u = component of velocity (m/s)in the x direction 
  v = " " y direction 
  w = " " z direction 
  r = reflectivity (dBZ) 

  Example:
  <pre>  
    51              51             35
    1000.000        1000.000       500.0000    
    30000.00        10000.00       0.0000000E+00
    1.000000    
    [here comes the data]
  </pre>
*/
vvFileIO::ErrorType vvFileIO::loadSynthFile(vvVolDesc* vd)
{
  const int NUM_CHANNELS = 4;
  FILE* fp;
  vvTokenizer* tok;
  uchar* rawData;
  int i, x, y, z, voxPerChan, index;

  vvDebugMsg::msg(1, "vvFileIO::loadSynthFile()");
  if ( (fp=fopen(vd->getFilename(), "rb")) == NULL)
  {
    vvDebugMsg::msg(1, "Error: Cannot open .synth file.");
    return FILE_ERROR;
  }

  // Initialize tokenizer:
  tok = new vvTokenizer(fp);
  tok->setEOLisSignificant(false);
  tok->setParseNumbers(true);
  tok->setWhitespaceCharacter(' ');

  vd->chan = NUM_CHANNELS;   // channels are u, v, w, r
  vd->bpc = 4;

  // Read header:
  for (i=0; i<3; ++i)   // read number of gridpoints
  {  
    if (tok->nextToken() != vvTokenizer::VV_NUMBER) assert(0);
    vd->vox[i] = int(tok->nval);
  }
  for (i=0; i<3; ++i)   // read distance between gridpoints
  {  
    if (tok->nextToken() != vvTokenizer::VV_NUMBER) assert(0);
    vd->dist[i] = tok->nval;
  }
  for (i=0; i<3; ++i)   // read lower grid corner 
  {  
    if (tok->nextToken() != vvTokenizer::VV_NUMBER) assert(0);
  }
  if (tok->nextToken() != vvTokenizer::VV_NUMBER) assert(0);

  // Allocate volume memory:
  rawData = new uchar[vd->getFrameBytes()];

  // Read volume data:
  voxPerChan = vd->vox[0] * vd->vox[1] * vd->vox[2];
  for (i=0; i<NUM_CHANNELS; ++i)
  {
    for (z=0; z<vd->vox[2]; ++z)
    {
      for (y=0; y<vd->vox[1]; ++y)
      {
        for (x=0; x<vd->vox[0]; ++x)
        {
          if (tok->nextToken() == vvTokenizer::VV_NUMBER)          
          {
            index = NUM_CHANNELS * (x + y * vd->vox[0] + z * vd->vox[0] * vd->vox[1]) + i;
            *(((float*)rawData)+index) = tok->nval;
          }
          else
          {
            cerr << "Error in line " << tok->getLineNumber() << " of .synth file" << endl;
            assert(0);
          }
        }
      }
    }
  }

  fclose(fp);
  vd->addFrame(rawData, vvVolDesc::ARRAY_DELETE);
  ++vd->frames;
  return OK;
}

//----------------------------------------------------------------------------
/** Saves all slices of the current volume as PPM or PGM images.
 A numbered suffix of four digits will be added to the file names.
 @param overwrite   true to overwrite existing files
*/
vvFileIO::ErrorType vvFileIO::savePXMSlices(vvVolDesc* vd, bool overwrite)
{
  FILE* fp;
  ErrorType err = OK;
  int digits;                                     // number of digits used for file numbering
  char** filenames;                               // list of filenames
  int len;                                        // filename length
  int i, j, k;
  int sliceSize;
  int tmpSliceSize = 0;
  char buffer[1024];
  uchar* slice;                                   // original slice data
  uchar* tmpSlice = NULL;                         // temporary slice data

  vvDebugMsg::msg(1, "vvFileIO::savePXMSlices()");

  if (vd->frames<1 || vd->vox[2]<1) return DATA_ERROR;
  if (vd->bpc * vd->chan > 4) return DATA_ERROR;

  // Generate file names:
  digits = 1 + int(log((double)vd->vox[2]) / log(10.0));
  filenames = new char*[vd->vox[2]];
  len = strlen(vd->getFilename());
  for (i=0; i<vd->vox[2]; ++i)
  {
    filenames[i] = new char[len + digits + 2];    // add 2 for '-' and '\0'
    vvToolshed::extractDirname(buffer, vd->getFilename());
    strcpy(filenames[i], buffer);
    vvToolshed::extractBasename(buffer, vd->getFilename());
    strcat(filenames[i], buffer);
    if (vd->vox[2] > 1)
    {
      sprintf(buffer, "-%0*d.", digits, i);
      strcat(filenames[i], buffer);
    }
    else strcat(filenames[i], ".");
    if (vd->chan==1) strcat(filenames[i], "pgm");
    else strcat(filenames[i], "ppm");
  }

  // Check files for existence:
  if (!overwrite)
  {
    for (i=0; i<vd->vox[2]; ++i)
    {
      if (vvToolshed::isFile(filenames[i]))       // check if file exists
      {
        vvDebugMsg::msg(1, "Error - file exists: ", filenames[i]);
        err = FILE_EXISTS;
      }
    }
  }

  // Write files:
  sliceSize = vd->getSliceBytes();
  if (vd->bpc==2 || vd->chan==4)
  {
    int bytes;
    if (vd->bpc==2) bytes = 1;
    else bytes = 3;
    tmpSliceSize = vd->vox[0] * vd->vox[1] * bytes;
    tmpSlice = new uchar[tmpSliceSize];
  }
  for (i=0; i<vd->vox[2] && err==OK; ++i)
  {
    // Open file to write:
    if ( (fp = fopen(filenames[i], "wb")) == NULL)
    {
      err = FILE_ERROR;
      continue;
    }

    // Write header:
    if (vd->chan==1) fprintf(fp, "P5\n");         // grayscale
    else fprintf(fp, "P6\n");                     // RGB

                                                  // write dimensions
    fprintf(fp, "%d %d\n", vd->vox[0], vd->vox[1]);
    fprintf(fp, "%d\n", 255);                     // write maximum value

    // Write data:
    slice = vd->getRaw() + i * sliceSize;
    if (vd->bpc==1 && (vd->chan==1 || vd->chan==3))
    {
      if ((int)fwrite(slice, sliceSize, 1, fp) != 1) err = FILE_ERROR;
    }
    else if (vd->bpc==2 || vd->chan==4)
    {
      for (j=0; j<vd->getSliceVoxels(); ++j)
      {
        for (k=0; k<(vd->bpc*vd->chan)-1; ++k)
        {
          tmpSlice[j * ((vd->bpc*vd->chan)-1) + k] = slice[j * (vd->bpc*vd->chan) + k];
        }
      }
      if ((int)fwrite(tmpSlice, tmpSliceSize, 1, fp) != 1) err = FILE_ERROR;
    }

    fclose(fp);
  }

  // Free memory:
  if (vd->bpc==2 || vd->chan==4) delete[] tmpSlice;
  for (i=0; i<vd->vox[2]; ++i) delete[] filenames[i];
  delete[] filenames;

  return err;
}

//----------------------------------------------------------------------------
/** Save volume data to a volume file.
  The file format is determined from the filename extension.
  @param vd        volume description
  @param overwrite true to overwrite existing file
  @param sec       bit encoded list of file sections to be saved (if present in file).
                   This value defaults to saving all data to a file.
  @return NO_ERROR if successful
*/
vvFileIO::ErrorType vvFileIO::saveVolumeData(vvVolDesc* vd, bool overwrite, LoadType sec)
{
  vvDebugMsg::msg(1, "vvFileIO::saveVolumeData(), file name: ", vd->getFilename());

  if (vd==NULL) return PARAM_ERROR;               // volume description missing

  if (vd->getFilename()==NULL) return PARAM_ERROR;
                                                  // filename too short
  if (strlen(vd->getFilename()) < 3) return PARAM_ERROR;

                                                  // check if file exists
  if (!overwrite && vvToolshed::isFile(vd->getFilename()))
  {
    vvDebugMsg::msg(1, "Error: File exists:", vd->getFilename());
    return FILE_EXISTS;
  }

  _sections = sec;

  if (vvToolshed::isSuffix(vd->getFilename(), ".rvf"))
    return saveRVFFile(vd);

  if (vvToolshed::isSuffix(vd->getFilename(), ".xvf"))
    return saveXVFFile(vd);

  if (vvToolshed::isSuffix(vd->getFilename(), ".avf"))
    return saveAVFFile(vd);

  if (vvToolshed::isSuffix(vd->getFilename(), ".dat"))
    return saveRawFile(vd);

  if (vvToolshed::isSuffix(vd->getFilename(), ".nrd"))
    return saveNrrdFile(vd);

  if (vvToolshed::isSuffix(vd->getFilename(), ".tif"))
    return saveTIFSlices(vd, overwrite);

  if (vvToolshed::isSuffix(vd->getFilename(), ".ppm") ||
    vvToolshed::isSuffix(vd->getFilename(), ".pgm"))
    return savePXMSlices(vd, overwrite);

  vvDebugMsg::msg(1, "Error in saveVolumeData: unknown extension");
  return PARAM_ERROR;
}

//----------------------------------------------------------------------------
/** Load volume data from a volume file.
  If filename is undefined, compute default volume.
  @param vd   volume description
  @param sec  bit encoded list of file sections to be loaded (if present in file).
              This value defaults to loading all data in file.
*/
vvFileIO::ErrorType vvFileIO::loadVolumeData(vvVolDesc* vd, LoadType sec, bool addFrame)
{
  vvDebugMsg::msg(1, "vvFileIO::loadVolumeData()");

  ErrorType err = OK;
  char* suffix;

  if (vd==NULL) return PARAM_ERROR;               // volume description missing

  if (vd->getFilename()==NULL || strlen(vd->getFilename()) == 0)
  {
    vd->computeVolume(vd->frames, vd->vox[0], vd->vox[1], vd->vox[2]);
    return OK;
  }

  if (vvToolshed::isFile(vd->getFilename())==false)
  {
    return FILE_NOT_FOUND;
  }

  _sections = sec;

  suffix = new char[strlen(vd->getFilename())+1];
  vvToolshed::extractExtension(suffix, vd->getFilename());

  // Load files according to extension:
  if (vvToolshed::strCompare(suffix, "wl") == 0)
    err = loadWLFile(vd);

  else if (vvToolshed::strCompare(suffix, "rvf") == 0)
    err = loadRVFFile(vd);

  else if (vvToolshed::strCompare(suffix, "xvf") == 0)
    err = loadXVFFile(vd);

  else if (vvToolshed::strCompare(suffix, "avf") == 0)
    err = loadAVFFile(vd);

  else if (vvToolshed::strCompare(suffix, "xb7") == 0)
    err = loadXB7File(vd);

  else if (vvToolshed::strCompare(suffix, "asc") == 0)
    err = loadASCFile(vd);

  else if (vvToolshed::strCompare(suffix, "tga") == 0)
    err = loadTGAFile(vd);

  else if (vvToolshed::strCompare(suffix, "tif") == 0 ||
    vvToolshed::strCompare(suffix, "tiff") == 0)
    err = loadTIFFile(vd, addFrame);

                                                  // VHD CT data
  else if (vvToolshed::strCompare(suffix, "fro") == 0 ||
    vvToolshed::strCompare(suffix, "fre") == 0)
    err = loadVHDCTFile(vd);

                                                  // VHD MRI data
  else if (vvToolshed::strCompare(suffix, "pd") == 0 ||
    vvToolshed::strCompare(suffix, "t1") == 0 ||
    vvToolshed::strCompare(suffix, "t2") == 0 ||
    vvToolshed::strCompare(suffix, "loc") == 0)
    err = loadVHDMRIFile(vd);

                                                  // SGI RGB file
  else if (vvToolshed::strCompare(suffix, "rgb") == 0)
    err = loadRGBFile(vd);

                                                  // PGM file
  else if (vvToolshed::strCompare(suffix, "pgm") == 0 ||
    vvToolshed::strCompare(suffix, "ppm") == 0)   // PPM file
    err = loadPXMRawImage(vd);

                                                  // VHD anatomic
  else if (vvToolshed::strCompare(suffix, "raw") == 0)
    err = loadVHDAnatomicFile(vd);

                                                  // DAT file = raw volume data w/o header information
  else if (vvToolshed::strCompare(suffix, "dat") == 0)
    err = loadRawFile(vd);

                                                  // DICOM file
  else if (vvToolshed::strCompare(suffix, "dcm") == 0 ||
    vvToolshed::strCompare(suffix, "dcom") == 0)
    err = loadDicomFile(vd);

                                                  // VMR file = BrainVoyager anatomical 3D data
  else if (vvToolshed::strCompare(suffix, "vmr") == 0)
    err = loadVMRFile(vd);

                                                  // VTC file = BrainVoyager functional data (time series)
  else if (vvToolshed::strCompare(suffix, "vtc") == 0)
    err = loadVTCFile(vd);

                                                  // NRRD file = Teem nrrd volume file
  else if (vvToolshed::strCompare(suffix, "nrd") == 0 ||
    vvToolshed::strCompare(suffix, "nrrd") == 0)
    err = loadNrrdFile(vd);

                                                  // XIMG = General Electric MRI file
  else if (vvToolshed::strCompare(suffix, "ximg") == 0)
    err = loadXIMGFile(vd);

                                                  // IEEE Visualization Contest format
  else if (vvToolshed::strCompare(suffix, "vis04") == 0)
    err = loadVis04File(vd);

                                                  // Meshviewer header file
  else if (vvToolshed::strCompare(suffix, "hdr") == 0)
    err = loadHDRFile(vd);

  else if (vvToolshed::strCompare(suffix, "volb") == 0)
    err = loadVOLBFile(vd);
                                                  // Microsoft DirectDraw Surface file
  else if (vvToolshed::strCompare(suffix, "dds") == 0)
    err = loadDDSFile(vd);

                                                  // Graham Kent's seismic data (Scripps Institution)
  else if (vvToolshed::strCompare(suffix, "gkent") == 0)
    err = loadGKentFile(vd);

  else if (vvToolshed::strCompare(suffix, "synth") == 0)
    err = loadSynthFile(vd);

  // Unknown extension error:
  else
  {
    vvDebugMsg::msg(1, "Cannot load volume: unknown extension. File name:", vd->getFilename());
    err = PARAM_ERROR;
  }

  delete[] suffix;
  return err;
}

//----------------------------------------------------------------------------
/** Set compression mode for data compression in files.
  This parameter is only used if the file type supports it.
  @param newCompression true = compression on
*/
void vvFileIO::setCompression(bool newCompression)
{
  _compression = newCompression;
}

//----------------------------------------------------------------------------
/** Parse a Leica confocal microscope type file name.
  Example: "Series006_z000_ch00.tif"
  @param fileName name of Leica image file
  @param slice number of slice (>=0)
  @param channel channel number (>=0)
  @param baseName base file name; expects _ALLOCATED_ memory with at least as many bytes as strlen(fileName)+1
          if baseName is NULL, it will be ignored
  @return true if file name was parsed ok, false if file name was not
     recognized as a Leica file.
*/
bool vvFileIO::parseLeicaFilename(const string fileName, int& slice, int& channel, string& baseName)
{
  size_t slicePos, channelPos;
  string sliceText, channelText;
  
  // Find out if and where file name contains slice and channel IDs:
  slicePos   = fileName.rfind("_z");
  channelPos = fileName.rfind("_ch");
  if (slicePos==string::npos || channelPos==string::npos) return false;
  
  // Extract slice and channel IDs:
  sliceText   = fileName.substr(slicePos+2, 3);
  channelText = fileName.substr(channelPos+3, 2);

  slice = atoi(sliceText.c_str());
  channel = atoi(channelText.c_str());
  baseName = fileName.substr(0, slicePos);
  
  return true;
}  

//----------------------------------------------------------------------------
/** Create a Leica confocal microscope type file name.
  Example: "Series006_z000_ch00.tif"
  @param baseName base file name, including path (in above example: "Series")
  @param slice number of slice (>=0)
  @param channel channel number (>=0)
  @param filename resulting file name; must be _allocated_ with at least strlen(baseName)+15 bytes
*/
void vvFileIO::makeLeicaFilename(const char* baseName, int slice, int channel, char* filename)
{
  sprintf(filename, "%s_z%03d_ch%02d.tif", baseName, slice, channel);
}

//----------------------------------------------------------------------------
/** Modifies the filename of a Leica file.
  @param fileName file name to be changed. This string will be modified!
  @param slice new slice number (>=0); -1 for no change
  @param channel new channel number (>=0); -1 for no change
  @return true if successful, false if error
*/
bool vvFileIO::changeLeicaFilename(string& fileName, int slice, int channel)
{
  size_t slicePos;
  size_t channelPos;
  char sliceText[32];
  char channelText[32];

  // Find out if and where file name contains slice and channel IDs:
  slicePos   = fileName.rfind("_z");
  channelPos = fileName.rfind("_ch");
  if (slicePos==string::npos || channelPos==string::npos) return false;
  slicePos += 2;
  channelPos += 3;

  // Make slice and channel IDs:
  if (slice > -1)
  {
    sprintf(sliceText, "%03d", slice);
    fileName.replace(slicePos, 3, sliceText);
  }
  if (channel > -1)
  {
    sprintf(channelText, "%02d", channel);
    fileName.replace(channelPos, 2, channelText);
  }
  return true;
}

//----------------------------------------------------------------------------
/** Merge image or volume files.
  @param vd volume to load slices into
  @param numFiles number of files to load
  @param increment file index increment. default = 1; 
          if 0 then read files alphabetically, ignoring any numbers in the file names
  @param mergeType way to merge files
*/
vvFileIO::ErrorType vvFileIO::mergeFiles(vvVolDesc* vd, int numFiles, int increment, vvVolDesc::MergeType mergeType)
{
  vvFileIO*  fio;
  vvVolDesc* newVD = NULL;                     // newly loaded file, might be just one channel
  vvVolDesc* currentVD = NULL;                 // currently being composited file, might be multiple channels
  string filename;                             // currently processed file name
  string extension;
  string plainFilename;
  string basename;
  string filePath;
  ErrorType ret = OK;                             // this function's return value
  int file  = 0;                                  // index of current file
  bool done = false;
  bool isLeicaFormat;
  int numLeicaChannels = 1;
  int leicaSlice, leicaChannel;
  int i, j;
  list<string> fileNames;
  list<string> dirNames;
  string dir;

  assert(increment >= 0);

  filename = vd->getFilename();
  
  // Extract extension:
  extension = vvToolshed::extractExtension(filename);
    
  isLeicaFormat = parseLeicaFilename(filename, leicaSlice, leicaChannel, basename);
  if (isLeicaFormat)
  {
    // Find out how many channels are available:
    for (i=0; vvToolshed::isFile(filename.c_str()); ++i)
    {
      numLeicaChannels = i;
      changeLeicaFilename(filename, -1, i);
      filename = vd->getFilename();        // reset file name to what it was
    }
    cerr << numLeicaChannels << " Leica channels found." << endl;
    leicaChannel = 0;
    numFiles *= numLeicaChannels;                 // need multiple files per slice
  }
  if (increment==0)   // read files alphabetically?
  {
    string currentDir = vvToolshed::extractDirname(filename);
    vvToolshed::makeFileList(currentDir, fileNames, dirNames);
  }

  fio = new vvFileIO();
  while (!done)
  {
    // Load current file:
    cerr << "Loading file " << (file+1) << ": " << filename << endl;
    newVD = new vvVolDesc(filename.c_str());

    if (fio->loadVolumeData(newVD) != vvFileIO::OK)
    {
      cerr << "Cannot load file: " << filename << endl;
      ret = FILE_ERROR;
      done = true;
    }
    else
    {
      newVD->printInfoLine("Loaded: ");

      // Merge new data to previous data:
      if (isLeicaFormat)
      {
        if (leicaChannel==0) currentVD = new vvVolDesc();

        if(currentVD->merge(newVD, vvVolDesc::VV_MERGE_CHAN2VOL) == vvVolDesc::OK)  cerr << "OK" << endl;

        if (leicaChannel == numLeicaChannels-1)
        {
          vd->merge(currentVD, mergeType);
          delete currentVD;
          currentVD = NULL;
          leicaChannel = 0;
          leicaSlice += increment;
        }
        else ++leicaChannel;
      }
      else vd->merge(newVD, mergeType);

      delete newVD;                            // now the new VD can be released
      newVD = NULL;

      // Find the next file:
      ++file;
      if (file < numFiles || numFiles==0)
      {
        if (isLeicaFormat)
        {
          if (!changeLeicaFilename(filename, leicaSlice, leicaChannel))
          {
            cerr << "Cannot change filename '" << filename << "'." << endl;
            ret = FILE_ERROR;
            done = true;
          }
        }
        else if (increment==0)    // move to next file in ordered list?
        {
          string nextName="";
          filePath = vvToolshed::extractDirname(filename);
          plainFilename = vvToolshed::extractFilename(filename);
          while (!done && nextName=="")
          {
            if (vvToolshed::nextListString(fileNames, plainFilename, nextName)) 
            {
              if (vvToolshed::extractExtension(nextName) != extension) 
              {
                plainFilename = nextName;
                nextName="";
              }
            }
            else done = true;
          }
          filename = filePath + nextName;
        }
        else
        {
          for (j=0; j<increment && !done; ++j)
          {
            if (!vvToolshed::increaseFilename(filename))
            {
              cerr << "Cannot increase filename '" << filename << "'." << endl;
              ret = FILE_ERROR;
              done = true;
            }
          }
        }

        if (!done)
        {
          if (!vvToolshed::isFile(filename.c_str()))
          {
            if (file < numFiles)
            {
              cerr << "File '" << filename << "' expected but not found." << endl;
              ret = FILE_NOT_FOUND;
            }
            done = true;
          }
        }
      }
      else done = true;
    }
  }
  delete fio;
  delete newVD;
  delete currentVD;

  // Set file name to base name of Leica files:
  if (isLeicaFormat)
  {
    filename = vd->getFilename();
    if (parseLeicaFilename(filename, leicaSlice, leicaChannel, basename))
    {
      // add leica specific file convention code here
      basename += ".xvf";
      vd->setFilename(basename.c_str());
    }
  }

  return ret;
}

//----------------------------------------------------------------------------
/** Import transfer function (TF) from another volume file.
  @param vd volume to load transfer function into
  @param filename volume file name of which to copy transfer function
*/
vvFileIO::ErrorType vvFileIO::importTF(vvVolDesc* vd, const char* filename)
{
  ErrorType ret = OK;                             // this function's return value
  
  vvVolDesc* vd2 = new vvVolDesc(filename);
  vvFileIO* fio = new vvFileIO();
  switch (fio->loadVolumeData(vd2, vvFileIO::TRANSFER))
  {
    case vvFileIO::OK: 
      vd->tf.copy(&vd->tf._widgets, &vd2->tf._widgets);
      break;
    default: 
      ret = FILE_ERROR;
      break;
  }
  delete fio;
  delete vd2;
  
  return ret;
}

//============================================================================
// End of File
//============================================================================
