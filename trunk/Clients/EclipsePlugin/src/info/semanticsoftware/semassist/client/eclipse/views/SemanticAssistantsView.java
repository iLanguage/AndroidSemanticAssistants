package info.semanticsoftware.semassist.client.eclipse.views;

import info.semanticsoftware.semassist.client.eclipse.handlers.FileSelectionHandler;
import info.semanticsoftware.semassist.client.eclipse.model.Result;
import info.semanticsoftware.semassist.client.eclipse.model.SemanticAssistantsViewModel;
import java.io.File;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

/**
 * The JavadocMiner view shows data obtained from the view model described in SemanticAssistantsViewModel class. 
 * <p>
 * This view is connected to the corresponding model using the SemanticAssistantsViewContentProvider class.
 * It view also uses a label provider to define how model objects should be presented in the view by using the SemanticAssistantsViewLabelProvider class. 
 * In this view the user will be shown the list of file evaluated by the selected semantic service along their details.
 * The view is set to close itself when the plug-in is stopped or the Eclipse workbench is about to shutdown.
 * <p>
 * @see SemanticAssistantsViewModel.java
 * @see SemanticAssistantsViewContentProvider.java
 * @see SemanticAssistantsViewLabelProvider.java
 */

public class SemanticAssistantsView extends ViewPart {
	
	// TODO remove sample action
	private Action sampleAction;
	
	/** The viewer to show the content of the table. */
	private TableViewer viewer;
	
	/** This action refreshes the contents of the table. */
	private Action refreshAction;

	/** When called, this action closes the view */
	private Action closeAction;
	
	/** When called, this action reads the object that has been clicked on, 
	 * finds the path to the selected file and open it in a new editor 
	 * */
	private Action doubleClickAction;
	
	
	/** An instance of SemanticAssistantsViewTableSorter class that offers table sorting by columns */
	private SemanticAssistantsViewTableSorter tableSorter;
	
	/** The type of marker being created by Semantic Assistants plug-in */
	final String MARKER_TYPE = "SemanticAssistants.markers";
	
	/** An instance of IMarker type to represent a semantic annotation */
	private IMarker marker;
	
	/** The file to attach the annotation markers */
	private IFile ifile;

	/**
	 * The constructor.
	 */
	public SemanticAssistantsView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);	
		tableSorter = new SemanticAssistantsViewTableSorter();
		viewer.setSorter(tableSorter);

		//FIXME discuss column names
		String[] columnNames = {"Project", "Class Name", "Type", "Content","Start", "End" , "Features"};
		//FIXME fix last column width
		int[] columnWidths = new int[] {100,100,100,400,50,50,800};
		
		// Make the columns and attach listeners
		for(int i=0; i < columnNames.length; i++){
			// Stated as final to use inside SelectionAdapter anonymous class
			final TableViewerColumn tableColumn = new TableViewerColumn(viewer, SWT.LEFT);
			
			tableColumn.getColumn().setText(columnNames[i]);
			tableColumn.getColumn().setWidth(columnWidths[i]);
			tableColumn.getColumn().setResizable(true);
			tableColumn.getColumn().setMoveable(false);

			// Helper variable stated as final to use inside SelectionAdapter anonymous class
			final int index = i;
			tableColumn.getColumn().addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					tableSorter.setColumn(index);
					int direction = viewer.getTable().getSortDirection();
					if (viewer.getTable().getSortColumn() == tableColumn.getColumn()) {
						// reverse the direction
						if(direction == SWT.UP){
							direction = SWT.DOWN;
						}else{
							direction = SWT.UP;
						}
					} else {
						// default direction
						direction = SWT.DOWN;
					}
					viewer.getTable().setSortDirection(direction);
					viewer.getTable().setSortColumn(tableColumn.getColumn());
					viewer.refresh();
				}
			});
		}
		
		viewer.setContentProvider(new SemanticAssistantsViewContentProvider());
		viewer.setLabelProvider(new SemanticAssistantsViewLabelProvider());
		viewer.setInput(SemanticAssistantsViewModel.getInstance().getResults());
 		
		makeActions();
 		hookDoubleClickAction();
 		hookContextMenu();
		contributeToActionBars();
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SemanticAssistantsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(refreshAction);
		//manager.add(sampleAction);
		manager.add(closeAction);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
		manager.add(new Separator());
		//manager.add(Action);
		manager.add(new Separator());
		manager.add(closeAction);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
		//manager.add(sampleAction);
		manager.add(closeAction);

	}

	
	/**  */
	private void makeActions() {
		
		refreshAction = new Action() {
			public void run() {
				viewer.refresh(true, true);
				viewer.setInput(SemanticAssistantsViewModel.getInstance().getResults());
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh the view");
		refreshAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		
		sampleAction = new Action() {
			public void run() {
				showMessage("Sample Action executed");
			}
		};
		//sampleAction.setText("Sample Action");
		//sampleAction.setToolTipText("Sample Action");
		//sampleAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				
				String featuresLine =  ((Result)obj).getFeatures();
				int lineNum = getLineNumber(featuresLine);
				if(lineNum != -1){
							Path ResourcePath = new Path(((Result)obj).getRelativePath());
							ifile = FileSelectionHandler.workspace.getRoot().getFile(ResourcePath);
							
							if(!markerExists(ifile,Integer.parseInt(((Result)obj).getID()))){
								addMarker(Integer.parseInt(((Result)obj).getID()) ,ifile, lineNum , ((Result)obj).getFeatures());
							}

							File fileToOpen = new File(((Result)obj).getFilePath());
							 
							if (fileToOpen.exists() && fileToOpen.isFile()) {
							    IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
							 
							    try {
							        IDE.openEditorOnFileStore(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileStore);
							        IDE.gotoMarker(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(), marker);
							    } catch ( PartInitException e ) {
							        System.err.println(e.getMessage());
							    }
							} else {
							  	System.err.println("File does not exist!");
							}
				}else{

					File fileToOpen = new File(((Result)obj).getFilePath());
					 
					if (fileToOpen.exists() && fileToOpen.isFile()) {
					    IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
					 
					    try {
					        IDE.openEditorOnFileStore(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileStore);
					    } catch ( PartInitException e ) {
					        System.err.println(e.getMessage());
					    }
					} else {
					  	System.err.println("File does not exist!");
					}
				}//else		
			}
		};
		
		closeAction = new Action() {
			public void run() {
				dispose();
			}
		};
		closeAction.setText("Close");
		closeAction.setToolTipText("Close the view");
		closeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_STOP));
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),"Semantic Assistants",message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
		viewer.refresh(true, true);
		viewer.setInput(SemanticAssistantsViewModel.getInstance().getResults());
	}
	
	/**
	 * Closes the view.
	 */
	@Override
	public void dispose(){
		getViewSite().getPage().hideView(this); 
		super.dispose();
	}
	
	/**
	 * Adds a warning marker on the left ruler of the editor.
	 * @param annotID Annotation ID
	 * @param file  The file to attach the annotation
	 * @param lineNumber The line number of which the annotation should attach itself
	 * @param features The annotation features
	 */
	private void addMarker(int annotID, IFile file, int lineNumber, String features) {			
		try {
			marker = file.createMarker(MARKER_TYPE);
            if (lineNumber == -1) {
              lineNumber = 1;
            }
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
            marker.setAttribute(IMarker.MESSAGE, features);
            marker.setAttribute(IMarker.SEVERITY, 1);
            marker.setAttribute("ID", annotID);
		} catch (CoreException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Finds whether an annotation exists or not. 
	 * This should be checked to avoid creating multiple instances of the same annotation ID.
	 * @param file The file to check
	 * @param annotID The annotation ID to look for
	 * @return true if the annotation exists, otherwise false
	 */
	private boolean markerExists(IFile file, int annotID){
		boolean exists = false;
		IMarker[] annotations = null;
		try{
			annotations = file.findMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
			for(int i=0; i < annotations.length; i++){
				if(annotations[i].getAttribute("ID").equals(annotID)){
					exists = true;
				}
			}
		} catch (CoreException e) {
			System.out.println(e.getMessage());
		}
		return exists;
	}
	
	private int getLineNumber(String featuresLine){
		int lineNum;
		
		//FIXME hard coded feature name		
		int index = featuresLine.indexOf("line");
		
		// if there is no "line" feature in the list
		if(index == -1){
			lineNum = -1 ;
		}else{
			// if the line feature has no value, i.e. "line=|"
			featuresLine = featuresLine.substring(index+5);
			// we know that features are separated by pipe characters
			index = featuresLine.indexOf("|");
			// this should bring back just the number
			featuresLine = featuresLine.substring(0, index-1);
				
			// FIXME Nasty hack for the case of "line=|"
			if(featuresLine.length() == 0){
				lineNum = -1;
			}else{
				try{
					lineNum = Integer.parseInt(featuresLine);
				}catch(NumberFormatException e){
					e.printStackTrace();
					lineNum = -1;
				}
			}
		}
		
		return lineNum;
	}
}