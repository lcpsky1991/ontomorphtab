package wbctest.atlas;
/*
	 * Copyright (c) 2003-2006 jMonkeyEngine
	 * All rights reserved.
	 *
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions are
	 * met:
	 *
	 * * Redistributions of source code must retain the above copyright
	 *   notice, this list of conditions and the following disclaimer.
	 *
	 * * Redistributions in binary form must reproduce the above copyright
	 *   notice, this list of conditions and the following disclaimer in the
	 *   documentation and/or other materials provided with the distribution.
	 *
	 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
	 *   may be used to endorse or promote products derived from this software 
	 *   without specific prior written permission.
	 *
	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
	 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
	 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
	 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
	 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
	 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
	 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
	 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
	 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
	 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 */

	import com.jme.app.SimpleGame;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;

	/**
	 * <code>TestAnisotropic</code>
	 * @author Joshua Slack
	 * @version $Id: TestTriangleStrip.java,v 1.1 2006/06/01 15:05:47 nca Exp $
	 */
	public class TestReferenceAtlas extends SimpleGame {
	  /**
	   * Entry point for the test,
	   * @param args
	   */
	  public static void main(String[] args) {
	    TestReferenceAtlas app = new TestReferenceAtlas();
	    app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
	    app.start();
	  }

	  /**
	   * @see com.jme.app.SimpleGame#initGame()
	   */
	  protected void simpleInitGame() {
	    display.setTitle("Test Triangle Strips");
	    

		ReferenceAtlas atlas = ReferenceAtlas.getInstance();
		/*
		for (BrainRegion r : atlas.getBrainRegions()) {
			rootNode.attachChild(r.getMesh());
		}
		*/
		//String[] regions = {"OLF", "HPF", "STRd", "STRv", "LSX", "sAMY", "PAL", "TH", "HY", "MBsen", "MBmot", "MBsta", "P", "MY", "CB"};
		String[] regions = {"OLF"};
		
		for (String s : regions) {
			rootNode.attachChild(atlas.getBrainRegion(s).getClodMesh());
		}
	  }
	}
