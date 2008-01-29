package edu.ucsd.ccdb.ontomorph2.core;

import java.util.ArrayList;
import java.util.Set;

public interface IScene {

				/**
	 * @associates edu.ucsd.ccdb.ontomorph2.core.ICell
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.Set lnkICell = null;
			/**
	 * @link aggregation 
	 * @associates edu.ucsd.ccdb.ontomorph2.core.IPopulation
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.Set lnkIPopulation = null;
		/**
	 * @link aggregation
	 * @associates edu.ucsd.ccdb.ontomorph2.core.ISlide
	 * @directed directed
	 * @supplierCardinality 0..*
	 */
	java.util.ArrayList lnkISlide = null;

	void load();

	void save();

	ArrayList<ISlide> getSlides();

	Set<ICell> getCells();
}