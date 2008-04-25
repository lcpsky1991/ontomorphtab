package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
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
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

public class NeuronMorphologyImpl extends SceneObjectImpl implements INeuronMorphology, ISemanticsAware, ISelectable  {
	
	URL _morphLoc = null;
	String _renderOption = RENDER_AS_LINES; //default render option
	ArrayList<ISegment> segmentList = null;
	Set<ISegment> selectedSegmentList = new HashSet<ISegment>();
	Set<ISegmentGroup> selectedSegmentGroupList = new HashSet<ISegmentGroup>();
	Level2Cell theCell;
	Set<ISegmentGroup> segmentGroupList = null;
	boolean selected = false;
	List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();
	
	public NeuronMorphologyImpl(URL morphLoc, IPosition position, IRotation rotation) {
		_morphLoc = morphLoc;
		setPosition(position);
		setRotation(rotation);
		
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
	
	public NeuronMorphologyImpl(URL morphLoc, IPosition position, IRotation rotation, String renderOption) {
		this(morphLoc, position, rotation);
		setRenderOption(renderOption);
	}

	public Level2Cell getMorphMLCell() {
		return theCell;
	}
	
	public URL getMorphMLURL() {
		return _morphLoc;
	}
	
	public String getRenderOption() {
		return _renderOption;
	}
	
	public void setRenderOption(String renderOption) {
		if (INeuronMorphology.RENDER_AS_LINES.equals(renderOption) || 
				INeuronMorphology.RENDER_AS_CYLINDERS.equals(renderOption) ||
				INeuronMorphology.RENDER_AS_LOD.equals(renderOption) ||
				INeuronMorphology.RENDER_AS_LOD_2.equals(renderOption)) {
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
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("sao:sao1211023249"));
					}
					if ("soma_group".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("sao:sao1044911821"));
					} 
					if ("axon_group".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("sao:sao1770195789"));
					}
					if ("apical_dendrite".equals(s)) {
						segGroup.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("sao:sao273773228"));
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
	
	public void selectSegment(ISegment s) {
		selectedSegmentList.add(s);
		changed();
	}
	
	public void unselectSegment(ISegment s) {
		selectedSegmentList.remove(s);
		changed();
	}
	
	public Set<ISegment> getSelectedSegments() {
		return selectedSegmentList;
	}

	public void select() {
		this.selected = true;		
		changed();
	}

	public void selectSegmentGroup(ISegmentGroup g) {
		selectedSegmentGroupList.add(g);
		changed();
	}

	public void unselect() {
		this.selected = false;
		changed();
	}

	public void unselectSegmentGroup(ISegmentGroup g) {
		selectedSegmentGroupList.remove(g);
		changed();
	}

	public Set<ISegmentGroup> getSelectedSegmentGroups() {
		return selectedSegmentGroupList;
	}

	public boolean isSelected() {
		return this.selected;
	}


	public boolean hasSelectedSegmentGroups() {
		return getSelectedSegmentGroups().size() > 0;
	}

	public List<ISemanticThing> getSemanticThings() {
		return semanticThings;
	}

	public void addSemanticThing(ISemanticThing thing) {
		semanticThings.add(thing);
		
	}

	public void removeSemanticThing(ISemanticThing thing) {
		semanticThings.remove(thing);
	}
	
}
