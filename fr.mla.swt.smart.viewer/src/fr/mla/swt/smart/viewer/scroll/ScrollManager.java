package fr.mla.swt.smart.viewer.scroll;

import java.util.List;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public interface ScrollManager {

	public void applyScroll(ScrollViewport viewport, SmartViewerLayout layout, List<SmartViewerItem> itemsa);

	public int next(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout, List<SmartViewerItem> items);

	public int previous(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout,
			List<SmartViewerItem> items);

	public int nextPage(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout,
			List<SmartViewerItem> items);

	public int previousPage(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout,
			List<SmartViewerItem> items);

	public int computeScrollToMakeVisible(ScrollViewport viewport, OrientationType type, SmartViewerLayout layout,
			SmartViewerItem item);

}
