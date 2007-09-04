package edu.ucsd.ccdb.OntoMorphTab;

import java.awt.Component;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;

import edu.duke.neuron.cells.cvapp.neuronEditorPanel;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.PopupMenuMouseListener;
import edu.stanford.smi.protege.util.Selectable;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.individuals.AssertedTypesListPanel;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourcePanel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protege.widget.AbstractTabWidget;

// an example tab
public class OntoMorphTab extends AbstractTabWidget {
    /**
	 *
	 */
	private static final long serialVersionUID = 4598424440166134279L;

    ResourcePanel resourcePanel;

    AssertedInstancesListOntoMorphPanel assertedInstancesListPanel;
    AssertedTypesListPanel typesListPanel;

    neuronEditorPanel neuronPanel=null;	//this needs to be global to ontomorph tab so the different panels can access other elements


    public void initialize() {
        initializeTabLabel();

        JSplitPane mainSplitPane = createMainSplitPane();
        add(mainSplitPane);


    }

    //initialize & return the cvapp GUI
    //This has been changed, it used to create  'new' neuron editor panel and return it
    private neuronEditorPanel getNeuronEditorPanel() {

    		//CA Maybe this is the start location of the data? - NOPE
        int w = 300; //300
        int h = 250; //250

        neuronEditorPanel neupan = new neuronEditorPanel(w, h, getFont());


        return neupan;
    }

    //split the tab real estate with a vertical line


    protected JSplitPane createMainSplitPane() {
        JSplitPane mainSplitPane = ComponentFactory.createLeftRightSplitPane();
        //set the left component to the cvapp GUI
        neuronPanel = getNeuronEditorPanel(); //the panel needs to be intialized from null delcaration
        mainSplitPane.setLeftComponent(neuronPanel);
        //set the right component to an instances panel with a splitter
        mainSplitPane.setRightComponent(createInstanceSplitter());
        mainSplitPane.setDividerLocation(250); //ca: 250 is correct val
        return mainSplitPane;
    }

    //take care of the label that appears on the tab itself
    private void initializeTabLabel() {
        setLabel("Onto Morph Tab CA");
        setIcon(Icons.getInstanceIcon());
    }


    private JComponent createInstanceSplitter() {
        JSplitPane pane = createLeftRightSplitPane("InstancesTab.right.left_right", 250);
        pane.setLeftComponent(createInstancesPanel());
        resourcePanel = ProtegeUI.getResourcePanelFactory().createResourcePanel((OWLModel)getKnowledgeBase(), ResourcePanel.DEFAULT_TYPE_INDIVIDUAL);
        pane.setRightComponent((Component) resourcePanel);
        return pane;
    }

    private JComponent createInstancesPanel() {
        JSplitPane panel = ComponentFactory.createTopBottomSplitPane();
        assertedInstancesListPanel = createAssertedInstancesListPanel();
        panel.setTopComponent(assertedInstancesListPanel);
        panel.setBottomComponent(createDirectTypesList());
        return panel;
    }

    protected AssertedInstancesListOntoMorphPanel createAssertedInstancesListPanel()
    {
        AssertedInstancesListOntoMorphPanel aiPanel = new AssertedInstancesListOntoMorphPanel((OWLModel)getKnowledgeBase(), this);
        aiPanel.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                Collection selection = assertedInstancesListPanel.getSelection();
                Instance selectedInstance;
                if (selection.size() == 1) {
                    selectedInstance = (Instance) CollectionUtilities.getFirstItem(selection);

                }
                else {
                    selectedInstance = null;
                }

                //CA: at this point the selectedInstance should be null (for multiple) or an Instance object
                if(selectedInstance == null || selectedInstance instanceof RDFResource) {
                    RDFResource resource = (RDFResource) selectedInstance;
                    resourcePanel.setResource(resource);
                    typesListPanel.setResource(resource);
                }
                else if(resourcePanel instanceof ResourceDisplay) {  // legacy only
                    ((ResourceDisplay)resourcePanel).setInstance(selectedInstance);
                    typesListPanel.setResource(null);
                }

                //This is where the code for setting the cvapp display will go
                //Only runs if user has selected ONE item and that item isn't empty
                if (selectedInstance != null && selectedInstance instanceof RDFResource)
                	{
                		selectNeuro((RDFResource) selectedInstance);
                	}
            }
        });
        setInstanceSelectable((Selectable) aiPanel.getDragComponent());
        final JList list = (JList) aiPanel.getDragComponent();
        list.addMouseListener(new PopupMenuMouseListener(list) {
            protected JPopupMenu getPopupMenu() {
                Instance instance = (Instance) list.getSelectedValue();
                if (instance instanceof RDFResource) {
                    JPopupMenu menu = new JPopupMenu();
                    ResourceActionManager.addResourceActions(menu, list, (RDFResource) instance);
                    if (menu.getComponentCount() > 0) {
                        return menu;
                    }
                }
                return null;
            }




            protected void setSelection(JComponent c, int x, int y) {
                for (int i = 0; i < list.getModel().getSize(); i++) {
                    if (list.getCellBounds(i, i).contains(x, y)) {
                        list.setSelectedIndex(i);
                        return;
                    }
                }
                list.setSelectedIndex(-1);
            }
        });
        return aiPanel;
    }

    protected JComponent createDirectTypesList() {
        typesListPanel = new AssertedTypesListPanel((OWLModel)getKnowledgeBase());
        return typesListPanel;
    }

    @SuppressWarnings("unchecked")
	public static boolean isSuitable(Project project, Collection errors) {
    	return true;
    }

    // this method is useful for debugging
    public static void main(String[] args) {
        edu.stanford.smi.protege.Application.main(args);
    }

    public void selectNeuro(RDFResource resItem)
    {
    		Collection notes = resItem.getComments();

    		Object[] objNotes = resItem.getComments().toArray();
    		String strOne="";	//any one line of comment
    		String begin="";		//prefix of string
    		String number="";	//the number in string

    		int method=-1;	//the selection method, default -1
    		int a=1;	//first point
    		int b=3;	//second point

    		//parse through the notes and find all the relevant information
    		for (int i=0; i < objNotes.length; i++)
    		{
    			strOne = objNotes[i].toString();
    			begin = strOne.substring(0,6);		//for conveiniance of equals() method
    			number = strOne.substring(8); 		//remove all the prefix string data
    			//First point
    			if ( "omt_n1".equals(begin) ) 			//Can't use the == operator here, returns false even if true
    			{
    				a = Integer.parseInt(number);
    			}
    			//Second point
    			else if ("omt_n2".equals(begin))
    			{
    				b = Integer.parseInt(number);
    			}
    			else if ("omt_me".equals(begin))
    			{
    				method = Integer.parseInt(number);
    			}
    		}

    		//will only make a selection if there is a valid entry for the method
    		//I am betting that if the method is valid then the point data is valid and -1 is an invalid method
    		if (method > -1)
    		{
    			neuronPanel.makeSelection(method, a, b);
    			System.out.println("*** Making Selection: [m=" + method + ", a=" + a + ", b=" + b + "] via selectNeuro()");
    		}
    		else
    		{
    			neuronPanel.setNormal();	//deselect any possible selection
    			System.out.println("*** De-Selection via selectNeuro()");
    		}
    	}

    public int[] getSelectedNodes() //returns the indices of the key nodes from neuronEditorPanel
    {
    		return	neuronPanel.getCanvas().getSelection();
    }
}



