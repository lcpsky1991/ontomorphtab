package edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.morphml.metadata.schema.Point3D;
import org.morphml.metadata.schema.impl.Point3DImpl;
import org.morphml.morphml.schema.Cable;
import org.morphml.networkml.schema.CellInstance;
import org.morphml.networkml.schema.CurveAssociation;
import org.morphml.networkml.schema.impl.CellInstanceImpl;
import org.morphml.networkml.schema.impl.CurveAssociationImpl;
import org.morphml.neuroml.schema.Level3Cell;
import org.morphml.neuroml.schema.Level3Cells;
import org.morphml.neuroml.schema.NeuroMLLevel3;
import org.morphml.neuroml.schema.XWBCTangible;
import org.morphml.neuroml.schema.impl.NeuromlImpl;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.MemoryCacheRepository;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationQuat;
import edu.ucsd.ccdb.ontomorph2.core.tangible.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.tangible.ContainerTangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

/** .
 * Describes the morphology of the cell, loaded by a MorphML file
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see NeuronMorphology
 */
public class NeuronMorphology extends Tangible{
	
	public static final String RENDER_AS_LINES = "lines";
	public static final String RENDER_AS_CYLINDERS = "cylinders";
	public static final String RENDER_AS_LOD = "LOD";
	public static final String RENDER_AS_LOD_2 = "LOD2";
	public static final String RENDER_AS_DETAILED_BOXES = "detailed_boxes";

	//public static final String RENDER_AS_CYLINDER_BATCH = "cylinder_batch";

	String _renderOption = RENDER_AS_LINES; //default render option
	
	Set<INeuronMorphologyPart> segmentGroupList = null;
	PositionVector lookAtPosition = null;
	
	CurveAssociation curveAssoc = null;

	Curve3D _curve = null;
	float _time = 0.0f;

	private Vector3f _upVector;
	
	NeuronCable tempCable = null;
	Level3Cell theCell = null;
	CellInstance cellInstance = null;
	
	public NeuronMorphology(String name)
	{
		//try to look it up in the DB first?
		this(name, null, null);
		
	}
	

	public NeuronMorphology(String name, PositionVector position, RotationQuat rotation) 
	{
		super(name);
		cellInstance = new CellInstanceImpl();

		cellInstance.setLocation(super.getPosition().toPoint3D());
		cellInstance.setRotation(super.getRotation().toWBCQuat());
		cellInstance.setId((BigInteger.valueOf(new Random().nextLong())));
		
		if (position != null)
		{
			setPosition(position);
		}
		if (rotation != null) 
		{
			setRotation(rotation);
		}
	}

	
	public NeuronMorphology(String name, PositionVector position, RotationQuat rotation, String renderOption) 
	{
		this(name, position, rotation);
		setRenderOption(renderOption);
	}
	
	
	public NeuronMorphology(String name, Curve3D curve, float time, String renderOption) {
		this(name);
		setCurve(curve);
		setTime(time);
		this.positionAlongCurve(curve, time);
		setRenderOption(renderOption);
	}
	
	public NeuronMorphology(String name, CellInstance ci) {
		super(name);
		this.cellInstance = ci;
		if (ci.getCurveAssociation() != null) 
		{
			this.curveAssoc = ci.getCurveAssociation();
			for (Curve3D curve : OntoMorph2.getCurrentScene().getCurves()) 
			{
				if ( curve.getMorphMLCurve().getId().equals(ci.getCurveAssociation().getCurveId()))
				{
					float t = (float)curveAssoc.getTime();
					System.out.println("Created NM with time " + t + " on " + curveAssoc.getCurveId().toString());
					this.setCurve(curve);
					this.setTime(t);
					this.positionAlongCurve(curve, this.getTime());
					return;
				}
			}
		}
	}


	public CurveAssociation getCurveAssociation() {
		return this.curveAssoc;
	}


	/**
	 * Get the Curve that this NeuronMorphology has been associated with
	 * @return
	 */
	public Curve3D getCurve() {
		return _curve;
	}
	
	public PositionVector getPosition() {
		return new PositionVector(cellInstance.getLocation());
	}
	
	public RotationQuat getRotation() {
		return new RotationQuat(cellInstance.getRotation());
	}
	
	public OMTVector getScale() {
		return new OMTVector(cellInstance.getScale());
	}
	
	public void setPosition(PositionVector pos, boolean flagChanged)
	{
		
		if (pos != null) 
		{
			cellInstance.setLocation(pos.toPoint3D());
			if (flagChanged) 
			{
				this.save();
				changed(CHANGED_MOVE);
			}
		}
	}
	
	public void setRotation(RotationQuat rot) {
		if (rot != null) {
			cellInstance.setRotation(rot.toWBCQuat());
			this.save();
			changed(CHANGED_ROTATE);
		}
	}
	
	public void setScale(OMTVector v) 
	{
		cellInstance.setScale(v.toPoint3D());
		this.save();
		changed(CHANGED_SCALE);
	}
	
	
	public void setCurve(Curve3D curve) {
		_curve = curve;
		if (curve == null) {
			curveAssoc = null;
			cellInstance.setCurveAssociation(null);
		} else {
			curveAssoc = new CurveAssociationImpl();
			curveAssoc.setCurveId(curve.getMorphMLCurve().getId());
			curveAssoc.setTime(_time);
			cellInstance.setCurveAssociation(curveAssoc);
		}
		save();
	}
	
	public void setTime(float time)
	{
		_time = time;
		if (curveAssoc != null) {
			curveAssoc.setTime(time);
		}
		save();
	}
	
	/**
	 * Disassociates a curve with this morphology and attaches this morphology to a different curve
	 * @param c
	 * @return True if successful. False if failed.
	 */
	public boolean attachTo(Curve3D c)
	{
		if (c != null)
		{
			this.setCurve(c);
			positionAlongCurve(_curve, _time);
			return true;
		}
		return false;
	}
	
	/**
	 * Retrieves the "time" along the curve that this INeuronMorphology is positioned at
	 * @return
	 */
	public float getTime() {
		return _time;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getRenderOption()
	 */
	public String getRenderOption() {
		return _renderOption;
	}
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#setRenderOption(java.lang.String)
	 */
	public void setRenderOption(String renderOption) {
			_renderOption = renderOption;
	}

	/*
	public List<ISemanticThing> getAllSemanticThings() {
		List<ISemanticThing> l = new ArrayList<ISemanticThing>();
		l.addAll(getSemanticThings());
		for (ICable sg : this.getSegmentGroups()) {
			l.addAll(sg.getAllSemanticThings());
		}
		return l;
	}*/
	
	/*
	public void addSemanticClass(String classURI) {
		semanticThings.add(GlobalSemanticRepository.getInstance().getSemanticClass(classURI));
	}*/

	
	/**
	 * Tells whether this cell is attached to a parent curve
	 * @return True if this cell has NO parent curve
	 */
	public boolean isFreeFloating()
	{
		if (_curve != null) return false;
		return true;
	}
	
	
	/**
	 * Overrides the general Tangible move() because most cells will be attached to a curve
	 * therefore, in most cases cells only move along a curve's time
	 */
	
	public PositionVector move(float dx, float dy, int mx, int my)
	{
		//get changes in mouse movement
		//if this cell is a free-floating cell, then move it as normal
		PositionVector p = null;
		if (this.isFreeFloating())
		{
			p = super.move(dx,dy, mx, my);
		}
		else 
		{
			//the cell is attached to a curve
			//move the cell by changing it's time signature on the parent curve
			PositionVector prev = this.getPosition();
			
			this._time += 0.001f * dx; //the dx passed may be negative
			
			if (_time <= 0 ) setTime(0.001f);
			if (_time >= 1) setTime(0.999f);
			this.positionAlongCurve(_curve,_time);
			//p = new PositionVector(prev.asVector3f().subtract(this.getRelativePosition().asVector3f())); //return the displacement
			
		}
		
		//apply the movement
		changed(CHANGED_MOVE);
		return p;
	}
	
	
	
	/**
	 * Set the position of this NeuronMorphology at point time
	 * along curve c, or does nothing if it is a free-floating neuron
	 * @return True if moved successfully, returns false if it does not move (free-floating neurons)
	 * @param c The curve to position this neuron along
	 * @param time The time to place it along the curve from [0,1]
	 */
	public boolean positionAlongCurve(Curve3D c, float time) 
	{
		if ( _curve != null)
		{
			setPosition(new PositionVector(((Curve3D)c).getPoint(time)));
			setTime(time);
			return true;
		}
		return false;
	}

	/**
	 * Rotates the NeuronMorphology to aim its 'up' direction towards p
	 * @param p
	 */
	public void lookAt(PositionVector p) {
		lookAtPosition = p;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getLookAtPosition()
	 */
	public PositionVector getLookAtPosition() {
		return lookAtPosition;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getName()
	 */
	//public abstract String getName();

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#setUpVector(com.jme.math.Vector3f)
	 */
	public void setUpVector(Vector3f vector3f) {
		_upVector = vector3f;
	}
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getUpVector()
	 */
	public Vector3f getUpVector() {
		if (_upVector != null) {
			return _upVector;
		}
		return Vector3f.UNIT_Y;
	}
	
	/**
	 * Returns the AllenMeshBrainRegion that this neuron morphology is currently located in
	 * @return
	 */
	public BrainRegion getEnclosingBrainRegion(){
		/*
		if (this.getCoordinateSystem() != null && this.getCoordinateSystem() instanceof AllenCoordinateSystem) {
			OMTVector position = this.getPosition();
			return ReferenceAtlas.getInstance().getBrainRegionByVoxel((int)position.x, (int)position.y, (int)position.z);
		} 
		throw new OMTException("Cannot get encolosing brain region from a NeuronMorphology that is not set to the AllenCoordinateSystem", null);
		*/
		return null;
	}


	public Level3Cell getMorphMLCell() 
	{
		if (theCell == null) 
		{
			//try to retrieve file from the cache
			if (MemoryCacheRepository.getInstance().isFileCached(getName())) {
				theCell = (Level3Cell) MemoryCacheRepository.getInstance().getCachedFile(getName());
			}
			if (theCell != null) {
				Log.warn("Successfully uncached cell " + getName() + "!");
				return theCell;
			}
			
			try 
			{
				//search for file in global database by name
				theCell = (Level3Cell) DataRepository.getInstance().findMorphMLByName(getName());
			}
			catch (Exception e) 
			{
				Log.warn("Did not find " + getName() + " neuron morphology in the database.  Trying to load from disk now...");
			}
			
			if (theCell != null) 
			{
//				store the file in the DataRepository once it is loaded for the next time.
				MemoryCacheRepository.getInstance().cacheFile(getName(), theCell);
				
				Log.warn("Successfully loaded cell " + getName() + " from the DB!");
				return theCell;
			}
			//if not found, search in expected directory for xml file
			try {
				URL cellURL = new File(Scene.morphMLDir + getName() + ".morph.xml").toURI().toURL();
				
				if (cellURL != null) {
					JAXBContext context = JAXBContext.newInstance("org.morphml.neuroml.schema");
					//Create the unmarshaller
					final Unmarshaller unmarshaller = context.createUnmarshaller();
					//Unmarshall the XML
					NeuroMLLevel3 neuroml = (NeuromlImpl)unmarshaller.unmarshal(new File(cellURL.getFile()));
					
					Level3Cells c = neuroml.getCells();
					
					assert c.getCell().size() == 1;
					theCell = (Level3Cell)c.getCell().get(0);
					
//					store the file in the DataRepository once it is loaded for the next time.
					MemoryCacheRepository.getInstance().cacheFile(getName(), theCell);
					DataRepository.getInstance().saveFileToDB(theCell);
					Log.warn("Storing cell " + getName() + " in the DB");
				}
			} catch (Exception e) {
				throw new OMTException("Cannot load " + getName() + " morphology! ", e);
			}
		}
		return theCell;
	}	

	/**
	 * Says how many cables are associated with this neuron morphology
	 */
	public int getCableCount() {
		return getMorphMLCell().getCables().getCable().size();
	}

	/**
	 * Retrieves the cable at position i.  IMPORTANT NOTE: This method does not return a new reference 
	 * each time it is called.  Instead it uses the same instance of a cable each time and simply
	 * calls a set method to make it into the appropriate cable.  Do not add these cables
	 * to any collections or they will not work correctly.
	 */
	public NeuronCable getCable(int i) {
		if (this.tempCable == null) {
			this.tempCable = new NeuronCable(this, (Cable)getMorphMLCell().getCables().getCable().get(i));
			return this.tempCable;
		} 
		tempCable.setMorphMLCable((Cable)getMorphMLCell().getCables().getCable().get(i));
		return tempCable;
	}

	/**
	 * Get a cable by its id, indepedent of its numerical position
	 * @param id
	 * @return
	 */
	public NeuronCable getCable(BigInteger id) {
		for (int i = 0; i < getCableCount(); i++) {
			NeuronCable c = getCable(i);
			if (c.getId().equals(id)) {
				return c; 
			}
		}
		return null;
	}
	
	public void save()
	{
		super.save();
		DataRepository.getInstance().saveFileToDB(cellInstance);
		
	}


	public CellInstance getMorphMLCellInstance() 
	{
		//mirrors all of the parts in theSpatial over to the cellInstance (whcih is usedin saving and loading)
		//cellInstance.setLocation(theSpatial.getPosition());
		//cellInstance.setRotation(theSpatial.getRotation());
		//cellInstance.setScale(theSpatial.getScale());
		
		
		
		return this.cellInstance;	
	}

}
