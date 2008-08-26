package wbctest.view;

import com.acarter.scenemonitor.SceneMonitor;
import com.jme.app.SimpleGame;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;


public class TestNeuronMorphologyView extends SimpleGame{
	
	
	/**
	 * Entry point for the test,
	 * @param args
	 */
	public static void main(String[] args) {
		TestNeuronMorphologyView app = new TestNeuronMorphologyView();
		app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
		app.start();
	}
	
	/**
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
		display.setTitle("Test loading a neuron morphology");
		
		test1();
	}
	
	protected void test1() {
		NeuronMorphology neuron = new MorphMLNeuronMorphology("cell1zr");
		neuron.setRelativeScale(0.01f);
		neuron.setRenderOption(NeuronMorphology.RENDER_AS_DETAILED_BOXES);
		//neuron.setRenderOption(NeuronMorphology.RENDER_AS_LOD_2);
		
		NeuronMorphologyView nmv = new NeuronMorphologyView(neuron);
		rootNode.attachChild(nmv);
		
		/*
		neuron.setRenderOption(NeuronMorphology.RENDER_AS_LINES);
		NeuronMorphologyView nmv2 = new NeuronMorphologyView(neuron);
		rootNode.attachChild(nmv2);
		*/
		
		SceneMonitor.getMonitor().registerNode(rootNode, "Root Node");
		SceneMonitor.getMonitor().showViewer(true);
	}
	
	
	
	
	
}

	
