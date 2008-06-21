package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import neuroml.generated.Level2Cell;
import neuroml.generated.NeuroMLLevel2;
import neuroml.generated.Point;
import neuroml.generated.Segment;
import neuroml.generated.Cell.Cables;
import neuroml.generated.Cell.Segments;
import neuroml.generated.NeuroMLLevel2.Cells;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * Describes the morphology of the cell, independent of different ways of visualizing it.  
 * Since it is a three-dimensional morphology, this will describe points in a local 3D space (MorphML?)
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class NeuronMorphology extends SceneObject{
	
	public static final String RENDER_AS_LINES = "lines"; 
	public static final String RENDER_AS_CYLINDERS = "cylinders";
	public static final String RENDER_AS_LOD = "LOD";
	public static final String RENDER_AS_LOD_2 = "LOD2";
	
	URL _morphLoc = null;
	String _renderOption = RENDER_AS_LINES; //default render option
	ArrayList<ISegment> segmentList = null;
	Set<ISegment> selectedSegmentList = new HashSet<ISegment>();
	Set<ISegmentGroup> selectedSegmentGroupList = new HashSet<ISegmentGroup>();
	Level2Cell theCell;
	Set<ISegmentGroup> segmentGroupList = null;
	List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();
	PositionVector lookAtPosition = null;
	
	Curve3D _curve = null;
	float _time = 0.0f;
	private Vector3f _upVector;
	
	public NeuronMorphology(URL morphLoc) {
		_morphLoc = morphLoc;
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance("neuroml.generated");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			JAXBElement o = (JAXBElement)unmarshaller.unmarshal(new File(_morphLoc.getFile()));
			NeuroMLLevel2 neuroml = (NeuroMLLevel2)o.getValue();
			
			Cells c = neuroml.getCells();
			
			assert c.getCell().size() == 1;
			theCell = c.getCell().get(0);
			
		} catch (JAXBException e) {
			throw new OMTException("Problem loading " + _morphLoc.getFile(), e);
		}
		
		this.addObserver(SceneObserver.getInstance());
	}
	
	public NeuronMorphology(URL morphLoc, PositionVector position, RotationVector rotation) {
		this(morphLoc);
		setRelativePosition(position);
		setRelativeRotation(rotation);
	}
	
	public NeuronMorphology(URL morphLoc, PositionVector position, RotationVector rotation, 
			CoordinateSystem c) {
		this(morphLoc, position, rotation);
		this.setCoordinateSystem(c);
	}

	
	public NeuronMorphology(URL morphLoc, PositionVector position, 
			RotationVector rotation, String renderOption) {
		this(morphLoc, position, rotation);
		setRenderOption(renderOption);
	}
	
	public NeuronMorphology(URL morphLoc, PositionVector position, 
			RotationVector rotation, String renderOption, CoordinateSystem c) {
		this(morphLoc, position, rotation, renderOption);
		this.setCoordinateSystem(c);
	}
	
	public NeuronMorphology(URL morphLoc, Curve3D curve, float time, String renderOption) {
		this(morphLoc);
		_curve = curve;
		_time = time;
		this.positionAlongCurve(curve, time);
		setRenderOption(renderOption);
	}
	
	public NeuronMorphology(URL morphLoc, Curve3D curve, float time, 
			String renderOption, CoordinateSystem c) {
		this(morphLoc, curve, time, renderOption);
		this.setCoordinateSystem(c);
	}

	/**
	 * Get the ICurve that this INeuronMorphology has been associated with
	 * @return
	 */
	public Curve3D getCurve() {
		return _curve;
	}
	
	/**
	 * Retrieves the "time" along the curve that this INeuronMorphology is positioned at
	 * @return
	 */
	public float getTime() {
		return _time;
	}

	public Level2Cell getMorphMLCell() {
		return theCell;
	}
	
	/**
	 * Get the URL for the MorphML file that corresponds to this INeuronMorphology
	 * @return - the URL
	 */
	public URL getMorphMLURL() {
		return _morphLoc;
	}
	
	public String getRenderOption() {
		return _renderOption;
	}
	
	public void setRenderOption(String renderOption) {
		if (RENDER_AS_LINES.equals(renderOption) || 
				RENDER_AS_CYLINDERS.equals(renderOption) ||
				RENDER_AS_LOD.equals(renderOption) ||
				RENDER_AS_LOD_2.equals(renderOption)) {
			_renderOption = renderOption;
		}
	}

	public List<ISegment> getSegments() {
		if (segmentList == null) {
			segmentList = new ArrayList<ISegment>();
			
			List<Segments> segments  = theCell.getSegments();
			for (Segments s : segments) {
				Point p1 = null;
				Point p2 = null;
				for (Segment seg : s.getSegment()) {
					
					if (seg.getProximal() != null) {
						p1 = seg.getProximal();
					} else {
						p1 = p2;
					}
					p2 = seg.getDistal();
					
					float[] prox = {(float)p1.getX(), (float)p1.getY(), (float)p1.getZ()};
					float[] dist = {(float)p2.getX(), (float)p2.getY(), (float)p2.getZ()};
					
					SegmentImpl si = new SegmentImpl(this, seg.getId(), prox, dist, 
							p1.getDiameter().floatValue(), p2.getDiameter().floatValue(), seg.getCable());
					segmentList.add(si);
				}
			}
		}
		return segmentList;
	}
	

	/**
	 * 
	 * @return the ISegmentGroups that are associated with this INeuronMorphology
	 */
	public Set<ISegmentGroup> getSegmentGroups() {
		if (segmentGroupList == null) {
			segmentGroupList = new HashSet<ISegmentGroup>();
			Cables c = theCell.getCables();
			for(neuroml.generated.Cable cab : c.getCable()) {
				BigInteger id = cab.getId();
				ArrayList<ISegment> childSegments = new ArrayList<ISegment>();
				for (ISegment s : this.getSegments()) {
					if (id.equals(s.getSegmentGroupId())) {
						childSegments.add(s);
					}
				}
				SegmentGroupImpl segGroup = new SegmentGroupImpl(this, id, childSegments, cab.getGroup());
				segmentGroupList.add(segGroup);
				/* Hackish way to extract some info from the MorphML Files I happen to have
				 * This needs to be generalized
				 */
				for (String s : cab.getGroup()) {
					if ("dendrite_group".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticClass("sao:sao1211023249"));
					}
					if ("soma_group".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticClass("sao:sao1044911821"));
					} 
					if ("axon_group".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticClass("sao:sao1770195789"));
					}
					if ("apical_dendrite".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticClass("sao:sao273773228"));
					}
					if (s.startsWith("Colour_")) {
						if (s.endsWith("Magenta")) {
							segGroup.setColor(Color.magenta);
						} else if (s.endsWith("Green")) {
							segGroup.setColor(Color.green);
						} else if (s.endsWith("White")) {
							segGroup.setColor(Color.WHITE);
						} else if (s.endsWith("DarkGrey")) {
							segGroup.setColor(Color.darkGray);
						}
					}
				}
			}
		}
		return segmentGroupList;
	}
	

	/** 
	 * Convenience method to select a segment within this INeuronMorphology
	 * @param s - the segment to select
	 */
	public void selectSegment(ISegment s) {
		selectedSegmentList.add(s);
		changed();
	}
	
	/** 
	 * Convenience method to unselect a segment within this INeuronMorphology
	 * @param s - the segmen to unselect
	 */
	public void unselectSegment(ISegment s) {
		selectedSegmentList.remove(s);
		changed();
	}
	
	/**
	 * 
	 * @return all ISegment's that are currently selected
	 */
	public Set<ISegment> getSelectedSegments() {
		return selectedSegmentList;
	}

	public void selectSegmentGroup(ISegmentGroup g) {
		selectedSegmentGroupList.add(g);
		changed();
	}

	public void unselectSegmentGroup(ISegmentGroup g) {
		selectedSegmentGroupList.remove(g);
		changed();
	}

	/**
	 * 
	 * @return all ISegmentGroups that are currently selected
	 */
	public Set<ISegmentGroup> getSelectedSegmentGroups() {
		return selectedSegmentGroupList;
	}

	/**
	 * 
	 * @return true if this INeuronMorphology has ISegmentGroups that are selected, false otherwise
	 */
	public boolean hasSelectedSegmentGroups() {
		return getSelectedSegmentGroups().size() > 0;
	}

	public List<ISemanticThing> getSemanticThings() {
		return this.semanticThings;
	}
	
	public List<ISemanticThing> getAllSemanticThings() {
		List<ISemanticThing> l = new ArrayList<ISemanticThing>();
		l.addAll(this.semanticThings);
		for (ISegmentGroup sg : this.getSegmentGroups()) {
			l.addAll(sg.getAllSemanticThings());
		}
		return l;
	}

	public void addSemanticThing(ISemanticThing thing) {
		semanticThings.add(thing);
		
	}

	public void removeSemanticThing(ISemanticThing thing) {
		semanticThings.remove(thing);
	}
	
	public void addSemanticClass(String classURI) {
		semanticThings.add(SemanticRepository.getInstance().getSemanticClass(classURI));
	}

	/**
	 * Set the position of this NeuronMorphology at point time
	 * along curve c
	 *
	 */
	public void positionAlongCurve(Curve3D c, float time) {
		setRelativePosition(new PositionVector(((Curve3D)c).getPoint(time)));
	}

	/**
	 * Rotates the NeuronMorphology to aim its 'up' direction towards p
	 * @param p
	 */
	public void lookAt(PositionVector p) {
		lookAtPosition = p;
	}

	public PositionVector getLookAtPosition() {
		return lookAtPosition;
	}

	public String getName() {
		int len = _morphLoc.getFile().length();
		return _morphLoc.getFile().substring(len-14, len);
	}

	public void setUpVector(Vector3f vector3f) {
		_upVector = vector3f;
	}
	
	public Vector3f getUpVector() {
		if (_upVector != null) {
			return _upVector;
		}
		return Vector3f.UNIT_Y;
	}
}
