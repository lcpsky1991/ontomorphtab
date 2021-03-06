package edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import neurolucida.generated.NeurolucidaData;
import neurolucida.generated.Point;
import neurolucida.generated.Tree;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationQuat;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * Describes the morphology of the cell, loaded by a Neurolucida XML file
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see NeuronMorphology
 */
public class NeurolucidaNeuronMorphology extends NeuronMorphology{
	
	URL _morphLoc = null;
	List<Tree> treeList = null;
	ArrayList<INeuronMorphologyPart> segmentList = null;
	
	public NeurolucidaNeuronMorphology(URL morphLoc) {
		super(morphLoc.toString());
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
	}
	
	public String getFilename()
	{
		return _morphLoc.getFile();
	}
	
	public NeurolucidaNeuronMorphology(URL morphLoc, PositionVector position, RotationQuat rotation) {
		this(morphLoc);
		setPosition(position);
		setRotation(rotation);
	}
	
	
	public NeurolucidaNeuronMorphology(URL morphLoc, PositionVector position, 
			RotationQuat rotation, String renderOption) {
		this(morphLoc, position, rotation);
		setRenderOption(renderOption);
	}
	
	
	public NeurolucidaNeuronMorphology(URL morphLoc, Curve3D curve, float time, String renderOption) {
		this(morphLoc);
		setCurve(curve);
		setTime(time);
		this.positionAlongCurve(curve, time);
		setRenderOption(renderOption);
	}
	
	/**
	 * Get the URL for the MorphML file that corresponds to this INeuronMorphology
	 * @return - the URL
	 */
	public URL getNeurolucidaXmlURL() {
		return _morphLoc;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getSegments()
	 */
	public List<INeuronMorphologyPart> getSegments() {
		if (segmentList == null) {
			segmentList = new ArrayList<INeuronMorphologyPart>();
			
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
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getSegmentGroups()
	 */
	//public Set<ICable> getCables() {
		//if (segmentGroupList == null) {
		//	segmentGroupList = new HashSet<ICable>();
			/*
			Cables c = theCell.getCables();
			for(neuroml.generated.Cable cab : c.getCable()) {
				BigInteger id = cab.getId();
				ArrayList<INeuronMorphologyPart> childSegments = new ArrayList<INeuronMorphologyPart>();
				for (INeuronMorphologyPart s : this.getSegments()) {
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
						segGroup.addSemanticThing(GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao1211023249"));
					}
					if ("soma_group".equals(s)) {
						segGroup.addSemanticThing(GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao1044911821"));
					} 
					if ("axon_group".equals(s)) {
						segGroup.addSemanticThing(GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao1770195789"));
						//should be adding all these segGroups to the Axon class and treating them as a separate unit.
					}
					if ("apical_dendrite".equals(s)) {
						segGroup.addSemanticThing(GlobalSemanticRepository.getInstance().getSemanticClass("sao:sao273773228"));
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
		//}
		//return segmentGroupList;
	//}
	
	/* (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology#getName()
	 */
	public String getName() {
		int len = _morphLoc.getFile().length();
		return _morphLoc.getFile().substring(len-14, len);
	}

	@Override
	public int getCableCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public NeuronCable getCable(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NeuronCable getCable(BigInteger id) {
		// TODO Auto-generated method stub
		return null;
	}

}
