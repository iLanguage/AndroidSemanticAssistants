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
package info.semanticsoftware.semassist.client.wiki.wikihelper;

import info.semanticsoftware.semassist.client.wiki.servlets.BaseOntologyKeeper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;

/**
 * This class is responsible to resolve MediaWiki ontology file.
 * @author Bahar Sateli
 * */
public class MediaWikiOntologyKeeper extends WikiOntologyKeeper{

	/** Reads the MediaWiki ontology and returns the list of namespaces.
	 * @return List<String> of namespaces
	 */
	public List<String> getNamespaces(){
		List<String> namespaces = new ArrayList<String>();
		try{
			OWLProperty temp = (OWLProperty) BaseOntologyKeeper.getModel().getOWLDatatypeProperty("http://localhost/MediaWiki.owl#NS_Value");
			
			OWLNamedClass nsClass = BaseOntologyKeeper.getModel().getOWLNamedClass("http://localhost/WikiOntology.owl#Namespace");
			Collection<OWLIndividual> instances = nsClass.getInstances(false);
			
			for (Iterator<OWLIndividual> jt = instances.iterator(); jt.hasNext();) {
				OWLIndividual individual = (OWLIndividual) jt.next();
				namespaces.add(individual.getPropertyValue(temp).toString());
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return namespaces;
	}
}
