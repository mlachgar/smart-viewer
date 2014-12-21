package fr.mla.swt.smart.viewer.layout;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public interface SmartViewerLayout<T> {

	public void layoutItems(int width, int height, List<SmartViewerItem<T>> items);

	public Point getNeededSize(int width, int height, List<T> items);

	public boolean isBoundsFilled(Rectangle bounds, List<T> items);

	public int itemAt(Rectangle bounds, int x, int y, List<T> items);

	public Point getSpacing();

	public Point getPreferredSize(List<T> items);

	int getMaxItemsCount(int width, int height, List<T> items);
}
