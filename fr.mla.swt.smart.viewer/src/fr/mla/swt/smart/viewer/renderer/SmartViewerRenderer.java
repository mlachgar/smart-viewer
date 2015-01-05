package fr.mla.swt.smart.viewer.renderer;

import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.ui.SmartViewerAction;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;
import fr.mla.swt.smart.viewer.ui.SmartViewer;

public interface SmartViewerRenderer {

	public void renderItems(GC gc, Rectangle paintBounds, SmartViewer viewer,
			List<SmartViewerItem> items);

	public void renderItem(GC gc, Rectangle paintBounds, SmartViewer viewer,
			SmartViewerItem item);

	public void dispose();

	public SmartViewerAction getActionAt(SmartViewer viewer,
			SmartViewerItem item, int x, int y);

	public Object getTooltipData(SmartViewer viewer, SmartViewerItem item, int x, int y);

}
