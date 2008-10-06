package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;

import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationQuat;
import edu.ucsd.ccdb.ontomorph2.util.AllenAtlasMeshLoader;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * Defines an anatomical region of the mouse brain.  Contains data about its parent in a 
 * hierarchy of brain regions, color, abbreviation and name
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
	
	public BrainRegion(String name, String abbrev, String parentAbbrev, Color c, 
			String regionId){
		super(name);
		
		Quaternion q = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*180,Vector3f.UNIT_X);
		this.setRotation(new RotationQuat(q));
		
		this.abbrev = abbrev;
		this.parentAbbrev = parentAbbrev;
		this.setColor(c);
		this.regionId = Integer.parseInt(regionId);
		this.setName(name);
	}
	
	public void loadData() {

		long tick = Log.tick();
		//data = loadAllenMesh();
		
		// when low detail meshes are working again, we can employ this loading strategy
		//try {
		data = loadHighDetailMesh();
		//} catch (IOException e) {
		//	Log.warn("Falling back to Allen Mesh for structure " + this.abbrev);
		//	data = loadAllenMesh();
		//}

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
		if (this.parentAbbrev != null && !"".equals(this.parentAbbrev))
			return ReferenceAtlas.getInstance().getBrainRegion(this.parentAbbrev);
		return null;
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
	
	protected TriMesh loadLowDetailMesh(){
		TriMesh t = null;
		try {
			String urlString = Scene.allenLowDetailObjMeshDir + "LD_" + getAbbreviation() + ".obj";
			URL url = new File(urlString).toURI().toURL();
			
			t = (TriMesh)loadObjFile(url);
		} catch (Exception e) {
			throw new OMTException("Cannot load OBJ file: " + this.getAbbreviation(), e);
		}
		return t;
	}
	
	protected TriMesh loadHighDetailMesh(){
		TriMesh t = null;
		try {
			String urlString = Scene.allenHighDetailObjMeshDir + getAbbreviation() + ".obj";
			URL url = new File(urlString).toURI().toURL();
			
			t = (TriMesh)loadObjFile(url);
		} catch (Exception e) {
			throw new OMTException("Cannot load OBJ file: " + this.getAbbreviation(), e);
		}
		return t;
	}
	
	protected void setSemanticClass() {
		/*
		SemanticRepository repo = SemanticRepository.getAvailableInstance();
		String semanticURI = getAllenToBrainInfoMap().get(this.abbrev);
		SemanticClass c = repo.getSemanticClass(semanticURI);
		this.addSemanticClass(c);*/
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
