package info.semanticsoftware.semassist.client.eclipse.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import info.semanticsoftware.semassist.client.eclipse.handlers.ServiceAgentSingleton;
import info.semanticsoftware.semassist.client.eclipse.handlers.ServiceSettingsValidator;
import info.semanticsoftware.semassist.client.eclipse.utils.Utils;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementHelper;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class allows the user to customize the host name and port value used to invoke semantic services. The entered values get validated real-time. 
 * The user can save the current settings to a properties file, so it will remain persistent between different Eclipse sessions.
 * 
 * @author Bahar Sateli
 */

public class ServiceSettingsDialog extends Dialog {

    /** Title of the dialog window */
	private String title;

    /** Message to be shown in dialog's content area */
	private String message;

    /** Validates user entered values for host name and port number */
	private IInputValidator validator;

    /** OK button component*/
	private Button okButton;
    
    /** If checked, dialog saves the host name and port value to a properties file */
	private Button checkbox;

    /** Text box component to store host name value */
	private Text txtServerHost;
    
    /** Text box component to store port value */
	private Text txtServerPort;

    /** A string to tell the user about any invalidity in entered values */
	private Text errorMessageText;
	
    /** Combobox containing available service names */
    private Combo cmbServers;
    
    Button serversButton;
    
    Button customButton;

    /**
     * Creates an input dialog with OK and Cancel buttons.
     * 
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     * @param dialogTitle the dialog title
     * @param dialogMessage the dialog message
     * @param validator an input validator
     */
    public ServiceSettingsDialog(Shell parentShell, IInputValidator validator) {
        super(parentShell);
        this.title = "Semantic Assistants Settings";
        this.message = "Please select a server:";
        this.validator = validator;
        Utils.propertiesReader();
    }

    /** Checks if OK button has been pressed and decides to save the values to a properties file */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            	/*if(txtServerHost.getText().equals("") || txtServerPort.getText().equals("")){
            		setErrorMessage("Please fill the server host and port number values");
            	}else{
            		if(checkbox.getSelection()){
                		Utils.propertiesWriter();
            		}else{
            			Utils.propertiesWriter(txtServerHost.getText(), txtServerPort.getText());
            		}
            		Utils.propertiesReader();
            		super.buttonPressed(buttonId);
            	}*/
        	
        	if(serversButton.getSelection()){
        		String address = cmbServers.getItem(cmbServers.getSelectionIndex());
        		String[] tokens = address.split(":");
        		ServiceAgentSingleton.setServerHost(tokens[0]);
        		ServiceAgentSingleton.setServerPort(tokens[1]);
        		super.buttonPressed(buttonId);
        	}else if(customButton.getSelection()){
        		if(txtServerHost.getText().equals("") || txtServerPort.getText().equals("")){
            		setErrorMessage("Please fill the server host and port number values");
            	}else{
            		Map<String, String> map = new HashMap<String, String>();
            		map.put(ClientUtils.XML_HOST_KEY, txtServerHost.getText());
            		map.put(ClientUtils.XML_PORT_KEY, txtServerPort.getText());
            		ClientUtils.setClientPreference("global", "server", map);
            		super.buttonPressed(buttonId);
            	}
        	}else{
        		setErrorMessage("Please choose a pre-defined or a custom server by selecting a radiobutton.");
        	}
        }
        
        if (buttonId == IDialogConstants.CANCEL_ID) {
        	super.buttonPressed(buttonId);
        }
        
    }

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        txtServerHost.setFocus();
    }

    /**
     * Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent) {
        // create composite to contain contents
        Composite composite = (Composite) super.createDialogArea(parent);


        // create the message label to show
        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        serversButton = new Button(composite, SWT.RADIO);
        serversButton.setText("Pre-defined Servers");
        
        serversButton.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				defaultMode();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				defaultMode();
			}
        });
        
        createServersArea(composite);
    	Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);     	 
    	separator.setBounds(50, 100, 150, 100);
    	
        customButton = new Button(composite, SWT.RADIO);
        customButton.setText("Add A New Server");
        
        customButton.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				customMode();
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				customMode();
			}
        });
        
    	createSettingsArea(composite);
		//createCheckbox(composite);
       
        errorMessageText = new Text(composite, SWT.NONE | SWT.READ_ONLY);
        errorMessageText.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_RED));
        errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        applyDialogFont(composite);
        return composite;
    }
    
    /** Creates the check box component */
    protected Composite createCheckbox(Composite composite){
    	Composite checkboxComposite = new Composite(composite, SWT.RIGHT);
    	GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        checkboxComposite.setLayout(layout);
        checkboxComposite.setFont(composite.getFont());
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        checkboxComposite.setData(data);
        
        checkbox = new Button(checkboxComposite, SWT.CHECK);
        checkbox.setText("Use defaults");
        
        if(Utils.defaultSettings){
        	defaultMode();
        }else{
        	customMode();
        }
        
        SelectionListener listener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	if(checkbox.getSelection()){
            		defaultMode();
            		setErrorMessage(null);
            	}else{
            		customMode();
            	}
            }
        };
        checkbox.addSelectionListener(listener);
        
    	return checkboxComposite;
    }

    /** Creates the text boxes component for host name and port number values */
    protected Composite createSettingsArea(Composite composite) {
    	 Composite serverSettingsComposite = new Composite(composite, SWT.RIGHT);
    	 GridLayout layout = new GridLayout();
         layout.numColumns = 2;
         serverSettingsComposite.setLayout(layout);
         serverSettingsComposite.setFont(composite.getFont());
         GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
         data.grabExcessHorizontalSpace = true;
         serverSettingsComposite.setData(data);
         //serverSettingsComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));

     	 Label lblServerHost = new Label(serverSettingsComposite, SWT.NONE);
         lblServerHost.setText("Server Host: ");
         txtServerHost = new Text(serverSettingsComposite, SWT.SINGLE | SWT.BORDER);
         txtServerHost.setSize(300, 70);
         //txtServerHost.setText(ServiceAgentSingleton.getServerHost());
         txtServerHost.addModifyListener(new ModifyListener() {
             public void modifyText(ModifyEvent e) {
                 ServiceSettingsValidator.source="host";
            	 validateInput(txtServerHost.getText());
            	 ServiceSettingsValidator.source="";
            	 //checkbox.setSelection(false);
             }
         });
         
         Label lblServerPort = new Label(serverSettingsComposite, SWT.NONE);
         lblServerPort.setText("Server Port: ");
         txtServerPort = new Text(serverSettingsComposite, SWT.SINGLE | SWT.BORDER);
         //txtServerPort.setText(ServiceAgentSingleton.getServerPort());
         txtServerPort.setSize(300, 70);
         txtServerPort.addModifyListener(new ModifyListener() {
             public void modifyText(ModifyEvent e) {
                 ServiceSettingsValidator.source="port";
            	 validateInput(txtServerPort.getText());
            	 ServiceSettingsValidator.source="";
            	 //checkbox.setSelection(false);
             }
         });
         
         return serverSettingsComposite;
	}
    
    /** Creates the text boxes component for host name and port number values */
    protected Composite createServersArea(Composite composite) {
    	 Composite serverListComposite = new Composite(composite, SWT.RIGHT);
         GridLayout layout = new GridLayout();
         layout.numColumns = 2;
         serverListComposite.setLayout(layout);
         serverListComposite.setFont(composite.getFont());
         GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
         data.grabExcessHorizontalSpace = true;
         serverListComposite.setData(data);
         //serverListComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
         
         Label lblServers = new Label(serverListComposite, SWT.NONE);
         lblServers.setText("Available Servers:");
         cmbServers = new Combo(serverListComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
         cmbServers.add("");
         ArrayList<XMLElementHelper> result = ClientUtils.getClientPreference("global", "server");
     	 for (int i = 0; i < result.size(); i++){
 	    		String key = result.get(i).getAttribute().get(ClientUtils.XML_HOST_KEY);
 	    		String value = result.get(i).getAttribute().get(ClientUtils.XML_PORT_KEY);
 	    		cmbServers.add(key + ":" + value);
     	}

         cmbServers.select(0);
         
         return serverListComposite;
	}

    /**
     * Validates the user entered values.
     * If it finds the input invalid, an error message is displayed in the dialog's message line. 
     * This method is called whenever the text changes in the input field.
     */
    protected void validateInput(String input) {
        String errorMessage = null;
        if (validator != null) {
            errorMessage = validator.isValid(input);
        }
        // important not to treat "" (blank error) the same as null
        setErrorMessage(errorMessage);
    }

    /**
     * Mutator for error message.
     * If the error message contains a message, the OK button is disabled.
     * 
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        errorMessageText.setText(errorMessage == null ? "" : errorMessage);
        errorMessageText.getParent().update();
        okButton.setEnabled(errorMessage == null);
    }
    
    /**
     * Convenience method to handle the default status of dialogs widgets
     * */
    private void defaultMode(){
    	//checkbox.setSelection(true);
		cmbServers.setEnabled(true);
    	txtServerHost.setEnabled(false);
    	txtServerPort.setEnabled(false);
    	setErrorMessage(null);
    }
    
    /**
     * Convenience method to handle the custom status of dialogs widgets
     * */
    private void customMode(){
    	//checkbox.setSelection(false);
		txtServerHost.setEnabled(true);
		txtServerPort.setEnabled(true);
		cmbServers.setEnabled(false);
    	setErrorMessage(null);
    }   
}
