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

package info.semanticsoftware.semassist.service.presentation;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides visual presentation utilities for highlighting annotations.
 * @author Bahar Sateli
 * */
public class AnnotationPresentation {
	
	/** Annotation color map. */
	private static Map<String, String> colorMap = null;
	
	/** Private class singleton object. */
	private static AnnotationPresentation instance = null;
	
	/** Protected class constructor. */
	protected AnnotationPresentation(){
		// defeat instantiation
	}
	
	/**
	 * Provides singleton access to class object.
	 * @return singleton class object
	 * */
	public static AnnotationPresentation getInstance(){
		if(instance == null){
			instance = new AnnotationPresentation();
		}
		return instance;
	}

	/**
	 * Initializes the color map.
	 */
	private static void initColorMap(){
		if(colorMap == null){
			colorMap = new HashMap<String, String>();
			colorMap.put("EnzymeStats", "E0FFFF");
			colorMap.put("Enzyme", "F5DEB3");
			colorMap.put("OrganismStats","ADFF2F");
			colorMap.put("Organism","DEB887");
			colorMap.put("SubstrateStats","A0522D");
			colorMap.put("SubstrateSpecificity","66CDAA");
			colorMap.put("Substrate","5F9EA0");
			colorMap.put("AccessionNumber","4169E1");
			colorMap.put("ActivityAssayConditions","BDB76B");
			colorMap.put("Assay","808000");
			colorMap.put("Family","F08080");
			colorMap.put("Gene","7FFFD4");
			colorMap.put("Glycoside_Hydrolase","D8BFD8");
			colorMap.put("Glycosylation","FFD700");
			colorMap.put("Host","E6EE0F");
			colorMap.put("KineticAssayConditions","DCDCDC");
			colorMap.put("Kinetics","FFF8DC");
			colorMap.put("Laccase","E6E6FA");
			colorMap.put("Lipase","F5F5DC");
			colorMap.put("Peroxidase","E0FFFF");
			colorMap.put("pH","E6EE0F");
			colorMap.put("PMID","FFDAB9");
			colorMap.put("ProductAnalysis","FFF8DC");
			colorMap.put("Reaction","FFFFE0");
			colorMap.put("SpecificActivity","7FFFD4");
			colorMap.put("Strain","FFA500");
			colorMap.put("Temperature","8FBC8F");
		}
	}

	/**
	 * Returns the color associated with this annotation type.
	 * @param type annotation type
	 * @return hex color code if type is known, yellow otherwise
	 * */
	public String findAnnotColor(final String type){
		initColorMap();
		if(colorMap.containsKey(type)){
			return colorMap.get(type);
		}else{
			//FIXME maybe we should do something smarter here? like randomly generate a color and add it to colormap
			System.err.println("[WARNING] Annotation color is not defined for type: " + type);
			return "FF0000"; // Dotted red
		}
	}
}
