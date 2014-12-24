package fr.mla.swt.smart.viewer.scroll;

import java.util.Collection;
import java.util.List;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class DefaultScrollManager<T> extends AbstractScrollManager<T> {

	@Override
	public void applyScroll(ScrollViewport viewport, SmartViewerLayout<T> layout, List<SmartViewerItem<T>> items,
			Collection<T> selectedData) {
		for (SmartViewerItem<T> item : items) {
			item.setLocation(item.getAbsoluteX() - viewport.hScroll, item.getAbsoluteY() - viewport.vScroll);
			item.setSelected(selectedData.contains(item.getData()));
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
				return viewport.clientArea.height;
			} else {
				return viewport.clientArea.width;
			}
		}
		return 0;
	}

}
