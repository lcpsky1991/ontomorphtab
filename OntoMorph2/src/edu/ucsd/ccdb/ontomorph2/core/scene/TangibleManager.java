package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.tangible.ContainerTangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.Axon;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.MultiHashSetMap;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.TreeNode;


/**
 * Is the keeper for all Tangibles that have been intialized in the system.  
 * Keeps track of selection of Tangibles.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class TangibleManager {

	private ArrayList<Tangible> selectedThings = null; 
	private Tangible selectedRollover = null;
	ArrayList<Tangible> tangibles = null;
	MultiHashSetMap tangiblesContainingTangibles = null;
	boolean multiSelect = false;
	boolean isRollover = false;
	MultiHashSetMap tangiblesContainedByTangibles = null;
	
	/**
	 * Holds singleton instance
	 */
	private static TangibleManager instance;
	
	private TangibleManager() 
	{
		selectedThings = new ArrayList<Tangible>();
		tangibles = new ArrayList<Tangible>();
		tangiblesContainingTangibles = new MultiHashSetMap();
		tangiblesContainedByTangibles = new MultiHashSetMap();
	}
	
	public TreeNode getCellTree() {
		TreeNode root = new TreeNode("Cells", null);
		
		for (NeuronMorphology n : getCells()) {
			TreeNode node = new TreeNode(n.getName(), n);
			
			for (SemanticThing t : ((ISemanticsAware)n).getAllSemanticClasses()) {	
				node.children.add(new TreeNode(t.toString(), t));
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
	
	/**
	 * If more than one object is selected, then this is an attempt to
	 *  make an 'intelligent' decision on which item should represent the group,
	 *  or if only one object is selected, return that
	 * @return the Tangible that best represents the group of all selected objects
	 */
	public Tangible getSelectedRelevent()
	{
		Tangible leader = null;
		
		if (selectedThings.size() <= 1)
		{
			leader = getSelectedRecent();
		}
		else
		{
			//give the first curve or the first axon found
			
			for ( Tangible part : selectedThings)
			{
				if (part instanceof Axon)
				{
					return part;
				}
				else if (part instanceof Curve3D)
				{
					return part;
				}
			}
		}
		
		return leader;
	}
	
	
	public List<Tangible> getSelected() {
		return this.selectedThings;
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
	
	public Set<Curve3D> getCurves()	//TODO: only return regular curves (not axons)
	{
		Set<Curve3D> curves = new HashSet<Curve3D>();
		for (Tangible t : this.tangibles) 
		{
			if (t instanceof Curve3D) 
			{
				curves.add((Curve3D)t);
			}
		}
		return curves;
	}
	
	public Set<Axon> getAxons()
	{
		Set<Axon> curves = new HashSet<Axon>();
		for (Tangible t : this.tangibles) 
		{
			if (t instanceof Axon) 
			{
				curves.add((Axon)t);
			}
		}
		return curves;
	}
	
	public Set<SphereParticles> getParticles() {
		Set<SphereParticles> particles = new HashSet<SphereParticles>();
		for (Tangible t : this.tangibles) {
			if (t instanceof SphereParticles) {
				particles.add((SphereParticles)t);
			}
		}
		return particles;
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
		//if we are not doing multi selection, get rid of everything 
		//else but this thing.
		if (!getMultiSelect()) { 
			this.unselectAll();
		}
//		only add if not already on selected list
		if ( !selectedThings.contains(thing) )
		{
			selectedThings.add(thing);	
		}
		Log.warn("Currently selected: " + selectedThings.toString());
	}
	
	public void setCurrentRolledOver(Tangible thing)
	{
		selectedRollover = thing;	
	}
	
	public void setMultiSelect(boolean multi) {
		this.multiSelect = multi;
	}
	
	public boolean getMultiSelect() {
		return this.multiSelect;
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

		try 
		{
 		     //call unselect on all objects rather than
			//just clearing the list to trigger the changed 
			//method in each object.
			
			//Must loop backward in deselection because they have to be 'removed' from the list that way
			//otherwise you get some orphaned updates			
			for (int i=selectedThings.size() - 1; i >= 0; i--)
			{
				Tangible t = selectedThings.get(i);
				t.unselect();
			}
		} 
		catch (ConcurrentModificationException e) {
			//if we stepped afoul of modifying the array at the same time
			//as another process, just try again.. can't be concurrently
			//modifying it forever...
			unselectAll();
		}
		
		//clear the memory at the end of selection
		selectedThings.clear();
	}
	
	public boolean isSelected(Tangible thing)
	{
		return selectedThings.contains(thing);
	}

	public boolean isRollOver(Tangible thing){
		//System.out.println("call to is RollOver");
		return selectedRollover.equals(thing);
	}
	public boolean addTangible(Tangible tangible) {
		return this.tangibles.add(tangible);
	}
	
	public boolean removeTangible(Tangible t)
	{
		return this.tangibles.remove(t);
	}

	public void loadFile(File file) {
		if (file != null && file.canRead()) {
			Log.warn("Trying to open file " + file.getPath());
			if (file.getName().endsWith(".tiff") || file.getName().endsWith(".tif")) {
				//add slide to scene
				//TangibleManager.getInstance().addTangible(new Slide(file.toURI().toURL(), null, null, null));
			} else if (file.getName().endsWith(".morph.xml")){
				//get first part of file name without extensions
				String fileName = file.getName().split("\\.")[0];
				
				//for demo purposes, load in a default location with default settings
				//for now can only load files in the /etc/hippocampus directory if they aren't already in the DB
				CellFactory.getInstance().createFreeCell(fileName);
				/*
				NeuronMorphology cell3 = new NeuronMorphology(fileName, 
						new PositionVector(289f, -118f, -180f), null, 
						NeuronMorphology.RENDER_AS_LOD_2);
				RotationQuat v = new RotationQuat(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Y));
				cell3.setRelativeRotation(v);
				//cell3.setCoordinateSystem(d);
				cell3.setRelativeScale(0.01f);
				
				//semantic thing for hippocampal CA3 neuron
				cell3.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.CA3_PYRAMIDAL_CELL_CLASS));
				cell3.getMainSemanticInstance();
				
				cell3.setVisible(true);
				
				OntoMorph2.getCurrentScene().changed(Scene.CHANGED_CELL);
				*/
				
			} else {
				Log.warn("This is not a file type that can be opened");
			}

		} else {
			Log.warn("Can't open File!");
		}
	}

	/**
	 * Notes a containment relationship between two tangibles
	 * @param container - the Tangible that encloses contained
	 * @param contained - the Tangible that is enclosed by container
	 */
	public void addContainedTangible(Tangible container, Tangible contained) {
		this.tangiblesContainingTangibles.put(container, contained);
		this.tangiblesContainedByTangibles.put(contained, container);
	}

	/**
	 * Returns a collection of those tangibles that this tangible encloses / contains
	 * @param container - the Tangible to discover what it contains
	 * @return 
	 */
	public Set<Tangible> getContainedTangibles(ContainerTangible container) {
		Set<Tangible> containedTangibles = new HashSet<Tangible>();
		Collection c = (Collection)this.tangiblesContainingTangibles.get(container);
		if (c == null) {
			return containedTangibles;
		}
		for (Iterator it = c.iterator(); it.hasNext();) {
			containedTangibles.add((Tangible)it.next());
		}
		return containedTangibles;
	}

	/**
	 * Returns a collection of those tangibles that this tangible is enclosed by
	 * @param contained - the Tangible to discover what it is enclosed by
	 * @return
	 */
	public Set<ContainerTangible> getContainerTangibles(Tangible contained) {
		Set<ContainerTangible> containerTangibles = new HashSet<ContainerTangible>();
		Collection c = (Collection)this.tangiblesContainedByTangibles.get(contained);
		if (c == null) {
			return containerTangibles;
		}
		for (Iterator it = c.iterator(); it.hasNext();) {
			Tangible t = (Tangible)it.next();
			if ((t instanceof ContainerTangible) == false ) { continue; }
			containerTangibles.add((ContainerTangible)t);
		}
		return containerTangibles;
	}
	
	/**
	 * Removes a Tangible from a containment relationship.
	 * @param container - the Tangible that encloses contained
	 * @param contained - the Tangible that is enclosed by container
	 */
	public void removeContainedTangible(Tangible container, Tangible contained) {
		this.tangiblesContainingTangibles.remove(container, contained);
		this.tangiblesContainedByTangibles.remove(contained, container);
	}
	

	public void viewSlides() {
		for (Slide s : getSlides())
		{
			s.setVisible(true);
		}
	}

	public void hideSlides() {
		for (Slide s : getSlides())
		{
			s.setVisible(false);
		}
	}
}
