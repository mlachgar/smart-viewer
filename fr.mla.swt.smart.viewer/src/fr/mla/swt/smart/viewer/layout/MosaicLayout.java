package fr.mla.swt.smart.viewer.layout;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.model.DirectionType;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class MosaicLayout<T> implements SmartViewerLayout<T> {

	private int DEFAULT_WIDTH = 160;
	private int DEFAULT_HEIGHT = 160;
	private int V_SPACING = 3;
	private int H_SPACING = 3;
	private int itemWidth = DEFAULT_WIDTH;
	private int itemHeight = DEFAULT_HEIGHT;
	private int columnsCount;
	private final Point spacing = new Point(3, 3);
	protected final Point size = new Point(0, 0);

	@Override
	public void layoutItems(int width, int height, List<SmartViewerItem<T>> items) {
		int maxWidth = width - 2 * H_SPACING;
		columnsCount = maxWidth / (itemWidth + H_SPACING);
		int x = H_SPACING;
		int y = V_SPACING;
		size.x = x;
		for (int i = 0; i < items.size(); i++) {
			SmartViewerItem<T> item = items.get(i);
			// if (item.isVisible()) {
			if (x + itemWidth > maxWidth) {
				y += itemHeight + V_SPACING;
				x = H_SPACING;
				size.y = y + itemHeight + V_SPACING;
			}
			item.setBounds(x, y, itemWidth, itemHeight);
			item.setAbsoluteLocation(x, y);
			x += itemWidth + H_SPACING;
			size.x = Math.max(size.x, x);
			// }
		}
	}

	@Override
	public Point getNeededSize(int width, int height, List<SmartViewerItem<T>> items) {
		int maxWidth = width - 2 * H_SPACING;
		int columns = maxWidth / (itemWidth + H_SPACING);
		if (columns > 0) {
			int rows = items.size() / columns;
			if (items.size() % columns != 0) {
				rows++;
			}
			rows++;
			return new Point(width, V_SPACING + rows * (itemHeight + V_SPACING));
		}
		return new Point(width, height);
	}

	public void setItemSize(int width, int height) {
		this.itemWidth = width;
		this.itemHeight = height;
	}

	public int getColumnsCount() {
		return columnsCount;
	}

	public int computeColumns(Rectangle b) {
		int maxWidth = b.width - 2 * H_SPACING;
		return maxWidth / (itemWidth + H_SPACING);
	}

	@Override
	public int itemAt(Rectangle bounds, int x, int y, List<SmartViewerItem<T>> items) {
		int maxWidth = bounds.width - 2 * H_SPACING;
		int columns = maxWidth / (itemWidth + H_SPACING);
		int col = (x) / (itemWidth + H_SPACING);
		int row = (y) / (itemHeight + V_SPACING);
		return columns * row + col;
	}

	@Override
	public Point getSpacing() {
		return spacing;
	}

	@Override
	public boolean isNavigable(OrientationType type) {
		return true;
	}

	@Override
	public Point getPreferredSize(int width, int height, List<SmartViewerItem<T>> items) {
		int w = SWT.DEFAULT;
		int h = SWT.DEFAULT;
		return new Point(w, h);
	}
	
	@Override
	public int getNeighborItem(int index, DirectionType type, List<SmartViewerItem<T>> items) {
		return -1;
	}
}
