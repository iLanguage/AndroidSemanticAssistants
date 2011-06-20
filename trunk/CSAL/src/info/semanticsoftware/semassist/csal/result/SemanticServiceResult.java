/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info


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
package info.semanticsoftware.semassist.csal.result;

import java.util.*;

/**
  Defines a result entity retrieved from parsing the server XML response.
  @author Tom Gitzinger,Nikolaos Papadakis
*/

public class SemanticServiceResult
{
    /** Result types */
    public static final String ANNOTATION = "annotation";
    public static final String FILE = "outputFile";
    public static final String DOCUMENT = "outputDocument";
    public static final String CORPUS = "corpus";
    public static final String ANNOTATION_IN_WHOLE = "annotation in whole";
    
    /** A result type. Must be one of the class constants */
    public String mResultType = "";

    /** List of annotations that maps document IDs to annotation instances */
    public HashMap<String, AnnotationVector> mAnnotations = new HashMap<String, AnnotationVector>();

    /** The URL of the retrieved result file */
    public String mFileUrl;

    /** The mime type of the retrieved result file */
    public String mMimeType;

    /** Corpus case */
    public Vector<RetrievedDocument> mCorpus = new Vector<RetrievedDocument>();
}
