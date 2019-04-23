package gui;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import a2l.A2l;

public final class A2lTree extends JTree {

	private static final long serialVersionUID = 1L;
	
	private final DefaultMutableTreeNode a2lRoot;
	
	public A2lTree(A2l a2l) {
		this.a2lRoot = new DefaultMutableTreeNode(a2l);
		setModel(new DefaultTreeModel(a2lRoot));
		buildTree(a2l);
	}
	
	private final void buildTree(A2l a2l)
	{
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("ADJUSTABLE OBJECT");
		this.a2lRoot.add(childNode);
		DynamicUtilTreeNode.createChildren(childNode, a2l.getListAdjustableObjects());
		
		childNode = new DefaultMutableTreeNode("FUNCTION");
		this.a2lRoot.add(childNode);
		DynamicUtilTreeNode.createChildren(childNode, a2l.getListFunction());
		
		Enumeration<?> functionEnum = childNode.children();
		while (functionEnum.hasMoreElements()) {
			DefaultMutableTreeNode function = (DefaultMutableTreeNode) functionEnum.nextElement();
			function.setAllowsChildren(true);
		}
		
		childNode = new DefaultMutableTreeNode("COMPU_METHOD");
		this.a2lRoot.add(childNode);
		DynamicUtilTreeNode.createChildren(childNode, a2l.getListCompuMethod());
		
		childNode = new DefaultMutableTreeNode("CONVERSION_TABLE");
		this.a2lRoot.add(childNode);
		DynamicUtilTreeNode.createChildren(childNode, a2l.getListConversionTable());
		
		childNode = new DefaultMutableTreeNode("RECORD_LAYOUT");
		this.a2lRoot.add(childNode);
		DynamicUtilTreeNode.createChildren(childNode, a2l.getListRecordLayout());
	}
	
	public final void addChildToFunction(DefaultMutableTreeNode function)
	{
		A2l a2l = (A2l) this.a2lRoot.getUserObject();
		DynamicUtilTreeNode.createChildren(function, a2l.getAdjustableObjectByFunction(function.toString()));
	}

}
