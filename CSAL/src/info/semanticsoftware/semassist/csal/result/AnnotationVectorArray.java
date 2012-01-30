/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info

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

package info.semanticsoftware.semassist.csal.result;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author nikolaos
 */
public class AnnotationVectorArray {


    public Vector<AnnotationVector> mAnnotVectorArray = new Vector<AnnotationVector>();

    /**
     * Temporary helper method to facilitate transition away from
     * call-sites depending on this nasty data-structure that
     * discourages information-hiding and encapsulation.
     *
     * @param arr Instance to convert from.
     * @return Resulting list of annotations.
     */
    public static final List<Annotation> convert(final AnnotationVectorArray arr) {
      final List<Annotation> lst = new ArrayList<Annotation>();

      for (final AnnotationVector vtr : arr.mAnnotVectorArray) {
         for (final Annotation ann : vtr.mAnnotationVector) {
            // Flatten out AnnotationVectorArray into an
            // list removing any noise annotations.
            if (ann.mContent != null && !"".equals(ann)) {
               lst.add(ann);
            }
         }
      }
      return lst;
    }
}
