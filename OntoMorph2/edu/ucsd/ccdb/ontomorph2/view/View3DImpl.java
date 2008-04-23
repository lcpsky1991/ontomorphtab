package edu.ucsd.ccdb.ontomorph2.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.CurveImpl;
import edu.ucsd.ccdb.ontomorph2.core.ICell;
import edu.ucsd.ccdb.ontomorph2.core.ICurve;
import edu.ucsd.ccdb.ontomorph2.core.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.IRotation;
import edu.ucsd.ccdb.ontomorph2.core.ISlide;
import edu.ucsd.ccdb.ontomorph2.core.ISurface;
import edu.ucsd.ccdb.ontomorph2.core.SurfaceImpl;

public class View3DImpl extends Node implements IView3D {
	
	private Node slidesNode = null;
	private Node cellsNode = null;
	private Node curvesNode = null;
	private Node surfacesNode = null;
	private Set<IStructure3D> cells = null;
	
	public View3DImpl() {
		slidesNode = new Node();
		cellsNode = new Node();
		curvesNode = new Node();
		surfacesNode = new Node();
		cells = new HashSet<IStructure3D>();
		this.attachChild(slidesNode);
		this.attachChild(cellsNode);
		this.attachChild(curvesNode);
		this.attachChild(surfacesNode);
	}
	
	public void setSlides(ArrayList<ISlide> slides) {
		slidesNode.detachAllChildren();
		for(ISlide slide : slides){
			URL imageURL = slide.getImageURL();
			IPosition position = slide.getPosition();
			IRotation rotation = slide.getRotation();
			slidesNode.attachChild(new SlideViewImpl(imageURL, position, rotation));
		}
	}
	
	public void setCells(Set<ICell> cells) {
		cellsNode.detachAllChildren();
		for(ICell cell : cells) {
			IStructure3D cellView = new Structure3DImpl(cell.getMorphology());
			cellsNode.attachChild(cellView.getNode());
			this.cells.add(cellView);
		}
	}

	public void setCurves(Set<ICurve> curves) {
		curvesNode.detachAllChildren();
		for(ICurve curve : curves) {
			curvesNode.attachChild((CurveImpl)curve);
		}
		
	}

	public void setSurfaces(Set<ISurface> surfaces) {
		surfacesNode.detachAllChildren();
		for(ISurface surf : surfaces) {
			surfacesNode.attachChild((SurfaceImpl)surf);
		}
	}

	public Set<IStructure3D> getCells() {
		return this.cells;
	}

	

}
