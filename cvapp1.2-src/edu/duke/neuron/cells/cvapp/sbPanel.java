package edu.duke.neuron.cells.cvapp;

/*
 cvapp - neuronal morphology viewer, editor and file converter
 Copyright (C) 1998  Robert Cannon

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 please send comments, bugs, and feature requests to rcc1@soton.ac.uk
 or see http://www.neuro.soton.ac.uk/cells/

 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Panel;

public class sbPanel extends Panel {

	sbPanel() {
		super();
	}

	public Insets getInsets() {
		return (new Insets(3, 3, 3, 3));
	}

	public void paint(Graphics g) {
		Dimension d = getSize();
		Color c = getBackground();
		WindowDressing.SunkenBorder(g, d, c);
	}
}
