package info.semanticsoftware.semassist.server.rest.business;

import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.SemanticServiceBrokerService;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

public class ServiceAgentSingleton
{

	private static SemanticServiceBrokerService service = null;
	private static SemanticServiceBroker agent = null;

	/** Host name value */
	//private static String serverHost ="semassist.ilanguage.ca";
	private static String serverHost ="minion.cs.concordia.ca";

	/** Port number value */
	private static String serverPort = "2011";

	public static synchronized SemanticServiceBroker getInstance()
	{
		try {
			if (!serverHost.isEmpty()) {
				System.out.println("Creating broker using hostname " + serverHost + " and port "+ serverPort );
				service = new SemanticServiceBrokerService( new URL( createURL() ),
						new QName( "http://server.semassist.semanticsoftware.info/",
								"SemanticServiceBrokerService" ) );
			} else {
				System.out.println( "Creating broker using default values");
				service = new SemanticServiceBrokerService();
			}
			agent = service.getSemanticServiceBrokerPort();
		} catch( MalformedURLException e ) {
			//e.printStackTrace();
			System.out.println("Server not found. Please check the Server Host and Port");
		}
		catch(  java.lang.NoClassDefFoundError we)
		{
			System.out.println("Please Add the CSAL.jar to the Java ClassPath. Connection Refused");
			return null;
		}

		return agent;
	}

	private static String createURL()
	{
		return "http://" + serverHost + ":" + serverPort + "/SemAssist?wsdl";
	}

	/**
	 * Sets the host name with the value provided
	 *
	 * @param value The value provided as host name
	 * */
	public static void setServerHost(final String value)
	{
	   serverHost = value;
	}

	/**
	 * Sets the port number with the value provided
	 *
	 * @param value The value provided as port number
	 * */
	public static void setServerPort(final String value )
	{
		serverPort = value;
	}
}