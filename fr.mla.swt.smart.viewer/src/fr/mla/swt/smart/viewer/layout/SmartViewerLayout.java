package fr.mla.swt.smart.viewer.layout;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.ui.SmartViewerItem;
import fr.mla.swt.smart.viewer.model.DirectionType;
import fr.mla.swt.smart.viewer.model.OrientationType;

public interface SmartViewerLayout<T> {

	public void layoutItems(int width, int height, List<SmartViewerItem<T>> items);

	public Point getNeededSize(int width, int height, List<SmartViewerItem<T>> items);

	public int itemAt(Rectangle bounds, int x, int y, List<SmartViewerItem<T>> items);

	public Point getSpacing();

	public boolean isNavigable(OrientationType type);

	public Point getPreferredSize(int width, int height, List<SmartViewerItem<T>> items);

	public int getNeighborItem(int index, DirectionType type, List<SmartViewerItem<T>> items);
}
