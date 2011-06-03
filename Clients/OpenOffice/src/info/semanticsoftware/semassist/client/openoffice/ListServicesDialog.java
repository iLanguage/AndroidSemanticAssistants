/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info
Nikolaos Papadakis
Tom Gitzinger

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.semanticsoftware.semassist.client.openoffice;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import info.semanticsoftware.semassist.server.*;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.client.openoffice.utils.*;

//import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.FontDescriptor;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XProgressBar;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import javax.swing.JOptionPane;
import javax.xml.ws.WebServiceException;

/**
 * 
 * @author Thomas Gitzinger <gitzing@ipd.uni-karlsruhe.de>
 */
public class ListServicesDialog
{

    private static Vector<String> serviceNames = new Vector<String>();
    private static final String _labelName = "lblName";
    private static final String _labelDescription = "lblDescription";
    private static final String _labelStatus = "lblStatus";
    private static final String _labelInfo = "lblInfo";
    private static final String _listName = "listServices";
    private static final String _progressName = "progrBar";
    private static final String _btnOKName = "btnOK";
    private static final String _btnCancelName = "btnCancel";
    // Layout constants
    private static final int DIALOG_WIDTH = 250;
    private static final int DIALOG_HEIGHT = 180;
    private static final int LBOX_WIDTH = DIALOG_WIDTH - 10;
    private static final int LBOX_HEIGHT = DIALOG_HEIGHT - 55;
    private static final int LBOX_TOP = 32;
    private XComponentContext _xComponentContext;
    private XDialog xDialog = null;
    private static Logger log = Logger.getLogger( ListServicesDialog.class );

    /**
     * ListServicesDialog constructor
     * @param XComponentContext xComponentContext
     * @param String davServer
     * @param int davPort
     * @param String davPath
     * @param String davUser
     * @param String davPass
     * @return OpenDialog
     * @throws IOException
     */
    public ListServicesDialog( XComponentContext xComponentContext ) throws IOException
    {
        _xComponentContext = xComponentContext;
    }

    /**
     * Method for creating a the dialog box
     */
    public void createDialog() throws com.sun.star.uno.Exception
    {
        log.log( Level.DEBUG, "createDialog();" );
        // Get the service manager from the component context
        XMultiComponentFactory xMultiComponentFactory = _xComponentContext.getServiceManager();

        // Create the dialog model and set the properties
        log.log( Level.DEBUG, "Create the dialog model and set the properties" );
        Object dialogModel = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.UnoControlDialogModel", _xComponentContext );
        final XPropertySet xPSetDialog = UnoRuntime.queryInterface(
                XPropertySet.class, dialogModel );
        xPSetDialog.setPropertyValue( "Width", new Integer( DIALOG_WIDTH ) );
        xPSetDialog.setPropertyValue( "Height", new Integer( DIALOG_HEIGHT ) );
        xPSetDialog.setPropertyValue( "PositionX", new Integer( 100 ) );
        xPSetDialog.setPropertyValue( "PositionY", new Integer( 100 ) );
        xPSetDialog.setPropertyValue( "Title", new String( "Available Assistants" ) );
        xPSetDialog.setPropertyValue( "Closeable", false );

        // Get the service manager from the dialog model
        log.log( Level.DEBUG, "Get the service manager from the dialog model" );
        final XMultiServiceFactory xMultiServiceFactory = UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dialogModel );


        // Create the labels of the list model and set the properties
        log.log( Level.DEBUG, "Create the labels of the list model and set the properties" );
        // Service name
        Object labelNameModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel" );

        final XPropertySet xPSetLabelName = UnoRuntime.queryInterface(
                XPropertySet.class, labelNameModel );

        xPSetLabelName.setPropertyValue( "PositionX", new Integer( 5 ) );
        xPSetLabelName.setPropertyValue( "PositionY", new Integer( LBOX_TOP - 13 ) );
        xPSetLabelName.setPropertyValue( "Width", new Integer( LBOX_WIDTH ) );
        xPSetLabelName.setPropertyValue( "Height", new Integer( 13 ) );
        xPSetLabelName.setPropertyValue( "Align", new Short( (short) 0 ) );
        xPSetLabelName.setPropertyValue( "Name", _labelName );
        xPSetLabelName.setPropertyValue( "TabIndex", new Short( (short) 99 ) );
        xPSetLabelName.setPropertyValue( "Label", new String( " Name and description" ) );


        // Create the list model and set the properties
        log.log( Level.DEBUG, "Create the list model and set the properties" );
        Object listModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlListBoxModel" );
        final XPropertySet xPSetList = UnoRuntime.queryInterface(
                XPropertySet.class, listModel );

        xPSetList.setPropertyValue( "PositionX", new Integer( 5 ) );
        xPSetList.setPropertyValue( "PositionY", new Integer( LBOX_TOP ) );
        xPSetList.setPropertyValue( "Width", new Integer( LBOX_WIDTH ) );
        xPSetList.setPropertyValue( "Height", new Integer( LBOX_HEIGHT ) );
        xPSetList.setPropertyValue( "Name", _listName );
        FontDescriptor f = new FontDescriptor();
        //f.Name = new String("Verdana");
        xPSetList.setPropertyValue( "FontDescriptor", f );
        xPSetList.setPropertyValue( "TabIndex", new Short( (short) 3 ) );


        /*
         * Status label
         */
        Object labelStatusModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel" );
        final XPropertySet xPSetStatusLabel = UnoRuntime.queryInterface(
                XPropertySet.class, labelStatusModel );
        xPSetStatusLabel.setPropertyValue( "PositionX", new Integer( 5 ) );
        xPSetStatusLabel.setPropertyValue( "PositionY", new Integer( LBOX_TOP + LBOX_HEIGHT + 8 ) );
        xPSetStatusLabel.setPropertyValue( "Width", new Integer( 65 ) );
        xPSetStatusLabel.setPropertyValue( "Height", new Integer( 10 ) );
        // xPSetStatusLabel.setPropertyValue("Align", new Short((short)0));
        xPSetStatusLabel.setPropertyValue( "Name", _labelStatus );
        xPSetStatusLabel.setPropertyValue( "TabIndex", new Short( (short) 0 ) );
        xPSetStatusLabel.setPropertyValue( "Label", new String( "" ) );

        /*
         * Info label
         */
        Object labelInfoModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel" );
        final XPropertySet xPSetInfoLabel = UnoRuntime.queryInterface(
                XPropertySet.class, labelInfoModel );
        xPSetInfoLabel.setPropertyValue( "PositionX", new Integer( 5 ) );
        xPSetInfoLabel.setPropertyValue( "PositionY", new Integer( LBOX_TOP - 25 ) );
        xPSetInfoLabel.setPropertyValue( "Width", new Integer( LBOX_WIDTH ) );
        xPSetInfoLabel.setPropertyValue( "Height", new Integer( 10 ) );
        // xPSetInfoLabel.setPropertyValue("Align", new Short((short)0));
        xPSetInfoLabel.setPropertyValue( "Name", _labelInfo );
        xPSetInfoLabel.setPropertyValue( "TabIndex", new Short( (short) 0 ) );
        xPSetInfoLabel.setPropertyValue( "Label", new String( "Select the text assistant you wish to use" ) );


        // Create the Progress bar and set the properties
        log.log( Level.DEBUG, "Create the Progress bar and set the properties" );
        Object progressModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlProgressBarModel" );
        final XPropertySet xPSetProgress = UnoRuntime.queryInterface(
                XPropertySet.class, progressModel );
        xPSetProgress.setPropertyValue( "PositionX", new Integer( 60 ) );
        xPSetProgress.setPropertyValue( "PositionY", new Integer( LBOX_TOP + LBOX_HEIGHT + 4 ) );
        xPSetProgress.setPropertyValue( "Width", new Integer( LBOX_WIDTH - 170 ) );
        xPSetProgress.setPropertyValue( "Height", new Integer( 14 ) );
        xPSetProgress.setPropertyValue( "Name", _progressName );
        xPSetProgress.setPropertyValue( "TabIndex", new Short( (short) 6 ) );


        // Create an OK button model and set the properties
        log.log( Level.DEBUG, "Create the Ok button model and set the properties" );
        Object okModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel" );
        final XPropertySet xPSetOk = UnoRuntime.queryInterface(
                XPropertySet.class, okModel );
        xPSetOk.setPropertyValue( "PositionX", new Integer( 5 + LBOX_WIDTH - 90 ) );
        xPSetOk.setPropertyValue( "PositionY", new Integer( LBOX_TOP + LBOX_HEIGHT + 4 ) );
        xPSetOk.setPropertyValue( "Width", new Integer( 42 ) );
        xPSetOk.setPropertyValue( "Height", new Integer( 14 ) );
        xPSetOk.setPropertyValue( "Name", _btnOKName );
        xPSetOk.setPropertyValue( "TabIndex", new Short( (short) 5 ) );
        // xPSetClose.setPropertyValue("PushButtonType", new Short((short)2));
        xPSetOk.setPropertyValue( "Label", new String( "Run" ) );

        // Create an OK button model and set the properties
        log.log( Level.DEBUG, "Create the Cancel button model and set the properties" );
        Object cancelModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel" );
        final XPropertySet xPSetCancel = UnoRuntime.queryInterface(
                XPropertySet.class, cancelModel );
        xPSetCancel.setPropertyValue( "PositionX", new Integer( 5 + LBOX_WIDTH - 42 ) );
        xPSetCancel.setPropertyValue( "PositionY", new Integer( LBOX_TOP + LBOX_HEIGHT + 4 ) );
        xPSetCancel.setPropertyValue( "Width", new Integer( 42 ) );
        xPSetCancel.setPropertyValue( "Height", new Integer( 14 ) );
        xPSetCancel.setPropertyValue( "Name", _btnCancelName );
        xPSetCancel.setPropertyValue( "TabIndex", new Short( (short) 5 ) );
        // xPSetClose.setPropertyValue("PushButtonType", new Short((short)2));
        xPSetCancel.setPropertyValue( "Label", new String( "Cancel" ) );




        // Insert the control models into the dialog model
        log.log( Level.DEBUG, "Insert the control models into the dialog model" );
        final XNameContainer xNameCont = UnoRuntime.queryInterface(
                XNameContainer.class, dialogModel );
        xNameCont.insertByName( _labelInfo, labelInfoModel );
        xNameCont.insertByName( _labelName, labelNameModel );
        // xNameCont.insertByName(_labelDescription, labelDescriptionModel);
        xNameCont.insertByName( _listName, listModel );
        xNameCont.insertByName( _labelStatus, labelStatusModel );
        xNameCont.insertByName( _progressName, progressModel );
        xNameCont.insertByName( _btnOKName, okModel );
        xNameCont.insertByName( _btnCancelName, cancelModel );

        // Create the dialog control and set the model
        log.log( Level.DEBUG, "Create the dialog control and set the model" );
        Object dialog = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.UnoControlDialog", _xComponentContext );
        final XControl xControl = UnoRuntime.queryInterface(
                XControl.class, dialog );
        final XControlModel xControlModel = UnoRuntime.queryInterface(
                XControlModel.class, dialogModel );
        xControl.setModel( xControlModel );

        // Events...
        final XControlContainer xControlCont = UnoRuntime.queryInterface(
                XControlContainer.class, dialog );

        updateServiceList( xControlCont );


        // Add an action listener to the OK button control
        log.log( Level.DEBUG, "Add an action listener to the OK button control" );
        Object objectOk = xControlCont.getControl( _btnOKName );
        final XButton xOkBtn = UnoRuntime.queryInterface( XButton.class, objectOk );
        xOkBtn.addActionListener( new OnOkClick( xControlCont ) );


        // Add an action listener to the OK button control
        log.log( Level.DEBUG, "Add an action listener to the Cancel button control" );
        Object objectCancel = xControlCont.getControl( _btnCancelName );
        final XButton xCancelBtn = UnoRuntime.queryInterface( XButton.class, objectCancel );
        xCancelBtn.addActionListener( new OnCancelClick( xControlCont ) );

        // Create a peer
        log.log( Level.DEBUG, "Create a peer" );
        Object toolkit = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.ExtToolkit", _xComponentContext );
        final XToolkit xToolkit = UnoRuntime.queryInterface(
                XToolkit.class, toolkit );
        final XWindow xWindow = UnoRuntime.queryInterface(
                XWindow.class, xControl );
        xWindow.setVisible( false );
        xControl.createPeer( xToolkit, null );

        // Execute the dialog
        log.log( Level.DEBUG, "Execute the dialog" );
        xDialog = UnoRuntime.queryInterface(
                XDialog.class, dialog );
        xDialog.execute();

        // Dispose the dialog
        log.log( Level.DEBUG, "Dispose the dialog" );
        final XComponent xComponent = UnoRuntime.queryInterface(
                XComponent.class, dialog );
        xComponent.dispose();
    }

    private void updateServiceList( XControlContainer c )
    {
        // Grant 30 seconds as timeout value
        TimeOut to = new TimeOut( c, 30000 );

        GetServices gsThread = new GetServices( c, to );

        gsThread.start();
    }

    private void setStatusText( XControlContainer m_c, String s )
    {
        // Get the model of the status label
        final XControl xControl = UnoRuntime.queryInterface(
                XControl.class, m_c.getControl( _labelStatus ) );
        XControlModel xControlModel = UnoRuntime.queryInterface(
                XControlModel.class, xControl.getModel() );
        XPropertySet ps = UnoRuntime.queryInterface(
                XPropertySet.class, xControlModel );

        try
        {
            ps.setPropertyValue( "Label", new String( s ) );
        }
        catch( Exception e )
        {
            log.log( Level.DEBUG, e.toString() );
        }

    }

    private XFrame getThisFrame()
    {
        // Get a desktop object
        Object m_oDesktop = null;
        XMultiComponentFactory xMultiComponentFactory = _xComponentContext.getServiceManager();

        try
        {
            m_oDesktop = xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", _xComponentContext );
        }
        catch( Exception e )
        {
            log.debug( e.getLocalizedMessage() );
        }

        // Query the desktop interface from the object
        final XDesktop m_xDesktop = UnoRuntime.queryInterface(
                XDesktop.class, m_oDesktop );

        // return (XFrame)UnoRuntime.queryInterface(XFrame.class, xDialog);
        return m_xDesktop.getCurrentFrame();
    }

    private String formatServiceInfo( ServiceInfoForClient info )
    {
        return info.getServiceName() + " - " + info.getServiceDescription();
    }

    /**
     * OnListDblClick - Called when the user double click on a document in the Open Document dialog
     */
    public class OnListDblClick implements XActionListener
    {

        private XControlContainer _xControlCont;

        public OnListDblClick( XControlContainer xControlCont )
        {
            _xControlCont = xControlCont;
        }

        // XEventListener
        public void disposing( EventObject eventObject )
        {
            _xControlCont = null;
        }

        // XActionListener
        public void actionPerformed( ActionEvent actionEvent )
        {
            // goIntoDav(_xControlCont);
        }

    }

    /**
     * OnCaneclClick - Called when the user presses the "Cancel"
     * button in the Open Document dialog
     */
    public class OnCancelClick implements XActionListener
    {

        XControlContainer _xControlCont;

        public OnCancelClick( XControlContainer xControlCont )
        {
            _xControlCont = xControlCont;
        }

        // XEventListener
        @Override
        public void disposing( EventObject eventObject )
        {
            _xControlCont = null;
        }

        // XActionListener
        @Override
        public void actionPerformed( ActionEvent actionEvent )
        {
            log.debug( "End execution with cancel btn" );
            Settings.setSelectedServiceName( "CancelBtn" );
            xDialog.endExecute();
        }

    }

    /**
     * OnOkClick - Called when the user presses the "Ok"
     * button in the Open Document dialog
     */
    public class OnOkClick implements XActionListener
    {

        XControlContainer _xControlCont;

        public OnOkClick( XControlContainer xControlCont )
        {
            _xControlCont = xControlCont;
        }

        // XEventListener
        @Override
        public void disposing( EventObject eventObject )
        {
            _xControlCont = null;
        }

        // XActionListener
        @Override
        public void actionPerformed( ActionEvent actionEvent )
        {

            // Find out which service is selected and save this
            // information to the settings class
            final XListBox x = UnoRuntime.queryInterface(
                    XListBox.class, _xControlCont.getControl( _listName ) );

            short pos = x.getSelectedItemPos();

            if( pos >= 0 )
            {
                Settings.setSelectedServiceName( serviceNames.get( pos ) );
                log.debug( "Saving service name \"" + (serviceNames.get( pos )) + "\" to Settings" );

                xDialog.endExecute();
            }
            else
            {
                log.debug( "pos is negative" );
                JOptionPane.showMessageDialog( null,"Please select a text assistant before you invoke it. ",
                    "Invoke text assistant", JOptionPane.WARNING_MESSAGE );
            }
        }

    }

    private class GetServices implements Runnable
    {

        private Thread thread;
        private XControlContainer m_c;
        private TimeOut m_to;

        public GetServices( XControlContainer c, TimeOut to )
        {
            m_c = c;
            m_to = to;
            thread = new Thread( this );
        }

        public void start()
        {
            thread.start();
        }

        public void join()
        {
            try
            {
                thread.join();
            }
            catch( InterruptedException e )
            {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {

            log.log( Level.DEBUG, "GetServices;" );

            // Start the timeout
            m_to.start();

            // Disable widgets
            UnoRuntime.queryInterface(
                    XWindow.class, m_c.getControl( _listName ) ).setEnable( false );


            // Update the list box
            final XListBox x = UnoRuntime.queryInterface(
                    XListBox.class, m_c.getControl( _listName ) );

            log.log( Level.DEBUG, "x.removeItems((short)0, " + String.valueOf( x.getItemCount() ) + ")" );
            x.removeItems( (short) 0, x.getItemCount() );

            log.log( Level.DEBUG, "Getting available services" );

            final XPropertySet ps = UnoRuntime.queryInterface(
                    XPropertySet.class, m_c.getControl( _labelStatus ) );


            // Connect to the server and retrieve the available
            // assistants
            try
            {


                setStatusText( m_c, "Connecting to server..." );
                SemanticServiceBroker broker = ServiceAgentSingleton.getInstance();
                if( broker == null )
                {
                    m_to.stop();
                    xDialog.endExecute();
                    return;
                }
                setStatusText( m_c, "Retrieving assistants..." );

                
                ServiceInfoForClientArray sia = broker.getAvailableServices();
                HashMap<String, ServiceInfoForClient> availableServices =
                                                      new HashMap<String, ServiceInfoForClient>();

                setStatusText( m_c, "Listing assistants..." );
                List<ServiceInfoForClient> results = sia.getItem();


                // Check if the window is still there (user
                // might have closed it)
                if( m_c == null )
                {
                    m_to.stop();
                    return;
                }


                String selectedName = Settings.getSelectedServiceName();

                if( selectedName == null )
                {
                    selectedName = "";
                }

                // While traversing the service information records,
                // keep the names of the services separately in the
                // vector serviceNames for later access to the names
                serviceNames.clear();
                short i = 0;
                short selIndex = -1;

                for( Iterator<ServiceInfoForClient> it = results.iterator(); it.hasNext(); )
                {
                    ServiceInfoForClient info = it.next();
                    availableServices.put( info.getServiceName(), info );

                    if( info.getServiceName().equals( selectedName ) )
                    {
                        selIndex = i;
                    }

                    serviceNames.add( info.getServiceName() );
                    x.addItem( formatServiceInfo( info ), i++ );
                }
                Settings.setAvailableServices( availableServices );


                // Scroll to top
                x.makeVisible( (short) 0 );

                // Select entry that should be selected
                log.debug( "selIndex = " + selIndex );
                if( selIndex >= 0 )
                {
                    x.selectItemPos( selIndex, true );
                }

                // Enable widgets
                UnoRuntime.queryInterface(
                        XWindow.class, m_c.getControl( _listName ) ).setEnable( true );

                 // Abort timeout
                m_to.stop();
                setStatusText( m_c, "" );

            }
            catch( WebServiceException we )
            {
                 // Abort timeout
                Settings.setSelectedServiceName( "Exception");
                we.printStackTrace();
                m_to.kill();
                setStatusText( m_c, "" );
                //xDialog.endExecute();
                JOptionPane.showMessageDialog( null, "Server not found. \nPlease check the Server Host and Port and if Server is Online",
                        "Server Offline", JOptionPane.ERROR_MESSAGE );
                xDialog.endExecute();
                
            }
        }

    }

    private class TimeOut implements Runnable
    {

        private Thread thread;
        private XControlContainer m_c;
        private boolean end = false;
        private final int COUNTDOWN;
        private Logger timeoutLog = Logger.getLogger( TimeOut.class );
        private boolean kill = false;

        public TimeOut( XControlContainer c, int milliSec )
        {
            m_c = c;
            thread = new Thread( this );
            COUNTDOWN = milliSec;
            timeoutLog.setLevel( Level.INFO );
        }

        public void start()
        {
            thread.start();
        }

        public void join()
        {
            try
            {
                thread.join();
            }
            catch( InterruptedException e )
            {
                e.printStackTrace();
            }
        }

        public void stop()
        {
            end = true;
        }

        public void kill()
        {
            kill = true;
        }

        @Override
        public void run()
        {
            // int timeout = 30;
            int timeout = COUNTDOWN;
            int sleepTime = 100;
            
            try
            {
                for( double i = 0.0; i < 100.0; i += (double) (100 * sleepTime) / (double) timeout )
                {
                    // timeoutLog.info("i = " + i);

                    if( kill == true)
                    {
                        return;
                    }

                    if( end == true )
                    {
                        UnoRuntime.queryInterface(
                                XWindow.class, m_c.getControl( _progressName ) ).setVisible( false );
                        return;
                    }
                    Thread.sleep( sleepTime );

                    UnoRuntime.queryInterface(
                            XWindow.class, m_c.getControl( _progressName ) ).setVisible( true );
                    final XProgressBar p = UnoRuntime.queryInterface(
                            XProgressBar.class, m_c.getControl( _progressName ) );

                    // Update the progress bar
                    // p.setValue(i);
                    p.setValue( (int) Math.round( i ) );
                }
            }
            catch( InterruptedException e )
            {
                //e.printStackTrace();
                System.out.println( "-------------InterruptedException following"  );
                return;
            }
            catch( NullPointerException e )
            {
                System.out.println( "-------------NullPointerException following"  );
                e.printStackTrace();
                return;
            }

            // Timeout is over. Notify user
            setStatusText( m_c, "Connection failed" );
            GUIUtils.showMessageBox( _xComponentContext, getThisFrame(),
                    "Connection failed", "Timed out while trying to connect to the server." );
        }

        public boolean hasStopped()
        {
            return !end;
        }

    } // End of Class Timeout

}
	
