package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
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
 * Is the keeper for all Tangibles that have been intialized in the system.  
 * Keeps track of selection of Tangibles.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class TangibleManager {

	

	private ArrayList<Tangible> selectedThings = null; 
	ArrayList<Tangible> tangibles = null;
	
	/**
	 * Holds singleton instance
	 */
	private static TangibleManager instance;
	
	private TangibleManager() 
	{
		selectedThings = new ArrayList<Tangible>();
		tangibles = new ArrayList<Tangible>();

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

	public int countSelected()
	{
		return selectedThings.size();
	}
	
	/**
	 * @author caprea
	 * @return the most recently selected item (last item on the list), or NULL of list is empty
	 */
	public Tangible getSelectedRecent()
	{
		//return the last item on the list
		if (selectedThings.size() > 0)	//only if the list is not empty
		{
			return selectedThings.get(selectedThings.size() - 1);
		}
		else
		{
			return null;
		}
	}
	
	public Tangible getSelected(int index)
	{
		return selectedThings.get(index);
	}
	
	public Set<Volume> getVolumes() {
		Set<Volume> volumes = new HashSet<Volume>();
		for (Tangible t : this.tangibles) {
			if (t instanceof Volume) {
				volumes.add((Volume)t);
			}
		}
		return volumes;
	}

	public Set<NeuronMorphology> getCells() {
		Set<NeuronMorphology> cells = new HashSet<NeuronMorphology>();
		for (Tangible t : this.tangibles) {
			if (t instanceof NeuronMorphology) {
				cells.add((NeuronMorphology)t);
			}
		}
		return cells;
	}

	public Set<Slide> getSlides() {
		Set<Slide> slides = new HashSet<Slide>();
		for (Tangible t : this.tangibles) {
			if (t instanceof Slide) {
				slides.add((Slide)t);
			}
		}
		return slides;
	}

	public Set<DataMesh> getMeshes() {
		Set<DataMesh> meshes = new HashSet<DataMesh>();
		for (Tangible t : this.tangibles) {
			if (t instanceof DataMesh) {
				meshes.add((DataMesh)t);
			}
		}
		return meshes;
	}

	public Set<Surface> getSurfaces() {
		Set<Surface> surfaces = new HashSet<Surface>();
		for (Tangible t : this.tangibles) {
			if (t instanceof Surface) {
				surfaces.add((Surface)t);
			}
		}
		return surfaces;
	}
	
	public Set<Curve3D> getCurves() {
		Set<Curve3D> surfaces = new HashSet<Curve3D>();
		for (Tangible t : this.tangibles) {
			if (t instanceof Curve3D) {
				surfaces.add((Curve3D)t);
			}
		}
		return surfaces;
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
	
	/**
	 * Calls unselect on all selected Tangibles
	 *
	 */
	public void unselectAll()
	{
		
		try {
 		     //call unselect on all objects rather than
			//just clearing the list to trigger the changed 
			//method in each object.
			for (Tangible t : selectedThings) {
				t.unselect();
			}
		} catch (ConcurrentModificationException e) {
			//if we stepped afoul of modifying the array at the same time
			//as another process, just try again.. can't be concurrently
			//modifying it forever...
			unselectAll();
		}
	}
	
	public void setHighlightColor(Color c)
	{
		
	}
	
	public boolean isSelected(Tangible thing)
	{
		return selectedThings.contains(thing);
	}

	public void addTangible(Tangible tangible) {
		this.tangibles.add(tangible);
	}
}
