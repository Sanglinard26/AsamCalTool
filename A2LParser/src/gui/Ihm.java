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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import a2lobject.A2l;
import a2lobject.Characteristic;
import hex.HexDecoder;
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

        list = new JList<Characteristic>();
        textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(500, 500));

        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    textPane.setText(list.getSelectedValue().getValues());
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

        private static final long serialVersionUID = 1L;
        private JButton btOpenA2L;
        private JButton btOpenHex;

        final StringBuilder sb = new StringBuilder();

        public PanelBt() {
            super();
            ((FlowLayout) getLayout()).setAlignment(FlowLayout.LEFT);
            btOpenA2L = new JButton(new AbstractAction("Open A2L") {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser("C:\\User\\U354706\\Perso\\WorkInProgress");
                    int rep = chooser.showOpenDialog(null);

                    if (rep == JFileChooser.APPROVE_OPTION) {

                        long start = System.currentTimeMillis();

                        a2l = new A2l(chooser.getSelectedFile());

                        sb.append("A2L parsing time : " + (System.currentTimeMillis() - start) + "ms\n");

                        List<Characteristic> listCharac = a2l.getCharacteristics();
                        Collections.sort(listCharac);
                        list.setListData(listCharac.toArray(new Characteristic[a2l.getCharacteristics().size()]));
                    }

                }
            });
            add(btOpenA2L);

            btOpenHex = new JButton(new AbstractAction("Open HEX") {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser("C:\\User\\U354706\\Perso\\WorkInProgress");
                    int rep = chooser.showOpenDialog(null);

                    if (rep == JFileChooser.APPROVE_OPTION) {

                        long lStartTime = System.nanoTime();
                        IntelHex pHex = null;
                        try {
                            pHex = new IntelHex(chooser.getSelectedFile().getAbsolutePath());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        long lEndTime = System.nanoTime();
                        long output = lEndTime - lStartTime;
                        sb.append("Hex parsing time : " + output / 1000000 + "ms\n");

                        lStartTime = System.nanoTime();

                        HexDecoder hexDecoder = new HexDecoder(a2l, pHex);

                        if (hexDecoder.checkEPK()) {
                            if (hexDecoder.readDataFromHex()) {
                                lEndTime = System.nanoTime();
                                output = lEndTime - lStartTime;
                                sb.append("Reading hex data : " + output / 1000000 + "ms\n");
                                JOptionPane.showMessageDialog(Ihm.this, sb.toString());
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "L'identifiant de l'EEPROM ne correspond pas !\nInterruption de la lecture.",
                                    "Attention", JOptionPane.WARNING_MESSAGE);
                        }
                    }

                }
            });
            add(btOpenHex);
        }
    }

}
