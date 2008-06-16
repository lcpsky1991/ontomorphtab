package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.List;
import java.util.Set;


/**
 * Defines the totality of the objects that can be viewed in the 3D world
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public interface IScene {

	void load();

	void save();

	List<ISlide> getSlides();

	Set<INeuronMorphology> getCells();

	Set<ICurve> getCurves();

	Set<ISurface> getSurfaces();

	Set<IMesh> getMeshes();

	Set<IVolume> getVolumes();
}
