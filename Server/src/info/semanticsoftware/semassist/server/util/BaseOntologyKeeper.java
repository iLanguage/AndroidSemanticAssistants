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
//import java.util.*;
import java.net.URI;


import edu.stanford.smi.protegex.owl.*;
import edu.stanford.smi.protegex.owl.jena.*;
//import edu.stanford.smi.protegex.owl.mModel.triplestore.*;
import edu.stanford.smi.protegex.owl.repository.impl.*;
import edu.stanford.smi.protege.util.*;
//import edu.stanford.smi.protegex.owl.server.triplestore.*;
//import edu.stanford.smi.protegex.owl.mModel.triplestore.impl.TripleStoreImpl;

//import com.hp.hpl.jena.util.*;
public class BaseOntologyKeeper
{

    protected static JenaOWLModel mBaseModelInstance = null;
    protected final static String UPPER_ONTOLOGY_FILE = "ConceptUpper.owl";
    protected final static String SEMASSIST_ONTOLOGY_FILE = "SemanticAssistants.owl";

    public static JenaOWLModel getBaseModel() throws Exception
    {
        if( mBaseModelInstance == null )
        {
            mBaseModelInstance = ProtegeOWL.createJenaOWLModel();

            // The importing file
            URI uri = URIUtilities.createURI( MasterData.Instance().getOntRepository() + SEMASSIST_ONTOLOGY_FILE );
            // System.out.println("------------- URI: " + uri.toString());

            // The imported file
            File file = new File( MasterData.Instance().getOntRepository() + UPPER_ONTOLOGY_FILE );

            // Instead of adding the LocalFileRepository, you can 
            // add any other implementation of the Repository interface
            // System.out.println("\n\nRepository: " + file.getAbsolutePath() + "\n\n");
            mBaseModelInstance.getRepositoryManager().addProjectRepository( new LocalFileRepository( file ) );

            try
            {
                mBaseModelInstance.load( uri, OwlUtils.langXMLAbbrev );
            }
            catch( Exception e )
            {
                Logging.exception( e );
            }



        /*
        mBaseModelInstance = ProtegeOWL.createJenaOWLModel();
        try {
        Logging.log("\n" + URIUtilities.createURI(Utils.getProcessModelFile()) + "\n");

        mBaseModelInstance.load(URIUtilities.createURI(Utils.getProcessModelFile()), Utils.langXMLAbbrev);
        mBaseModelInstance.load(URIUtilities.createURI(Utils.getProcessContextFile()), Utils.langXMLAbbrev);
        Logging.log("Loaded both models");
        } catch (Exception e) {
        Logging.exception(e);
        }
         */

        }

        return mBaseModelInstance;
    }

    public static JenaOWLModel getBaseModelPlus( File f ) throws Exception
    {
        //Logging.log("Entered getBaseModelPlus");

        // Start with empty OWL mModel
        JenaOWLModel result = ProtegeOWL.createJenaOWLModel();


        // The importing file	       
        URI uri = URIUtilities.createURI( f.getAbsolutePath() );
        //Logging.log("Created URI: " + uri);

        // The imported files (all in one directory)
        File repositoryFolder = new File( MasterData.Instance().getOntRepository() );
        //Logging.log("repositoryFolder: " + repositoryFolder.toString());


        // Now set up a repository so that Protege finds the
        // imports specified in f
        result.getRepositoryManager().addProjectRepository( new LocalFolderRepository( repositoryFolder ) );

        try
        {
            result.load( uri, OwlUtils.langXMLAbbrev );
        }
        catch( Exception e )
        {
            Logging.exception( e );
        }

        return result;
    }

    // Temp removed not referenced in any other project
    /*
    public static void ensureSubOntology(File f) 
    {
    JenaOWLModel mModel = BaseOntologyKeeper.getBaseModel();
    TripleStoreModel tsm = mModel.getTripleStoreModel();

    if (tsm.getTripleStore(f.getPath()) == null)
    {
    tsm.createTripleStore(f.getPath());

    }

    }

     */
}
