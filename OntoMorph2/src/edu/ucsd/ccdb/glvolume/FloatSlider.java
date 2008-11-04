//****************************************************************************
// Filename:          	FloatSlider.java
// Functionality:      	Floating point slider
// Author:            	Juergen Schulze-Doebold (schulze@hlrs.de)
// Institution:       	University of Stuttgart, Supercomputing Center (HLRS)
// Operating System:  	SGI IRIX 6.5
// Requirements:      	Java 1.1.6, Swing 1.1.1
// History:           	01-08-23  Creation date
//****************************************************************************

package edu.ucsd.ccdb.glvolume;

import javax.swing.*;

/** This class provides a slider to modify floating point values.
  Use getFloatValue(), getFloatMinimum() and getFloatMaximum() instead
  of getValue(), getMinimum() and getMaximum().
*/
public class FloatSlider extends JSlider
{
  private final float FACTOR = 1000.0f;   ///< scaling from real to slider values

  public FloatSlider()
  {
    super(0, (int)(100.0f * 1000.0f), (int)(50.0f * 1000.0f));
  }

  public FloatSlider(int orientation)
  {
    super(orientation, 0, (int)(100.0f * 1000.0f), (int)(50.0f * 1000.0f));
  }

  public FloatSlider(float min, float max)
  {
    super((int)(min * 1000.0f), (int)(max * 1000.0f), (int)(50.0f * 1000.0f));
  }

  public FloatSlider(int orientation, float min, float max, float current)
  {
    super(orientation, (int)(min * 1000.0f), (int)(max * 1000.0f), (int)(current * 1000.0f));
  }

  public FloatSlider(float min, float max, float current)
  {
    super((int)(min * 1000.0f), (int)(max * 1000.0f), (int)(current * 1000.0f));
  }

  public void setFloatValue(float val)
  {
    super.setValue((int)(val * FACTOR));
  }

  public void setValue(float val)
  {
    setFloatValue(val);
  }

  public float getFloatValue()
  {
    return super.getValue() / FACTOR;
  }

  public void setFloatMinimum(float val)
  {
    super.setMinimum((int)(val * FACTOR));
  }

  public void setMinimum(float val)
  {
    setFloatMinimum(val);
  }

  public void setFloatMaximum(float val)
  {
    super.setMaximum((int)(val * FACTOR));
  }

  public void setMaximum(float val)
  {
    setFloatMaximum(val);
  }

  public float getFloatMinimum()
  {
    return super.getMinimum() / FACTOR;
  }

  public float getFloatMaximum()
  {
    return super.getMaximum() / FACTOR;
  }
}
