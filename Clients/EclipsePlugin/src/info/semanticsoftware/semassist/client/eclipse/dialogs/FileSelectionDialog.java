package info.semanticsoftware.semassist.client.eclipse.dialogs;

import info.semanticsoftware.semassist.client.eclipse.handlers.FileSelectionHandler;
import info.semanticsoftware.semassist.client.eclipse.handlers.ServiceAgentSingleton;
import info.semanticsoftware.semassist.client.eclipse.handlers.ServiceInformationThread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * This class allows the user to select file(s) and/or project(s) to send to server from a tree structure.
 * The tree structure lists all the projects and files available in the user's workspace
 * 
 * @author Bahar Sateli
 */
public class FileSelectionDialog extends SelectionStatusDialog {
    
	/** The tree viewer to show the files hierarchy */
	private CheckboxTreeViewer fViewer;

	/** The tree label provider */
	private ILabelProvider fLabelProvider;

	/** The tree content provider */
	private ITreeContentProvider fContentProvider;

    /** The tree state validator */
	private ISelectionStatusValidator fValidator;

    /** The comparator is uses the default comparator to sort the elements provided by its content provider getText() method. 
     * @see SemanticAssistantsViewContentProvider
     * */
	private ViewerComparator fComparator;

	/** The error message to be shown if the tree is empty */
    private String fEmptyListMessage = "No entries available to show."; 

    /** This object is used to represent the outcome of tree update operation */
    private IStatus fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, 0, "", null);

    /** The list of filters applied on the viewer */
    private List<ViewerFilter> fFilters;

    /** Root element of the tree */
    private Object fInput;

    /** A boolean variable indicating whether the tree is empty */
    private boolean fIsEmpty;

    /**  Dialog default width */
    private int fWidth = 60;

    /**  Dialog default height */
    private int fHeight = 18;

    /** An array containing tree elements when expanding */
    private Object[] fExpandedElements;
    
    /** Combobox containing available service names */
    public Combo cmbServices;
    
    /** Combobox containing file format filters */
    public Combo cmbExtensions;
    
    public static boolean CONNECTION_IS_FINE;
    
    private ServiceInformationThread servicesThread;
    
    private ViewerFilter filter;
    
    private String[] extensionList = {".java", ".cpp", ".txt"};

    /**
     * Constructs an instance of CheckedTreeSelectionDialog.
     * 
     * @param parentShell The shell to parent from.
     * @param labelProvider The label provider to render the entries
     * @param contentProvider The content provider to evaluate the tree structure
     */
    public FileSelectionDialog(Shell parentShell,ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        super(parentShell);
        	CONNECTION_IS_FINE = true;
			fLabelProvider = labelProvider;
		    fContentProvider = contentProvider;
		    fComparator = new ViewerComparator();
		    setResult(new ArrayList<ViewerFilter>(0));
		    setStatusLineAboveButtons(true);
		    fExpandedElements = null;
		    setHelpAvailable(false);
    }

    /**
     * Adds a filter to the tree viewer.
     * 
     * @param filter A filter.
     */
    public void addFilter(ViewerFilter filter) {
        if (fFilters == null) {
			fFilters = new ArrayList<ViewerFilter>(4);
		}
        fFilters.add(filter);
    }
    
    /**
     * Sets the tree input.
     * 
     * @param input The tree input.
     */
    public void setInput(Object input) {
        fInput = input;
    }

    /**
     * Expands elements in the tree.
     * 
     * @param elements The elements that will be expanded.
     */
    public void setExpandedElements(Object[] elements) {
        fExpandedElements = elements;
    }

    /**
     * Validate the receiver and update the status with the result.
     *
     */
    protected void updateOKStatus() {
        if (!fIsEmpty) {
            if (fValidator != null) {
                fCurrStatus = fValidator.validate(fViewer.getCheckedElements());
                updateStatus(fCurrStatus);
            } else if (!fCurrStatus.isOK()) {
                fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID,IStatus.OK, "",null);
            }
        } else {
            fCurrStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID,
                    IStatus.OK, fEmptyListMessage, null);
        }
        updateStatus(fCurrStatus);
    }

    /**
     * @see org.eclipse.jface.window.Window#open()
     */
    public int open() {
    	
    	servicesThread = new ServiceInformationThread();
		servicesThread.start();
		while(servicesThread.isAlive()){
			 //wait for the thread to fetch all the service names
		}
		
		if(CONNECTION_IS_FINE){
	        fIsEmpty = evaluateIfTreeEmpty(fInput);
	        super.open();
	        return OK;
		}else{
			MessageDialog.openError(FileSelectionHandler.window.getShell(),"Semantic Assistants","Error: Server is offline\n\nSemantic Assistants cannot show the list available services.\nPlease check the server host and port values and if server is online.");
		    return CANCEL;
		}
    }

    private void access$superCreate() {
        super.create();
    }

    /**
     * Handles cancel button pressed event.
     */
    protected void cancelPressed() {
        setResult(null);
        super.cancelPressed();
    }

    /**
     * Handles OK button pressed event.
     */
    protected void okPressed() {
        FileSelectionHandler.serviceName = cmbServices.getItem(cmbServices.getSelectionIndex());
        super.okPressed();
    }

    /**
     * @see SelectionStatusDialog#computeResult()
     */
    protected void computeResult() {
        setResult(Arrays.asList(fViewer.getCheckedElements()));
    }

    /**
     * @see org.eclipse.jface.window.Window#create()
     */
    public void create() {
        BusyIndicator.showWhile(null, new Runnable() {
            public void run() {
                access$superCreate();
                fViewer.setCheckedElements(getInitialElementSelections().toArray());
                if (fExpandedElements != null) {
                    fViewer.setExpandedElements(fExpandedElements);
                }
                updateOKStatus();
            }
        });
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        CheckboxTreeViewer treeViewer = createTreeViewer(composite);
        Control buttonComposite = createSelectionButtons(composite);
        Control cmbComposite = createServicesCombobox(composite);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertWidthInCharsToPixels(fWidth);
        data.heightHint = convertHeightInCharsToPixels(fHeight);
        Tree treeWidget = treeViewer.getTree();
        treeWidget.setLayoutData(data);
        treeWidget.setFont(parent.getFont());
        if (fIsEmpty) {
            treeWidget.setEnabled(false);
            buttonComposite.setEnabled(false);
            cmbComposite.setEnabled(false);
        }
        return composite;
    }

    /**
     * Creates the tree viewer.
     * 
     * @param parent The parent composite
     * @return the Tree viewer
     */
    protected CheckboxTreeViewer createTreeViewer(Composite parent) {
    	Label lblStepOne = new Label(parent, SWT.LEFT);
		lblStepOne.setText("Step 1: Please choose the files to be evaluated:");
        fViewer = new CheckboxTreeViewer(parent, SWT.BORDER);
        
        fViewer.setContentProvider(fContentProvider);
        fViewer.setLabelProvider(fLabelProvider);
        fViewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                updateOKStatus();
                if(event.getChecked()){
					fViewer.setSubtreeChecked(event.getElement(), true);
				}else{
					fViewer.setSubtreeChecked(event.getElement(), false);
				}
              
            }
        });
      
        fViewer.setComparator(fComparator);
        if (fFilters != null) {
            for (int i = 0; i != fFilters.size(); i++) {
				fViewer.addFilter((ViewerFilter) fFilters.get(i));
			}
        }
        fViewer.setInput(fInput);
        return fViewer;
    }

    /**
     * Returns the tree viewer.
     * 
     * @return the tree viewer
     */
    protected CheckboxTreeViewer getTreeViewer() {
        return fViewer;
    }

    /**
     * Adds the selection and de-selection buttons to the dialog.
     * 
     * @param composite The parent composite
     * @return Composite The composite the buttons were created in.
     */
    protected Composite createSelectionButtons(Composite composite) {
        Composite buttonComposite = new Composite(composite, SWT.RIGHT);
        //buttonComposite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        buttonComposite.setLayout(layout);
        buttonComposite.setFont(composite.getFont());
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        buttonComposite.setData(data);
        
        Label lblExtensions = new Label(buttonComposite, SWT.NONE);
        lblExtensions.setText("File Format:");
        cmbExtensions = new Combo(buttonComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        cmbExtensions.add("");
        
        for(int i=0; i < extensionList.length; i++){
        	cmbExtensions.add(extensionList[i]);
        }
        
        cmbExtensions.select(0);
        
        cmbExtensions.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				ViewerFilter[] filters = fViewer.getFilters();
				for (int i=0; i < filters.length; i++){
					fViewer.removeFilter(filters[i]);
				}
				
				String extTemp = cmbExtensions.getItem(cmbExtensions.getSelectionIndex());
	        	if(!extTemp.equals("")){
		        	extTemp = extTemp.substring(1);
		        	final String extension = extTemp;
		        	filter = new ViewerFilter() {
						@Override
						public boolean select(Viewer viewer, Object parent, Object element) {
			               IResource src = (IResource) element;
			                
			               if(src.getType() == IResource.PROJECT || src.getType() == IResource.FOLDER || ((src.getType() == IResource.FILE) && src.getFileExtension().equals(extension))){
			            		   return true;   
			               }else{
			                	 return false;
			               }
						}
			        };
					fViewer.addFilter(filter);
	        	}
	        	setInput(FileSelectionHandler.workspace.getRoot());
			}
		});
   
        Button selectButton = createButton(buttonComposite,
                IDialogConstants.SELECT_ALL_ID, "Select All",
                false);
        SelectionListener listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Object[] viewerElements = fContentProvider.getElements(fInput);
                
                    for (int i = 0; i < viewerElements.length; i++) {
						fViewer.setSubtreeChecked(viewerElements[i], true);
					}
                
                updateOKStatus();
            }
        };
        selectButton.addSelectionListener(listener);
        Button deselectButton = createButton(buttonComposite,
                IDialogConstants.DESELECT_ALL_ID, "Deselect All",
                false);
        listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                fViewer.setCheckedElements(new Object[0]);
                updateOKStatus();
            }
        };
        deselectButton.addSelectionListener(listener);
        return buttonComposite;
    }

    /**
     * Evaluates whether the tree has any nodes at first and after applying the filters.
     * 
     * @param input root element
     * @return true if the tree is empty
     * */
    private boolean evaluateIfTreeEmpty(Object input) {
        Object[] elements = fContentProvider.getElements(input);
        if (elements.length > 0) {
            if (fFilters != null) {
                for (int i = 0; i < fFilters.size(); i++) {
                    ViewerFilter curr = (ViewerFilter) fFilters.get(i);
                    elements = curr.filter(fViewer, input, elements);
                }
            }
        }
        return elements.length == 0;
    }
    
    /**
     * Adds the available services combobox to the dialog.
     * 
     * @param composite The parent composite
     * @return Composite The composite the combobox was created in.
     */
    protected Composite createServicesCombobox(Composite composite) {
        Composite cmbComposite = new Composite(composite, SWT.RIGHT);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        cmbComposite.setLayout(layout);
        cmbComposite.setFont(composite.getFont());
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        composite.setData(data);
		Label lblServices = new Label(composite, SWT.LEFT);
		lblServices.setText("Step 2: Please choose one of the available services:");
		Label lblServerNote = new Label(composite, SWT.LEFT);
		lblServerNote.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
		lblServerNote.setText("Connected to http://" + ServiceAgentSingleton.getServerHost() + ":" + ServiceAgentSingleton.getServerPort());
		cmbServices = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
	    cmbServices.add("");

		 for(int i=0; i < servicesThread.servicesNames.size(); i++){
			 cmbServices.add(servicesThread.servicesNames.get(i));
		 }

		cmbServices.select(0);
		
        return cmbComposite;
    }
}
