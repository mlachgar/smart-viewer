package fr.mla.swt.smart.viewer.scroll;

import java.util.List;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class GridScrollManager extends AbstractScrollManager {

	@Override
	public void applyScroll(ScrollViewport viewport, SmartViewerLayout layout, List<SmartViewerItem> items) {
		if (items.size() > 0) {
			int dx = 0;
			int dy = 0;
			SmartViewerItem firstItem = getFirstItem(viewport, items);
			if (viewport.hScroll > 0) {
				int offset = viewport.hScroll - firstItem.getAbsoluteX();
				if (offset < firstItem.getWidth() / 2) {
					dx = -offset;
				} else if (viewport.hScroll < firstItem.getAbsoluteX() + firstItem.getWidth()) {
					dy = firstItem.getAbsoluteX() + firstItem.getWidth() - viewport.hScroll;
				}
			}
			if (viewport.vScroll > firstItem.getAbsoluteY()) {
				int offset = viewport.vScroll - firstItem.getAbsoluteY();
				if (offset < firstItem.getHeight() / 2) {
					dy = -offset;
				} else if (viewport.vScroll < firstItem.getAbsoluteY() + firstItem.getHeight()) {
					dy = firstItem.getAbsoluteY() + firstItem.getHeight() - viewport.vScroll;
				}
			}

			for (SmartViewerItem item : items) {
				item.setLocation(item.getAbsoluteX() - viewport.hScroll - dx, item.getAbsoluteY() - viewport.vScroll
						- dy);
				for (SmartViewerItem child : item.getChildren()) {
					child.setLocation(child.getAbsoluteX() - viewport.hScroll - dx, child.getAbsoluteY()
							- viewport.vScroll - dy);
				}
			}
		}
	}

	@Override
	public int next(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout, List<SmartViewerItem> items) {
		if (items != null && !items.isEmpty()) {
			if (type == OrientationType.VERTICAL) {
				return items.get(0).getHeight() + layout.getSpacing().y;
			} else {
				return items.get(0).getWidth() + layout.getSpacing().x;
			}
		}
		return 0;
	}

	@Override
	public int nextPage(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout,
			List<SmartViewerItem> items) {
		if (items != null && !items.isEmpty()) {
			if (type == OrientationType.VERTICAL) {
				SmartViewerItem bottom = getBottomItem(viewport, items);
				if (bottom != null) {
					return bottom.getAbsoluteY() - viewport.vScroll - layout.getSpacing().y;
				}
			} else {
				SmartViewerItem right = getRightItem(viewport, items);
				if (right != null) {
					return right.getAbsoluteX() - viewport.hScroll - layout.getSpacing().x;
				}
			}
		}
		return 0;
	}

	public int computeScrollToMakeVisible(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout,
			SmartViewerItem item) {
		if (type == OrientationType.HORIZONTAL) {
			if (viewport.hScroll + viewport.clientArea.width < item.getAbsoluteX() + item.getWidth()) {
				return (item.getAbsoluteX() + item.getWidth()) - (viewport.hScroll + viewport.clientArea.width)
						+ item.getWidth();
			} else if (item.getAbsoluteX() < viewport.hScroll) {
				return item.getAbsoluteX() - layout.getSpacing().x;
			}
		} else {
			if (viewport.vScroll + viewport.clientArea.height < item.getAbsoluteY() + item.getHeight()) {
				return (item.getAbsoluteY() + item.getHeight()) - (viewport.vScroll + viewport.clientArea.height)
						+ item.getHeight();
			} else if (item.getAbsoluteY() < viewport.vScroll) {
				return item.getAbsoluteY() - viewport.vScroll - layout.getSpacing().y;
			}
		}
		return 0;
	}

}
