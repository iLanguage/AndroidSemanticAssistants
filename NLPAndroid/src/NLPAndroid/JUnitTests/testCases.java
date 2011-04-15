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
 * 	This Class is for JUnit test cases. 
 *  Tested: getting services, and invoke Yahoo service
 */
package NLPAndroid.JUnitTests;

import java.util.ArrayList;
import java.util.List;

import NLPAndroid.applicationUI.userInput;
import NLPAndroid.domainLogic.Service;
import NLPAndroid.domainLogic.WebServiceHandler;
import NLPAndroid.domainLogic.serviceInvocationAddParams;
import NLPAndroid.domainLogic.Utils.webServiceConnectionUtil;
import junit.framework.TestCase;

public class testCases extends TestCase {

    private String methodname;
	
	//THIS IP NEEDS TO BE CHANGED EVERYTIME!!!
	private String url = "http://132.205.214.62:8879/SemAssist?wsdl";
	
	private String namespace = "http://server.semassist.semanticsoftware.info/";
	private String action = "SemanticServiceBroker";
	private List<Service> availableServices;
	private  Service choseService;
	public void test()
	{
		webServiceConnectionUtil wsutil = new webServiceConnectionUtil();
		wsutil.setAction(action);
		wsutil.setNamespace(namespace);
		wsutil.setUrl(url);
		WebServiceHandler test = new WebServiceHandler(wsutil);
		testGetAvailableServices(test,wsutil);
		

			serviceInvocationAddParams usrOptPr = new serviceInvocationAddParams();
			
	        usrOptPr.setConnID(0);
	        usrOptPr.getDocuments().listOfuriList.add("#literal");
	        usrOptPr.getDocuments().listOfuriList.add("http://www.concordia.ca");
	        usrOptPr.getDocuments().listOfuriList.add("http://www.cse.concordia.ca");
	        
	        usrOptPr.getLiteralDocs().item = "John Travolta" ;
	        usrOptPr.getUserCtx().mDocLang ="es";
	        usrOptPr.getUserCtx().mUserLanguages ="en";
	        
	        List<userInput> usrInList =new ArrayList<userInput>();
        	userInput usr = new userInput();
        	usr.setIntValue(5);//5 results test
        	usrInList.add(usr);
	        choseService= availableServices.get(0);//
	        testInvokeYahooService(test, choseService, usrOptPr,usrInList);
		
			
	}
	public void testGetAvailableServices(WebServiceHandler test,webServiceConnectionUtil wsutil)
	{
		methodname = "getAvailableServices";
		wsutil.setMethodname(methodname);
		availableServices = test.getListOfServices();
	}
	public void testInvokeYahooService(WebServiceHandler test,Service chosenService, serviceInvocationAddParams usrOptPr,List<userInput> usrInList)
	{
    	test.invokeServices(chosenService, usrOptPr, usrInList);
	}

}
