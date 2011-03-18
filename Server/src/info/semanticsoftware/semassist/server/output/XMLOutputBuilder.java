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
package info.semanticsoftware.semassist.server.output;

import java.util.*;
import java.net.URL;

import gate.*;
import gate.util.InvalidOffsetException;

import info.semanticsoftware.semassist.server.util.*;
import java.util.regex.*;

public class XMLOutputBuilder extends OutputBuilder
{

    private StringBuffer mTotalResult = new StringBuffer();

    @Override
    public void reset()
    {
        mTotalResult = new StringBuffer();
    }

    @Override
    protected String startResponse()
    {
        //return "<?xml version=\"1.0\"?>\n<saResponse>\n";
	return "<?xml version=\"1.0\"?><saResponse>";
    }

    @Override
    protected String endResponse()
    {
        //return "</saResponse>\n";
	return "</saResponse>";
    }

    @Override
    public void addOutput( GATEPipelineOutput o )
    {
        Logging.log( "---------------- Adding output " + o.getHRFormat() );

        // ***** Annotation case
        if( o.isAnnotation() )
        {
            if( corpusNotSet() )
            {
                Logging.log( MasterData.ERROR_ANNOUNCEMENT + "XMLOutputBuilder: added " +
                             "output claims to be an annotation, but I have no corpus." );
                return;
            }
            Logging.log( "---------------- Output: isAnnotation case " );

            GATEAnnotation a = o.getAnnotation();
            mTotalResult.append( ResponseFormatXML.openAnnotationTag( a ) );

            // Iterate over the documents of the mCorpus

            for( Iterator<Document> it = mCorpus.iterator(); it.hasNext(); )
            {
                Logging.log( "---------------- Iterating... " );

                // Start a section for the current document in the response
                Document doc = it.next();
                URL sourceURL = doc.getSourceUrl();
                String sourceURLString = sourceURL.toString();

                if( sourceURL.toString().equals( MasterData.getDummyDocumentURL().toString() ) )
                {
                    sourceURL = null;
                }
                mTotalResult.append( ResponseFormatXML.openDocumentTag( sourceURL ) );

                AnnotationSet parentSet = null;

                if( a.mSetName == null || a.mSetName.equals( "Annotation" ) )
                {
                    parentSet = doc.getAnnotations();
                }
                else
                {
                    parentSet = doc.getAnnotations( a.mSetName );
                }

                AnnotationSet actualSet = parentSet.get( a.mName );

                // Sort the annotations
                Annotation currAnnot;
                List<Annotation> sortedList = new ArrayList<Annotation>( actualSet );
                Collections.sort( sortedList, new gate.util.OffsetComparator() );

                // Iterate over the annotation instances
                for( int j = 0; j < sortedList.size(); j++ )
                {
                    currAnnot = (Annotation) sortedList.get( j );
                    mTotalResult.append( oneAnnotation( doc, a, currAnnot ) );
                } // End for each annotation instance

                mTotalResult.append( ResponseFormatXML.closeDocumentTag() );
            } // End for each document in the mCorpus

            mTotalResult.append( ResponseFormatXML.closeAnnotationTag() );
        // Logging.log("---------------- Annotation case finished. Text:");
        // Logging.log(getActualContents());
        }

        // ***** File case
        else if( o.getParameterForFileURL() != null && o.getFileURL() != null )
        {
            // TODO: consider isPerDocument. Already during the invocation!
            URL fileURL = o.getFileURL();
            mTotalResult.append( ResponseFormatXML.outputFile( fileURL, o.getMIMEType(), o.getHRFormat() ) );
        }

        // ***** Corpus case
        else if( o.getParameterForResultCorpus() != null )
        {
            for( Iterator<Document> it = mCorpus.iterator(); it.hasNext(); )
            {
                mTotalResult.append( ResponseFormatXML.outputDocument( it.next() ) );
            }
        }
        // ***** Out of ideas...
        else
        {
            Logging.log( MasterData.WARNING_ANNOUNCEMENT + "XMLOutputBuilder::addOutput: Don't " +
                         "know what to do with this output: " + o.getHRFormat() );
            return;
        }
    }
    
    
    protected String getActualContents()
    {
        return mTotalResult.toString();
    }

    protected String oneAnnotation( Document doc, GATEAnnotation a, Annotation currAnnot )
    {
        StringBuffer result = new StringBuffer();

        // Check boundaries of annotation
        long positionStart = currAnnot.getStartNode().getOffset().longValue();
        long positionEnd = currAnnot.getEndNode().getOffset().longValue();

        if( positionEnd != -1 && positionStart != -1 )
        {
            String content = null;
            try
            {
                content = doc.getContent().getContent( positionStart, positionEnd ).toString();
                content = content.replaceAll( "\n", "" );
                content = content.replaceAll( "\r", "" );
                content = content.replaceAll( "\t", "" );
            }
            catch( InvalidOffsetException e )
            {
                Logging.exception( e );
                return "";
            }

            result.append( ResponseFormatXML.openAnnotationInstance( content, positionStart, positionEnd ) );

            // Output the mFeatures of the annotation and its values
            if( a.mFeatures != null )
            {
                FeatureMap fmap = currAnnot.getFeatures();
                Iterator<String> it_features = a.mFeatures.iterator();
                while( it_features.hasNext() )
                {
                    String featureName = it_features.next();
                    Object featureValue = fmap.get( featureName );
                    if( featureValue == null )
                    {
                        featureValue = "";
                    }
                    result.append( ResponseFormatXML.outputFeature( featureName, featureValue.toString() ) );
                }
            }

            // Close the output for this annotation
            result.append( ResponseFormatXML.closeAnnotationInstance() );
        }

        return result.toString();
    }

}



