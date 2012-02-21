package info.semanticsoftware.semassist.server.rest.business;

import info.semanticsoftware.semassist.server.SemanticServiceBroker;
import info.semanticsoftware.semassist.server.SemanticServiceBrokerService;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.xml.namespace.QName;

public class ServiceAgentSingleton
{

    private static SemanticServiceBrokerService service = null;
    private static SemanticServiceBroker agent = null;

    /** Host name value */
	//private static String serverHost ="semassist.ilanguage.ca";
    private static String serverHost ="minion.cs.concordia.ca";
	
    /** Port number value */
	private static String serverPort = "8879";

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
            JOptionPane.showMessageDialog( null,"Server not found. \nPlease check the Server Host and Port",
                    "MalformedURL", JOptionPane.ERROR_MESSAGE );
        }
        catch(  java.lang.NoClassDefFoundError we)
        {
             JOptionPane.showMessageDialog( null,"Please Add the CSAL.jar to your openOffice Java ClassPath",
                    "Connection Refused", JOptionPane.ERROR_MESSAGE );

             return null;
        }
       

        return agent;
    }

    private static String createURL()
    {
    	//return "http://semassist.ilanguage.ca/SemAssist?wsdl";
    	//return "http://minion.cs.concordia:8879/SemAssist?wsdl";
    	//System.out.println("returning " + "http://" + serverHost + "/SemAssist?wsdl");
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