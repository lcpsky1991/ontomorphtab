package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.renderer.Renderer;

/**
 * @$comment Defines the view of the entire application.  Is associated with the 3D parts of the view and the 2D parts of view.
 * @see edu.ucsd.ccdb.ontomorph2.view.scene.IView3D
 * @see edu.ucsd.ccdb.ontomorph2.view.scene.IView2D
 */

public interface IView {

		public  IView3D getView3D();
		public  IView2D getView2D();
		
		public Renderer getRenderer();
}
