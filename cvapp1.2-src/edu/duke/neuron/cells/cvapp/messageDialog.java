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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class messageDialog extends Dialog implements ActionListener {
	Label lab1;

	Label lab2;

	Button bcancel;

	messageDialog(Frame fr) {
		super(fr);

		rsbPanel pan = new rsbPanel();
		pan.setLayout(new BorderLayout());

		lab1 = new Label(" ");
		lab2 = new Label(" ");

		Panel plab = new Panel();
		plab.setLayout(new GridLayout(2, 1, 6, 6));
		plab.add(lab1);
		plab.add(lab2);

		pan.add("Center", plab);

		Button bcancel = new Button("cancel");
		pan.add("South", bcancel);

		setLayout(new BorderLayout());
		add("Center", pan);
		setSize(500, 140);

	}

	public void showMessage() {
		validate();
		pack();
		show();
		// setVisible (true);
	}

	public void hideMessage() {
		setVisible(false);
	}

	public void setLabels(String s1, String s2) {
		setLabel1(s1);
		setLabel2(s2);
	}

	public void setLabel1(String s) {
		lab1.setText(s);
		lab1.invalidate();
		validate();
	}

	public void setLabel2(String s) {
		lab2.setText(s);
		lab2.invalidate();
		validate();
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof Button) {
			String sarg = ((Button) source).getLabel();
			if (sarg.equals("cancel")) {
				System.out.println("meesge dialog cancel event");
			}

		}
	}

}
