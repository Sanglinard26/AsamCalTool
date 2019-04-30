package gui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import a2l.TableModelView;

public final class TableView extends JTable {

	private static final long serialVersionUID = 1L;

	public TableView(TableModelView model) {
		super(model);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setTableHeader(null);
		setDefaultRenderer(Object.class, new TableRenderer());
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.setCellSelectionEnabled(true);
	}

	@Override
	public TableModelView getModel() {
		return (TableModelView) super.getModel();
	}

	public static final void adjustCells(JTable table) {

		final TableColumnModel columnModel = table.getColumnModel();
		final int nbCol = columnModel.getColumnCount();
		final int nbRow = table.getRowCount();
		int maxWidth;
		TableCellRenderer cellRenderer;
		Object value;
		Component component;
		TableColumn column;

		for (short col = 0; col < nbCol; col++) {
			maxWidth = 0;
			for (short row = 0; row < nbRow; row++) {
				cellRenderer = table.getCellRenderer(row, col);
				value = table.getValueAt(row, col);
				component = cellRenderer.getTableCellRendererComponent(table, value, false, false, row, col);
				maxWidth = Math.max(component.getPreferredSize().width, maxWidth);
			}
			column = columnModel.getColumn(col);
			column.setPreferredWidth(maxWidth + 10);
		}
	}

	private final class TableRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1,
				boolean paramBoolean2, int paramInt1, int paramInt2) {
			super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1,
					paramInt2);
			setHorizontalAlignment(SwingConstants.CENTER);
			return this;
		}
	}
}
