package info.semanticsoftware.semassist.client.eclipse.views;

import info.semanticsoftware.semassist.client.eclipse.model.SemanticAssistantsStatusViewModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class SemanticAssistantsStatusView extends ViewPart {

	/** The ID of the view as specified by the extension. */
	public static final String ID = "info.semanticsoftware.semassist.client.eclipse.views.SemanticAssistantsStatusView";
	
	private Action refreshAction;
	/** The viewer to show the content of the table. */
	private TableViewer viewer;

	/**
	 * The constructor.
	 */
	public SemanticAssistantsStatusView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		
		String[] columnNames = {"Log Details"};
		int[] columnWidths = new int[] {500};
		for(int i=0; i < columnNames.length; i++){
			final TableViewerColumn tableColumn = new TableViewerColumn(viewer, SWT.LEFT);
			tableColumn.getColumn().setText(columnNames[i]);
			tableColumn.getColumn().setWidth(columnWidths[i]);
			tableColumn.getColumn().setResizable(true);
			tableColumn.getColumn().setMoveable(false);
		}
		viewer.setContentProvider(new SemanticAssistantsStatusViewContentProvider());
		viewer.setLabelProvider(new SemanticAssistantsStatusViewLabelProvider());
		//viewer.setInput(getViewSite());
		viewer.setInput(SemanticAssistantsStatusViewModel.getInstance().getLogs());
		
		makeActions();
 		hookContextMenu();
		contributeToActionBars();
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SemanticAssistantsStatusView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(refreshAction);

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
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
	}

	
	/**  */
	private void makeActions() {
		refreshAction = new Action() {
			public void run() {
				viewer.refresh(true, true);
				viewer.setInput(SemanticAssistantsStatusViewModel.getInstance().getLogs());
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh");
		refreshAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
		viewer.refresh(true, true);
		viewer.setInput(SemanticAssistantsStatusViewModel.getInstance().getLogs());
	}
	
	@Override
	public void dispose(){
		getViewSite().getPage().hideView(this); 
		super.dispose();
	}

}
