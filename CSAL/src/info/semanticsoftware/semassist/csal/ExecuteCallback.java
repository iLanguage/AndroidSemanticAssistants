/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2011 Semantic Software Lab, http://www.semanticsoftware.info

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

package info.semanticsoftware.semassist.csal;


public interface ExecuteCallback<T> {

   /**
    * Overwritable method implementing context specific execute
    * behaviour.
    *
    * @param param General parameter(s) to be used to be used in callback.
    * @return True if the call was successful, false otherwise.
    */
   abstract public boolean execute(final T param);
}
