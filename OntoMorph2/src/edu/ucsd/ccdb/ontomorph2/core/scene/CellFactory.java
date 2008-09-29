package edu.ucsd.ccdb.ontomorph2.core.scene;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.MorphMLNeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Contains methods for special construction of NeuronMorphologies.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 *
 */
public class CellFactory {
	
	private static CellFactory instance = null;
	
	public static CellFactory getInstance() {
		if (instance == null) {
			instance = new CellFactory();
		}
		return instance;
	}
	
	private CellFactory() {}
	
	/**
	 * For conveiniance, cells can be created without updating the scene. This may be useful for making many cells at once.
	 * If the parameters are erroneous, the factory will attempt to make a free-floating cell
	 * @param cellType Semantic string
	 * @param modelURL	String to the filename of the 3D-model file
	 * @param crvParent The parent curve to attach the cell to. If null, the cell will be free-floating.
	 * @param updateView True - forces Viewto update the scene. If creating many cells at once, it is nice to only redraw at the end 
	 * @return cell created
	 */
	public NeuronMorphology createCell(String cellType, String modelURL, Curve3D crvParent, boolean updateView)
	{
		NeuronMorphology ncell = null;
		
		if (null == modelURL)
		{
			System.out.println("Warning: created erroneous cell");
			crvParent = null; //continue to create cell with the assumption its a free floating one
		}
		
		float t = 0.5f;
		
		//create the cell two different ways, depending on whether it's a free-floating or attached cell
		if (null == crvParent)
		{	//free float
			ncell = new MorphMLNeuronMorphology(modelURL, null, t, NeuronMorphology.RENDER_AS_LOD, null);
		}
		else
		{	//attached
			ncell = new MorphMLNeuronMorphology(modelURL, crvParent, t, NeuronMorphology.RENDER_AS_LOD, crvParent.getCoordinateSystem());
		}
		
		ncell.setRelativeScale(0.01f);

		ncell.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(cellType));
		
		ncell.setVisible(true);
		ncell.addObserver(SceneObserver.getInstance()); //add an observer to the new cell
		
		
		if ( updateView) View.getInstance().getScene().changed(Scene.CHANGED_CELL); //
		
		return ncell;
	}
	
	public void createFreeCell(String modelURL)
	{
		//FIND WHERE to put it
		Vector3f camPos = View.getInstance().getCameraView().getCamera().getLocation();
		Vector3f camDir = View.getInstance().getCameraView().getCamera().getDirection().normalize().mult(30f); //get 4 unit-direction 
		Vector3f dest = camPos.add(camDir);
			
		NeuronMorphology nc = createCell("harcoded_semantics", modelURL, null, true);	//create the cell
		//place the thing in front of the camera
		nc.setCoordinateSystem(null);
		nc.setRelativePosition(new PositionVector(dest));
	}
	

	public void createCellOn(Tangible src, String type, String modelURL)
	{
		NeuronMorphology nc = null;
		Curve3D ocurve = null;
		
		//INITIAL
		//first, set things up and get the Reference Curve
		float t = 0.5f;	//default is middle
		if ( src instanceof Curve3D)
		{
			ocurve = (Curve3D) src;
		}
		else if ( src instanceof CurveAnchorPoint)
		{
			CurveAnchorPoint ocp = (CurveAnchorPoint) src;
			ocurve = ocp.getParentCurve();
			t = ocp.aproxTime();	//make the cell appear on the anchorpoints time
		}
		else if ( src != null)
		{
			ocurve = CurveFactory.getInstance().createCurve(src);
			//ocurve = null;
		}
		else
		{
			//exit early without updating the scene
			System.out.println("Cell not created, error with source");
			return;
		}
		
		//WHERE
		//Find out where to put it
		
		//CREATE
		//nc = cellFactory(type, ocurve);	//create the cell
		nc= createCell(type, modelURL, ocurve, true);	//create the cell and update display
		
		nc.select();
	}

}
