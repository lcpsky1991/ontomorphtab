package edu.ucsd.ccdb.OntoMorphTab;

import java.awt.Component;
import java.net.URL;
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
	private static final long serialVersionUID = 4598424440166134279L;


	//	Global information and labeling
	final String URI = "http://ccdb.ucsd.edu/SAO/ontomorph/1.0/"; //http://ccdb.ucsd.edu/SAO/ontomorph/1.0/
	final String title = "Onto Morph Tab v0.1";
	final String idPrefix = "omt";
	String namespacePrefix;	//hopefully the same as idPrefix //TODO: make it the same

	final String pURL = idPrefix + "_url";	//String label for URL property
	final String pMethod = idPrefix + "_me"; //String label for method property
	final String pn1 = idPrefix + "_n1"; //String label for point 1
	final String pn2 = idPrefix + "_n2"; //String label for point 2
	final String pReady = idPrefix; //String label for omt-flag ready

    ResourcePanel resourcePanel;

    AssertedInstancesListOntoMorphPanel assertedInstancesListPanel;
    AssertedTypesListPanel typesListPanel;

    neuronEditorPanel neuronPanel=null;	//this needs to be global to ontomorph tab so the different panels can access other elements

    OWLModel ontoModel = null;

    public void initialize() {

    		ontoModel = (OWLModel)getKnowledgeBase();	//Get hte owl Model because this is used alot
    		namespacePrefix = findPrefix();				//get the prefix, useful for finding properties

    		System.out.println("*** Prefix set to: '" + namespacePrefix + "'");

        initializeTabLabel();

        JSplitPane mainSplitPane = createMainSplitPane();
        add(mainSplitPane);





    }


    //initialize & return the cvapp GUI
    //This has been changed, it used to create  'new' neuron editor panel and return it
    private neuronEditorPanel makeNeuronEditorPanel() {

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
        neuronPanel = makeNeuronEditorPanel(); //the panel needs to be intialized from null delcaration
        mainSplitPane.setLeftComponent(neuronPanel);
        //set the right component to an instances panel with a splitter
        mainSplitPane.setRightComponent(createInstanceSplitter());
        mainSplitPane.setDividerLocation(250); //ca: 250 is correct val
        return mainSplitPane;
    }

    //take care of the label that appears on the tab itself
    private void initializeTabLabel() {
        setLabel(title);
        setIcon(Icons.getInstanceIcon());
    }


    private JComponent createInstanceSplitter() {
        JSplitPane pane = createLeftRightSplitPane("InstancesTab.right.left_right", 250);
        pane.setLeftComponent(createInstancesPanel());
        resourcePanel = ProtegeUI.getResourcePanelFactory().createResourcePanel(ontoModel, ResourcePanel.DEFAULT_TYPE_INDIVIDUAL);
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
        AssertedInstancesListOntoMorphPanel aiPanel = new AssertedInstancesListOntoMorphPanel(ontoModel, this);
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
        typesListPanel = new AssertedTypesListPanel(ontoModel);
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

    public String findPrefix()
    {
    		//Need to know what the prefix is for ontology so its easier to locate properties
		Collection props = ontoModel.getRDFProperties();
		Iterator i = props.iterator();

		i = props.iterator();
		while ( i.hasNext() )
		{
			RDFProperty data = (RDFProperty) i.next();
			if ( URI.equals(data.getNamespace()) )
			{
				return data.getNamespacePrefix();
			}
		}
		return "[" + idPrefix + " error]";
    }

    public void selectNeuro(RDFResource resItem)
    {
    		String loc="none";
    		int method=-1;	//the selection method, default -1
    		int a=1;	//first point
    		int b=3;	//second point
    		Object val; //used fo temporary store of property value
    		//TODO: read the values

    		try
    		{
    			//Attempt to get the property values, false parameter avoids subproperties
    			//casting an Integer from a null object will throw an exception, so check for null objects
    			val = resItem.getPropertyValue(ontoModel.getRDFProperty(namespacePrefix + ":" + pURL), false);
    			if (val != null) loc = (String) val;
    			val = resItem.getPropertyValue(ontoModel.getRDFProperty(namespacePrefix + ":" + pn1), false);
    			if (val != null) a = (Integer) val;
	    		val = resItem.getPropertyValue(ontoModel.getRDFProperty(namespacePrefix + ":" + pn2), false);
	    		if (val != null) b = (Integer) val;
	    		val = resItem.getPropertyValue(ontoModel.getRDFProperty(namespacePrefix + ":" + pMethod), false);
	    		if (val != null) method = (Integer) val;
    		}
    		catch (Exception e)
    		{
    			System.err.println("*** Error selecting " + resItem.getName() + " (Properties do not exist, has OntoMorphTab ontology been imported? " + resItem.getNamespace() + ") Message[" + e.getMessage() + "] ");
    		}

       	//will only make a selection if there is a valid entry for the method
    		//I am betting that if the method is valid then the point data is valid and -1 is an invalid method
    		if (method > -1)
    		{
    			//If the current image is not the proper image, then download the correct image, change images and then select
    			if ( !neuronPanel.getURL().equalsIgnoreCase(loc) )
    			{
    				System.out.println("*** Changing neurolucida image");
    				//The following is modified version of setDataFromURL(String surl) of neuronEditorPanel.java
    				URL u = null;
    				try
    				{
    					u = new URL(loc);
    					String[] sdat = neuronPanel.readStringArrayFromURL(u);
        				neuronPanel.setCell(sdat, u.getHost(), u.getFile()); //setCell(sdat, hostroot, surl)
        				neuronPanel.setURL(loc);		//since we changed images, it is approipriate to update the URL
    				}
    				catch (Exception e)
    				{
    					System.out.println("*** The URL supplied ('" + loc + "') was malformed");
    				}
    			}

    			//Make the selection on the current image
    			neuronPanel.makeSelection(method, a, b);
    			System.out.println("*** Making Selection: [m=" + method + ", a=" + a + ", b=" + b + "] via selectNeuro()");
    		}
    		else
    		{
    			neuronPanel.setNormal();	//deselect any possible selection
    			//System.out.println("*** De-Selection via selectNeuro()");
    		}
    	}

    public int[] getSelectedNodes() //returns the indices of the key nodes from neuronEditorPanel
    {
    		return	neuronPanel.getCanvas().getSelection();
    }




}



