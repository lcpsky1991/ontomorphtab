package edu.ucsd.ccdb.OntoMorphTab;

import edu.stanford.smi.protege.action.DeleteInstancesAction;
import edu.stanford.smi.protege.action.MakeCopiesAction;
import edu.stanford.smi.protege.action.ReferencersAction;
import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.resource.Colors;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.ConfigureAction;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.DefaultSelectionDialogFactory;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.individuals.InstancesList;
import edu.stanford.smi.protegex.owl.ui.individuals.MultiSlotPanel;
import edu.stanford.smi.protegex.owl.ui.search.finder.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * The panel that holds the list of direct instances of one or more classes. If
 * only one class is chosen then you can also create new instances of this
 * class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class AssertedInstancesListOntoMorphPanel extends SelectableContainer implements Disposable {

    private Collection classes = Collections.EMPTY_LIST;

    private AllowableAction createAction;

    private AllowableAction createAnonymousAction;

    private AllowableAction assignNeuroSelection; //CA intended for the button

    private AllowableAction copyAction;

    private AllowableAction deleteAction;

    private AllowableAction developerCommand;

    private HeaderComponent header;

    private OWLLabeledComponent lc;

    private InstancesList list;

    private Collection listenedToInstances = new ArrayList();

    private OWLModel owlModel;

    private static final int SORT_LIMIT;

    private boolean showSubclassInstances = true;

    private OntoMorphTab oTab;

    static {
        SORT_LIMIT = ApplicationProperties.getIntegerProperty("ui.DirectInstancesList.sort_limit", 1000);
    }


    private ClsListener _clsListener = new ClsAdapter() {
        public void directInstanceAdded(ClsEvent event) {
            Instance instance = event.getInstance();
            if (!getModel().contains(instance)) {
                ComponentUtilities.addListValue(list, instance);
                instance.addFrameListener(_instanceFrameListener);
            }
        }


        public void directInstanceRemoved(ClsEvent event) {
            removeInstance(event.getInstance());
        }
    };

    private FrameListener _clsFrameListener = new FrameAdapter() {
        public void ownSlotValueChanged(FrameEvent event) {
            super.ownSlotValueChanged(event);
            updateButtons();
        }
    };

    private FrameListener _instanceFrameListener = new FrameAdapter() {
        public void browserTextChanged(FrameEvent event) {
            super.browserTextChanged(event);
            sort();
            repaint();
        }
    };




    public AssertedInstancesListOntoMorphPanel(OWLModel owlModel, OntoMorphTab oTab) {
        this.owlModel = owlModel;
        this.oTab = oTab;

        Action viewAction = createViewAction();

        list = new InstancesList(viewAction);

        lc = new OWLLabeledComponent(null, ComponentFactory.createScrollPane(list));
        addButtons(viewAction, lc);

        ResultsViewModelFind findAlg = new DefaultIndividualFind(owlModel, Find.CONTAINS) {
            protected boolean isValidFrameToSearch(Frame f) {
                return (((SimpleListModel) list.getModel()).getValues()).contains(f) &&
                       super.isValidFrameToSearch(f);
            }

            public String getDescription() {
                return "Find Individual Of Selected Class";
            }
        };
        FindAction fAction = new FindInDialogAction(findAlg,
                                                    Icons.getFindInstanceIcon(),
                                                    list, true);

        ResourceFinder finder = new ResourceFinder(fAction);
        lc.setFooterComponent(finder);

        lc.setBorder(ComponentUtilities.getAlignBorder());
        add(lc, BorderLayout.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createHeader(), BorderLayout.NORTH);
        add(panel, BorderLayout.NORTH);

        setSelectable(list);
        // initializeShowSubclassInstances();
        lc.setHeaderLabel("Asserted Instances");

    }


    private void updateLabel() {
        String text;
        Cls cls = getSoleAllowedCls();
        BrowserSlotPattern pattern = (cls == null) ? null : cls.getBrowserSlotPattern();
        if (pattern == null) {
            text = null;
        }
        else {
            // text = "Instances by ";
            if (pattern.isSimple()) {
                text = pattern.getFirstSlot().getBrowserText();
                if (Model.Slot.NAME.equals(text)) {
                    text = "Asserted Instances";
                }
            }
            else {
                text = "multiple properties";
            }
        }
        lc.setHeaderLabel(text);
    }


    private HeaderComponent createHeader() {
        JLabel label = ComponentFactory.createLabel();
        String instanceBrowserLabel = LocalizedText.getText(ResourceKey.INSTANCE_BROWSER_TITLE);
        String forClassLabel = LocalizedText.getText(ResourceKey.CLASS_EDITOR_FOR_CLASS_LABEL);
        header = new HeaderComponent(instanceBrowserLabel, forClassLabel, label);
        header.setColor(Colors.getInstanceColor());
        return header;
    }


    private void fixRenderer() {
        FrameRenderer frameRenderer = (FrameRenderer) list.getCellRenderer();
        frameRenderer.setDisplayType(showSubclassInstances);
    }


    protected void addButtons(Action viewAction, LabeledComponent c) {
        // c.addHeaderButton(createReferencersAction());

    		c.addHeaderButton(createDeveloperCommand());
        c.addHeaderButton(createAssignNeuroSelection());
        c.addHeaderButton(createCreateAction());
        c.addHeaderButton(createCopyAction());
        c.addHeaderButton(createDeleteAction());
        //c.addHeaderButton(createCreateAnonymousAction());
        c.addHeaderButton(createConfigureAction());
    }


    private void addClsListeners() {
        Iterator i = classes.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            cls.addClsListener(_clsListener);
            cls.addFrameListener(_clsFrameListener);
        }
    }


    private void addInstanceListeners() {
        ListModel model = list.getModel();
        int start = list.getFirstVisibleIndex();
        int stop = list.getLastVisibleIndex();
        for (int i = start; i < stop; ++i) {
            Instance instance = (Instance) model.getElementAt(i);
            addInstanceListener(instance);

        }
    }


    private void removeInstanceListeners() {
        Iterator i = listenedToInstances.iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            instance.removeFrameListener(_instanceFrameListener);
        }
        listenedToInstances.clear();
    }


    private void addInstanceListener(Instance instance) {
        instance.addFrameListener(_instanceFrameListener);
        listenedToInstances.add(instance);
    }


    protected Action createCreateAction() {
        createAction = new CreateAction("Create instance", OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_INDIVIDUAL)) {
            public void onCreate() {
            	//Every time you hit the create new instance button, pop up the class chooser


                	RDFResource newResource = (RDFResource) ProtegeUI.getSelectionDialogFactory().selectClass(AssertedInstancesListOntoMorphPanel.this, owlModel, "Select a named class to add");
                	if (newResource instanceof Cls)
                	{

                		/* How creation works:
						1. First, create a resource
						2. add resource to the 'classes' list
						3. refresh the display list based on the 'classes'
						4. create the instance in the owlModel
						5. set the selected on the display list value to match
                		*/

                		ArrayList type = new ArrayList();
                		type.add(newResource);

                		//** Not sure what this does, but deleteding it will criple the 'delete individual' button
                		ArrayList acsum = new ArrayList();
                		acsum.addAll(classes);
                		acsum.add(newResource);
                		removeClsListeners();
                		classes = acsum;
                		list.setClasses(acsum);
                		//****


                		Instance instance = owlModel.createInstance(null, type);
                		if (instance instanceof Cls) {
                			Cls newCls = (Cls) instance;
                			//if this is a top level class, make it at least be a subclass of owlThing
                			if (newCls.getDirectSuperclassCount() == 0) {
                				newCls.addDirectSuperclass(owlModel.getOWLThingClass());
                			}
                		}

                		//make it ready for markup
                		newResource = (RDFResource) instance;
                		assignImgSelection(newResource, "none", 0, 0, 0);
                		System.out.println("*** New OMT individual: " + newResource.getName());

                		//Reload the list
                		reload();
                		updateButtons();
                		addClsListeners();

                		list.setSelectedValue(instance, true);
                	}
            }
        };
        return createAction;
    }



    //This button has been removed because OMT coders don't know what an anonymous instance is, so we won't support it
    /*
    protected Action createCreateAnonymousAction() {
        createAnonymousAction = new CreateAction("Create anonymous instance", OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_ANON_INDIVIDUAL)) {
            public void onCreate() {
                if (!classes.isEmpty()) {
                	String name = owlModel.getNextAnonymousResourceName();
                    Instance instance = owlModel.createInstance(name, classes);
                    if (instance instanceof Cls) {
                        Cls newCls = (Cls) instance;
                        if (newCls.getDirectSuperclassCount() == 0) {
                            newCls.addDirectSuperclass(owlModel.getOWLThingClass());
                        }
                    }
                    list.setSelectedValue(instance, true);
                }
            }
        };
        return createAnonymousAction;
    }*/

    protected Action createDeveloperCommand()
    {
    		developerCommand = new CreateAction("Debug", OWLIcons.getNerdErrorIcon())
    		{
    			public void onCreate()
    			{
    				debug();
    			}
    		};
    		return developerCommand;
    }

    public void assignImgSelection(RDFResource resItem, String URL, int method, int n1, int n2)
    {

    		//Add data - NEEDS prefix
    		RDFProperty propReady, propURL, prop1, prop2, propMethod;

    		//Look up the property objects to be set
    		propURL = owlModel.getRDFProperty(oTab.namespacePrefix + ":" + oTab.pURL);
    		propReady = owlModel.getRDFProperty(oTab.namespacePrefix + ":" + oTab.pReady);
    		prop1 = owlModel.getRDFProperty(oTab.namespacePrefix + ":" + oTab.pn1);
    		prop2 = owlModel.getRDFProperty(oTab.namespacePrefix + ":" + oTab.pn2);
    		propMethod = owlModel.getRDFProperty(oTab.namespacePrefix + ":" + oTab.pMethod);

    		if ( propURL == null || propReady == null || prop1 == null || prop2 == null || propMethod == null)
    		{
    			System.out.println("*** Error: Could not assigning selection: one of the properties does not exist ontology may be missing " + resItem.getNamespace());
    		}
    		else
    		{
        		System.out.println("*** Adding properties to " + resItem.getName() + " " + URL + " m" + method + " p" + n1 + " p" + n2);

        		//If the resource doesn't have the properties, then add them, else set
        		if ( !resItem.hasPropertyValue(propURL) ) resItem.addPropertyValue(propURL, URL);
        		//METHOD
        		if ( !resItem.hasPropertyValue(propMethod) ) resItem.addPropertyValue(propMethod, method);
        		//P1
        		if ( !resItem.hasPropertyValue(prop1) ) resItem.addPropertyValue(prop1, n1);
        		//P2
        		if ( !resItem.hasPropertyValue(prop2) ) resItem.addPropertyValue(prop2, n2);
        		//READY
        		if ( !resItem.hasPropertyValue(propReady) ) resItem.addPropertyValue(propReady, true);

        		resItem.setPropertyValue(propURL, URL);
        		resItem.setPropertyValue(propMethod, method);
        		resItem.setPropertyValue(propReady, true);
        		resItem.setPropertyValue(prop1, n1);
        		resItem.setPropertyValue(prop2, n2);
    		}

	}


    protected Action createAssignNeuroSelection() {
        assignNeuroSelection = new CreateAction("Assign Neuroleucida Selection to the selected Instance", OWLIcons.getCreateIndividualIcon(OWLIcons.ACCEPT)) {
            public void onCreate()
            {
            		//*
	        		////TODO: Replace this code that uses the comments with proper properties, following code may be useful:
	        		////First create the general datatype
	        		//OWLModel owlModel = (OWLModel)getKnowledgeBase();
	        		//OWLDatatypeProperty setprop = owlModel.createOWLDatatypeProperty("nodeA");
	        		//OWLDatatypeProperty getprop = owlModel.createOWLDatatypeProperty("has Property");
	        		//*

	        		//RDFResource resource = (RDFResource) selectedInstance;
            		Instance instance = (Instance) list.getSelectedValue();
            		if (instance != null)
            		{
            			RDFResource res = (RDFResource) instance;
	        			//write the value of the nodes that were selected to the instance data (assign it)
//            		//TODO: get the info then assign it

            			int[] plist = {0,0};
            			int reqNodes = 3;
            			plist=oTab.getSelectedNodes();

            			System.out.println("*** Resolving selected nodes");
            			if (plist == null)
            			{
            				System.out.println("*** Error: No points were selected, needed" + reqNodes);
            			}
            			else if (plist.length == reqNodes)
            			{
            				assignImgSelection(res, oTab.neuronPanel.getURL(), plist[0], plist[1], plist[2]);	//graphically display the class that was selected by calling this method on current image
            			}
            			else
            			{
            				System.out.println("*** Error: Could not resolve selection list (Needed " + reqNodes + " have " + plist.length);
            			}
            		}

            }


        };
        return assignNeuroSelection;
    }


    protected Action createConfigureAction() {
        return new ConfigureAction() {
            public void loadPopupMenu(JPopupMenu menu) {
                menu.add(createSetDisplaySlotAction());
                //menu.add(createShowAllInstancesAction()); submenu not needed
            }
        };
    }


    /* we dont need this submenu
    protected JMenuItem createShowAllInstancesAction() {
        Action action = new AbstractAction("Show Subclass Instances") {
            public void actionPerformed(ActionEvent event) {
                setShowAllInstances(!showSubclassInstances);
            }
        };
        JMenuItem item = new JCheckBoxMenuItem(action);
        item.setSelected(showSubclassInstances);
        return item;
    }

    //    private void initializeShowSubclassInstances() {
    //        showSubclassInstances = ApplicationProperties.getBooleanProperty(SHOW_SUBCLASS_INSTANCES, false);
    //        reload();
    //        fixRenderer();
    //    }


    private void setShowAllInstances(boolean b) {
        showSubclassInstances = b;
        // ApplicationProperties.setBoolean(SHOW_SUBCLASS_INSTANCES, b);
        reload();
        fixRenderer();
    }
    */


    protected Cls getSoleAllowedCls() {
        Cls cls;
        if (classes.size() == 1) {
            cls = (Cls) CollectionUtilities.getFirstItem(classes);
        }
        else {
            cls = null;
        }
        return cls;
    }


    protected JMenu createSetDisplaySlotAction() {
        JMenu menu = ComponentFactory.createMenu("Set Display Slot");
        boolean enabled = false;
        Cls cls = getSoleAllowedCls();
        if (cls != null) {
            BrowserSlotPattern pattern = cls.getBrowserSlotPattern();
            Slot browserSlot = (pattern != null && pattern.isSimple()) ? pattern.getFirstSlot() : null;
            Iterator i = cls.getVisibleTemplateSlots().iterator();
            while (i.hasNext()) {
                Slot slot = (Slot) i.next();
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(createSetDisplaySlotAction(slot));
                if (slot.equals(browserSlot)) {
                    item.setSelected(true);
                }
                menu.add(item);
                enabled = true;
            }
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(createSetDisplaySlotMultipleAction());
            if (browserSlot == null) {
                item.setSelected(true);
            }
            menu.add(item);
        }
        menu.setEnabled(enabled);
        return menu;
    }


    protected Action createSetDisplaySlotAction(final Slot slot) {
        return new AbstractAction(slot.getBrowserText(), slot.getIcon()) {
            public void actionPerformed(ActionEvent event) {
                getSoleAllowedCls().setDirectBrowserSlot(slot);
                updateLabel();
                repaint();
            }
        };
    }


    protected Action createSetDisplaySlotMultipleAction() {
        return new AbstractAction("Multiple Slots...") {
            public void actionPerformed(ActionEvent event) {
                Cls cls = getSoleAllowedCls();
                BrowserSlotPattern currentPattern = getSoleAllowedCls().getBrowserSlotPattern();
                MultiSlotPanel panel = new MultiSlotPanel(currentPattern, cls);
                int rval = ModalDialog.showDialog(AssertedInstancesListOntoMorphPanel.this, panel, "Multislot Display Pattern",
                                                  ModalDialog.MODE_OK_CANCEL);
                if (rval == ModalDialog.OPTION_OK) {
                    BrowserSlotPattern pattern = panel.getBrowserTextPattern();
                    if (pattern != null) {
                        cls.setDirectBrowserSlotPattern(pattern);
                    }
                }
                updateLabel();
                repaint();
            }
        };
    }


    protected Action createDeleteAction() {
        deleteAction = new DeleteInstancesAction(this);
        return deleteAction;
    }


    protected Action createCopyAction() {
        copyAction = new MakeCopiesAction(ResourceKey.INSTANCE_COPY, this) {
            protected Instance copy(Instance instance, boolean isDeep) {
                Instance copy = super.copy(instance, isDeep);
                setSelectedInstance(copy);
                return copy;
            }
        };
        return copyAction;
    }


    protected Action createReferencersAction() {
        return new ReferencersAction(ResourceKey.INSTANCE_VIEW_REFERENCES, this);
    }


    protected Action createViewAction() {
        return new ViewAction(ResourceKey.INSTANCE_VIEW, this) {
            public void onView(Object o) {
            		Instance i = (Instance) o;	//make an instance before attempting to show it
            		if ( i != null)
            		{
            			owlModel.getProject().show(i);
            		}
            }
        };
    }


    public void dispose() {
        removeClsListeners();
        removeInstanceListeners();
    }


    public JComponent getDragComponent() {
        return list;
    }


    private SimpleListModel getModel() {
        return (SimpleListModel) list.getModel();
    }


    private boolean isSelectionEditable() {
        boolean isEditable = true;
        Iterator i = getSelection().iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (!instance.isEditable()) {
                isEditable = false;
                break;
            }
        }
        return isEditable;
    }


    public void onSelectionChange() {
        // Log.enter(this, "onSelectionChange");
        boolean editable = isSelectionEditable();
        ComponentUtilities.setDragAndDropEnabled(list, editable);
        updateButtons();
    }


    private void removeInstance(Instance instance) {
        ComponentUtilities.removeListValue(list, instance);
        instance.removeFrameListener(_instanceFrameListener);
    }


    private void removeClsListeners() {
        Iterator i = classes.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            cls.removeClsListener(_clsListener);
            cls.removeFrameListener(_clsFrameListener);
        }
    }


    public void setClses(Collection newClses) {
        removeClsListeners();
        classes = new ArrayList(newClses);
        list.setClasses(newClses);
        reload();
        updateButtons();
        addClsListeners();
    }


    public void reload() {
        removeInstanceListeners();
        Object selectedValue = list.getSelectedValue();
        Set instanceSet = new LinkedHashSet();

        Collection found = getInstances();

        System.out.println("*** reloading list");

        if ( found != null )
        		instanceSet.addAll(found);

        List instances = new ArrayList(instanceSet);
        if (instances.size() <= SORT_LIMIT) {
            Collections.sort(instances, new FrameComparator());
        }
        getModel().setValues(instances);
        if (instances.contains(selectedValue)) {
            list.setSelectedValue(selectedValue, true);
        }
        else if (!instances.isEmpty()) {
            list.setSelectedIndex(0);
        }
        addInstanceListeners();
        reloadHeader(found);
        updateLabel();
    }

    public void initialize()
    {
    		//Find out what the namespace prefix is for looking up properties
    		System.out.println("*** Initializing asserted list ...");
    		reload();
    		System.out.println("*** ... List loaded");
    }



    private void reloadHeader(Collection individuals)
	{
		Icon icon = null;
		Iterator i = individuals.iterator();
		String strNames="";
		final String para = ", ";

		while (i.hasNext())
		{
			Instance ind = (Instance) i.next();

			//get the first available icon
			if (icon == null)	icon = ind.getIcon();

			//add the name if it is not already in existance
			if (!strNames.contains(ind.getDirectType().getBrowserText()))
			{
				strNames += ind.getDirectType().getBrowserText() + para;
			}
			System.out.println("*** Classes for header: " + strNames);
		}
		//remove the last commas
		strNames = strNames.substring(0, strNames.length() - para.length());

		JLabel label = (JLabel) header.getComponent();
		label.setText(strNames);
		label.setIcon(icon);
	}


    private Collection getInstances() {
    		// method returns all instances which are aware of ontomorph tab
    		Collection instances=null;

        //instances = cls.getInstances();
    		RDFProperty ready = owlModel.getRDFProperty(oTab.namespacePrefix + ":" + oTab.pReady);

    		if ( ready != null )
    		{
    			instances = owlModel.getRDFResourcesWithPropertyValue(ready, true);
    		}
    		else
    		{
    			System.out.println("*** Could not lookup property defintion for " + oTab.namespacePrefix + ":" + oTab.pReady + " Ontology not imported");
    		}

        return instances;
    }

    public void sort() {
        list.setListenerNotificationEnabled(false);
        Object selectedValue = list.getSelectedValue();
        List instances = new ArrayList(getModel().getValues());
        if (instances.size() <= SORT_LIMIT) {
            Collections.sort(instances, new FrameComparator());
        }
        getModel().setValues(instances);
        list.setSelectedValue(selectedValue);
        list.setListenerNotificationEnabled(true);
    }


    public void setSelectedInstance(Instance instance) {
        list.setSelectedValue(instance, true);
        System.out.println("*** Selection List: [Proj: '" + instance.getProject().getProjectName() + "'] [Icon: " + instance.getIcon().toString() + "]\n Browser Text for '" + instance.getName() + "' \n [" + instance.getBrowserText() + "] \n");
        updateButtons();
    }

    public void debug()
    {
    		//This function's purpose is only for development. This code is run whenever the developer button is pressed
    		//It is meant as conveiniance of temporary testing code
    		reload();
    }

    private void updateButtons() {
        Cls cls = (Cls) CollectionUtilities.getFirstItem(classes); //formerly
        createAction.setEnabled(true);
        //createAnonymousAction.setEnabled(cls == null ? false : cls.isConcrete()); //removed because OMT doesnt support

        //Make the button inactive while no viable class is selected
        Instance instance = (Instance) getSoleSelection();
        boolean allowed = instance != null && instance instanceof SimpleInstance;
        assignNeuroSelection.setAllowed(allowed);
        copyAction.setAllowed(allowed);

    }


    /**
     * Does nothing anymore. This functionality moved to the menu button.
     *
     * @deprecated
     */
    public void setShowDisplaySlotPanel(boolean b) {

    }
}