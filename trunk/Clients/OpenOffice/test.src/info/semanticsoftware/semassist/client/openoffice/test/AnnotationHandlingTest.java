package info.semanticsoftware.semassist.client.openoffice.test;

// Tested Code
import info.semanticsoftware.semassist.client.openoffice.utils.*;

// JUnit
import org.junit.*;
import static org.junit.Assert.*;

// Open Office Lib
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.XComponentContext;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.bridge.UnoUrlResolver;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.beans.XPropertySet;
import com.sun.star.uno.UnoRuntime;

import java.io.*;


public class AnnotationHandlingTest {

   private XComponentContext ctx;

   @Before
   public void setUp() throws Exception {

      final String cmd = "soffice -nodefault -nologo -headless -accept=\"socket,host=localhost,port=8100;urp\"";
      // TODO: Spawning the openoffice.org server this way does not
      // seem to receive the junit requests. Need to figure out why?
      System.out.println(
         "\nIMPORTANT:\nTests require access to OpenOffice. Manually run the following command.\n"+cmd);
      //final Process proc = Runtime.getRuntime().exec(cmd);
      //Thread.sleep(4000);

      final XComponentContext xcomponentcontext = Bootstrap.createInitialComponentContext(null);
      final XUnoUrlResolver urlResolver = UnoUrlResolver.create(xcomponentcontext);
 
      final Object initialObject = urlResolver.resolve(
         "uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager");
 
      final XMultiComponentFactory xOfficeFactory = 
         (XMultiComponentFactory) UnoRuntime.queryInterface(
            XMultiComponentFactory.class, initialObject);
 
      // retrieve the component context as property (it is not yet exported from the office)
      // Query for the XPropertySet interface.
      final XPropertySet xProperySet = (XPropertySet) UnoRuntime.queryInterface( 
         XPropertySet.class, xOfficeFactory);
 
      // Get the default context from the office server.
      final Object oDefaultContext = xProperySet.getPropertyValue("DefaultContext");
 
      // Query for the interface XComponentContext.
      this.ctx = (XComponentContext) UnoRuntime.queryInterface(
         XComponentContext.class, oDefaultContext);
   }

   @After
   public void tearDown() throws Exception {}


   /**
    * This test ensures all fields of the resulting data model memory
    * structure have been initialized properly.
    */
   @Test
   public void testCursorFocus() throws Exception {
      UNOUtils.createNewDoc(this.ctx, "hello world");

      assertNotNull("OppenOffice context is null", this.ctx);
      UNOUtils.initializeCursor(this.ctx, "");

      //assertNotNull("dbg", obj);               
      //assertTrue("dbg", "expected", "actual");
      //assertEquals("dbg", "expected", "actual");
   }
}
