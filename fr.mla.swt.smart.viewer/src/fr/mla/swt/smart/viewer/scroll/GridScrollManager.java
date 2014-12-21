package fr.mla.swt.smart.viewer.scroll;

import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class GridScrollManager<T> implements ScrollManager<T> {

	@Override
	public void applyScroll(Rectangle clientArea, int hScroll, int vScroll, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList) {
		if (dataList.size() > 0) {
			applyScrollFromIndex(getStartModelIndex(clientArea, hScroll, vScroll, layout, items, dataList), items,
					dataList);
		}
	}

	@Override
	public int getStartModelIndex(Rectangle clientArea, int hScroll, int vScroll, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList) {
		int index = 0;
		if (hScroll != 0 || vScroll != 0) {
			index = layout.itemAt(clientArea, hScroll, vScroll, dataList);
		}
		return index;
	}

	@Override
	public int next(Rectangle clientArea, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList) {
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
	public int previous(Rectangle clientArea, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList) {
		return -next(clientArea, type, layout, items, dataList);
	}

	@Override
	public int nextPage(Rectangle clientArea, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList) {
		if (items != null && !items.isEmpty()) {
			SmartViewerItem<T> firsttItem = items.get(0);
			SmartViewerItem<T> lastItem = items.get(items.size() - 1);
			if (type == OrientationType.VERTICAL) {
				int full = lastItem.getY() + lastItem.getHeight() - firsttItem.getY();
				if (full > clientArea.height) {
					return full - lastItem.getHeight();
				}
				return full;
			} else {
				int full = lastItem.getX() + lastItem.getWidth() - firsttItem.getX();
				if (full > clientArea.width) {
					return full - lastItem.getWidth();
				}
				return full;
			}
		}
		return 0;
	}

	@Override
	public int previousPage(Rectangle clientArea, OrientationType type, SmartViewerLayout<T> layout,
			List<SmartViewerItem<T>> items, List<T> dataList) {
		return -nextPage(clientArea, type, layout, items, dataList);
	}

	@Override
	public void applyScrollFromIndex(int index, List<SmartViewerItem<T>> items, List<T> dataList) {
		if (index >= 0 && dataList.size() > 0) {
			for (SmartViewerItem<T> item : items) {
				if (index < dataList.size()) {
					item.setData(dataList.get(index), index);
				} else {
					item.setData(null, -1);
				}
				index++;
			}
		}
	}

}