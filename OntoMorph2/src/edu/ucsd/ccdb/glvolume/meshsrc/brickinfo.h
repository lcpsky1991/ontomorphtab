
#ifndef _BRICK_INFO_H_
#define _BRICK_INFO_H_

#include <vector>

#include <vvvecmath.h>
#include <vvopengl.h>
#include <vvvoldesc.h>
#include <vvglsl.h>


namespace MipMapVideoLib
{

  //============================================================================
  // Class Definitions
  //============================================================================
  /** BrickInfo Class
	BrickInfo represents a quad in 2D space and a cube in 3D space

	corners are saved in the following way:
	  [0]: bottom left
	  [1]: top left
	  [2]: top right
	  [3]: bottom right

		5____ 6       
	   /___ /|        Y
	 1| | 2| |        |  / Z
	  | 4 -|-/7       | /
	  |/___|/         |/____ X
	  0    3

  */
  class BrickInfo
  {
	public:
	  BrickInfo(int _level, int _volNum, int _frameIndex, int _numChannel, int _brickSize);
	  virtual ~BrickInfo();

	public:
	  virtual void initialize(vvVolDesc* vd, vvMatrix* transformMatrix, vvVector3 eye, vvVector3 _normal, vvVector3 _volumeCenter, bool isOrtho, int pixelToVoxelRatio) = 0;
	  virtual int initializeChannelTexture(int c) = 0;
	  virtual int draw(vvGLSL* _shader, GLuint _fragProgram) = 0;
	  virtual vvVector3 getCoord(int i) = 0;

	  void setMemoryBuf(int c, uchar* p) { memoryBuf[c] = p;}
	  void setMemID(int c, int id) { memID[c] = id; }

	  // Tex memory
	  void setTexName(int c, GLuint _texName) { texName[c] = _texName; };
	  void setTexIndex(int c, int _index) { texIndex[c] = _index; }
	  void resetTex();

	  void setInMemory(bool val) { inMemory = val; }
	  void setInTexture(bool val) { inTexture = val; }
	  void setFrame(unsigned long _frame) { frame = _frame; }
	  void setPrefetchEnqueued(bool val) { prefetchEnqueued = val; }

	  ///////////////////////////////////////////////
	  // getters
	  ///////////////////////////////////////////////

	  vvVector3 getCenter() { return center;}
	  vvVector3 getActualSize() { return actualSize; }

	  int getVolNum() { return volNum; }
	  int getFrameIndex() { return frameIndex; }
	  int getLevel() { return level; }
	  int getNumChannel() { return numChannel; }
	  int getBrickSize() { return brickSize; }

	  int getBrickX() { return brickX; }
	  int getBrickY() { return brickY; }
	  int getBrickZ() { return brickZ; }

	  bool isInMemory() { return inMemory; }
	  bool isInTexture() { return inTexture; }
	  bool isRendered() { return rendered; }
	  bool getFiner() { return finer; }
	  bool isPrefetchEnqueued() { return prefetchEnqueued; }

	  GLuint getTexName(int c) { return texName[c]; }
	  //GLuint getPBOName(int c) { return pboName[c]; }

	  int getTexIndex(int c) { return texIndex[c]; }
	  //int getPBOIndex(int c) { return pboIndex[c]; }

	  float getDistance() { return distance; }
	  float getCost() { return cost; }


	  int getMemID(int c) { return memID[c]; }
	  uchar* getMemoryBuf(int c) { return memoryBuf[c]; }

	public:
	  // LRU cache list for main memory
	  BrickInfo* prevInMemory;
	  BrickInfo* nextInMemory;

	  // LRU cache list for texture memory
	  BrickInfo* prevInTexture;
	  BrickInfo* nextInTexture;

	protected:

	  vvVector3 center;				///< the center of this brick [world coordinate]
	  vvVector3 actualSize;			///< the (world) coordinate of this quad that overlaps with the original image
	  vvVector3 normal;				///< normal vector to slice planes, used only for 3D rendering
	  vvVector3 volumeCenter;		///< the center of the entire volume. used for 3D rendering

	  vvVolDesc* vd;

	  int level;						///< MIP map level
	  int volNum;						///< volume id
	  int frameIndex;					///< frame index
	  int numChannel;					///< number of channel
	  int brickSize;					///< brick size in pixel

	  int brickX;						///< (brickX, brickY) denotes the relative location between bricks
	  int brickY;						///< (0, 0) starts from left-bottom corner
	  int brickZ;						///< 1 if the data is 2D image

	  //=========================================================================
	  // time-dependent variables
	  // this changes according to 
	  //
	  // 1) translation & rotation (visibility)
	  // 2) memory caching mechanism

	  bool inMemory;					///< specifies whether the texture of this brick is loaded into memory
	  bool inTexture;					///< specifies whether the texture of this brick is copied to texture
	  bool rendered;					///< this is initialized in setCost()
	  bool finer;						///< indicates if this quad can be splitted to four finer quads, 
	  bool prefetchEnqueued;			///< indicates if this brick is enqueued in prefetch queue

	  int* memID;
	  uchar** memoryBuf;
	  //int* pboIndex;					///< index for pixel buffer pool, each channel has its own id
	  int* texIndex;					///< index for texture pool, each channel has its own id
	  //GLuint* pboName;				///< pixel buffer object names of this brick
	  GLuint* texName;				///< texture names of this brick

	  vector<vvVector3> viewPortCorners; /// the (viewport) coordinates of corners of this brick;
	  vvVector4 boundingBox;			///< (minX, minY, maxX, maxY) in viewport coordinate

	  float distance;					///< distance from eye to center in world coordinate system
	  float cost;						///< the value that determines its priority in submeshing process, the higher cost, the higher priority to be finer
									  ///< if this quad is mapped to a smaller resolution than the size of BRICK_SIZE*BRICK_SIZE, 
									  ///< then there is no need to be finer

	  double timeUsed;
	  unsigned long frame;			///< LRU caching information. denotes at which frame this brick is used to render 

	  //=========================================================================

	protected:

	  //=========================================================================
	  // setters
	  // all variables are set inside the constructor
	  // no need to be called from outside
	  //=========================================================================
	  void setDistance(vvVector3 eye, bool isOrtho);

	  virtual void setCenter() = 0;
	  virtual void setViewPortCorners(vvMatrix* transformMatrix) = 0;
	  virtual void setCost(int pixelToVoxelRatio) = 0;
	  //virtual void setFiner(int ratio) = 0;
	  virtual void setBoundingBox() = 0;

  };	// end of BrickInfo class definition


}

#endif

