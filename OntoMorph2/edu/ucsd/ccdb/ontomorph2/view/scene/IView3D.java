package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.scene.IMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISlide;
import edu.ucsd.ccdb.ontomorph2.core.scene.IVolume;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICurve;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ISurface;

/**
 * @$comment Stands in for the Root Node of the 3D Scene Graph
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
}
