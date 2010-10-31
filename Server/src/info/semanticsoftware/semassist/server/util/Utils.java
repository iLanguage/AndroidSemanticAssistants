/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2009, 2010 Semantic Software Lab, http://www.semanticsoftware.info
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

package info.semanticsoftware.semassist.server.util;

import java.io.*;
import java.util.*;
import java.net.*;
import java.text.DateFormat;

import gate.*;
//import gate.util.*;

public class Utils
{

    /**
     * Receives the input for a language service and writes it
     * to temporary files on the disk.
     */
    public static Vector<File> writeInputToDisk( URI[] uris, String[] literal )
    {
        Vector<File> result = new Vector<File>();
        int literalIndex = 0;
        for( int i = 0; i < uris.length; i++ )
        {
            // Literal case
            if( uris[i].toString().equals( MasterData.LITERAL_DOC_URI ) )
            {
                try
                {
                    File f = createTempFile( "input-", ".xml" );
                    FileWriter writer = new FileWriter( f );
                    BufferedWriter bufWriter = new BufferedWriter( writer );

                    bufWriter.write( literal[literalIndex++] );
                    bufWriter.flush();
                    bufWriter.close();

                    result.add( f );
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                }


            }
            // Web document or local file URL case
            else
            {
                try
                {
                    File f = writeUrlToDisk( uris[i].toURL() );
                    if( f != null )
                    {
                        result.add( f );
                    }
                }
                catch( MalformedURLException e )
                {
                    Logging.exception( e );
                }


            }


        }

        return result;
    }

    public static File writeUrlToDisk( URL url )
    {
        if( url == null )
        {
            return null;
        }
        File result = null;


        // Let GATE do the reading job
        try
        {
            File f = createTempFile( "input-", ".xml" );
            FileWriter writer = new FileWriter( f );
            BufferedWriter bufWriter = new BufferedWriter( writer );

            // Get rid of markup
            Document doc = null;
            try
            {
                doc = Factory.newDocument( url );
                DocumentContent content = doc.getContent();
                String contentString = content.toString().replaceAll( "&", "&amp;" );
                bufWriter.write( contentString );
                // bufWriter.write(doc.toXml());
            }
            catch( gate.creole.ResourceInstantiationException e )
            {
                Logging.exception( e );
            }

            bufWriter.flush();
            bufWriter.close();
            return f;
        }
        catch( IOException e )
        {
            Logging.exception( e );
            return null;
        }


        /*
        // Read URL target directly
        BufferedReader r = getURLReader(url);
        if (r == null) {
        System.out.println("Problem ocurred with URL " + url.toString() + "...");
        return null;
        }
        result = writeReaderToDisk(r);
        if (result == null) {
        System.out.println("Could not write the reader contents to disk. URL "
        + url.toString() + "...");
        return null;
        }

        // If URL points to HTML
        try {
        URLConnection conn = url.openConnection();
        //Logging.log("---------------- Content type for " + url.toString() + ": " +
        //conn.getContentType());
        if (conn.getContentType().substring(0, 9).equals("text/html")) {
        Logging.log("---------------- Preparing to extract text from HTML...");
        File htmlFile = result;
        result = html2text(result, true);
        if (result != null) {
        Logging.log("---------------- Done extracting text from HTML...");
        } else {
        Logging.log("---------------- Failed extracting text from HTML...");
        result = htmlFile;
        }
        }
        } catch (IOException e) {
        Logging.log("---------------- Could not open URL connection for " + url.toString());
        }

        // Remove HTML entities
        //result = removeHTMLEntities(result);

        return result;
         */
    }

    public static Vector<File> writeCorpusToDisk( Corpus corpus )
    {
        Vector<File> result = new Vector<File>();
        Iterator<Document> it = corpus.iterator();
        while( it.hasNext() )
        {
            Document currentDoc = it.next();
            currentDoc.setPreserveOriginalContent( new Boolean( true ) );
            currentDoc.setMarkupAware( new Boolean( true ) );

            URL sourceUrl = currentDoc.getSourceUrl();
            // Information (annotations, features) could become lost
            if( sourceUrl != null && sourceUrl.toString().startsWith( "http://" ) )
            {
                File f = writeUrlToDisk( sourceUrl );
                result.add( f );
            }
            else
            {
                try
                {
                    File f = createTempFile( "input-", ".xml" );
                    FileWriter writer = new FileWriter( f );
                    BufferedWriter bufWriter = new BufferedWriter( writer );

                    // Get rid of markup
                    DocumentContent content = currentDoc.getContent();
                    Document temp = null;
                    try
                    {
                        temp = Factory.newDocument( content.toString() );
                        bufWriter.write( temp.toXml() );
                    }
                    catch( gate.creole.ResourceInstantiationException e )
                    {
                        Logging.exception( e );
                    }

                    //bufWriter.write(content.toString());

                    bufWriter.flush();
                    bufWriter.close();
                    result.add( f );
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static File html2text( File f )
    {
        return html2text( f, false );
    }

    public static File html2text( File f, boolean preserveAmpersand )
    {
        if( f == null )
        {
            return null;
        }

        String command = MasterData.Instance().getHTMLtoText();
        if( command == null )
        {
            command = "/usr/bin/html2text -nobs";
        }

        // Create an output file
        File outFile = Utils.createTempFile( "html2text-", ".xml" );

        try
        {
            command += " " + f.getCanonicalPath();
            Logging.log( "---------------- Executing " + command );
            Process p = Runtime.getRuntime().exec( command );
            Logging.log( "---------------- Command executed" );

            // Get a hold of the potential error output of the program
            BufferedReader error = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );
            BufferedReader output = new BufferedReader( new InputStreamReader( p.getInputStream() ) );

            // Store the error output in a string
            String lines = "";
            String line;
            Logging.log( "---------------- Checking error..." );

            /*
            while( (line = error.readLine()) != null) {
            Logging.log("------" + line);
            lines = lines + "\n" + line;
            }
             */
            if( !lines.equals( "" ) )
            {
                Logging.log( "---------------- Error output of html2text tool:\n" + lines );
            }
            else
            {
                Logging.log( "---------------- No error output of html2text..." );
                try
                {
                    // Read the output of html2text and write
                    // it to the output file
                    lines = "";
                    FileWriter writer = new FileWriter( outFile );
                    BufferedWriter bufWriter = new BufferedWriter( writer );

                    while( (line = output.readLine()) != null )
                    {
                        // Logging.log("------" + line);
                        if( !preserveAmpersand )
                        {
                            line = line.replaceAll( "&", "&amp;" );
                        }
                        bufWriter.write( "\n" + line );
                    }
                    bufWriter.flush();
                    bufWriter.close();
                }
                catch( IOException e )
                {
                    Logging.log( "---------------- IOException" );
                }
            }

            return outFile;
        }
        catch( IOException e )
        {
            e.printStackTrace();
            return null;
        }

    }

    public static File removeHTMLEntities( File f )
    {
        if( f == null )
        {
            return null;
        }

        // Create an output file
        File outFile = Utils.createTempFile( "merged-", ".xml" );

        try
        {
            String fn = f.getCanonicalPath();
            String newFilename = fn + ".ed.xml";
            String command = "sed -e \"s/\\&[^;]*;//g\" " + fn + " > " + newFilename;
            Logging.log( "---------------- Executing " + command );
            Process p = Runtime.getRuntime().exec( command );
            return new File( newFilename );
        }
        catch( IOException e )
        {
            e.printStackTrace();
            return null;
        }

    }

    public static File writeReaderToDisk( BufferedReader r )
    {
        return writeReaderToDisk( r, true );
    }

    public static File writeReaderToDisk( BufferedReader r, boolean maskMarkup )
    {
        if( r == null )
        {
            return null;
        }

        try
        {
            File f = createTempFile( "input-", ".xml" );
            FileWriter writer = new FileWriter( f );
            BufferedWriter bufWriter = new BufferedWriter( writer );

            String line = "";
            while( (line = r.readLine()) != null )
            {
                /*
                if (maskMarkup) {
                // This is quite ugly, but I'm under time pressure...
                line = line.replaceAll("&nbsp;", " ");
                line = line.replaceAll("&reg;", " ");
                line = line.replaceAll("&copy;", " ");
                line = line.replaceAll("&", "&amp;");
                }
                 */
                bufWriter.write( line );
            }
            bufWriter.flush();
            bufWriter.close();

            return f;

        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        return null;
    }

    public static BufferedReader getURLReader( URL url )
    {
        if( url == null )
        {
            return null;
        }

        if( url.getProtocol().equals( "http" ) )
        {
            try
            {
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

                int responseCode = httpConnection.getResponseCode();
                String contentType = httpConnection.getContentType();
                // System.out.println("Content type: " + contentType);

                if( responseCode != HttpURLConnection.HTTP_OK )
                {
                    System.out.println( "HTTP connection response is not HTTP_OK for URI " + url.toString() );
                    return null;
                }
                if( !contentType.substring( 0, 4 ).equals( "text" ) )
                {
                    System.out.println( "Content type of " + url.toString() + " is non-text." );
                }

                InputStream is = httpConnection.getInputStream();
                BufferedReader input = new BufferedReader( new InputStreamReader( is ) );
                return input;
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
        } // end "http" case
        else if( url.getProtocol().equals( "file" ) )
        {
            try
            {
                File f = new File( url.toURI() );
                return new BufferedReader( new InputStreamReader( new FileInputStream( f ) ) );
            }
            catch( FileNotFoundException e )
            {
                Logging.log( "---------------- Could not find file " + url.toString() + " on disk. Omitting." );
            }
            catch( URISyntaxException e )
            {
                e.printStackTrace();
            }


        }


        return null;
    }

    public static File createTempFile()
    {
        return createTempFile( "serviceResult-", "txt" );
    }

    public static File createTempFile( String prefix, String ext )
    {
        String fileName = getRandomFileName( prefix );
        File outFile = null;

        try
        {
            outFile = File.createTempFile( fileName, ext );
        }
        catch( IOException e )
        {
            Logging.exception( e );
        }

        return outFile;
    }

    public static String getRandomFileName( String prefix )
    {
        DateFormat df = DateFormat.getTimeInstance( DateFormat.LONG );
        String ds = df.toString().replace( " ", "" );
        return prefix + ds + ".";
    }

    public static Corpus mergeCorpusDocuments( Corpus corpus )
    {
        if( corpus == null )
        {
            return null;
        }
        Vector<File> corpusDocuments = writeCorpusToDisk( corpus );
        if( corpusDocuments == null )
        {
            return null;
        }

        File mergedFile = mergeFiles( corpusDocuments );
        Corpus newCorpus = null;
        try
        {
            newCorpus = Factory.newCorpus( "Merged Corpus" );
            newCorpus.add( Factory.newDocument( mergedFile.toURI().toURL() ) );
        }
        catch( gate.creole.ResourceInstantiationException e )
        {
            Logging.exception( e );
        }
        catch( MalformedURLException e )
        {
            Logging.exception( e );
        }

        return newCorpus;
    }

    /**
     * Returns - if successful - a File object representing the
     * merged input documents. For merging, a command-line tools is
     * called.
     */
    public static File mergeInput( URI[] uris, String[] literal )
    {
        Logging.log( "---------------- Preparing to merge input..." );

        // Save the input to disk so that it
        // can be passed to the merge tool
        Vector<File> inputFiles = Utils.writeInputToDisk( uris, literal );
        return mergeFiles( inputFiles );
    }

    public static File mergeFiles( Vector<File> files )
    {
        // Get a merge command line
        File outFile = Utils.createTempFile( "merged-", ".xml" );
        String mergeCommand = fillMergeCommand( files, outFile );
        if( mergeCommand == null )
        {
            String error = "--------------- No command line command for document merge given. Aborting.";
            Logging.log( error );
            return null;
        }

        // Invoke merge tool
        Logging.log( "---------------- Running command line: " + mergeCommand );
        try
        {
            Process p = Runtime.getRuntime().exec( mergeCommand );

            // Get a hold of the error output of the merge program
            BufferedReader input = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );

            // Store the output in a string
            String lines = "";
            String line;
            while( (line = input.readLine()) != null )
            {
                lines = lines + "\n" + line;
            }
            if( !lines.equals( "" ) )
            {
                System.out.println( "---------------- Error output of merge tool:\n" + lines );
            }

            return outFile;
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String fillMergeCommand( Vector<File> inputFiles, File outFile )
    {
        String mergeCommand = MasterData.Instance().getMergeCmd();
        if( mergeCommand == null )
        {
            return null;
        }

        // Edit the merge command. The merge command must
        // contain the strings %OUTPUT_FILE and %INPUT_FILES.
        // These will be replaced in the following
        String ifString = "";
        Iterator<File> it = inputFiles.iterator();
        try
        {
            while( it.hasNext() )
            {
                File current = it.next();
                ifString += " " + current.getCanonicalPath();
            }
        }
        catch( IOException e )
        {
        }
        mergeCommand = mergeCommand.replace( "%INPUT_FILES%", ifString );

        // Output file
        try
        {
            mergeCommand = mergeCommand.replace( "%OUTPUT_FILE%", outFile.getCanonicalPath() );
        }
        catch( IOException e )
        {
        }


        return mergeCommand;
    }

    /**
     * Adds the name of the containing pipeline to the
     * GATERuntimeParameter objects.
     */
    public static void addPipelineInfo( ServiceInfo info )
    {
        if( info == null )
        {
            return;
        }

        String sn = info.getServiceName();
        Vector<GATERuntimeParameter> params = info.mParams;
        Iterator<GATERuntimeParameter> it = params.iterator();
        while( it.hasNext() )
        {
            GATERuntimeParameter p = it.next();
            p.setPipelineName( sn );
        }

    }

    public static void completeServiceInformation( Vector<ServiceInfo> infos,
                                                   HashMap<String, ServiceInfo> availableServices )
    {
        if( infos == null )
        {
            return;
        }

        Iterator<ServiceInfo> it = infos.iterator();
        while( it.hasNext() )
        {
            ServiceInfo current = it.next();
            completeServiceInformation( current, availableServices );
        }

    }

    public static void completeServiceInformation( ServiceInfo info,
                                                   HashMap<String, ServiceInfo> availableServices )
    {
        if( info == null )
        {
            return;
        }

        if( info.isConcatenation() )
        {
            String services = info.getConcatenationOf();
            if( services == null )
            {
                Logging.log( "---------------- completeServiceInformation: " +
                             "No concatenation, but claims to be." );
            }
            else
            {
                String[] array = services.split( "," );
                if( array == null )
                {
                    Logging.log( "---------------- Array of services is null." );
                }
                else
                {
                    Vector<GATERuntimeParameter> combinedParams = new Vector<GATERuntimeParameter>();
                    for( int i = 0; i < array.length; i++ )
                    {
                        ServiceInfo current = availableServices.get( array[i].trim() );
                        combinedParams.addAll( current.mParams );
                    }
                    info.mParams = combinedParams;
                }
            }
        }

    }

}
