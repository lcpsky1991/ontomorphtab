// DeskVOX - Volume Exploration Utility for the Desktop
// Copyright (C) 1999-2003 University of Stuttgart, 2004-2005 Brown University
// Contact: Jurgen P. Schulze, schulze@cs.brown.edu
// 
// This file is part of DeskVOX.
//
// DeskVOX is free software; you can redistribute it and/or
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

#ifndef vvtranslinesFX_INCLUDED
#define vvtranslinesFX_INCLUDED

#include <fx.h>

#include <iostream>
#include <string.h>

class VVTransferLine
{
  public:
    VVTransferLine();
    virtual ~VVTransferLine();

    virtual void draw(FXCanvas*)=0;
    virtual bool clicked(int x);

    int xPos, angle;
    double width,max;
    bool selected;
};

class VVTransferHat:public VVTransferLine
{
  public:
    VVTransferHat();
    ~VVTransferHat();

    virtual void draw(FXCanvas*);
};

class VVTransferRamp:public VVTransferLine
{
  public:
    VVTransferRamp();
    ~VVTransferRamp();

    virtual void draw(FXCanvas*);
};

class VVTransferBlank:public VVTransferLine
{
  public:
    VVTransferBlank();
    ~VVTransferBlank();

    virtual void draw(FXCanvas*);
};
#endif
