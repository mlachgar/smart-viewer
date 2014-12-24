package fr.mla.swt.smart.viewer.scroll;

import java.util.List;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public abstract class AbstractScrollManager<T> implements ScrollManager<T> {

	@Override
	public int previous(ScrollViewport viewport, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items) {
		return -next(viewport, type, layout, items);
	}

	@Override
	public int previousPage(ScrollViewport viewport, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items) {
		return -nextPage(viewport, type, layout, items);
	}

	protected SmartViewerItem<T> getFirstItem(ScrollViewport viewport, List<SmartViewerItem<T>> items) {
		if (!items.isEmpty()) {
			int x = viewport.hScroll != -1 ? viewport.hScroll : 0;
			int y = viewport.vScroll != -1 ? viewport.vScroll : 0;
			for (final SmartViewerItem<T> item : items) {
				if (item.intersects(x, y, viewport.clientArea.width, viewport.clientArea.height)) {
					return item;
				}
			}
			return items.get(0);
		}
		return null;
	}

	protected SmartViewerItem<T> getTopItem(ScrollViewport viewport, List<SmartViewerItem<T>> items) {
		if (!items.isEmpty()) {
			if (viewport.vScroll == -1) {
				return items.get(0);
			}
			for (final SmartViewerItem<T> item : items) {
				if (item.getAbsoluteY() + item.getHeight() > viewport.vScroll) {
					return item;
				}
			}
		}
		return null;
	}

	protected SmartViewerItem<T> getBottomItem(ScrollViewport viewport, List<SmartViewerItem<T>> items) {
		if (!items.isEmpty()) {
			if (viewport.vScroll == -1) {
				return items.get(items.size() - 1);
			}
			final int endY = viewport.vScroll + viewport.clientArea.height;
			SmartViewerItem<T> found = null;
			for (final SmartViewerItem<T> item : items) {
				if (item.getAbsoluteY() > viewport.vScroll) {
					if (item.getAbsoluteY() <= endY) {
						found = item;
					} else {
						break;
					}
				}
			}
			return found;
		}
		return null;
	}

	protected SmartViewerItem<T> getLeftItem(ScrollViewport viewport, List<SmartViewerItem<T>> items) {
		if (!items.isEmpty()) {
			if (viewport.hScroll == -1) {
				return items.get(0);
			}
			for (final SmartViewerItem<T> item : items) {
				if (item.getAbsoluteX() + item.getWidth() > viewport.hScroll) {
					return item;
				}
			}
		}
		return null;
	}

	protected SmartViewerItem<T> getRightItem(ScrollViewport viewport, List<SmartViewerItem<T>> items) {
		if (!items.isEmpty()) {
			if (viewport.hScroll == -1) {
				return items.get(items.size() - 1);
			}
			final int endX = viewport.hScroll + viewport.clientArea.width;
			SmartViewerItem<T> found = null;
			for (final SmartViewerItem<T> item : items) {
				if (item.getAbsoluteX() > viewport.hScroll) {
					if (item.getAbsoluteX() <= endX) {
						found = item;
					} else {
						break;
					}
				}
			}
			return found;
		}
		return null;
	}

}
