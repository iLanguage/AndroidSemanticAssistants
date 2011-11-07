package info.semanticsoftware.semassist.client.eclipse.handlers;

import java.io.File;

import info.semanticsoftware.semassist.client.eclipse.dialogs.FileSelectionDialog;
import info.semanticsoftware.semassist.client.eclipse.model.Resource;
import info.semanticsoftware.semassist.client.eclipse.model.SemanticAssistantsStatusViewModel;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * This class fetches all the projects and their contents on the workspace root and allows the user to select multiple files.
 * @author Bahar Sateli
 *
 */
public class FileSelectionHandler extends AbstractHandler{
	
	public static IWorkspace workspace;
	public static String serviceName;
	public static IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public FileSelectionHandler() {
	}

	/**
	 * This method is invoked when the user has clicked on the menu item and the command has been executed.
	 * @param event the event of user clicking on the menu item
	 * @return Object the files selected by the user
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		workspace = ResourcesPlugin.getWorkspace();
       
		FileSelectionDialog dialog = new FileSelectionDialog(window.getShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle("Semantic Assistants - File Selection");
      
		dialog.setInput(workspace.getRoot());   
		dialog.open();

       /* Wait for the user to select files and press a button. 
       	  selection variable contains the list of selections made by the user, or null if the selection was canceled
       */
       Object[] selection = dialog.getResult();
       
       // Clear the session. Make it ready for the new selection
       EvaluationSession.getResources().clear();
       
       // Check if the user has pressed the OK button
       if( selection != null ){
           for( int i = 0; i < selection.length; i++ ){
                IResource src = (IResource) selection[i];
                
                //if its not a file, we are not interested
                if(src.getType() != IResource.FILE ){
                	continue;
                }
                
                //FIXME allow hidden files
                // filtering out hidden, .class and .pref files
                if(src.getType() == IResource.FILE && src.getName().startsWith(".") || src.getType() == IResource.FILE && src.getName().endsWith(".class") || src.getType() == IResource.FILE && src.getName().endsWith(".prefs")){
                	continue;
                }

                //add the file to the session resources list
                EvaluationSession.getResources().add(new Resource((IFile) src));
            }
    		
           if(EvaluationSession.getResources().size() == 0){
        	   showError("No file was selected. Aborting...");
           }else{
        	   // We have the files in session, let's invoke the service...
        	   
        	   // First, let's make sure we have the name of the service!       	   
	      		 if(serviceName.equals("")){
	    			 showError("No service was selected to run. Aborting...");
	    		  }else{
	    			  EvaluationSession.invoke(serviceName);  
	    			  SemanticAssistantsStatusViewModel.addLog("Invoking " + serviceName + "...");
	    		  }
		        }
        }
        return null;
	}
	
	public static void openViews(){
         //Open the views
			try {

				// Open a new one with fresh data
				window.getActivePage().showView("info.semanticsoftware.semassist.client.eclipse.views.SemanticAssistantsView");

				// Open a new one with fresh data
				window.getActivePage().showView("info.semanticsoftware.semassist.client.eclipse.views.SemanticAssistantsStatusView");
		
			} catch (PartInitException e) {
				System.err.println("Could not open the view.");
				e.printStackTrace();
			}
	}
	
	public static void openEditor(File file){
		 IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
		 IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		 
		 try {
		        IDE.openEditorOnFileStore( page, fileStore );
		 } catch ( PartInitException e ) {
			 System.err.println("Could not open the editor.");
		 }
	}
	
	public static void showError(String errorMessage) {
		MessageDialog.openInformation(window.getShell(),"Semantic Assistants",errorMessage);
	}
}