package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import a2l.A2l;
import a2l.AdjustableObject;
import a2l.AxisPts;
import a2l.Characteristic;
import a2l.CompuMethod;
import a2l.ConversionTable;
import a2l.Function;
import a2l.RecordLayout;

/**
 * Tree widget which allows the tree to be filtered on keystroke time. Only nodes who's
 * toString matches the search field will remain in the tree or its parents.
 *
 * Copyright (c) Oliver.Watkins
 */

public class FilteredTree extends JPanel{

	private static final long serialVersionUID = 1L;

	private String filteredText = "";
	private DefaultTreeModel originalTreeModel;
	private final JTree tree = new JTree();
	private DefaultMutableTreeNode originalRoot;

	public FilteredTree(A2l a2l){
		this.originalRoot = new DefaultMutableTreeNode(a2l);
		tree.setRowHeight(18);
		tree.setExpandsSelectedPaths(true);
		buildTree(a2l);
		guiLayout();

	}

	private final void buildTree(A2l a2l)
	{
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
	}

	public final void addChildToFunction(DefaultMutableTreeNode function)
	{
		A2l a2l = (A2l) this.originalRoot.getUserObject();
		DynamicUtilTreeNode.createChildren(function, a2l.getAdjustableObjectByFunction(function.toString()));
	}

	private void guiLayout() {
		tree.setCellRenderer(new Renderer());

		final JTextField field = new JTextField(10);

		field.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent paramDocumentEvent) {
				filterTree(field.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent paramDocumentEvent) {
				filterTree(field.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent paramDocumentEvent) {
				filterTree(field.getText());
			}
		});

		originalTreeModel = new DefaultTreeModel(originalRoot);

		tree.setModel(originalTreeModel);

		this.setLayout(new BorderLayout());

		add(field, BorderLayout.NORTH);
		add(new JScrollPane(tree), BorderLayout.CENTER);

		originalRoot = (DefaultMutableTreeNode) originalTreeModel.getRoot();

	}

	/**
	 *
	 * @param text
	 */

	private void filterTree(String text) {
		
		filteredText = text;

		if (text.trim().length() == 0) {

			//reset with the original root
			originalTreeModel.setRoot(originalRoot);

			tree.setModel(originalTreeModel);
			tree.updateUI();
			//scrollpane.getViewport().setView(tree);
			
			return;
		} else {

			//get a copy
			DefaultMutableTreeNode filteredRoot = copyNode(originalRoot);
			TreeNodeBuilder b = new TreeNodeBuilder(text);
			filteredRoot = b.prune((DefaultMutableTreeNode) filteredRoot.getRoot());

			originalTreeModel.setRoot(filteredRoot);

			tree.setModel(originalTreeModel);
			tree.updateUI();
			//scrollpane.getViewport().setView(tree);

			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}
		}


	}

	/**
	 * Clone/Copy a tree node. TreeNodes in Swing don't support deep cloning.
	 *
	 * @param orig to be cloned
	 * @return cloned copy
	 */
	private DefaultMutableTreeNode copyNode(DefaultMutableTreeNode orig) {

		DefaultMutableTreeNode newOne = new DefaultMutableTreeNode();
		newOne.setUserObject(orig.getUserObject());

		Enumeration<?> enm = orig.children();

		while(enm.hasMoreElements()){

			DefaultMutableTreeNode child = (DefaultMutableTreeNode) enm.nextElement();
			newOne.add(copyNode(child));
		}
		return newOne;
	}

	/**
	 * Renders bold any tree nodes who's toString() value starts with the filtered text
	 * we are filtering on.
	 *
	 * @author Oliver.Watkins
	 */
	private class Renderer extends DefaultTreeCellRenderer{

		private static final long serialVersionUID = 1L;

		private static final String A2L = "/A2L.png";
		private static final String COMPU_METHOD = "/COMPU_METHOD.png";
		private static final String FUNCTION = "/FUNCTION.png";
		private static final String RECORD_LAYOUT = "/RECORD_LAYOUT.png";
		private static final String CONVERSION_TABLE = "/CONVERSION_TABLE.png";
		private static final String SCALAIRE = "/SCALAIRE.png";
		private static final String CURVE = "/CURVE.png";
		private static final String MAP = "/MAP.png";
		private static final String INCONNU = "/INCONNU.png";
		private static final String VALUEBLOCK = "/VALUEBLOCK.png";
		private static final String AXIS = "/AXIS.png";
		private static final String ASCII = "/ASCII.png";

		private final ImageIcon[] icons = new ImageIcon[]{
				new ImageIcon(getClass().getResource(SCALAIRE)),
				new ImageIcon(getClass().getResource(CURVE)),
				new ImageIcon(getClass().getResource(MAP)),
				new ImageIcon(getClass().getResource(INCONNU)),
				new ImageIcon(getClass().getResource(VALUEBLOCK)),
				new ImageIcon(getClass().getResource(AXIS)),
				new ImageIcon(getClass().getResource(ASCII)),
				new ImageIcon(getClass().getResource(A2L)), 
				new ImageIcon(getClass().getResource(COMPU_METHOD)),
				new ImageIcon(getClass().getResource(FUNCTION)),
				new ImageIcon(getClass().getResource(RECORD_LAYOUT)),
				new ImageIcon(getClass().getResource(CONVERSION_TABLE))};

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasfocus) {

			JLabel c = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasfocus);

			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

			if(userObject instanceof A2l)
			{
				c.setIcon(icons[7]);
			}else if(userObject instanceof AxisPts)
			{
				c.setIcon(icons[5]);
			}else if(userObject instanceof CompuMethod)
			{
				c.setIcon(icons[8]);
			}else if(userObject instanceof Function)
			{
				c.setIcon(icons[9]);
			}else if(userObject instanceof RecordLayout)
			{
				c.setIcon(icons[10]);
			}else if(userObject instanceof ConversionTable)
			{
				c.setIcon(icons[11]);
			}else if (userObject instanceof AdjustableObject){
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
			}

			Font newFont;

			if (filteredText.length() > 0 && value.toString().startsWith(filteredText)){
				newFont = c.getFont().deriveFont(Font.BOLD);
			}else{
				newFont = c.getFont().deriveFont(Font.PLAIN);
			}

			c.setFont(newFont);

			return c;
		}
	}

	public JTree getTree() {
		return tree;
	}

	/**
	 * Class that prunes off all leaves which do not match the search string.
	 *
	 * @author Oliver.Watkins
	 */

	public class TreeNodeBuilder {

		private String textToMatch;

		public TreeNodeBuilder(String textToMatch) {
			this.textToMatch = textToMatch;
		}

		public DefaultMutableTreeNode prune(DefaultMutableTreeNode root) {

			boolean badLeaves = true;

			//keep looping through until tree contains only leaves that match
			while (badLeaves){
				badLeaves = removeBadLeaves(root);
			}
			return root;
		}

		/**
		 *
		 * @param root
		 * @return boolean bad leaves were returned
		 */
		private boolean removeBadLeaves(DefaultMutableTreeNode root) {

			//no bad leaves yet
			boolean badLeaves = false;

			//reference first leaf
			DefaultMutableTreeNode leaf = root.getFirstLeaf();

			//if leaf is root then its the only node
			if (leaf.isRoot())
				return false;

			int leafCount = root.getLeafCount(); //this get method changes if in for loop so have to define outside of it
			for (int i = 0; i < leafCount; i++) {

				DefaultMutableTreeNode nextLeaf = leaf.getNextLeaf();

				//if it does not start with the text then snip it off its parent
				if (!leaf.getUserObject().toString().startsWith(textToMatch)) {
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) leaf.getParent();

					if (parent != null)
						parent.remove(leaf);

					badLeaves = true;
				}
				leaf = nextLeaf;
			}
			return badLeaves;
		}
	}
}
