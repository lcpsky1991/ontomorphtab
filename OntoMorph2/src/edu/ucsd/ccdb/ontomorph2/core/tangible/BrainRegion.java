package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.jme.scene.BatchMesh;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;
import com.sun.org.apache.xerces.internal.util.URI;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.util.AllenAtlasMeshLoader;
import edu.ucsd.ccdb.ontomorph2.util.Log;

/**
 * Defines an anatomical region of the mouse brain.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class BrainRegion extends ContainerTangible {

	/**
	 * Solid and visible
	 */
	public static final int VISIBLE = 0;
	/**
	 * Not visible
	 */
	public static final int INVISIBLE = 1;
	/**
	 * transparent and visible
	 */
	public static final int TRANSPARENT = 2;

	private String abbrev;
	private String parentAbbrev;
	private int regionId;
	private int visibility = INVISIBLE; // by default
	
	private TriMesh data = null;
	
	public BrainRegion(String name, String abbrev, String parentAbbrev, Color c, String regionId, CoordinateSystem co){
		this.abbrev = abbrev;
		this.parentAbbrev = parentAbbrev;
		this.setColor(c);
		this.regionId = Integer.parseInt(regionId);
		this.setCoordinateSystem(co);
		this.setName(name);
	}
	
	public void loadData() {

		long tick = Log.tick();
		data = loadAllenMesh();
		/* when low detail meshes are working again, we can employ this loading strategy
		try {
			data = loadLowDetailMesh();
		} catch (IOException e) {
			data = loadAllenMesh();
		}*/

		Log.tock("Loading BrainRegion for " + getName() + " took ", tick);
	}
	
	public TriMesh getData() {
		if (data == null) {
			loadData();
		}
		return data;
	}
	
	/**
	 * Sets the visibility state of this brain region.
	 * @param mode - BrainRegion.VISIBLE, BrainRegion.INVISIBLE, BrainRegion.TRANSPARENT
	 */
	public void setVisibility(int mode) {
		if (visibility != mode) {
			visibility = mode;
			changed(CHANGED_VISIBLE);
		}
	}
	
	/**
	 * Returns the visibility state of this brain region.
	 * @returns - BrainRegion.VISIBLE, BrainRegion.INVISIBLE, BrainRegion.TRANSPARENT
	 */
	public int getVisibility() {
		return visibility;
	}
	
	public BrainRegion getParent() {
		return ReferenceAtlas.getInstance().getBrainRegion(this.parentAbbrev);
	}

	public String getAbbreviation() {
		return abbrev;
	}
	
	public int getRegionId() {
		return this.regionId;
	}
	
	protected TriMesh loadAllenMesh() {
		TriMesh tMesh = null;
		AllenAtlasMeshLoader loader = new AllenAtlasMeshLoader();
		loader.setColor(getColor());
		tMesh = loader.loadTriMeshByAbbreviation(getAbbreviation());
		return tMesh;
	}
	
	protected TriMesh loadLowDetailMesh() throws IOException{
		String urlString = Scene.allenObjMeshDir + "LD_" + getAbbreviation() + ".obj";
		URL url = null;
		try {
			url = new File(urlString).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return (TriMesh)loadObjFile(url);
	}
	
	private Object loadObjFile(URL model) throws IOException{
		// Create something to convert .obj format to .jme
		FormatConverter converter = new ObjToJme();
		return loadFile(converter, model);
	}
	
	private Object loadFile(FormatConverter converter, URL model) throws IOException{
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		// This will read the .jme format and convert it into a scene graph
		BinaryImporter jbr = new BinaryImporter();
		
		// Use the format converter to convert .obj to .jme
		converter.convert(model.openStream(), BO);
		// Load the binary .jme format into a scene graph
		
		return jbr.load(new ByteArrayInputStream(BO.toByteArray()));
	}
	
}
