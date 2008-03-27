package edu.ucsd.ccdb.ontomorph2.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.ICell;
import edu.ucsd.ccdb.ontomorph2.core.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.IRotation;
import edu.ucsd.ccdb.ontomorph2.core.ISlide;

public class View3DImpl extends Node implements IView3D {
	
	private Node slidesNode = null;
	private Node cellsNode = null;
	
	public View3DImpl() {
		slidesNode = new Node();
		cellsNode = new Node();
		this.attachChild(slidesNode);
		this.attachChild(cellsNode);
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
			cellsNode.attachChild(new Structure3DImpl(cell.getMorphology()).getNode());
		}
	}

	

}
