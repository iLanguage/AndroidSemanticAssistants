/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2009 Semantic Software Lab, http://www.semanticsoftware.info
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


package info.semanticsoftware.semassist.client.openoffice.utils;

import java.util.Properties;
import java.util.HashMap;

import info.semanticsoftware.semassist.server.ServiceInfoForClient;

public class Settings
{
    private static Properties p = null;
    private static String selectedServiceName = null;
    private static HashMap<String, ServiceInfoForClient> availableServices;


    public static String getSelectedServiceName()
    {
        return selectedServiceName;
    }

    public static void setSelectedServiceName( final String s )
    {
        selectedServiceName = s;
        UNOUtils.setCurrentPipeline( selectedServiceName );
    }

    public static void setAvailableServices( final HashMap<String, ServiceInfoForClient> m )
    {
        availableServices = m;
    }

    public static HashMap<String, ServiceInfoForClient> getAvailableServices()
    {
        return availableServices;
    }

}
