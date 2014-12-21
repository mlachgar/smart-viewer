package fr.mla.swt.smart.viewer.renderer;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public abstract class DefaultRenderer<T> implements SmartViewerRenderer<T>, SimpleItemRenderer<T> {

	private Color background;

	public DefaultRenderer() {

	}

	@Override
	public void dispose() {

	}

	public void setBackground(Color background) {
		this.background = background;
	}

	@Override
	public void renderItems(GC gc, Rectangle paintBounds, Point scroll, List<SmartViewerItem<T>> items) {
		if (background == null) {
			background = gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY);
		}
		gc.setBackground(background);
		gc.fillRectangle(paintBounds);
		for (SmartViewerItem<T> item : items) {
			if (paintBounds.intersects(item.getX(), item.getY(), item.getWidth(), item.getHeight())) {
				renderItem(gc, paintBounds, scroll, item);
			}
		}
	}
}
