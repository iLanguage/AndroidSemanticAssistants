/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2012, 2013 Semantic Software Lab, http://www.semanticsoftware.info
Rene Witte
Bahar Sateli

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
package info.semanticsoftware.semassist.client.wiki.servlets;

import info.semanticsoftware.semassist.client.wiki.utils.Wiki;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFileRepository;

/**
 * This class interprets the wiki OWL files in the repository.
 * @author Bahar Sateli
 * */
public class BaseOntologyKeeper {
	//FIXME should this be private?
	/** Jena OWL model object. */
	static JenaOWLModel owlModel = null;

	/** Wikis base OWL model. */
	JenaOWLModel mBaseModelInstance = null;

	/** 
	 * Class constructor that reads the wiki OWL file and keeps a model in memory.
	 */
	public BaseOntologyKeeper(){
		//FIXME fix this to iterate throug .owl files
		File owlFile = new File(SemAssistServlet.ONTOLOGY_REPO+"/MediaWiki.owl");
		owlModel = createServicesModel(owlFile);
	}

	/**
	 * Creates an in memory model of a wiki OWL description file.
	 * @param owlFile wiki owl description file
	 * @return a Jena OWL model
	 * */
	private JenaOWLModel createServicesModel(final File owlFile){
		JenaOWLModel result = getBaseModel();
		ImportHelper importHelper = new ImportHelper(result);

		URI importUri = URIUtilities.createURI(owlFile.getAbsolutePath());
		importHelper.addImport(importUri);

		try{
			importHelper.importOntologies(false);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return result;
	}

	/**
	 * Returns the base model singleton object.
	 * @return base model singleton object
	 * */
	public JenaOWLModel getBaseModel(){
		if( mBaseModelInstance == null ){
			try {
				mBaseModelInstance = ProtegeOWL.createJenaOWLModel();
			} catch (OntologyLoadException e1) {
				e1.printStackTrace();
			}

			// importing wiki OWL file
			URI uri = URIUtilities.createURI(SemAssistServlet.WIKI_ONTOLOGY);
			// System.out.println("------------- URI: " + uri.toString());

			// importing upper wiki OWL file
			File file = new File(SemAssistServlet.UPPER_ONTOLOGY);

			// Instead of adding the LocalFileRepository, you can 
			// add any other implementation of the Repository interface
			//System.out.println("\n\nRepository: " + file.getAbsolutePath() + "\n\n");
			mBaseModelInstance.getRepositoryManager().addProjectRepository( new LocalFileRepository( file ) );

			try{
				//FIXME deprecated code, replace with the new API
				mBaseModelInstance.load( uri, "RDF/XML-ABBREV" );
			}catch( Exception e ){
				System.out.println(e.getMessage());
			}
		}
		return mBaseModelInstance;
	}

	/**
	 * Returns the JENA owl model.
	 * @return JENA OWL model object
	 * */
	public static JenaOWLModel getModel(){
		return owlModel;
	}

	/**
	 * Returns a list of supported wiki engines as in-memory
	 * Java objects.
	 * @return list of supported wiki engine names and versions
	 * */
	public static List<Wiki> getSupportedWikis(){
		List<Wiki> wikiNames = new ArrayList<Wiki>();
		try{
			OWLProperty engine = (OWLProperty) owlModel.getOWLDatatypeProperty("http://localhost/WikiOntology.owl#hasEngine");
			OWLProperty version = (OWLProperty) owlModel.getOWLDatatypeProperty("http://localhost/WikiOntology.owl#hasVersion");

			OWLNamedClass nsClass = owlModel.getOWLNamedClass("http://localhost/WikiOntology.owl#Wiki");
			//FIXME deprecated code, replace with the new API
			Collection<OWLIndividual> instances = nsClass.getInstances(false);

			for (Iterator<OWLIndividual> jt = instances.iterator(); jt.hasNext();) {
				OWLIndividual individual = (OWLIndividual) jt.next();
				wikiNames.add(new Wiki(individual.getPropertyValue(engine).toString(),individual.getPropertyValue(version).toString()));
			}

		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		/*for(int i=0; i < wikiNames.size(); i++){
			System.out.println(wikiNames.get(i).getEngine() + " ver. " + wikiNames.get(i).getVersion());
		}*/
		return wikiNames;
	}
}
