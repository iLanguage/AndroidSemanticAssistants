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

package  info.semanticsoftware.semassist.csal.result;

import java.util.*;

/**
  Defines the structure of an annotation instance.
  @author Tom Gitzinger, Nikolaos Papadakis
*/
public class Annotation
{

    // annotation type.
    public String mType = "";
    
    // annotation content
    public String mContent = "";
    
    // list of annotation features as <key,value> pairs
    public HashMap<String, String> mFeatures = new HashMap<String, String>();
    
    // annotation start offset
    public long mStart;
    
    //annotation end offset
    public long mEnd;  
     
}
