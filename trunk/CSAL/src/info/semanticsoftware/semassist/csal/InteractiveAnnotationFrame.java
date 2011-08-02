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


import info.semanticsoftware.semassist.csal.result.Annotation;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


/**
 * Generic dialog for clients to associate annotations to
 * a list of actions identified by the annotation features.
 */
public class InteractiveAnnotationFrame extends JFrame {

   // CONSTRUCTORS

   /**
    * Constructor to invoke a dialog that iterates through a set of
    * annotations prompting users to choose between a set of actions.
    *
    * @param descr    Optional context description of what the callback does.
    * @param annots   Non-empty list of annotations to prompt for changes.
    * @param feature  Feature name indicating which elements are accepted to
    *                 be displayed in the option list. If null, no feature
    *                 filtering is pefromed.
    * @param callback Invokes the callback when the execute button is pressed
    *                 with a selected list element. The passed argument to
    *                 the callback.execute() is the String of the selected
    *                 list elemement.
    *
    * @throws IllegalArgumentException if @a annots are empty, in which case
    * caller should do proper dialog handling (show msg box).
    */
   public InteractiveAnnotationFrame(final String descr, final Annotation[] annots,
      final String feature, final ExecuteCallback callback) throws IllegalArgumentException {

      // Validate arguments
      if (annots == null || callback == null) {
         throw new NullPointerException();
      }

      this.annots = new ImmutableCollection<Annotation>(annots);
      this.feature = feature;
      this.callback = callback;

      contextLbl.setText(descr);
      populateFields(this.annots.current());
      init();
   }


   // HELPER METHODS

   /**
    * Configure the dialog's GUI components and sets the look-and-feel.
    */
   private void init() {

      // Configure event listeners.
      ignoreBtn.setToolTipText("Do not apply these changes & analyze the next annotation.");
      ignoreBtn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent evnt) {
            System.out.println("Ignore Button Pressed");
            nextItem();
         }
      });

      executeBtn.setToolTipText("Apply selected changes & analyze the next annotation.");
      executeBtn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent evnt) {
            System.out.println("Execute Button Pressed");
            handleItem();
         }
      });

      cancelBtn.setToolTipText("Close this dialog without analyzing remaining annotations.");
      cancelBtn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent evnt) {
            System.out.println("Cancel Button Pressed");
            closeDialog();
         }
      });

      final Container mainContent = getContentPane();
      mainContent.setLayout(new GridBagLayout());

      // Layout GUI input panel components.
      mainContent.add(contextLbl, new GridBagConstraints(
         0, 0, 4, 1, 0.2, 1.0,
         GridBagConstraints.NORTH,
         GridBagConstraints.BOTH,
         new Insets(10, 10, 5, 10), 0, 0
      ));

      final JPanel inputPnl = new JPanel();
      final JScrollPane scrollTxt = new JScrollPane(inputTxt,
         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      inputTxt.setEditable(false);
      inputTxt.setLineWrap(true);
      inputTxt.setPreferredSize(new Dimension(300, 30));
      inputPnl.setBorder(BorderFactory.createTitledBorder("Input Text"));
      inputPnl.setLayout(new GridBagLayout());
      inputPnl.add(scrollTxt, new GridBagConstraints(
         0, 0, 1, 1, 1.0, 1.0,
         GridBagConstraints.CENTER,
         GridBagConstraints.BOTH,
         new Insets(0, 0, 0, 0), 0, 0
      ));
      mainContent.add(inputPnl, new GridBagConstraints(
         0, 1, 3, 2, 1.0, 0.4,
         GridBagConstraints.CENTER,
         GridBagConstraints.BOTH,
         new Insets(5, 10, 5, 5), 0, 0
      ));

      // Layout GUI option panel components.
      final JPanel optionPnl = new JPanel();
      final JScrollPane scrollLst = new JScrollPane(optionLst,
         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      optionLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      optionLst.setPreferredSize(new Dimension(300, 50));
      optionPnl.setBorder(BorderFactory.createTitledBorder("Available Options"));
      optionPnl.setLayout(new GridBagLayout());
      optionPnl.add(optionLst, new GridBagConstraints(
         0, 0, 1, 1, 1.0, 1.0,
         GridBagConstraints.EAST,
         GridBagConstraints.BOTH,
         new Insets(0, 0, 0, 0), 0, 0
      ));
      mainContent.add(optionPnl, new GridBagConstraints(
         0, 3, 3, 2, 1.0, 0.6,
         GridBagConstraints.EAST,
         GridBagConstraints.BOTH,
         new Insets(5, 10, 10, 5), 0, 0
      ));

      // Layout GUI button panel components.
      final JPanel buttonPnl = new JPanel();
      buttonPnl.setLayout(new GridBagLayout());
      buttonPnl.add(ignoreBtn, new GridBagConstraints(
         0, 0, 1, 1, 0.0, 0.0,
         GridBagConstraints.CENTER,
         GridBagConstraints.HORIZONTAL,
         new Insets(0, 0, 5, 0), 0, 0
      ));
      buttonPnl.add(executeBtn, new GridBagConstraints(
         0, 1, 1, 1, 0.0, 0.0,
         GridBagConstraints.CENTER,
         GridBagConstraints.HORIZONTAL,
         new Insets(5, 0, 5, 0), 0, 0
      ));
      buttonPnl.add(cancelBtn, new GridBagConstraints(
         0, 2, 1, 1, 0.0, 0.0,
         GridBagConstraints.CENTER,
         GridBagConstraints.HORIZONTAL,
         new Insets(5, 0, 0, 0), 0, 0
      ));
      mainContent.add(buttonPnl, new GridBagConstraints(
         3, 1, 1, 4, 0.0, 0.0,
         GridBagConstraints.WEST,
         GridBagConstraints.NONE,
         new Insets(5, 5, 10, 10), 0, 0
      ));

      // Propertize the main frame.
      pack();
      setTitle("Annotation Changes");
      setLocationRelativeTo(null);
      setAlwaysOnTop(true);
      setVisible(true);
   }

   /**
    * @param annot Annotation from which to populate input and option panels.
    */
   private void populateFields(final Annotation annot) {
      inputTxt.setText(annot.mContent);

      // Filter out all annotation features not specified.
      final Set<String> keys = annot.mFeatures.keySet();
      final Vector<String> vtr = new Vector<String>();
      for (Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
         final String key = iter.next();
         final String val = annot.mFeatures.get(key);
         if (feature == null || feature.equals(key)) {
            vtr.add(val);
         }
      }

      // Add feature values to the GUI option list & select the
      // first entry as default.
      if (!vtr.isEmpty()) {
         optionLst.setListData(vtr);
         optionLst.setSelectedIndex(0);
      }
   }

   // BEHAVIOUR EVENTS

   private void nextItem() {
      try {
         populateFields(annots.next());
      } catch (final NoSuchElementException ex) {
         closeDialog();
      }
   }

   private void handleItem() {
      // Do nothing until an option is explicitly selected from the list.
      if (optionLst.isSelectionEmpty()) {
         JOptionPane.showMessageDialog(this,
            "No option was selected to modify.",
            "Missing Selection",
            JOptionPane.ERROR_MESSAGE);
         return;
      }

      // This assumes multi-list selection is not configured.
      callback.execute(optionLst.getSelectedValue());
      nextItem();
   }

   private void closeDialog() {
      dispose();
   }

   /* Helper read-only data structure. */
   class ImmutableCollection<T> {
      private T[] arr;
      private int i = 0;

      public ImmutableCollection(final T[] arr) {
         if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Empty immutable collection.");
         }
         this.arr = arr;
      }

      public boolean hasNext() {
         return (i < arr.length - 1);
      }

      public T next() {
         if (!hasNext()) {
            throw new NoSuchElementException();
         }
         return arr[++i];//.clone();
      }

      public T current() {
         return arr[i];//.clone();
      }
   }


   // GUI ELEMENTS
   private final JLabel contextLbl = new JLabel(); // Description of modification context.
   private final JTextArea inputTxt = new JTextArea(); // Annotation content.
   private final JList optionLst = new JList(); // Option lits.
   private final JButton ignoreBtn = new JButton("Ignore"); // Do not execute on this annotation.
   private final JButton executeBtn = new JButton("Modify"); // Execute callback on this annotation.
   private final JButton cancelBtn = new JButton("Cancel"); // Abort the dialog.


   // MEMBER VARIABLES
   private ImmutableCollection<Annotation> annots;
   private String feature;
   private ExecuteCallback callback;

   /* For backwards compatibility, increment this serialization value ONLY when the
    * public interface of this class is changed, otherwise keep it fixed!
    */
   private static final long serialVersionUID = 1L;
}
