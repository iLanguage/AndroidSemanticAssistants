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
//import serverside.ipd.semassist.util.GATERuntimeParameter;

public class ServiceInfo
{

    protected String mServiceName = "";
    protected String mServiceDescription = "";
    protected String mServiceDir = "";
    protected String mAppFileName = "";
    protected boolean mPublishAsNLPService = false;
    protected boolean mMergeInputDocs = false;
    /**
     * Parameter list to be shown to the user
     */
    public Vector<GATERuntimeParameter> mParams = new Vector<GATERuntimeParameter>();
    public Vector<String> mInputArtifactTypes = new Vector<String>();
    /**
     * List of those parameters that receive the input document as
     * value. Important for search query parameters etc., when there
     * is no actual input corpus.
     */
    public Vector<GATERuntimeParameter> mParamsTakingInput = new Vector<GATERuntimeParameter>();
    public Vector<GATEPipelineOutput> mOutputArtifacts = new Vector<GATEPipelineOutput>();
    protected HashMap<String, List<String>> mProducedAnnotations = new HashMap<String, List<String>>();
    protected String mConcatenationOf = "";

    public ServiceInfo()
    {
    }

    public ServiceInfo( String name, String desc, String dir )
    {
        mServiceName = name;
        mServiceDescription = desc;
        mServiceDir = dir;
    }

    public boolean hasMandatoryRuntimeParams()
    {
        Vector<GATERuntimeParameter> p = this.mParams;

        for( Iterator<GATERuntimeParameter> it = p.iterator(); it.hasNext(); )
        {
            GATERuntimeParameter param = it.next();

            if( !param.getOptional() )
            {
                return true;
            }
        }

        return false;
    }

    public boolean producesCorpus()
    {

        for( Iterator<GATEPipelineOutput> it = mOutputArtifacts.iterator(); it.hasNext(); )
        {
            GATEPipelineOutput o = it.next();

            if( o.getParameterForResultCorpus() != null )
            {
                return true;
            }
        }

        return false;
    }

    public boolean isConcatenation()
    {
        return (mConcatenationOf != null && !mConcatenationOf.equals( "" ));
    }

    public String getServiceName()
    {
        return mServiceName;
    }

    public void setServiceName( String n )
    {
        mServiceName = n;
    }

    public String getAppFileName()
    {
        return mAppFileName;
    }

    public void setAppFileName( String n )
    {
        mAppFileName = n;
    }

    public String getServiceDescription()
    {
        return mServiceDescription;
    }

    public void setServiceDescription( String d )
    {
        mServiceDescription = d;
    }

    public String getServiceDir()
    {
        return mServiceDir;
    }

    public void setServiceDir( String d )
    {
        mServiceDir = d;
    }

    public String getConcatenationOf()
    {
        return mConcatenationOf;
    }

    public void setConcatenationOf( String c )
    {
        mConcatenationOf = c;
    }

    public void addInputArtifactType( String f )
    {
        mInputArtifactTypes.add( f );
    }

    public boolean getPublishAsNLPService()
    {
        return mPublishAsNLPService;
    }

    public void setPublishAsNLPService( boolean b )
    {
        mPublishAsNLPService = b;
    }

    public boolean getMergeInputDocs()
    {
        return mMergeInputDocs;
    }

    public void setMergeInputDocs( boolean b )
    {
        mMergeInputDocs = b;
    }

    public void addParameter( GATERuntimeParameter p )
    {
        mParams.add( p );
    }

    public void removeParameter( GATERuntimeParameter p )
    {
        mParams.remove( p );
    }

    public void addParamTakingInput( GATERuntimeParameter p )
    {

        boolean added = false;
        for( Iterator<GATERuntimeParameter> it = mParams.iterator(); it.hasNext(); )
        {
            GATERuntimeParameter normalParam = it.next();

            if( normalParam.getParamName().equals( p.getParamName() ) &&
                normalParam.getPRName().equals( p.getPRName() ) )
            {
                mParamsTakingInput.add( normalParam );
                added = true;
                break;
            }

        }

        if( !added )
        {
            mParamsTakingInput.add( p );
        }

    }

    public void removeParamTakingINput( GATERuntimeParameter p )
    {
        mParamsTakingInput.remove( p );
    }

    public void addOutput( GATEPipelineOutput o )
    {
        mOutputArtifacts.add( o );
    }

    public void removeOutput( GATEPipelineOutput o )
    {
        mOutputArtifacts.remove( o );
    }

    /**
     * Specify that the service produces an annotation
     * named <code>aName</code> in the annotation set
     * named <code>asName</code>.
     */
    public void addProducedAnnotation( String asName, String aName )
    {
        if( !mProducedAnnotations.containsKey( asName ) )
        {
            mProducedAnnotations.put( asName, new ArrayList<String>() );
        }

        mProducedAnnotations.get( asName ).add( aName );
    }

    public HashMap<String, List<String>> getProducedAnnotations()
    {
        return mProducedAnnotations;
    }

}
