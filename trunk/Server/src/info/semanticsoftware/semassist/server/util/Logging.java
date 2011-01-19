/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info
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

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

public class Logging
{

    public static boolean DO_LOG = true;

    public static void log( String s )
    {
        Date rightNow = Calendar.getInstance().getTime();

        if( DO_LOG )
        {
        	//modified the datetime format to show time.
        	//Easier to know when each process was actually run
            System.out.println( "[" + DateFormat.getDateTimeInstance().format( rightNow ) + "] " + s );
        }
    }

    public static void exception( Exception e, boolean printStackTrace )
    {
        Date rightNow = Calendar.getInstance().getTime();
        System.out.println( "[" + DateFormat.getDateTimeInstance().format( rightNow ) + "] " + e );
        if( printStackTrace )
        {
            e.printStackTrace();
        }
    }

    public static void exception( Exception e )
    {
        exception( e, true );
    }

}
