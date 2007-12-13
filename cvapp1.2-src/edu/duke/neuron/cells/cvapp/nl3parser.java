package edu.duke.neuron.cells.cvapp;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.logging.Logger;

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

class nl3parser extends Object {

	final int iq;	 	// " quote
	final int icom; 	// , comma
	final int isc;		// ; semicolon
	final int isbo;	// [ square bracket open
	final int isbc;	// ] square bracket close
	final int icbo;	// { curly bracket open
	final int icbc;	// } curly bracket close
	final int irbo;	// ( round bracket open
	final int irbc;	// ) round bracket close
	final int ieq;		// = equal
	final int ius;		// _ underscore
	final int iamp;	// & ampersand
	final int ipip;	// | pipebar
	final int ieol;	// end of line
	final int ieof;	// end of file
	final int iws;		// white space

	int nob;			//ca maybe number of objects

	nl3Object[] obs;		//ca does this stand for object as string?

	neulucData neuroData;	//ca this is cvapp proprietary, this is naive about neuroleucida markup

	nl3parser() {
		String sord = "\",;[]{}()=_&| ";
		iq = sord.charAt(0);
		icom = sord.charAt(1);
		isc = sord.charAt(2);
		isbo = sord.charAt(3);
		isbc = sord.charAt(4);
		icbo = sord.charAt(5);
		icbc = sord.charAt(6);
		irbo = sord.charAt(7);
		irbc = sord.charAt(8);
		ieq = sord.charAt(9);
		ius = sord.charAt(10);
		iamp = sord.charAt(11);
		ipip = sord.charAt(12);
		iws = sord.charAt(13);
		ieof = -101;
		ieol = -102;

		/*
		 * System.out.println ("tokens: " + iq + " " + icom + " " + isc + " " +
		 * isbo + " " + isbc + " " + icbo + " " + icbc + " " + irbo + " " + irbc + " " +
		 * ieq + " " + ius + " " + iamp + " " + ipip);
		 */
	}

	int ntok(StreamTokenizer st) {
		int itok = ieof;
		try {
			itok = st.nextToken();
		} catch (IOException e) {
			System.out.println(e);
		}
		if (st.ttype == st.TT_EOL)
			itok = ieol;
		if (st.ttype == st.TT_EOF)
			itok = ieof;
		return itok;
	}

	int nexttok(StreamTokenizer st) {
		int itok = ntok(st);
		if (itok == isc) {
			while (itok != ieol) {
				itok = ntok(st);
				if (itok == ieof)
					return itok;
			}
			itok = nexttok(st);
		}
		if (itok == ieol)
			itok = nexttok(st);
		/*
		 * System.out.println ("token " + itok + " " + st.ttype + " " +
		 * st.TT_NUMBER + " " + st.TT_WORD + " " + st.sval + " " + st.nval);
		 *
		 */
		return itok;
	}

	public void addObject(nl3Object ob)
	{
		if (obs == null) obs = new nl3Object[4];
		if (nob >= obs.length)
		{
			nl3Object[] tobs = new nl3Object[2 * nob];
			for (int i = 0; i < nob; i++)
				tobs[i] = obs[i];
			obs = tobs;
		}
		obs[nob] = ob;
		nob++;
		//CA object recogn


		System.out.println("*** Adding a " + ob.itype + " with txt:" + ob.txt + " qtxt:" + ob.qtxt);
		// System.out.println("top level added object " + this + " " + ob + " " + ob.itype);
	}

	public void loadFile(String[] sa, neulucData tnld) {
		neuroData = tnld;
		String ts;
		neuroData.headerText = " ";
		neuroData.points.removeAllElements();

		int nsl = sa.length;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nsl; i++) {
			sb.append(sa[i]);
			sb.append("\n");
		}
		StreamTokenizer st = new StreamTokenizer(
				new StringReader(sb.toString()));

		st.eolIsSignificant(true);
		st.slashStarComments(true);
		st.slashSlashComments(true);
		st.lowerCaseMode(true);
		st.parseNumbers();

		st.quoteChar(iq);
		st.wordChars(ius, ius);
		st.whitespaceChars(iws, iws);

		boolean eof = false;
		int itok = ieof;
		while (!eof)
		{
			itok = nexttok(st);
			if (itok == ieof)
			{
				eof = true;
			}
			else if (itok == irbo)
			{
				//ca the file is loaded per nl3 object, then added
				nl3Object ob = readObject(st);
				addObject(ob);		//incriments nob
			}
			else
			{
				//System.out.println("unexpected token at top level " + itok + "  at line " + st.lineno());
			}
		}

		if (nob == 0)
			return;


		//==============================================================
		//==============================================================
		//ca now there is a list of objects, now get into those objects
		//==============================================================
		//==============================================================

		nlpoint ppar = null;
		nlpoint pnew = null;


		int ipt = -1;
		for (int i = 0; i < nob; i++)	//ca nob is mostly the same as obs.length except that nob is kept because the entire array may not be used
		{
			nl3Object tob = obs[i];		//does obs stand for object string?


			if (tob.itype == tob.PROPERTY)
			{
				//System.out.println("ignoring top level property " + tob.txt);
			}

			//contours deserve add of type - ???
			else if (tob.itype == tob.CONTOUR)
			{
				ipt = neuroData.getCode("unknown");

				////the first index of the item will be the number of objects that currently exist
				//that is to say that if nothing exists, it'll be index 0, if 200 objs exist then it starts at 201
				int i1=nob;

				for (int j = 0; j < tob.nob; j++)
				{
					if (tob.obs[j].itype == tob.POINT)
					{
						pnew = nlPointOfnlOb(tob.obs[j]);
						neuroData.addPoint(pnew, ppar, ipt);
						ppar = pnew;
					}
				}

				//contours are good candidates for instances
				listPotentialInstance("Contour " + tob.qtxt, i1, pnew.myIndex);
			}


			//Axons and dendrite deserve tree-adds
			//anon or "unknown" is assumed to be a tree
			else if (tob.itype == tob.AXON || tob.itype == tob.DENDRITE || tob.itype == tob.APICAL || tob.itype == tob.ANON)
			{
				addTree(tob);
			}
			else
			{
				System.out.println("section of type " + tob.itype + " ignored");
			}
		}

		neuroData.reindexPoints();
		neuroData.trustLineList = false;
		neuroData.trustPointList = false;
	}

	public void addTree(nl3Object nl3o)
	{
		int ityp = nl3o.itype;
		int ipt = -1;
		if (ityp == nl3o.AXON) ipt = neuroData.getCode("axon");
		if (ityp == nl3o.APICAL) ipt = neuroData.getCode("apical-dendrite");
		if (ityp == nl3o.DENDRITE) ipt = neuroData.getCode("dendrite");
		if (ityp == nl3o.ANON) ipt = neuroData.getCode("unknown");

		if (ipt == -1)
		{
			//System.out.println("error - unknown tree type " + ityp);
			return;
		}

		nlpoint ppar = null;

		recAddTree(nl3o, ppar, ipt);		//add a new tree with a null parent point

	}

	//recursive add tree
	void recAddTree(nl3Object nl3o, nlpoint ppar, int ipt)
	{
		//ca this is RECURSIVE add tree
		//ca ppar probably starts as null point
		//		CA

		nlpoint pprev = ppar;	//parent point
		nlpoint pnew;


		boolean listObj=false;		//if true then this neuroleucida item will show on the list of potential instances

		//check if the parent is a tree item
		if (nl3o.itype != nl3o.AXON && nl3o.itype != nl3o.DENDRITE && nl3o.itype != nl3o.APICAL && nl3o.itype != nl3o.ANON)
		{
			System.out.println("rec add tree - wrong type object " + nl3o.itype);
			return;
		}

		for (int i = 0; i < nl3o.nob; i++)
		{
			nl3Object tob = nl3o.obs[i];

			listObj = false; //not all objects should ahve ready instances

			if (tob.itype == tob.POINT)
			{
				pnew = nlPointOfnlOb(tob);
				neuroData.addPoint(pnew, pprev, ipt);
				pprev = pnew;
			}
			else if (tob.itype == tob.BRANCHSEP)	//if its a branch seperator, act as though encountered a point
			{
				pprev = ppar;
			}
			else if (tob.itype == tob.ANON)
			{
				recAddTree(tob, pprev, ipt);		//add to tree using the parent
			}
			else if (tob.itype == tob.PROPERTY)
			{
				String s = tob.txt;
				//if the object is not an axon, dendrite or apical then its a property and is ignored
				if (!s.startsWith("axon") && !s.startsWith("dendrite") && !s.startsWith("apical"))
				{
					//System.out.println("recAddTree ignoring property: "	+ tob.txt);
				}
			}
			else
			{
				System.out.println("recAddTree ignoring object of type " + tob.itype + " " + tob.txt);
			}


			// To list all dendrite use:
			//listObj = (tob.itype == tob.ANON);

			//If the type is a branch, and it has no parent, then it is a root level branch
			if ( tob.itype == tob.ANON && ppar == null) listObj = true; //display root level branches

			if ( tob.itype == tob.PROPERTY)
			{
				listObj = true;
			}

			if ( listObj )		//if top level object
			{
				int begin=0;
				int end=0;

				//If the parent is null then dont reference it, if the previous is null then dont reference it
				if (ppar != null) begin = ppar.myIndex;
				if (pprev != null) end = pprev.myIndex;

				//As long as the object doesn't begin with 'color'
				//if (tob.txt != null && !tob.txt.startsWith("color"))
				{
					if ( begin != end)
					{
						listPotentialInstance("Tree " + tob.qtxt, begin, end);	
					}						
				}
			}
		}

		//System.out.println("*** Adding a tree ipt[" + ipt + "] " + nl3o.txt + " a[" + nl3o.obs.length  + "] b[" + nl3o.nob + "]");

	}

	public void listPotentialInstance(String name, int p1, int p2)
	{
		
		omtInstance inst = new omtInstance(name, p1, p2);
		neuroData.inObjectNames.add(inst);
		
		System.out.println("*** Potential instance: " + name + " @ " + p1 + " - " + p2);
	}

	public nlpoint nlPointOfnlOb(nl3Object nl3o) {
		if (nl3o.itype != nl3o.POINT) {
			System.out.println("error - called nlPointOfnlObject on "
					+ " non-point " + nl3o.itype);
			return null;

		}

		nlpoint p = new nlpoint();
		p.x = nl3o.x;
		p.y = nl3o.y;
		p.z = nl3o.z;
		p.r = nl3o.r;
		return p;
	}

	public nl3Object readObject(StreamTokenizer st)
	{
		nl3Object nlob = new nl3Object();
		int itok;
		int ibl = 1;
		while (ibl > 0)
		{
			itok = nexttok(st);
			if (itok == icom) itok = nexttok(st);
			if (itok == ieof)
			{
				System.out.println("error - eof while reading object");
				return null;
			}
			else if (itok == irbc)
			{
				ibl--;
			}
			else if (itok == iq)
			{
				// quoted string;
				nlob.addQtext(st.sval);
			}
			else if (st.ttype == st.TT_WORD)
			{
				nlob.addText(st.sval);
			}
			else if (st.ttype == st.TT_NUMBER)
			{
				// System.out.println ("got double....******** " + st.nval);
				nlob.addDouble(st.nval);
			}
			else if (itok == irbo)
			{
				nlob.addObject(readObject(st));
			}
			else if (itok == ipip)
			{
				nl3Object tob = new nl3Object();
				tob.itype = tob.BRANCHSEP;
				nlob.addObject(tob);
			}
			else
			{
				//ca System.out.println("read object unknown, itoken " + itok + ", st type:"	+ st.ttype + ", stval: " + st.sval + " stnval:" + st.nval);
			}
		}
		nlob.tidy(st.lineno());
		return nlob;
	}

}

class nl3Object {
	static int AXON = 1;

	static int DENDRITE = 2;

	static int APICAL = 3;

	static int PROPERTY = 4;

	static int POINT = 5;

	static int CONTOUR = 6;

	static int LABEL = 7;

	static int ANON = 8;

	static int BRANCHSEP = 9;

	int itype;	//default is -1 (unknown), 1=axon, 2=dendrite, 3=apical

	double x, y, z, r;

	int nno;					//ca some kind of dimensional description or measure

	String qtxt;

	String txt;

	String definedShapeName;		//ca This variable preliminarily used to store the 'type' of object it is

	nl3Object[] obs;				//the nested lines that mkae up the object

	int nob = 0;				//ca number of obs, or obs.length

	nl3Object() {
		itype = -1;
		nno = 0;
		x = y = z = r = 0.0;
	}

	public void addText(String s)
	{
		if (txt == null)
		{
			txt = s;
		}
		else
		{
			txt += " ";
			txt += s;
		}
	}

	public void addQtext(String s) {
		if (qtxt == null) {
			qtxt = s;
		} else {
			System.out.println("multiple quoted stings in nl3 object?? " + qtxt
					+ " " + s);
			qtxt += " ";
			qtxt += s;
		}
		if (nob == 0)
			itype = CONTOUR;
		if (nob == 1)
			itype = LABEL;
	}

	public void addDouble(double d) {
		if (nno == 0) {
			x = d;
		} else if (nno == 1) {
			y = d;
		} else if (nno == 2) {
			z = d;
		} else if (nno == 3) {
			r = 0.5 * d; // NL3 stores the radius, not the diamter;
		}
		nno++;
		itype = POINT;
	}

	public void addObject(nl3Object nl3o) {
		if (obs == null)
			obs = new nl3Object[4];
		if (nob >= obs.length) {
			nl3Object[] tobs = new nl3Object[2 * nob];
			for (int i = 0; i < nob; i++)
				tobs[i] = obs[i];
			obs = tobs;
		}
		obs[nob] = nl3o;
		nob++;
		/*
		 * System.out.println ("added object " + this + " " + nl3o + " " +
		 * nl3o.itype + " " + nl3o.x + " " + nl3o.y + " " + nl3o.txt);
		 */
	}

	public void tidy(int lno)
	{
		boolean hasprops = false;
		if (itype < 0)
		{
			if (obs == null)
			{
				itype = PROPERTY;
			}
			else
			{
				for (int i = 0; i < obs.length && itype < 0; i++)
				{
					nl3Object tob = obs[i];
					if (tob != null && tob.itype == PROPERTY)
					{
						hasprops = true;
						String s = tob.txt;

						if (s == null)
						{
							System.out
									.println("error: property with null text? "
											+ tob.x + " " + tob.qtxt + " "
											+ tob.nob + "   at " + lno);
						}
						else if (s.startsWith("axon"))
						{
							itype = AXON;
						}
						else if (s.startsWith("apical"))
						{
							itype = APICAL;

						}
						else if (s.startsWith("dendrite"))
						{
							itype = DENDRITE;
						}

						//regardless of the itype, lets find what it's called
						tob.definedShapeName = s;
					}
				}
			}
		}

		if (itype < 0)
		{
			if (hasprops)
			{
				//System.out.println("warning - unknown nl3 object " + txt + " " 	+ qtxt + " " + nob);
			}
			itype = ANON;
		}
	}

}
