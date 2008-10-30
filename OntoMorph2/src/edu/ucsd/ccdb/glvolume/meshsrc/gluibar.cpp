///////////////////////////////////////
// Original code is from Chih Liang's code
//
// Author: Han Suk Kim (hskim@cs.ucsd.edu)
// ID: cs169wad
// Class: cse 169 Winter 2006
////////////////////////////////////////

#include "gluibar.h"
#include "tester.h"
#include "vvvirtexrendmngr.h"
//#include "meshrend.h"

#ifdef WIN32 // -Raj
#define snprintf		_snprintf
#endif

using namespace MipMapVideoLib;

/********** User IDs for callbacks ********/
enum GUI_CALLBACK_USER_ID {
	UNHIDE_BAR_ID = 0,
	HIDE_BAR_ID,
	//VOX_DIST_ID,
	SHOW_BOUNDARY_ID,
	SHOW_TEXTURE_ID,
	NEXT_FRAME_ID,
	PREV_FRAME_ID,
	START_SLIDESHOW_ID,
	STOP_SLIDESHOW_ID,
	FASTER_SLIDESHOW_ID,
	SLOWER_SLIDESHOW_ID,
	VOLUME_RADIO_ID,
	CHANNEL_RADIO_ID,
	CHANNEL_COLOR_CHANGE_ID,
	ACTIVATE_CHANNEL_ID,
	CAMERA_X_ID,
	CAMERA_Y_ID,
	CAMERA_Z_ID,
	PIXEL_TO_VOXEL_RATIO_ID,
	BRICK_LIMIT_ID
};

Glui GluiBar;

/**************************************** control_cb() *******************/
/* GLUI control callback   Hacks!!                                       */

static void CB(int control)
{
	GluiBar.CallBack(control);
}

/************************************************************************/

Glui::Glui(): axisOn(0), mouserate(0.1f), showBoundary(1), showTexture(1), selectedVolume(0), selectedChannel(0), pixelToVoxelRatio(16), brickLimit(40)
{
  for(int i = 0; i < 3; i++)
	BGcolor[i] = 0.0f;
}

void Glui::CallBack( int control )
{
  switch (control)
  {
	//case VOX_DIST_ID:
	  //g_rendererManager->SetVoxDist(voxDist);
	  //break;
	case SHOW_BOUNDARY_ID:
	  g_rendererManager->setShowBoundary(selectedVolume, showBoundary);
	  break;
	case SHOW_TEXTURE_ID:
	  g_rendererManager->setShowTexture(selectedVolume, showTexture);
	  break;
	case PIXEL_TO_VOXEL_RATIO_ID:
	  g_rendererManager->setPixelToVoxelRatio(selectedVolume, pixelToVoxelRatio);
	  break;
	case BRICK_LIMIT_ID:
	  g_rendererManager->setBrickLimit(selectedVolume, brickLimit);
	  break;


	// Frame control ==================================================
	case NEXT_FRAME_ID:
	  g_rendererManager->nextFrame();
	  break;
	case PREV_FRAME_ID:
	  g_rendererManager->prevFrame();
	  break;
	case START_SLIDESHOW_ID:
	  g_rendererManager->startPlay();
	  break;
	case STOP_SLIDESHOW_ID:
	  g_rendererManager->pausePlay();
	  break;
	case FASTER_SLIDESHOW_ID:
	  g_rendererManager->decreasePlaySpeed();
	  break;
	case SLOWER_SLIDESHOW_ID:
	  g_rendererManager->increasePlaySpeed();
	  break;

	// Volume select =================================================
	case VOLUME_RADIO_ID:
	  g_rendererManager->setCurrentVolume(selectedVolume);
	  channel[0]->hide_internal(1);
	  activeChannel[0]->hide_internal(1);

	  channel[selectedVolume]->hidden = false;
	  channelRadios[selectedVolume]->unhide_internal(1);

	  hueStaticText->hidden = false;

	  activeChannel[selectedVolume]->hidden = false;
	  activeChannelCheckbox[selectedVolume][0]->unhide_internal(1);

	  channelHueScrollBar->hidden = false;

	  selectedChannel = 0;

	  for(int i = 0; i < g_rendererManager->getNumChannel(selectedVolume); i++)
	  {
		activeChannelCheckbox[selectedVolume][i]->set_int_val(g_rendererManager->isActiveChannel(selectedVolume, i));
	  }
	  channelHueScrollBar->set_float_val(g_rendererManager->getChannelHue(selectedVolume, 0));
	  break;

	// Channel =======================================================
	case CHANNEL_RADIO_ID:
	  channelHueScrollBar->set_float_val(g_rendererManager->getChannelHue(selectedVolume, selectedChannel));
	  break;
	case CHANNEL_COLOR_CHANGE_ID:
	  g_rendererManager->setChannelHue(selectedVolume, selectedChannel, channelHue);
	  break;
	case ACTIVATE_CHANNEL_ID:
	  for(int i = 0; i < g_rendererManager->getNumChannel(selectedVolume); i++)
	    g_rendererManager->setActiveChannel(selectedVolume, i, activeChannelCheckbox[selectedVolume][i]->get_int_val());
	  break;

	// Camera ========================================================
	case CAMERA_X_ID:
	  TESTER->keyboard('x', 0, 0);
	  break;
	case CAMERA_Y_ID:
	  TESTER->keyboard('y', 0, 0);
	  break;
	case CAMERA_Z_ID:
	  TESTER->keyboard('z', 0, 0);
	  break;

	// Un/Hide ========================================================
	case UNHIDE_BAR_ID:
	  glui->show();
	  showbar->hide();
	  break;
	case HIDE_BAR_ID:
	  glui->hide();
	  showbar->show();
	  break;

	default:
		cerr << "Glui::CallBack- Invalid ID:" << control << endl;
		break;
  }
  glutPostRedisplay();

}

void Glui::SetUpGlui(int WindowHandle)
{
	glui = GLUI_Master.create_glui_subwindow( WindowHandle, GLUI_SUBWINDOW_BOTTOM);
	glui->set_main_gfx_window( WindowHandle );

	// to unhide the left menu bar
	showbar = GLUI_Master.create_glui_subwindow(WindowHandle, GLUI_SUBWINDOW);
	showbar->add_button("UNHIDE", UNHIDE_BAR_ID, CB);
	showbar->hide();

	GLUI_Panel *panel = glui->add_panel( "MENU");

	//====================================================================
	// 1. Options 
	GLUI_Rollout *options = glui->add_rollout_to_panel(panel, "Options");
	glui->add_checkbox_to_panel(options, "Axis On", &axisOn);
	glui->add_column_to_panel(options, false);
	glui->add_checkbox_to_panel(options, "Show Bounding Boxes", &showBoundary, SHOW_BOUNDARY_ID, CB);
	glui->add_column_to_panel(options, false);
	glui->add_checkbox_to_panel(options, "Show Texture", &showTexture, SHOW_TEXTURE_ID, CB );
	glui->add_spinner_to_panel(options, "Pixel to Voxel Ratio: ", GLUI_SPINNER_INT, &pixelToVoxelRatio, PIXEL_TO_VOXEL_RATIO_ID, CB)->set_int_limits(1, 40, GLUI_LIMIT_WRAP);
	glui->add_spinner_to_panel(options, "Brick Limit: ", GLUI_SPINNER_INT, &brickLimit, BRICK_LIMIT_ID, CB)->set_int_limits(1, 200, GLUI_LIMIT_WRAP);


	//====================================================================
	// 2. Video (Frame)
	glui->add_column_to_panel(panel, false);
	GLUI_Panel *frame = glui->add_panel_to_panel(panel, "Frame");
	glui->add_button_to_panel(frame, "+", NEXT_FRAME_ID, CB);
	glui->add_button_to_panel(frame, "-", PREV_FRAME_ID, CB);
	glui->add_column_to_panel(frame, false);
	glui->add_button_to_panel(frame, "Play", START_SLIDESHOW_ID, CB);
	glui->add_button_to_panel(frame, "Stop", STOP_SLIDESHOW_ID, CB);
	glui->add_column_to_panel(frame, false);
	glui->add_button_to_panel(frame, "Faster!", FASTER_SLIDESHOW_ID, CB);
	glui->add_button_to_panel(frame, "Slower~", SLOWER_SLIDESHOW_ID, CB);
	glui->add_column_to_panel(frame, false);


	//====================================================================
	// 3. Volume Select
	glui->add_column_to_panel(panel, false);
	GLUI_Panel* volume = glui->add_panel_to_panel(panel, "Volume");
	GLUI_RadioGroup* volumeRadio = glui->add_radiogroup_to_panel(volume, &selectedVolume, VOLUME_RADIO_ID, CB);
	for(int v = 0; v < g_rendererManager->getNumVolume(); v++)
	{
	  char volumeName[50];
	  snprintf(volumeName, sizeof(volumeName), "Volume #%d", v+1);
	  glui->add_radiobutton_to_group(volumeRadio, volumeName);
	}
	channelRadios = new GLUI_RadioGroup*[g_rendererManager->getNumVolume()];


	glui->add_column_to_panel(volume, false);

	//====================================================================
	// 4. Channel control for selected volume
	//GLUI_Panel* channel = glui->add_panel_to_panel(volume, "Adjust Channel Hue");
	channel = new GLUI_Panel*[g_rendererManager->getNumVolume()];

	for(int v = 0; v < g_rendererManager->getNumVolume(); v++)
	{
	  char channelControlName[100];
	  snprintf(channelControlName, sizeof(channelControlName), "Volume #%d: Adjust Channel Hue", v+1);

	  channel[v] = glui->add_panel_to_panel(volume, channelControlName);
	  channelRadios[v] = glui->add_radiogroup_to_panel(channel[v], &selectedChannel, CHANNEL_RADIO_ID, CB);

	  for(int i = 0; i < g_rendererManager->getNumChannel(v); i++)
	  {
		char channelName[50];
		snprintf(channelName, sizeof(channelName), "Channel #%d-%d", v+1, i+1);
		glui->add_radiobutton_to_group(channelRadios[v], channelName);
	  }
	  channel[v]->hide_internal(1);
	}
	  

	// channel color scroll bar
	hueStaticText = glui->add_statictext_to_panel(volume, "Hue");
	hueStaticText->set_alignment(GLUI_ALIGN_LEFT);

	channelHueScrollBar = new GLUI_Scrollbar(volume, "hue", GLUI_SCROLL_HORIZONTAL, &channelHue, CHANNEL_COLOR_CHANGE_ID, CB);
	channelHueScrollBar->set_float_limits(0.0, 1.0, GLUI_LIMIT_CLAMP); 


	cerr << "selectedVolume: " << selectedVolume << endl;
	cerr << "getNumChannel: " << g_rendererManager->getNumChannel(selectedVolume) << endl;

	//====================================================================
	// 4. De/activate channels
	glui->add_column_to_panel(volume, false);

	activeChannel = new GLUI_Panel*[g_rendererManager->getNumVolume()];
	activeChannelCheckbox = new GLUI_Checkbox**[g_rendererManager->getNumVolume()];

	for(int v = 0; v < g_rendererManager->getNumVolume(); v++)
	{
	  char checkBoxName[100];
	  snprintf(checkBoxName, sizeof(checkBoxName), "De/Activate Channels for Volume #%d", v+1);
	  activeChannel[v] = glui->add_panel_to_panel(volume, checkBoxName);
	  activeChannelCheckbox[v] = new GLUI_Checkbox*[g_rendererManager->getNumChannel(v)];

	  for(int i = 0; i < g_rendererManager->getNumChannel(selectedVolume); i++)
	  {
		char channelName[50];
		snprintf(channelName, sizeof(channelName), "Volume#%d: Channel#%d", selectedVolume+1, i+1);
		activeChannelCheckbox[v][i] = glui->add_checkbox_to_panel(activeChannel[v], channelName, NULL, ACTIVATE_CHANNEL_ID, CB);
		activeChannelCheckbox[v][i]->set_int_val(1);
		glui->add_column(false);
	  }
	  activeChannel[v]->hide_internal(1);
	}

	g_rendererManager->setCurrentVolume(0);
	channel[0]->hidden = false;
	channelRadios[0]->unhide_internal(1);
	channelHueScrollBar->hidden = false;
	hueStaticText->hidden = false;
	activeChannel[0]->hidden = false;
	activeChannelCheckbox[0][0]->unhide_internal(1);




	//====================================================================
	// 5. Camera position
	glui->add_column_to_panel(panel, false);
	GLUI_Panel *camera = glui->add_panel_to_panel(panel, "Camera");
	glui->add_button_to_panel(camera, "X", CAMERA_X_ID, CB)->set_w(3);
	glui->add_column_to_panel(camera, false);
	glui->add_button_to_panel(camera, "Y", CAMERA_Y_ID, CB)->set_w(3);
	glui->add_column_to_panel(camera, false);
	glui->add_button_to_panel(camera, "Z", CAMERA_Z_ID, CB)->set_w(3);


	//====================================================================
	// 6. Hide and Quit
	glui->add_column(false);
	//glui->add_button("SAVE", SAVE_SETTINGS_ID, CB);
	//glui->add_separator();
	glui->add_button("HIDE", HIDE_BAR_ID, CB);
	//glui->add_separator();
	glui->add_button("QUIT", 0, (GLUI_Update_CB)exit);

}

