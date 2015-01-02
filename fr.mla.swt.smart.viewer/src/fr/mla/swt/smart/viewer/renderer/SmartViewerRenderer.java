package fr.mla.swt.smart.viewer.renderer;

import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public interface SmartViewerRenderer {

	public void renderItems(GC gc, Rectangle canvasBounds, Rectangle paintBounds, Point scroll,
			List<SmartViewerItem> items);

	public void renderItem(GC gc, Rectangle paintBounds, Point scroll, SmartViewerItem item);

	public void dispose();

}
