package edu.ucsd.ccdb.OntoMorphTab;

import java.awt.Component;
import java.util.Collection;

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

    public void initialize() {
        initializeTabLabel();
        
        JSplitPane mainSplitPane = createMainSplitPane();
        add(mainSplitPane);
        
        
    }

    //initialize & return the cvapp GUI
    private neuronEditorPanel getNeuronEditorPanel() {
        
        int w = 300;
        int h = 250;

        neuronEditorPanel neupan = new neuronEditorPanel(w, h, getFont());
        
        return neupan;
    }
    
    //split the tab real estate with a vertical line


    protected JSplitPane createMainSplitPane() {
        JSplitPane mainSplitPane = ComponentFactory.createLeftRightSplitPane();
        //set the left component to the cvapp GUI
        mainSplitPane.setLeftComponent(getNeuronEditorPanel());
        //set the right component to an instances panel with a splitter
        mainSplitPane.setRightComponent(createInstanceSplitter());
        mainSplitPane.setDividerLocation(250);
        return mainSplitPane;
    }
    
    //take care of the label that appears on the tab itself
    private void initializeTabLabel() {
        setLabel("OntoMorphTab");
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
    
    protected AssertedInstancesListOntoMorphPanel createAssertedInstancesListPanel() {
        AssertedInstancesListOntoMorphPanel result = new AssertedInstancesListOntoMorphPanel((OWLModel)getKnowledgeBase());
        result.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                Collection selection = assertedInstancesListPanel.getSelection();
                Instance selectedInstance;
                if (selection.size() == 1) {
                    selectedInstance = (Instance) CollectionUtilities.getFirstItem(selection);
                }
                else {
                    selectedInstance = null;
                }
                if(selectedInstance == null || selectedInstance instanceof RDFResource) {
                    RDFResource resource = (RDFResource) selectedInstance;
                    resourcePanel.setResource(resource);
                    typesListPanel.setResource(resource);
                }
                else if(resourcePanel instanceof ResourceDisplay) {  // legacy only
                    ((ResourceDisplay)resourcePanel).setInstance(selectedInstance);
                    typesListPanel.setResource(null);
                }
            }
        });
        setInstanceSelectable((Selectable) result.getDragComponent());
        final JList list = (JList) result.getDragComponent();
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
        return result;
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
}
