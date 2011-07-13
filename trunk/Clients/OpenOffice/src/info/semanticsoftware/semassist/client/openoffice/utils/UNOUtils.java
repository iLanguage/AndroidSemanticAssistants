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
package info.semanticsoftware.semassist.client.openoffice.utils;

import java.io.*;

import org.apache.log4j.Logger;


import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XController;
import com.sun.star.frame.XComponentLoader;

import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextRange;
import com.sun.star.text.XText;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;

import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XComponent;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextField;
import com.sun.star.util.XSearchDescriptor;
import com.sun.star.util.XSearchable;
import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.result.Annotation;
import java.util.Iterator;
import java.util.Set;

public class UNOUtils
{

    private static final int HIGHLIGHT_YELLOW = 0x00FFFD00;
    private static final int HIGHLIGHT_OFF = 0xFFFFFF0A;
    private static int CURRENT_HIGHLIGHT = HIGHLIGHT_YELLOW;
    private static final String SEM_ASSIST = "Semantic Assistants:";

    // Configuration Defaults
    private static float mSideNoteFontSize = 8; /* Default annotation font-size. */
    private static Boolean mBrowserResultHandling = true; /* Handle results with an external browser. */
    private static Boolean mShowAnnotationContent = false; /* Put annotation content in side-notes. */
    private static Boolean mEmptyFeatureFilter = true; /* Ignore empty-valued features by default. */
    private static String mServerHost = ClientUtils.defaultServerHost();
    private static String mServerPort = ClientUtils.defaultServerPort();

    private static Logger mLogger = Logger.getLogger( GUIUtils.class );
    private static XMultiServiceFactory mxDocFactory = null;
    private static XTextCursor mxDocCursor = null;
    private static XSearchDescriptor mxSearchDescr = null;
    private static XSearchable mxSearchable = null;
    private static XText mxAnnotText = null;
    private static String mCurrentPipeline;
    private static boolean mServerInfoChanged;

    /**
     * Retrieves either the marked text of the current document
     * or, if nothing is marked, the whole text of the document.
     */
    public static String getArgumentText( XComponentContext ctx )
    {
        // Get the XModel interface from the active document,
        // and its controller
        XModel xModel = getActiveDocumentModel( ctx );
        XController xController = xModel.getCurrentController();

        // The controller gives us the TextViewCursor
        // Query the viewcursor supplier interface
        XTextViewCursorSupplier xViewCursorSupplier =
                                UnoRuntime.queryInterface(
                XTextViewCursorSupplier.class, xController );

        // Get the cursor
        XTextViewCursor xViewCursor = xViewCursorSupplier.getViewCursor();
        String result = xViewCursor.getString();

        if( result == null || result.equals( "" ) )
        {
            final XTextDocument xDoc = UnoRuntime.queryInterface( XTextDocument.class, xModel );
            XText wholeText = xDoc.getText();
            return wholeText.getString();
        }
        else
        {
            return result;
        }

    }

    public static void createNewDoc( XComponentContext ctx )
    {
        createNewDoc( ctx, "" );
    }

    public static void createNewDoc( XComponentContext ctx, String text )
    {

        // Get an empty text document
        XTextDocument doc = createTextDocument( getDesktop( ctx ) );
        XText docText = doc.getText();
        int endIndex = text.length();

        if( text.length() > 10000 )
        {
            endIndex = 10000;
        }


        docText.setString( text.substring( 0, endIndex ) );

    }

    public static XComponent createNewDoc( XComponentContext ctx, File f )
    {
        // Query the XComponentLoader interface from the Desktop service
        final XComponentLoader xComponentLoader = UnoRuntime.queryInterface(
                XComponentLoader.class, getDesktop( ctx ) );

        PropertyValue[] loadProps = new PropertyValue[0];
        /*
        PropertyValue[] loadProps = new PropertyValue[1];
        loadProps[0] = new PropertyValue();
        loadProps[0].Name = "Hidden";
        loadProps[0].Value = new Boolean(true);
         */

        // Load
        String url = "";
        try
        {
            url = "file://" + f.getCanonicalPath();
        }
        catch( Exception e )
        {
            //e.printStackTrace();
        }

        try
        {
            System.out.println( "--------------- File URL: " + url );
            return xComponentLoader.loadComponentFromURL( url, "_blank", 0, loadProps );
        }
        catch( com.sun.star.io.IOException e )
        {
            e.printStackTrace();
        }
        catch( com.sun.star.lang.IllegalArgumentException e )
        {
            e.printStackTrace();
        }


        return null;
    }

    public static void createDocAnnotations( XComponentContext ctx, Annotation annotation )
    {
        // get the active document
        XTextDocument doc = getActiveTextDocument( ctx );

        mxDocFactory = UnoRuntime.queryInterface(
                XMultiServiceFactory.class, doc );

        createInvisibleCursor( ctx, annotation );
    }

    public static void enableHighlighting()
    {
        CURRENT_HIGHLIGHT = HIGHLIGHT_YELLOW;
        System.out.println( "---------------- Enable highlight: " + getCURRENT_HIGHLIGHT() );

    }

    public static void disableHighlighting()
    {
        CURRENT_HIGHLIGHT = HIGHLIGHT_OFF;
        System.out.println( "---------------- Disbale highlight: " + getCURRENT_HIGHLIGHT() );
    }

    public static void initializeCursor( XComponentContext ctx )
    {
        // get the active document
        XTextDocument doc = getActiveTextDocument( ctx );

        mxDocFactory = UnoRuntime.queryInterface(
                XMultiServiceFactory.class, doc );

        mxSearchable = UnoRuntime.queryInterface( XSearchable.class, doc );

        mxSearchDescr = mxSearchable.createSearchDescriptor();
    }

    public static short getActiveDocCharCount( XComponentContext ctx )
    {

        // get the active document
        XTextDocument doc = getActiveTextDocument( ctx );
        XText docText = doc.getText();

        return (short) docText.getString().length();

    }

    /**
     * @return the mCurrentPipeline
     */
    public static String getCurrentPipeline()
    {
        return mCurrentPipeline;
    }

    /**
     * @param aMCurrentPipeline the mCurrentPipeline to set
     */
    public static void setCurrentPipeline( String aMCurrentPipeline )
    {
        mCurrentPipeline = aMCurrentPipeline;
    }

    private static XTextDocument createTextDocument( XDesktop xDesktop )
    {
        XTextDocument aTextDocument = null;

        try
        {
            XComponent xComponent = createNewDocument( xDesktop, "swriter" );
            aTextDocument = UnoRuntime.queryInterface(
                    com.sun.star.text.XTextDocument.class, xComponent );
        }
        catch( Exception e )
        {
            e.printStackTrace( System.err );
        }

        return aTextDocument;
    }

    /**
     * Get the currently active text document
     */
    private static XTextDocument getActiveTextDocument( XComponentContext ctx )
    {
        XModel xDocModel = getActiveDocumentModel( ctx );
        return UnoRuntime.queryInterface( XTextDocument.class, xDocModel );
    }

    private static XModel getActiveDocumentModel( XComponentContext ctx )
    {
        XComponent document = getCurrentComponent( ctx );

        return UnoRuntime.queryInterface( XModel.class, document );
    }

    private static XComponent getCurrentComponent( XComponentContext ctx )
    {
        XDesktop xDesktop = getDesktop( ctx );
        XComponent document = xDesktop.getCurrentComponent();

        return document;
    }

    /**
     * Get the desktop service
     */
    private static XDesktop getDesktop( XComponentContext ctx )
    {
        XMultiComponentFactory xmcf = ctx.getServiceManager();
        Object desktop = null;
        try
        {
            desktop = xmcf.createInstanceWithContext( "com.sun.star.frame.Desktop", ctx );
        }
        catch( com.sun.star.uno.Exception e )
        {
        }
        return UnoRuntime.queryInterface( com.sun.star.frame.XDesktop.class, desktop );
    }

    private static XComponent createNewDocument( XDesktop xDesktop, String sDocumentType )
    {
        String sURL = "private:factory/" + sDocumentType;
        XComponent xComponent = null;

        XComponentLoader xComponentLoader = null;
        PropertyValue xValues[] = new PropertyValue[1];
        PropertyValue xEmptyArgs[] = new PropertyValue[0];

        try
        {
            xComponentLoader = UnoRuntime.queryInterface(
                    XComponentLoader.class, xDesktop );

            xComponent = xComponentLoader.loadComponentFromURL( sURL, "_blank", 0, xEmptyArgs );
        }
        catch( Exception e )
        {
            e.printStackTrace( System.err );
        }

        return xComponent;
    }

    private static void annotateField( Annotation annotation )
    {
        try {
            mxAnnotText.insertTextContent(mxDocCursor, makeAnnotation(annotation), false);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Highlight annotated field
        highlightField();
    }

    /**
     * Create an annotation (side-note) textfield object.
     *
     * @param Annotation Memory representation of the annotation.
     *
     * @throws Exception
     */
    private static XTextField makeAnnotation(final Annotation annotation)
      throws Exception
    {
      // Create a side-note object & get its properties to be modified.
      final XTextField annot = UnoRuntime.queryInterface(
         XTextField.class, mxDocFactory.createInstance(
         "com.sun.star.text.TextField.Annotation"));
      final XPropertySet props = UnoRuntime.queryInterface(XPropertySet.class, annot);

      // Keep track of side-note text & content with the same information but for
      // different purposes. Text is for embedding complex objects into side-notes,
      // while content is for side-note mandatory properties. Should investigate
      // how to leverage of OpenOffice's (unknown) internal defaults to not have
      // multiple representation of the same information.
      final XText text = UnoRuntime.queryInterface(
         XText.class, props.getPropertyValue("TextRange"));
      String content = "";

      // Configure look-&-feel of side-note information.
      setFontSize(text, getSideNoteFontSize());

      // If configured, duplicate annotation content as part of the side-note.
      if (mShowAnnotationContent) {
         content += "content= "+ annotation.mContent +"\n";
      }

      // Iterate through annotation features.
      final Set<String> keys = annotation.mFeatures.keySet();
      final Iterator<String> iter = keys.iterator();

      while (iter.hasNext()) {
         final String key = iter.next();
         final String val = annotation.mFeatures.get(key);

         // If configured, suppress empty-valued features.
         if (mEmptyFeatureFilter && "".equals(val)) {
            System.out.println("---------------- Ignoring empty valued feature: "+ key);
            continue;
         }

         // Make URL features hyper-linkable.
         if ("URL".equals(key)) {
            text.insertString(text, key +"= ", false);
            text.insertTextContent(text, makeHyperLink(val, val), false);
            text.insertString(text, "\n", false);
         } else {
            text.insertString(text, key +"= "+ val +"\n", false);
         }
      }

      // Define side-note properties.
      try {
         props.setPropertyValue("Content", content);
         props.setPropertyValue("Author", mCurrentPipeline + SEM_ASSIST);
      } catch (UnknownPropertyException e) {
         /* Thrown ONLY on programming/typo error of the try body! */
         e.printStackTrace();
      }

      return annot;
    }

    /**
     * Create a hyperlink textfield object.
     *
     * @param linkURL Address of the hyperlink including its protocol (ie: http://)
     * @param linkName Symbolic name of the hyperlink.
     *
     * @throws Exception
     */
    private static XTextField makeHyperLink(final String linkURL, final String linkName)
      throws Exception
    {
      //Create a URL object & gets its properties to be modified.
      final XTextField link = UnoRuntime.queryInterface(
         XTextField.class, mxDocFactory.createInstance(
         "com.sun.star.text.TextField.URL"));
      final XPropertySet props = UnoRuntime.queryInterface(XPropertySet.class, link);

      // Define URL properties.
      try {
         props.setPropertyValue("Representation", linkName);
         props.setPropertyValue("URL", linkURL);
      } catch (UnknownPropertyException e) {
         /* Thrown ONLY on programming/typo error of the try body! */
         e.printStackTrace();
      }

      return link;
    }

    /**
     * Configure the font-size of a range of text.
     *
     * @param range of text to change the font.
     * @param size value of the new font.
     */
    private static void setFontSize(XTextRange range, float size)
    {
        // Extract the cursor properties & change its font-size.
        final XTextCursor cursor = range.getText().createTextCursorByRange(range);
        final XPropertySet props = UnoRuntime.queryInterface(XPropertySet.class, cursor);
        try {
            props.setPropertyValue("CharHeight", size);
        } catch (UnknownPropertyException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (com.sun.star.lang.WrappedTargetException e) {
            e.printStackTrace();
        } catch (com.sun.star.lang.IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static void highlightField()
    {
        try
        {
            // Highlight text to yellow
            // call setPropertyValue, passing in a Float object
            // query the XPropertySet interface


            final XPropertySet xCursorProps = UnoRuntime.queryInterface( XPropertySet.class, mxDocCursor );
            xCursorProps.setPropertyValue( "CharBackColor", CURRENT_HIGHLIGHT );

            mxDocCursor.gotoRange( mxDocCursor.getEnd(), false );
            mxDocCursor.goRight( (short) 1, true );
            xCursorProps.setPropertyValue( "CharBackColor", 0xFFFFFF1A );
            //mxDocCursor.goLeft( (short) 1, false );

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

    }

    private static boolean IsTextAnnotated()
    {
        try
        {
            /*
            Hashtable textFields;
            final XTextFieldsSupplier supplier = UnoRuntime.queryInterface( XTextFieldsSupplier.class, xTextDoc );
            XEnumerationAccess enumAccess = supplier.getTextFields();
            XEnumeration xEnum = enumAccess.createEnumeration();
            textFields = new Hashtable();

            while( xEnum.hasMoreElements() )
            {
            Object o = xEnum.nextElement();
            final XTextField text = UnoRuntime.queryInterface( XTextField.class, o );
            // text.getAnchor()
            //System.out.println( "---------------- XTextField text: " + text.getAnchor() );

            }
             */


            final XPropertySet xCursorProps = UnoRuntime.queryInterface( XPropertySet.class, mxDocCursor );

            Object property = xCursorProps.getPropertyValue( "CharBackColor" );


            boolean result = (property.equals( HIGHLIGHT_OFF ) || property.equals( HIGHLIGHT_YELLOW ));

            if( result )
            {
                if( !property.equals( CURRENT_HIGHLIGHT ) )
                {
                    highlightField();
                }
            }

            System.out.println( "---------------- result: " + result );

            return result;

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return false;
    }

    private static void createInvisibleCursor( XComponentContext ctx, Annotation annotation )
    {
        // get the active document
        XTextDocument doc = getActiveTextDocument( ctx );
        XText docText = doc.getText();
        boolean isMoreElements = true;

        try
        {
            // For plain text
            mxSearchDescr.setSearchString( annotation.mContent );
            mxSearchDescr.setPropertyValue( "SearchWords", new Boolean( true ) );

            System.out.println( "---------------- Text to be searched: " + annotation.mContent );


            Object xTextRange = mxSearchable.findFirst( mxSearchDescr );
            XTextRange xTagTxtRange = UnoRuntime.queryInterface( XTextRange.class, xTextRange );

            while( true )
            {
                // find initial range for the searched word 
                try
                {
                    mxAnnotText = xTagTxtRange.getText();
                    mxDocCursor = mxAnnotText.createTextCursor();
                    mxDocCursor.gotoRange( xTagTxtRange, false );
                    mxDocCursor.gotoRange( xTagTxtRange, true );

                    break;
                }
                catch( RuntimeException re )
                {
                    System.out.println( "---------------- non plain text, go to next range1" );
                    try
                    {
                        xTextRange = mxSearchable.findNext( xTextRange, mxSearchDescr );
                        xTagTxtRange = UnoRuntime.queryInterface( XTextRange.class, xTextRange );
                    }
                    catch( RuntimeException runtimeException )
                    {
                        System.out.println( "---------------- No more elements" );
                        isMoreElements = false;
                        break;
                    }
                }
            }

            while( IsTextAnnotated() && isMoreElements )
            {

                if( (xTextRange = mxSearchable.findNext( xTextRange, mxSearchDescr )) == null )
                {
                    System.out.println( "---------------- No more elements" );
                    isMoreElements = false;
                    break;
                }

                xTagTxtRange = UnoRuntime.queryInterface( XTextRange.class, xTextRange );

                if( xTagTxtRange == null )
                {
                    System.out.println( "---------------- Null tagTxtRange " );
                }
                // go to start, the to the stat of the range and then expand accordingly
                try
                {
                    mxAnnotText = xTagTxtRange.getText();
                    mxDocCursor = mxAnnotText.createTextCursor();
                    mxDocCursor.gotoRange( xTagTxtRange, false );
                    mxDocCursor.gotoRange( xTagTxtRange, true );
                }
                catch( RuntimeException runExc )
                {
                    System.out.println( "---------------- non plain text, go to next range2" );
                }

            }

            if( isMoreElements )
            {
                // Annotate text field
                annotateField( annotation );
            }

        }
        catch( Exception e )
        {
            System.out.println( "---------------- Exception in plain text searching" );
            e.printStackTrace();
            return;
        }

    }

    /**
     * @return the CURRENT_HIGHLIGHT
     */
    public static boolean getCURRENT_HIGHLIGHT()
    {
        if( CURRENT_HIGHLIGHT == HIGHLIGHT_YELLOW )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

   /**
    * @param size value of the side-note font.
    */
   public static void setSideNoteFontSize( float size )
   {
      mSideNoteFontSize = size;
   }

   /**
    * @return the font-size for side-note content.
    */
   public static float getSideNoteFontSize()
   {
      return mSideNoteFontSize;
   }

   /**
    * @param status true to allow an external browser to handle the document
    *        results or false otherwise.
    */
   public static void setBrowserResultHandling( boolean status )
   {
      mBrowserResultHandling = status;
   }

   /**
    * @return the mBrowserResultHandling status.
    */
   public static boolean isBrowserResultHandling()
   {
      return mBrowserResultHandling;
   }

   /**
    * @param status true to allow filtering of empty-valued
    *               features or false otherwise.
    */
   public static void setEmptyFeatureFilter( boolean status )
   {
      mEmptyFeatureFilter = status;
   }

   /**
    * @return the mEmptyFeatureFilter status.
    */
   public static boolean isEmptyFeatureFilter()
   {
      return mEmptyFeatureFilter;
   }

   /**
    * @param status true to show annotation content within side-notes,
    *               or false otherwise.
    */
   public static void setShowAnnotationContent( boolean status )
   {
      mShowAnnotationContent = status;
   }

   /**
    * @return the mShowAnnotationContent status.
    */
   public static boolean isShowAnnotationContent()
   {
      return mShowAnnotationContent;
   }


    /**
     * @return the mServerHost
     */
    public static String getServerHost()
    {
        if(mServerHost.isEmpty() )
        {
            mServerHost = ClientUtils.defaultServerHost();
        }

        return mServerHost;
    }

    /**
     * @param amServerHost the mServerHost to set
     */
    public static void setServerHost( String amServerHost )
    {
        if( mServerHost.equals( amServerHost ) )
        {
            return;
        }

        mServerHost = amServerHost;
        mServerInfoChanged = true;
    }

    /**
     * @return the mServerPort
     */
    public static String getServerPort()
    {
        if( mServerPort.isEmpty() )
        {
            mServerPort = ClientUtils.defaultServerPort();
        }

        return mServerPort;
    }

    /**
     * @param amServerPort the mServerPort to set
     */
    public static void setServerPort( String amServerPort )
    {
        mServerPort = amServerPort;
    }

    /**
     * @return the mServerInfoChanged
     */
    public static boolean getServerInfoChanged()
    {
        return mServerInfoChanged;
    }

    /**
     * @param amServerInfoChanged the mServerInfoChanged to set
     */
    public static void setServerInfoChanged( boolean amServerInfoChanged )
    {
        mServerInfoChanged = amServerInfoChanged;
    }

}
