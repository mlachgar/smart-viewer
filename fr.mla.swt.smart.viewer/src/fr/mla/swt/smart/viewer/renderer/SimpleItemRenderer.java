package fr.mla.swt.smart.viewer.renderer;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public interface SimpleItemRenderer<T> {

	public void renderItem(GC gc, Rectangle paintBounds, Point scroll, SmartViewerItem<T> item);

}
