package fr.mla.swt.smart.viewer.scroll;

import java.util.List;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class DefaultScrollManager extends AbstractScrollManager {

	@Override
	public void applyScroll(ScrollViewport viewport, SmartViewerLayout layout, List<SmartViewerItem> items) {
		for (SmartViewerItem item : items) {
			item.setLocation(item.getAbsoluteX() - viewport.hScroll, item.getAbsoluteY() - viewport.vScroll);
			applyScroll(viewport, layout, item.getChildren());
		}
	}

	@Override
	public int next(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout, List<SmartViewerItem> items) {
		SmartViewerItem item = getFirstItem(viewport, items);
		if (item != null) {
			if (type == OrientationType.VERTICAL) {
				return item.getHeight() + layout.getSpacing().y;
			} else {
				return item.getWidth() + layout.getSpacing().x;
			}
		}
		return 0;
	}

	@Override
	public int nextPage(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout,
			List<SmartViewerItem> items) {
		if (items != null && !items.isEmpty()) {
			if (type == OrientationType.VERTICAL) {
				return viewport.clientArea.height;
			} else {
				return viewport.clientArea.width;
			}
		}
		return 0;
	}

	@Override
	public int computeScrollToMakeVisible(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout,
			SmartViewerItem item) {
		if (type == OrientationType.HORIZONTAL) {
			if (viewport.hScroll + viewport.clientArea.width < item.getAbsoluteX() + item.getWidth()) {
				return (item.getAbsoluteX() + item.getWidth()) - (viewport.hScroll + viewport.clientArea.width)
						+ layout.getSpacing().x;
			} else if (item.getAbsoluteX() < viewport.hScroll) {
				return item.getAbsoluteX() - viewport.hScroll - layout.getSpacing().x;
			}
		} else {
			if (viewport.vScroll + viewport.clientArea.height < item.getAbsoluteY() + item.getHeight()) {
				return (item.getAbsoluteY() + item.getHeight()) - (viewport.vScroll + viewport.clientArea.height)
						+ layout.getSpacing().y;
			} else if (item.getAbsoluteY() < viewport.vScroll) {
				return item.getAbsoluteY() - viewport.vScroll - layout.getSpacing().y;
			}
		}
		return 0;
	}

}
