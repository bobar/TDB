package main;

public class VerticalTableHeaderCellRenderer extends DefaultTableHeaderCellRenderer {

  private static final long serialVersionUID = 1L;

  public VerticalTableHeaderCellRenderer() {
    setHorizontalAlignment(LEFT);
    setHorizontalTextPosition(CENTER);
    setVerticalAlignment(CENTER);
    setVerticalTextPosition(TOP);
    setUI(new VerticalLabelUI());
  }

  /*
   * protected Icon getIcon(JTable table, int column) { SortKey sortKey = getSortKey(table, column);
   * if (sortKey != null && table.convertColumnIndexToView(sortKey.getColumn()) == column) {
   * SortOrder sortOrder = sortKey.getSortOrder(); switch (sortOrder) { case ASCENDING: return
   * VerticalSortIcon.ASCENDING; case DESCENDING: return VerticalSortIcon.DESCENDING; default:
   * break; } } return null; }
   */

  /*
   * private enum VerticalSortIcon implements Icon {
   * ASCENDING(UIManager.getIcon("Table.ascendingSortIcon")), DESCENDING(UIManager
   * .getIcon("Table.descendingSortIcon")); private final Icon icon;// = ; private
   * VerticalSortIcon(Icon icon) { this.icon = icon; } public void paintIcon(Component c, Graphics
   * g, int x, int y) { int maxSide = Math.max(getIconWidth(), getIconHeight()); Graphics2D g2 =
   * (Graphics2D) g.create(x, y, maxSide, maxSide); g2.rotate((Math.PI / 2)); g2.translate(0,
   * -maxSide); icon.paintIcon(c, g2, 0, 0); g2.dispose(); } public int getIconWidth() { return
   * icon.getIconHeight(); } public int getIconHeight() { return icon.getIconWidth(); } }
   */
}
