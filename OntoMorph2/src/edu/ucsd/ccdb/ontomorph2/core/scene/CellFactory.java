package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.HashMap;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTUtility;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Contains methods for special construction of NeuronMorphologies.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 *
 */
public class CellFactory {
	
	//IDs for easily creating cells
	public static final String TYPE_CELL_DG_A = "5199202a";
	public static final String TYPE_CELL_DG_B = "";
	public static final String TYPE_CELL_DG_C = "";
	public static final String TYPE_CELL_PYR_CA1_A = "pc1c";
	public static final String TYPE_CELL_PYR_CA1_B = "pc2a";
	public static final String TYPE_CELL_PYR_CA1_C = "";
	public static final String TYPE_CELL_PYR_CA3_A = "cell1zr";
	public static final String TYPE_CELL_PYR_CA3_B = "cell2zr";
	public static final String TYPE_CELL_PYR_CA3_C = "cell6zr";
	public static final String TYPE_CELL_DISK = "disk";
	
	private HashMap<String, String> cellNameToSemanticClass = new HashMap<String, String>();
	
	private static CellFactory instance = null;
	
	public static CellFactory getInstance() {
		if (instance == null) {
			instance = new CellFactory();
		}
		return instance;
	}
	
	private CellFactory() {
		cellNameToSemanticClass.put(TYPE_CELL_DG_A, SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS);
		
		cellNameToSemanticClass.put(TYPE_CELL_PYR_CA1_A, SemanticClass.CA1_PYRAMIDAL_CELL_CLASS);
		cellNameToSemanticClass.put(TYPE_CELL_PYR_CA1_B, SemanticClass.CA1_PYRAMIDAL_CELL_CLASS);
		
		cellNameToSemanticClass.put(TYPE_CELL_PYR_CA3_A, SemanticClass.CA3_PYRAMIDAL_CELL_CLASS);
		cellNameToSemanticClass.put(TYPE_CELL_PYR_CA3_B, SemanticClass.CA3_PYRAMIDAL_CELL_CLASS);
		cellNameToSemanticClass.put(TYPE_CELL_PYR_CA3_C, SemanticClass.CA3_PYRAMIDAL_CELL_CLASS);
	}
	
	/**
	 * For conveiniance, cells can be created without updating the scene. This may be useful for making many cells at once.
	 * If the parameters are erroneous, the factory will attempt to make a free-floating cell
	 * @param cellType Semantic string
	 * @param modelURL	String to the filename of the 3D-model file
	 * @param crvParent The parent curve to attach the cell to. If null, the cell will be free-floating.
	 * @param updateView True - forces Viewto update the scene. If creating many cells at once, it is nice to only redraw at the end 
	 * @return cell created
	 */
	public NeuronMorphology createCell(String modelURL, Curve3D crvParent, boolean updateView)
	{
		NeuronMorphology ncell = null;
		
		if (null == modelURL)
		{
			Log.warn("Warning: created erroneous cell");
			crvParent = null; //continue to create cell with the assumption its a free floating one
		}
		
		float t = 0.5f;
		
		//create the cell two different ways, depending on whether it's a free-floating or attached cell
		if (null == crvParent)
		{	//free float
			ncell = new NeuronMorphology(modelURL, null, t, NeuronMorphology.RENDER_AS_LOD);
		}
		else
		{	//attached
			ncell = new NeuronMorphology(modelURL, crvParent, t, NeuronMorphology.RENDER_AS_LOD);
		}
		
		ncell.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(this.cellNameToSemanticClass.get(modelURL)));
		
		//creates a SemanticInstance of this cell in the SemanticRepository
		ncell.getSemanticInstance();
		ncell.setVisible(true);
		ncell.setScale(0.02f);
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
			
		NeuronMorphology nc = createCell(modelURL, null, true);	//create the cell
		//place the thing in front of the camera
		nc.setPosition(new PositionVector(dest));
	}
	

	public void createCellOn(Tangible src, String modelURL)
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
			ocurve = TangibleFactory.getInstance().createCurve(src);
			//ocurve = null;
		}
		else
		{
			//exit early without updating the scene
			Log.warn("Cell not created, error with source");
			return;
		}
		
		//WHERE
		//Find out where to put it
		
		//CREATE
		//nc = cellFactory(type, ocurve);	//create the cell
		nc = createCell(modelURL, ocurve, true);	//create the cell and update display
		
		nc.select();
	}
	
	/**
	 * Propagates a NeuronMorphology with normal distribution
	 * @param cell
	 */
	public void propagate(NeuronMorphology cell, int howMany)
	{
		for (int i = 0; i < howMany; i++)
		{
			NeuronMorphology copy = createCell(cell.getName(),cell.getCurve(), true);	//create a copy of the cells
			
			float rx=0;
			float ry=0;
			
			//if cell is attached to curve have to scale the movement by alot more
			if (!cell.isFreeFloating())
			{
				copy.positionAlongCurve(cell.getCurve(), cell.getTime()); //start in same place
				rx = (float)OMTUtility.randomNumberGuassian(0, 100);
				copy.move(rx, ry, 0, 0);
			}
			else
			{	//put it at the original place
				copy.setPosition(cell.getPosition());	//start in same place
				rx = (float)OMTUtility.randomNumberGuassian(0, 10) + copy.getPosition().getX();
				ry = (float)OMTUtility.randomNumberGuassian(0, 10) + copy.getPosition().getY();
				copy.setPosition(rx, ry, copy.getPosition().getZ()); //keep the same Z
			}
			
			copy.setScale(cell.getScale()); //make their scales match
			copy.setRotation(cell.getRotation());
			//copy.rotate(rx, 0, new OMTVector(0,1,0)); //for aesthetics rotate them about Y to make them seem more random
			
			
		}
	}

}
