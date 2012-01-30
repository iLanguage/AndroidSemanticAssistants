/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2012 Semantic Software Lab, http://www.semanticsoftware.info

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
package info.semanticsoftware.semassist.csal;


// DEPENDENCIES //
import info.semanticsoftware.semassist.csal.result.Annotation;

import java.util.Comparator;


/**
 * Compare annotations by starting offset.
 *
 * To avoid leaking back-end implementation details, this
 * class is a re-implementation of gate.util.OffsetComparator
 * in order to avoid linking GATE package in client code.
 *
 * @author elian
 */
public final class OffsetComparator implements Comparator<Annotation>
{
   /**
    * Specify ordering of two annotations based on their starting
    * offsets.
    *
    * @param ann1 First annotation.
    * @param ann2 Second annotation.
    *
    * @return Negative value if @ann1 offset precedes @ann2.
    *         Positive value if @ann2 offset precedes @ann1.
    *         Zero otherwise.
    *
    * Note: In case when two annotations have the same starting
    *       offset, it might be useful to further sort these
    *       annotations by their length.
    */
   public int compare(final Annotation ann1, final Annotation ann2) {
      // Instantiate Long object to avoid precision roundoff
      // & compile-time problems.
      return new Long(ann1.mStart - ann2.mStart).intValue();
   }
}
