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
 * 	This Class has two main Targets:
 * 	1- Discard and set parameters values at Runtime : This can be discarded when proper modifications on the server will be done
 * 	2- Unlock user input tab : to be kept if the approach of unlocking fields of user input will be Used.
 */

package NLPAndroid.domainLogic.Utils;

import java.util.ArrayList;
import java.util.List;
import NLPAndroid.domainLogic.WSMatchingClasses.*;
import NLPAndroid.applicationUI.*;
import NLPAndroid.domainLogic.*;

public class gateParamDictionary {

	
	//Variables that have a number  corresponding  to a specific url box in the UI
	private final static int NB_OF_RESULTS = 0;//Nb of results to display
	private final static int DURM_INDEXER_OUPUTFORMAT  = 1;//Output format. Use 'mediawiki' or 'html'.; 	
	private final static int DURM_INDEXER_GROUPS_SUB_INDEX = 2;//Use groups of words as sub-index
	private final static int DURM_INDEXER_SPLIT_SUB_INDEX = 3;//Split sub-index into single words; 
	
	public gateParamDictionary(){}
	
	
	//----------------------------------------------------------------------
	// 		Part 1: temporary function for setting/discarding parameters
	//----------------------------------------------------------------------
	
	// Currently : Each service needs some parameters to be discarded!
	public static void discardParameter(String serviceName,gateRuntimeParameter grtp)
	{
		if(serviceName.equalsIgnoreCase("English Durm Indexer"))
			{
				grtp.PRName="";
			}
	}

	// And some to be set by the user
	public static void setParameter(String serviceName,userInput usrIn,gateRuntimeParameter grtp,serviceInvocationAddParams usrOptPr)
	{
		if(serviceName.equalsIgnoreCase("English Durm Indexer"))
		{
			//grtp.defaultValueString = usrIn.getDefaultValueString();//HTML,MEDIAWIKI for format
			// true,false or yes/no for the optional part (2 parts)
			
			//Optional: so might be null from textbox, or "" check with jerome 
			//grtp.paramName = usrIn.getParamName();
			grtp.type = usrIn.getType();
				
			//Any urilist that is #literal change it to ""
			for(int i=0;i<usrOptPr.getDocuments().listOfuriList.size();i++)
			{
				if(usrOptPr.getDocuments().listOfuriList.get(i).equalsIgnoreCase("#literal"))
					usrOptPr.getDocuments().listOfuriList.set(i, "") ;
				
			}

		}
		
		if(serviceName.equalsIgnoreCase("Yahoo Search"))
		{
			grtp.intValue = usrIn.getIntValue();//How many results we want
			
			//#literal will only be allowed for yahoo search uriList 
			for(int i=0;i<usrOptPr.getDocuments().listOfuriList.size();i++)
			{
				usrOptPr.getDocuments().listOfuriList.set(i, "#literal") ;
				
			}
			
		}
		if(serviceName.equalsIgnoreCase("IR Information Extractor"))
		{
			grtp.intValue = usrIn.getIntValue();//How many results we want
			//#literal will only be allowed for yahoo search uriList 
			for(int i=0;i<usrOptPr.getDocuments().listOfuriList.size();i++)
			{
				usrOptPr.getDocuments().listOfuriList.set(i, "#literal") ;
				
			}
		}
		
	}
	
	
	//--------------------------------------------------------------------------------
	// 		Part 2: Unlocking User Input Tab
	//				To tell the UI what inputs to unlock for each specific service!
	//--------------------------------------------------------------------------------
	public static List<Integer> unlockUserInputTab(String serviceName)
	{
		List<Integer> unlockList = new ArrayList<Integer>();
		if(serviceName.equalsIgnoreCase("Yahoo Search"))
		{
			unlockList.add(NB_OF_RESULTS);
		}
		if(serviceName.equalsIgnoreCase("IR Information Extractor"))
		{ 
			unlockList.add(NB_OF_RESULTS);
		}
		if(serviceName.equalsIgnoreCase("English Durm Indexer"))
		{
			unlockList.add(DURM_INDEXER_OUPUTFORMAT);
			unlockList.add(DURM_INDEXER_GROUPS_SUB_INDEX);
			unlockList.add(DURM_INDEXER_SPLIT_SUB_INDEX);
		}
		if(serviceName.equalsIgnoreCase("English Yahoo Indexer")){}
		
		return unlockList;
		
	}

}



