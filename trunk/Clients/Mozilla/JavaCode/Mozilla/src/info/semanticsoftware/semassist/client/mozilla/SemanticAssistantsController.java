/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the Semantic Assistants Mozilla Extension.
 *
 * The Initial Developer of the Original Code is
 * Semantic Software Lab (http://www.semanticsoftware.info).
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Jason Tan
 *   Kevin Tung
 *   Paola Jimenez
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package info.semanticsoftware.semassist.client.mozilla;

import info.semanticsoftware.semassist.server.GateRuntimeParameter;
import info.semanticsoftware.semassist.server.GateRuntimeParameterArray;
import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.ServiceInfoForClient;
import info.semanticsoftware.semassist.server.ServiceInfoForClientArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import info.semanticsoftware.semassist.client.mozilla.domain.Results;
import info.semanticsoftware.semassist.client.mozilla.domain.Settings;
import info.semanticsoftware.semassist.client.mozilla.presentation.ParamActionListener;
import info.semanticsoftware.semassist.client.mozilla.services.ServiceAgentSingleton;
import info.semanticsoftware.semassist.client.mozilla.services.ServiceInvocationHandler;
import info.semanticsoftware.semassist.csal.RTParamFrame;

public class SemanticAssistantsController {
	
	/** The text to be analyzed */
	private static String text;
	
	/** The parameters dialog */
	protected static JFrame mparamFrame = null;
	
	/** The results from the invoked service */
	private static ArrayList<Results> results;
	
	/** 
	 * Returns the host name as String 
	 * 
	 * @return String Server host name
	 */
	public static String getServerHost() {
		return ServiceAgentSingleton.getServerHost();
	}

	/** 
	 * Sets the host name with the value provided 
	 * 
	 * @param value The value provided as host name
	 */
	public static void setServerHost(String value) {
		ServiceAgentSingleton.setServerHost(value);
	}

	/** 
	 * Returns the port number as String 
	 * 
	 * @return String Server port number
	 */
	public static String getServerPort() {
		return ServiceAgentSingleton.getServerPort();
	}

	/** 
	 * Sets the port number with the value provided 
	 * 
	 * @param value The value provided as port number
	 */
	public static void setServerPort(String value) {
		ServiceAgentSingleton.setServerPort(value);
	}
	
	/** 
	 * Returns the list of results
	 * 
	 * @return ArrayList<Results> the list of results
	 */
	public static ArrayList<Results> getResults() {
		return results;
	}
	
	/** 
	 * Gets the list of services and populates the Settings class
	 * 
	 * @param args the text to be analyzed
	 * @return String empty string for success or error message for the exception
	 */
	public static String startSemanticAssistants(String args[]) {
		text = args[0];
		List<String> names = new ArrayList<String>();
		List<String> descriptions = new ArrayList<String>();
		HashMap<String, ServiceInfoForClient> availableServices = new HashMap<String, ServiceInfoForClient>();
		try {
			SemanticServiceBroker agent = ServiceAgentSingleton.getInstance();
			ServiceInfoForClientArray sia = agent.getAvailableServices();

			List<ServiceInfoForClient> results = sia.getItem();
			Iterator<ServiceInfoForClient> it = results.iterator();

			System.out.println( "Start of list of available services" );
			System.out.println();

			while( it.hasNext() ) {
				ServiceInfoForClient info = it.next();
				availableServices.put( info.getServiceName(), info );
				System.out.println( info.getServiceName() );
				System.out.println( info.getServiceDescription() );
				System.out.println();
				names.add( info.getServiceName() );
				descriptions.add( info.getServiceDescription() );
			}

			System.out.println( "End of list of available services" );
			System.out.println();

			Settings.setAvailableServices( availableServices );
		}
		catch( javax.xml.ws.WebServiceException e ) {
			return "Connection error. Possible reasons: Exception on server or server not running.";
		}

		String[] namesArray = new String[ names.size() ];
		for (int i = 0; i < names.size(); i++) {
			namesArray[i] = names.get(i);
		}
		
		String[] descriptionsArray = new String[ descriptions.size() ];
		for (int i = 0; i < descriptions.size(); i++) {
			descriptionsArray[i] = descriptions.get(i);
		}

		Settings.setAvailableServiceNamesArray( namesArray );		
		Settings.setAvailableServiceDescriptionsArray( descriptionsArray );
		return "";
	}

	/** 
	 * Returns the array of available service names
	 * 
	 * @return String array of available service names
	 */
	public static String[] getAvailableServiceNamesArray() {
		return Settings.getAvailableServiceNamesArray();
	}
	
	/** 
	 * Returns the array of available service descriptions
	 * 
	 * @return String array of available service descriptions
	 */
	public static String[] getAvailableServiceDescriptionsArray() {
		return Settings.getAvailableServiceDescriptionsArray();
	}

	/** 
	 * Gets the service corresponding to the specified index. 
	 * 
	 * @param index the index in the service name array at the selected 
	 * @return String return of the runSelectedService method for success, error message for exception 
	 */
	public static String invokeServiceAtIndex(Integer index) {
		String[] availableServiceNamesArray = Settings.getAvailableServiceNamesArray();		
		String serviceName = availableServiceNamesArray[index];		
		Settings.setSelectedServiceName( serviceName );
		
		System.out.println("Service name: " + serviceName);
		System.out.println();
		
		if( serviceName == null || serviceName.equals( "" ) ) {
			return "No Services";
		}
		else {
			System.out.println("Running selected service");
			System.out.println();
			
			return runSelectedService();
		}
	}

	/**
	 * Checks if there are any runtime parameters to pass
	 * to the selected service. If yes, this function brings
	 * up a window where these parameters can be set, and
	 * then invokes the service. If no, it invokes the service
	 * right away.
	 * 
	 * @return String 
	 */
	private static String runSelectedService() {
		try {
			String serviceName = Settings.getSelectedServiceName();
			ServiceInfoForClient info = Settings.getAvailableServices().get( serviceName );
			List<GateRuntimeParameter> params = info.getParams();

			if ( params.iterator().hasNext() ) {
				// avoid recreation
				if ( mparamFrame != null ) {
					if ( !mparamFrame.isVisible() ) {
						mparamFrame = null;
					}
					else {
						return "Error generating paramater frame";
					}
				}

				// There are runtime parameters to take care of
				mparamFrame = buildRTParamFrame( info, params );
				mparamFrame.pack();
				mparamFrame.setLocation( 430, 430 );
				mparamFrame.show(true);
				while (mparamFrame.isVisible()) {
					try {
						Thread.sleep(100);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				System.out.println( "------ Retrieved params array from the frame: " );
				GateRuntimeParameterArray paramsArray = ((RTParamFrame) mparamFrame).getParams();
				List<GateRuntimeParameter> list = paramsArray.getItem();
				Iterator<GateRuntimeParameter> it = list.iterator();

				while ( it.hasNext() ) {
					GateRuntimeParameter p = it.next();
					System.out.println( "------   Parameter: " + p.getParamName() );
				}

				mparamFrame = null;
				return doRunSelectedService(paramsArray);
			}
			else {
				return doRunSelectedService( new GateRuntimeParameterArray() );
			}
		}
		catch( RuntimeException re ) {
			return re.toString();
		}
	}
	
	/**
	 * Builds the parameters dialog. 
	 * 
	 * @param info the information of the service
	 * @param params the list of GateRuntimeParameter
	 * @return JFrame the parameters dialog 
	 */
	private static JFrame buildRTParamFrame( ServiceInfoForClient info, List<GateRuntimeParameter> params ) {
		Vector<GateRuntimeParameter> mandatory = new Vector<GateRuntimeParameter>();
		Vector<GateRuntimeParameter> optional = new Vector<GateRuntimeParameter>();

		for( Iterator<GateRuntimeParameter> it = params.iterator(); it.hasNext(); ) {
			GateRuntimeParameter rtp = it.next();
			if( rtp.isOptional() ) {
				optional.add( rtp );
			}
			else {
				mandatory.add( rtp );
			}
		}

		// Show window for parameter settings
		RTParamFrame frame = new RTParamFrame( info );
		ParamActionListener paramActionListener = new ParamActionListener();
		frame.setOkActionListener( paramActionListener );
		frame.setMandatories( mandatory );
		frame.setOptionals( optional );

		return frame;
	}

	/**
	 * Should not be called directly. Call <code>runSelectedService</code>
	 * instead, or runtime parameters will not be taken into account.
	 * 
	 * @param params the array of GateRuntimeParameter
	 * @return String an empty string 
	 */
	private static String doRunSelectedService( GateRuntimeParameterArray rtpArray ) {
		String arg = text;

		ServiceInvocationHandler handler = new ServiceInvocationHandler();
		handler.setServiceName( Settings.getSelectedServiceName() );
		handler.setArgumentText( arg );

		if ( rtpArray != null ) {
			handler.setRuntimeParameters( rtpArray );
		}

		results = handler.getResults(Settings.getSelectedServiceName());
		
		System.out.println( "Start of results" );
		System.out.println();
		
		for (int i = 0; i < results.size(); i++) {
			System.out.println( results.get(i).getResult() );
		}
		
		System.out.println( "End of results" );
		System.out.println();

		return "";
	}

}
