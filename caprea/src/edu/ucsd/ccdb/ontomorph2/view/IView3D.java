package edu.ucsd.ccdb.ontomorph2.view;

import java.util.List;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.scene.ICurve;
import edu.ucsd.ccdb.ontomorph2.core.scene.IMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISlide;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISurface;
import edu.ucsd.ccdb.ontomorph2.core.scene.IVolume;
import edu.ucsd.ccdb.ontomorph2.view.scene.INeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.VolumeViewImpl;

/**
 * Stands in for the Root Node of the 3D Scene Graph
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public interface IView3D {
	public void setSlides(List<ISlide> slides);
	public void setCells(Set<INeuronMorphology> cells);
	public Set<INeuronMorphologyView> getCells();
	public Set<VolumeViewImpl> getVolumes();
	public void setCurves(Set<ICurve> curves);
	public void setSurfaces(Set<ISurface> surfaces);
	public void setMeshes(Set<IMesh> meshes);
	public void setVolumes(Set<IVolume> volumes);
	public void displayBrainRegion(BrainRegion br);
	public void unDisplayBrainRegion(BrainRegion br);
}
