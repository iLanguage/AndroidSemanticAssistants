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

package info.semanticsoftware.semassist.csal;

import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;

import info.semanticsoftware.semassist.server.*;
import info.semanticsoftware.semassist.csal.*;

/**
 *
 * @author  tom
 */
public class RTParamFrame extends JFrame
        implements ActionListener
{

    private static final long serialVersionUID = 1L;
    private ActionListener okActionListener = null;
    private static final String YES_STRING = "Yes";
    private static final String NO_STRING = "No";

    /** Creates new form RTParamDialog */
    public RTParamFrame()
    {
        initComponents();
    }

    public RTParamFrame( ServiceInfoForClient info )
    {
        initComponents();

        setAlwaysOnTop( true );
        
        /*addWindowFocusListener( new WindowAdapter()
		{
            @Override
			public void windowDeactivated(WindowEvent e)
			{
				e.getWindow().toFront();
                requestFocus();
			}
		});
        */
        

        if( info != null )
        {
            setTitle( "Parameters for Service \"" + info.getServiceName() + "\"" );
        }
    }

    public class MyFocusListener implements WindowFocusListener
    {
        @Override
        public void windowGainedFocus( WindowEvent arg0 )
        {
            requestFocus();
        }

        @Override
        public void windowLostFocus( WindowEvent arg0 )
        {
            requestFocus();
        }

    }

    private HashMap<GateRuntimeParameter, JComponent> paramMap =
                                                      new HashMap<GateRuntimeParameter, JComponent>();
    private GateRuntimeParameterArray allParams = new GateRuntimeParameterArray();
    private Vector<GateRuntimeParameter> mandatory = null;
    private Vector<GateRuntimeParameter> optional = null;

    public void setMandatories( Vector<GateRuntimeParameter> m )
    {
        mandatory = m;
        Iterator<GateRuntimeParameter> it = m.iterator();
        List<GateRuntimeParameter> allList = allParams.getItem();

        while( it.hasNext() )
        {
            GateRuntimeParameter p = it.next();
            allList.add( p );
            addInputLine( pnlMandatory, p );
        }

        if( m.size() == 0 )
        {
            pnlMandatory.add( new JLabel( "--None--" ) );
        }

    }

    public void setOptionals( Vector<GateRuntimeParameter> o )
    {
        optional = o;
        Iterator<GateRuntimeParameter> it = o.iterator();
        List<GateRuntimeParameter> allList = allParams.getItem();

        while( it.hasNext() )
        {
            GateRuntimeParameter p = it.next();
            allList.add( p );
            addInputLine( pnlOptional, p );
        }

        if( o.size() == 0 )
        {
            pnlOptional.add( new JLabel( "--None--" ) );
            pnlOptional.add( new JLabel( "" ) );
        }
    }

    private void addInputLine( JPanel pnl, GateRuntimeParameter p )
    {
        String s = p.getParamName();
        if( p.getLabel() != null && !p.getLabel().equals( "" ) )
        {
            s = p.getLabel();
        }

        JLabel l = new JLabel( s );
        pnl.add( l );
        if( p.getType().equals( "boolean" ) )
        {
            // Create a combo box
            String[] yesNo =
            {
                YES_STRING, NO_STRING
            };
            JComboBox cb = new JComboBox( yesNo );
            pnl.add( cb );
            paramMap.put( p, cb );

            // Set default value if given
            String dv = p.getDefaultValueString();
            if( dv != null && !dv.equals( "" ) )
            {
                if( dv.equals( "true" ) )
                {
                    cb.setSelectedItem( yesNo[0] );
                }
                else
                {
                    cb.setSelectedItem( yesNo[1] );
                }
            }

        }
        else
        {
            // Create a text field
            JTextField tf = new JTextField();
            pnl.add( tf );
            paramMap.put( p, tf );

            // Set default value
            String dv = p.getDefaultValueString();
            if( dv != null )
            {
                tf.setText( dv );
            }

        }
    }

    public void setOkActionListener( ActionListener a )
    {
        okActionListener = a;
    }

    public GateRuntimeParameterArray getParams()
    {
        return allParams;
    }

    public void actionPerformed( ActionEvent e )
    {
        // Get the values out of the input fields
        boolean valuesAssigned = assignParamValues();
        if( !valuesAssigned )
        {
            return;
        }

        if( mandatoryParamsOk() )
        {
            setVisible( false );
            if( okActionListener != null )
            {
                okActionListener.actionPerformed( e );
            }

            dispose();
        }
        else
        {
            // At least one mandatory parameter has not been given
            JOptionPane.showMessageDialog( this, "Please give values for all the " +
                                                 "parameters listed under \"Mandatory Parameters\".",
                    "Missing Parameters", JOptionPane.ERROR_MESSAGE );
        }


    }

    private boolean assignParamValues()
    {
        boolean b = assignParamValues( mandatory );
        boolean c = true;
        if( b )
        {
            c = assignParamValues( optional );
        }

        return b && c;
    }

    private boolean assignParamValues( Vector<GateRuntimeParameter> params )
    {
        Iterator<GateRuntimeParameter> it = params.iterator();

        while( it.hasNext() )
        {
            GateRuntimeParameter p = it.next();
            JComponent comp = paramMap.get( p );

            if( !assignValueFromComponent( p, comp ) )
            {
                return false;
            }
        }

        return true;
    }

    private boolean assignValueFromComponent( GateRuntimeParameter p, JComponent comp )
    {
        if( p == null || comp == null )
        {
            return false;
        }

        if( p.getType().equals( "double" ) )
        {
            JTextField tf = (JTextField) comp;
            String valueString = tf.getText().trim();
            double value;
            try
            {
                value = Double.parseDouble( valueString );
                p.setDoubleValue( new Double( value ) );
            }
            catch( NumberFormatException e )
            {
                return false;
            }

            return true;
        }
        else if( p.getType().equals( "int" ) )
        {
            JTextField tf = (JTextField) comp;
            String valueString = tf.getText().trim();
            int value;
            try
            {
                value = Integer.parseInt( valueString );
                p.setIntValue( new Integer( value ) );
            }
            catch( NumberFormatException e )
            {
                return false;
            }

            return true;
        }
        else if( p.getType().equals( "boolean" ) )
        {
            JComboBox cb = (JComboBox) comp;
            String boolString = (String) cb.getSelectedItem();
            if( boolString.equals( YES_STRING ) )
            {
                p.setBooleanValue( new Boolean( true ) );
            }
            else
            {
                p.setBooleanValue( new Boolean( false ) );
            }

            return true;
        }
        else if( p.getType().equals( "string" ) )
        {
            JTextField tf = (JTextField) comp;
            String valueString = tf.getText().trim();
            p.setStringValue( valueString );

            return true;
        }
        else if( p.getType().equals( "url" ) )
        {
            JTextField tf = (JTextField) comp;
            String valueString = tf.getText().trim();
            try
            {
                URL u = new URL( valueString );
                p.setUrlValue( valueString );
            }
            catch( MalformedURLException e )
            {
                return false;
            }

            return true;
        }


        return false;
    }

    private boolean mandatoryParamsOk()
    {
        Iterator<GateRuntimeParameter> it = mandatory.iterator();

        while( it.hasNext() )
        {
            GateRuntimeParameter p = it.next();
            if( !ClientUtils.paramHasValue( p, false ) )
            {
                return false;
            }
        }


        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        pnlMandatory = new javax.swing.JPanel();
        pnlOptional = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnOk.addActionListener( this );

        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
        setTitle( "Parameters for Service" );
        // pnlMandatory.setLayout(new java.awt.GridLayout(0, 2, 15, 10));
        pnlMandatory.setLayout( new java.awt.GridLayout( 0, 2, 20, 10 ) );

        pnlMandatory.setBorder( javax.swing.BorderFactory.createTitledBorder( "Mandatory Parameters" ) );

        pnlOptional.setLayout( new java.awt.GridLayout( 0, 2, 20, 10 ) );

        pnlOptional.setBorder( javax.swing.BorderFactory.createTitledBorder( "Optional Parameters" ) );

        btnOk.setText( "Ok" );

        btnCancel.setText( "Cancel" );
        btnCancel.addActionListener( new ActionListener()
        {

            public void actionPerformed( ActionEvent e )
            {
                setVisible( false );
                dispose();
            }

        } );


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout( getContentPane() );
        getContentPane().setLayout( layout );
        layout.setHorizontalGroup(
                layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addGroup( layout.createSequentialGroup().addContainerGap().addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addComponent( pnlMandatory, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE ).addGroup( javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent( btnCancel ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addComponent( btnOk ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ) ).addComponent( pnlOptional, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE ) ).addContainerGap() ) );
        layout.setVerticalGroup(
                layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addGroup( layout.createSequentialGroup().addContainerGap().addComponent( pnlMandatory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addComponent( pnlOptional, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE ).addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.BASELINE ).addComponent( btnCancel ).addComponent( btnOk ) ).addContainerGap() ) );

        /*
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(pnlMandatory, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addComponent(btnCancel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnOk)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
        .addComponent(pnlOptional, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE))
        .addContainerGap())
        );
        layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(pnlMandatory, javax.swing.GroupLayout.DEFAULT_SIZE, , Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(pnlOptional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
        .addComponent(btnCancel)
        .addComponent(btnOk))
        .addContainerGap())
        );
         */
        pack();

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JPanel pnlMandatory;
    private javax.swing.JPanel pnlOptional;
    // End of variables declaration//GEN-END:variables
}
