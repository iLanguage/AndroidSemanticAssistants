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

/**
 *
 * @author nikolaos
 */
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.awt.Dialog.ModalExclusionType;

public class SettingsDialog
{

    protected static final String mLabelName = "lblName";
    protected static final String mLabelDescription = "lblDescription";
    protected static final String mLabelStatus = "lblStatus";
    protected static final String mLabelInfo = "lblInfo";
    protected static final String mListName = "listServices";
    protected static final String mProgressName = "progrBar";
    protected static final String mBtnOKName = "btnOK";
    protected static final String mChkBxName = "ChkBox";

    /*protected static final String HIGHLIGHT_ON = "Disable Annotation Highlighting";
    protected static final String HIGHLIGHT_OFF = "Enable Annotation Highlighting";
    protected static String  CURRENT_HIGHLIGHT = HIGHLIGHT_OFF;
    protected static String CURRENT_SERVER_IP = "Change Server IP";
    */
    
    // Layout constants
    protected static final int DIALOG_WIDTH = 250;
    protected static final int DIALOG_HEIGHT = 180;
    protected static final int LBOX_WIDTH = DIALOG_WIDTH - 10;
    protected static final int LBOX_HEIGHT = DIALOG_HEIGHT - 55;
    protected static final int LBOX_TOP = 32;
    protected XComponentContext mxComponentContext;
    protected XDialog mxDialog = null;
    protected static Logger mLog = Logger.getLogger( SettingsDialog.class );
    protected GlobalSettingsFrame mGloablSettingsFrame = null;
    protected SettingsFrame mSettingsFrame = null;


    public SettingsDialog( XComponentContext xComponentContext ) throws IOException
    {
        mxComponentContext = xComponentContext;

    }

    public void createDialog() throws com.sun.star.uno.Exception
    {
        mLog.log( Level.DEBUG, "createDialog();" );
        // Get the service manager from the component context
        XMultiComponentFactory xMultiComponentFactory = mxComponentContext.getServiceManager();

        // Create the dialog model and set the properties
        mLog.log( Level.DEBUG, "Create the dialog model and set the properties" );
        Object dialogModel = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.UnoControlDialogModel", mxComponentContext );
        XPropertySet xPSetDialog = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, dialogModel );
        xPSetDialog.setPropertyValue( "Width", new Integer( DIALOG_WIDTH ) );
        xPSetDialog.setPropertyValue( "Height", new Integer( DIALOG_HEIGHT ) );
        xPSetDialog.setPropertyValue( "PositionX", new Integer( 100 ) );
        xPSetDialog.setPropertyValue( "PositionY", new Integer( 100 ) );
        xPSetDialog.setPropertyValue( "Title", new String( "Global Settings" ) );


        // Get the service manager from the dialog model
        mLog.log( Level.DEBUG, "Get the service manager from the dialog model" );
        XMultiServiceFactory xMultiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dialogModel );


        // Create the labels of the list model and set the properties
        mLog.log( Level.DEBUG, "Create the labels of the list model and set the properties" );
        // Service name
        Object labelNameModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel" );

        XPropertySet xPSetLabelName = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, labelNameModel );

        xPSetLabelName.setPropertyValue( "PositionX", new Integer( 5 ) );
        xPSetLabelName.setPropertyValue( "PositionY", new Integer( LBOX_TOP - 13 ) );
        xPSetLabelName.setPropertyValue( "Width", new Integer( LBOX_WIDTH ) );
        xPSetLabelName.setPropertyValue( "Height", new Integer( 13 ) );
        xPSetLabelName.setPropertyValue( "Align", new Short( (short) 0 ) );
        xPSetLabelName.setPropertyValue( "Name", mLabelName );
        xPSetLabelName.setPropertyValue( "TabIndex", new Short( (short) 99 ) );
        xPSetLabelName.setPropertyValue( "Label", new String( "Options" ) );


        // Create the list model and set the properties
        mLog.log( Level.DEBUG, "Create the list model and set the properties" );
        Object listModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlListBoxModel" );
        XPropertySet xPSetList = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, listModel );

        xPSetList.setPropertyValue( "PositionX", new Integer( 5 ) );
        xPSetList.setPropertyValue( "PositionY", new Integer( LBOX_TOP ) );
        xPSetList.setPropertyValue( "Width", new Integer( LBOX_WIDTH ) );
        xPSetList.setPropertyValue( "Height", new Integer( LBOX_HEIGHT ) );
        xPSetList.setPropertyValue( "Name", mListName );
        FontDescriptor f = new FontDescriptor();
        //f.Name = new String("Verdana");
        xPSetList.setPropertyValue( "FontDescriptor", f );
        xPSetList.setPropertyValue( "TabIndex", new Short( (short) 3 ) );



        /*
         * Status label
         */
        Object labelStatusModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel" );
        XPropertySet xPSetStatusLabel = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, labelStatusModel );
        xPSetStatusLabel.setPropertyValue( "PositionX", new Integer( 5 ) );
        xPSetStatusLabel.setPropertyValue( "PositionY", new Integer( LBOX_TOP + LBOX_HEIGHT + 8 ) );
        xPSetStatusLabel.setPropertyValue( "Width", new Integer( 65 ) );
        xPSetStatusLabel.setPropertyValue( "Height", new Integer( 10 ) );
        // xPSetStatusLabel.setPropertyValue("Align", new Short((short)0));
        xPSetStatusLabel.setPropertyValue( "Name", mLabelStatus );
        xPSetStatusLabel.setPropertyValue( "TabIndex", new Short( (short) 0 ) );
        xPSetStatusLabel.setPropertyValue( "Label", new String( "" ) );

        /*
         * Info label
         */
        Object labelInfoModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel" );
        XPropertySet xPSetInfoLabel = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, labelInfoModel );
        xPSetInfoLabel.setPropertyValue( "PositionX", new Integer( 5 ) );
        xPSetInfoLabel.setPropertyValue( "PositionY", new Integer( LBOX_TOP - 25 ) );
        xPSetInfoLabel.setPropertyValue( "Width", new Integer( LBOX_WIDTH ) );
        xPSetInfoLabel.setPropertyValue( "Height", new Integer( 10 ) );
        // xPSetInfoLabel.setPropertyValue("Align", new Short((short)0));
        xPSetInfoLabel.setPropertyValue( "Name", mLabelInfo );
        xPSetInfoLabel.setPropertyValue( "TabIndex", new Short( (short) 0 ) );
        xPSetInfoLabel.setPropertyValue( "Label", new String( "Please verify the options for the annotations" ) );




        // Create an OK button model and set the properties
        mLog.log( Level.DEBUG, "Create the OK button model and set the properties" );
        Object OkModel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetOk = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, OkModel );
        xPSetOk.setPropertyValue( "PositionX", new Integer( 5 + LBOX_WIDTH - 42 ) );
        xPSetOk.setPropertyValue( "PositionY", new Integer( LBOX_TOP + LBOX_HEIGHT + 4 ) );
        xPSetOk.setPropertyValue( "Width", new Integer( 42 ) );
        xPSetOk.setPropertyValue( "Height", new Integer( 14 ) );
        xPSetOk.setPropertyValue( "Name", mBtnOKName );
        xPSetOk.setPropertyValue( "TabIndex", new Short( (short) 5 ) );
        // xPSetClose.setPropertyValue("PushButtonType", new Short((short)2));
        xPSetOk.setPropertyValue( "Label", new String( "OK" ) );


        // Insert the control models into the dialog model
        mLog.log( Level.DEBUG, "Insert the control models into the dialog model" );
        XNameContainer xNameCont = (XNameContainer) UnoRuntime.queryInterface(
                XNameContainer.class, dialogModel );
        xNameCont.insertByName( mLabelInfo, labelInfoModel );
        xNameCont.insertByName( mLabelName, labelNameModel );
        // xNameCont.insertByName(_labelDescription, labelDescriptionModel);
        xNameCont.insertByName( mListName, listModel );
        xNameCont.insertByName( mLabelStatus, labelStatusModel );
        xNameCont.insertByName( mBtnOKName, OkModel );
        // xNameCont.insertByName( mChkBxName, CheckBoxModel );


        // Create the dialog control and set the model
        mLog.log( Level.DEBUG, "Create the dialog control and set the model" );
        Object dialog = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.UnoControlDialog", mxComponentContext );
        XControl xControl = (XControl) UnoRuntime.queryInterface(
                XControl.class, dialog );
        XControlModel xControlModel = (XControlModel) UnoRuntime.queryInterface(
                XControlModel.class, dialogModel );
        xControl.setModel( xControlModel );

        // Events...
        XControlContainer xControlCont = (XControlContainer) UnoRuntime.queryInterface(
                XControlContainer.class, dialog );



        // TODO: Create new JFRAME instead of oo-window
        //createSettings( xControlCont );
        

/*
        // Add an action listener to the OK button control
        mLog.log( Level.DEBUG, "Add an action listener to the OK button control" );
        Object objectClose = xControlCont.getControl( mBtnOKName );
        XButton xClose = (XButton) UnoRuntime.queryInterface( XButton.class, objectClose );
        xClose.addActionListener( new OnOkClick( xControlCont ) );

        // Create a peer
        mLog.log( Level.DEBUG, "Create a peer" );
        Object toolkit = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.ExtToolkit", mxComponentContext );
        XToolkit xToolkit = (XToolkit) UnoRuntime.queryInterface(
                XToolkit.class, toolkit );
        XWindow xWindow = (XWindow) UnoRuntime.queryInterface(
                XWindow.class, xControl );
        xWindow.setVisible( false );
        xControl.createPeer( xToolkit, null );

        // Execute the dialog
        mLog.log( Level.DEBUG, "Execute the dialog" );
        mxDialog = (XDialog) UnoRuntime.queryInterface(
                XDialog.class, dialog );
        mxDialog.execute();

        // Dispose the dialog
        mLog.log( Level.DEBUG, "Dispose the dialog" );
        XComponent xComponent = (XComponent) UnoRuntime.queryInterface(
                XComponent.class, dialog );
        xComponent.dispose();*/

    }



/*
    private void createSettings()
    {
        // Update the list box
        mGloablSettingsFrame = new GlobalSettingsFrame();

        mGloablSettingsFrame.removeItems( (short) 0, mGloablSettingsFrame.getItemCount() );
        mGloablSettingsFrame.addItem( CURRENT_HIGHLIGHT, (short) 0 );
        mGloablSettingsFrame.addItem( CURRENT_SERVER_IP, (short) 1);
        // Scroll to top
        mGloablSettingsFrame.makeVisible( (short) 0 );
        // Enable widgets
        ((XWindow) UnoRuntime.queryInterface(
                XWindow.class, xControlCont.getControl( mListName ) )).setEnable( true );

        mGloablSettingsFrame.addActionListener( new OnSelectList( xControlCont ) );
    }

    public class OnSelectList implements XActionListener
    {

        short mSelPosition;
        XControlContainer mxControlCont;

        private OnSelectList( XControlContainer xControlCont )
        {
            mxControlCont = xControlCont;
        }

        @Override
        public void actionPerformed( ActionEvent arg0 )
        {
            mLog.log( Level.DEBUG, "Pos selected: " + mGloablSettingsFrame.getSelectedItemPos() );
            mSelPosition = mGloablSettingsFrame.getSelectedItemPos();

            switch( mSelPosition )
            {
                case 0:
                    ToggleHighlight();
                    mGloablSettingsFrame.removeItems( (short) 0, mGloablSettingsFrame.getItemCount() );
                    mGloablSettingsFrame.addItem( CURRENT_HIGHLIGHT, (short) 0 );
                    break;

                case 1:
                    // TODO: create Dialog for params (NPK)
                    break;
            }
        }

        @Override
        public void disposing( EventObject arg0 )
        {
        }

        private void ToggleHighlight()
        {
            if( CURRENT_HIGHLIGHT.equals( HIGHLIGHT_ON ) )
            {
                CURRENT_HIGHLIGHT = new String( HIGHLIGHT_OFF );
                System.out.println( "--------------- toggling CURRENT_HIGHLIGHT" + CURRENT_HIGHLIGHT );
                UNOUtils.DisableHighlighting();

            }
            else
            {
                CURRENT_HIGHLIGHT = new String( HIGHLIGHT_ON );
                System.out.println( "--------------- toggling CURRENT_HIGHLIGHT" + CURRENT_HIGHLIGHT );
                UNOUtils.EnableHighlighting();
            }
        }
    }

    public class OnOkClick implements XActionListener
    {

        XControlContainer mxControlCont;

        public OnOkClick( XControlContainer xControlCont )
        {
            mxControlCont = xControlCont;
        }

        // XEventListener
        @Override
        public void disposing( EventObject eventObject )
        {
            mxControlCont = null;
        }

        // XActionListener
        @Override
        public void actionPerformed( ActionEvent actionEvent )
        {
            /*
            // Find out which service is selected and save this
            // information to the settings class
            XListBox x = (XListBox) UnoRuntime.queryInterface(
            XListBox.class, mxControlCont.getControl( mListName ) );

            short pos = x.getSelectedItemPos();

            if( pos >= 0 )
            {
            Settings.setSelectedServiceName( serviceNames.get( pos ) );
            mLog.debug( "Saving service name \"" + (serviceNames.get( pos )) + "\" to Settings" );
            }
            else
            {
            mLog.debug( "pos is negative" );
            Settings.setSelectedServiceName( null );
            }

            mxDialog.endExecute();
        }

    }
*/
}




