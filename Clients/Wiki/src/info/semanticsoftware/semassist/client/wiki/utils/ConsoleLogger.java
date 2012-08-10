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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class that offers console logging capabilities.
 * @author Bahar Sateli
 * */
public class ConsoleLogger {

	/** Logger buffer. */
	private static StringBuffer logger = new StringBuffer();

	/** Protected constructor to conform to Singleton pattern. */
	protected ConsoleLogger(){
		//defeat instantiation
	}

	/**
	 * Add the message to the console log string.
	 * @param message log message
	 * */
	public static void log(final String message){
		logger.append("[" + getTime().concat("] ").concat(message).concat("<br>"));
	}

	/**
	 * Returns the aggregated contents of console.
	 * @return String console contents
	 * */
	public static String getConsoleLogs(){
		return logger.toString();
	}

	/**
	 * Clears the console content.
	 * */
	public static void clearConsole(){
		logger.delete(0, logger.capacity());
	}

	/**
	 * Returns current time in MMM dd,yyyy HH:mm format.
	 * @return String current time
	 * */
	public static String getTime(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date();
		return dateFormat.format(resultdate);
	}
}
