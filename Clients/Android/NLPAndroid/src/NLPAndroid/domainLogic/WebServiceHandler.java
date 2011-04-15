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
 * 	This Class is the main Controller of the application.
 *  It gets the list of services and invoke selected services.
 *  It builds SoapEnvelopes using ksoap2, and Sends/Receive Soap Request/Response respectively
 */

package NLPAndroid.domainLogic;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import android.net.Uri;
import NLPAndroid.domainLogic.*;
import NLPAndroid.domainLogic.WSMatchingClasses.*;
import NLPAndroid.applicationUI.*;
import NLPAndroid.domainLogic.Utils.*;
import NLPAndroid.domainLogic.WSMatchingClasses.gateRuntimeParameter;
import NLPAndroid.domainLogic.Service;
import NLPAndroid.domainLogic.Utils.webServiceConnectionUtil;
import NLPAndroid.domainLogic.WSMatchingClasses.*;


//----------------------------------------------------------------------------------------------------
// This Class handles the server wsdl and its functions, sends a soap request and receives a response
//----------------------------------------------------------------------------------------------------
public class WebServiceHandler 
{
	// PreLoad All available Services into a list
	private List<Service> listOfServices;
	
	// Getting the web service util, ie: url, action, namespace etc...
	private webServiceConnectionUtil wsutil;

	// Constructor
	public WebServiceHandler(webServiceConnectionUtil wsutil)
	{
		// Loading WebService configurations (Stored in phone memory)
		this.wsutil = wsutil;
		
		// Pre-load List of services:
			listOfServices = new ArrayList<Service>();
			preLoadListOfServices();
		
		// For testing purpose, this prints list contenants which can be viewed in the LogCat (Use filter: System.out to view results outputed only)
		printList(listOfServices);
		
	}
	
	

	//---------------------------------------------------------------------
	//			Method To Load Services Into the UI
	//---------------------------------------------------------------------
	
	public List<Service> getListOfServices()
	{
		return listOfServices;
	}
	
	//---------------------------------------------------------------------
	//			Method To PreLoad Services from server
	//---------------------------------------------------------------------
	
	public String preLoadListOfServices()
	{
		
		wsutil.setMethodname("getAvailableServices");
		try
		{
			
			SoapObject request = new SoapObject(wsutil.getNamespace(),wsutil.getMethodname());
			
			SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			soapEnvelope.setOutputSoapObject(request);
			
			AndroidHttpTransport aht = new AndroidHttpTransport(wsutil.getUrl()); 
			
			try 
			{
				aht.call(wsutil.getAction(), soapEnvelope);
				
				SoapObject resultsRequestSOAP = (SoapObject) soapEnvelope.bodyIn;
				SoapObject mainArray = (SoapObject) resultsRequestSOAP.getProperty(0);
				
				for(int i=0;i<mainArray.getPropertyCount();i++)
				{
					SoapObject service = (SoapObject)mainArray.getProperty(i);
					loadAvailableServices(service);					
				}
				
				return "Done with getting Services";
				
				
			} 
			
			catch (Exception e) 
			{
				return "Exception1: " + e.getMessage();
			}
		}
		catch(Exception ex)
		{
			return "Exception2: " + ex.getMessage();
		}
	}
	
	//---------------------------------------------------------------------------
	//		Method that gets Services from the web Service then parse and fill 
	//		gateParameters using reflections. It then creates Service object 
	//		and adds to the list.
	//---------------------------------------------------------------------------
	
	public void loadAvailableServices(SoapObject objArray)
	{
		
		// 	The 3 attributes of a service: 
		// 	- serviceName
		// 	- serviceDescription
		// 	- List of gateParams (some services have more than one object array of gateParams)
		String serviceName=null;
		String serviceDescription=null;
		List<gateRuntimeParameter> gateParamsObjectList =null;//In case we have multiple array of gateParams Objects
		
		/** Service Array Returned From web Service looks like:
		 --------------------------------------------------
				item=anyType //property 0 
				{
					serviceName=Yahoo Search; // property 0 [0]
					serviceDescription=Performs a Yahoo! search and returns the first 10 results; // property 0 [1]
					params=anyType// property 0 [2]
					{
						defaultValueString=10; 
						label=Number of search results; 
						optional=true; 
						PRName=Yahoo PR; 
						paramName=limit; 
						pipelineName=Yahoo Search; 
						type=int; 
						
					}; 
				}; 
		 * 
		 */
		
		PropertyInfo propertyInfo;
		
		//Loop through ONE service at a time depending on how many properties it has:
		for(int i=0;i<objArray.getPropertyCount();i++)
		{
			propertyInfo = new PropertyInfo();
			objArray.getPropertyInfo(i, propertyInfo);
			int gateParamStartIndex = 2;//params if exist, start at getProperty(2) generally, but is modified below just in case.

			if(propertyInfo.name.equalsIgnoreCase("serviceName"))
			{
				serviceName = objArray.getProperty(i).toString();
			}
			if(propertyInfo.name.equalsIgnoreCase("serviceDescription"))
			{
				serviceDescription = objArray.getProperty(1).toString();
			}
			if(propertyInfo.name.equalsIgnoreCase("params"))
			{
				gateRuntimeParameter  grtParm ;
				
				//IMPORTANT:
				gateParamsObjectList = new ArrayList<gateRuntimeParameter>();
				gateParamStartIndex = i;
				i = objArray.getPropertyCount();//The purpose of this i: I have to STOP the outer FOR loop Because as soon as i reach params in the
				//	returned array,  i should just continue Looping on params Array. Once i reach a param array, everything remaining will either 
				//	also be a param array or nothing
				
				int counter = 1;
				
				//Check if there exist gateParams and how many
				while(gateParamStartIndex < objArray.getPropertyCount()) //as long as we didn't reach the end of the array of a service : loop
				{
						SoapObject gateParamArrayFromSoap = (SoapObject) objArray.getProperty(gateParamStartIndex);
						propertyInfo = new PropertyInfo();
						grtParm = new gateRuntimeParameter();  
						 
						for(int j=0;j<gateParamArrayFromSoap.getPropertyCount();j++)
						{
							gateParamArrayFromSoap.getPropertyInfo(j, propertyInfo);
							try {
								//Filling class field values using reflection and dynamic casting
								grtParm.setAttr(propertyInfo.name, gateParamArrayFromSoap.getProperty(j).toString());
								
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SecurityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchFieldException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}//end for
						
						gateParamsObjectList.add(grtParm);
						gateParamStartIndex++;		
						counter++;
						
				}//end while
				
			}//end if prop = params
		}//end for
		
		//--------------------------------------------
		//		Add to the Final List of services
		//--------------------------------------------
		listOfServices.add(new Service(serviceName,serviceDescription,gateParamsObjectList));
		
	}//end function
	
	
	//---------------------------------------------------------------------
	//						Method To InvokeServices
	//---------------------------------------------------------------------
	
	public String invokeServices(Service service, serviceInvocationAddParams usrOptPr,List<userInput> usrInList) {
		
		wsutil.setMethodname("invokeService");
		
		try
		{
			SoapObject sobj = new SoapObject(wsutil.getNamespace(),wsutil.getMethodname());
			
			//------------------------------------------------
			//	Method invokeService requirements from wsdl:
			//------------------------------------------------
			//
			//		<message name="invokeService">
			//			<part name="serviceName" type="xsd:string"/>
			//			<part name="documents" type="tns:uriList"/>
			//			<part name="literalDocs" type="ns1:stringArray"/>
			//			<part name="connID" type="xsd:long"/>
			//			<part name="gateParams" type="tns:gateRuntimeParameterArray"/>
			//			<part name="userCtx" type="tns:userContext"/>
			//		</message>
			//
			//------------------------------------------------
			
			//--------------
			//	serviceName
			//--------------
			sobj.addProperty("serviceName", service.getServiceName());//"Yahoo Search" for example
			
			
			//--------------
			//	documents
			//--------------
			PropertyInfo pi = new PropertyInfo();
	        pi.setName("documents");
	        pi.setValue(usrOptPr.getDocuments());
	        pi.setType(UriList.class);
	        sobj.addProperty(pi);
			
	        //--------------
			//	literalDocs
	        //--------------
			pi = new PropertyInfo();
	        pi.setName("literalDocs");
	        pi.setValue(usrOptPr.getLiteralDocs());
	        pi.setType(stringArray.class);
			sobj.addProperty(pi);
			
			//--------------
			//	connID
	        //--------------
			sobj.addProperty("connID", usrOptPr.getConnID());//add parameters
			
			//--------------
			//	gateParams
	        //--------------
			gateRuntimeParameter gtRTparam;
			gateRuntimeParameterArray gtParamArr = new gateRuntimeParameterArray();
	
			try{
				for(int i=0;i<service.getGateParams().size();i++)
				{	
					try
					{
						gtRTparam = service.getGateParams().get(i);
					}
					catch(NullPointerException ex)
					{
						 gtRTparam = null;
					}
					if(gtRTparam!=null)
					{	
						//-----------------------------------------------------------------------------------------------------
						// 		TEMPORARY RUNTIME FUNCTIONS: from gateParamDictionary (to be removed after server modification
						//-----------------------------------------------------------------------------------------------------
					
							//Each Service discards specific parameters, use this function as a dictionary to know which to discard
							gateParamDictionary.discardParameter(service.getServiceName(), gtRTparam);
							
							//Set the parameters that the user Sets:
							gateParamDictionary.setParameter(service.getServiceName(),usrInList.get(i),gtRTparam,usrOptPr);
							
						//-----------------------------------------------------------------------------------------------------
						//										END RUNTIME TEMPORARY FUNCTIONS
						//-----------------------------------------------------------------------------------------------------
						
							
						// Adding gate parameters object to gate parameter array required by WSDL
						   gtParamArr.item.add(gtRTparam);
					}
				}
			}
			catch(NullPointerException ex)
			{
				gtRTparam = null;
			}
			
			//-->In case no gateParams, gtRTparam will be null and no prob since in wsdl nillable=true
			
			pi = new PropertyInfo();
	        pi.setName("gateParams");
	        pi.setType(gateRuntimeParameterArray.class);
	        pi.setValue(gtParamArr);
			sobj.addProperty(pi);

			//---------------
			//	USER CONTEXT
	        //---------------
			UserContext usctx = new UserContext();
			pi = new PropertyInfo();
	        pi.setName("userCtx");
	        pi.setType(UserContext.class);
	        pi.setValue(usrOptPr.getUserCtx());
	        sobj.addProperty(pi);

	        //------------------------------
			//	START BUILDING ENVELOPPE
	        //------------------------------
			SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			soapEnvelope.setOutputSoapObject(sobj);
			
			
			//---------------------------------------------------------------------------------------
			//		MAPPINGS:	
			//			A mapping tells the ksoap what class to generate.
			//			Complex data types that are not mapped are generated as SoapObjects.
			//			The mapping is required for both the request and the response.
			//---------------------------------------------------------------------------------------
			
			soapEnvelope.addMapping(wsutil.getNamespace(), UriList.class.getSimpleName(), UriList.class);
			soapEnvelope.addMapping(wsutil.getNamespace(), stringArray.class.getSimpleName(), stringArray.class);
			soapEnvelope.addMapping(wsutil.getNamespace(), gateRuntimeParameterArray.class.getSimpleName(), gateRuntimeParameterArray.class);
			soapEnvelope.addMapping(wsutil.getNamespace(), UserContext.class.getSimpleName(), UserContext.class);
			
			//---------------------------------------------------------------------------------------
			//	 	MARSHALLING: 
			//			Marshalling uses java serialization to change Objects to stream of data
			// 			to be unmarshalled on the web service.
			//---------------------------------------------------------------------------------------
			
			Marshal floatMarshal = new MarshalFloat();
			floatMarshal.register(soapEnvelope);
			
			
			AndroidHttpTransport aht = new AndroidHttpTransport(wsutil.getUrl()); 
			String retval = "";
			
			aht.debug = true;
				
			try 
			{
				
				aht.call(wsutil.getAction(), soapEnvelope);
				
				//Importat Outputs to check how the request/Response looks like.. Check Logcat to find these outputs
				System.out.println("aht requestDump is :"+aht.requestDump);
				System.out.println("aht responseDump is :"+aht.responseDump);

				retval += "For method invokeservice, using service:"+service.getServiceName()+", response is "+soapEnvelope.getResponse()+"\n\n";
				return retval;

			} 
			catch (Exception e) 
			{
				System.out.println("Stack trace: ");
				e.printStackTrace();
				return "Exception1: " + e.getMessage()+" AND message ISSSS :" +e.getMessage()+" AND localizedmessage :"+e.getLocalizedMessage();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return "Exception2: " + ex.getMessage();
		}
		}
	
	//---------------------------------------------------------------------
	//			Method USED BY INDEXER SERVICE TO GET FILE HTML
	//---------------------------------------------------------------------
	
	// Problem in this function: 
	//		ksoap2 insists on sending type inside the soap envelope
	//	    On the WSDL type xsd:anyType.
	//		java.lang.URL is supported by android, but ksoap2 is not serializing it.
	//		if we just use the string, we get classCastException on the server (which requires java.lang.URL type)
	//		What to fix? only the parameter url type to be able to let ksoap2 serialize it.
	public String getResultFile(String url)
	{
		wsutil.setMethodname("getResultFile");
		try
		{
			
			SoapObject sobj = new SoapObject(wsutil.getNamespace(),wsutil.getMethodname());
			URL fileURL = new URL(url);
			
	
			PropertyInfo pi = new PropertyInfo();
	        pi.setName("resultFileUrl");
	        pi.setType("anything");
	        pi.setValue(fileURL);
	        sobj.addProperty(pi);

			SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			soapEnvelope.setOutputSoapObject(sobj);
			
			AndroidHttpTransport aht = new AndroidHttpTransport(wsutil.getUrl()); 
			aht.debug = true;
			try 
			{
				aht.call(wsutil.getAction(), soapEnvelope);
				System.out.println("aht requestDump is :"+aht.requestDump);
				System.out.println("aht responseDump is :"+aht.responseDump);
				return soapEnvelope.getResponse().toString();

			} 
			
			catch (Exception e) 
			{
				System.out.println("Exception1: " + e.getMessage());
				return "Exception1: " + e.getMessage();
			}
		}
		catch(Exception ex)
		{
			System.out.println("Exception2: " + ex.getMessage());
			return "Exception2: " + ex.getMessage();
		}
		
	}
	//---------------------------------------------------------------------
	//			TEST Method To Print Services from server
	//---------------------------------------------------------------------
	private void printList(List<Service> listOfServices) {
		for(int i=0;i<listOfServices.size();i++)
		{
			System.out.println("We have the list of services that has the following :\n");
			System.out.println("ServiceNAME : "+listOfServices.get(i).getServiceName()+"\n");
			System.out.println("ServiceDESC : "+listOfServices.get(i).getServiceDescription()+"\n");
			
			
			if(listOfServices.get(i).getGateParams()!=null)
			{
				for(int j=0;j<listOfServices.get(i).getGateParams().size();j++)
				{
					System.out.println("we have "+listOfServices.get(i).getGateParams().size()+" gatePARAMS  Object");
				
					System.out.println("gatePARAMS object: "+j+" : "+listOfServices.get(i).getGateParams().get(j).defaultValueString+"\n");
					System.out.println("gatePARAMS object: "+j+" : "+listOfServices.get(i).getGateParams().get(j).label+"\n");
					System.out.println("gatePARAMS object: "+j+" : "+listOfServices.get(i).getGateParams().get(j).paramName+"\n");
					System.out.println("gatePARAMS object: "+j+" : "+listOfServices.get(i).getGateParams().get(j).pipelineName+"\n");
					System.out.println("gatePARAMS object: "+j+" : "+listOfServices.get(i).getGateParams().get(j).PRName+"\n");
					System.out.println("gatePARAMS object: "+j+" : "+listOfServices.get(i).getGateParams().get(j).type+"\n");
					System.out.println("gatePARAMS object: "+j+" : "+listOfServices.get(i).getGateParams().get(j).optional+"\n");
				}
			}
		}
		
	}

}
