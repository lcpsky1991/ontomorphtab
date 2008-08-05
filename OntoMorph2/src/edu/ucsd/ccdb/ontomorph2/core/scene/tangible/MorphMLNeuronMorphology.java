package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import java.awt.Color;
import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.morphml.morphml.schema.Cable;
import org.morphml.morphml.schema.Segment;
import org.morphml.morphml.schema.Cell.CablesType;
import org.morphml.morphml.schema.Cell.SegmentsType;
import org.morphml.neuroml.schema.Level3Cell;
import org.morphml.neuroml.schema.Level3Cells;
import org.morphml.neuroml.schema.NeuroMLLevel3;
import org.morphml.neuroml.schema.impl.NeuromlImpl;

import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * Describes the morphology of the cell, loaded by a MorphML file
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see NeuronMorphology
 */
public class MorphMLNeuronMorphology extends NeuronMorphology{
	
	URL _morphLoc = null;
	Level3Cell theCell;
	Map<Segment, MorphMLSegmentImpl> segmentMap = new HashMap<Segment, MorphMLSegmentImpl>();
	
	public MorphMLNeuronMorphology(URL morphLoc) {
		Log.warn("Loading MorphMLNeuronMorphology");
		long tick = Log.tick();
		_morphLoc = morphLoc;
		
		JAXBContext context;
		try {
			NeuroMLLevel3 neuroml = null;
			
			if (!DataRepository.getInstance().isFileCached(_morphLoc.getFile(), NeuromlImpl.class)) {
				
				context = JAXBContext.newInstance("org.morphml.neuroml.schema");
//				Create the unmarshaller
				final Unmarshaller unmarshaller = context.createUnmarshaller();
//				Unmarshall the XML
				neuroml = (NeuromlImpl)unmarshaller.unmarshal(new File(_morphLoc.getFile()));
				//cache it for next time
				DataRepository.getInstance().cacheFile(_morphLoc.getFile(), neuroml);
			} else {
//				if this file has already been loaded, retrieve it from the cache.
				Log.warn("Retrieving this MorphMLNeuronMorphology from the cache!");
				neuroml = (NeuroMLLevel3)DataRepository.getInstance().getCachedFile(_morphLoc.getFile(), NeuromlImpl.class);
			}

			Level3Cells c = neuroml.getCells();
			
			assert c.getCell().size() == 1;
			theCell = (Level3Cell)c.getCell().get(0);
			
		} catch (JAXBException e) {
			throw new OMTException("Problem loading " + _morphLoc.getFile(), e);
		}
		Log.tock("Loading MorphMLNeuronMorphology " + _morphLoc.getFile() + " took ", tick);
	}
	
	public MorphMLNeuronMorphology(URL morphLoc, PositionVector position, RotationVector rotation) {
		this(morphLoc);
		setRelativePosition(position);
		setRelativeRotation(rotation);
	}
	
	public MorphMLNeuronMorphology(URL morphLoc, PositionVector position, RotationVector rotation, 
			CoordinateSystem c) {
		this(morphLoc, position, rotation);
		this.setCoordinateSystem(c);
	}

	
	public MorphMLNeuronMorphology(URL morphLoc, PositionVector position, 
			RotationVector rotation, String renderOption) {
		this(morphLoc, position, rotation);
		setRenderOption(renderOption);
	}
	
	public MorphMLNeuronMorphology(URL morphLoc, PositionVector position, 
			RotationVector rotation, String renderOption, CoordinateSystem c) {
		this(morphLoc, position, rotation, renderOption);
		this.setCoordinateSystem(c);
	}
	
	public MorphMLNeuronMorphology(URL morphLoc, Curve3D curve, float time, String renderOption) {
		this(morphLoc);
		_curve = curve;
		_time = time;
		this.positionAlongCurve(curve, time);
		setRenderOption(renderOption);
	}
	
	public MorphMLNeuronMorphology(URL morphLoc, Curve3D curve, float time, 
			String renderOption, CoordinateSystem c) {
		this(morphLoc, curve, time, renderOption);
		this.setCoordinateSystem(c);
	}

	public Level3Cell getMorphMLCell() {
		return theCell;
	}
	
	public ISegment getMorphMLSegment(Segment seg) {
		if (this.segmentMap.get(seg) == null) {
			MorphMLSegmentImpl ms = new MorphMLSegmentImpl(this, seg);
			this.segmentMap.put(seg, ms);
			return ms;
		}
		return this.segmentMap.get(seg);
	}
	
	/**
	 * Get the URL for the MorphML file that corresponds to this INeuronMorphology
	 * @return - the URL
	 */
	public URL getMorphMLURL() {
		return _morphLoc;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getSegments()
	 */
	/*
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
	}*/
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getSegments()
	 */
	public List<ISegment> getSegments() {
		List<ISegment> segmentList = new ArrayList<ISegment>();

		for (Object o : theCell.getSegments()) {
			SegmentsType s = (SegmentsType)o;
			for (Object ob : s.getSegment()) {
				Segment seg = (Segment)ob;
				segmentList.add(getMorphMLSegment(seg));
			}
		}
		return segmentList;
	}
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getSegments()
	 */
	protected List<ISegment> getMorphMLSegmentsForCableId(BigInteger id) {
		List<ISegment> segmentList = new ArrayList<ISegment>();

		for (Object o : theCell.getSegments()) {
			SegmentsType s = (SegmentsType)o;
			for (Object ob : s.getSegment()) {
				Segment seg = (Segment)ob;
				if (seg.getCable().equals(id)) {
					segmentList.add(getMorphMLSegment(seg));
				}
			}
		}
		return segmentList;
	}
	
	protected Segment getSegmentFromId(BigInteger id) {
		List<SegmentsType> segments  = theCell.getSegments();
		for (SegmentsType s : segments) {
			for (Object o : s.getSegment()) {
				Segment seg = (Segment)o;
				if (seg.getId().equals(id)) {
					return seg;
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getSegmentGroups()
	 */
	public Set<ICable> getSegmentGroups() {
		if (segmentGroupList == null) {
			segmentGroupList = new HashSet<ICable>();
			CablesType c = theCell.getCables();
			for(Object o: c.getCable()) {
				Cable cab = (Cable)o;
				BigInteger id = cab.getId();
				ArrayList<ISegment> childSegments = new ArrayList<ISegment>();
				for (ISegment s : this.getSegments()) {
					if (id.equals(s.getSegmentGroupId())) {
						childSegments.add(s);
					}
				}
				ICable segGroup = new MorphMLCableImpl(this, cab);
				segmentGroupList.add(segGroup);
				/* Hackish way to extract some info from the MorphML Files I happen to have
				 * This needs to be generalized
				 */
				for (Object ob : cab.getGroup()) {
					String s = (String)ob;
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
			}
		}
		return segmentGroupList;
	}
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology#getName()
	 */
	public String getName() {
		int len = _morphLoc.getFile().length();
		return _morphLoc.getFile().substring(len-14, len);
	}

}
