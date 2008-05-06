package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.spatial.ICurve;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ISurface;

/**
 * Defines the totality of the objects that can be viewed in the 3D world
 * 
 * @author stephen
 *
 */
public interface IScene {

				/**
	 * @associates edu.ucsd.ccdb.ontomorph2.core.ICell
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.Set lnkICell = null;
			/**
	 * @link aggregation 
	 * @associates edu.ucsd.ccdb.ontomorph2.core.misc.IPopulation
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.Set lnkIPopulation = null;
		/**
	 * @link aggregation
	 * @associates edu.ucsd.ccdb.ontomorph2.core.scene.ISlide
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.ArrayList lnkISlide = null;

	void load();

	void save();

	List<ISlide> getSlides();

	Set<INeuronMorphology> getCells();

	Set<ICurve> getCurves();

	Set<ISurface> getSurfaces();

	Set<IMesh> getMeshes();

	Set<IVolume> getVolumes();
}
