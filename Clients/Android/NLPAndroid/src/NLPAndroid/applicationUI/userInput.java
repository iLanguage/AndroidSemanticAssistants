/**
   Semantic Assistants - http://www.semanticsoftware.info/semantic-assistants

   This file is part of the Semantic Assistants architecture.

   Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info

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

/**
 * 
 * 
 * @author Chadi Cortbaoui - Concordia University Software Engineering
 * 
 * 	This class takes the user inputs and store them in a userInput Object
 *
 */
package NLPAndroid.applicationUI;
public class userInput {
	
	private String defaultValueString;//For example: for english indexer durm indexer service, Yes/No for the optional cases
	private String label;//For example: label=Number of search results
	private boolean optional;//For example: optional=true
	private String PRName;//For example: PRName=Yahoo PR 
	private String paramName;//For example: paramName=limit 
	private String pipelineName;//For example: pipelineName=Yahoo Search
	private String type;//For example: type=int
	private Integer intValue;//For example: how many results we want for yahoo PR for example

	public String getDefaultValueString() {
		return defaultValueString;
	}


	public void setDefaultValueString(String defaultValueString) {
		this.defaultValueString = defaultValueString;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public boolean isOptional() {
		return optional;
	}


	public void setOptional(boolean optional) {
		this.optional = optional;
	}


	public String getPRName() {
		return PRName;
	}


	public void setPRName(String pRName) {
		PRName = pRName;
	}


	public String getParamName() {
		return paramName;
	}


	public void setParamName(String paramName) {
		this.paramName = paramName;
	}


	public String getPipelineName() {
		return pipelineName;
	}


	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Integer getIntValue() {
		return intValue;
	}


	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
	

}
