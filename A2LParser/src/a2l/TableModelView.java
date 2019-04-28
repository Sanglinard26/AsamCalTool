package a2l;

import javax.swing.table.AbstractTableModel;

public final class TableModelView extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String EMPTY = "";
    private int nbCol = 0;
    private Values values;

    @Override
    public String getColumnName(int column) {
        return EMPTY;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getColumnCount() {
        return nbCol;
    }

    @Override
    public int getRowCount() {
        return values!=null ? values.getDimY() : 0;
    }

    @Override
    public Object getValueAt(int row, int col) {

        final Object value = values.getValue(row, col);

        return value != null ? value : EMPTY;
    }

    public final void setData(Values data) {
        this.values = data;
        this.nbCol = values != null ? values.getDimX() : 0;

        fireTableStructureChanged();
    }
}
