/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2009, 2010 Semantic Software Lab, http://www.semanticsoftware.info
        Nikolaos Papadakis
        Tom Gitzinger

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
package info.semanticsoftware.semassist.server.util;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;

import gate.*;
import gate.creole.*;



public class GateUtils 
{
     
     public static Corpus getCorpusFromString(String s) 
	  throws gate.creole.ResourceInstantiationException 
	  {
	       // By default, assign standard URL, even if it's meaningless
	       return getCorpusFromString(s, true);
	  }

     public static Corpus getCorpusFromString(String s, boolean assignDefaultUrl) 
	  throws gate.creole.ResourceInstantiationException 
	  {
	       URL param = null;
	       if (assignDefaultUrl) {
		    param = MasterData.getDummyDocumentURL();
	       }
	       return getCorpusFromString(s, param);
	  }
     

     public static Corpus getCorpusFromString(String s, URL docURL) 
	  throws gate.creole.ResourceInstantiationException
     {
	  Corpus corpus = (Corpus) Factory.createResource("gate.corpora.CorpusImpl");
	  Document doc = Factory.newDocument(s);
	  if (docURL != null) {
	       doc.setSourceUrl(docURL);
	  }
	  
	  corpus.add(doc);
	  return corpus;
     }	


     public static void passRuntimeParameter(SerialController controller, 
					      GATERuntimeParameter gateParam) 
	  {
	       GATERuntimeParameter[] params = new GATERuntimeParameter[1];
	       params[0] = gateParam;
	       passRuntimeParameters(controller, params);
	  }
     

     public static void passRuntimeParameters(SerialController controller, 
					  GATERuntimeParameter[] gateParams)
	  {
	       if (gateParams == null) return;
	       
	       Vector<GATERuntimeParameter> v = new Vector<GATERuntimeParameter>();
	       for (int i = 0; i < gateParams.length; i++) {
		    v.add(gateParams[i]);
	       }
	       passRuntimeParameters(controller, v);
	  }

     public static void passRuntimeParameters(SerialController controller, Vector<GATERuntimeParameter> gateParams) 
	  {
	       passRuntimeParameters(controller, gateParams, null);
	  }
     

     /**
      * If service name is null, then it will not be
      * checked. Possibly, gateParams can contain parameter objects
      * for several language services (i.e., GATE pipelines), not just
      * the one represented by controller.
      */
     public static void passRuntimeParameters(SerialController controller, 
					      Vector<GATERuntimeParameter> gateParams, String serviceName) 
	  {
	       if (gateParams == null) return;
	       
	       Collection<ProcessingResource> prs = controller.getPRs();
	       Iterator<ProcessingResource> it = prs.iterator();
	       while (it.hasNext()) {
		    ProcessingResource pr = it.next();
		    String prName = pr.getName();

		    // For the current PR, look for runtime parameters
		    // to assign. If you find any, assign them.
		    // System.out.println("Parameters for resource \"" + mPrName + "\":");
		    Iterator<GATERuntimeParameter> itParam = gateParams.iterator();
		    while (itParam.hasNext()) {
			 GATERuntimeParameter p = itParam.next();
			 if (serviceName != null && !serviceName.equals(p.getPipelineName())) continue;
			 /*
			 Logging.log("---------------- p.getIntValue(): " + p.getIntValue());
			 Logging.log("---------------- p.getBooleanValue(): " + p.getBooleanValue());
			 Logging.log("---------------- p.getDoubleValue(): " + p.getDoubleValue());
			 Logging.log("---------------- p.getStringValue(): " + p.getStringValue());
			 */

			 
			 

			 // Logging.log("---------------- Service name: " + mServiceName + ", p.getPipelineName(): " + p.getPipelineName());

			 if (prName.equals(p.getPRName())) {
			      try {
				   Logging.log("---------------- Setting parameter \"" + p.getParamName()
				                       + "\" to \"" + p.getValueAsObject() + "\" for PR \"" 
						       + p.getPRName() + "\".\n\n");
				   pr.setParameterValue(p.getParamName(), p.getValueAsObject());
			      } catch (ResourceInstantiationException e) {
				   Logging.exception(e);
			      }
			      			      
			 }
			 
		    }

	       }
	  }



     public static boolean passDocToParameters(SerialController serialCtrl, Vector<GATERuntimeParameter> pti, Document doc) 
	  {
	       // Save the document content to a string
	       String docString = GateUtils.getDocAsString(doc);
		    
	       Iterator<GATERuntimeParameter> it = pti.iterator();
	       while (it.hasNext()) {
		    GATERuntimeParameter p = it.next();
		    if (!p.getType().equals(GATERuntimeParameter.STRING)) {
			 String warning = MasterData.ERROR_ANNOUNCEMENT + "It is not allowed to give the (corpus) " +
			      "input of a language service to a  parameter whose type is " + p.getType() + ".";
			 Logging.log(warning);
			 return false;
		    }
			 
		    // Create a copy of the parameter
		    GATERuntimeParameter docParam = new GATERuntimeParameter(p);
		    docParam.setStringValue(docString);
			 
		    // Assign the parameter to the PR
		    GateUtils.passRuntimeParameter(serialCtrl, docParam);
	       }

	       return true;
	  }
     



     public static boolean assignOutputFileLocations(SerialController serialCtrl, Vector<GATEPipelineOutput> outputs) 
	  {
	       
	       Iterator<GATEPipelineOutput> it_output = outputs.iterator();

	       while (it_output.hasNext()) {
		    GATEPipelineOutput o = it_output.next();
		    if (o == null) continue;
		    
		    GATERuntimeParameter ofp = o.getParameterForFileURL();
		    if (ofp != null) {
			 Logging.log("------------- Setting output file parameter " + ofp.getParamName());

			 // Create a temporary file 
			 File outFile = Utils.createTempFile();
			 if (outFile == null) {
			      Logging.log("----------------" +  MasterData.ERROR_ANNOUNCEMENT + 
				   "Could not create temporary file for result output (IOException).");
			      return false;
			 }
			 
		    
			 // Assign the file's URL as value of the runtime parameter
			 URL fileURL = null;
			 try {
			      fileURL = outFile.toURI().toURL();
			      ofp.setUrlValue(fileURL);
			 } catch (MalformedURLException e) {
			      Logging.exception(e);
			      Logging.log(MasterData.ERROR_ANNOUNCEMENT + "Caught MalformedURLException.");
			      return false;
			 } catch (Exception e) {
			      Logging.exception(e);
			 }

			 // Let the output object know where its file is
			 o.setFileURL(fileURL);
			 GateUtils.passRuntimeParameter(serialCtrl, ofp);
		    }
	       }

	       return true;
	  }
     
     


     public static void runApplicationOnCorpus(CorpusController serviceApp, Corpus corpus)
	  throws gate.creole.ExecutionException 
	  {
	       //final Runtime runtime = Runtime.getRuntime();
	       SerialController sctrl = (SerialController) serviceApp;
	       sctrl.addStatusListener(new gate.event.StatusListener() {
			 public void statusChanged(String text) 
			      {
				   Logging.log("---------------- " + text);
				   /*
				   Logging.log("---------------- totalMemory: " + runtime.totalMemory());
				   Logging.log("---------------- maxMemory  : " + runtime.maxMemory());
				   */
			      }
		    });

	       
	       // System.out.println("---------------- Entered runApplicationOnCorpus()");
	       // Type casts necessary
	       if (serviceApp instanceof SerialAnalyserController) {
		    SerialAnalyserController sac = (SerialAnalyserController) serviceApp;
			 
		    Logging.log("---------------- Setting corpus for SerialAnalyserController");
		    sac.setCorpus(corpus);
		    Logging.log("---------------- Executing SerialAnalyserController");
		    sac.execute();
		    Logging.log("---------------- Done executing SerialAnalyserController");
	       } else if (serviceApp instanceof ConditionalSerialAnalyserController) {
		    ConditionalSerialAnalyserController csac = (ConditionalSerialAnalyserController) serviceApp;
			 
		    Logging.log("---------------- Setting corpus for ConditionalSerialAnalyserController");
		    csac.setCorpus(corpus);
		    Logging.log("---------------- Executing ConditionalSerialAnalyserController");
		    csac.execute();
		    Logging.log("---------------- Done executing ConditionalSerialAnalyserController");
	       }
	       //System.out.println("---------------- Leaving runApplicationOnCorpus()");
	  }
     
     public static String getDocAsString(Document doc)
	  {
	       if (doc == null) return null;
	       
	       DocumentContent content = doc.getContent();
	       return content.toString();
	  }


     public static boolean assignResultCorpus(SerialController ctrl, 
					      ServiceInfo info, Corpus resultCorpus) 
	  {
	       Iterator<GATEPipelineOutput> it = info.mOutputArtifacts.iterator();
	       while (it.hasNext()) {
		    GATEPipelineOutput o = it.next();
		    if (o.getParameterForResultCorpus() != null) {
			 // This is the right output artifact
			 GATERuntimeParameter p = o.getParameterForResultCorpus();
			 GATERuntimeParameter assignParam = new GATERuntimeParameter(p);
			 assignParam.setType(GATERuntimeParameter.CORPUS);
			 assignParam.setCorpusValue(resultCorpus);
			 
			 /*
			 if (resultCorpus == null) {
			      Logging.log("---------------- Result corpus is null");
			 }
			 */
			 
			 Logging.log("---------------- Assigning result corpus to parameter " + p.getParamName());
			 Vector<GATERuntimeParameter> v = new Vector<GATERuntimeParameter>();

			 v.add(assignParam);
			 passRuntimeParameters(ctrl, v, info.getServiceName());
			 return true;
		    }
	       }

	       return false;
	  }
     
     public static Document getFirstDocument(Corpus c) 
	  {
	       if (c == null) return null;
	       Iterator<Document> it = c.iterator();
	       if (!it.hasNext()) return null;
	       return it.next();
	  }
     

     
}
