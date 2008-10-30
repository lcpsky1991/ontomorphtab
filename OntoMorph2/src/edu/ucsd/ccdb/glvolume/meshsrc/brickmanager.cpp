#include <vvopengl.h>
#include <vvglext.h>

#include <map>
#include <vector>
#include <math.h>
#include <time.h>
#ifndef WIN32
#include <unistd.h>
#endif
#include <GL/glut.h>

#include <vvdebugmsg.h>
#include <vvdynlib.h>

#include "brickmanager.h"
#include "cubeinfo.h"
#include "quadinfo.h"

using namespace std;
using namespace MipMapVideoLib;

/*

*/
//BrickManager::BrickManager(int _dim, int _numVolume, std::vector<VolumeInfo> _volumeInfoList): dim(_dim), numVolume(_numVolume)
BrickManager::BrickManager(int _dim, int _numVolume, int _numFrame, VolumeInfo*** _volumeInfoList): dim(_dim), numVolume(_numVolume), numFrame(_numFrame)
{
  volumeInfoList = _volumeInfoList;

  /*
  for(int v = 0; v < _numVolume; v++)
  {
	for(int f = 0; f < _numFrame; f++)
	{
	  cerr << "volumeInfoList: " << v << " " << f << " " << volumeInfoList[v][f]->descList[0].name << endl;
	}
  }
  */

  totalMemoryUsage = 0;
  totalTextureUsage = 0;

  bricksInMemoryHead = NULL;
  bricksInMemoryTail = NULL;
  bricksInTextureHead = NULL;
  bricksInTextureTail = NULL;

  //windowID = glutGetWindow();
  //vvDebugMsg::msg(1, "window number: ", windowID);

  /*
  int error;
  if(error = pthread_create(&tid, NULL, brickReaderThreadRun, this))
  {
	vvDebugMsg::msg(1, "Failed to create thread: ", strerror(error));
	exit(0);
  }
  */

  sw = new vvStopwatch();
  sw->start();
  memoryLoadingTime = 0.0;
  textureLoadingTime = 0.0;

  _currentFrame = 0;

  // extract function pointers for ARB calls - Raj
#ifdef WIN32
	glGenBuffersARB = (PFNGLGENBUFFERSARBPROC)wglGetProcAddress("glGenBuffersARB");
	glBindBufferARB = (PFNGLBINDBUFFERARBPROC)wglGetProcAddress("glBindBufferARB");
	glBufferDataARB = (PFNGLBUFFERDATAARBPROC)wglGetProcAddress("glBufferDataARB");

	glMapBufferARB = (PFNGLMAPBUFFERARBPROC)wglGetProcAddress("glMapBufferARB");
	glUnmapBufferARB = (PFNGLUNMAPBUFFERARBPROC)wglGetProcAddress("glUnmapBufferARB");

	// do a quick test to make sure all pointers are valid

	if( !glGenBuffersARB || !glBindBufferARB || !glBufferDataARB || 
		!glMapBufferARB || !glUnmapBufferARB)
	{
		vvDebugMsg::msg(1, "Failed to get pointers to one or more ARB calls ", strerror(2));
		exit(0);
	}

#endif


}

/*

*/
BrickManager::~BrickManager()
{
  vvDebugMsg::msg(1, "BrickManager::~BrickManager()");

  //volumeInfoList = NULL;

  //delete[] pboPoolIds;
  delete[] texPoolIds;
  delete sw;

  for(int i = 0; i < numVolume; i++)
	delete[] readerList[i];
  delete readerList;
  //readerList.clear();

  // FIXME: clean up brickTableList!!!
  for(int v = 0; v < numVolume; v++)
  {
	for(int f = 0; f < numFrame; f++)
	  delete[] brickTableList[v][f];
	delete[] brickTableList[v];
  }
  delete[] brickTableList;

}




/*

*/
int BrickManager::initialize()
{
  //assert(volumeInfoList.size() > 0);

  // TODO: we are assuming all the images have the same size!!!
  // this should be changed to a vector
  volumeSize.set(volumeInfoList[0][0]->descList[0].sizeX,
	  			volumeInfoList[0][0]->descList[0].sizeY,
	  			volumeInfoList[0][0]->descList[0].sizeZ);

#ifndef WIN32
  glTexImage3DEXT = (glTexImage3DEXT_type*)vvDynLib::glSym("glTexImage3D");
#else
    glTexImage3DEXT = (PFNGLTEXIMAGE3DEXTPROC)vvDynLib::glSym("glTexImage3D");
#endif
  if(glTexImage3DEXT == NULL) 
  {
	vvDebugMsg::msg(1, "The graphic card in this machine does not support glTexImage3D");
	return -1;
  }

#if 0
  // remember number of bricks in X, Y and Z direction
  for(int l = 0; l <= volumeInfoList[0][0]->maxMipLevel; l++)
  {
	int stepSize = volumeInfoList[0][0]->brickSize * (int)powf((float)2.0, (float)l);

	vvVector3 maxBrick;

	// for future use...
	int maxBrickX = (int)ceilf(volumeSize[0]/stepSize);
	int maxBrickY = (int)ceilf(volumeSize[1]/stepSize);
	int maxBrickZ = (int)ceilf(volumeSize[2]/stepSize);

	vvDebugMsg::msg(1, "level, max_x, max_y, max_z: ", l, maxBrickX, maxBrickY, maxBrickZ);

	maxBrick.set(maxBrickX, maxBrickY, maxBrickZ);
	maxBrickNum.push_back(maxBrick);
  }
#endif

  brickTableList = new BrickMap**[numVolume];
  readerList = new BrickReader**[numVolume];

  cerr << "BrickManager::initialize(): numVolume: " << numVolume << ", numFrame: " << numFrame << endl;

  for(int v = 0; v < numVolume; v++)
  {
	brickTableList[v] = new BrickMap*[numFrame];
	readerList[v] = new BrickReader*[numFrame];

	for(int f = 0; f < numFrame; f++)
	  brickTableList[v][f] = new BrickMap[volumeInfoList[v][f]->maxMipLevel + 1];
  }

  for(int v = 0; v < numVolume; v++)
  {
	//cerr << "volume#" << v << endl;

	for(int f = 0; f < numFrame; f++)
	{
	  //cerr << "frame#" << f << endl;

	  //======================================================
	  // 2. initialize BrickReader
	  //======================================================
	  BrickReader* aReader = new BrickReader();

	  vvDebugMsg::msg(2, "numChannel, brickSize, maxMipLevel:", 
						  volumeInfoList[v][f]->numChannel, 
						  volumeInfoList[v][f]->brickSize, 
						  volumeInfoList[v][f]->maxMipLevel);

	  if(aReader->init(dim, volumeInfoList[v][f]->maxMipLevel, volumeInfoList[v][f]->brickSize, volumeInfoList[v][f]->descList) < 0)
	  {
		vvDebugMsg::msg(1, "BrickReader init error");
		return -1;
	  }

	  //readerList.push_back(aReader);
	  readerList[v][f] = aReader;

	  // first we deactivate readers
	  if(numFrame > 10)
		readerList[v][f]->deactivate();

	  //======================================================
	  // 3. get voldesc from reader
	  //======================================================
	  /*
	  vvVolDesc* vd = aReader->getVolDesc();
	  if(vd == NULL)
	  {
		vvDebugMsg::msg(1, "VolDesc is NULL");
		return -1;
	  }
	  vdList.push_back(vd);
	  */

	  // (Optional) 3. On-the-fly downsampling


	  //======================================================
	  // 4. Build up a brick table that contains 
	  //======================================================
	  //vector<map<vector<int>, BrickInfo*, compareBrickInfo> > aBrickTable;

	  for(int l = 0; l <= volumeInfoList[v][f]->maxMipLevel; l++)
	  {
		//map<vector<int>, BrickInfo*, compareBrickInfo> aMap;
		BrickMap aMap;

		int stepSize = volumeInfoList[v][f]->brickSize * (int)powf((float)2.0, (float)l);
		vvDebugMsg::msg(2, "(level, stepSize): ", l, stepSize);
		
		for(int i = 0; i < volumeSize[0]; i+= stepSize)
		{
		  for(int j = 0; j < volumeSize[1]; j+= stepSize)
		  {

			// 3D volume 
			if(dim == 3)
			{
			  for(int k = 0; k < volumeSize[2]; k += stepSize)
			  {
				//vvDebugMsg::msg(2, "L: (x, y): ", l, (int)i/stepSize, (int)j/stepSize);

				vvVector3 bottomLeft = vvVector3(i, j, k);
				vvVector3 topRight = vvVector3(i + stepSize, j + stepSize, k+stepSize);

				//vvDebugMsg::msg(2, "*** i, brickSize ", v, volumeInfoList[v].brickSize);
				BrickInfo *bi = new CubeInfo(bottomLeft, 
											  topRight, 
											  l, 
											  volumeInfoList[v][f]->volIndex, 
											  volumeInfoList[v][f]->frameIndex,
											  volumeInfoList[v][f]->numChannel, 
											  volumeInfoList[v][f]->brickSize);

				vector<int> index;
				index.push_back(bi->getBrickX());
				index.push_back(bi->getBrickY());
				index.push_back(bi->getBrickZ());

				aMap[index] = bi;

				//vvDebugMsg::msg(2, "brick index (v, l):", v, l);
				//vvDebugMsg::msg(2, "brick index (x, y, z):" , bi->getBrickX(), bi->getBrickY(), bi->getBrickZ());
			  }
			} 
			// 2D Image
			else if (dim == 2)
			{
			  vvVector3 bottomLeft = vvVector3(i, j, 0);
			  vvVector3 topRight = vvVector3(i + stepSize, j + stepSize, 0);

			  //cerr << "volumeInfoList[v][f]->frameIndex: " << volumeInfoList[v][f]->frameIndex << endl;

			  BrickInfo* bi = new QuadInfo(bottomLeft, 
										  topRight, 
										  l, 
										  volumeInfoList[v][f]->volIndex, 
										  volumeInfoList[v][f]->frameIndex,
										  volumeInfoList[v][f]->numChannel,
										  volumeInfoList[v][f]->brickSize);

			  vector<int> index;
			  index.push_back(bi->getBrickX());
			  index.push_back(bi->getBrickY());
			  index.push_back(0);

			  aMap[index] = bi;
			}
		  }
		}

		//aBrickTable.push_back(aMap);
		brickTableList[v][f][l] = aMap;
	  }

	  //brickTableList.push_back(aBrickTable);
	}
  }
  //vvDebugMsg::msg(2, "BrickManager loaded all BrickInfo and created a list of maps: size=", (int)brickTableList.size());
  //vvDebugMsg::msg(2, "BrickManager loaded all BrickInfo and created a list of maps: size=", (int)brickTableList[0].size());


  //==================================================================
  // 5. memory pool
  //==================================================================
  vvDebugMsg::msg(2, "Memory Pool initialization");

  //==================================================================
  // 5.1. initialize Pixel Buffer Object
  //==================================================================

  // FIXME: what if we have different brick sizes in multiple volumes?
  // FIXME: byte per channel???
  memoryUsagePerBrick = (int)powf((float)volumeInfoList[0][0]->brickSize, (float)dim);
  //memoryUsagePerBrick = memoryUsagePerBrick * volumeInfoList[0][0]->numChannel;

  int poolSize = MEMORY_LIMIT / memoryUsagePerBrick;

  for(int i = 1; i <= poolSize; i++)
  {
	uchar* p = new uchar[memoryUsagePerBrick];
	memoryPoolMap[i] = p;
	freeBlockList.push_back(i);
  }

  //==================================================================
  // 5.2. initialize texture memory pool
  //==================================================================
  int texturePoolSize = TEXTURE_LIMIT / memoryUsagePerBrick;
  texPoolIds = new GLuint[texturePoolSize];
  glGenTextures(texturePoolSize, texPoolIds);

  uchar* p = new uchar[memoryUsagePerBrick];
  assert(p != NULL);
  memset(p, 0, memoryUsagePerBrick);

  glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

  for(int i = 0; i < texturePoolSize; i++)
  {
	assert(texPoolIds[i] != 0);
	if(dim == 2)
	{
	  //vvDebugMsg::msg(1, "texPoolIds at ", i, (int)texPoolIds[i]);
	  glBindTexture(GL_TEXTURE_2D, texPoolIds[i]);

	  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

	  glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, 
	  				volumeInfoList[0][0]->brickSize, volumeInfoList[0][0]->brickSize, 
					0, GL_LUMINANCE, GL_UNSIGNED_BYTE, p);
	  glBindTexture(GL_TEXTURE_2D, 0);
	}
	else
	{
	  glBindTexture(GL_TEXTURE_3D_EXT, texPoolIds[i]);
	  glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

	  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_WRAP_S, GL_CLAMP);
	  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_WRAP_T, GL_CLAMP);
	  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_WRAP_R_EXT, GL_CLAMP);
	  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_MIN_FILTER, GL_CLAMP);
	  glTexParameteri(GL_TEXTURE_3D_EXT, GL_TEXTURE_MAG_FILTER, GL_CLAMP);

	  glTexImage3DEXT(GL_PROXY_TEXTURE_3D_EXT, 0, GL_LUMINANCE,
		  			volumeInfoList[0][0]->brickSize, volumeInfoList[0][0]->brickSize,
					volumeInfoList[0][0]->brickSize, 0, GL_LUMINANCE,
					GL_UNSIGNED_BYTE, NULL);
	  glTexImage3DEXT(GL_TEXTURE_3D_EXT, 0, GL_LUMINANCE,
		  			volumeInfoList[0][0]->brickSize, volumeInfoList[0][0]->brickSize,
					volumeInfoList[0][0]->brickSize, 0, GL_LUMINANCE,
					GL_UNSIGNED_BYTE, p);
	  glBindTexture(GL_TEXTURE_3D_EXT, 0);
	}

	freeTexBlockList.push_back(i);
  }

  delete[] p;

  vvDebugMsg::msg(2, "BrickManager::initialize() done");

  return 0;
}


int BrickManager::asyncPreRendering(BrickInfo* brick)
{
  //int rc;
  
  if(brick->isInMemory())
	updateInMemoryList(brick);

  if(brick->isInTexture())
  {
	updateInTextureList(brick);
	return 0;
  }

  if(!brick->isInMemory())
  {
	loadRequestQueue.push_back(brick);
	//cerr << "brick enqueued loadRequestQueue" << endl;
  }

  return 0;
}

int BrickManager::asyncPrefetch(BrickInfo* brick)
{
  if(brick->isInMemory())
	updateInMemoryList(brick);
  if(brick->isInTexture())
  {
	updateInTextureList(brick);
	return 0;
  }

  if(!brick->isInMemory())
  {
	prefetchRequestQueue.push_back(brick);
	brick->setPrefetchEnqueued(true);
	//cerr << "brick enqueued prefetchRequestQueue" << endl;
  }

  return 0;
}


/** pre-rendering step
  check if data is already loaded
  if not, load the data into memory and texture
  LRU information is also updated here
*/
int BrickManager::preRendering(BrickInfo* brick)
{
  int rc;
  int texIndex;

  if(brick->isInMemory())
	updateInMemoryList(brick);
  
  if(brick->isInTexture())
  {
	updateInTextureList(brick);
	return 0;
  }

  //float start = sw->getTime();

  // we know we don't have data in texture memory
  // assert(!brick->isInTexture());

  if(!brick->isInMemory())
  {
	for(int c = 0; c < brick->getNumChannel(); c++)
	{
	  //if(!activeChannel[c])
		//continue;

	  if(totalMemoryUsage + memoryUsagePerBrick > MEMORY_LIMIT)
	  {
		if(evictBrickFromMemory() < 0)
		{
		  vvDebugMsg::msg(1, "There is no main memory left" );
		  return -1;
		}
	  }

	  int memID = getAvailableMemoryPoolIndex();

	  if(memID == -1)
	  {
		vvDebugMsg::msg(1, "[memory pool] can't get a free memory block");
		return -1;
	  }
	  brick->setMemID(c, memID);
	  brick->setMemoryBuf(c, memoryPoolMap[memID]);
	  assert(brick->getMemoryBuf(c));

	  // update totalMemoryUsage
	  totalMemoryUsage += memoryUsagePerBrick;

  	  //vvDebugMsg::msg(2, "[cache] brick is loaded in memory: (l, x, y, z):", brick->getLevel(), brick->getBrickX(), brick->getBrickY(), brick->getBrickZ());

  	  if((rc = readerList[brick->getVolNum()][brick->getFrameIndex()]->getOneChannelData(brick->getLevel(), brick->getBrickX(), brick->getBrickY(), brick->getBrickZ(), c, brick->getMemoryBuf(c))) < 0)
  	  {
		vvDebugMsg::msg(2, "BrickReader::getMultiChannelData error");
		return rc;
  	  }
	}

	insertInMemoryList(brick);
	brick->setInMemory(true);
  }

  //memoryLoadingTime += sw->getTime() - start;

  // up to this point, we have data in memory buffers 
  // and we know the id for the buffers
  //==================================================================
  
  for(int c = 0; c < brick->getNumChannel(); c++)
  {
	//if(!activeChannel[c])
	  //continue;

	if(totalTextureUsage + memoryUsagePerBrick > TEXTURE_LIMIT)
	  if(evictBrickFromTexture() < 0)
	  {
		vvDebugMsg::msg(1, "There is no texture memory left");
		return -1;
	  }

	if((texIndex = getAvailableTexPoolIndex()) == -1)
	{
	  cerr << "BrickManager::getAvailableTexPoolIndex error" << endl;
	  return -1;
	}

	brick->setTexIndex(c, texIndex);
	brick->setTexName(c, texPoolIds[texIndex]);

	brick->initializeChannelTexture(c);

	totalTextureUsage += memoryUsagePerBrick;
  }

  insertInTextureList(brick);
  brick->setInTexture(true);

  return 0;
}

/*

*/
int BrickManager::renderBrick(BrickInfo* brick, vvGLSL* shader, GLuint fragProgram)
{
  // prerendering step must have loaded the data in texture memory
  assert(brick->isInTexture());

  // 3. rendering
  if(brick->draw(shader, fragProgram) < 0)
  {
	vvDebugMsg::msg(2, "BrickInfo::draw() failed");
	return -1;
  }

  return 0;
}

void BrickManager::requestActivateReaders(int frame)
{
  assert(frame < numFrame && frame >= 0);

  activateRequestQueue.push_back(frame);
}

void BrickManager::requestDeactivateReaders(int frame)
{
  assert(frame < numFrame && frame >= 0);
  deactivateRequestQueue.push_back(frame);
}


void BrickManager::activateReaders(int frame)
{
  assert(frame < numFrame && frame >= 0);

  //vvDebugMsg::msg(1, "Frame activated: ", frame);
  for(int v = 0; v < numVolume; v++)
	readerList[v][frame]->activate();
}

void BrickManager::deactivateReaders(int frame)
{
  assert(frame < numFrame && frame >= 0);

  //vvDebugMsg::msg(1, "Frame deactivated: ", frame);

  for(int v = 0; v < numVolume; v++)
	readerList[v][frame]->deactivate();
}

//============================================================
// Methods called by ReaderThread
//============================================================


/* Insert the speicifed brick into InTextureList

   BrickInfo itself is a list element,
   manipulating prev/next pointers are enough
   @param brick one that is recently loaded
*/
int BrickManager::insertInTextureList(BrickInfo* brick)
{
  assert(brick != NULL);

  if(bricksInTextureHead == NULL)
  {
	// list is empty
	bricksInTextureHead = brick;
	bricksInTextureTail = brick;
	brick->prevInTexture = NULL;
	brick->nextInTexture = NULL;
  } else {
	// insert at the end
	brick->prevInTexture = bricksInTextureTail;
	brick->nextInTexture = NULL;
	bricksInTextureTail->nextInTexture = brick;
	bricksInTextureTail = brick;
  }

  return 0;
}

/* Move the specified brick to the end of InTextureList

   BrickInfo itself is a list element,
   manipulating prev/next pointers are enough
   @param brick one that is recently used
*/
int BrickManager::updateInTextureList(BrickInfo* brick)
{
  assert(bricksInTextureTail != NULL);
  assert(brick != NULL);

  // remove brick, adjust pointers of which bricks pointing to the brick
  if(brick->nextInTexture != NULL)
  {
	// move to the back (recently used)
	if(brick->prevInTexture != NULL)
	  brick->prevInTexture->nextInTexture = brick->nextInTexture;
	if(bricksInTextureHead == brick)
	  bricksInTextureHead = brick->nextInTexture;
	  
	brick->nextInTexture->prevInTexture = brick->prevInTexture;
	brick->prevInTexture = bricksInTextureTail;
	brick->nextInTexture = NULL;
	bricksInTextureTail->nextInTexture = brick;
	bricksInTextureTail = brick;
  }

  return 0;
}

/** Reclaims the texture memory of the least recently used brick.
   use glDeleteTexture to actually deallocate the memory
   another good chunk of operations are pointer operation, to update the InTextureList
*/
int BrickManager::evictBrickFromTexture()
{
  BrickInfo* brick;			// pointing to the brick to be deleted

  assert(bricksInTextureHead != NULL);
  assert(bricksInTextureHead->prevInTexture == NULL);

  // find a brick to be evicted;
  brick = bricksInTextureHead;
  //while(brick != NULL && brick->isRendered())
	//brick = brick->nextInTexture;

  if(brick == NULL)
  {
	vvDebugMsg::msg(1, "[cache] Not Enough Memory !!: ", totalTextureUsage);
	return -1;
  }

  //vvDebugMsg::msg(2, "[cache] texture memory eviction: ", brick->getLevel(), brick->getBrickX(), brick->getBrickY());
 
  // update memory usage information
  //totalTextureUsage -= brick->getMemoryUsage();

  // update list
  //if(brick == bricksInTextureHead)
  //{
	bricksInTextureHead = brick->nextInTexture;
	brick->nextInTexture->prevInTexture = NULL;
	/*
  }
  else 
  {
	if(brick->prevInTexture != NULL)
	  brick->prevInTexture->nextInTexture = brick->nextInTexture;
	if(brick->nextInTexture != NULL)
	  brick->nextInTexture->prevInTexture = brick->prevInTexture;
  }
  */

  // return the index
  for(int c = 0; c < brick->getNumChannel(); c++)
  {
	freeTexBlockList.push_back(brick->getTexIndex(c));
	totalTextureUsage -= memoryUsagePerBrick;
  }

  brick->resetTex();
  brick->setInTexture(false);

  return 0;
}

/* reclaim the main memory of the brick that is the least recently used
   for the current version uses memory pool mechansim, it returns the id it was using
*/ 
int BrickManager::evictBrickFromMemory()
{
  BrickInfo* brick;

  assert(bricksInMemoryHead != NULL);

  brick = bricksInMemoryHead;
  //while(brick != NULL && brick->isRendered())
	//brick=brick->nextInMemory;

  if(brick == NULL)
  {
	vvDebugMsg::msg(1, "[cache] Not Enough Memory!!");
	return -1;
  }

  //vvDebugMsg::msg(2, "[cache] main memory eviction: ", brick->getLevel(), brick->getBrickX(), brick->getBrickY());

  // memory is full, so there must exist other bricks in the list
  assert(brick->nextInMemory != NULL); 

  //if(brick == bricksInMemoryHead)
  //{
	bricksInMemoryHead = brick->nextInMemory;
	brick->nextInMemory->prevInMemory = NULL;
	/*
  } else {
	if(brick->prevInMemory != NULL)
	  brick->prevInMemory->nextInMemory = brick->nextInMemory;
	if(brick->nextInMemory != NULL)
	  brick->nextInMemory->prevInMemory = brick->prevInMemory;
  }
  */

  // reclaim memory and reset members
  for(int c = 0; c < brick->getNumChannel(); c++)
  {
	freeBlockList.push_back(brick->getMemID(c));
	totalMemoryUsage -= memoryUsagePerBrick;
	brick->setMemoryBuf(c, NULL);
	brick->setMemID(c, -1);
  }

  //brick->resetPBO();
  brick->setInMemory(false);

  return 0;
}




/** Insert the specified brick into InMemoryList
   BrickInfo itself is a list element,
   manipulating prev/next pointers are enough

   @param brick one that is recently loaded
*/
int BrickManager::insertInMemoryList(BrickInfo* brick)
{
  assert(brick != NULL);

  // list is empty
  if(bricksInMemoryHead == NULL)
  {
	bricksInMemoryHead = brick;
	bricksInMemoryTail = brick;
	brick->prevInMemory = NULL;
	brick->nextInMemory = NULL;
  }
  // inserting at the end
  else 
  {
	brick->prevInMemory = bricksInMemoryTail;
	brick->nextInMemory = NULL;
	bricksInMemoryTail->nextInMemory = brick;
	bricksInMemoryTail = brick;
  }

  return 0;
}

/* Move the specified brick to the end of InMemoryList

   BrickInfo itself is a list element,
   manipulating prev/next pointers are enough
   @param brick one that is recently used
*/
int BrickManager::updateInMemoryList(BrickInfo* brick)
{
  assert(bricksInMemoryTail != NULL);
  assert(brick != NULL);

  if(brick->nextInMemory != NULL)
  {
	if(brick->prevInMemory != NULL)
	  brick->prevInMemory->nextInMemory = brick->nextInMemory;
	  
	if(bricksInMemoryHead == brick)
	  bricksInMemoryHead = brick->nextInMemory;
	brick->nextInMemory->prevInMemory = brick->prevInMemory;
	brick->prevInMemory = bricksInMemoryTail;
	brick->nextInMemory = NULL;
	bricksInMemoryTail->nextInMemory = brick;
	bricksInMemoryTail = brick;
  }

  return 0;
}


//============================================================
// BrickManager Getter Methods
//============================================================

/*

*/
BrickInfo* BrickManager::getBrickInfo(int volNum, int frame, int level, int x, int y, int z)
{
  //vvDebugMsg::msg(2, "BrickManager::getBrickInfo(): level, x, y ", level, x, y);

  vector<int> index;
  index.push_back(x);
  index.push_back(y);
  index.push_back(z);

  assert((dim == 3) || (dim == 2 && z == 0));
  assert(volNum >= 0 && volNum < numVolume);
  assert(frame >= 0 && frame < numFrame);
  assert(level >= 0 && level <= volumeInfoList[volNum][frame]->maxMipLevel);

  BrickInfo* bi = brickTableList[volNum][frame][level][index];
  //cerr << "BrickManager::getBrickInfo: frame=" << bi->getFrameIndex() << endl;

  assert(bi != NULL);
  return bi;
}


/*

*/
BrickInfo* BrickManager::getBrickInfo(int volNum, int frame, int level, vvVector3 bottomLeft)
{
  //vvVector3 coord = bottomLeft.getImageCoord();

  int x = (int)(bottomLeft[0])/volumeInfoList[volNum][frame]->brickSize/(int)pow(2.0, (double)level);
  int y = (int)(bottomLeft[1])/volumeInfoList[volNum][frame]->brickSize/(int)pow(2.0, (double)level);
  int z = (int)(bottomLeft[2])/volumeInfoList[volNum][frame]->brickSize/(int)pow(2.0, (double)level);

  return getBrickInfo(volNum, frame, level, x, y, z);
}



vvVolDesc* BrickManager::getVolDesc(int volNum, int frame) 
{
  //cerr << "Frame: " << frame << endl;
  assert(volNum >= 0 && volNum < numVolume);
  assert(frame >= 0 && frame < numFrame);

  return readerList[volNum][frame]->getVolDesc();
}



int BrickManager::getBrickSize(int volNum, int frame)
{
  assert(volNum >= 0 && volNum < numVolume);

  assert(frame >= 0 && frame < numFrame);

  return volumeInfoList[volNum][frame]->brickSize;
}


/* retrieve an index of free pixel buffer in PBO pool
   @return -1 if error, a valid index otherwise
*/
int BrickManager::getAvailableMemoryPoolIndex()
{
  if(freeBlockList.empty())
  {
	cerr << "There is no available pixel buffer left, evict first" << endl;
	return -1;
  }

  int idx = (int)freeBlockList.front();
  freeBlockList.pop_front();

  return idx;
}



/* retrieve an index of free texture buffer in texture pool
   @return -1 if error, a valid index otherwise
*/
int BrickManager::getAvailableTexPoolIndex()
{
  if(freeTexBlockList.empty())
  {
	cerr << "There is no available texture block left, evict first" << endl;
	return -1;
  }

  int idx = (int)freeTexBlockList.front();
  freeTexBlockList.pop_front();

  return idx;
}


/** Thread function 
  dequeue an element from LoadRequestQueue and load data
  enqueuing happens at BrickManager preRendering() steps
*/
void* brickReaderThreadRun(void* arg)
{
  BrickManager* brickManager = (BrickManager*)(arg);

  //glutSetWindow(brickManager->getWindowID());
  //unsigned int t[2];
  //glGenTextures(2, t);
  //vvDebugMsg::msg(1, "glGenTextures: ", (int)t[0], (int)t[1]);
  //vvDebugMsg::msg(1, "error: ", (char*)gluErrorString(glGetError()));

#if 0
  while(true)
  {
	BrickInfo* front = brickManager->getLoadRequestQueueFront();

	if(front == NULL)
	{
	  //vvDebugMsg::msg(1, "brickReaderThreadRun(): empty");
#if !_HS_USE_PREFETCH_
	  usleep(1);
	  continue;
#else
	  // prefetching starts only when request queue is empty
	  BrickInfo* prefetch = brickManager->getPrefetchQueueFront();
	  if(prefetch == NULL)
	  {
		usleep(1);
		continue;
	  }
		vvDebugMsg::msg(1, "prefetching: ", prefetch->getLevel(), prefetch->getBrickX(), prefetch->getBrickY(), prefetch->getBrickZ());
		if(brickManager->loadBrickToMemory(prefetch) < 0)
		{
		  vvDebugMsg::msg(1, "loadBrickToMemory() failed");
		  exit(0);
		}
	  }
		*/
#endif  // end of !_HS_USE_PREFETCH_
	}

	//vvDebugMsg::msg(1, "BrickInfo in brickReaderThreadRun: ", front->getBrickX(), front->getBrickY());

	if(!front->isInMemory())
	{
	  if(brickManager->loadBrickToMemory(front) < 0)
	  {
		vvDebugMsg::msg(1, "loadBrickToMemory() failed");
		exit(0);
	  }
	}

	// FIXME
	//glutSetWindow(brickManager->getWindowID());

#if _HS_ASYNC_TEX_COPY_
	if(!front->isInTexture())
	{
	  if(brickManager->loadBrickToTexture(front) < 0)
	  {
		vvDebugMsg::msg(1, "loadBrickToTexture() failed");
		exit(0);
	  }
	}
#endif
  }
#endif
  return NULL;
}

/*

*/
BrickInfo* BrickManager::getLoadRequestQueueFront()
{
  if(loadRequestQueue.empty())
	return NULL;

  BrickInfo* ret = loadRequestQueue.front();
  assert(ret != NULL);
  loadRequestQueue.pop_front();

  return ret;
}

/*

*/
BrickInfo* BrickManager::getPrefetchQueueFront()
{
  do
  {
	if(prefetchRequestQueue.empty())
	  return NULL;

	BrickInfo* ret = prefetchRequestQueue.front();
	assert(ret != NULL);
	prefetchRequestQueue.pop_front();
	if(!ret->isInMemory())
	  return ret;
  } while(true);
}


