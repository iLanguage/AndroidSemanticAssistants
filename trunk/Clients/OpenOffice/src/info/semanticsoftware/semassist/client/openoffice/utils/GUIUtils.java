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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.sun.star.awt.XToolkit;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.awt.WindowDescriptor;
import com.sun.star.awt.WindowAttribute;
import com.sun.star.awt.WindowClass;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.XWindow;
import com.sun.star.frame.XFrame;


public class GUIUtils 
{

     private static Logger logger = Logger.getLogger(GUIUtils.class);

     public static void showMessageBox(XComponentContext ctx, XFrame frame, String title, String msg) 
	  {
	       XToolkit m_xToolkit = null;

	       try {
		    m_xToolkit = (XToolkit)UnoRuntime.queryInterface(
			 XToolkit.class,
			 ctx.getServiceManager().createInstanceWithContext("com.sun.star.awt.Toolkit",
									  ctx));     
	       } catch (com.sun.star.uno.Exception e) {
		    
	       }
	       
	        

	       try {
		    if (null != frame && null != m_xToolkit) {

			 // Create a new window description for the message box
			 WindowDescriptor aDescriptor = new WindowDescriptor();
			 aDescriptor.Type              = WindowClass.MODALTOP;
			 aDescriptor.WindowServiceName = new String("infobox");
			 aDescriptor.ParentIndex       = -1;
			 aDescriptor.Parent            = (XWindowPeer)UnoRuntime.queryInterface(
			    XWindowPeer.class, frame.getContainerWindow());

			 // Position relative to parent window
			 XWindow parentWindow = (XWindow) UnoRuntime.queryInterface(
			      XWindow.class, frame.getContainerWindow());
			 Rectangle pwRect = parentWindow.getPosSize();

			 aDescriptor.Bounds            = new Rectangle(pwRect.Width / 2 - 150,
								       pwRect.Height / 2 - 100,
								       300,200);
			 aDescriptor.WindowAttributes  = WindowAttribute.BORDER |
			      WindowAttribute.MOVEABLE |
			      WindowAttribute.CLOSEABLE;
			 
			 XWindowPeer xPeer = m_xToolkit.createWindow(aDescriptor);
			 if (null != xPeer) {
			      XMessageBox xMsgBox = (XMessageBox)UnoRuntime.queryInterface(
				   XMessageBox.class, xPeer);
			      if (null != xMsgBox)
			      {
				   xMsgBox.setCaptionText(title);
				   xMsgBox.setMessageText(msg);
				   xMsgBox.execute();
			      }
			 }
		    } else {
			 logger.debug("frame == null: " + (frame == null) + ", toolkit == null: " + (m_xToolkit == null));
		    }
		    
	       } catch (com.sun.star.uno.Exception e) {
		    // do your error handling 
	       }
	  }
     
     
}


