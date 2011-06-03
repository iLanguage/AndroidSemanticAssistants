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

import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author nikolaos
 */
public class GlobalSettingsFrame extends JFrame
        implements ActionListener
{

    private ActionListener okActionListener = null;
    //private static final String HIGHLIGHT_ON = "Disable Annotation Highlighting";
    //private static final String HIGHLIGHT_OFF = "Enable Annotation Highlighting";
    //private static String  CURRENT_HIGHLIGHT = HIGHLIGHT_OFF;
    private static String CURRENT_SERVER_IP = "Change Server IP";
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JPanel pnlMandatory;
    private javax.swing.JPanel pnlOptional;

    public GlobalSettingsFrame()
    {
        initComponents();
    }

    private void initComponents()
    {
        pnlMandatory = new javax.swing.JPanel();
        pnlOptional = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnOk.addActionListener( this );
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
    }

}
