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


import java.util.Vector;

/**
 * Instances of this class capture the relevant aspects
 * of a GATE annotation when it is part of a result of
 * an invoked service. More specifically, it holds information
 * like the annotation mName, the mName of the containing
 * annotation set, and the mFeatures that could be of
 * interest to the client.
 */
public class GATEAnnotation
{

     public String mName = "";
     public String mSetName = "";
     public String mIsBoundless = "";
     
     public Vector<String> mFeatures = new Vector<String>();
     

     public void addFeature(String f) 
	  {
	       mFeatures.add(f);
	  }

     public void removeFeature(String f) 
	  {
	       mFeatures.remove(f);
	  }
          
}
