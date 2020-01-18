/*
 * Creation : 2 janv. 2019
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
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

    private final Container container;
    private FilteredTree filteredTree;
    private JTree a2lTree;
    private JLabel labelData;
    private final PanelView panelView;
    private final JTextArea textLog;

    private A2l a2l;

    public Ihm() {
        super("AsamCalTool");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(new PanelBt(), BorderLayout.NORTH);

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
        container.add(filteredTree, BorderLayout.WEST);

        panelView = new PanelView();
        container.add(panelView, BorderLayout.CENTER);

        textLog = new JTextArea(5,100);
        JScrollPane jScrollPane = new JScrollPane(new JScrollPane(textLog));
        jScrollPane.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
        container.add(jScrollPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private final class PanelBt extends JPanel {

        private static final long serialVersionUID = 1L;
        private JButton btOpenA2L;
        private JButton btOpenDataFile;
        private JButton btComparA2L;

        public PanelBt() {
            super();
            ((FlowLayout) getLayout()).setAlignment(FlowLayout.LEFT);

            labelData = new JLabel("Data initialized with : ...");

            btOpenA2L = new JButton(new AbstractAction("Open A2L") {

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
            add(btOpenA2L);

            btOpenDataFile = new JButton(new AbstractAction("Open data file") {

                private static final long serialVersionUID = 1L;

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

                        if (chooser.getSelectedFile().getName().toLowerCase().endsWith("hex")) {
                            try {
                                dataCalibration = new IntelHex(chooser.getSelectedFile().getAbsolutePath());
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if (chooser.getSelectedFile().getName().toLowerCase().endsWith("s19")) {
                            try {
                                dataCalibration = new MotorolaS19(chooser.getSelectedFile().getAbsolutePath());
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        DataDecoder dataDecoder = new DataDecoder(a2l, dataCalibration);

                        if (dataDecoder.readDataFromFile()) {
                            labelData.setText("<html>Data initialized with : " + "<b>" + chooser.getSelectedFile().getName() + "</b></html>");
                        } else {
                            JOptionPane.showMessageDialog(null, "EEPROM identifier doesn't match, reading aborted.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }
            });
            add(btOpenDataFile);

            add(labelData);

            final JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
            sep.setPreferredSize(new Dimension(20, 20));
            add(sep);

            btComparA2L = new JButton(new AbstractAction("Compare A2L") {

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
            add(btComparA2L);
        }
    }

    private final class PanelView extends JPanel {
        private static final long serialVersionUID = 1L;

        private final JTextPane textPane;
        private final JLabel labelValues;
        private final JLabel labelChart;
        private final TableView tableView;
        private final SurfaceChart surfaceChart;

        public PanelView() {

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

        }
    }

    private final void updateSelection(DefaultMutableTreeNode selectedNode) {

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
            }

            if (userObject instanceof A2lObject && ((A2lObject) userObject).isValid()) {
                panelView.displayObject((A2lObject) userObject);

                if (userObject instanceof Characteristic) {
                    Characteristic characteristic = (Characteristic) userObject;
                    if (characteristic.hasData() && (characteristic.getType().compareTo(CharacteristicType.MAP) == 0
                            || characteristic.getType().compareTo(CharacteristicType.CURVE) == 0)) {
                        panelView.updateChart((Characteristic) userObject);
                        panelView.surfaceChart.setVisible(true);
                    } else {
                        panelView.surfaceChart.setVisible(false);
                    }

                }

                if (userObject instanceof Function) {
                    Function function = (Function) userObject;
                    filteredTree.addChildToFunction(selectedNode, function);
                }
            }

        }
    }

    private final class A2lWorker extends SwingWorker<Void, Void> {
        private File a2lFile;
        private SimpleDateFormat sdf;

        public A2lWorker(File file) {
            this.a2lFile = file;
            sdf = new SimpleDateFormat("HH:mm:ss");
        }

        @Override
        protected Void doInBackground() throws Exception {
            a2l = new A2l();
            a2l.addA2lStateListener(new A2lStateListener() {

                @Override
                public void stateChange(final String state) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            textLog.append(sdf.format(System.currentTimeMillis()) +  " : " +  state + "\n");
                        }
                    });

                }
            });
            a2l.parse(a2lFile);
            return null;
        }

        @Override
        protected void done() {
            labelData.setText("Data initialized with : ...");
            panelView.textPane.setText("<html><br>");
            panelView.tableView.getModel().setData(null);
            filteredTree.addA2l(a2l);
            container.revalidate();
        }

    }
}
