package edu.ucsd.ccdb.ontomorph2.core;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.ucsd.ccdb.ontomorph2.util.OMTException;

import neuroml.generated.Level2Cell;
import neuroml.generated.NeuroMLLevel2;
import neuroml.generated.Point;
import neuroml.generated.Segment;
import neuroml.generated.Cell.Cables;
import neuroml.generated.Cell.Segments;
import neuroml.generated.NeuroMLLevel2.Cells;

public class MorphologyImpl implements IMorphology  {
	
	URL _morphLoc = null;
	IPosition _position = null;
	IRotation _rotation = null;
	float _scale = 1F;
	String _renderOption = RENDER_AS_LINES; //default render option
	ArrayList<ISegment> segmentList = null;
	ArrayList<ISegment> selectedSegmentList = new ArrayList<ISegment>();
	Level2Cell theCell;
	ArrayList<ISegmentGroup> segmentGroupList = null;
	
	public MorphologyImpl(URL morphLoc, IPosition position, IRotation rotation) {
		_morphLoc = morphLoc;
		_position = position;
		_rotation = rotation;
		
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
	}
	
	public MorphologyImpl(URL morphLoc, IPosition position, IRotation rotation, String renderOption) {
		this(morphLoc, position, rotation);
		setRenderOption(renderOption);
	}

	public URL getMorphMLURL() {
		return _morphLoc;
	}

	public IRotation getRotation() {
		return _rotation;
	}

	public IPosition getPosition() {
		return _position;
	}
	
	public String getRenderOption() {
		return _renderOption;
	}
	
	public void setRenderOption(String renderOption) {
		if (IMorphology.RENDER_AS_LINES.equals(renderOption) || IMorphology.RENDER_AS_CYLINDERS.equals(renderOption)) {
			_renderOption = renderOption;
		}
	}
	
	public float getScale() {
		return _scale;
	}

	public void setPosition(IPosition pos) {
		_position = pos;
	}
	
	public void setRotation(IRotation rot) {
		_rotation = rot;
	}
	
	public void setScale(float f) {
		_scale = f;
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
					
					SegmentImpl si = new SegmentImpl(seg.getId(), prox, dist, 
							p1.getDiameter().floatValue(), p2.getDiameter().floatValue(), seg.getCable());
					segmentList.add(si);
				}
			}
		}
		return segmentList;
	}
	
	public List<ISegmentGroup> getSegmentGroups() {
		if (segmentGroupList == null) {
			segmentGroupList = new ArrayList<ISegmentGroup>();
			Cables c = theCell.getCables();
			for(neuroml.generated.Cable cab : c.getCable()) {
				BigInteger id = cab.getId();
				ArrayList<ISegment> childSegments = new ArrayList<ISegment>();
				for (ISegment s : this.getSegments()) {
					if (id.equals(s.getSegmentGroupId())) {
						childSegments.add(s);
					}
				}
				segmentGroupList.add(new SegmentGroupImpl(id, childSegments, cab.getGroup()));
			}
		}
		return segmentGroupList;
	}
	
	public void selectSegment(ISegment s) {
		selectedSegmentList.add(s);
	}
	
	public void unselectSegment(ISegment s) {
		selectedSegmentList.remove(s);
	}
	
	public List<ISegment> getSelectedSegments() {
		return selectedSegmentList;
	}
	
}
