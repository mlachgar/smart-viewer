package fr.mla.swt.smart.viewer.group;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.DirectionType;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class DataGroupLayout implements SmartViewerLayout {

	private int ITEM_SPACING = 5;
	private int GROUP_SPACING = 10;
	private int rowsCount = 2;
	private int itemWidth = 120;
	private int itemHeight = 120;
	private final Point spacing = new Point(ITEM_SPACING, ITEM_SPACING);
	private OrientationType type;
	private int expandedWidth;
	private int expandedHeight;
	private int maxStackCount = 5;

	public DataGroupLayout(OrientationType type) {
		this(type, 2);
	}

	public DataGroupLayout(OrientationType type, int rowsCount) {
		this.rowsCount = rowsCount;
		this.type = type;
		this.expandedWidth = (rowsCount * itemWidth) + (rowsCount + 1)
				* ITEM_SPACING;
		this.expandedHeight = (rowsCount * itemHeight) + (rowsCount + 1)
				* ITEM_SPACING;
	}

	private boolean isExpanded(SmartViewerItem item) {
		Object data = item.getData();
		if (data instanceof DataGroup) {
			DataGroup group = (DataGroup) data;
			return group.isExpanded();
		}
		return false;
	}

	private Point computeGroupSize(SmartViewerItem item) {
		return computeGroupSize(item.getChildren(), isExpanded(item));
	}

	private Point computeGroupSize(List<SmartViewerItem> items, boolean expanded) {
		int width = 0;
		int height = 0;
		if (expanded) {
			int size = items.size();
			if (size > 0) {
				int columns = size / rowsCount;
				if (size % rowsCount != 0) {
					columns++;
				}
				if (type == OrientationType.HORIZONTAL) {
					width = (columns * itemWidth)
							+ ((columns + 1) * ITEM_SPACING);
					height = expandedHeight;
				} else {
					width = expandedWidth;
					height = (columns * itemHeight)
							+ ((columns + 1) * ITEM_SPACING);
				}
			}
		} else {
			width = expandedWidth;
			height = expandedHeight;
		}
		return new Point(width + 16, height);
	}

	private Point computeItemsSize(List<SmartViewerItem> items) {
		int width;
		int height;
		if (type == OrientationType.HORIZONTAL) {
			width = GROUP_SPACING;
			height = itemHeight + 2 * GROUP_SPACING;
			for (SmartViewerItem item : items) {
				Point size = computeGroupSize(item);
				width += size.x + GROUP_SPACING;
			}
		} else {
			height = GROUP_SPACING;
			width = itemWidth + 2 * GROUP_SPACING;
			for (SmartViewerItem item : items) {
				Point size = computeGroupSize(item);
				height += size.y + GROUP_SPACING;
			}
		}
		return new Point(width, height);
	}

	private void layoutChildren(List<SmartViewerItem> children,
			boolean expanded, int parentX, int parentY) {
		int x = parentX + ITEM_SPACING;
		int y = parentY + ITEM_SPACING;
		int width = itemWidth;
		int height = itemHeight;
		int startStackIndex = (rowsCount * 2) - 1;
		int stackCount = Math.min(children.size() - startStackIndex,
				maxStackCount);
		for (int i = 0; i < children.size(); i++) {
			if (!expanded && stackCount > 1 && i >= startStackIndex) {
				width = itemWidth - (stackCount-1) * ITEM_SPACING;
				height = itemHeight - (stackCount-1) * ITEM_SPACING;
			}
			SmartViewerItem child = children.get(i);
			child.setBounds(x, y, width, height);
			child.setAbsoluteLocation(x, y);
			if (expanded || i < startStackIndex) {
				if ((i + 1) % 2 == 0) {
					if (type == OrientationType.HORIZONTAL) {
						x += itemWidth + ITEM_SPACING;
						y = parentY + ITEM_SPACING;
					} else {
						y += itemHeight + ITEM_SPACING;
						x = parentX + ITEM_SPACING;
					}
				} else {
					if (type == OrientationType.HORIZONTAL) {
						y += itemHeight + ITEM_SPACING;
					} else {
						x += itemWidth + ITEM_SPACING;
					}
				}
			} else {
				if (i < maxStackCount + startStackIndex) {
					x += ITEM_SPACING;
					y += ITEM_SPACING;
				}
			}
		}
	}

	@Override
	public void layoutItems(int width, int height, List<SmartViewerItem> items) {
		int x = GROUP_SPACING;
		int y = GROUP_SPACING;
		for (SmartViewerItem item : items) {
			boolean expanded = isExpanded(item);
			Point size = computeGroupSize(item.getChildren(), expanded);
			item.setBounds(x, y, size.x, size.y);
			item.setAbsoluteLocation(x, y);
			layoutChildren(item.getChildren(), expanded, x, y);
			if (type == OrientationType.HORIZONTAL) {
				x += size.x + GROUP_SPACING;
			} else {
				y += size.y + GROUP_SPACING;
			}
		}
	}

	@Override
	public int itemAt(Rectangle bounds, int x, int y,
			List<SmartViewerItem> items) {
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
	public Point getNeededSize(int width, int height,
			List<SmartViewerItem> items) {
		if (!items.isEmpty()) {
			return computeItemsSize(items);
		}
		return new Point(0, 0);
	}

	@Override
	public Point getPreferredSize(int width, int height,
			List<SmartViewerItem> items) {
		width = expandedWidth;
		height = expandedHeight;
		if (type == OrientationType.HORIZONTAL) {
			return new Point(SWT.DEFAULT, height + 2 * GROUP_SPACING);
		}
		return new Point(width + 2 * GROUP_SPACING, SWT.DEFAULT);
	}

	public int getNeighborItemIndex(int index, DirectionType type,
			List<SmartViewerItem> items) {
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

	@Override
	public SmartViewerItem getNeighborItem(SmartViewerItem item,
			DirectionType type, List<SmartViewerItem> items) {
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
