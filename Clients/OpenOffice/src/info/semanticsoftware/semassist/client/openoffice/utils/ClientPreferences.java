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
package info.semanticsoftware.semassist.client.openoffice.utils;


import info.semanticsoftware.semassist.csal.ClientUtils;
import info.semanticsoftware.semassist.csal.XMLElementModel;

import java.util.*;


/**
 * Helper class as a facade to ClientUtils to set and get OpenOffice
 * specific configuration preferences with enforced datatypes and
 * default values.
 */
public abstract class ClientPreferences {

   // Client-unique key to differentiate its preferences from
   // other clients.
   public static final String CLIENT_NAME = "openoffice";


   /**
    * @param size value of the side-note font.
    */
   public static void setSideNoteFontSize(final float size) {
      setPreference(PRESENTATION_SIDENOTE, "fontsize", Float.valueOf(size));
   }


   /**
    * @return the font-size for side-note content.
    */
   public static float getSideNoteFontSize() {
      float result;
      final ArrayList<XMLElementModel> preferences =
         ClientUtils.getClientPreference(CLIENT_NAME, PRESENTATION_SIDENOTE);
      final float dfl = 8; // Default.

      if (preferences.size() == 0) {
         // Use defaults since no client tag exists or no key tag exists within the client tag.
         result = dfl;
      } else {
         // Use the first side-note tag if multiple are defined.
         final String val = preferences.get(0).getAttribute().get("fontsize");
         if (val != null) {
            result = Float.valueOf(val);
         } else {
            result = dfl;
         }
      }
      return result;
   }


   /**
    * @param status true to highlight document text associated with a side-note,
    *               or false otherwise.
    */
   public static void setTextHighlightMode(final boolean status) {
      setPreference(PRESENTATION_SIDENOTE, "showHighlight", Boolean.valueOf(status));
   }


   /**
    * @return Flag indicating if document text associated to a side-note should
    *         be highlighted or not.
    */
   public static boolean isTextHighlightMode() {
      return getBooleanPreference(PRESENTATION_SIDENOTE, "showHighlight", true);
   }


   /**
    * @param status true to show annotation content within side-notes,
    *               or false otherwise.
    */
   public static void setShowAnnotationContent(final boolean status) {
      setPreference(PRESENTATION_SIDENOTE, "showContent", Boolean.valueOf(status));
   }


   /**
    * @return Flag indicating if redundant annotation content feature should
    *         be shown in the side-note.
    */
   public static boolean isShowAnnotationContent() {
      return getBooleanPreference(PRESENTATION_SIDENOTE, "showContent", false);
   }


   /**
    * @param status true to allow filtering of empty-valued
    *               features or false otherwise.
    */
   public static void setEmptyFeatureFilter(final boolean status) {
      setPreference(PRESENTATION_SIDENOTE, "showEmptyFeatures", Boolean.valueOf(status));
   }


   /**
    * @return Flag indicating if any empty-valued feature in the responce
    *         should be displayed on the side note or not.
    */
   public static boolean isEmptyFeatureFilter() {
      return getBooleanPreference(PRESENTATION_SIDENOTE, "showEmptyFeatures", true);
   }


   /**
    * @param status true to allow alteration of resulting annotation responses
    *        or false otherwise.
    */
   public static void setInteractiveResultHandling(final boolean status) {
      setPreference(RESULT_HANDLING, "interactive", Boolean.valueOf(status));
   }


   /**
    * @return Flag indicating if interactive annotations are allowed to be
    *         modified via a context-specific dialog.
    */
   public static boolean isInteractiveResultHandling() {
      return getBooleanPreference(RESULT_HANDLING, "interactive", true);
   }


   /**
    * @param status true to allow an external browser to handle the document
    *        results or false otherwise.
    */
   public static void setBrowserResultHandling(final boolean status) {
      setPreference(RESULT_HANDLING, "browserDelegate", Boolean.valueOf(status));
   }


   /**
    * @return Flag indicating if applicable results (such as HTML, XML, ect)
    *         content should be delegated to a browser.
    */
   public static boolean isBrowserResultHandling() {
      return getBooleanPreference(RESULT_HANDLING, "browserDelegate", true);
   }


   /**
    * Helper method to set a generic preference.
    * @param pref XML tag name of the preference.
    * @param key XML attribute name of the preference.
    * @param val XML attribute value of the preference.
    */
   private static <T> void setPreference(final String pref, final String key, final T val) {
      // Prepare the attribute/value map.
      final Map<String, String> map = new HashMap<String, String>();
      map.put(key, val.toString());

      ClientUtils.setClientPreference(CLIENT_NAME, pref, map);
   }


   /**
    * Helper method to get a boolean preference.
    * @param pref XML tag name of the preference.
    * @param key XML attribute name of the preference to retrieve.
    * @param dfl Default value if the key is not found.
    *
    * @return The value of the extracted boolean preference.
    */
   private static boolean getBooleanPreference(final String pref, final String key, boolean dfl) {
      boolean result;
      final ArrayList<XMLElementModel> preferences =
         ClientUtils.getClientPreference(CLIENT_NAME, pref);

      if (preferences.size() == 0) {
         // Use defaults since no client tag exists or no key tag exists within the client tag.
         result = dfl;
      } else {
         // Use the first side-note tag if multiple are defined.
         final String val = preferences.get(0).getAttribute().get(key);
         if (val != null) {
            result = Boolean.valueOf(val);
         } else {
            // Use defaults if key was not found.
            result = dfl;
         }
      }
      return result;
   }


   // Preference categories must comply with XML attribute name syntax.
   private static final String PRESENTATION_SIDENOTE = "sidenote";   // Presentational
   private static final String RESULT_HANDLING = "resultHandling";   // Behavioural
}
