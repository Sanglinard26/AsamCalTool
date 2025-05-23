package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import a2l.A2l;
import a2l.A2lUtils;
import a2l.AdjustableObject;
import a2l.AxisPts;
import a2l.Characteristic;
import a2l.CompuMethod;
import a2l.ConversionTable;
import a2l.Function;
import a2l.Measurement;
import a2l.RecordLayout;

/**
 * Tree widget which allows the tree to be filtered on keystroke time. Only nodes who's toString matches the search field will remain in the tree or
 * its parents. Copyright (c) Oliver.Watkins
 */

public class FilteredTree extends JPanel {

    private static final long serialVersionUID = 1L;

    private String filteredText = "";
    private DefaultTreeModel originalTreeModel;
    private final JTree tree = new JTree();
    private DefaultMutableTreeNode originalRoot;
    private final JTextField field;
    private final JToggleButton toggleReadOnly;

    private final HashMap<String, Boolean> filter;

    public FilteredTree() {

        this.filter = new HashMap<String, Boolean>();
        this.filter.put("READ_ONLY", false);

        this.originalRoot = new DefaultMutableTreeNode();
        tree.setRowHeight(18);
        tree.setLargeModel(true);

        tree.setExpandsSelectedPaths(true);

        tree.addMouseListener(new NodeMouseListener());
        tree.setCellRenderer(new Renderer());

        field = new JTextField(50);

        field.addKeyListener(new FilterKeyListener());

        originalTreeModel = new DefaultTreeModel(originalRoot);

        tree.setModel(originalTreeModel);

        this.setLayout(new BorderLayout());

        add(field, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);

        toggleReadOnly = new JToggleButton("Read only Characteristic");
        add(toggleReadOnly, BorderLayout.SOUTH);

        toggleReadOnly.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                filter.put("READ_ONLY", toggleReadOnly.isSelected());
                filterTree(field.getText());
            }
        });

        this.setVisible(false);

        originalRoot = (DefaultMutableTreeNode) originalTreeModel.getRoot();

    }

    private final class FilterKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    filterTree(field.getText());

                }
            });
        }
    }

    public final void addA2l(A2l a2l) {
        this.originalRoot = new DefaultMutableTreeNode(a2l);
        originalTreeModel = new DefaultTreeModel(this.originalRoot);
        tree.setModel(originalTreeModel);
        buildTree(a2l);
        this.setVisible(true);
        if (this.originalRoot.getChildCount() > 0) {
            tree.expandRow(0);
        }
    }

    private final void buildTree(A2l a2l) {

        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("ADJUSTABLE OBJECT");
        this.originalRoot.add(childNode);
        DynamicUtilTreeNode.createChildren(childNode, a2l.getListAdjustableObjects());

        childNode = new DefaultMutableTreeNode("FUNCTION");
        this.originalRoot.add(childNode);
        DynamicUtilTreeNode.createChildren(childNode, a2l.getListFunction());

        Enumeration<?> functionEnum = childNode.children();
        while (functionEnum.hasMoreElements()) {
            DefaultMutableTreeNode function = (DefaultMutableTreeNode) functionEnum.nextElement();
            function.setAllowsChildren(true);
        }

        childNode = new DefaultMutableTreeNode("COMPU_METHOD");
        this.originalRoot.add(childNode);
        DynamicUtilTreeNode.createChildren(childNode, a2l.getListCompuMethod());

        childNode = new DefaultMutableTreeNode("CONVERSION_TABLE");
        this.originalRoot.add(childNode);
        DynamicUtilTreeNode.createChildren(childNode, a2l.getListConversionTable());

        childNode = new DefaultMutableTreeNode("RECORD_LAYOUT");
        this.originalRoot.add(childNode);
        DynamicUtilTreeNode.createChildren(childNode, a2l.getListRecordLayout());

        childNode = new DefaultMutableTreeNode("UNIT");
        this.originalRoot.add(childNode);
        DynamicUtilTreeNode.createChildren(childNode, a2l.getListUnit());

        childNode = new DefaultMutableTreeNode("SYSTEM_CONSTANT");
        this.originalRoot.add(childNode);
        DynamicUtilTreeNode.createChildren(childNode, a2l.getListSystemConstant());

        childNode = new DefaultMutableTreeNode("MEASUREMENT");
        this.originalRoot.add(childNode);
        DynamicUtilTreeNode.createChildren(childNode, a2l.getListMeasurement());

        childNode = new DefaultMutableTreeNode("GROUP");
        this.originalRoot.add(childNode);
        DynamicUtilTreeNode.createChildren(childNode, a2l.getListGroup());
    }

    public final void addChildToFunction(DefaultMutableTreeNode functionNode, Function function) {

        if (functionNode.getChildCount() > 0) {
            return;
        }

        A2l a2l = (A2l) this.originalRoot.getUserObject();
        DefaultMutableTreeNode childNode;
        Vector<?> childs;

        childs = a2l.getAdjustableObjectByFunction(functionNode.toString());
        if (childs.size() > 0) {
            childNode = new DefaultMutableTreeNode("DEF_CHARACTERISTIC");
            functionNode.add(childNode);
            DynamicUtilTreeNode.createChildren(childNode, childs);
        }

        childs = function.getInMeasurement();
        if (childs.size() > 0) {
            childNode = new DefaultMutableTreeNode("IN_MEASUREMENT");
            functionNode.add(childNode);
            DynamicUtilTreeNode.createChildren(childNode, childs);
        }

        childs = function.getLocMeasurement();
        if (childs.size() > 0) {
            childNode = new DefaultMutableTreeNode("LOC_MEASUREMENT");
            functionNode.add(childNode);
            DynamicUtilTreeNode.createChildren(childNode, childs);
        }

        childs = function.getOutMeasurement();
        if (childs.size() > 0) {
            childNode = new DefaultMutableTreeNode("OUT_MEASUREMENT");
            functionNode.add(childNode);
            DynamicUtilTreeNode.createChildren(childNode, childs);
        }

        childs = a2l.getAdjustableObjectFromList(function.getRefCharacteristic());
        if (childs.size() > 0) {
            childNode = new DefaultMutableTreeNode("REF_CHARACTERISTIC");
            functionNode.add(childNode);
            DynamicUtilTreeNode.createChildren(childNode, childs);
        }

    }

    public final boolean followLink(String textLink) {

        filterTree(""); // Remove filter in order to find the object

        final TreePath path = find((DefaultMutableTreeNode) this.tree.getModel().getRoot(), textLink);
        if (path == null) {
            return false;
        }
        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
        return true;
    }

    private TreePath find(DefaultMutableTreeNode root, String s) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        DefaultMutableTreeNode node;
        while (e.hasMoreElements()) {
            node = e.nextElement();
            if (node.toString().equalsIgnoreCase(s)) {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    /**
     * @param text
     */

    private synchronized void filterTree(String text) {

        filteredText = text.toLowerCase();

        int nbFilter = Collections.frequency(filter.values(), true);

        if ((text.trim().length() == 0 || "*".equals(text.trim())) && nbFilter == 0) {

            originalTreeModel.setRoot(originalRoot);

            tree.setModel(originalTreeModel);

            return;
        }
        // get a copy
        DefaultMutableTreeNode filteredRoot = copyNode(originalRoot);
        TreeNodeBuilder b = new TreeNodeBuilder(text, filter);
        filteredRoot = b.prune((DefaultMutableTreeNode) filteredRoot.getRoot());

        originalTreeModel.setRoot(filteredRoot);

        tree.setModel(originalTreeModel);

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

    }

    /**
     * Clone/Copy a tree node. TreeNodes in Swing don't support deep cloning.
     *
     * @param orig to be cloned
     * @return cloned copy
     */
    private final DefaultMutableTreeNode copyNode(DefaultMutableTreeNode orig) {

        DefaultMutableTreeNode newOne = new DefaultMutableTreeNode(orig.getUserObject());

        Enumeration<?> enm = orig.children();

        DefaultMutableTreeNode child;

        while (enm.hasMoreElements()) {
            child = (DefaultMutableTreeNode) enm.nextElement();
            newOne.add(copyNode(child));
        }
        return newOne;
    }

    /**
     * Renders bold any tree nodes who's toString() value starts with the filtered text we are filtering on.
     *
     * @author Oliver.Watkins
     */
    private class Renderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = 1L;

        private static final String A2L = "/A2L.png";
        private static final String COMPU_METHOD = "/COMPU_METHOD.png";
        private static final String FUNCTION = "/FUNCTION.png";
        private static final String RECORD_LAYOUT = "/RECORD_LAYOUT.png";
        private static final String MEASUREMENT = "/MEASUREMENT.png";
        private static final String IN_MEASUREMENT = "/IN_MEASUREMENT.png";
        private static final String LOC_MEASUREMENT = "/LOC_MEASUREMENT.png";
        private static final String OUT_MEASUREMENT = "/OUT_MEASUREMENT.png";
        private static final String CONVERSION_TABLE = "/CONVERSION_TABLE.png";
        private static final String SCALAIRE = "/SCALAIRE.png";
        private static final String CURVE = "/CURVE.png";
        private static final String MAP = "/MAP.png";
        private static final String INCONNU = "/INCONNU.png";
        private static final String VALUEBLOCK = "/VALUEBLOCK.png";
        private static final String AXIS = "/AXIS.png";
        private static final String ASCII = "/ASCII.png";

        private final ImageIcon[] icons = new ImageIcon[] { new ImageIcon(getClass().getResource(SCALAIRE)),
                new ImageIcon(getClass().getResource(CURVE)), new ImageIcon(getClass().getResource(MAP)),
                new ImageIcon(getClass().getResource(INCONNU)), new ImageIcon(getClass().getResource(VALUEBLOCK)),
                new ImageIcon(getClass().getResource(AXIS)), new ImageIcon(getClass().getResource(ASCII)), new ImageIcon(getClass().getResource(A2L)),
                new ImageIcon(getClass().getResource(COMPU_METHOD)), new ImageIcon(getClass().getResource(FUNCTION)),
                new ImageIcon(getClass().getResource(RECORD_LAYOUT)), new ImageIcon(getClass().getResource(CONVERSION_TABLE)),
                new ImageIcon(getClass().getResource(MEASUREMENT)), new ImageIcon(getClass().getResource(IN_MEASUREMENT)),
                new ImageIcon(getClass().getResource(LOC_MEASUREMENT)), new ImageIcon(getClass().getResource(OUT_MEASUREMENT)) };

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasfocus) {

            JLabel c = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasfocus);

            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

            if ("ROOT".equals(userObject)) {
                return c;
            }

            if (userObject instanceof A2l) {
                c.setIcon(icons[7]);
            } else if (userObject instanceof AxisPts) {
                c.setIcon(icons[5]);
            } else if (userObject instanceof CompuMethod) {
                c.setIcon(icons[8]);
            } else if (userObject instanceof Function) {
                c.setIcon(icons[9]);
            } else if (userObject instanceof RecordLayout) {
                c.setIcon(icons[10]);
            } else if (userObject instanceof ConversionTable) {
                c.setIcon(icons[11]);
            } else if (userObject instanceof Measurement) {
                c.setIcon(icons[12]);
            } else if (userObject instanceof AdjustableObject) {
                switch (((Characteristic) userObject).getType()) {
                case VALUE:
                    c.setIcon(icons[0]);
                    break;
                case CURVE:
                    c.setIcon(icons[1]);
                    break;
                case MAP:
                    c.setIcon(icons[2]);
                    break;
                case VAL_BLK:
                    c.setIcon(icons[4]);
                    break;
                case ASCII:
                    c.setIcon(icons[6]);
                    break;
                default:
                    c.setIcon(icons[3]);
                    break;
                }
            } else if (userObject instanceof String && ((DefaultMutableTreeNode) value).getParent().toString().endsWith("MEASUREMENT")) {
                String parent = ((DefaultMutableTreeNode) value).getParent().toString();

                switch (parent) {
                case "IN_MEASUREMENT":
                    c.setIcon(icons[13]);
                    break;
                case "LOC_MEASUREMENT":
                    c.setIcon(icons[14]);
                    break;
                case "OUT_MEASUREMENT":
                    c.setIcon(icons[15]);
                    break;
                default:
                    break;
                }
            }

            Font newFont;

            if (filteredText.length() > 0 && value.toString().toLowerCase().startsWith(filteredText)) {
                newFont = c.getFont().deriveFont(Font.BOLD);
            } else {
                newFont = c.getFont().deriveFont(Font.PLAIN);
            }

            c.setFont(newFont);

            setBackgroundSelectionColor(Color.LIGHT_GRAY);
            setTextSelectionColor(Color.BLACK);

            return c;
        }
    }

    public JTree getTree() {
        return tree;
    }

    private final class NodeMouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());

            if (treePath == null) {
                return;
            }

            final Object object = ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject();

            if (e.isPopupTrigger() && (object instanceof A2l)) {
                tree.setSelectionPath(treePath);

                final JPopupMenu menu = new JPopupMenu();

                JMenuItem menuItem = new JMenuItem("Show in text format");
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent paramActionEvent) {

                        // new A2lDisplayer(((A2l) object).getPath());
                        TextSearchTest.createAndShowGUI(((A2l) object).getPath());

                    }
                });
                menu.add(menuItem);

                menuItem = new JMenuItem("Link prototype axis");
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent paramActionEvent) {
                        final JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new FileFilter() {

                            @Override
                            public String getDescription() {
                                return "lab file (*.a2l)";
                            }

                            @Override
                            public boolean accept(File paramFile) {
                                if (paramFile.isDirectory())
                                    return true;
                                return paramFile.getName().toLowerCase().endsWith("lab");
                            }
                        });
                        int rep = chooser.showOpenDialog(null);

                        if (rep == JFileChooser.APPROVE_OPTION) {
                            A2lUtils.linkPrototypeAxis(object, chooser.getSelectedFile());
                        }
                    }
                });
                menu.add(menuItem);

                menuItem = new JMenuItem("Check MEI bloc");
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent paramActionEvent) {
                        final JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new FileFilter() {

                            @Override
                            public String getDescription() {
                                return "text file (*.txt)";
                            }

                            @Override
                            public boolean accept(File paramFile) {
                                if (paramFile.isDirectory())
                                    return true;
                                return paramFile.getName().toLowerCase().endsWith("txt");
                            }
                        });
                        int rep = chooser.showOpenDialog(null);

                        if (rep == JFileChooser.APPROVE_OPTION) {
                            A2lUtils.checkMEIBloc(object, chooser.getSelectedFile());
                            JOptionPane.showMessageDialog(null, "Done !");
                        }
                    }
                });
                menu.add(menuItem);

                menuItem = new JMenuItem("Search FId");
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent paramActionEvent) {
                        String fidName = JOptionPane.showInputDialog("Enter FId name :", "");

                        if (!fidName.isEmpty()) {
                            List<String> result = A2lUtils.searchFId(object, fidName);
                            JPanel panel = new JPanel();
                            JTextArea area = new JTextArea();

                            for (String s : result) {
                                area.append(s + "\n");
                            }

                            panel.add(area);
                            JOptionPane.showMessageDialog(null, panel, "Results", JOptionPane.INFORMATION_MESSAGE);
                        }

                    }
                });
                menu.add(menuItem);

                menuItem = new JMenuItem("Check measurement from lab");
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent paramActionEvent) {
                        final JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileFilter(new FileFilter() {

                            @Override
                            public String getDescription() {
                                return "Fichier Lab (*.lab)";
                            }

                            @Override
                            public boolean accept(File f) {
                                if (f.isDirectory())
                                    return true;
                                return f.getName().toLowerCase().endsWith("lab");
                            }
                        });

                        final int rep = fileChooser.showOpenDialog(null);

                        if (rep == JFileChooser.APPROVE_OPTION) {
                            A2lUtils.checkMeasurementFromLab(object, fileChooser.getSelectedFile());
                            JOptionPane.showMessageDialog(null, "Check done !");
                        }

                    }
                });
                menu.add(menuItem);

                menuItem = new JMenuItem("Export all measurement");
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent paramActionEvent) {
                        final JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileFilter(new FileFilter() {

                            @Override
                            public String getDescription() {
                                return "Fichier Lab (*.lab)";
                            }

                            @Override
                            public boolean accept(File f) {
                                String fileName = f.getName();
                                int lenght = fileName.length();
                                if (lenght < 5) {
                                    return false;
                                }
                                return f.getName().substring(lenght - 4, lenght - 1).equals("lab");
                            }
                        });
                        fileChooser.setSelectedFile(new File(object.toString() + "_Measurements.lab"));
                        final int rep = fileChooser.showSaveDialog(null);

                        if (rep == JFileChooser.APPROVE_OPTION) {
                            A2lUtils.writeAllMeasurementLab(fileChooser.getSelectedFile(), (A2l) FilteredTree.this.originalRoot.getUserObject());
                            JOptionPane.showMessageDialog(null, "Export done !");
                        }

                    }
                });
                menu.add(menuItem);

                menuItem = new JMenuItem("Get Z resolution from lab");
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent paramActionEvent) {
                        final JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new FileFilter() {

                            @Override
                            public String getDescription() {
                                return "lab file (*.a2l)";
                            }

                            @Override
                            public boolean accept(File paramFile) {
                                if (paramFile.isDirectory())
                                    return true;
                                return paramFile.getName().toLowerCase().endsWith("lab");
                            }
                        });
                        int rep = chooser.showOpenDialog(null);

                        if (rep == JFileChooser.APPROVE_OPTION) {
                            File resFile = A2lUtils.getZResolutionFromLab(object, chooser.getSelectedFile());

                            if (resFile != null) {
                                JOptionPane.showMessageDialog(null, "File saved : " + resFile.getAbsolutePath());
                            } else {
                                JOptionPane.showMessageDialog(null, "Error : No file created");
                            }
                        }
                    }
                });
                menu.add(menuItem);

                menu.show(e.getComponent(), e.getX(), e.getY());
            }

            if (e.isPopupTrigger() && (object instanceof Function)) {

                tree.setSelectionPath(treePath);

                final JPopupMenu menu = new JPopupMenu();

                final JMenuItem menuCharacteristic = new JMenuItem("Export characteristic in lab file");
                menuCharacteristic.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent paramActionEvent) {

                        final JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileFilter(new FileFilter() {

                            @Override
                            public String getDescription() {
                                return "Fichier Lab (*.lab)";
                            }

                            @Override
                            public boolean accept(File f) {
                                String fileName = f.getName();
                                int lenght = fileName.length();
                                if (lenght < 5) {
                                    return false;
                                }
                                return f.getName().substring(lenght - 4, lenght - 1).equals("lab");
                            }
                        });
                        fileChooser.setSelectedFile(new File(object.toString() + "_Charateristics.lab"));
                        final int rep = fileChooser.showSaveDialog(null);

                        if (rep == JFileChooser.APPROVE_OPTION) {
                            A2lUtils.writeCharacteristicLab(fileChooser.getSelectedFile(), (A2l) originalRoot.getUserObject(), (Function) object);
                            JOptionPane.showMessageDialog(null, "Export done !");
                        }

                    }
                });
                menu.add(menuCharacteristic);

                final JMenuItem menuMeasurement = new JMenuItem("Export measurement in lab file");
                menuMeasurement.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent paramActionEvent) {

                        final JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileFilter(new FileFilter() {

                            @Override
                            public String getDescription() {
                                return "Fichier Lab (*.lab)";
                            }

                            @Override
                            public boolean accept(File f) {
                                String fileName = f.getName();
                                int lenght = fileName.length();
                                if (lenght < 5) {
                                    return false;
                                }
                                return f.getName().substring(lenght - 4, lenght - 1).equals("lab");
                            }
                        });
                        fileChooser.setSelectedFile(new File(object.toString() + "_Measurements.lab"));
                        final int rep = fileChooser.showSaveDialog(null);

                        if (rep == JFileChooser.APPROVE_OPTION) {
                            A2lUtils.writeMeasurementLab(fileChooser.getSelectedFile(), (Function) object);
                            JOptionPane.showMessageDialog(null, "Export done !");
                        }

                    }
                });
                menu.add(menuMeasurement);

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    /**
     * Class that prunes off all leaves which do not match the search string.
     *
     * @author Oliver.Watkins
     */

    public class TreeNodeBuilder {

        private String textToMatch;
        private HashMap<String, Boolean> filter;

        public TreeNodeBuilder(String textToMatch, HashMap<String, Boolean> filter) {
            this.textToMatch = textToMatch.toLowerCase();
            this.filter = filter;
        }

        public DefaultMutableTreeNode prune(DefaultMutableTreeNode root) {

            boolean badLeaves = true;

            // keep looping through until tree contains only leaves that match
            while (badLeaves) {
                badLeaves = removeBadLeaves(root);
            }
            return root;
        }

        /**
         * @param root
         * @return boolean bad leaves were returned
         */
        private boolean removeBadLeaves(DefaultMutableTreeNode root) {

            // no bad leaves yet
            boolean badLeaves = false;

            // reference first leaf
            DefaultMutableTreeNode leaf = root.getFirstLeaf();

            // if leaf is root then its the only node
            if (leaf.isRoot())
                return false;

            int leafCount = root.getLeafCount(); // this get method changes if in for loop so have to define outside of it
            for (int i = 0; i < leafCount; i++) {

                DefaultMutableTreeNode nextLeaf = leaf.getNextLeaf();

                if (filter.get("READ_ONLY") && leaf.getUserObject() instanceof AdjustableObject
                        && !((AdjustableObject) leaf.getUserObject()).isReadOnly()) {

                    leaf.removeFromParent();

                    badLeaves = true;
                } else if (filter.get("READ_ONLY") && !(leaf.getUserObject() instanceof AdjustableObject)) {
                    leaf.removeFromParent();

                    badLeaves = true;
                }

                // if it does not start with the text then snip it off its parent
                if (textToMatch.length() > 0 && textToMatch.charAt(0) == '*') {
                    if (!leaf.getUserObject().toString().toLowerCase().contains(textToMatch.substring(1))) {
                        leaf.removeFromParent();

                        badLeaves = true;
                    }
                } else {
                    if (!leaf.getUserObject().toString().toLowerCase().startsWith(textToMatch)) {
                        leaf.removeFromParent();

                        badLeaves = true;
                    }

                }

                leaf = nextLeaf;

            }
            return badLeaves;
        }
    }
}
