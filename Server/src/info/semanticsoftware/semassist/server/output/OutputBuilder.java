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

import gate.*;

import info.semanticsoftware.semassist.server.util.*;

public abstract class OutputBuilder
{

    public abstract void reset();

    public abstract void addOutput( GATEPipelineOutput o );

    protected abstract String startResponse();

    protected abstract String endResponse();

    protected Corpus mCorpus;

    /**
     * Template method
     */
    public final String getResult()
    {
        // Logging.log("---------------- getResult() called. Result buffer: ");

        StringBuffer resultBuffer = new StringBuffer( startResponse() );

        // Hook
        resultBuffer.append( getActualContents() );

        resultBuffer.append( endResponse() );

        // Logging.log(resultBuffer.toString());


        return resultBuffer.toString();
    }

    protected abstract String getActualContents();

    public void setCorpus( Corpus c )
    {
        mCorpus = c;
    }

    public Corpus getCorpus()
    {
        return mCorpus;
    }

    public boolean corpusNotSet()
    {
        return mCorpus == null;
    }

    public String getEmptyResponse()
    {
        return "";
    }

}


