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
    * @param annots Non-empty list of annotations to prompt for changes.
    * @param contextFeature Feature name indicating the context of the @a targetFeature.
    * @param targetFeature Feature name indicating which elements are accepted
    *        to be displayed in the option list. If null, no feature filtering
    *        is pefromed.
    * @param callback Invokes the callback when the modify button is pressed with
    *        a selected list element. The passed argument to the callback.execute()
    *        is the String of the selected list elemement.
    *
    * @throws IllegalArgumentException if @a annots are empty, in which case
    * caller should do proper dialog handling (show msg box).
    */
   public InteractiveAnnotationFrame(final Annotation[] annots,
      final String contextFeature, final String targetFeature,
      final ExecuteCallback<String> callback) throws IllegalArgumentException {

      // Validate arguments
      if (annots == null || callback == null) {
         throw new NullPointerException();
      }

      this.annots = new ImmutableCollection<Annotation>(annots);
      this.contextFeature = contextFeature;
      this.targetFeature = targetFeature;
      this.callback = callback;

      init();
      populateFields(this.annots.current());
   }


   // HELPER METHODS

   /**
    * Configure the dialog's GUI components and sets the look-and-feel.
    */
   private void init() {

      // Configure list-item event listeners.
      optionLst.setToolTipText("Select an item to try these text changes.");
      optionLst.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent evnt) {
            // Handle double clicks.
            if (evnt.getClickCount() == 2) {
               System.out.println("List option selection");
               selectedItem();
            }
         }
      });
      optionLst.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(final KeyEvent evnt) {
            // Handle enter-key
            if (evnt.getKeyCode() == KeyEvent.VK_ENTER) {
               System.out.println("List option selection");
               selectedItem();
            }
         }
      });

      // Configure button event listeners.
      ignoreBtn.setToolTipText("Do not apply text changes & analyze the next annotation.");
      ignoreBtn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent evnt) {
            System.out.println("Ignore Button Pressed");
            nextItem();
         }
      });

      modifyBtn.setToolTipText("Apply text changes & analyze the next annotation.");
      modifyBtn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent evnt) {
            System.out.println("Modify Button Pressed");
            handleItem();
         }
      });

      resetBtn.setToolTipText("Restore original annotation text changes.");
      resetBtn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent evnt) {
            System.out.println("Reset Button Pressed");
            resetItem();
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

      // Layout GUI context description panel components.
      final JPanel contextPnl = new JPanel();
      final JScrollPane contextScrl = new JScrollPane(contextTxt,
         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      contextScrl.setBorder(BorderFactory.createEmptyBorder());
      contextTxt.setEditable(false);
      contextTxt.setLineWrap(true);
      contextTxt.setPreferredSize(new Dimension(400, 30));
      //dbg>> hack to get the look-&-feel of a label.
      final JLabel dummyLbl = new JLabel();
      contextTxt.setFont(dummyLbl.getFont());
      contextTxt.setForeground(dummyLbl.getForeground());
      contextTxt.setBackground(dummyLbl.getBackground());
      contextPnl.setLayout(new GridBagLayout());
      contextPnl.add(contextScrl, new GridBagConstraints(
         0, 0, 1, 1, 1.0, 1.0,
         GridBagConstraints.CENTER,
         GridBagConstraints.BOTH,
         new Insets(0, 0, 0, 0), 0, 0
      ));
      mainContent.add(contextScrl, new GridBagConstraints(
         0, 0, 4, 1, 1.0, 0.01,
         GridBagConstraints.CENTER,
         GridBagConstraints.BOTH,
         new Insets(10, 10, 5, 10), 0, 0
      ));

      // Layout GUI input panel components.
      final JPanel inputPnl = new JPanel();
      final JScrollPane scrollTxt = new JScrollPane(inputTxt,
         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      inputTxt.setEditable(true);
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
      buttonPnl.add(modifyBtn, new GridBagConstraints(
         0, 1, 1, 1, 0.0, 0.0,
         GridBagConstraints.CENTER,
         GridBagConstraints.HORIZONTAL,
         new Insets(5, 0, 5, 0), 0, 0
      ));
      buttonPnl.add(resetBtn, new GridBagConstraints(
         0, 2, 1, 1, 0.0, 0.0,
         GridBagConstraints.CENTER,
         GridBagConstraints.HORIZONTAL,
         new Insets(5, 0, 5, 0), 0, 0
      ));
      buttonPnl.add(cancelBtn, new GridBagConstraints(
         0, 3, 1, 1, 0.0, 0.0,
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
      // Show context description if any.
      if (annot.mFeatures.containsKey(contextFeature)) {
         contextTxt.setText(annot.mFeatures.get(contextFeature));
      }

      resetItem();

      // Filter out all annotation features not specified.
      final Vector<String> vtr = new Vector<String>();
      final Set<String> keys = annot.mFeatures.keySet();
      for (final String key : keys) {
         // NOTE: Due to the server's response for annotations having feature
         // keys with multiple values encoded as a comma separated string,
         // this dialog cannot (& should not) dissambiguate these multiple
         // values. Once this is enhanced in the server's response & client's
         // datamodel this should automagically work!
         if (targetFeature == null || targetFeature.equals(key)) {
            final String val = annot.mFeatures.get(key);
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

   /**
    * @param selection Index of the selected option list-item.
    */
   private void selectedItem() {
      // NOTE: This assumes multi-list selection is not configured.
      inputTxt.setText(optionLst.getSelectedValue().toString());
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

      // Pass (possibly tweeked) input text to the callback.
      callback.execute(inputTxt.getText());
      nextItem();
   }

   private void resetItem() {
      inputTxt.setText(annots.current().mContent);
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
   private final JTextArea inputTxt = new JTextArea(); // Annotation content.
   private final JTextArea contextTxt = new JTextArea(); // Annotation context description.
   private final JList optionLst = new JList(); // Option lits.
   private final JButton ignoreBtn = new JButton("Ignore"); // Do not execute on this annotation.
   private final JButton modifyBtn = new JButton("Modify"); // Execute callback on this annotation.
   private final JButton resetBtn = new JButton("Reset"); // Set input text to original value.
   private final JButton cancelBtn = new JButton("Cancel"); // Abort the dialog.


   // MEMBER VARIABLES
   private ImmutableCollection<Annotation> annots;
   private String contextFeature;
   private String targetFeature;
   private ExecuteCallback<String> callback;

   /* For backwards compatibility, increment this serialization value ONLY when the
    * public interface of this class is changed, otherwise keep it fixed!
    */
   private static final long serialVersionUID = 1L;
}
