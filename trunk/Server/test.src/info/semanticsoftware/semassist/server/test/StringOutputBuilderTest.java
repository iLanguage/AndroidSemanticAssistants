package info.semanticsoftware.semassist.server.test;

import org.apache.commons.lang.StringEscapeUtils;

import junit.framework.TestCase;

public class StringOutputBuilderTest extends TestCase{
	public void testIllegalXMLCharacters(){
		String s = "&";
		String e = "&amp;";
		assertEquals(e,StringEscapeUtils.escapeXml(s));	
	}
}
