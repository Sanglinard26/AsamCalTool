/*
 * Creation : 2 janv. 2019
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import a2lobject.A2l;
import a2lobject.Characteristic;
import association.Association;
import hex.IntelHex;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    final JList<Characteristic> list;
    final JTextPane textPane;

    private A2l a2l;

    public Ihm() {
        super("A2LParser");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container container = getContentPane();

        container.setLayout(new BorderLayout());

        // System.out.println(a2l.getContent().toString());

        list = new JList<Characteristic>();
        textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(500, 500));

        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    textPane.setText(list.getSelectedValue().getInfo());
                }

            }
        });

        container.add(new PanelBt(), BorderLayout.NORTH);
        container.add(new JScrollPane(list), BorderLayout.WEST);
        container.add(textPane, BorderLayout.CENTER);

        pack();

        setVisible(true);
    }

    private final class PanelBt extends JPanel {

        private JButton btOpenA2L;
        private JButton btOpenHex;

        public PanelBt() {
            super();
            ((FlowLayout) getLayout()).setAlignment(FlowLayout.LEFT);
            btOpenA2L = new JButton(new AbstractAction("Open A2L") {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser("C:\\User\\U354706\\Perso\\WorkInProgress");
                    int rep = chooser.showOpenDialog(null);

                    if (rep == JFileChooser.APPROVE_OPTION) {
                        a2l = new A2l(chooser.getSelectedFile());
                        List<Characteristic> listCharac = a2l.getCharacteristics();
                        Collections.sort(listCharac);
                        list.setListData(listCharac.toArray(new Characteristic[a2l.getCharacteristics().size()]));
                    }

                }
            });
            add(btOpenA2L);

            btOpenHex = new JButton(new AbstractAction("Open HEX") {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser("C:\\User\\U354706\\Perso\\WorkInProgress");
                    int rep = chooser.showOpenDialog(null);

                    if (rep == JFileChooser.APPROVE_OPTION) {

                        // new Hex(chooser.getSelectedFile());

                        /*
                         * ParserHex pHex = null; // start long lStartTime = System.nanoTime();
                         * 
                         * // Parsing of the IntelHex file (and transfer of the data record with their respective start // addresses in the HashMap
                         * pHex.addressMap) try (FileInputStream isHex = new FileInputStream(chooser.getSelectedFile())) {
                         * System.out.println("Start parsing the IntelHex file: "); pHex = new ParserHex(isHex); RangeDetector rangeDetector = new
                         * RangeDetector(); pHex.setDataListener(rangeDetector); pHex.parse(); System.out.println("End parsing of the IntelHex: "); }
                         * catch (IOException | IntelHexException e1) { e1.printStackTrace(); }
                         * 
                         * // end long lEndTime = System.nanoTime();
                         * 
                         * // time elapsed long output = lEndTime - lStartTime; System.out.println("Hex parsing time in miliseconds: " + output /
                         * 1000000);
                         */

                        long lStartTime = System.nanoTime();
                        System.out.println("Start parsing the IntelHex file: ");
                        IntelHex pHex = null;
                        try {
                            pHex = new IntelHex(chooser.getSelectedFile().getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        long lEndTime = System.nanoTime();
                        long output = lEndTime - lStartTime;
                        System.out.println("Hex parsing time in miliseconds: " + output / 1000000);

                        Association.combine(a2l, pHex);
                    }

                }
            });
            add(btOpenHex);
        }
    }

}
