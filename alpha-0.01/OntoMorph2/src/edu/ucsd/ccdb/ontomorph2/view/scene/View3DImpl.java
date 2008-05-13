package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jme.scene.Node;

import edu.ucsd.ccdb.ontomorph2.core.scene.IMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISlide;
import edu.ucsd.ccdb.ontomorph2.core.scene.IVolume;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CurveImpl;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICurve;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ISurface;
import edu.ucsd.ccdb.ontomorph2.core.spatial.SurfaceImpl;

public class View3DImpl extends Node implements IView3D {
	
	private Node slidesNode = null;
	private Node cellsNode = null;
	private Node curvesNode = null;
	private Node surfacesNode = null;
	private Node meshesNode = null;
	private Node volumesNode = null;
	private Set<INeuronMorphologyView> cells = null;
	private Set<VolumeViewImpl> volumes = null;
	
	public View3DImpl() {
		slidesNode = new Node();
		cellsNode = new Node();
		curvesNode = new Node();
		surfacesNode = new Node();
		meshesNode = new Node();
		volumesNode = new Node();
		cells = new HashSet<INeuronMorphologyView>();
		volumes = new HashSet<VolumeViewImpl>();
		this.attachChild(slidesNode);
		this.attachChild(cellsNode);
		this.attachChild(curvesNode);
		this.attachChild(surfacesNode);
		this.attachChild(meshesNode);
		this.attachChild(volumesNode);
	}
	
	public void setSlides(List<ISlide> slides) {
		slidesNode.detachAllChildren();
		for(ISlide slide : slides){
			slidesNode.attachChild(new SlideViewImpl(slide.getImageURL(), 
					slide.getPosition(), slide.getRotation(), slide.getScale(), 
					slide.getRatio()));
		}
	}
	
	public void setCells(Set<INeuronMorphology> cells) {
		cellsNode.detachAllChildren();
		for(INeuronMorphology cell : cells) {
			INeuronMorphologyView cellView = new NeuronMorphologyViewImpl(cell);
			Node n = cellView.getNode();
			cellsNode.attachChild(n);
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

	public Set<INeuronMorphologyView> getCells() {
		return this.cells;
	}

	public void setMeshes(Set<IMesh> meshes) {
		meshesNode.detachAllChildren();
		for(IMesh mesh : meshes) {
			MeshViewImpl meshView = new MeshViewImpl(mesh);
			meshesNode.attachChild(meshView.getNode());
		}
	}

	public void setVolumes(Set<IVolume> volumes) {
		
		volumesNode.detachAllChildren();
		for (IVolume vol : volumes) {
			VolumeViewImpl volView = new VolumeViewImpl(vol);
			this.volumes.add(volView);
			volumesNode.attachChild(volView.getNode());
		}
	}
	
	public Set<VolumeViewImpl> getVolumes() {
		return volumes;
	}
	
}
