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

class nlpoint extends Object {
	nlpoint parent;

	nlpoint identPoint;

	int nnbr;

	nlpoint[] pnbr;

	double x, y, z, r;

	double dgx, dgy;

	int myIndex = -1;

	int writeIndex = 0;

	int nlcode;

	int imark = -1;

	boolean shiftMark = false;

	String name;

	int iseg;

	nlpoint() {
		nnbr = 0;
		pnbr = new nlpoint[4];
		myIndex = -1;
		nlcode = -1;
	}

	nlpoint(int ind, int code) {
		nnbr = 0;
		pnbr = new nlpoint[4];
		myIndex = ind;
		nlcode = code;
	}

	nlpoint(int ind, int code, nlpoint p, double tx, double ty, double tz,
			double tr) {
		nnbr = 0;
		pnbr = new nlpoint[4];
		myIndex = ind;
		nlcode = code;

		setPosition(tx, ty, tz, tr);
		if (p != null)
			addNeighbor(p);
	}

	nlpoint(int ind, int code, double tx, double ty, double tz, double tr) {
		nnbr = 0;
		pnbr = new nlpoint[4];
		myIndex = ind;
		nlcode = code;

		setPosition(tx, ty, tz, tr);
	}

	public void identifyWith(nlpoint p) {
		identPoint = p;
		r = 0.0;
	}

	public void print() {
		System.out.println(" nlpoint " + this + " myIndex[" + myIndex + "] x[" + x + "] y["	+ y + "] z[" + z + "]");
		System.out.println("parent [" + parent + " "	+ (parent != null ? parent.myIndex : -999) + "]");
		for (int i = 0; i < nnbr; i++) {
			System.out.println("nbr " + i + " " + pnbr[i].myIndex + " "
					+ pnbr[i].x + " " + pnbr[i].y + " " + pnbr[i].z);
		}
	}

	public void shift(double[] cdp) {
		x += cdp[0];
		y += cdp[1];
		z += cdp[2];
		shiftMark = false;
	}

	public void setPosition(double tx, double ty, double tz, double tr) {
		x = tx;
		y = ty;
		z = tz;
		r = tr;
	}

	public void setPosition(double[] ad) {
		x = ad[0];
		y = ad[1];
		z = ad[2];
		r = ad[3];
	}

	public void setRadius(double tr) {
		r = tr;
	}

	public void addNeighbor(nlpoint p) {
		if (p != this) {
			if (nnbr >= pnbr.length) {
				nlpoint[] newpnbr = new nlpoint[nnbr + 2];
				for (int i = 0; i < pnbr.length; i++)
					newpnbr[i] = pnbr[i];
				pnbr = newpnbr;
			}
			pnbr[nnbr] = p;
			// System.out.println ("added " + p.myIndex + " to " + myIndex);
			nnbr++;
		}
	}

	public boolean removeNeighbor(nlpoint p) {
		int i, j;
		boolean got = false;
		if (p != this) {

			for (i = 0; !got && i < nnbr; i++) {
				if (pnbr[i] == p) {
					got = true;
					nlpoint[] newpnbr = new nlpoint[pnbr.length];
					for (j = 0; j < i; j++)
						newpnbr[j] = pnbr[j];
					for (j = i; j < nnbr - 1; j++)
						newpnbr[j] = pnbr[j + 1];
					pnbr = newpnbr;
					nnbr--;
				}
			}
		}
		return got;
	}

	public double pointSeparation(nlpoint p) {
		return Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y)
				+ (z - p.z) * (z - p.z));
	}

}
