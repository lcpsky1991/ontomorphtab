package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.scene.objects.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.objects.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.objects.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.objects.SceneObject;
import edu.ucsd.ccdb.ontomorph2.core.scene.objects.Slide;
import edu.ucsd.ccdb.ontomorph2.core.scene.objects.Surface;
import edu.ucsd.ccdb.ontomorph2.core.scene.objects.Volume;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.MyNode;


/**
 * Keeps lists of all scene objects.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SceneObjectManager {

	

	ArrayList<Slide> slides = null;
	Set<NeuronMorphology> cells = null;
	Set<Curve3D> curves = null;
	Set<Surface> surfaces = null;
	Set<DataMesh> meshes = null;
	Set<Volume> volumes = null;
	/**
	 * Holds singleton instance
	 */
	private static SceneObjectManager instance;
	
	private SceneObjectManager() {
		slides = new ArrayList<Slide>();
		cells = new HashSet<NeuronMorphology>();
		curves = new HashSet<Curve3D>();
		surfaces = new HashSet<Surface>();
		meshes = new HashSet<DataMesh>();
		volumes = new HashSet<Volume>();
	}

	public void addSlide(SceneObject s) {
		slides.add((Slide) s);
		
	}

	public void addCell(SceneObject s) {
		cells.add((NeuronMorphology) s);
	}
	
	public MyNode getCellTree() {
		MyNode root = new MyNode("Cells", null);
		
		for (NeuronMorphology n : getCells()) {
			MyNode node = new MyNode(n.getName(), n);
			
			for (ISemanticThing t : ((ISemanticsAware)n).getAllSemanticThings()) {	
				node.children.add(new MyNode(t.getLabel(), t));
			}
			
			root.children.add(node);
		}
				
		return root;
	}

	public void addVolume(SceneObject s) {
		volumes.add((Volume) s);
	}

	public Set<Volume> getVolumes() {
		return volumes;
	}

	public Set<NeuronMorphology> getCells() {
		return cells;
	}

	public ArrayList<Slide> getSlides() {
		return slides;
	}

	public void addMesh(SceneObject s) {
		meshes.add((DataMesh) s);
	}

	public Set<DataMesh> getMeshes() {
		return meshes;
	}

	public Set<Curve3D> getCurves() {
		return curves;
	}

	public Set<Surface> getSurfaces() {
		return surfaces;
	}

	public void addCurve(Curve3D curve1) {
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

	public void addSurface(Surface surf2) {
		surfaces.add(surf2);
	}

}
