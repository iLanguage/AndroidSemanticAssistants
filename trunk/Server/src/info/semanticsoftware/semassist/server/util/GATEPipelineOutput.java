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

import java.util.*;
import java.net.URL;
//import java.net.MalformedURLException;

/**
 * Instances of this class are  known by
 * the service agent, so that it knows which
 * results are available and can be passed on
 * to the client.
 */
public class GATEPipelineOutput
{

    /**
     * List holding parameters with certain values. The semantics is
     * that the pipeline output represented by this object
     * is only present if the values actually passed to the
     * pipeline match the values of the parameters in this list.
     */
    private Vector<GATERuntimeParameter> mNecessaryParameterSettings = new Vector<GATERuntimeParameter>();
    /**
     * Human readable format of the output
     */
    private String mHrFormat = "";
    /**
     * If the output that this object represents is written
     * to a file, then the pipeline must provide a runtime
     * parameter of type java.net.URL by which we can specify
     * where the output file is saved. The name of this
     * runtime parameter has to be put in this string variable.
     */
    private GATERuntimeParameter mParameterForFileURL = null;
    /**
     * If the output that this object represents is actually a corpus
     * (e.g., in case of a search pipeline), put the parameter
     * information where we can set this (initially empty) corpus here.
     */
    private GATERuntimeParameter mParameterForResultCorpus = null;
    /**
     * If the output that this object represents is written
     * to a file, the program can use this field to specify
     * where the file URL.
     */
    private URL mFileURL = null;
    /**
     * In case this object represents a group of annotations,
     * the mAnnotation object will hold all the relevant
     * information on it.
     */
    private GATEAnnotation mAnnotation = null;
    /**
     * Captures if an output is created per document or
     * once per corpus.
     */
    private boolean mIsPerDocument = false;
    /**
     * MIME type of the output format
     */
    private String mMimeType = "";

    public void addParameterConstraint( GATERuntimeParameter p )
    {
        mNecessaryParameterSettings.add( p );
    }

    public void addParameterConstraint( GATERuntimeParameter p, String value )
    {
        p.parseStringAndSetValue( value );
        mNecessaryParameterSettings.add( p );
    }

    public Vector<GATERuntimeParameter> getNecessaryParameterSettings()
    {
        return mNecessaryParameterSettings;
    }

    public boolean isAnnotation()
    {
        return mAnnotation != null;
    }


    // ------------------------------
    public void setAnnotation( GATEAnnotation a )
    {
        mAnnotation = a;
    }

    public GATEAnnotation getAnnotation()
    {
        return mAnnotation;
    }

    public void setHRFormat( String s )
    {
        mHrFormat = s;
    }

    public String getHRFormat()
    {
        return mHrFormat;
    }

    public void setMIMEType( String s )
    {
        mMimeType = s;
    }

    public String getMIMEType()
    {
        return mMimeType;
    }

    public void setParameterForFileURL( GATERuntimeParameter p )
    {
        mParameterForFileURL = p;
    }

    public GATERuntimeParameter getParameterForFileURL()
    {
        return mParameterForFileURL;
    }

    public void setParameterForResultCorpus( GATERuntimeParameter p )
    {
        mParameterForResultCorpus = p;
    }

    public GATERuntimeParameter getParameterForResultCorpus()
    {
        return mParameterForResultCorpus;
    }

    public void setFileURL( URL u )
    {
        mFileURL = u;
    }

    public URL getFileURL()
    {
        return mFileURL;
    }

    public void setIsPerDocument( boolean b )
    {
        mIsPerDocument = b;
    }

    public boolean getIsPerDocument()
    {
        return mIsPerDocument;
    }

}
