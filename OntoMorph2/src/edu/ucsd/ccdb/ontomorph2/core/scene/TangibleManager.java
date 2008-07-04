package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.MyNode;


/**
 * Keeps lists of all scene objects.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class TangibleManager {

	

	private ArrayList<Tangible> selectedThings = null; 
	ArrayList<Slide> slides = null;
	Set<NeuronMorphology> cells = null;
	Set<Curve3D> curves = null;
	Set<Surface> surfaces = null;
	Set<DataMesh> meshes = null;
	Set<Volume> volumes = null;
	/**
	 * Holds singleton instance
	 */
	private static TangibleManager instance;
	
	private TangibleManager() 
	{
		selectedThings = new ArrayList<Tangible>();
		slides = new ArrayList<Slide>();
		cells = new HashSet<NeuronMorphology>();
		curves = new HashSet<Curve3D>();
		surfaces = new HashSet<Surface>();
		meshes = new HashSet<DataMesh>();
		volumes = new HashSet<Volume>();
	}

	public void addSlide(Tangible s)
	{
		slides.add((Slide) s);	
	}

	public void addCell(Tangible s) {
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

	public void addVolume(Tangible s) {
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

	public void addMesh(Tangible s) {
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
	static public TangibleManager getInstance() {
		if (instance == null) {
			instance = new TangibleManager();
		}
		return instance;
	}

	public void addSurface(Surface surf2) {
		surfaces.add(surf2);
	}

	/**
	 * Adds the parameter to the list of 'things' that have been selected
	 * the order of the list corresponds to the order they were clicked in.
	 * 0 - oldest item selected
	 */
	public void select(Tangible thing)
	{
		selectedThings.add(thing);
	}
	
	/**
	 * Removes this tangible from the list of things that have been selected.
	 * @param thing
	 */
	public void unselect(Tangible thing)
	{
		selectedThings.remove(thing);
	}
	
	public void unselectAll()
	{
		selectedThings.clear();
	}
	
	public void setHighlightColor(Color c)
	{
		
	}
	
	public boolean isSelected(Tangible thing)
	{
		return selectedThings.contains(thing);
	}
}
