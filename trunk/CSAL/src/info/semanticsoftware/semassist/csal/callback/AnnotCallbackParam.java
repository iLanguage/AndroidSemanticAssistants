/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2011 Semantic Software Lab, http://www.semanticsoftware.info

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

package info.semanticsoftware.semassist.csal.callback;


import info.semanticsoftware.semassist.csal.result.Annotation;


/**
 * Data model class for the callback parameter to modify
 * annotations.
 */
public final class AnnotCallbackParam {

   private final Annotation affected; // Original annotation.
   private final String context; // New annotation context.

   /**
    * @param affected Non-null original annotation.
    * @param contex Non-null new annotation text.
    */
   public AnnotCallbackParam(final Annotation affected, final String context)
      throws NullPointerException {

      if (affected == null || context == null) {
         throw new NullPointerException();
      }
      this.affected = affected;
      this.context = context;
   }

   public Annotation getAffectedAnnotation() { return affected; }
   public String getContext() { return context; }
}
