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

#include "vvtoolshed.h"
#include "vvcolor.h"

vvColor::vvColor()
{
  for (int i=0; i<3; ++i) _col[i] = 1.0f;         // default color is white to be visible on typically black screens
}

/** @param r,g,b RGB color components [0..1]
*/
vvColor::vvColor(float r, float g, float b)
{
  _col[0] = r;
  _col[1] = g;
  _col[2] = b;
}

/// Overload RHS subscription operator.
float vvColor::operator[](const int index) const
{
  return _col[index];
}

/// Overload LHS subscription operator.
float& vvColor::operator[](const int index)
{
  return _col[index];
}

/** Add two colors by using the maximum intensity of each channel.
 */
vvColor vvColor::operator+ (const vvColor operand) const
{
  vvColor tmp;
  int i;

  for (i=0; i<3; ++i)
  {
    tmp._col[i] = ts_max(this->_col[i], operand._col[i]);
  }
  return tmp;
}

/** Sets the color according to the RGB color model.
  @param r,g,b color components [0..1]. Any component that is negative remains the same.
*/
void vvColor::setRGB(float r, float g, float b)
{
  if (r>=0.0f) _col[0] = r;
  if (g>=0.0f) _col[1] = g;
  if (b>=0.0f) _col[2] = b;
}

/** Sets the color according to the HSB color model.
  @param h,s,b color components [0..1]. Any component that is negative remains the same.
*/
void vvColor::setHSB(float h, float s, float b)
{
  float hOld, sOld, bOld;
  vvToolshed::RGBtoHSB(_col[0], _col[1], _col[2], &hOld, &sOld, &bOld);
  if (h<0.0f) h = hOld;
  if (s<0.0f) s = sOld;
  if (b<0.0f) b = bOld;
  vvToolshed::HSBtoRGB(h, s, b, &_col[0], &_col[1], &_col[2]);
}

void vvColor::getRGB(float& r, float& g, float& b)
{
  r = _col[0];
  g = _col[1];
  b = _col[2];
}

void vvColor::getHSB(float& h, float& s, float& b)
{
  vvToolshed::RGBtoHSB(_col[0], _col[1], _col[2], &h, &s, &b);
}