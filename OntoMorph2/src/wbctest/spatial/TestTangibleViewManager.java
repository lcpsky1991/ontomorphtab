package wbctest.spatial;

import org.morphml.neuroml.schema.XWBCSlide;

import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.scene.SlideView;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;
import junit.framework.TestCase;

public class TestTangibleViewManager extends TestCase {

	/*
	 * Test method for 'edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager.getTangibleViewFor(Tangible)'
	 */
	public void testGetTangibleViewFor() {
		XWBCSlide xwbcslide = (XWBCSlide)DataRepository.getInstance().loadTangible(Slide.class, "hippocampus 2");
		Slide slide = new Slide(xwbcslide);
		SlideView sv = new SlideView(slide);
		
		TangibleView tv = TangibleViewManager.getInstance().getTangibleViewFor(slide);
		
		assertNotNull(tv);
	}

}
