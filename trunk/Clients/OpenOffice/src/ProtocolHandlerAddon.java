/* Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

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

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import javax.swing.*;
import java.awt.event.*;

import info.semanticsoftware.semassist.server.*;
import info.semanticsoftware.semassist.csal.*;
import info.semanticsoftware.semassist.csal.RTParamFrame;
import info.semanticsoftware.semassist.client.openoffice.*;
import info.semanticsoftware.semassist.client.openoffice.utils.*;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;


import com.sun.star.awt.Rectangle;
import com.sun.star.awt.WindowAttribute;
import com.sun.star.awt.WindowClass;
import com.sun.star.awt.WindowDescriptor;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.frame.DispatchDescriptor;
import com.sun.star.frame.XDispatch;
import com.sun.star.frame.XDispatchProvider;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XStatusListener;


//import com.sun.star.lang.XInitialization;
import com.sun.star.lang.XInitialization;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import com.sun.star.frame.XModel;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextRange;
import com.sun.star.text.XText;

import org.apache.commons.lang.StringUtils;

public class ProtocolHandlerAddon
{

    /** This class implements the component. At least the interfaces XServiceInfo,
     * XTypeProvider, and XInitialization should be provided by the service.
     */
    protected static SettingsFrame mSettingsFrame = null;
    protected static JFrame mparamFrame = null;

    public static class ProtocolHandlerAddonImpl extends WeakBase implements
            XDispatchProvider,
            XDispatch,
            XInitialization,
            XServiceInfo
    {

        private static Logger log = Logger.getLogger( ProtocolHandlerAddonImpl.class );
        /** The service name, that must be used to get an instance of this service.
         */
        static private final String[] mServiceNames =
        {
            "com.sun.star.frame.ProtocolHandler"
        };
        /** The component context, that gives access to the service manager and all registered services.
         */
        private XComponentContext mxCmpCtx;
        /** The toolkit, that we can create UNO dialogs.
         */
        private static XToolkit mxToolkit;
        /** 
         * The frame the addon depends on.
         */
        private static XFrame mxFrame;
        private static final String SEMASSIST_PROTOCOL = "info.semanticsoftware.semassist.client.openoffice:";

        /** The constructor of the inner class has a XMultiServiceFactory parameter.
         * @param xmultiservicefactoryInitialization A special service factory
         * could be introduced while initializing.
         */
        public ProtocolHandlerAddonImpl( XComponentContext xComponentContext )
        {
            mxCmpCtx = xComponentContext;
        }

        /** This method is a member of the interface for initializing an object
         * directly after its creation.
         * @param object This array of arbitrary objects will be passed to the
         * component after its creation.
         * @throws Exception Every exception will not be handled, but will be
         * passed to the caller.
         */
        @Override
        public void initialize( Object[] object )
                throws com.sun.star.uno.Exception
        {

            if( object.length > 0 )
            {
                mxFrame = (XFrame) UnoRuntime.queryInterface(
                        XFrame.class, object[0] );
            }

            // Create the toolkit to have access to it later
            mxToolkit = (XToolkit) UnoRuntime.queryInterface(
                    XToolkit.class,
                    mxCmpCtx.getServiceManager().createInstanceWithContext( "com.sun.star.awt.Toolkit",
                    mxCmpCtx ) );

            // Configure log4j to print to the console
            BasicConfigurator.configure();
        }

        /** This method returns an array of all supported service names.
         * @return Array of supported service names.
         */
        @Override
        public String[] getSupportedServiceNames()
        {
            return getServiceNames();
        }

        public static String[] getServiceNames()
        {
            return mServiceNames;
        }

        /** This method returns true, if the given service will be
         * supported by the component.
         * @param stringService Service name.
         * @return True, if the given service name will be supported.
         */
        @Override
        public boolean supportsService( String sService )
        {
            int len = mServiceNames.length;

            for( int i = 0; i < len; i++ )
            {
                if( sService.equals( mServiceNames[i] ) )
                {
                    return true;
                }
            }

            return false;
        }

        /** Return the class name of the component.
         * @return Class name of the component.
         */
        @Override
        public String getImplementationName()
        {
            return ProtocolHandlerAddonImpl.class.getName();
        }

        // XDispatchProvider
        @Override
        public XDispatch queryDispatch( com.sun.star.util.URL aURL,
                                        String sTargetFrameName,
                                        int iSearchFlags )
        {

            XDispatch xRet = null;
            if( aURL.Protocol.compareTo( SEMASSIST_PROTOCOL ) == 0 )
            {
                if( aURL.Path.compareTo( "ListAll" ) == 0 )
                {
                    xRet = this;
                }
                if( aURL.Path.compareTo( "Settings" ) == 0 )
                {
                    xRet = this;
                }
            }
            return xRet;
        }

        @Override
        public XDispatch[] queryDispatches( DispatchDescriptor[] seqDescripts )
        {
            int nCount = seqDescripts.length;
            XDispatch[] lDispatcher = new XDispatch[nCount];

            for( int i = 0; i < nCount; ++i )
            {
                lDispatcher[i] = queryDispatch( seqDescripts[i].FeatureURL,
                        seqDescripts[i].FrameName,
                        seqDescripts[i].SearchFlags );
            }

            return lDispatcher;
        }

        // XDispatch
        @Override
        public void dispatch( com.sun.star.util.URL aURL,
                              com.sun.star.beans.PropertyValue[] aArguments )
        {
            
            if( aURL.Protocol.compareTo( SEMASSIST_PROTOCOL ) == 0 )
            {
                if( aURL.Path.compareTo( "ListAll" ) == 0 )
                {
                    ListServicesDialog s = null;
                    try
                    {
                        s = new ListServicesDialog( mxCmpCtx );
                        s.createDialog();

                        // invoke selected service
                        invokeService();
                    }
                    catch( NumberFormatException e )
                    {
                        e.printStackTrace();
                    }
                    catch( IOException e )
                    {
                        e.printStackTrace();
                    }
                    catch( Exception e )
                    {
                        e.printStackTrace();
                    }
                }
                else if( aURL.Path.compareTo( "Settings" ) == 0 )
                {
                    createSettingsFrame();
                }
            }
        }

        private void invokeService()
        {
            String serviceName = Settings.getSelectedServiceName();
            if( serviceName == null || serviceName.equals( "" ) )
            {
                //showMessageBox( "Invoke text assistant", "Please select a text assistant before you invoke it. " );
            }
            else if( serviceName.equals( "CancelBtn" )
                    ||  serviceName.equals( "Exception" ) )

            {
                // cancel btn pressed or exception occured
                return;
            }
            else
            {
                runSelectedService();
            }
        }

        private void createSettingsFrame()
        {
            // prevent recreation of dialogs
            if( mSettingsFrame != null )
            {
                if( !mSettingsFrame.isVisible() )
                {
                    mSettingsFrame = null;
                }
                else
                {
                    return;
                }
            }

            mSettingsFrame = new SettingsFrame();
            mSettingsFrame.setVisible( true );
        }

        @Override
        public void addStatusListener( XStatusListener xControl,
                                       com.sun.star.util.URL aURL )
        {
        }

        @Override
        public void removeStatusListener( XStatusListener xControl,
                                          com.sun.star.util.URL aURL )
        {
        }

        public void showMessageBox( String sTitle, String sMessage )
        {
            GUIUtils.showMessageBox( mxCmpCtx, mxFrame, sTitle, sMessage );
        }

        /**
         * Checks if there are any runtime parameters to pass
         * to the selected service. If yes, this function brings
         * up a window where these parameters can be set, and
         * then invokes the service. If no, it invokes the service
         * right away.
         */
        private void runSelectedService()
        {
            try
            {
                String serviceName = Settings.getSelectedServiceName();
                ServiceInfoForClient info = Settings.getAvailableServices().get( serviceName );
                List<GateRuntimeParameter> params = info.getParams();

                if( params.iterator().hasNext() )
                {
                    // avoid recreation
                    if( mparamFrame != null )
                    {
                        if( !mparamFrame.isVisible() )
                        {
                            mparamFrame = null;
                        }
                        else
                        {
                            return;
                        }
                    }

                    // There are runtime parameters to take care of
                    mparamFrame = buildRTParamFrame( info, params );
                    mparamFrame.pack();
                    mparamFrame.setLocation( 430, 430 );
                    mparamFrame.setVisible( true );
                }
                else
                {
                    doRunSelectedService( new GateRuntimeParameterArray() );
                }

            }
            catch( RuntimeException re )
            {
                showMessageBox( "Text Selection",
                        "Please select the part of the text you would like to call the service" +
                        " on \nor place the cursor within the text for the whole document to be processed " );
            }

        }

        /**
         * Should not be called directly. Call <code>runSelectedService</code>
         * instead, or runtime parameters will not be taken into account.
         */
        private void doRunSelectedService( GateRuntimeParameterArray rtpArray )
        {
            String arg = UNOUtils.getArgumentText( mxCmpCtx );

            if( StringUtils.isBlank( arg ) )
            {
                showMessageBox( "Empty Document", "No text in the document" );
                return;
            }



            ServiceInvocationHandler handler = new ServiceInvocationHandler( mxCmpCtx );
            handler.setServiceName( Settings.getSelectedServiceName() );
            handler.setArgumentText( arg );


            if( rtpArray != null )
            {
                handler.setRuntimeParameters( rtpArray );
            }

            handler.start();
        }

        private JFrame buildRTParamFrame( ServiceInfoForClient info, List<GateRuntimeParameter> params )
        {

            Vector<GateRuntimeParameter> mandatory = new Vector<GateRuntimeParameter>();
            Vector<GateRuntimeParameter> optional = new Vector<GateRuntimeParameter>();

            for( Iterator<GateRuntimeParameter> it = params.iterator(); it.hasNext(); )
            {
                GateRuntimeParameter p = it.next();
                if( p.isOptional() )
                {
                    optional.add( p );
                }
                else
                {
                    mandatory.add( p );
                }
            }

            // Show window for parameter settings
            RTParamFrame frame = new RTParamFrame( info );
            frame.setOkActionListener( new ParamActionListener( frame ) );
            frame.setMandatories( mandatory );
            frame.setOptionals( optional );


            return frame;
        }

        private class ParamActionListener implements ActionListener
        {

            private RTParamFrame frame = null;

            public ParamActionListener( RTParamFrame f )
            {
                frame = f;
            }

            @Override
            public void actionPerformed( ActionEvent e )
            {
                GateRuntimeParameterArray params = frame.getParams();
                System.out.println( "------ Retrieved params array from the frame: " );
                List<GateRuntimeParameter> list = params.getItem();
                Iterator<GateRuntimeParameter> it = list.iterator();

                while( it.hasNext() )
                {
                    GateRuntimeParameter p = it.next();
                    System.out.println( "------   Parameter: " + p.getParamName() );
                }


                frame = null;
                doRunSelectedService( params );
            }

        }

    } // End of inner class

    /** Gives a factory for creating the service.
     * This method is called by the <code>JavaLoader</code>
     * <p>
     * @return Returns a <code>XSingleServiceFactory</code> for creating the
     * component.
     * @see com.sun.star.comp.loader.JavaLoader#
     * @param stringImplementationName The implementation name of the component.
     * @param xmultiservicefactory The service manager, who gives access to every
     * known service.
     * @param xregistrykey Makes structural information (except regarding tree
     * structures) of a single
     * registry key accessible.
     */
    public static XSingleComponentFactory __getComponentFactory( String sImplementationName )
    {
        XSingleComponentFactory xFactory = null;

        if( sImplementationName.equals( ProtocolHandlerAddonImpl.class.getName() ) )
        {
            xFactory = Factory.createComponentFactory( ProtocolHandlerAddonImpl.class, ProtocolHandlerAddonImpl.getServiceNames() );
        }

        return xFactory;
    }

    /** Writes the service information into the given registry key.
     * This method is called by the <code>JavaLoader</code>.
     * @return returns true if the operation succeeded
     * @see com.sun.star.comp.loader.JavaLoader#
     * @see com.sun.star.lib.uno.helper.Factory#
     * @param xregistrykey Makes structural information (except regarding tree
     * structures) of a single
     * registry key accessible.
     */
    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey )
    {
        return Factory.writeRegistryServiceInfo(
                ProtocolHandlerAddonImpl.class.getName(),
                ProtocolHandlerAddonImpl.getServiceNames(),
                xRegistryKey );
    }

}
