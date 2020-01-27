/*
 * Creation : 2 janv. 2019
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeSelectionModel;

import a2l.A2l;
import a2l.A2lObject;
import a2l.A2lStateListener;
import a2l.AdjustableObject;
import a2l.Characteristic;
import a2l.Characteristic.CharacteristicType;
import a2l.Function;
import a2l.Measurement;
import a2l.TableModelView;
import data.DataCalibration;
import data.DataDecoder;
import data.IntelHex;
import data.MotorolaS19;
import net.ericaro.surfaceplotter.surface.JSurface;

public final class Ihm extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String INFO = "/INFO_24.png";
    private static final String WARNING = "/WARNING_24.png";

    private final Container container;
    private FilteredTree filteredTree;
    private JTree a2lTree;
    private JLabel labelData;
    private final PanelView panelView;
    private final JList<String> listLog;
    private final DefaultListModel<String> listModel;
    private JButton btOpenDataFile;

    private static final GridBagConstraints gc = new GridBagConstraints();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");;

    private A2l a2l;

    public Ihm() {
        super("AsamCalTool");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        container = getContentPane();
        container.setLayout(new GridBagLayout());
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        container.add(createToolBar(), gc);

        labelData = new JLabel("Data initialized with : ...");
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 2;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 0;
        gc.insets = new Insets(5, 5, 5, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        container.add(labelData, gc);

        filteredTree = new FilteredTree();
        a2lTree = filteredTree.getTree();
        a2lTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        a2lTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent treeEvent) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeEvent.getPath().getLastPathComponent();
                updateSelection(node);
            }
        });

        a2lTree.addTreeWillExpandListener(new TreeWillExpandListener() {

            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                if (!(node.getUserObject() instanceof String)) {
                    updateSelection(node);
                }

            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            }
        });
        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 1;
        gc.gridheight = 2;
        gc.weightx = 0.2;
        gc.weighty = 0.9;
        gc.fill = GridBagConstraints.BOTH;
        container.add(filteredTree, gc);

        panelView = new PanelView();
        gc.gridx = 1;
        gc.gridy = 2;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 0.8;
        gc.weighty = 0.9;
        gc.fill = GridBagConstraints.BOTH;
        container.add(panelView, gc);

        listModel = new DefaultListModel<String>();
        listModel.addListDataListener(new ListDataListener() {

            @Override
            public void intervalRemoved(ListDataEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void intervalAdded(ListDataEvent e) {
                listLog.ensureIndexIsVisible(listModel.getSize() - 1);
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                // TODO Auto-generated method stub

            }
        });
        listLog = new JList<String>(listModel);
        listLog.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value.toString().toUpperCase().contains("ERROR")) {
                    label.setIcon(new ImageIcon(getClass().getResource(WARNING)));
                    label.setForeground(Color.RED);
                } else {
                    label.setIcon(new ImageIcon(getClass().getResource(INFO)));
                    label.setForeground(Color.BLACK);
                }

                if (isSelected) {
                    label.setBackground(Color.LIGHT_GRAY);
                } else {
                    label.setBackground(Color.WHITE);
                }

                return label;
            }
        });
        JScrollPane jScrollPane = new JScrollPane(listLog);
        gc.gridx = 1;
        gc.gridy = 3;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 0.1;
        gc.fill = GridBagConstraints.BOTH;
        container.add(jScrollPane, gc);

        pack();
        setMinimumSize(new Dimension(getWidth(), getHeight()));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private final JToolBar createToolBar() {

        final String A2L = "/OPEN_A2L_24.png";
        final String DATA = "/OPEN_DATA_24.png";
        final String COMPAR = "/COMPAR_A2L_24.png";

        final JToolBar bar = new JToolBar();
        btOpenDataFile = new JButton("Open data file", new ImageIcon(getClass().getResource(DATA)));

        bar.setFloatable(false);
        bar.setBorder(BorderFactory.createEtchedBorder());

        final JButton btOpenA2L = new JButton(new AbstractAction("Open A2L", new ImageIcon(getClass().getResource(A2L))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser chooser = new JFileChooser("C:\\User\\U354706\\Perso\\WorkInProgress");
                chooser.setFileFilter(new FileFilter() {

                    @Override
                    public String getDescription() {
                        return "A2L files (*.a2l)";
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

                    A2lWorker worker = new A2lWorker(chooser.getSelectedFile());

                    worker.execute();
                }

            }
        });
        bar.add(btOpenA2L);

        bar.addSeparator();

        btOpenDataFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser("C:\\User\\U354706\\Perso\\WorkInProgress");
                chooser.setFileFilter(new FileFilter() {

                    @Override
                    public String getDescription() {
                        return "Data files (*.hex, *.s19)";
                    }

                    @Override
                    public boolean accept(File paramFile) {
                        if (paramFile.isDirectory())
                            return true;
                        return paramFile.getName().toLowerCase().endsWith("hex") || paramFile.getName().toLowerCase().endsWith("s19");
                    }
                });
                int rep = chooser.showOpenDialog(null);

                if (rep == JFileChooser.APPROVE_OPTION) {

                    labelData.setText("Data file : ...");

                    DataCalibration dataCalibration = null;

                    try {
                        String fileExtension = chooser.getSelectedFile().getName().toLowerCase();

                        if (fileExtension.endsWith("hex")) {
                            dataCalibration = new IntelHex(chooser.getSelectedFile().getAbsolutePath());
                        } else if (fileExtension.endsWith("s19")) {
                            dataCalibration = new MotorolaS19(chooser.getSelectedFile().getAbsolutePath());
                        }
                    } catch (IOException io) {
                        listModel.addElement(io.getMessage());
                    }

                    final DataDecoder dataDecoder = new DataDecoder(a2l, dataCalibration);

                    if (dataDecoder.readDataFromFile()) {
                        labelData.setText("<html>Data initialized with : " + "<b>" + chooser.getSelectedFile().getName() + "</b></html>");
                        listModel.addElement(
                                sdf.format(System.currentTimeMillis()) + " : " + chooser.getSelectedFile().getName() + " read succesfully");
                    } else {
                        JOptionPane.showMessageDialog(null, "EEPROM identifier doesn't match, reading aborted.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });
        btOpenDataFile.setEnabled(false);
        bar.add(btOpenDataFile);

        bar.addSeparator();

        final JButton btComparA2L = new JButton(new AbstractAction("Compare A2L", new ImageIcon(getClass().getResource(COMPAR))) {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser("C:\\User\\U354706\\Perso\\WorkInProgress");
                chooser.setMultiSelectionEnabled(true);
                chooser.setFileFilter(new FileFilter() {

                    @Override
                    public String getDescription() {
                        return "A2L files (*.a2l)";
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
                    File[] a2lFiles = chooser.getSelectedFiles();
                    if (a2lFiles.length == 2) {

                        StringBuilder sb = A2l.compareA2L(a2lFiles[0], a2lFiles[1]);

                        JTextArea textArea = new JTextArea(sb.toString());
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        scrollPane.setPreferredSize(new Dimension(500, 500));
                        JOptionPane.showMessageDialog(null, scrollPane);

                    } else {
                        JOptionPane.showMessageDialog(Ihm.this, "Two files are required !");
                    }

                }

            }
        });
        bar.add(btComparA2L);

        return bar;
    }

    private final class PanelView extends JPanel {
        private static final long serialVersionUID = 1L;

        private final JTextPane textPane;
        private final JLabel labelValues;
        private final JLabel labelChart;
        private final TableView tableView;
        private final SurfaceChart surfaceChart;

        public PanelView() {

            super();

            GridBagConstraints gc = new GridBagConstraints();

            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createLoweredSoftBevelBorder());
            setPreferredSize(new Dimension(1200, 800));
            setBackground(Color.WHITE);

            textPane = new JTextPane();
            textPane.setEditable(false);
            textPane.addHyperlinkListener(new HyperlinkListener() {

                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        boolean reached = filteredTree.followLink(e.getDescription());
                        if (!reached) {
                            JOptionPane.showMessageDialog(null, "Object not found !", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    }

                }
            });

            textPane.setContentType("text/html");
            gc.insets = new Insets(5, 5, 0, 0);
            gc.gridx = 0;
            gc.gridy = 0;
            gc.gridwidth = 2;
            gc.weightx = 100;
            gc.weighty = 40;
            gc.fill = GridBagConstraints.BOTH;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.getViewport().setPreferredSize(new Dimension(500, 400));
            add(scrollPane, gc);

            labelValues = new JLabel("<html><b><u>Values:</u></b>");
            gc.gridx = 0;
            gc.gridy = 1;
            gc.gridwidth = 1;
            gc.weightx = 0;
            gc.weighty = 0;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(5, 5, 0, 0);
            add(labelValues, gc);

            labelChart = new JLabel("<html><b><u>Chart:</u></b>");
            gc.gridx = 1;
            gc.gridy = 1;
            gc.gridwidth = 1;
            gc.weightx = 0;
            gc.weighty = 0;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(5, 5, 0, 0);
            add(labelChart, gc);

            tableView = new TableView(new TableModelView());
            gc.gridx = 0;
            gc.gridy = 2;
            gc.gridwidth = 1;
            gc.weightx = 50;
            gc.weighty = 50;
            gc.fill = GridBagConstraints.BOTH;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(0, 5, 0, 0);
            add(new JScrollPane(tableView), gc);

            surfaceChart = new SurfaceChart();
            gc.gridx = 1;
            gc.gridy = 2;
            gc.gridwidth = 1;
            gc.weightx = 50;
            gc.weighty = 50;
            gc.fill = GridBagConstraints.BOTH;
            gc.anchor = GridBagConstraints.FIRST_LINE_START;
            gc.insets = new Insets(0, 5, 0, 0);
            surfaceChart.setVisible(false);
            add(new JScrollPane(surfaceChart), gc);
        }

        public final void displayObject(A2lObject a2lObject) {
            textPane.setText(a2lObject.getProperties());
            textPane.setCaretPosition(0);
            if (a2lObject instanceof AdjustableObject) {
                tableView.getModel().setData(((AdjustableObject) a2lObject).getValues());
                TableView.adjustCells(tableView);
            } else {
                tableView.getModel().setData(null);
            }

        }

        public final void updateChart(Characteristic characteristic) {

            JSurface jSurface = surfaceChart.getSurface();

            switch (characteristic.getType()) {
            case MAP:
                surfaceChart.getArraySurfaceModel().setValues(characteristic.getValues().getXAxis(), characteristic.getValues().getYAxis(),
                        characteristic.getValues().getZvalues());
                jSurface.setXLabel("X [" + characteristic.getUnit()[0] + "]");
                jSurface.setYLabel("Y [" + characteristic.getUnit()[1] + "]");
                break;
            case CURVE:
                float[][] zValuesOrigin = characteristic.getValues().getZvalues();

                int length = zValuesOrigin[0].length;
                float[][] zValuesNew = new float[2][length];

                zValuesNew[0] = Arrays.copyOf(zValuesOrigin[0], length);
                zValuesNew[1] = Arrays.copyOf(zValuesOrigin[0], length);

                surfaceChart.getArraySurfaceModel().setValues(characteristic.getValues().getXAxis(), new float[] { 0, 1 }, zValuesNew);
                jSurface.setXLabel("X [" + characteristic.getUnit()[0] + "]");
                break;
            default:
                break;
            }

            surfaceChart.setVisible(true);

        }
    }

    private final void updateSelection(DefaultMutableTreeNode selectedNode) {

        panelView.surfaceChart.setVisible(false);

        if (selectedNode != null) {
            Object userObject = selectedNode.getUserObject();

            if (userObject instanceof String && selectedNode.getParent().toString().endsWith("MEASUREMENT")) {
                Enumeration<Measurement> enumMeasurment = a2l.getListMeasurement().elements();
                Measurement measurement;
                while (enumMeasurment.hasMoreElements()) {
                    measurement = enumMeasurment.nextElement();
                    if (userObject.toString().equals(measurement.toString())) {
                        userObject = measurement;
                        break;
                    }
                }
            } else {
                panelView.textPane.setText("...");
                panelView.tableView.getModel().setData(null);
            }

            if (userObject instanceof A2lObject && ((A2lObject) userObject).isValid()) {
                panelView.displayObject((A2lObject) userObject);

                if (userObject instanceof Characteristic) {
                    Characteristic characteristic = (Characteristic) userObject;
                    if (characteristic.hasData() && (characteristic.getType().compareTo(CharacteristicType.MAP) == 0
                            || characteristic.getType().compareTo(CharacteristicType.CURVE) == 0)) {
                        panelView.updateChart((Characteristic) userObject);
                    }
                } else if (userObject instanceof Function) {
                    Function function = (Function) userObject;
                    filteredTree.addChildToFunction(selectedNode, function);
                }
            }
        }
    }

    private final class A2lWorker extends SwingWorker<Boolean, Void> {
        private File a2lFile;

        public A2lWorker(File file) {
            this.a2lFile = file;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            a2l = new A2l();
            a2l.addA2lStateListener(new A2lStateListener() {

                @Override
                public void stateChange(final String state) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            listModel.addElement(sdf.format(System.currentTimeMillis()) + " : " + state);
                        }
                    });

                }
            });

            return a2l.parse(a2lFile);
        }

        @Override
        protected void done() {

            try {
                btOpenDataFile.setEnabled(get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            labelData.setText("Data initialized with : ...");
            panelView.textPane.setText("<html><br>");
            panelView.tableView.getModel().setData(null);
            filteredTree.addA2l(a2l);
        }

    }
}
