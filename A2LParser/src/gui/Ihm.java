/*
 * Creation : 2 janv. 2019
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import a2lobject.A2l;
import a2lobject.Characteristic;
import hex.HexDecoder;
import hex.IntelHex;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JList<Characteristic> list;
    private final JTextPane textPane;

    private List<Characteristic> listCharac = Collections.emptyList();
    private Vector<Characteristic> listCharacFiltre = new Vector<Characteristic>();

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
                if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
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
        private JTextField txtFiltre;

        private final StringBuilder sb = new StringBuilder();

        public PanelBt() {
            super();
            ((FlowLayout) getLayout()).setAlignment(FlowLayout.LEFT);
            btOpenA2L = new JButton(new AbstractAction("Open A2L") {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser("C:\\User\\U354706\\Perso\\WorkInProgress");
                    chooser.setFileFilter(new FileFilter() {

                        @Override
                        public String getDescription() {
                            return "Fichier A2L";
                        }

                        @Override
                        public boolean accept(File paramFile) {
                            if (paramFile.isDirectory())
                                return true;
                            return paramFile.getName().toLowerCase().endsWith("a2l");
                        }
                    });
                    int rep = chooser.showOpenDialog(null);

                    if (rep == JFileChooser.APPROVE_OPTION) {

                        long start = System.currentTimeMillis();

                        a2l = new A2l(chooser.getSelectedFile());

                        sb.append("A2L parsing time : " + (System.currentTimeMillis() - start) + "ms\n");

                        listCharac = a2l.getCharacteristics();
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
                    chooser.setFileFilter(new FileFilter() {

                        @Override
                        public String getDescription() {
                            return "Fichier Hex";
                        }

                        @Override
                        public boolean accept(File paramFile) {
                            if (paramFile.isDirectory())
                                return true;
                            return paramFile.getName().toLowerCase().endsWith("hex");
                        }
                    });
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

            txtFiltre = new JTextField(20);
            txtFiltre.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (listCharac.size() > 0)
                        setFilter(txtFiltre.getText());

                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (listCharac.size() > 0)
                        setFilter(txtFiltre.getText());

                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (listCharac.size() > 0)
                        setFilter(txtFiltre.getText());

                }
            });
            add(txtFiltre);
        }
    }

    private final void setFilter(String filtre) {

        final Set<Characteristic> tmpList = new LinkedHashSet<Characteristic>();

        listCharacFiltre.clear();

        final int nbLabel = listCharac.size();
        Characteristic charac;

        for (int i = 0; i < nbLabel; i++) {
            charac = listCharac.get(i);
            if (charac.toString().toLowerCase().indexOf(filtre.toLowerCase()) > -1) {
                tmpList.add(charac);
            }
        }

        listCharacFiltre.addAll(tmpList);
        list.setListData(listCharacFiltre);
    }

}
