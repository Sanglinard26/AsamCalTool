package gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public final class FilteredJTreeExample {

    public static void main(final String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Filtered JTree Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addComponentsToPane(frame.getContentPane());

        frame.pack();
        frame.setVisible(true);
    }

    private static void addComponentsToPane(final Container pane) {
        pane.setLayout(new GridBagLayout());

        JTree tree = createTree(pane);
        JTextField filter = createFilterField(pane);

        filter.getDocument().addDocumentListener(createDocumentListener(tree, filter));
    }

    private static JTree createTree(final Container pane) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");
        FilteredTreeModel model = new FilteredTreeModel(new DefaultTreeModel(root));
        JTree tree = new JTree(model);
        JScrollPane scrollPane = new JScrollPane(tree);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(scrollPane, c);
        createTreeNodes(root);
        expandTree(tree);

        return tree;
    }

    private static JTextField createFilterField(final Container pane) {
        JTextField filter = new JTextField();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(filter, c);

        return filter;
    }

    private static DocumentListener createDocumentListener(final JTree tree, final JTextField filter) {
        return new DocumentListener() {

            @Override
            public void insertUpdate(final DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                applyFilter();
            }

            public void applyFilter() {
                FilteredTreeModel filteredModel = (FilteredTreeModel) tree.getModel();
                filteredModel.setFilter(filter.getText());

                DefaultTreeModel treeModel = (DefaultTreeModel) filteredModel.getTreeModel();
                treeModel.reload();

                expandTree(tree);
            }
        };
    }

    private static void expandTree(final JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private static void createTreeNodes(final DefaultMutableTreeNode node) {
        DefaultMutableTreeNode ab = new DefaultMutableTreeNode("ab");
        DefaultMutableTreeNode cd = new DefaultMutableTreeNode("cd");
        DefaultMutableTreeNode ef = new DefaultMutableTreeNode("ef");
        DefaultMutableTreeNode gh = new DefaultMutableTreeNode("gh");
        DefaultMutableTreeNode ij = new DefaultMutableTreeNode("ij");
        DefaultMutableTreeNode kl = new DefaultMutableTreeNode("kl");
        DefaultMutableTreeNode mn = new DefaultMutableTreeNode("mn");
        DefaultMutableTreeNode op = new DefaultMutableTreeNode("op");
        DefaultMutableTreeNode qr = new DefaultMutableTreeNode("qr");
        DefaultMutableTreeNode st = new DefaultMutableTreeNode("st");
        DefaultMutableTreeNode uv = new DefaultMutableTreeNode("uv");
        DefaultMutableTreeNode wx = new DefaultMutableTreeNode("wx");
        DefaultMutableTreeNode yz = new DefaultMutableTreeNode("yz");

        node.add(ab);
        node.add(cd);
        ab.add(ef);
        ab.add(gh);
        cd.add(ij);
        cd.add(kl);
        ef.add(mn);
        ef.add(op);
        gh.add(qr);
        gh.add(st);
        ij.add(uv);
        ij.add(wx);

        node.add(yz);
    }
}