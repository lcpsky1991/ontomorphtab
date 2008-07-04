package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

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

import neurolucida.generated.NeurolucidaData;
import neurolucida.generated.Tree;
import neuroml.generated.Level2Cell;
import neuroml.generated.NeuroMLLevel2;
import neurolucida.generated.Point;
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
 * Describes the morphology of the cell, loaded by a Neurolucida XML file
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see NeuronMorphology
 */
public class NeurolucidaNeuronMorphology extends NeuronMorphology{
	
	List<Tree> treeList = null;
	
	public NeurolucidaNeuronMorphology(URL morphLoc) {
		_morphLoc = morphLoc;
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance("neurlucida.generated");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			JAXBElement o = (JAXBElement)unmarshaller.unmarshal(new File(_morphLoc.getFile()));
			
			NeurolucidaData neuroData = (NeurolucidaData)o.getValue();
			treeList = neuroData.getTree();
			
		} catch (JAXBException e) {
			throw new OMTException("Problem loading " + _morphLoc.getFile(), e);
		}
		
		this.addObserver(SceneObserver.getInstance());
	}
	
	public NeurolucidaNeuronMorphology(URL morphLoc, PositionVector position, RotationVector rotation) {
		this(morphLoc);
		setRelativePosition(position);
		setRelativeRotation(rotation);
	}
	
	public NeurolucidaNeuronMorphology(URL morphLoc, PositionVector position, RotationVector rotation, 
			CoordinateSystem c) {
		this(morphLoc, position, rotation);
		this.setCoordinateSystem(c);
	}

	
	public NeurolucidaNeuronMorphology(URL morphLoc, PositionVector position, 
			RotationVector rotation, String renderOption) {
		this(morphLoc, position, rotation);
		setRenderOption(renderOption);
	}
	
	public NeurolucidaNeuronMorphology(URL morphLoc, PositionVector position, 
			RotationVector rotation, String renderOption, CoordinateSystem c) {
		this(morphLoc, position, rotation, renderOption);
		this.setCoordinateSystem(c);
	}
	
	public NeurolucidaNeuronMorphology(URL morphLoc, Curve3D curve, float time, String renderOption) {
		this(morphLoc);
		_curve = curve;
		_time = time;
		this.positionAlongCurve(curve, time);
		setRenderOption(renderOption);
	}
	
	public NeurolucidaNeuronMorphology(URL morphLoc, Curve3D curve, float time, 
			String renderOption, CoordinateSystem c) {
		this(morphLoc, curve, time, renderOption);
		this.setCoordinateSystem(c);
	}

	/**
	 * Get the URL for the MorphML file that corresponds to this INeuronMorphology
	 * @return - the URL
	 */
	public URL getNeurolucidaXmlURL() {
		return _morphLoc;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getSegments()
	 */
	public List<ISegment> getSegments() {
		if (segmentList == null) {
			segmentList = new ArrayList<ISegment>();
			
			for (Tree t : treeList) {
				Point p1 = null;
				Point p2 = null;
				for (Object o : t.getPointOrTree()) {
					if (o instanceof Point) {
						p1 = (Point)o;
						
						//skip spines for now
						if (p1.getComment().equals("spine")) {
							continue;
						}
						
						//more processing here to extract segments.
						//see etc/neurolucida-xml/1132.xml
					}
				}
				/*
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
				*/
			}
		}
		return segmentList;
	}
	

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getSegmentGroups()
	 */
	public Set<ISegmentGroup> getSegmentGroups() {
		if (segmentGroupList == null) {
			segmentGroupList = new HashSet<ISegmentGroup>();
			/*
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
				//Hackish way to extract some info from the MorphML Files I happen to have
				// This needs to be generalized
				for (String s : cab.getGroup()) {
					if ("dendrite_group".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticClass("sao:sao1211023249"));
					}
					if ("soma_group".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticClass("sao:sao1044911821"));
					} 
					if ("axon_group".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticClass("sao:sao1770195789"));
						//should be adding all these segGroups to the Axon class and treating them as a separate unit.
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
			}*/
		}
		return segmentGroupList;
	}

}
