package info.semanticsoftware.semassist.client.eclipse.utils;

import info.semanticsoftware.semassist.client.eclipse.Activator;
import info.semanticsoftware.semassist.client.eclipse.handlers.ServiceAgentSingleton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Utils {
	
	private static XMLStreamWriter writer;
	private static XMLOutputFactory factory = XMLOutputFactory.newInstance();
	private static final String PROPERTIES_FILE_PATH = Activator.metadata + System.getProperty("file.separator") + "settings.prefs";

	
	public Utils(){
		
	}
	
	/** 
	 * This method creates a properties file with the host and port values provided by the user.
	 */
	public static void propertiesWriter(String host, String port){
		try{
			writer = factory.createXMLStreamWriter(new FileWriter(PROPERTIES_FILE_PATH));
			
			writer.writeStartDocument();
			writer.writeStartElement("saProperties");
			writer.writeStartElement("settings");
			writer.writeAttribute("default", "false");

			writer.writeStartElement("server");
			writer.writeAttribute("host", host);
			writer.writeAttribute("port", port);
			
			writer.writeEndElement();//server
			writer.writeEndElement();//settings
			writer.writeEndElement();//saProperties

			writer.writeEndDocument();

			writer.flush();

		}catch(IOException e1){
			System.err.println(e1.getMessage());	
		}catch(XMLStreamException e2){
				System.err.println(e2.getMessage());
		}finally{
			try {
				writer.close();
			} catch (XMLStreamException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	/** 
	 * This method creates a properties file with default host and port values.
	 */
	public static void propertiesWriter(){
		try{
			writer = factory.createXMLStreamWriter(new FileWriter(Activator.metadata + System.getProperty("file.separator") + "settings.prefs"));
			
			writer.writeStartDocument();
			writer.writeStartElement("saProperties");
			writer.writeStartElement("settings");
			writer.writeAttribute("default", "true");

			writer.writeStartElement("server");
			writer.writeAttribute("host", "");
			writer.writeAttribute("port", "");
			
			writer.writeEndElement();//server
			writer.writeEndElement();//settings
			writer.writeEndElement();//saProperties

			writer.writeEndDocument();
			
			writer.flush();
			writer.close();
		}catch(XMLStreamException e1){
			System.err.println(e1.getMessage());
		}catch(IOException e2){
			System.err.println(e2.getMessage());			
		}
	}
	
	/** 
	 * This method reads the saved values on the properties file and set the service agent host and port variables.
	 * If no properties file exists, it creates one with default values.
	 */
	public static void propertiesReader(){
		try{		
			File propertiesFile = new File(PROPERTIES_FILE_PATH);
			
			if(propertiesFile.exists()){
				DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = builder.newDocumentBuilder();
				Document doc = docBuilder.parse(propertiesFile);
				doc.getDocumentElement().normalize();
	
				NodeList settings = doc.getElementsByTagName("settings");
				
				for(int i=0; i < settings.getLength(); i++){
					if((settings.item(i).getAttributes().getNamedItem("default").getNodeValue()).equals("false")){
						NodeList server = doc.getElementsByTagName("server");
						for(int j=0; j < server.getLength(); j++){
							String host = server.item(j).getAttributes().getNamedItem("host").getNodeValue();
							ServiceAgentSingleton.setServerHost(host);
							String port = server.item(j).getAttributes().getNamedItem("port").getNodeValue();
							ServiceAgentSingleton.setServerPort(port);
						}
					
					}else{
						ServiceAgentSingleton.setServerHost("");
						ServiceAgentSingleton.setServerPort("");
					}
				}
				
			}else{
				propertiesWriter();
				propertiesReader();
			}
			
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

}
