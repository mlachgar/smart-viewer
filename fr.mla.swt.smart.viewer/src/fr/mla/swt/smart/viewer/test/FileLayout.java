package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.DirectionType;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.CompositeSmartViewerItem;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class FileLayout implements SmartViewerLayout<File> {

	private int V_SPACING = 3;
	private int H_SPACING = 3;
	private int rowsCount = 2;
	private int itemWidth = 120;
	private int itemHeight = 120;
	private final Point spacing = new Point(H_SPACING, V_SPACING);
	private OrientationType type;

	public FileLayout(OrientationType type) {
		this.type = type;
	}

	private Point computeItemSize(SmartViewerItem<File> item) {
		int width = (rowsCount * itemWidth) + (rowsCount + 1) * H_SPACING;
		int height = (rowsCount * itemHeight) + (rowsCount + 1) * V_SPACING;
		if (item instanceof CompositeSmartViewerItem) {
			CompositeSmartViewerItem<?, ?> cmp = (CompositeSmartViewerItem<?, ?>) item;
			int size = cmp.getChildren().size();
			if (size > 0) {
				int columns = size / rowsCount;
				if (size % rowsCount != 0) {
					columns++;
				}
				if (type == OrientationType.HORIZONTAL) {
					width = H_SPACING + columns * (itemWidth + H_SPACING);
				} else {
					height = V_SPACING + columns * (itemHeight + V_SPACING);
				}
			}
		}
		return new Point(width, height);
	}

	private Point computeItemsSize(List<SmartViewerItem<File>> items) {
		int width;
		int height;
		if (type == OrientationType.HORIZONTAL) {
			width = H_SPACING;
			height = itemHeight + 2 * V_SPACING;
			for (int i = 0; i < items.size(); i++) {
				SmartViewerItem<File> item = items.get(i);
				Point size = computeItemSize(item);
				width += size.x + H_SPACING;
			}
		} else {
			height = V_SPACING;
			width = itemWidth + 2 * H_SPACING;
			for (int i = 0; i < items.size(); i++) {
				SmartViewerItem<File> item = items.get(i);
				Point size = computeItemSize(item);
				height += size.y + V_SPACING;
			}
		}

		return new Point(width, height);
	}

	private void layoutChildren(SmartViewerItem<File> item, int x, int y) {
		if (item instanceof CompositeSmartViewerItem) {
			CompositeSmartViewerItem<File, File> cmp = (CompositeSmartViewerItem<File, File>) item;
			int childX = H_SPACING;
			int childY = V_SPACING;
			List<SmartViewerItem<File>> children = cmp.getChildren();
			for (int i = 0; i < children.size(); i++) {
				SmartViewerItem<?> child = children.get(i);
				child.setBounds(childX, childY, itemWidth, itemHeight);
				child.setAbsoluteLocation(childX + item.getAbsoluteX(), childY + item.getAbsoluteY());
				if ((i + 1) % 2 == 0) {
					if (type == OrientationType.HORIZONTAL) {
						childX += itemWidth + H_SPACING;
						childY = V_SPACING;
					} else {
						childY += itemHeight + V_SPACING;
						childX = H_SPACING;
					}
				} else {
					if (type == OrientationType.HORIZONTAL) {
						childY += itemHeight + V_SPACING;
					} else {
						childX += itemWidth + H_SPACING;
					}
				}
			}
		}
	}

	@Override
	public void layoutItems(int width, int height, List<SmartViewerItem<File>> items) {
		int x = H_SPACING;
		int y = V_SPACING;
		if (type == OrientationType.HORIZONTAL) {
			for (int i = 0; i < items.size(); i++) {
				SmartViewerItem<File> item = items.get(i);
				Point size = computeItemSize(item);
				item.setBounds(x, y, size.x, size.y);
				item.setAbsoluteLocation(x, y);
				layoutChildren(item, x, y);
				x += size.x + H_SPACING;
			}
		} else {
			for (int i = 0; i < items.size(); i++) {
				SmartViewerItem<File> item = items.get(i);
				Point size = computeItemSize(item);
				item.setBounds(x, y, size.x, size.y);
				item.setAbsoluteLocation(x, y);
				layoutChildren(item, x, y);
				y += size.y + V_SPACING;
			}
		}
	}

	@Override
	public Point getNeededSize(int width, int height, List<SmartViewerItem<File>> items) {
		if (!items.isEmpty()) {
			return computeItemsSize(items);
		}
		return new Point(width, height);
	}

	@Override
	public int itemAt(Rectangle bounds, int x, int y, List<SmartViewerItem<File>> items) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).contains(x, y)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Point getSpacing() {
		return spacing;
	}

	@Override
	public boolean isNavigable(OrientationType type) {
		return type == OrientationType.HORIZONTAL;
	}

	@Override
	public Point getPreferredSize(int width, int height, List<SmartViewerItem<File>> items) {
		if (type == OrientationType.HORIZONTAL) {
			return new Point(SWT.DEFAULT, (rowsCount * itemHeight) + (rowsCount + 1) * V_SPACING);
		}
		return new Point((rowsCount * itemWidth) + (rowsCount + 1) * H_SPACING, SWT.DEFAULT);
	}

	@Override
	public int getNeighborItem(int index, DirectionType type, List<SmartViewerItem<File>> items) {
		switch (type) {
		case LEFT:
		case UP:
			return index - 1;
		case RIGHT:
		case DOWN:
			return index + 1;
		}
		return -1;
	}

}
