package info.semanticsoftware.semassist.client.eclipse.handlers;

import info.semanticsoftware.semassist.client.eclipse.dialogs.ServiceSettingsDialog;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This class handles the command for viewing or updating service settings. 
 * 
 * @author Bahar Sateli
 */
public class ServiceSettingsHandler extends AbstractHandler{
	
	/** A static reference to user's current active window */
	public static IWorkbenchWindow window;

	public ServiceSettingsHandler(){
	}
	
	/** Invoked when user click on settings menu item and opens the Service Settings dialog window */
	public Object execute(ExecutionEvent event) throws ExecutionException {
        
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
               
        ServiceSettingsValidator validator = new ServiceSettingsValidator();
        ServiceSettingsDialog dialog = new ServiceSettingsDialog(window.getShell() ,validator);
        
        dialog.open();
 
        return null;
     }
}
