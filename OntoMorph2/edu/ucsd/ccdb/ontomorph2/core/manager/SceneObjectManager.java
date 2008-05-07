package edu.ucsd.ccdb.ontomorph2.core.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.scene.IMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISelectable;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISlide;
import edu.ucsd.ccdb.ontomorph2.core.scene.IVolume;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CurveImpl;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICurve;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ISurface;


/**
 * Represents a singleton.
 */

public class SceneObjectManager {

	

	ArrayList<ISlide> slides = null;
	Set<INeuronMorphology> cells = null;
	Set<ICurve> curves = null;
	Set<ISurface> surfaces = null;
	Set<IMesh> meshes = null;
	Set<IVolume> volumes = null;
	/**
	 * Holds singleton instance
	 */
	private static SceneObjectManager instance;
	
	private SceneObjectManager() {
		slides = new ArrayList<ISlide>();
		cells = new HashSet<INeuronMorphology>();
		curves = new HashSet<ICurve>();
		surfaces = new HashSet<ISurface>();
		meshes = new HashSet<IMesh>();
		volumes = new HashSet<IVolume>();
	}

	public void addSlide(ISceneObject s) {
		slides.add((ISlide) s);
		
	}

	public void addCell(ISceneObject s) {
		cells.add((INeuronMorphology) s);
	}
	
	public MyNode getCellTree() {
		MyNode root = new MyNode("Cells", null);
		
		for (INeuronMorphology n : getCells()) {
			MyNode node = new MyNode(n.getName(), n);
			
			for (ISemanticThing t : ((ISemanticsAware)n).getAllSemanticThings()) {	
				node.children.add(new MyNode(t.getLabel(), t));
			}
			
			root.children.add(node);
		}
				
		return root;
	}

	public void addVolume(ISceneObject s) {
		volumes.add((IVolume) s);
	}

	public Set<IVolume> getVolumes() {
		return volumes;
	}

	public Set<INeuronMorphology> getCells() {
		return cells;
	}

	public ArrayList<ISlide> getSlides() {
		return slides;
	}

	public void addMesh(ISceneObject s) {
		meshes.add((IMesh) s);
	}

	public Set<IMesh> getMeshes() {
		return meshes;
	}

	public Set<ICurve> getCurves() {
		return curves;
	}

	public Set<ISurface> getSurfaces() {
		return surfaces;
	}

	public void addCurve(CurveImpl curve1) {
		curves.add(curve1);
	}

	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	static public SceneObjectManager getInstance() {
		if (instance == null) {
			instance = new SceneObjectManager();
		}
		return instance;
	}

}
