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
package info.semanticsoftware.semassist.server;

import java.util.*;

import gate.*;
//import gate.creole.*;
//import gate.util.*;

import info.semanticsoftware.semassist.server.util.*;

/**
 * This class is especially intended for sequences of language
 * services. Instances of this class serve as the information that is
 * passed on to the next service once the current service has finished
 * its work.
 */
public class ServiceExecutionStatus
{

    private Corpus mCorpus = null;
    /**
     * A vector of runtime parameters passed by the client
     */
    public Vector<GATERuntimeParameter> mParams;
    /**
     * Runtime parameters that are possible
     */
    public Vector<GATERuntimeParameter> mPossibleParams;
    private long mConnID;
    private UserContext mUserContext;
    public Vector<ServiceInfo> mServiceInfos = new Vector<ServiceInfo>();
    public Vector<GATEPipelineOutput> mExpectedOutputs = null;

    public void setCorpus( Corpus c )
    {
        mCorpus = c;
    }

    public Corpus getCorpus()
    {
        return mCorpus;
    }

    public void setConnID( long id )
    {
        mConnID = id;
    }

    public long getConnID()
    {
        return mConnID;
    }

    public void setUserContext( UserContext u )
    {
        mUserContext = u;
    }

    public UserContext getUserContext()
    {
        return mUserContext;
    }

    public void setServiceInfos( Vector<ServiceInfo> v )
    {
        mServiceInfos = v;
        if( v == null )
        {
            return;
        }

        // Compile all the possible runtime parameters of the
        // language services in the vector
        mPossibleParams = new Vector<GATERuntimeParameter>();
        Iterator<ServiceInfo> it = v.iterator();
        while( it.hasNext() )
        {
            ServiceInfo current = it.next();
            mPossibleParams.addAll( current.mParams );
        }

    }

    public Vector<ServiceInfo> getServiceInfos()
    {
        return mServiceInfos;
    }

    public void setParams( GATERuntimeParameter[] p )
    {
        if( p == null )
        {
            return;
        }
        mParams = new Vector<GATERuntimeParameter>();
        for( int i = 0; i < p.length; i++ )
        {
            mParams.add( p[i] );
        }

    }

}





