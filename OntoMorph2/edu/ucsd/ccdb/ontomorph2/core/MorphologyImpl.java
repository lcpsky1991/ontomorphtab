package edu.ucsd.ccdb.ontomorph2.core;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import neuroml.generated.Level2Cell;
import neuroml.generated.NeuroMLLevel2;
import neuroml.generated.Point;
import neuroml.generated.Segment;
import neuroml.generated.Cell.Segments;
import neuroml.generated.NeuroMLLevel2.Cells;

import org.w3c.dom.Document;

import edu.ucsd.ccdb.ontomorph2.util.XSLTransformManager;
import edu.ucsd.ccdb.ontomorph2.view.IStructure3D;
import edu.ucsd.ccdb.ontomorph2.view.Structure3DImpl;

public class MorphologyImpl implements IMorphology  {
	
	URL _morphLoc = null;
	IPosition _position = null;
	IRotation _rotation = null;
	float _scale = 1F;
	String _renderOption = RENDER_AS_LINES; //default render option
	ArrayList<ISegment> segmentList = null;
	ArrayList<ISegment> selectedSegmentList = new ArrayList<ISegment>();
	Level2Cell theCell;
	
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
			e.printStackTrace();
		}
	}
	
	public MorphologyImpl(URL morphLoc, IPosition position, IRotation rotation, String renderOption) {
		this(morphLoc, position, rotation);
		setRenderOption(renderOption);
	}

	public URL getMorphML() {
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

	public ArrayList<ISegment> getSegments() {
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
					
					SegmentImpl si = new SegmentImpl(seg.getId(), prox, dist, p1.getDiameter().floatValue(), p2.getDiameter().floatValue());
					segmentList.add(si);
				}
			}
		}
		return segmentList;
	}
	
	public void selectSegment(ISegment s) {
		selectedSegmentList.add(s);
	}
	
	public void unselectSegment(ISegment s) {
		selectedSegmentList.remove(s);
	}
	
	public ArrayList<ISegment> getSelectedSegments() {
		return selectedSegmentList;
	}
	
}
