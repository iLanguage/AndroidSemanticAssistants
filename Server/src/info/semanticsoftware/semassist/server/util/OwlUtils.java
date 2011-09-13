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
import java.net.URI;

//import edu.stanford.smi.protegex.owl.*;
import edu.stanford.smi.protegex.owl.jena.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protege.util.*;
 

public class OwlUtils 
{

     public static final String langXMLAbbrev               = "RDF/XML-ABBREV";
     public static final String SEMASSIST_NAMESPACE_PREFIX  = "sa";
     public static final String UPPERONT_NAMESPACE_PREFIX   = "cu";
     // Name of the individual representing the GATE annotation format
     public static final String IND_NAME_ANNOTATION_FORMAT  = "Standard_GATEAnnotation_Format";
     public static final String IND_NAME_CORPUS_FORMAT      = "GATETransientCorpus";
     private static final boolean DO_REPORT_NULL            = false;
     


     // Ontology classes and properties
     private static JenaOWLModel model;
     private static OWLNamedClass pipelineClass;
     private static OWLProperty commentProperty;
     private static OWLProperty fileNameProperty;
     private static OWLProperty publishProperty;
     private static OWLProperty mergeInputProperty;
     private static OWLProperty concatenationOfProperty;
     private static OWLProperty giveInputProperty;
     private static OWLProperty consumesInputProperty;

     // Properties related to the produced output artifact(s)
     private static OWLObjectProperty producesOutputProperty;
     private static OWLObjectProperty hasFormatProperty;
     private static OWLObjectProperty urlGivenByParameterProperty;
     private static OWLObjectProperty necessaryParamSettingProperty;
     private static OWLObjectProperty parameterProperty;
     private static OWLProperty mimeTypeProperty;
     private static OWLProperty hrFormatProperty;
     private static OWLProperty valueProperty;     
     private static OWLProperty isPerDocumentProperty; 
     private static OWLProperty isBoundlessProperty;

     // Properties regarding potentional parameters for the pipeline
     private static OWLObjectProperty hasParameterProperty;
     private static OWLObjectProperty isActualArtifactProperty;

     private static OWLProperty hasGATENameProperty;
     private static OWLProperty typeProperty;
     private static OWLProperty optionalProperty;
     private static OWLProperty prNameProperty;
     private static OWLProperty isInAnnotationSetProperty;
     private static OWLProperty hasFeatureProperty;
     private static OWLProperty defaultValueProperty;
     private static OWLProperty labelProperty;
     



     protected static void populateOWLArtifacts(File f) throws Exception
	  {
	       // Gather information from the .owl file
	       model = BaseOntologyKeeper.getBaseModelPlus(f);
	       pipelineClass         = model.getOWLNamedClass(SEMASSIST_NAMESPACE_PREFIX + ":GATEPipeline");
	       commentProperty       = model.getOWLProperty("rdfs:comment");
	       fileNameProperty      = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":appFileName");

	       giveInputProperty     = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":giveInputToParameter");
	       OwlUtils.reportNull(giveInputProperty, "giveInputProperty");
	       consumesInputProperty = model.getOWLProperty(UPPERONT_NAMESPACE_PREFIX + ":consumesInput");
	       OwlUtils.reportNull(consumesInputProperty, "consumesInputProperty");

	       

	       // Properties related to the produced output artifact(s)
	       producesOutputProperty = model.getOWLObjectProperty(
		                                          UPPERONT_NAMESPACE_PREFIX + ":producesOutput");
	       OwlUtils.reportNull(producesOutputProperty, "producesOutputProperty");
	       hasFormatProperty = model.getOWLObjectProperty(
		                                          UPPERONT_NAMESPACE_PREFIX + ":hasFormat");
	       OwlUtils.reportNull(hasFormatProperty, "hasFormatProperty");
	       urlGivenByParameterProperty = model.getOWLObjectProperty(
		                                          SEMASSIST_NAMESPACE_PREFIX + ":urlGivenByParameter");
	       OwlUtils.reportNull(urlGivenByParameterProperty, "urlGivenByParameterProperty");
	       necessaryParamSettingProperty = model.getOWLObjectProperty(
		                                          SEMASSIST_NAMESPACE_PREFIX + ":necessaryParameterSetting");
	       OwlUtils.reportNull(necessaryParamSettingProperty, "necessaryParamSettingProperty");
	       parameterProperty = model.getOWLObjectProperty(
		                                          SEMASSIST_NAMESPACE_PREFIX + ":parameter");
	       OwlUtils.reportNull(parameterProperty, "parameterProperty");


	       // Datatype properties
	       publishProperty           = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":publishAsNLPService");
	       OwlUtils.reportNull(publishProperty, "publishProperty");
	       mergeInputProperty        = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":mergeInputDocuments");
	       OwlUtils.reportNull(mergeInputProperty, "mergeInputProperty");
	       concatenationOfProperty   =  model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":concatenationOfPipelines");
	       OwlUtils.reportNull(concatenationOfProperty, "concatenationOfProperty");
	       mimeTypeProperty          = model.getOWLProperty(UPPERONT_NAMESPACE_PREFIX + ":mimeType");
	       OwlUtils.reportNull(mimeTypeProperty, "mimeTypeProperty");
	       hrFormatProperty          = model.getOWLProperty(UPPERONT_NAMESPACE_PREFIX + ":humanReadableFormat");
	       OwlUtils.reportNull(hrFormatProperty, "hrFormatProperty");
	       valueProperty             = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":value");
	       OwlUtils.reportNull(valueProperty, "valueProperty");
	       isPerDocumentProperty     = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":isPerDocument");
	       OwlUtils.reportNull(isPerDocumentProperty, "isPerDocumentProperty");
	       isInAnnotationSetProperty = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":isInAnnotationSet");
	       OwlUtils.reportNull(isInAnnotationSetProperty, "isInAnnotationSetProperty");
	       hasFeatureProperty        = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":hasFeature");
	       OwlUtils.reportNull(hasFeatureProperty, "hasFeatureProperty");
	       isBoundlessProperty        = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":isBoundless");
	       OwlUtils.reportNull(isBoundlessProperty, "isBoundlessProperty");


	       // Properties regarding potentional parameters for the pipeline
	       hasParameterProperty = model.getOWLObjectProperty(
		    UPPERONT_NAMESPACE_PREFIX + ":hasParameter");
	       OwlUtils.reportNull(hasParameterProperty, "hasParameterProperty");
	       isActualArtifactProperty = model.getOWLObjectProperty(
		    UPPERONT_NAMESPACE_PREFIX + ":isActualArtifact");
	       OwlUtils.reportNull(isActualArtifactProperty, "isActualArtifactProperty");

	       hasGATENameProperty = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":hasGATEName");
	       OwlUtils.reportNull(hasGATENameProperty, "hasGATENameProperty");
	       defaultValueProperty = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":defaultValue");
	       OwlUtils.reportNull(defaultValueProperty, "defaultValueProperty");
	       typeProperty        = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":paramType");
	       OwlUtils.reportNull(typeProperty, "typeProperty");
	       optionalProperty    = model.getOWLProperty(UPPERONT_NAMESPACE_PREFIX  + ":isOptional");
	       OwlUtils.reportNull(optionalProperty, "optionalProperty");
	       prNameProperty      = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":prName");
	       OwlUtils.reportNull(prNameProperty, "prNameProperty");
	       labelProperty       = model.getOWLProperty(UPPERONT_NAMESPACE_PREFIX + ":hasLabel");
	       OwlUtils.reportNull(labelProperty, "labelProperty");

	  }

     


     /**
      * Takes a vector of service description files. Returns 
      * an OWL model containing the upper ontologies plus the
      * instances defined in the service description files.
      */
     public static JenaOWLModel createServicesModel(Vector<File> sdFiles) throws Exception
	  {

	       // Start with OWL model representing the upper ontologies
	       JenaOWLModel result = BaseOntologyKeeper.getBaseModel();
	       ImportHelper importHelper = new ImportHelper(result);

	       // Iterate over service descriptions and import them
	       Iterator<File> it = sdFiles.iterator();
	       while (it.hasNext()) {
		    File current = it.next();
		    URI importUri = URIUtilities.createURI(current.getAbsolutePath());
		    importHelper.addImport(importUri);
	       }
	       
	       try {
		    // Do the actual import
		    importHelper.importOntologies(false);
	       } catch (Exception e) {		
		    Logging.exception(e);
	       }
	       
	       return result;
	  }



     /**
      * Takes a File object which is supposed to represent an .owl
      * file. From this file, the method assembles a ServiceInfo
      * object with information on the service.
      */
     public static ServiceInfo getServiceInfoFromFile(File f) throws Exception
	  {
	       // Get the .owl file
	       // File f = OwlUtils.getServiceDescriptionFile(serviceDir);
	       // System.out.println("------------- getServiceInfo(): received serviceDir = " + serviceDir.toString());
	       // System.out.println("------------- getServiceInfo(): found .owl file: " + f);

	       if (f == null) {
		    return null;
	       }
	       
	       // Create an empty ServiceInfo object
	       ServiceInfo result = new ServiceInfo();
	       // System.out.println("------------Created new empty result object");

	       // Get the OWL class and property objects
	       populateOWLArtifacts(f);

	       
	       Collection instances = pipelineClass.getInstances(false);
	       // System.out.println("------------instances = null: " + (instances == null));

	       // Get the pipeline
	       for (Iterator it = instances.iterator(); it.hasNext();) {
		    // System.out.println("------------Entering for loop");
		    OWLIndividual individual = (OWLIndividual) it.next();
		    // System.out.println("------------pipeline individual = null: " + (individual == null));
		    
		    Object temp = individual.getPropertyValue(hasGATENameProperty);
		    String serviceName = "";
		    if (temp != null) {
			 serviceName = temp.toString();
		    }
		    result.setServiceName(serviceName);

		    temp = individual.getPropertyValue(publishProperty);
		    if (temp != null) {
			 result.setPublishAsNLPService(((Boolean) temp).booleanValue());
		    }

		    temp = individual.getPropertyValue(mergeInputProperty);
		    if (temp != null) {
			 result.setMergeInputDocs(((Boolean) temp).booleanValue());
		    }

		    temp = individual.getPropertyValue(commentProperty);
		    if (temp != null) {
			 result.setServiceDescription(temp.toString());
		    }

		    temp = individual.getPropertyValue(concatenationOfProperty);
		    if (temp != null) {
			 result.setConcatenationOf(temp.toString());
		    }

		    temp = individual.getPropertyValue(fileNameProperty);
		    if (temp != null) {
			 String tempString = temp.toString();
			 String homeDir = System.getProperty("user.home");
			 if (tempString.charAt(0) == '~') {
			      tempString = tempString.replaceFirst("~", homeDir);
			 }
			 
			 result.setAppFileName(tempString);
		    }
		    


		    // Collect the runtime parameters for the PR
		    Collection params = individual.getPropertyValues(hasParameterProperty);
		    if (params != null) {
			 // System.out.println("------------" + serviceName + ": params is not null");
			 for (Iterator itp = params.iterator(); itp.hasNext();) {
			      // Obsolete:
			      // Get the actual parameter object
			      // OWLIndividual inputArtifact = (OWLIndividual) itp.next();
			      // OWLIndividual parameterInd  = (OWLIndividual) inputArtifact.
			      //   getPropertyValueAs(isActualArtifactProperty, OWLIndividual.class);
			 
			      OWLIndividual parameterInd = (OWLIndividual) itp.next();
			      GATERuntimeParameter p = getRTPObject(parameterInd);
			      p.setPipelineName(serviceName);

			      // Obsolete:
			      // The value for isOptional has to be taken from
			      // the inputArtifact, not from the actual parameter
			      // p.setOptional(((Boolean) inputArtifact.getPropertyValue(optionalProperty)).mBooleanValue());
			      result.addParameter(p);
			 }
		    } else {
			 // System.out.println("------------" + serviceName + ": params is null"); 
		    }

		    // See if there are parameters that 
		    // should receive the input document
		    // (e.g., for search queries)
		    Collection paramsTakingInput = individual.getPropertyValues(giveInputProperty);
		    if (paramsTakingInput != null) {

			 for (Iterator itp = paramsTakingInput.iterator(); itp.hasNext();) {
			      OWLIndividual parameterInd = (OWLIndividual) itp.next();
			      GATERuntimeParameter p = getRTPObject(parameterInd);
			      p.setPipelineName(serviceName);
			      result.addParamTakingInput(p);
			 }
		    } else {
			 // System.out.println("------------" + serviceName + ": paramsTakingInput is null"); 
		    }

		    
		    // Find out what the language service takes as input
		    Collection<OWLIndividual> inputArtifacts = individual.getPropertyValues(consumesInputProperty);
		    if (inputArtifacts != null) {
			 Iterator<OWLIndividual> ita = inputArtifacts.iterator();
			 while (ita.hasNext()) {
			      OWLIndividual inpArt = ita.next();
			      String readableFormat = getHRFormat(inpArt);
			      result.addInputArtifactType(readableFormat);
			 }
		    }
		    
		    
		    // List the possible kinds of output of this pipeline
		    Vector<GATEPipelineOutput> outputs = getOutputObjects(individual);
		    passOutputObjects(result, outputs);
		    

		    // There should be only one service description in the file, so...
		    break;
	       }

	       return result;
	  }

     /**
      * Creates a GATERuntimeParameter object out of 
      * an OWL individual
      */
     public static GATERuntimeParameter getRTPObject(OWLIndividual ind) 
	  {
	       GATERuntimeParameter rtp = new GATERuntimeParameter();
	       Object temp = ind.getPropertyValue(prNameProperty);
	       if (temp != null) {
		    rtp.setPRName(temp.toString());
	       }
	       
	       temp = ind.getPropertyValue(typeProperty);
	       if (temp != null) {
		    rtp.setType(temp.toString());
	       }

	       temp = ind.getPropertyValue(hasGATENameProperty);
	       if (temp != null) {
		    rtp.setParamName(temp.toString());
	       }

	       temp = ind.getPropertyValue(defaultValueProperty);
	       if (temp != null) {
		    rtp.setDefaultValueString(temp.toString());
	       }

	       temp = ind.getPropertyValue(labelProperty);
	       if (temp != null) {
		    rtp.setLabel(temp.toString());
	       }
	       
	       temp = ind.getPropertyValue(optionalProperty);
	       if (temp != null) {
		    rtp.setOptional(((Boolean) temp).booleanValue());
	       } else {
		    rtp.setOptional(true);
	       }
	       
	       
	       
	       return rtp;
	  }


     /**
      * Gets, via the hasFormat property and then the
      * humanReadableFormat property, the human readable format of an
      * artifact.
      */
     protected static String getHRFormat(OWLIndividual ind) 
	  {
	       if (ind == null) return null;
	       
	       Collection<OWLIndividual> formats = ind.getPropertyValues(hasFormatProperty);
	       if (formats != null) {
		    Iterator<OWLIndividual> it = formats.iterator();
		    if (!it.hasNext()) return null;
		    
		    OWLIndividual formatInd = it.next();
		    Object temp = formatInd.getPropertyValue(hrFormatProperty);
		    if (temp != null) {
			 return temp.toString();
		    }		    
		    
	       }
	       return null;
	  }
     

     
     public static void passOutputObjects(ServiceInfo si, List<GATEPipelineOutput> list) 
	  {
	       Iterator<GATEPipelineOutput> it = list.iterator();
	       while (it.hasNext()) {
		    si.addOutput(it.next());
	       }	       
	  }
     

     /**
      * The incoming individual should represent a GATE pipeline.abcd
      */
     public static Vector<GATEPipelineOutput> getOutputObjects(OWLIndividual individual) 
	  {
	       Vector<GATEPipelineOutput> result = new Vector<GATEPipelineOutput>();
	       Object temp = individual.getPropertyValue(hasGATENameProperty);
	       String serviceName = "";
	       if (temp != null) {
		    serviceName = temp.toString();
	       }
	       	       
	       Collection outputs = individual.getPropertyValues(producesOutputProperty);
	       for (Iterator ito = outputs.iterator(); ito.hasNext(); ) {
		    GATEPipelineOutput o = new GATEPipelineOutput();
		    OWLIndividual outputInd = (OWLIndividual) ito.next();

		    
		    // Possibly get the mName of the parameter by which
		    // we can specify the target file for this
		    // output. If the parameter is null, we assume
		    // that this output is not produced as a file.
		    OWLIndividual urlParamInd = (OWLIndividual) outputInd.getPropertyValue(urlGivenByParameterProperty);
		    if (urlParamInd != null) {
			 GATERuntimeParameter rtp = getRTPObject(urlParamInd);
			 rtp.setPipelineName(serviceName);
			 o.setParameterForFileURL(rtp);
		    }
		    Boolean tempBoolean = (Boolean) outputInd.getPropertyValue(isPerDocumentProperty);
		    if (tempBoolean != null) {
			 o.setIsPerDocument(tempBoolean.booleanValue());
		    }
		    
		    // Get the format individual associated with the 
		    // output and pass along its information
		    OWLIndividual formatInd = (OWLIndividual) outputInd.getPropertyValue(hasFormatProperty, false);
		    o.setMIMEType(toStringIfNotNull(formatInd.getPropertyValue(mimeTypeProperty)));
		    o.setHRFormat(toStringIfNotNull(formatInd.getPropertyValue(hrFormatProperty)));
		    
		    
		    // If the actual output artifact is an annotation,
		    // get more information from it
		    if (formatInd.getLocalName().equals(IND_NAME_ANNOTATION_FORMAT)) {
			 OWLIndividual annotationInd = (OWLIndividual) outputInd.getPropertyValue(
			      isActualArtifactProperty, false);

			 // Create a new annotation object and fill its slots
			 GATEAnnotation a = new GATEAnnotation();
			 a.mName    = toStringIfNotNull(annotationInd.getPropertyValue(hasGATENameProperty));
			 a.mSetName = toStringIfNotNull(annotationInd.getPropertyValue(isInAnnotationSetProperty));
			 a.mIsBoundless = toStringIfNotNull(annotationInd.getPropertyValue(isBoundlessProperty));
			 Collection features = annotationInd.getPropertyValueLiterals(hasFeatureProperty);
			 Iterator itf = features.iterator();
			 while (itf.hasNext()) {
			      String currentFeature = itf.next().toString();
			      a.addFeature(currentFeature);
			 }

			 // Add annotation object to the output object
			 o.setAnnotation(a);
		    } 


		    

		    // Check if there are parameter settings that are
		    // required for this output to exist
		    Collection requirements  = outputInd.getPropertyValues(necessaryParamSettingProperty);
		    if (requirements != null) {
			 // Iterate over all requirements
			 for (Iterator itr = requirements.iterator(); itr.hasNext(); ) {
			      OWLIndividual paramValuePair = (OWLIndividual) itr.next();
			      OWLIndividual paramInd = (OWLIndividual) paramValuePair.getPropertyValue(parameterProperty, false);
			      GATERuntimeParameter param = getRTPObject(paramInd);
			      param.setPipelineName(serviceName);
			      String requiredValue = toStringIfNotNull(paramValuePair.getPropertyValue(valueProperty));
			      if (requiredValue != null) {
				   param.parseStringAndSetValue(requiredValue);
			      }

			      // If the output artifact is a corpus,
			      // the constraint will hold the corpus
			      // parameter we can set.
			      if (formatInd.getLocalName().equals(IND_NAME_CORPUS_FORMAT)) {
				   Logging.log("---------------- Setting parameter for result corpus: " + 
					       param.getParamName());
				   o.setParameterForResultCorpus(param);
				   param.setType(GATERuntimeParameter.CORPUS);
			      } else {
				   Logging.log("---------------- Adding constraint involving parameter " + 
					       param.getParamName());
				   o.addParameterConstraint(param);
			      }
			      
			 }
		    } else {
			 // Logging.log("---------------- No requirements for output of mType " + formatInd.getLocalName());
		    }
		    
			 
		    // Add this output object to the result
		    result.add(o);
	       }

	       return result;
	  }
     

     /**
      * Checks the list of output artifacts, and returns a new list
      * with only those output artifacts whose requirements are met
      * by the given parameter values.
      */
     public static Vector<GATEPipelineOutput> getExpectedOutputs(
	  Vector<GATEPipelineOutput> outputs, GATERuntimeParameter[] params) 
	  {
	       return getExpectedOutputs(outputs, params, null);
	  }
     

     public static Vector<GATEPipelineOutput> getExpectedOutputs(
	  Vector<GATEPipelineOutput> outputs, GATERuntimeParameter[] params, String serviceName) 
	  {	
	       if (outputs == null) return null;
	       if (params  == null) params = new GATERuntimeParameter[0];

	       Vector<GATERuntimeParameter> v = new Vector<GATERuntimeParameter>();
	       for (int i = 0; i < params.length; i++) {
		    v.add(params[i]);
	       }
	       return getExpectedOutputs(outputs, v, serviceName);
	  }

     public static Vector<GATEPipelineOutput> getExpectedOutputs(
	  Vector<GATEPipelineOutput> outputs, Vector<GATERuntimeParameter> params, String serviceName)
	  {
	       if (outputs == null) return null;
	       if (params  == null) params = new Vector<GATERuntimeParameter>();	  

	       Iterator<GATEPipelineOutput> it = outputs.iterator();
	       Vector<GATEPipelineOutput> result = new Vector<GATEPipelineOutput>();
	       while (it.hasNext()) {
		    GATEPipelineOutput o = it.next();
		    Vector<GATERuntimeParameter> constraints = o.getNecessaryParameterSettings();
		    
		    // Check if the constraints of the output artifact are met
		    if (constraintsAreMet(constraints, params, serviceName)) {
			 result.add(o);
		    }
	       }
	       
	       return result;
	  }


     private static boolean constraintsAreMet(Vector<GATERuntimeParameter> constraints, 
					   GATERuntimeParameter[] params) 
	  {
	       return constraintsAreMet(constraints, params, null);
	  }
     

     private static boolean constraintsAreMet(Vector<GATERuntimeParameter> constraints, 
					   GATERuntimeParameter[] params, String serviceName) 
	  {
	       if (constraints == null) return true;

	       Vector<GATERuntimeParameter> v = new Vector<GATERuntimeParameter>();
	       for (int i = 0; i < params.length; i++) {
		    v.add(params[i]);
	       }
	       return constraintsAreMet(constraints, v, serviceName);
	  }
     
	       
     /**
      * If serviceName is not null, only constraints for the service
      * with this service mName will be checked.
      */
     private static boolean constraintsAreMet(Vector<GATERuntimeParameter> constraints, 
					   Vector<GATERuntimeParameter> params, String serviceName) 
	  {
	       if (constraints == null) return true;
	       
	       Iterator<GATERuntimeParameter> it = constraints.iterator();
	       while (it.hasNext()) {
		    GATERuntimeParameter p = it.next();
		    if (!p.getPipelineName().equals("") && !p.getPipelineName().equals(serviceName)) {
			 continue;
		    }
		    
		    // System.out.println("--------------- Current parameter constraint: " + p.getParamName());
		    boolean paramFound = false;
		    String paramName   = p.getParamName();
		    String prName      = p.getPRName();
		    
		    Iterator<GATERuntimeParameter> it2 = params.iterator();
		    while (it2.hasNext()) {
			 GATERuntimeParameter other = it2.next();
			 if (other == null) {
			      continue;
			 }
			 
			 // System.out.println("--------------- Other parameter: " + other.getParamName());
			 String otherName = other.getParamName();
			 
			 if (paramName.equals(other.getParamName()) && prName.equals(other.getPRName())) {
			      paramFound = true;
			      if (!p.valuesMatch(other)) {
				   return false;
			      }
			 }
		    }
		    // If the parameter required is not given, this
		    // constraint is not met. Return false
		    if (!paramFound) {
			 return false;
		    }
	       }

	       return true;
	  }
     
     

     /*
       Old version
     public static ServiceInfo getServiceInfo(File serviceDir) 
	  {
	       // Get the .owl file
	       File f = OwlUtils.getServiceDescriptionFile(serviceDir);
	       // System.out.println("------------- getServiceInfo(): received serviceDir = " + serviceDir.toString());
	       // System.out.println("------------- getServiceInfo(): found .owl file: " + f);
	       if (f == null) {
		    return null;
	       }
	       
	       // Create an empty ServiceInfo object
	       ServiceInfo result = new ServiceInfo();
	       
	       // Gather information from the .owl file
	       JenaOWLModel model = BaseOntologyKeeper.getBaseModelPlus(f);
	       OWLNamedClass pipelineClass      = model.getOWLNamedClass(SEMASSIST_NAMESPACE_PREFIX + ":GATEPipeline");
	       OWLProperty commentProperty      = model.getOWLProperty("rdfs:comment");
	       OWLProperty fileNameProperty     = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":appFileName");
	       OWLProperty outputAsFileProperty = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":producesOutputAsFile");

	       // Properties related to the (possibly) 
	       // produced relevant annotations
	       OWLObjectProperty producesAnnProperty = model.getOWLObjectProperty(
		                                          SEMASSIST_NAMESPACE_PREFIX + ":producesAnnotation");
	       OWLObjectProperty containedInProperty = model.getOWLObjectProperty(
		                                          SEMASSIST_NAMESPACE_PREFIX + ":isContainedIn");


	       // Properties regarding potentional parameters for the pipeline
	       OWLObjectProperty hasParameterProperty = model.getOWLObjectProperty(
		    SEMASSIST_NAMESPACE_PREFIX + ":hasParameter");
	       OWLObjectProperty hasOutFileParameterProperty = model.getOWLObjectProperty(
		    SEMASSIST_NAMESPACE_PREFIX + ":hasOutputFileParameter");
	       OWLProperty nameProperty        = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":gateName");
	       OWLProperty typeProperty        = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":paramType");
	       OWLProperty optionalProperty    = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":isOptional");
	       //OWLProperty prClassnameProperty = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":prClassname");
	       OWLProperty prNameProperty      = model.getOWLProperty(SEMASSIST_NAMESPACE_PREFIX + ":mPrName");
	       // System.out.println("------------nameProperty = null: " + (nameProperty == null));

	       
	       Collection instances = pipelineClass.getInstances(false);
	       // System.out.println("------------instances = null: " + (instances == null));

	       for (Iterator it = instances.iterator(); it.hasNext();) {
		    OWLIndividual individual = (OWLIndividual) it.next();
		    // String serviceName = individual.getPropertyValue(nameProperty).toString();
		    // System.out.println("--------------- serviceName: " + serviceName);
		    String serviceName = individual.getPropertyValue(nameProperty).toString();
		    result.setServiceName(serviceName);
		    result.setServiceDescription(individual.getPropertyValue(commentProperty).toString());
		    result.setAppFileName(individual.getPropertyValue(fileNameProperty).toString());
		    Boolean poaf = ((Boolean) individual.getPropertyValue(outputAsFileProperty)).mBooleanValue();
		    result.setProducesOutputAsFile(poaf);

		    // Collect the runtime parameters for the PR
		    Collection params = individual.getPropertyValues(hasParameterProperty);
		    for (Iterator itp = params.iterator(); itp.hasNext();) {
			 GATERuntimeParameter p = new GATERuntimeParameter();
			 OWLIndividual ip = (OWLIndividual) itp.next();
			 p.setParamName(ip.getPropertyValue(nameProperty).toString());
			 p.setType(ip.getPropertyValue(typeProperty).toString());
			 p.setPRName(ip.getPropertyValue(prNameProperty).toString());
			 p.setOptional((Boolean)ip.getPropertyValue(optionalProperty));
			 result.addParameter(p);
		    }

		    // Possibly collect the runtime parameter 
		    // representing the resulting output file
		    Collection outFiles = individual.getPropertyValues(hasOutFileParameterProperty);
		    Iterator<OWLIndividual> itOut = outFiles.iterator();
		    if (itOut.hasNext()) {
			 GATERuntimeParameter ofp = new GATERuntimeParameter();
			 OWLIndividual ip = (OWLIndividual) itOut.next();
			 ofp.setType(GATERuntimeParameter.URL_TYPE);
			 ofp.setParamName(ip.getPropertyValue(nameProperty).toString());
			 ofp.setPRName(ip.getPropertyValue(prNameProperty).toString());
			 result.setOutFileParameter(ofp);
		    } else {
			 if (poaf.mBooleanValue()) {
			      Logging.log("Warning: Service description for service \"" + serviceName +
					  "\" says it produces output as file, but no parameter to set " +
					  "the output file has been provided.");
			 }
		    }
		    
		    

		    // Find out which annotations the PR produces
		    Collection annos = individual.getPropertyValues(producesAnnProperty);
		    for (Iterator ita = annos.iterator(); ita.hasNext();) {
			 OWLIndividual a    = (OWLIndividual) ita.next();
			 String annName     = a.getPropertyValue(nameProperty).toString();
			 OWLIndividual aSet = (OWLIndividual) a.getPropertyValue(containedInProperty, false);
			 String asName      = aSet.getPropertyValue(nameProperty).toString();

			 result.addProducedAnnotation(asName, annName);
			 
		    }
		    		    
		    // There should be only one service description in the file, so...
		    break;
	       }

	       return result;
	  }     
     */

     
     
     protected static String serviceNameToDir(String name) 
	  {
	       // For now don't do anything
	       return name;
	  }


     /**
      * Not used at the moment because file names
      * are supposed to be absolute (better separation
      * of architecture and plugged-in language 
      * services).
      */ 
     public static File getServiceAppFile(ServiceInfo si) 
	  {
	       String serviceDir = si.getServiceDir();
	       String sep = File.separator;
	       File serviceAppFile = new File(MasterData.Instance().getServiceRepository() + sep +
					      serviceDir + sep + si.getAppFileName());
	       return serviceAppFile;
	  }
     
     

   
     


     public static String getServiceDescriptionFromFile(File f) throws Exception
	  {
	       // Get a base model and have the contents
	       // of f added to it
	       // System.out.println("Calling getBaseModelPlus");
	       JenaOWLModel model = BaseOntologyKeeper.getBaseModelPlus(f);
	       // System.out.println("------------model = null: " + (model == null));
	       
	       OWLNamedClass pipelineClass = model.getOWLNamedClass(SEMASSIST_NAMESPACE_PREFIX + ":GATEPipeline");
	       OWLProperty commentProperty = model.getOWLProperty("rdfs:comment");

	       // System.out.println("------------pipelineClass = null: " + (pipelineClass == null));
	       // System.out.println("------------commentProperty = null: " + (commentProperty == null));
	       
	       Collection instances = pipelineClass.getInstances(false);
	       // System.out.println("------------instances = null: " + (instances == null));

	       for (Iterator it = instances.iterator(); it.hasNext();) {
		    OWLIndividual individual = (OWLIndividual) it.next();
		    return toStringIfNotNull(individual.getPropertyValue(commentProperty));
	       }

	       return "";
	  }


     public static File getServiceDescriptionFile(File serviceDir) 
	  {
	       
	       Iterator<File> files = Arrays.asList(serviceDir.listFiles()).iterator();
	       
               // Iterate over the files in the service directory
	       while (files.hasNext()) {
		    File currentFile = files.next();
		    
		    // Return the first .owl file we find
		    if (currentFile.getName().endsWith(".owl")) {
			 return currentFile;
		    }
		    
	       }
	       return null;
	  }
     

     public static String getServiceDescription(File serviceDir) throws Exception
	  {
	       File f = OwlUtils.getServiceDescriptionFile(serviceDir);
	       if (f == null) {
		    return "";
	       }
	       
	       return OwlUtils.getServiceDescriptionFromFile(f);
	  }
     


     /** 
      * 
      */
     public static boolean serviceDirExists(String serviceName) 
	  {
	       // System.out.println("Repository: " + MasterData.serviceRepository);
	       String s = MasterData.Instance().getServiceRepository() + File.separator + serviceNameToDir(serviceName);
	       File service = new File(s);
	       //System.out.println("serviceExists checking: " + s);
	       return (service.exists() && service.isDirectory());
	  }


     public static void reportNull(Object testee, String varName) 
	  {    
	       if (testee == null && DO_REPORT_NULL) {
		    System.out.println("------------" + varName + "  = null: " + (testee == null));
	       }
	  }
     

     private static String toStringIfNotNull(Object o) 
	  {
	       if (o == null) return "";
	       else return o.toString();
	  }

     private static Boolean toBooleanIfNotNull(Object o) 
	  {
	       if (o == null) return null;
	       
	       if (o instanceof Boolean) {
		    return (Boolean) o;
	       } else {
		    return null;
	       }	       
	  }
     


/*
     public static SemanticService getSemanticServiceData(String serviceName) 
	  {
	       String s = MasterData.getServiceRepository() + File.separator + serviceNameToDir(serviceName);

	       File serviceDir = new File(s);
	       if (!serviceDir.exists()) return null;
	       
	       File descFile = getServiceDescriptionFile(serviceDir);
	       if (descFile == null) return null;

	       JenaOWLModel model = BaseOntologyKeeper.getBaseModelPlus(descFile);
	       SemanticService result = new SemanticService();
	       
	       
	       // Relevant properties
	       OWLNamedClass pipelineClass             = model.getOWLNamedClass(
		                                          MasterData.OWL_PREFIX_SEMASSIST_NAMESPACE + ":GATEPipeline");
	       // System.out.println("------------pipelineClass = null: " + (pipelineClass == null));
	       OWLObjectProperty producesASProperty    = model.getOWLObjectProperty(
		                                          MasterData.OWL_PREFIX_SEMASSIST_NAMESPACE + ":producesAnnotationSet");
	       // System.out.println("------------producesASProperty = null: " + (producesASProperty == null));
	       OWLDatatypeProperty annSetNameProperty  = model.getOWLDatatypeProperty(
		                                          MasterData.OWL_PREFIX_SEMASSIST_NAMESPACE + ":ASName");
	       // System.out.println("------------ASNameProperty = null: " + (annSetNameProperty == null));
	       OWLDatatypeProperty serviceTypeProperty = model.getOWLDatatypeProperty(
		                                          MasterData.OWL_PREFIX_SEMASSIST_NAMESPACE + ":semanticServiceType");
	       // System.out.println("------------serviceTypeProperty = null: " + (serviceTypeProperty == null));

	       OWLProperty commentProperty             = model.getOWLProperty("rdfs:comment");

	       
	       

	       
	       Collection instances = pipelineClass.getInstances(false);
	       // System.out.println("------------instances = null: " + (instances == null));


	       // There should only be one such pipeline
	       for (Iterator it = instances.iterator(); it.hasNext();) {
		    OWLIndividual individual = (OWLIndividual) it.next();

		    // Name
		    result.mName = individual.getName();

		    // Description
		    result.description = individual.getPropertyValue(commentProperty).toString();

		    // Service mType
		    result.mType = individual.getPropertyValue(serviceTypeProperty).toString();
		    

		    // Produced annotation sets
		    Collection c = individual.getPropertyValues(producesASProperty);
		    Iterator itC = c.iterator();
		    List<String> annotationNames = new java.util.ArrayList<String>();
		    while (itC.hasNext()) {
			 // Get the mName of the annotation set
			 OWLIndividual annotationSet = (OWLIndividual) itC.next();
			 annotationNames.add(annotationSet.getPropertyValue(annSetNameProperty).toString());			 
		    }
		    String[] dummy = new String[1];
		    result.producedAnnotationSets = annotationNames.toArray(dummy);
		    
		    
	       }

	       return result;
	  }
*/
     


     
     
}
