/*
  Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

  This file is part of the Semantic Assistants architecture.

  Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info

  The Semantic Assistants architecture is free software: you can
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

	public static final String XML_HOST_KEY = "host";
	public static final String XML_PORT_KEY = "port";
 	public static final String PROPERTIES_FILE_PATH = System.getProperty("user.home")+ System.getProperty("file.separator") +"semassist-settings.xml";
	
    public static ArrayList<Annotation> mAnnotArray;
    protected static Comparator<Annotation> mByStartCharacter = new CompareByStart();
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

    public static boolean paramHasValue( GateRuntimeParameter p )
    {
        return paramHasValue( p, false );
    }

    public static String defaultServerHost()
    {
        String strHost = "";
        try
        {
            String host = new SemanticServiceBrokerService().getWSDLDocumentLocation().getHost();
            strHost += host;
        }
        catch( Exception ex)
        {
            ex.printStackTrace();
            
        }
        return strHost;
    }

    public static String defaultServerPort()
    {
        String strPort = "";
        try
        {
            int port = new SemanticServiceBrokerService().getWSDLDocumentLocation().getPort();
            strPort += port;

        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }

        return strPort;
    }

    public static boolean paramHasValue( GateRuntimeParameter p, boolean allowEmptyStrings )
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
    public static String getHRResult( String xmlResult )
    {
        Document xmlDoc = getXmlDoc( xmlResult );
        if( xmlDoc == null )
        {
            return "XML file could not be parsed. Sorry.";
        }

        Node root = xmlDoc.getDocumentElement();
        //FIXME check for unused variable here
        Vector<SemanticServiceResult> results = getServiceResults( root );

        // Work with node.getLocalName()

        return xmlDoc.toString();
    }

    public static Vector<SemanticServiceResult> getServiceResults( String xmlResult )
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

    public static File writeStringToFile( String s )
    {
        return writeStringToFile( s, ".xml" );
    }

    public static String getFileNameExt( String mimetype )
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

    public static File writeStringToFile( String s, String ext )
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

    public static File createTempFile( String prefix, String ext )
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

    public static String getRandomFileName( String prefix )
    {
        DateFormat df = DateFormat.getTimeInstance( DateFormat.LONG );
        String ds = df.toString().replace( " ", "" );
        return prefix + ds + ".";
    }

    public static Vector<SemanticServiceResult> getServiceResults( Node root )
    {
        Vector<SemanticServiceResult> result = new Vector<SemanticServiceResult>();
        Node child;

        if( root != null )
        {
            if( root.hasChildNodes() )
            {
                for( child = root.getFirstChild(); child != null; child = child.getNextSibling() )
                {
                    SemanticServiceResult r = getOneResult( child );

                    if( r != null )
                    {
                        result.add( r );
                    }

                    System.out.println( "------------- Node name: " + child.getNodeName() );
                }
            }
        }
        return result;
    }

    protected static SemanticServiceResult getOneResult( Node node )
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

	    if(isBoundless.equals("true"))
            {
                result.mResultType = SemanticServiceResult.BOUNDLESS_ANNOTATION;
            }
            else
            {
                // for side-notes
                result.mResultType = SemanticServiceResult.ANNOTATION;
            }

            // annotation vector, annotation sorted by type

            // annotvector by start
            HashMap<String, AnnotationVector> map = getAnnotationObjectByStart( node );


            result.mAnnotations = map;

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

    protected static Vector<RetrievedDocument> getCorpusDocuments( Node node )
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
    protected static HashMap<String, AnnotationVector> getAnnotationObjects( Node node )
    {
        if( node == null )
        {
            return null;
        }

        NamedNodeMap nm = node.getAttributes();
        String annotationType = nm.getNamedItem( "type" ).getNodeValue();



        Node kid;
        // Use document ID as key, AnnotationVector (not yet annotation
        // instances!) as content
        HashMap<String, AnnotationVector> result = new HashMap<String, AnnotationVector>();

        // Traverse the child nodes of the annotation node, which
        // should be <document> nodes
        if( node.hasChildNodes() )
        {
            int documentCount = 0;

            for( kid = node.getFirstChild(); kid != null; kid = kid.getNextSibling() )
            {

                // kid should be annotation document node now
                if( kid.getNodeName().equals( "document" ) )
                {

                    // Get some document ID
                    NamedNodeMap nmKid = kid.getAttributes();
                    String url = nmKid.getNamedItem( "url" ).getNodeValue();
                    if( url == null || url.equals( "" ) )
                    {
                        url = getDocID( documentCount );
                    }

                    // Get current annotation vector for this document
                    Vector<Annotation> va = getAnnotationsForOneDocument( kid );
                    AnnotationVector anns = new AnnotationVector();
                    anns.mAnnotationVector = va;
                    anns.mType = annotationType;

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

    protected static HashMap<String, AnnotationVector> getAnnotationObjectByStart( Node node )
    {
        if( node == null )
        {
            return null;
        }

        NamedNodeMap nm = node.getAttributes();
        String annotationType = nm.getNamedItem( "type" ).getNodeValue();

        Node kid;
        // Use document ID as key, AnnotationVector (not yet annotation
        // instances!) as content
        HashMap<String, AnnotationVector> result = new HashMap<String, AnnotationVector>();

        // Traverse the child nodes of the annotation node, which
        // should be <document> nodes
        if( node.hasChildNodes() )
        {
            int documentCount = 0;

            for( kid = node.getFirstChild(); kid != null; kid = kid.getNextSibling() )
            {

                // kid should be annotation document node now
                if( kid.getNodeName().equals( "document" ) )
                {

                    // Get some document ID
                    NamedNodeMap nmKid = kid.getAttributes();
                    String url = nmKid.getNamedItem( "url" ).getNodeValue();

                    if( url == null || url.equals( "" ) )
                    {
                        url = getDocID( documentCount );
                    }

                    // Get current annotation vector for this document
                    Vector<Annotation> va = getAnnotationsForOneDocument( kid );
                    AnnotationVector anns = new AnnotationVector();
                    anns.mAnnotationVector = va;
                    anns.mType = annotationType;
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
    protected static String getDocID( int num )
    {
        return (new Integer( num )).toString();
    }

    /**
     * Retrieves annotation vector of annotation instances (Annotation objects)
     * from the DOM tree. All these instances are children of the
     * passed node object, which is typically annotation document node.
     */
    protected static Vector<Annotation> getAnnotationsForOneDocument( Node node )
    {

        if( node == null )
        {
            return null;
        }

        Vector<Annotation> result = new Vector<Annotation>();

        Node kid;
        for( kid = node.getFirstChild(); kid != null; kid = kid.getNextSibling() )
        {

            // kid should be an annotationInstance node now
            if( kid.getNodeName().equals( "annotationInstance" ) )
            {
                Annotation annotation = new Annotation();

                // Get content, start, and end
                NamedNodeMap nm = kid.getAttributes();
                String content = nm.getNamedItem( "content" ).getNodeValue();
                annotation.mContent = content;
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

        return result;
    }

    protected static HashMap<String, String> getAnnotationFeatures( Node node )
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

    public final static String getElementValue( Node elem )
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
    public static Document getXmlDoc( Object obj )
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

    /**
     * Escape characters for text appearing as XML data, between tags.
     *
     * <P>The following characters are replaced with corresponding character entities :
     * <table border='1' cellpadding='3' cellspacing='0'>
     * <tr><th> Character </th><th> Encoding </th></tr>
     * <tr><td> < </td><td> &lt; </td></tr>
     * <tr><td> > </td><td> &gt; </td></tr>
     * <tr><td> & </td><td> &amp; </td></tr>
     * <tr><td> " </td><td> &quot;</td></tr>
     * <tr><td> ' </td><td> &#039;</td></tr>
     * </table>
     *
     * <P>Note that JSTL's {@code <c:out>} escapes the exact same set of
     * characters as this method. <span class='highlight'>That is, {@code <c:out>}
     *  is good for escaping to produce valid XML, but not for producing safe
     *  HTML.</span>
     */
    public static void SortAnnotations(AnnotationVectorArray annotVectorArr)
    {
        mAnnotArray = new ArrayList<Annotation>();

        if(annotVectorArr == null)
        {
            return;
        }

        for(Iterator<AnnotationVector> it = annotVectorArr.mAnnotVectorArray.iterator(); it.hasNext();)
        {
            AnnotationVector annotVector = it.next();

            //Add Annotations to AnnotationArray in order to sort
            CreateAnnotationsArray(annotVector);
        }

        // sort all mAnnotations by start offset
        Collections.sort( mAnnotArray, mByStartCharacter );

    }

    protected static void CreateAnnotationsArray(AnnotationVector annotVector)
    {

        for(Iterator<Annotation> it = annotVector.mAnnotationVector.iterator(); it.hasNext();)
        {
            Annotation annotation = it.next();

            if (annotation.mContent != null && !annotation.mContent.equals( "" ))
            {
                annotation.mType = annotVector.mType;
                System.out.println("annotation.mType: " + annotation.mType);

                mAnnotArray.add(annotation);
            }
        }
    }

   /* public static void setClientPreference(String client, String element, Map<String, String> map){
    		try {		
    			File propertiesFile = new File(PROPERTIES_FILE_PATH);
    			if (propertiesFile.exists()) {
    				DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
    				DocumentBuilder docBuilder = builder.newDocumentBuilder();
    				Document doc = docBuilder.parse(propertiesFile);
    				doc.getDocumentElement().normalize();

    				Element root = doc.getDocumentElement();
    				Element clientTag;
					NodeList clientElement = doc.getElementsByTagName(client);
					if (clientElement.getLength() == 0){
	    				clientTag = doc.createElement(client);
					} else {
	    				clientTag = (Element) clientElement.item(0);
					}

					NodeList children = clientTag.getChildNodes();
					Element elementNode = null;

					for(int i=0; i < children.getLength(); i++){
						if(children.item(i).getNodeName().equals(element)){
							elementNode = (Element) children.item(i);
		    				Set<String> keys = map.keySet();
		        			for(Iterator<String> iterator = keys.iterator(); iterator.hasNext();){
		        	    		String key = iterator.next();
		        	    		String value = map.get(key);
		        	    		elementNode.setAttribute(key, value);
		        	    	}
						}
					}

					if (elementNode == null){
						elementNode = doc.createElement(element);
						Set<String> keys = map.keySet();
	        			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();){
	        	    		String key = iterator.next();
	        	    		String value = map.get(key);
	        	    		elementNode.setAttribute(key, value);
	        	    	}
					}

        			clientTag.appendChild(elementNode);
    				root.appendChild(clientTag);

    				DOMSource source = new DOMSource(doc);
    				StreamResult result = new StreamResult(new FileOutputStream(PROPERTIES_FILE_PATH));

    				TransformerFactory transFactory = TransformerFactory.newInstance();
    				Transformer transformer = transFactory.newTransformer();
    				//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    				transformer.transform(source, result);

    			} else {
    	    		//createPropertiesFile();
    	    		setClientPreference(client, element, map);
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
    }*/
    
    public static void setClientPreference(String client, String element, Map<String, String> map){
		try {		
			File propertiesFile = new File(PROPERTIES_FILE_PATH);
			if (propertiesFile.exists()) {
				DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = builder.newDocumentBuilder();
				Document doc = docBuilder.parse(propertiesFile);
				doc.getDocumentElement().normalize();

				Element root = doc.getDocumentElement();
				Element clientTag;
				NodeList clientElement = doc.getElementsByTagName(client);
				clientTag = (Element) clientElement.item(0);
			
				Element elementNode = doc.createElement(element);
				Set<String> keys = map.keySet();
    			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();){
    	    		String key = iterator.next();
    	    		String value = map.get(key);
    	    		elementNode.setAttribute(key, value);
    	    	}

    			clientTag.appendChild(elementNode);
				root.appendChild(clientTag);

				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new FileOutputStream(PROPERTIES_FILE_PATH));

				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer transformer = transFactory.newTransformer();
				//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(source, result);

			} else {
	    		System.out.println("Error: No properties file found at " + PROPERTIES_FILE_PATH);
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

    public static String getClientPreference(String client, String element, String key){
    	String value = "";
    	File propertiesFile = new File(PROPERTIES_FILE_PATH);
		if(propertiesFile.exists()){
			try {
				DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = builder.newDocumentBuilder();
				Document doc = docBuilder.parse(propertiesFile);
				doc.getDocumentElement().normalize();

				NodeList clientElement = doc.getElementsByTagName(client);
				if(clientElement.getLength() != 0){
    				Element clientTag = (Element) clientElement.item(0);
    				NodeList children = clientTag.getChildNodes();
					Element elementNode = null;

					for(int i=0; i < children.getLength(); i++){
						if(children.item(i).getNodeName().equals(element)){
							elementNode = (Element) children.item(i);
		        	    	value = elementNode.getAttribute(key);
						}
					}

					if(elementNode == null){
						System.out.println("Error: No such element.");
						return "";
					}else if(value.equals("")){
						System.out.println("Error: No such key.");
						return "";
					}else{
						return value;
					}
				}else{
					System.out.println("Error: No such client.");
					return "";
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return value; 
		}else{
			System.out.println("Error: No file found.");
			return "";
		}
    }
    
    public static ArrayList<XMLElementModel> getClientPreference(String client, String element){
    	ArrayList<XMLElementModel> result = new ArrayList<XMLElementModel>();
    	File propertiesFile = new File(PROPERTIES_FILE_PATH);
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
					System.out.println("Error: No such client.");
					return result;
				}else{
					Element clientTag = (Element) clientElement.item(0);
    				NodeList children = clientTag.getChildNodes();
					Element elementNode = null;

					for(int i=0; i < children.getLength(); i++){
						// check if the client has the element
						if(children.item(i).getNodeName().equals(element)){							
							XMLElementModel candid = new XMLElementModel();
							candid.setName(element);
							elementNode = (Element) children.item(i);

							// Get all the attributes and put each pair in the hash map
							NamedNodeMap attrs = elementNode.getAttributes();
							for (int j = 0; j < attrs.getLength(); j++){
								candid.setAttribute(attrs.item(j).getNodeName(), attrs.item(j).getNodeValue());
							}

							result.add(candid);
						}
					}

					// if there is no such element in the XML file, this object is null
					if (elementNode == null) {
						System.out.println("Error: No such element.");
						return result;
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
			System.out.println("Error: No file found.");
			return result;
		}
		return result;
    }
}