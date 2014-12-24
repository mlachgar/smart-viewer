package fr.mla.swt.smart.viewer.scroll;

import java.util.Collection;
import java.util.List;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public interface ScrollManager<T> {

	public void applyScroll(ScrollViewport viewport, SmartViewerLayout<T> layout, List<SmartViewerItem<T>> items,
			Collection<T> selectedData);

	public int next(ScrollViewport viewport, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items);

	public int previous(ScrollViewport viewport, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items);

	public int nextPage(ScrollViewport viewport, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items);

	public int previousPage(ScrollViewport viewport, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items);

}
