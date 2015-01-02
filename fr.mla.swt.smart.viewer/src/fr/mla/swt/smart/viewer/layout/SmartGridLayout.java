package fr.mla.swt.smart.viewer.layout;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.model.DirectionType;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class SmartGridLayout implements SmartViewerLayout {

	private static final String COL_DATA_KEY = "layout.grid.col";
	private static final String ROW_DATA_KEY = "layout.grid.row";
	private static final int DEFAULT_WIDTH = 160;
	private static final int DEFAULT_HEIGHT = 160;
	private int V_SPACING = 5;
	private int H_SPACING = 5;
	private int itemWidth = DEFAULT_WIDTH;
	private int itemHeight = DEFAULT_HEIGHT;
	private int columnsCount = -1;
	private int rowsCount = -1;
	private final Point spacing = new Point(3, 3);

	public SmartGridLayout() {
		this(-1, -1);
	}

	public SmartGridLayout(int columnsCount, int rowsCount) {
		this.columnsCount = columnsCount;
		this.rowsCount = rowsCount;
	}

	@Override
	public void layoutItems(int width, int height, List<SmartViewerItem> items) {
		int x = H_SPACING;
		int y = V_SPACING;
		int col = 1;
		int row = 1;
		Point gridSize = getGridSize(width, height, items);
		for (int i = 0; i < items.size(); i++) {
			SmartViewerItem item = items.get(i);
			item.setBounds(x, y, itemWidth, itemHeight);
			item.setAbsoluteLocation(x, y);
			item.putExtraData(COL_DATA_KEY, col);
			item.putExtraData(ROW_DATA_KEY, row);
			// if (item.isVisible()) {
			if (rowsCount > 0) {
				if (row == gridSize.y) {
					x += itemWidth + H_SPACING;
					y = V_SPACING;
					col++;
					row = 1;
				} else {
					y += itemHeight + V_SPACING;
					row++;
				}
			} else {
				if (col == gridSize.x) {
					y += itemHeight + V_SPACING;
					x = H_SPACING;
					row++;
					col = 1;
				} else {
					x += itemWidth + H_SPACING;
					col++;
				}
			}
		}
	}

	private Point getGridSize(int width, int height, List<SmartViewerItem> items) {
		int columns = columnsCount;
		int rows = rowsCount;
		if (columns > 0) {
			rows = items.size() / columns;
			if (items.size() % columns != 0) {
				rows++;
			}
		} else if (rows > 0) {
			columns = items.size() / rows;
			if (items.size() % rows != 0) {
				columns++;
			}
		} else {
			int maxWidth = width - 2 * H_SPACING;
			columns = maxWidth / (itemWidth + H_SPACING);
			if (columns > 0) {
				rows = items.size() / columns;
				if (items.size() % columns != 0) {
					rows++;
				}
			}
		}
		return new Point(columns, rows);
	}

	@Override
	public Point getNeededSize(int width, int height, List<SmartViewerItem> items) {
		Point gridSize = getGridSize(width, height, items);
		if (gridSize.x > 0 && gridSize.y > 0) {
			return new Point(H_SPACING + gridSize.x * (itemWidth + H_SPACING), V_SPACING + gridSize.y
					* (itemHeight + V_SPACING));
		} else {
			return new Point(width, height);
		}
	}

	@Override
	public Point getPreferredSize(int width, int height, List<SmartViewerItem> items) {
		int w = SWT.DEFAULT;
		int h = SWT.DEFAULT;
		if (columnsCount > 0) {
			w = H_SPACING + columnsCount * (itemWidth + H_SPACING);
		} else if (rowsCount > 0) {
			h = V_SPACING + rowsCount * (itemHeight + V_SPACING);
		}
		return new Point(w, h);
	}

	public void setItemSize(int width, int height) {
		this.itemWidth = width;
		this.itemHeight = height;
	}

	public int computeColumns(Rectangle b) {
		int maxWidth = b.width - 2 * H_SPACING;
		return maxWidth / (itemWidth + H_SPACING);
	}

	@Override
	public int itemAt(Rectangle bounds, int x, int y, List<SmartViewerItem> items) {
		Point gridSize = getGridSize(bounds.width, bounds.height, items);
		int col = (x) / (itemWidth + H_SPACING);
		int row = (y) / (itemHeight + V_SPACING);
		return gridSize.x * row + col;
	}

	@Override
	public Point getSpacing() {
		return spacing;
	}

	@Override
	public boolean isNavigable(OrientationType type) {
		return true;
	}

	private int findCell(int col, int row, List<SmartViewerItem> items) {
		if (col > 0 && row > 0) {
			for (int i = 0; i < items.size(); i++) {
				SmartViewerItem item = items.get(i);
				Integer c = (Integer) item.getExtraData(COL_DATA_KEY);
				Integer r = (Integer) item.getExtraData(ROW_DATA_KEY);
				if (c != null && r != null && c.intValue() == col && r.intValue() == row) {
					return i;
				}
			}
		}
		return -1;
	}

	private int nextColumn(int index, int col, int row, int offset, List<SmartViewerItem> items) {
		if (columnsCount <= 0 && rowsCount <= 0) {
			return index + offset;
		}
		col += offset;
		if (col < 1) {
			col = 1;
			row--;
		}
		if (columnsCount > 0 && col > columnsCount) {
			col = 1;
			row++;
		}
		return findCell(col, row, items);
	}

	private int nextRow(int col, int row, int offset, List<SmartViewerItem> items) {
		row += offset;
		if (row < 1 || (rowsCount > 0 && row > rowsCount)) {
			return -1;
		}
		// if (row < 1) {
		// row = 1;
		// col--;
		// }
		// if (rowsCount > 0 && row > rowsCount) {
		// row = 1;
		// col++;
		// }
		return findCell(col, row, items);
	}

	public int getNeighborItemIndex(int index, DirectionType type, List<SmartViewerItem> items) {
		SmartViewerItem item = items.get(index);
		Integer col = (Integer) item.getExtraData(COL_DATA_KEY);
		Integer row = (Integer) item.getExtraData(ROW_DATA_KEY);
		if (col != null && row != null) {
			switch (type) {
			case LEFT:
				return nextColumn(index, col, row, -1, items);
			case RIGHT:
				return nextColumn(index, col, row, 1, items);
			case UP:
				return nextRow(col, row, -1, items);
			case DOWN:
				return nextRow(col, row, 1, items);
			}
		}
		return -1;
	}

	@Override
	public SmartViewerItem getNeighborItem(SmartViewerItem item, DirectionType type, List<SmartViewerItem> items) {
		int index = items.indexOf(item);
		if (index != -1) {
			int neighborIndex = getNeighborItemIndex(index, type, items);
			if (neighborIndex >= 0 && neighborIndex < items.size()) {
				return items.get(neighborIndex);
			}
		}
		return null;
	}
}
