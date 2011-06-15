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

import java.awt.Desktop;
import java.util.*;
import java.io.*;
import java.net.URI;

import info.semanticsoftware.semassist.csal.*;
import net.java.dev.jaxb.array.*;
import info.semanticsoftware.semassist.csal.result.*;
import info.semanticsoftware.semassist.client.openoffice.utils.*;
import info.semanticsoftware.semassist.server.*;


import com.sun.star.uno.XComponentContext;
import javax.swing.JOptionPane;

public class ServiceInvocationHandler implements Runnable
{

    private XComponentContext compCtx;
    private String argumentText = null;
    private String serviceName = null;
    private Thread thread;
    private GateRuntimeParameterArray rtpArray = new GateRuntimeParameterArray();

    public ServiceInvocationHandler( XComponentContext xComponentContext )
    {
        compCtx = xComponentContext;
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
        if( argumentText == null || serviceName == null )
        {
            return;
        }

        System.out.println( "Argument text: " + argumentText );

        SemanticServiceBroker broker = ServiceAgentSingleton.getInstance();
        UriList uriList = new UriList();
        uriList.getUriList().add( new String( "#literal" ) );
        StringArray stringArray = new StringArray();
        stringArray.getItem().add( new String( argumentText ) );

        String serviceResponse = null;
        try
        {
            serviceResponse = broker.invokeService( serviceName, uriList, stringArray, 0L,
                    rtpArray, new UserContext() );
        }
        catch( Exception connEx)
        {
            JOptionPane.showMessageDialog( null, "Server not found. \nPlease check the Server Host and Port and if Server is Online",
                        "Server Offline", JOptionPane.ERROR_MESSAGE );
            return;
        }

            // returns result in sorted by type
            Vector<SemanticServiceResult> results = ClientUtils.getServiceResults( serviceResponse );

            // used for document case
            String DocString = "";
            boolean DocCase = false;

            if( results == null )
            {
                // Open document showing response message
                System.out.println( "---------- No results retrieved in response message" );
                UNOUtils.createNewDoc( compCtx, serviceResponse );
                return;
            }

            // Key is annotation document URL or ID
            HashMap<String, AnnotationVectorArray> annotationsPerDocument =
                                                   new HashMap<String, AnnotationVectorArray>();

            for( Iterator<SemanticServiceResult> it = results.iterator(); it.hasNext(); )
            {
                SemanticServiceResult current = it.next();

                if( current.mResultType.equals( SemanticServiceResult.FILE ) )
                {
                    // File case

                    System.out.println( "------------ Result type: " + SemanticServiceResult.FILE );
                    String fileString = broker.getResultFile( current.mFileUrl );
                    String fileExt = ClientUtils.getFileNameExt( current.mFileUrl );

                    if( fileExt == null )
                    {
                        fileExt = ".txt";
                    }

                    System.out.println( "------------ fileExt: " + fileExt );
                    File f = ClientUtils.writeStringToFile( fileString, fileExt );

                    if ( UNOUtils.isExternalResultHandling() )
                    {
                       // Attempt to open HTML files through an external browser,
                       // else open the file through the default word-processor.
                       if (fileString.startsWith( "<!DOCTYPE HTML" )) {
                           if (!spawnBrowser( f.toURI() )) {
                              System.out.println( "---------------- Defaulting to word-processor handling." );
                              UNOUtils.createNewDoc( compCtx, f );
                           }
                       }
                    }
                    else
                    {
                       // Default word-processor handling.
                       UNOUtils.createNewDoc( compCtx, f );
                    }
                }
                else if( current.mResultType.equals( SemanticServiceResult.ANNOTATION_IN_WHOLE ) )
                {
                    // Annotation case => append to data structure
                    System.out.println( "---------------- Annotation case..." );

                    // Keys are document IDs or URLs
                    HashMap<String, AnnotationVector> map = current.mAnnotations;
                    Set<String> keys = map.keySet();

                    for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); )
                    {
                        String docID = it2.next();

                        if( annotationsPerDocument.get( docID ) == null )
                        {
                            annotationsPerDocument.put( docID, new AnnotationVectorArray() );
                        }

                        AnnotationVectorArray v = annotationsPerDocument.get( docID );
                        v.mAnnotVectorArray.add( map.get( docID ) );
                    }

                    // Assemble annotations string
                    if( annotationsPerDocument.size() > 0 )
                    {
                        System.out.println( "---------------- Creating document with annotation information..." );
                        UNOUtils.createNewDoc( compCtx, getAnnotationsString( annotationsPerDocument ) );
                    }

                }
                else if( current.mResultType.equals( SemanticServiceResult.ANNOTATION ) )
                {
                    // Sidenote case => append to data structure
                    System.out.println( "---------------- Sidenote case..." );

                    // Keys are document IDs or URLs
                    HashMap<String, AnnotationVector> map = current.mAnnotations;
                    Set<String> keys = map.keySet();

                    for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); )
                    {
                        String docID = it2.next();

                        if( annotationsPerDocument.get( docID ) == null )
                        {
                            annotationsPerDocument.put( docID, new AnnotationVectorArray() );
                        }

                        AnnotationVectorArray v = annotationsPerDocument.get( docID );
                        v.mAnnotVectorArray.add( map.get( docID ) );
                    }

                    // Assemble annotations string
                    if( annotationsPerDocument.size() > 0 )
                    {
                        System.out.println( "---------------- Creating side-notes with annotation information..." );
                        getAnnotationsString( annotationsPerDocument );
                    }
                }
                else if( current.mResultType.equals( SemanticServiceResult.CORPUS ) )
                {
                    // Corpus case
                    System.out.println( "---------------- Corpus case..." );
                    Vector<RetrievedDocument> docs = current.mCorpus;
                    StringBuffer sbCorpus = new StringBuffer();
                    sbCorpus.append( "Documents:\n" );
                    int count = 1;

                    for( Iterator<RetrievedDocument> itCorpus = docs.iterator(); itCorpus.hasNext(); )
                    {
                        RetrievedDocument d = itCorpus.next();
                        sbCorpus.append( count + ". " + d.url + "\n" );
                        count++;
                    }

                    UNOUtils.createNewDoc( compCtx, sbCorpus.toString() );
                }
                else if( current.mResultType.equals( SemanticServiceResult.DOCUMENT ) )
                {
                    // Corpus case
                    System.out.println( "---------------- Document case... URL:" + current.mFileUrl );
                    DocCase = true;
                    DocString += current.mFileUrl + "\n";
                }
                // Everything else
                else
                {
                    System.out.println( "---------------- Do not recognize kind of output: " + current.mResultType );
                }

            } // end while (for each result)

            if( DocCase )
            {
                UNOUtils.createNewDoc( compCtx, DocString );
            }

        }

    protected

     String getAnnotationsString( HashMap<String, AnnotationVectorArray> map )
    {
        if( map == null )
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        // The key is annotation document ID (URL or number), the values are
        // annotation instances, basically
        Set<String> keys = map.keySet();


        for( Iterator<String> it = keys.iterator(); it.hasNext(); )
        {
            String docID = it.next();
            sb.append( "Annotations for document " + docID + ":\n\n" );
            AnnotationVectorArray va = map.get( docID );
            sb.append( getAnnotationsString( va ) );
        }


        return sb.toString();
    }

    ///
    @SuppressWarnings( "unchecked" )
    protected String getAnnotationsString( AnnotationVectorArray annotVectorArr )
    {

        StringBuffer strBuffer = new StringBuffer();


        if( annotVectorArr == null )
        {
            return "";
        }


        for( Iterator<AnnotationVector> it = annotVectorArr.mAnnotVectorArray.iterator(); it.hasNext(); )
        {
            AnnotationVector annotVector = it.next();

            strBuffer.append( "Type: " + annotVector.mType + "\n" );

            System.out.println( "Type: " + annotVector.mType + "\n" );

            strBuffer.append( listAnnotations( annotVector ) );

        }




        // sort annotations by start
        ClientUtils.SortAnnotations( annotVectorArr );
        //

        // create enumeration of existing sidenotes in the text

        UNOUtils.initializeCursor( compCtx );

        for( Iterator<Annotation> it2 = ClientUtils.mAnnotArray.iterator(); it2.hasNext(); )
        {
            // Create Side Notes

            CreateSideNotes( it2.next() );
        }

        return strBuffer.toString();

    }

    protected String listAnnotations( AnnotationVector as )
    {
        if( as == null )
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();


        for( Iterator<Annotation> it = as.mAnnotationVector.iterator(); it.hasNext(); )
        {
            Annotation annotation = it.next();

            if( annotation.mContent != null && !annotation.mContent.equals( "" ) )
            {
                sb.append( "Start: " + annotation.mStart + ", end: " + annotation.mEnd + ", content: " + annotation.mContent + "\n" );
            }

            if( annotation.mFeatures == null || annotation.mFeatures.size() == 0 )
            {
                sb.append( "\n" );
                continue;
            }

            if( annotation.mFeatures.size() > 1 )
            {
                sb.append( "Features:\n" );
            }

            Set<String> keys = annotation.mFeatures.keySet();


            for( Iterator<String> it2 = keys.iterator(); it2.hasNext(); )
            {
                String currentKey = it2.next();
                sb.append( currentKey + ": " + annotation.mFeatures.get( currentKey ) + "\n" );
            }

            sb.append( "\n" );
        }

        return sb.toString();
    }

    public void setServiceName( String s )
    {
        serviceName = s;
    }

    public void setArgumentText( String s )
    {
        argumentText = s;
    }

    public void setRuntimeParameters( GateRuntimeParameterArray a )
    {
        rtpArray = a;
    }

    protected void CreateSideNotes( Annotation annotation )
    {

        if( annotation == null )
        {
            return;
        }

        UNOUtils.createDocAnnotations( compCtx, annotation );

    }

   /**
    * Open an URI through a browser.
    *
    * @param uri HTML document.
    * @return true if successful, false otherwise.
    */
   private boolean spawnBrowser(URI uri)
   {
      boolean status = true;

      final Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.BROWSE)) {
         try {
            desktop.browse(uri);
         } catch (IOException ex) {
            System.out.println( "---------------- Failed to launch browser");
            status = false;
         }
      } else {
         System.out.println( "---------------- Browsing is not supported");
         status = false;
      }
      return status;
   }
}
