/*******************************************************************************************************
 *
 *	Authors	:	Rajvikram Singh
 *	Email	:	rsingh@evl.uic.edu
 *
 *	Status	:	Experimental
 *	Description:	Gives the interface for interacting with the plugin.
 *
 *	Note	:	The plugins all contain the implementation of only one class. Eaach plugin in addition
 *			will contain a free floating C style function, which when called will create 
 *			and return an object of the class.
 *
*******************************************************************************************************/ 

#ifndef _TVPLUGIN_H
#define _TVPLUGIN_H

#ifdef WIN32
	#define TV_EXPORT_DIRECTIVES __declspec(dllexport)
#endif	
		
				
#ifdef linux
	#define TV_EXPORT_DIRECTIVES
#endif


extern "C"
{
TV_EXPORT_DIRECTIVES void getObjHandle(void *&);	
				// This is a free floating C function which will be present within each DL
				// and when called, it will create and return an
				// object of the plugin int the parameter passed to
				// it
TV_EXPORT_DIRECTIVES void delObj(void *&);
				// Delete the object whose handle is passed		
}

#endif
