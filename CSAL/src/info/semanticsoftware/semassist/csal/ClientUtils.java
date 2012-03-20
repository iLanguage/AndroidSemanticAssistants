/*
  Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

  This file is part of the Semantic Assistants architecture.

  Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info

  The Semantic Assistants CSAL is free software: you can
  redistribute and/or modify it under the terms of the GNU Lesser General
  Public License as published by the Free Software Foundation, either
  version 3 of the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.semanticsoftware.semassist.csal;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import info.semanticsoftware.semassist.csal.result.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import info.semanticsoftware.semassist.server.*;

public class ClientUtils
{
	public static final String XML_CLIENT_GLOBAL = "global";
	public static final String XML_HOST_KEY = "host";
	public static final String XML_PORT_KEY = "port";
 	public static final String PROPERTIES_FILE_PATH = System.getProperty("user.home")+ System.getProperty("file.separator");
	
    protected static byte[] ILLEGAL_XML_1_0_CHARS;

    // Symbolic names for supported MIME-types.
    public static final String MIME_TEXT_HTML = "text/html";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    // Symbolic names for supported file-extensions.
    public static final String FILE_EXT_HTML = ".html";
    public static final String FILE_EXT_TEXT = ".txt";

    // Supported immutable MIME-type to file-extension mapping.
    private static final Map<String, String> mMimeToExtMap;
    static {
      final Map<String, String> map = new HashMap<String, String>();
      map.put(MIME_TEXT_HTML, FILE_EXT_HTML);
      map.put(MIME_TEXT_PLAIN, FILE_EXT_TEXT);
      mMimeToExtMap = Collections.unmodifiableMap(map);
    };

    public static boolean paramHasValue( final GateRuntimeParameter p )
    {
        return paramHasValue( p, false );
    }

    public static boolean paramHasValue( final GateRuntimeParameter p, final boolean allowEmptyStrings )
    {
        if( p == null )
        {
            return false;
        }
        String type = p.getType();

        if( type.equals( "double" ) )
        {
            return p.getDoubleValue() != null;
        }
        else if( type.equals( "int" ) )
        {
            return p.getIntValue() != null;
        }
        else if( type.equals( "boolean" ) )
        {
            return p.isBooleanValue() != null;
        }
        else if( type.equals( "string" ) )
        {
            if( allowEmptyStrings )
            {
                return p.getStringValue() != null;
            }
            else
            {
                return (p.getStringValue() != null && !p.getStringValue().equals( "" ));
            }
        }
        else if( type.equals( "url" ) )
        {
            return p.getUrlValue() != null;
        }

        return false;
    }

    /**
     * Produce annotation human-readable string
     */
    public static String getHRResult( final String xmlResult )
    {
        Document xmlDoc = getXmlDoc( xmlResult );
        if( xmlDoc == null )
        {
            return "XML file could not be parsed. Sorry.";
        }

        Node root = xmlDoc.getDocumentElement();
        //FIXME check for unused variable here
        //Vector<SemanticServiceResult> results = getServiceResults( root );

        // Work with node.getLocalName()

        return xmlDoc.toString();
    }

    public static Vector<SemanticServiceResult> getServiceResults( final String xmlResult )
    {
        Document xmlDoc = getXmlDoc( xmlResult );
        if( xmlDoc == null )
        {
            System.out.println( "XML file could not be parsed. Sorry." );
            return null;
        }

        Node root = xmlDoc.getDocumentElement();
        Vector<SemanticServiceResult> results = getServiceResults( root );
        return results;
    }

    public static File writeStringToFile( final String s )
    {
        return writeStringToFile( s, ".xml" );
    }

    public static String getFileNameExt( final String mimetype )
    {
        if (mimetype == null) {
            return null;
        }

        final Iterator<String> iter = mMimeToExtMap.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            if (key.equalsIgnoreCase(mimetype)) {
               return mMimeToExtMap.get(key);
            }
        }
        return null;
    }

    public static File writeStringToFile( final String s, final String ext )
    {
        try
        {
            File f = createTempFile( "input-", ext );
            FileWriter writer = new FileWriter( f );
            BufferedWriter bufWriter = new BufferedWriter( writer );

            bufWriter.write( s );
            bufWriter.flush();
            bufWriter.close();

            return f;
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }

        return null;
    }

    public static File createTempFile()
    {
        return createTempFile( "serviceResult-", ".xml" );
    }

    public static File createTempFile( final String prefix, final String ext )
    {
        String fileName = getRandomFileName( prefix );
        File outFile = null;

        try
        {
            outFile = File.createTempFile( fileName, ext );
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }

        return outFile;
    }

    public static String getRandomFileName( final String prefix )
    {
        DateFormat df = DateFormat.getTimeInstance( DateFormat.LONG );
        String ds = df.toString().replace( " ", "" );
        return prefix + ds + ".";
    }

    private static Vector<SemanticServiceResult> getServiceResults( final Node root )
    {
        final Vector<SemanticServiceResult> result = new Vector<SemanticServiceResult>();

        if( root != null && root.hasChildNodes() )
        {
            for( Node child = root.getFirstChild(); child != null; child = child.getNextSibling() )
            {
               final SemanticServiceResult r = getOneResult( child );

               if( r != null )
               {
                  result.add( r );
               }

               System.out.println( "------------- Node name: " + child.getNodeName() );
            }
        }
        return result;
    }

    protected static SemanticServiceResult getOneResult( final Node node )
    {
        String nodeName = node.getNodeName();
        SemanticServiceResult result = new SemanticServiceResult();

        // Annotation case
        if( nodeName.equals( SemanticServiceResult.ANNOTATION ) )
        {
            NamedNodeMap nm = node.getAttributes();
	    
	    String isBoundless = "";	    
	    try{
		/* NB: This property is not mandatory for pipeline outputs 
		* Therefore, if it's not there, we assume it is false
		*/
		isBoundless = nm.getNamedItem("isBoundless").getNodeValue();

	    }catch(NullPointerException e){
		isBoundless = "false";
	    }
	    
            System.out.println( "------------- isBoundless = " + isBoundless );
            
	    String annotationType = nm.getNamedItem( "annotationSet" ).getNodeValue();
            System.out.println( "------------- annotationSet = " + annotationType );

            result.mResultType = isBoundless.equals("true") ?
               SemanticServiceResult.BOUNDLESS_ANNOTATION : SemanticServiceResult.ANNOTATION;
            result.mAnnotations = getAnnotationObjects( node );
        }
        // File case
        else if( nodeName.equals( SemanticServiceResult.FILE ) )
        {
            result.mResultType = SemanticServiceResult.FILE;
            NamedNodeMap nm = node.getAttributes();

            // Get the document url & mime-type on server.
            result.mFileUrl = nm.getNamedItem( "url" ).getNodeValue();
            result.mMimeType = nm.getNamedItem( "mimeType" ).getNodeValue();
        }
        // Document / Corpus
        else if( nodeName.equals( SemanticServiceResult.CORPUS ) )
        {
            result.mResultType = SemanticServiceResult.CORPUS;
            result.mCorpus = getCorpusDocuments( node );
        }
        else if( nodeName.equals( SemanticServiceResult.DOCUMENT ) )
        {
            result.mResultType = SemanticServiceResult.DOCUMENT;
            NamedNodeMap nm = node.getAttributes();

            // Get file URL on server
            Node urlNode = nm.getNamedItem( "url" );
            result.mFileUrl = urlNode.getNodeValue();
        }
        else if( nodeName.equals( "#text" ) )
        {

            System.out.println( "------------- #text case!! " + nodeName );
            result = null;
        }
        else
        {
            System.out.println( "------------- Unhandled case: " + nodeName );
            result = null;
        }

        return result;
    }

    protected static Vector<RetrievedDocument> getCorpusDocuments( final Node node )
    {
        if( node == null )
        {
            return null;
        }
        Vector<RetrievedDocument> result = new Vector<RetrievedDocument>();

        Node kid;
        for( kid = node.getFirstChild(); kid != null; kid = kid.getNextSibling() )
        {
            // kid should be an outputDocument node now
            if( kid.getNodeName().equals( "outputDocument" ) )
            {
                RetrievedDocument r = new RetrievedDocument();
                NamedNodeMap nm = kid.getAttributes();
                r.url = nm.getNamedItem( "url" ).getNodeValue();
                result.add( r );
            }
        }

        return result;
    }

    /**
     * Input: A node representing one annotation, including documents
     * and annotation instances. Output: HashMap with document ID as
     * key, and AnnotationVector objects as value. These stand for multiple
     * annotation instances.
     */
    protected static HashMap<String, AnnotationVector> getAnnotationObjects( final Node node )
    {
        if( node == null )
        {
            return null;
        }

        final NamedNodeMap nm = node.getAttributes();
        final String annotationType = nm.getNamedItem( "type" ).getNodeValue();

        // Use document ID as key, AnnotationVector (not yet annotation
        // instances!) as content
        final HashMap<String, AnnotationVector> result = new HashMap<String, AnnotationVector>();

        // Traverse the child nodes of the annotation node, which
        // should be <document> nodes
        if( node.hasChildNodes() )
        {
            int documentCount = 0;

            for( Node kid = node.getFirstChild(); kid != null; kid = kid.getNextSibling() )
            {

                // kid should be annotation document node now
                if( kid.getNodeName().equals( "document" ) )
                {
                    // Get some document ID
                    final NamedNodeMap nmKid = kid.getAttributes();
                    String url = nmKid.getNamedItem( "url" ).getNodeValue();
                    if( url == null || url.equals( "" ) )
                    {
                        url = getDocID( documentCount );
                    }

                    // Get current annotation vector for this document
                    final AnnotationVector anns = new AnnotationVector();
                    anns.mType = annotationType;
                    anns.mAnnotationVector = getAnnotationsForOneDocument( kid, anns.mType );

                    // Put the AnnotationVector in the document's space
                    result.put( url, anns );

                }
                else
                {
                    System.out.println( "---------- Strange thing in annotation case: node " +
                                        "name is not \"document\", but " + kid.getNodeName() + "." );
                }

                documentCount++;
            }
        }

        return result;
    }

    // Attention: Giving an ID to annotation document based on their position
    // in the result probably works only if there are no documents
    // that lacks any type of annotation entirely. Content-based ID
    // would be better.
    protected static String getDocID( final int num )
    {
        return (new Integer( num )).toString();
    }

    /**
     * Retrieves annotation vector of annotation instances (Annotation objects)
     * from the DOM tree. All these instances are children of the
     * passed node object, which is typically annotation document node.
     */
    protected static Vector<Annotation> getAnnotationsForOneDocument( final Node node, final String annType )
    {

        if( node == null )
        {
            return null;
        }

        final Vector<Annotation> result = new Vector<Annotation>();

        for( Node kid = node.getFirstChild(); kid != null; kid = kid.getNextSibling() )
        {

            // kid should be an annotationInstance node now
            if( kid.getNodeName().equals( "annotationInstance" ) )
            {
                final Annotation annotation = new Annotation();

                // Get content, start, and end
                final NamedNodeMap nm = kid.getAttributes();
                final String content = nm.getNamedItem( "content" ).getNodeValue();
                annotation.mContent = content;
                annotation.mType = annType;
                annotation.mStart = Long.parseLong( nm.getNamedItem( "start" ).getNodeValue() );
                annotation.mEnd = Long.parseLong( nm.getNamedItem( "end" ).getNodeValue() );
		
		if(kid.getFirstChild() != null)
		{
		    // Get features
		    annotation.mFeatures = getAnnotationFeatures( kid );
		}
		else
		{
		    System.out.println( "------------- This annotation has no features."); 
		}

                // Add to result
                result.add( annotation );
            }
            else
            {
                System.out.println( "---------- Strange thing in annotation case: node " +
                                    "name is not \"annotationInstance\", but " + kid.getNodeName() + "." );
            }
        }

        // Sort annotations by start offset.
        System.out.println("------ Sorting "+ result.size() +" annotationInstance(s) for document.");
        Collections.sort( result, new OffsetComparator() );

        return result;
    }

    protected static HashMap<String, String> getAnnotationFeatures( final Node node )
    {
        if( node == null )
        {
            return null;
        }
        if( node.getFirstChild() == null )
        {
            return null;
        }

        HashMap<String, String> result = new HashMap<String, String>();
        Node kid;
        for( kid = node.getFirstChild(); kid != null; kid = kid.getNextSibling() )
        {

            // kid should be annotation feature node now
            if( kid.getNodeName().equals( "feature" ) )
            {

                // Get name and value
                NamedNodeMap nm = kid.getAttributes();
                String name = nm.getNamedItem( "name" ).getNodeValue();
                String value = nm.getNamedItem( "value" ).getNodeValue();

                // Add to result
                result.put( name, value );
            }
            else
            {
                System.out.println( "---------- Strange thing in annotation case: node " +
                                    "name is not \"feature\", but " + kid.getNodeName() + "." );
            }
        }

        return result;
    }

    public final static String getElementValue( final Node elem )
    {
        Node kid;
        if( elem != null )
        {
            if( elem.hasChildNodes() )
            {
                for( kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling() )
                {
                    if( kid.getNodeType() == Node.TEXT_NODE )
                    {
                        return kid.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    /** Parses XML file and returns XML document.
     * @param obj Object string to parse
     * @return XML document or <B>null</B> if error occured
     */
    public static Document getXmlDoc( final Object obj )
    {

        DocumentBuilder docBuilder;
        Document doc = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace( true );

        try
        {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch( ParserConfigurationException e )
        {
            System.out.println( "Wrong parser configuration: " + e.getMessage() );
            return null;
        }

        try
        {
            if( obj instanceof File )
            {
                doc = docBuilder.parse( (File) obj );

            }
            else if( obj instanceof String )
            {
                String sDoc = (String) obj;
                doc = docBuilder.parse( new InputSource( new StringReader( sDoc ) ) );
            }
            else
            {
                System.out.println("param not File or String ");
                return null;
            }

        }
        catch( SAXException e )
        {
            System.out.println("Wrong XML file structure: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        catch( Throwable e )
        {
            System.out.println("Could not read source string: " + e.getMessage());
        }

        System.out.println("---------------- XML string parsed");

        return doc;
    }

    /*
    public static List<Annotation> sortAnnotations(final AnnotationVectorArray annotVectorArr)
    {
        if (annotVectorArr == null) {
            return null;
        }

        final List<Annotation> result = AnnotationVectorArray.convert();
        Collections.sort(result, new OffsetComparator());

        return result;
    }
    */
    
    /**
     * Finds and sets the attribute values of the specified element in the client scope.
     * If the client does not exist, it first creates the client and then adds the element.
     * If the client, element and attributes all exist, it just updates the attributes with the provided values in the map.
     * @param client the target client scope
     * @param element the target element
     * @param map a hash map of element attributes in form of <key,value> pairs
     */
    public static void setClientPreference(final String client, final String element, final Map<String, String> map){
		try {		
			File propertiesFile = new File(getPropertiesFileName());
			if (propertiesFile.exists()) {
				DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = builder.newDocumentBuilder();
				Document doc = docBuilder.parse(propertiesFile);
				doc.getDocumentElement().normalize();

				Element root = doc.getDocumentElement();
				Element clientTag;
				NodeList clientElement = doc.getElementsByTagName(client);
				Element elementNode = null;
				if(clientElement.getLength() != 0){
    				clientTag = (Element) clientElement.item(0);
    				NodeList children = clientTag.getChildNodes();
					for(int i=0; i < children.getLength(); i++){
						if(children.item(i).getNodeName().equals(element)){
							elementNode = (Element) children.item(i);
						}
					}
					
					// if the client is there, but there is no element tag, let's create it
					if(elementNode == null){
						elementNode = doc.createElement(element);
					}
    				
  				}else{
  					clientTag = doc.createElement(client);
  					elementNode = doc.createElement(element);
  				}
				
				Set<String> keys = map.keySet();
    			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();){
    	    		String key = iterator.next();
    	    		String value = map.get(key);
    	    		elementNode.setAttribute(key, value);
    	    	}

    			clientTag.appendChild(elementNode);
				root.appendChild(clientTag);

				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new FileOutputStream(propertiesFile));

				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer transformer = transFactory.newTransformer();
				//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(source, result);

			} else {
		    	createPropertiesFile();
		    	setClientPreference(client,element,map);
			}
		} catch (IOException e1){
			e1.printStackTrace();
		} catch (SAXException e2) {
			e2.printStackTrace();
		} catch (ParserConfigurationException e3) {
			e3.printStackTrace();
		} catch (TransformerConfigurationException e4) {
			e4.printStackTrace();
		} catch (TransformerException e5) {
			e5.printStackTrace();
		}
    }

    /**
     * Returns a list of elements in the client scope in form of XMLElementModel objects.
     * If the client or element does not exist, it returns an empty list.
     * @see XMLElementModel
     * @param client the target client scope
     * @param element the specific target element to retrieve or null to retrieve all
     *                elements at the @a client scope.
     * @return a list of elements or an empty list if no such client or element exists
     */
    public static ArrayList<XMLElementModel> getClientPreference(final String client, final String element){
    	final ArrayList<XMLElementModel> result = new ArrayList<XMLElementModel>();
    	File propertiesFile = new File(getPropertiesFileName());
		if(propertiesFile.exists()){
			try {
				DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = builder.newDocumentBuilder();
				Document doc = docBuilder.parse(propertiesFile);
				doc.getDocumentElement().normalize();

				// Check if such client exists in the XML file or if there are more than one target
				NodeList clientElement = doc.getElementsByTagName(client);
				if(clientElement.getLength() == 0 || clientElement.getLength() > 1){
					System.out.println("WARNING: Cannot resolve client: \"" + client + "\"");
				}else{
					Element clientTag = (Element) clientElement.item(0);
    				NodeList children = clientTag.getChildNodes();

					for(int i=0; i < children.getLength(); i++){
					   final Element elementNode = (Element) children.item(i);
					   // If not specified, include all preference elements defined
					   // for the client, else keep just the wanted ones.
						if(element == null || elementNode.getNodeName().equals(element)){							
							final XMLElementModel candid = new XMLElementModel();
							candid.setName(elementNode.getNodeName());
                     candid.setAttributes(elementNode.getAttributes());

							result.add(candid);
						}
					}

					// if there is no such element in the XML file, results are empty
					if (result.isEmpty()) {
						System.out.println("WARNING: No \"" + element + "\" element found for client \"" + client + "\"");
					}
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
	    	// Generate property file & recursively retry.
	    	createPropertiesFile();
	    	return getClientPreference(client, element);
		}
		return result;
    }
    
    /**
     * Adds a new server element to the global scope
     * @param map a hash map of server attributes in form of <key,value> pairs
     * */
    public static void addNewServer(final Map<String, String> map){
    	try {		
			File propertiesFile = new File(getPropertiesFileName());
			if (propertiesFile.exists()) {
				DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = builder.newDocumentBuilder();
				Document doc = docBuilder.parse(propertiesFile);
				doc.getDocumentElement().normalize();

				Element root = doc.getDocumentElement();
				Element clientTag;
				NodeList clientElement = doc.getElementsByTagName(XML_CLIENT_GLOBAL);

				if(clientElement.getLength() != 0){
    				clientTag = (Element) clientElement.item(0);
  				}else{
  					clientTag = doc.createElement(XML_CLIENT_GLOBAL);
  				}

				//FIXME should prevent adding duplication
				Element elementNode = doc.createElement("server");
				Set<String> keys = map.keySet();
    			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();){
    	    		String key = iterator.next();
    	    		String value = map.get(key);
    	    		elementNode.setAttribute(key, value);
    	    	}
				
    			clientTag.appendChild(elementNode);
				root.appendChild(clientTag);

				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new FileOutputStream(propertiesFile));

				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer transformer = transFactory.newTransformer();
				//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(source, result);

			} else {
		    	createPropertiesFile();
		    	addNewServer(map);
			}
		} catch (IOException e1){
			e1.printStackTrace();
		} catch (SAXException e2) {
			e2.printStackTrace();
		} catch (ParserConfigurationException e3) {
			e3.printStackTrace();
		} catch (TransformerConfigurationException e4) {
			e4.printStackTrace();
		} catch (TransformerException e5) {
			e5.printStackTrace();
		}
    }
    
    /**
     * Creates a new preference file in the user home directory
     * */
    public static void createPropertiesFile(){
		try {
			File propertiesFile;
			if(System.getProperty("os.name").toLowerCase().equals("windows")){
				propertiesFile = new File(getPropertiesFileName());
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec("attrib -s -h -r " + PROPERTIES_FILE_PATH + "semassist-settings.xml");
				//FIXME what's this for?
				int exitVal = pr.exitValue();
			}else{
				propertiesFile = new File(PROPERTIES_FILE_PATH + ".semassist-settings.xml");
			}
			
	    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder documentBuilder;
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			
			Element root = document.createElement("saProperties");
			Element globalNode = document.createElement(XML_CLIENT_GLOBAL);
			
			Element lastServer = document.createElement("lastCalledServer");
			lastServer.setAttribute(XML_HOST_KEY, "minion.cs.concordia.ca");
			lastServer.setAttribute(XML_PORT_KEY, "8879");
			
			Element serverOne = document.createElement("server");
			serverOne.setAttribute(XML_HOST_KEY, "minion.cs.concordia.ca");
			serverOne.setAttribute(XML_PORT_KEY, "8879");
			
			globalNode.appendChild(lastServer);
			globalNode.appendChild(serverOne);
			root.appendChild(globalNode);
			document.appendChild(root);
	    	
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    	Transformer transformer = transformerFactory.newTransformer();
	    	DOMSource source = new DOMSource(document);
	    	StreamResult result =  new StreamResult(new FileOutputStream(propertiesFile));
 	    	//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    	transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
    }
    
    private static String getPropertiesFileName(){
    	String fileName = "";
    	if(System.getProperty("os.name").toLowerCase().equals("windows")){
			fileName = PROPERTIES_FILE_PATH + "semassist-settings.xml";
		}else{
			fileName = PROPERTIES_FILE_PATH + ".semassist-settings.xml";
		}
    	return fileName;
    }
}
