#ifndef __BRICK_MANAGER_H__
#define __BRICK_MANAGER_H__

#include <pthread.h>
#include <map>

#include <vvvecmath.h>
#include <vvvoldesc.h>
#include <vvdebugmsg.h>
#include <vvstopwatch.h>
#include <vvglsl.h>

#include "brickinfo.h"
#include "brickreader.h"

// Looks like support for ARB extensions is missing under win32. So adding it here - Raj
#if defined(_USE_GLARB_UNDER_WIN32) && !defined(__USEGLARBUNDERWIN32__)
#define __USEGLARBUNDERWIN32__

	#define GL_ARRAY_BUFFER_ARB 0x8892
	#define GL_STATIC_DRAW_ARB 0x88E4

	typedef void (APIENTRY * PFNGLBINDBUFFERARBPROC) (GLenum target, GLuint buffer);
	typedef void (APIENTRY * PFNGLDELETEBUFFERSARBPROC) (GLsizei n, const GLuint *buffers);
	typedef void (APIENTRY * PFNGLGENBUFFERSARBPROC) (GLsizei n, GLuint *buffers);
	typedef void (APIENTRY * PFNGLBUFFERDATAARBPROC) (GLenum target, int size, const GLvoid *data, GLenum usage);
	typedef GLvoid* (APIENTRY * PFNGLMAPBUFFERARBPROC) (GLenum target, GLenum access);
	typedef GLboolean (APIENTRY * PFNGLUNMAPBUFFERARBPROC) (GLenum target);
	typedef void (APIENTRY * PFNGLTEXIMAGE3DEXTPROC) (GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height, GLsizei depth, GLint border, GLenum format, GLenum type, const GLvoid *pixels);
	typedef void (APIENTRY * PFNGLTEXSUBIMAGE3DEXTPROC) (GLenum, GLint, GLint, GLint, GLint, GLsizei, GLsizei, GLsizei, GLenum, GLenum, const GLvoid *);

#endif



// reader thread run function
void* brickReaderThreadRun(void* arg);

struct compareBrickInfo
{
  bool operator()(std::vector<int> s1, std::vector<int> s2) const
  {
	if( (s1[0] < s2[0]) || 
	  (s1[0]==s2[0] && s1[1]< s2[1]) || 
	  (s1[0]==s2[0] && s1[1]==s2[1] && s1[2]<s2[2]) )
	{
	  //vvDebugMsg::msg(2, "s1 brick index (l, x, y, z):" , s1[0], s1[1], s1[2], s1[3]);
	  //vvDebugMsg::msg(2, "s2 brick index (l, x, y, z):" , s2[0], s2[1], s2[2], s2[3]);
	  return true;
	}
	  
	return false;
  }
};

namespace MipMapVideoLib
{
  class VolumeInfo
  {
	public:
	  int volIndex;
	  int frameIndex;
	  int brickSize;
	  int maxMipLevel;
	  int numChannel;
	  std::vector<MipMapVideoLib::FileInfo> descList;
  };

  class BrickManager
  {
	public:
	  //BrickManager(int _dim, int _numVolume, std::vector<MipMapVideoLib::VolumeInfo> _volumeInfoList);
	  BrickManager(int _dim, int _numVolume, int _numFrame, MipMapVideoLib::VolumeInfo*** _volumeInfoList);

	  ~BrickManager();

	public:
	  // we will need a way to figure out the size of texture memory
	  //static const int MEMORY_LIMIT = 1024* 1024;			// 1M
	  //static const int MEMORY_LIMIT = 50 * 1024 * 1024; 	// 50M of main memory
	  //static const int MEMORY_LIMIT = 256* 200;

	  //static const int MEMORY_LIMIT = 16 * 16 * 64 * 64 * 3;
	  //static const int MEMORY_LIMIT = 14 * 64 * 64 * 3;
	  //static const int MEMORY_LIMIT = 16 * 7 * 64 * 64 * 3;		// 16*7 bricks

	  static const int MEMORY_LIMIT = 200 * 1024 * 1024;  // 800MB
	  //static const int MEMORY_LIMIT = 100 * 1024 * 1024;

	  //static const int TEXTURE_LIMIT = 14 * 64 * 64 * 3;
	  //static const int TEXTURE_LIMIT = 16 * 7 * 64 * 64 * 3;
	  //static const int TEXTURE_LIMIT = 14 * 128 * 128 * 3;
	  //static const int TEXTURE_LIMIT = 128*128*3 + 100;

	  static const int TEXTURE_LIMIT = 200 * 1024 * 1024; // 100MB
	  //static const int TEXTURE_LIMIT = 16 * 16 * 64 * 64 * 3 * 12; // 36M
	  //static const int TEXTURE_LIMIT = 8 * 4 * 4 * 64 * 64 * 64 * 3; // 36M
	  //static const int TEXTURE_LIMIT = 10 * 64 * 64 * 64 * 3;

	  static const int PREFETCH_WIDTH = 1;
	  static const int PREFETCH_DEPTH = 1;

	public:
	  /*=======================================================
		public methods calls
	  =======================================================*/
	  int initialize();

	  BrickInfo* getBrickInfo(int volNum, int frame, int level, int x, int y, int z);
	  BrickInfo* getBrickInfo(int volNum, int frame, int level, vvVector3 bottomLeft);

	  int preRendering(BrickInfo* brick);
	  int renderBrick(BrickInfo* brick, vvGLSL* shader, GLuint fragProgram);

	  void activateReaders(int frame);
	  void deactivateReaders(int frame);

	  /*=======================================================
		methods called by ReaderThread
	  =======================================================*/
	  // LRU caching mechanism
	  int insertInMemoryList(BrickInfo* brick);
	  int updateInMemoryList(BrickInfo* brick);

	  int insertInTextureList(BrickInfo* brick);
	  int updateInTextureList(BrickInfo* brick);

	  // load data to memory/texture
	  int asyncPreRendering(BrickInfo* brick);
	  int asyncPrefetch(BrickInfo* brick);
	  int loadBrickToMemory(BrickInfo* brick);
	  int loadBrickToTexture(BrickInfo* brick);

	  /*=======================================================
		getter
	  =======================================================*/
	  int getTotalMemoryUsage() { return totalMemoryUsage; }
	  int getTotalTextureUsage() { return totalTextureUsage; }
	  int getMemoryUsagePerBrick() { return memoryUsagePerBrick; }

	  int getNumVolume() { return numVolume; }
	  int getNumFrame() { return numFrame; }
	  vvVolDesc* getVolDesc(int volNum, int frame); 
	  int getBrickSize(int volNum, int frame);

	  int getVolumeSizeX() { return (int)volumeSize[0]; }
	  int getVolumeSizeY() { return (int)volumeSize[1]; }
	  int getVolumeSizeZ() { return (int)volumeSize[2]; }
	  int getDataDimension() { return dim; }

	  int getMaxBrickX(int level) { return (int)maxBrickNum[level][0]; }
	  int getMaxBrickY(int level) { return (int)maxBrickNum[level][1]; }
	  int getMaxBrickZ(int level) { return (int)maxBrickNum[level][2]; }

	  int getCurrentFrame() { return _currentFrame; }
	  void setCurrentFrame(int val) { _currentFrame = val; }

	  // dequeue
	  BrickInfo* getLoadRequestQueueFront();
	  BrickInfo* getPrefetchQueueFront();

	  int getActivateRequestQueueFront();
	  int getDeactivateRequestQueueFront();

	  // enqueue
	  void requestActivateReaders(int frame);
	  void requestDeactivateReaders(int frame);

	protected:
	  vvVector3 volumeSize;

	  int dim;
	  int numVolume;			
	  int numFrame;
	  //std::vector<VolumeInfo> volumeInfoList;	


	  VolumeInfo*** volumeInfoList;				///< VolumeInfo*[volume][frame]

	  // this is really ugly!
	  // the index for map consist of five elements
	  // vector[v]: volume
	  // vector[f]: frame
	  // vector[l]: MIPMAP level
	  // map index[0]: x
	  // map index[1]: y
	  // map index[2]: z
	  //std::vector<std::vector<map<std::vector<int>, BrickInfo*, compareBrickInfo> > > brickTableList; 
	  typedef map<std::vector<int>, BrickInfo*, compareBrickInfo> BrickMap;
	  BrickMap*** brickTableList;

	  //std::vector<BrickReader*> readerList; 	
	  BrickReader*** readerList;
	  //std::vector<vvVolDesc*> vdList;

	  std::map<int, uchar*, std::less<int> > memoryPoolMap;
	  std::list<int> freeBlockList;
	  //std::list<int> freePixelBufferList;
	  std::list<int> freeTexBlockList;

	  //GLuint* pboPoolIds;
	  GLuint* texPoolIds;

	  std::list<BrickInfo*> loadRequestQueue;
	  std::list<BrickInfo*> prefetchRequestQueue;
	  std::list<int> activateRequestQueue;
	  std::list<int> deactivateRequestQueue;

	  int totalMemoryUsage;
	  int totalTextureUsage;
	  int memoryUsagePerBrick;
   
	  // LRU caching list
	  BrickInfo* bricksInMemoryHead;  // this brick will be evicted
	  BrickInfo* bricksInMemoryTail;	// tail of the eviction list

	  BrickInfo* bricksInTextureHead;
	  BrickInfo* bricksInTextureTail;


	  std::vector<vvVector3> maxBrickNum; // stores how many bricks in X, Y, Z exists for each level
	  int windowID;
	  pthread_t tid;

	  int _currentFrame;

	protected:
	  //int getAvailableMemID();
	  int getAvailableMemoryPoolIndex();
	  int getAvailableTexPoolIndex();

	  int evictBrickFromTexture();
	  int evictBrickFromMemory();

	  /*====================================================
		measuring performance
		===================================================*/
	  float memoryLoadingTime;
	  float textureLoadingTime;
	  vvStopwatch* sw;

	public:
	  float getMemoryLoadingTime() { return memoryLoadingTime; }
	  float getTextureLoadingTime() { return textureLoadingTime; }
	  void resetMemoryLoadingTime() { memoryLoadingTime = 0.0; }
	  void resetTextureLoadingTime() { textureLoadingTime = 0.0; }


	private:
	  /*====================================================
		function pointers
		===================================================*/
#if !defined(_USE_GLARB_UNDER_WIN32)
	  typedef void (glTexImage3DEXT_type)(GLenum, GLint, GLenum, GLsizei, GLsizei, GLsizei, GLint, GLenum, GLenum, const GLvoid*);
	  glTexImage3DEXT_type* glTexImage3DEXT;
#else
	  // VBO Extension Function Pointers
	  PFNGLGENBUFFERSARBPROC glGenBuffersARB;					// VBO Name Generation Procedure
	  PFNGLBINDBUFFERARBPROC glBindBufferARB;					// VBO Bind Procedure
	  PFNGLBUFFERDATAARBPROC glBufferDataARB;					// VBO Data Loading Procedure
	  PFNGLDELETEBUFFERSARBPROC glDeleteBuffersARB;
	  PFNGLMAPBUFFERARBPROC glMapBufferARB;
	  PFNGLUNMAPBUFFERARBPROC glUnmapBufferARB;
	  PFNGLTEXIMAGE3DEXTPROC glTexImage3DEXT;
	  PFNGLTEXSUBIMAGE3DEXTPROC glTexSubImage3DEXT;
#endif
  };

}

#endif




// end of file

