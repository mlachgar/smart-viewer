package fr.mla.swt.smart.viewer.scroll;

import java.util.Collection;
import java.util.List;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class GridScrollManager<T> extends AbstractScrollManager<T> {

	@Override
	public void applyScroll(ScrollViewport viewport, SmartViewerLayout<T> layout, List<SmartViewerItem<T>> items,
			Collection<T> selectedData) {
		if (items.size() > 0) {
			int dx = 0;
			int dy = 0;
			SmartViewerItem<T> firstItem = getFirstItem(viewport, items);
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
			for (SmartViewerItem<T> item : items) {
				item.setLocation(item.getAbsoluteX() - viewport.hScroll - dx, item.getAbsoluteY() - viewport.vScroll
						- dy);
				item.setSelected(selectedData.contains(item.getData()));
			}
		}
	}

	@Override
	public int next(ScrollViewport viewport, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items) {
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
	public int nextPage(ScrollViewport viewport, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items) {
		if (items != null && !items.isEmpty()) {
			if (type == OrientationType.VERTICAL) {
				SmartViewerItem<T> bottom = getBottomItem(viewport, items);
				if (bottom != null) {
					return bottom.getAbsoluteY() - viewport.vScroll - layout.getSpacing().y;
				}
			} else {
				SmartViewerItem<T> right = getRightItem(viewport, items);
				if (right != null) {
					return right.getAbsoluteX() - viewport.hScroll - layout.getSpacing().x;
				}
			}
		}
		return 0;
	}

}
