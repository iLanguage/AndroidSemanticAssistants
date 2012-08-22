/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2012, 2013 Semantic Software Lab, http://www.semanticsoftware.info
Rene Witte
Bahar Sateli

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
package info.semanticsoftware.semassist.client.wiki.utils;

/**
 * Helper class to represent a wiki system
 * @author Bahar Sateli
 * */
public class Wiki {

	/** Wiki engine name. */
	private String engine;

	/** Wiki version identifier. */
	private String version;

	/**
	 * Class constructor. Creates a wiki instance in memory.
	 * using the provided arguments.
	 * @param _engine wiki engine name
	 * @param _version wiki version identifier
	 * */
	public Wiki(final String _engine, final String _version){
		this.engine = _engine;
		this.version = _version;
	}

	/**
	 * Returns the wiki engine name.
	 * @return wiki engine name string
	 * */
	public String getEngine(){
		return this.engine;
	}

	/**
	 * Returns the wiki version identifier.
	 * @return wiki version identifier string
	 * */
	public String getVersion(){
		return this.version;
	}
}
