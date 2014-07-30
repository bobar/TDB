package main;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class DefaultTableHeaderCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public DefaultTableHeaderCellRenderer() {
		setHorizontalAlignment(CENTER);
		setHorizontalTextPosition(LEFT);
		setVerticalAlignment(BOTTOM);
		setOpaque(false);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		JTableHeader tableHeader = table.getTableHeader();
		if (tableHeader != null) {
			setForeground(tableHeader.getForeground());
		}
		// setIcon(getIcon(table, column));
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		return this;
	}

	/*
	 * protected Icon getIcon(JTable table, int column) { SortKey sortKey =
	 * getSortKey(table, column); if (sortKey != null &&
	 * table.convertColumnIndexToView(sortKey.getColumn()) == column) { switch
	 * (sortKey.getSortOrder()) { case ASCENDING: return
	 * UIManager.getIcon("Table.ascendingSortIcon"); case DESCENDING: return
	 * UIManager.getIcon("Table.descendingSortIcon"); default: break; } } return
	 * null; } protected SortKey getSortKey(JTable table, int column) {
	 * RowSorter<?> rowSorter = table.getRowSorter(); if (rowSorter == null) {
	 * return null; } List<?> sortedColumns = rowSorter.getSortKeys(); if
	 * (sortedColumns.size() > 0) { return (SortKey) sortedColumns.get(0); }
	 * return null; }
	 */
}
