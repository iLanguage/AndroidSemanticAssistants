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

import java.net.URL;

import info.semanticsoftware.semassist.server.util.*;
import gate.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;

public class ResponseFormatXML
{

    /* We keep these strings for better readability while debugging
    public static final String START_MARKING = "    <mark ";
    public static final String END_MARKING = "</mark>\n";
    public static final String START_ANNOTATION = "    <annotation ";
    public static final String START_DOCUMENT = "      <document ";
    public static final String START_ANNOTATION_INSTANCE = "        <annotationInstance ";
    public static final String START_OUTPUT_FILE = "    <outputFile ";
    public static final String START_OUTPUT_DOCUMENT = "    <outputDocument ";
    public static final String START_FEATURE = "          <feature ";*/

    public static final String START_MARKING = "<mark";
    public static final String END_MARKING = "</mark>";
    public static final String START_ANNOTATION = "<annotation";
    public static final String START_DOCUMENT = "<document";
    public static final String START_ANNOTATION_INSTANCE = "<annotationInstance";
    public static final String START_OUTPUT_FILE = "<outputFile";
    public static final String START_OUTPUT_DOCUMENT = "<outputDocument";
    public static final String START_FEATURE = "<feature";

    public static String createMarkingElement( String type,
                                               String startOffset,
                                               String endOffset,
                                               String content )
    {
        StringBuffer result = new StringBuffer( START_MARKING );
        result.append( specifyType( type ) );
        result.append( specifyStartOffset( startOffset ) );
        result.append( specifyEndOffset( endOffset ) );
        result.append( ">" );
        result.append( content );
        result.append( END_MARKING );

        return result.toString();
    }

    public static String openAnnotationTag( GATEAnnotation a )
    {
        StringBuffer result = new StringBuffer( START_ANNOTATION );
        result.append( specifyType( a.mName ) );

        if( a.mSetName.equals("") )
        {
            a.mSetName = "Annotation";
        }

        result.append( set( "annotationSet", a.mSetName ) );
        //result.append( ">\n" );
	result.append( ">" );
        return result.toString();
    }

    public static String closeAnnotationTag()
    {
        //return "    </annotation>\n";
	return "</annotation>";
    }

    public static String openAnnotationInstance( String content, long start, long end )
    {
        StringBuffer result = new StringBuffer( START_ANNOTATION_INSTANCE );
        result.append( set( "content", content ) );
        result.append( set( "start", Long.toString( start ) ) );
        result.append( set( "end", Long.toString( end ) ) );
        //result.append( " >\n" );
	result.append( ">" );
        return result.toString();
    }

    public static String closeAnnotationInstance()
    {
        //return "        </annotationInstance>\n";
	  return "</annotationInstance>";
    }

    public static String outputFeature( String name, String value )
    {
        StringBuffer result = new StringBuffer( START_FEATURE );

        result.append( set( "name", name ) );
        value = forXML( value );
        System.out.println( "--------Value after stripping out illegal characters: " + value );
        result.append( set( "value", value ) );
        //result.append( " />\n" );
        result.append( "/>" );

        return result.toString();
    }

    public static String forXML( String aText )
    {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator( aText );
        char character = iterator.current();



        while( character != CharacterIterator.DONE )
        {
            switch( character )
            {
                case '<':
                case '>':
                case '\"':
                case '\'':
                    break;

                case '&':
                    result.append( "and" );
                    break;
                default:
                    //the char is not a special one
                    //add it to the result as is
                    result.append( character );
                    break;

            }

            character = iterator.next();
        }
        return result.toString();
    }

    public static String openDocumentTag( URL docUrl )
    {
        StringBuffer result = new StringBuffer( START_DOCUMENT );
        String urlString = (docUrl == null) ? "" : docUrl.toString();
        result.append( set( "url", urlString ) );
        //result.append( " >\n" );
	result.append( ">" );
        return result.toString();
    }

    public static String closeDocumentTag()
    {
        //return "      </document>\n";
	  return "</document>";
    }

    public static String outputFile( URL u, String mime, String hrFormat )
    {
        StringBuffer result = new StringBuffer( START_OUTPUT_FILE );
        if( u != null )
        {
            result.append( set( "url", u.toString() ) );
        }

        result.append( set( "mimeType", (mime == null ? "" : mime) ) );
        result.append( set( "format", (hrFormat == null ? "" : hrFormat) ) );
        //result.append( " />\n" );
	result.append( "/>" );
        return result.toString();
    }

    public static String outputDocument( Document doc )
    {
        StringBuffer result = new StringBuffer( START_OUTPUT_DOCUMENT );
        if( doc != null )
        {
            URL url = doc.getSourceUrl();
            if( url != null )
            {
                result.append( set( "url", url.toString() ) );
            }
        }

        //result.append( " />\n" );
        result.append( "/>" );
        return result.toString();
    }

    public static String specifyType( String type )
    {
        return set( "type", type );
    }

    public static String specifyStartOffset( String offset )
    {
        return set( "startOffset", offset );
    }

    public static String specifyEndOffset( String offset )
    {
        return set( "endOffset", offset );
    }

    protected static String set( String attName, String value )
    {
        return " " + attName + "=\"" + value + "\"";
    }

}
