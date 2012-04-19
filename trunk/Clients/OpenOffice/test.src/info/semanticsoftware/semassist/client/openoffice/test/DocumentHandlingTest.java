package info.semanticsoftware.semassist.client.openoffice.test;

// Tested Code
import info.semanticsoftware.semassist.client.openoffice.utils.UNOUtils;

// JUnit
import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

// Open Office Lib
import com.sun.star.uno.XComponentContext;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.bridge.UnoUrlResolver;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.beans.XPropertySet;
import com.sun.star.uno.UnoRuntime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class DocumentHandlingTest {

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
      
      assertNotNull("OppenOffice context is null", this.ctx);
   }

   @After
   public void tearDown() throws Exception {}


   /**
    * Positive/Negative tests to attempt to load documents into OpenOffice
    * from non-existing and existing files - platform dependent.
    */
   @Test
   public void testCreateDoc1() throws Exception {
      testCreateDoc("dir"+ File.separator +"test.txt");
   }
   @Test
   public void testCreateDoc2() throws Exception {
      testCreateDoc("dir with spaces"+ File.separator +"test.txt");
   }
   private void testCreateDoc(final String testfile) {
      // Attempt to load a document in OpenOffice from a file that does
      // not exist. Any 'Invalid Properties' stack trace in std err are
      // informative details of handled exceptions & are irrelevant for
      // this test.
      assertFalse(UNOUtils.createNewDoc(this.ctx, new File(testfile)));

      // Load a document in OpenOffice from a file that does exist.
      makeTestFile(testfile, "");
      assertTrue(UNOUtils.createNewDoc(this.ctx, new File(testfile)));
   }

   // HELPER METHODS //

   private File makeTestFile(final String filename, final String txt) {
      final File file = new File(filename);
      file.deleteOnExit();
      
      final File dir = file.getParentFile();
      if (dir != null) {
         dir.mkdirs();
      }

      try {
         final BufferedWriter out = new BufferedWriter(new FileWriter(filename));
         out.write(txt);
         out.close();
      } catch (final Exception ex) {
         return null;
      }
      return file;
   }
}
