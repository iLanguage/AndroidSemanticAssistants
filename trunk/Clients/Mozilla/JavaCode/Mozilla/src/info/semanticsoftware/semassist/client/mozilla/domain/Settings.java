/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the Semantic Assistants Mozilla Extension.
 *
 * The Initial Developer of the Original Code is
 * Semantic Software Lab (http://www.semanticsoftware.info).
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Nikolaos Papadakis
 *   Tom Gitzinger
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package info.semanticsoftware.semassist.client.mozilla.domain;

import java.util.Properties;
import java.util.HashMap;

import info.semanticsoftware.semassist.server.ServiceInfoForClient;

public class Settings {
	
	private static Properties prop = null;
	private static String selectedServiceName = null;
	private static HashMap<String, ServiceInfoForClient> availableServices;
	private static String[] availableServiceNamesArray;
	private static String[] availableServiceDescriptionsArray;

	public static String getSelectedServiceName() {
		return selectedServiceName;
	}

	public static void setSelectedServiceName( String s ) {
		selectedServiceName = s;
	}

	public static void setAvailableServices( HashMap<String, ServiceInfoForClient> m ) {
		availableServices = m;
	}

	public static HashMap<String, ServiceInfoForClient> getAvailableServices() {
		return availableServices;
	}
	
	public static String[] getAvailableServiceNamesArray() {
		return availableServiceNamesArray;
	}
	
	public static void setAvailableServiceNamesArray(String[] availableServiceNamesArrayParam) {
		availableServiceNamesArray = availableServiceNamesArrayParam;
	}

	public static String[] getAvailableServiceDescriptionsArray() {
		return availableServiceDescriptionsArray;
	}
	
	public static void setAvailableServiceDescriptionsArray(String[] availableServiceDescriptionsArrayParam) {
		availableServiceDescriptionsArray = availableServiceDescriptionsArrayParam;
	}

}
