//File jtiffLoader.cpp
//This file is experimental as a wrapper for tiffLoader

/**
 * Author: Christopher Aprea
 * 
 * Date: 	2008-07-03
 * 
 * Purpose: wraps the tiffLoader.cpp with JNI exports so that it can be used in Java programs
 * 			meant to be compiled into a DLL or SO
 * 			if on a linux machine, it must be an .so with a 'lib' prefix
 * 			for example: libjtiffLoader.so
 * 	
 * 			for documentation of functions see tiffLoader.cpp 
 * 	
 * 
 * Environment:	Kubuntu 8
 * 				jdk 1.6
 * 				x86_64
 */


#include <jni.h>
#include "jtiffLoader.h"
#include <iostream>
#include "tiffLoader.cpp"
#include <string.h>

//MIN BUFFER is basicly an arbitrary number of how much the minimum buffersize should be; 1900x1200 screen
#define JTL_MIN_BUFFER	2280000

using namespace std;


//prototypes


//main object
tiffLoader *tLoader = new tiffLoader();
	

void test()
{
	cout << "Im Width is " << tLoader->getImageW() << endl;
	cout << "Im Height is " << tLoader->getImageH() << endl;	
}

JNIEXPORT void JNICALL Java_jtiffLoader_init (JNIEnv *env, jobject obj, jstring file, jlong x, jlong y, jfloat dpi)
{
	cout << "Initializing... ";	//no endl because putting filename next

	//Convert the jstring to a CSTRING
	/**
	 * 
	 * This is the WRONG way (this is for C):
	 * 			const char *nativeString = (*env)->GetStringUTFChars(env, javaString, 0);
	 * This is the RIGHT way for C++:
	 * 	
	 * 
	 * 
	 */
    char * cstr = (char *) env->GetStringUTFChars(file, NULL);
    if (cstr == NULL) 
	{
		cout << "\n: could not convert string in jtiffLoader_init() : (OutOfMemoryError?)" << endl;
		return; /* OutOfMemoryError already thrown */
	}
	//End Convert
	
	cout << " finished " << cstr << endl;
	
	if (tLoader->init(cstr, x, y, dpi) != TL_OK)
	{
		cout << "\n: Error initializing in jtiffLoader_init() : " << endl;
		return;
	}
	
	//Release the memory for the variables we've converted
	(env)->ReleaseStringUTFChars(file, cstr);
}


JNIEXPORT void JNICALL Java_jtiffLoader_hello (JNIEnv *env, jobject)
{
	cout << "Hello, this is jtiffLoader 0.1" << endl;
	test();
}

JNIEXPORT jint JNICALL Java_jtiffLoader_getImageW (JNIEnv *env, jobject)
{
	return tLoader->getImageW();
}

JNIEXPORT jint JNICALL Java_jtiffLoader_getImageH (JNIEnv *env, jobject)
{
	return tLoader->getImageH();
}

JNIEXPORT jint JNICALL Java_jtiffLoader_getTileW (JNIEnv *env, jobject)
{
	return tLoader->getTileW();
}

JNIEXPORT jint JNICALL Java_jtiffLoader_getTileH (JNIEnv *env, jobject)
{
	return tLoader->getTileH();
}

//public native int extractRGBAImage(double bottomLeft_x, double bottomLeft_y, double topRight_x, double topRight_y, int oBuf, long bufferSize, int aprox_W, int aprox_H);
//extractRGBAImage(double _bl_x, double _bl_y, double _ur_x, double _ur_y, void *_oBuf, unsigned _bufSize, unsigned &_W, unsigned &_H);
JNIEXPORT jintArray JNICALL Java_jtiffLoader_getRGBA (JNIEnv *env, jobject obj, jdouble blx, jdouble bly, jdouble urx, jdouble ury, jint w, jint h)

{

	//some objects need to be made ready for C++
	//make objects of the type the function actually wants
	double c_blx = 0;//blx;
	double c_bly = 0;//bly;
	double c_urx = 0;//urx;
	double c_ury = 0;//ury;
	unsigned c_bSize = 8388608 * 4; //approximately the screen size of 1900x1200 (* 8)
	void 	*c_oBuf; //c buffer in which to store the data temporarily	
	unsigned c_w = w; //1920
	unsigned c_h = h; //1200
	int r = 0;		//return value


	
	c_blx = blx;
	c_bly = bly;
	c_urx = urx;
	c_ury = ury;

	//allocate more than enough memory for the specified WxH
	c_bSize = (c_w * c_h * 4 * 4); //this should be sufficient, this is FOUR times the allocation needed for [ RGBA of (W by H) ]
	
	//use a buffer of at LEAST some ammount (for those really small images)
	//was getting insufficient buffer errors for 50x50 or and less
	if ( c_bSize < JTL_MIN_BUFFER )
	{
		c_bSize = JTL_MIN_BUFFER;
	}
	
	
	//check the parameters, are they legal?
	if ( !(c_blx >= 0 && c_blx <= 1 && c_bly >= 0 && c_bly <= 1 && c_urx >= 0 && c_urx < 1 && c_ury >= 0 && c_ury <= 1) )
	{
		return NULL;
	}
	

	c_oBuf = malloc(c_bSize);	//allocate memory for the buffer
	memset(c_oBuf, 0, c_bSize);
	
	r = tLoader->extractRGBAImage( c_blx,  c_bly, c_urx, c_ury, c_oBuf, c_bSize, c_w, c_h);
	
	//do error checking on the return value
	if ( r < 0 ) 
	{
		cout << "Error extracting image: ";	//begin error msg
		switch ( r )
		{
			case TL_FAIL:
				cout << "FAILED";
				break;	
			case TL_FILEOPEN_ERR:
				cout << "File open error";
				break;
			case TL_INVALID_VIEWPORT:
				cout << "Invalid viewport";
				break;
			case TL_TIFF_READ_ERR:
				cout << "Tiff read error";
				break;
			case TL_INSUFF_BUFFER:
				cout << "Insufficient buffer (used " << c_bSize << ")";
				break;
			default:
				cout << "Unknown error type (" << r << ")";
				break;
		}
		cout << endl;	//end the error message
	
		return NULL;
	}

	//create the array
	int *c_rgba;
	unsigned len = c_w * c_h * 4;
	
	//fill the array
	c_rgba = (int *) malloc(len);

	for (int i=0; i < len; i++)
	{
		//set the value of c_rgb to be: val @ i, element x
		*c_rgba = (int) ((char *) c_oBuf)[i];		//char * because char are typically 8bits
	}

	FILE *fptr = fopen("out.raw", "w");
	fwrite(c_oBuf, len, 1, fptr);
	fclose(fptr);

	
 	// create the new Java array
 	jintArray j_rgba = env->NewIntArray(len);
 	
	// copy the native info into the java array
	//we do not need to copy the WHOLE buffer, only W * H * 4 (R G B A)

	env->SetIntArrayRegion(j_rgba, 0, len, c_rgba);	//problematic
	

	cout << "extracted size is " << c_w << ":" << c_h << endl;

	return  j_rgba;
}


/*
OLD CODE
	//print the buffer
	//for (int i=0; i < c_bSize; i++)
	//{
	//( i += 4
	//	cout << (int) ((char *) c_oBuf + i)[0] << ":" << (int)((char *) c_oBuf + i)[1] <<  ":" << (int)((char *) c_oBuf +i)[2] << ":" << (int)((char *) c_oBuf + i)[3] << endl;
	//	cout << (int) ((char *) c_oBuf + (4*i))[0] << ":" << (int)((char *) c_oBuf + (4*i))[1] <<  ":" 
	//		<< (int)((char *) c_oBuf + (4*i))[2] << ":" << (int)((char *) c_oBuf + (4*i))[3] << endl;
	//}
	//FILE *fptr = fopen("test.raw", "w");
	//fwrite(c_oBuf, c_w * c_h * 4, 1, fptr);
	//fclose(fptr);
*/


JNIEXPORT jint JNICALL Java_jtiffLoader_getPixelFormat (JNIEnv *env, jobject)
{
	return 666;
}
JNIEXPORT jint JNICALL Java_jtiffLoader_getCompressionScheme (JNIEnv *env, jobject)
{
	return tLoader->getCompressionScheme();
}

JNIEXPORT jint JNICALL Java_jtiffLoader_getBytesPerPixel (JNIEnv *env, jobject)
{
	return tLoader->getBytesPerPixel();
}

