////////////////////////////////////////
////////////////////////////////////////

#ifndef _GLUIBAR_H
#define _GLUIBAR_H

#include <GL/glui.h>
#include "vvvirtexrendmngr.h"

////////////////////////////////////////////////////////////////////////////////


class Glui
{
  public:
	Glui();
	~Glui() {}

	void setRenderer(MipMapVideoLib::vvVirTexMultiRendMngr* renderer) {g_rendererManager = renderer; }
	void Update() { glui->sync_live();}
	void SetUpGlui(int WindowHandle);
	void CallBack( int control );
	int GetAxisOn() { return axisOn; }
	float *GetBGcolor() { return BGcolor; }
	GLUI *GetBar() { return glui; }
	float GetMouserate() { return mouserate; }

  private:
	int axisOn;
	float BGcolor[3];
	float voxDist[3];
	float mouserate;
	int showBoundary;
	int showTexture;
	int selectedVolume;
	int selectedChannel;
	float channelHue;
	int pixelToVoxelRatio;
	int brickLimit;

	GLUI *glui;
	GLUI *showbar;

	GLUI_Panel** channel;
	GLUI_RadioGroup** channelRadios;

	GLUI_StaticText* hueStaticText;
	GLUI_Scrollbar* channelHueScrollBar;

	GLUI_Panel** activeChannel;
	GLUI_Checkbox*** activeChannelCheckbox;

	MipMapVideoLib::vvVirTexMultiRendMngr* g_rendererManager;

};

extern Glui GluiBar;

#endif

