package fr.mla.swt.smart.viewer.scroll;

import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public interface ScrollManager<T> {

	public void applyScroll(Rectangle clientArea, int hScroll, int vScroll, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList);

	public int next(Rectangle clientArea, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList);

	public int previous(Rectangle clientArea, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList);

	public int nextPage(Rectangle clientArea, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList);

	public int previousPage(Rectangle clientArea, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList);

	public int getStartModelIndex(Rectangle clientArea, int hScroll, int vScroll, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList);

	public void applyScrollFromIndex(int index, List<SmartViewerItem<T>> items, List<T> dataList);
}
