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

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import a2l.A2l;
import a2l.AdjustableObject;
import a2l.Function;
import a2l.TableModelView;
import hex.HexDecoder;
import hex.IntelHex;

public final class Ihm extends JFrame {

	private static final long serialVersionUID = 1L;

	private final Container container;
	private FilteredTree filteredTree;
	private JTree a2lTree;
	private final PanelView panelView;

	private A2l a2l;

	public Ihm() {
		super("AsamCalTool");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(new PanelBt(), BorderLayout.NORTH);
		
		panelView = new PanelView();
		container.add(new JScrollPane(panelView), BorderLayout.CENTER);

		pack();

		setVisible(true);
	}

	private final class PanelBt extends JPanel {

		private static final long serialVersionUID = 1L;
		private JButton btOpenA2L;
		private JButton btOpenHex;
		private JLabel labelHex;

		private final StringBuilder sb = new StringBuilder();

		public PanelBt() {
			super();
			((FlowLayout) getLayout()).setAlignment(FlowLayout.LEFT);

			labelHex = new JLabel("Hex file : ...");

			btOpenA2L = new JButton(new AbstractAction("Open A2L") {

				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser("C:\\User\\U354706\\Perso\\WorkInProgress");
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

						long start = System.currentTimeMillis();

						a2l = new A2l(chooser.getSelectedFile());

						sb.append("A2L parsing time : " + (System.currentTimeMillis() - start) + "ms\n");

						filteredTree = new FilteredTree(a2l);

						a2lTree = filteredTree.getTree();
						a2lTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
						a2lTree.addTreeSelectionListener(new TreeSelectionListener() {

							@Override
							public void valueChanged(TreeSelectionEvent treeEvent) {

								DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) a2lTree.getLastSelectedPathComponent();

								if (selectedNode != null) {
									Object userObject = selectedNode.getUserObject();
									if (userObject instanceof AdjustableObject) {
										panelView.displayValues((AdjustableObject) userObject);
									} else if (userObject instanceof Function) {
										filteredTree.addChildToFunction(selectedNode);
									}
								}
							}
						});

						container.add(filteredTree, BorderLayout.WEST);

						container.revalidate();
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
							return "Hex files (*.hex)";
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

						labelHex.setText("Hex file : ...");

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

						if (hexDecoder.readDataFromHex()) {
							lEndTime = System.nanoTime();
							output = lEndTime - lStartTime;
							labelHex.setText("<html>Hex file : " + "<b>" + chooser.getSelectedFile().getName() + "</b></html>");
							sb.append("Reading hex data : " + output / 1000000 + "ms\n");
							JOptionPane.showMessageDialog(Ihm.this, sb.toString());

						} else {
							JOptionPane.showMessageDialog(null, "EEPROM identifier doesn't match, reading aborted.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}

						sb.setLength(0);
					}

				}
			});
			add(btOpenHex);

			add(labelHex);
		}
	}
	
	private final class PanelView extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		private final TableView tableView;
		private final JTextPane textPane;
		
		public PanelView() {
			
			setLayout(new BorderLayout());
			
			textPane = new JTextPane();
			textPane.setBorder(BorderFactory.createLoweredSoftBevelBorder());
			textPane.setPreferredSize(new Dimension(1200, 300));
			add(textPane, BorderLayout.NORTH);
			
			tableView = new TableView(new TableModelView());
			tableView.setPreferredSize(new Dimension(1200, 500));
			
			JPanel panTable = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panTable.add(tableView);
			add(panTable, BorderLayout.CENTER);
		}
		
		public final void displayValues(AdjustableObject adjustableObject)
		{
			tableView.getModel().setData(adjustableObject.getValues());
			TableView.adjustCells(tableView);
			tableView.updateUI();
		}
	}

}
