package edu.ucsd.ccdb.ontomorph2.view;

import java.util.ArrayList;
import java.util.Set;

import edu.ucsd.ccdb.ontomorph2.core.ICell;
import edu.ucsd.ccdb.ontomorph2.core.ISlide;

/**
 * @$comment Stands in for the Root Node of the 3D Scene Graph
 */

public interface IView3D {
	public void setSlides(ArrayList<ISlide> slides);
	public void setCells(Set<ICell> cells);
}
