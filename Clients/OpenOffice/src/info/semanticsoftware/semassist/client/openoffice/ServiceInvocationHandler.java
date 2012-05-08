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
import info.semanticsoftware.semassist.csal.callback.*;

import net.java.dev.jaxb.array.*;
import info.semanticsoftware.semassist.csal.result.*;
import info.semanticsoftware.semassist.client.openoffice.utils.*;
import info.semanticsoftware.semassist.server.*;


import com.sun.star.uno.XComponentContext;
import javax.swing.JOptionPane;

public class ServiceInvocationHandler implements Runnable
{

    private XComponentContext compCtx;

    /** Textual content of the active document or selected document region when
        the service was invoked. */
    private String argumentText = null;

    private String serviceName = null;
    private Thread thread;
    private GateRuntimeParameterArray rtpArray = new GateRuntimeParameterArray();

    public ServiceInvocationHandler( final XComponentContext xComponentContext )
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
            System.out.println( "No text is available to process." );
            return;
        }

        // Populate the document & content arrays to transmit as the server
        // request. 
        final UriList uriList = new UriList();

        final URI docURI = UNOUtils.getDocumentURI( compCtx );
        if (docURI != null && !docURI.toString().equals("")) {
            uriList.getUriList().add("#"+ docURI.toString());
        } else {
            // Unsaved documents do not have a URI.
            uriList.getUriList().add("#literal");
        }

        final StringArray stringArray = new StringArray();
        stringArray.getItem().add( argumentText );
        
        System.out.println( "Text Start -------------------------------------" );
        System.out.println( argumentText );
        System.out.println( "Text End ---------------------------------------" );

        String serviceResponse = null;
        SemanticServiceBroker broker = null;
        try
        {
            broker = ServiceAgentSingleton.getInstance();
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
            System.out.println("serviceResponse:\n"+ serviceResponse);

            // used for document case
            String DocString = "";
            boolean DocCase = false;

            String boundlessString = "";
            boolean boundlessCase = false;

            if( results == null )
            {
                // Open document showing response message
                System.out.println( "---------- No results retrieved in response message" );
                UNOUtils.createNewDoc( compCtx, serviceResponse );
                return;
            }

            // Clear previously generated annotations by a given pipeline.
            if (ClientPreferences.isRefreshAnnotations()) {
               System.out.println("Removing older annotations");
               UNOUtils.clearDocAnnotations(compCtx, UNOUtils.getCurrentPipeline());
            }

            for( Iterator<SemanticServiceResult> it = results.iterator(); it.hasNext(); )
            {
                SemanticServiceResult current = it.next();

                if( current.mResultType.equals( SemanticServiceResult.FILE ) )
                {
                    // File case

                    System.out.println( "------------ Result type: " + SemanticServiceResult.FILE );
                    String fileString = broker.getResultFile( current.mFileUrl );

                    // Get file extension from MIME type or default to text if unknown.
                    String fileExt = ClientUtils.getFileNameExt( current.mMimeType );
                    if( fileExt == null )
                    {    
                        fileExt = ClientUtils.FILE_EXT_TEXT;
                    }      

                    System.out.println( "------------ fileExt: " + fileExt );
                    final File f = ClientUtils.writeStringToFile( fileString, fileExt );

                    if ( ClientPreferences.isBrowserResultHandling() )
                    {
                       // Attempt to open HTML files through an external browser,
                       // else open the file through the default word-processor.
                       if (ClientUtils.MIME_TEXT_HTML.equalsIgnoreCase( current.mMimeType )) {
                           if (!spawnBrowser( f )) {
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
                else if( current.mResultType.equals( SemanticServiceResult.BOUNDLESS_ANNOTATION ) )
                {
                    // Annotation case => append to data structure
                    System.out.println( "---------------- Annotation case..." );

                    boundlessCase = true;

                    // Process annotations corresponding to each of the
                    // documents.
                    for (final String docID : current.mAnnotations.keySet() ) {
                        System.out.println("Annotating document <"+ docID +">");

                        final AnnotationVector annots = current.mAnnotations.get(docID);

                        boundlessString += "\n" + listAnnotations(annots);

                        handleAnnotations(compCtx, annots, null);
                    }    
                }
                else if( current.mResultType.equals( SemanticServiceResult.ANNOTATION ) )
                {
                    // Sidenote case => append to data structure
                    System.out.println( "---------------- Sidenote case..." );

                    // Process annotations corresponding to each of the
                    // documents.
                    for( final String docID : current.mAnnotations.keySet() ) {
                        System.out.println("Annotating document <"+ docID +">");

                        // NOTE: Unsaved documents requested for server processing
                        // are numerically indexed rather than represented by URLs
                        // in the server's response. Need to revert this non-URL 
                        // mapping here.
                        final String url = isURL(docID) ? docID : "";

                        if (UNOUtils.isDocumentLoaded(compCtx, url)) {
                           final AnnotationVector annots = current.mAnnotations.get(docID);
                           handleAnnotations(compCtx, annots, url);
                        }
                    }
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

            // FIXME: This branch is only relevant for the SemanticServiceResult.DOCUMENT
            // case & should be moved there to reduce variable scopes. More seriously, if
            // multiple documents are returned in a corpus, at best only 1 new document is
            // created. Also the DocString argument should be the content of documents not
            // their URLs! From the looks of our pipelines, this seams like dead code.
            if( DocCase )
            {
                UNOUtils.createNewDoc( compCtx, DocString );
            }

            if( boundlessCase )
            {
               UNOUtils.createNewDoc( compCtx, boundlessString);
            }

        }

   /**
    * Performs side-note annotation on a given document context.
    *
    * @param ctx Document context to annotate.
    * @param annots Annotation vector corresponding to a document.
    * @param url URL of the save document to which annotate or null.
    */
   private static void handleAnnotations(
      final XComponentContext ctx, final AnnotationVector annots, final String url)
   {
      // Divide annotations into interactive & non-interactive ones.
      final Collection<Annotation> sideNoteAnnots = new ArrayList<Annotation>();
      final Collection<Annotation> dialogAnnots = new ArrayList<Annotation>();
      final String contextFeature = "problem";
      for (final Annotation annot : annots.mAnnotationVector) {
         if (ClientPreferences.isInteractiveResultHandling() &&
             annot.mFeatures.containsKey(contextFeature)) {
            dialogAnnots.add(annot);
         } else {
            sideNoteAnnots.add(annot);
         }
      }

      // Handle interactive annotations (if any) through a modify dialog.
      if (ClientPreferences.isInteractiveResultHandling()) {
        if (!dialogAnnots.isEmpty()) {
            new InteractiveAnnotationFrame(
               dialogAnnots.toArray(new Annotation[dialogAnnots.size()]),
               contextFeature, "suggestion", new ReplaceAnnotCallback<AnnotModifyCallbackParam>());
         } else {
            System.out.println("Found no interactive annotations with <"+ contextFeature +"> features");
         }
      }

      UNOUtils.initializeCursor(ctx, url);

      // Default annotation handling.
      for (final Annotation annot : sideNoteAnnots) {
         if (annot != null) {
            UNOUtils.createDocAnnotations(ctx, annot);
         }
      }
   }


    /**
     * Convert a memory representation of annotations into
     * a human-readable string.
     */
    private static String listAnnotations( final AnnotationVector as )
    {
        if( as == null )
        {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        sb.append( "Type: " + as.mType + "\n" );

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

   /**
    * Open an file through a browser.
    *
    * @param f HTML document.
    * @return true if successful, false otherwise.
    */
   private boolean spawnBrowser(final File f)
   {
      boolean status = true;
      try {
         String command = "firefox " + f.getCanonicalPath();
         System.out.println( "---------------- Executing " + command );
         Process p = Runtime.getRuntime().exec( command );
         System.out.println( "---------------- Command executed" );

         // Get annotation hold of the potential error output of the program
         BufferedReader error = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );
      } catch( java.io.IOException e ) {
         System.out.println( "---------------- Failed to launch browser");
         status = false;
      }
      return status;
   }


   private static final boolean isURL(final String str) {
      boolean result = true;
      try {
         new URI(str).toURL();
      } catch (final Exception ex) {
         // Test by side-effect.
         result = false;
      }
      return result;
   }
}

// Helper Class
class ReplaceAnnotCallback<T extends AnnotModifyCallbackParam> implements Callback<T> {
   @Override
   public boolean execute(final T param) {
      return UNOUtils.replaceAnnotation(param.getAffectedAnnotation(), param.getContext());
   }
}
