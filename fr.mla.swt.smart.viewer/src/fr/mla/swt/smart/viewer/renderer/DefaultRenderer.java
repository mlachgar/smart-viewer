package fr.mla.swt.smart.viewer.renderer;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;

import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class DefaultRenderer implements SmartViewerRenderer {

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
	public void renderItem(GC gc, Rectangle paintBounds, Point scroll, SmartViewerItem item) {
		if (item.getData() != null) {
			TextLayout l = new TextLayout(gc.getDevice());
			try {
				int startX = item.getX() - paintBounds.x;
				int startY = item.getY() - paintBounds.y;
				gc.drawRectangle(startX, startY, item.getWidth(), item.getHeight());
				l.setAlignment(SWT.CENTER);
				l.setWidth(item.getWidth());
				l.setText(String.valueOf(item.getData()));
				l.draw(gc, startX + 5, startY + 5);
			} finally {
				l.dispose();
			}
		}
	}

	@Override
	public void renderItems(GC gc, Rectangle canvasBounds, Rectangle paintBounds, Point scroll,
			List<SmartViewerItem> items) {
		if (background == null) {
			background = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		}
		gc.setBackground(background);
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.fillRectangle(paintBounds);
		for (SmartViewerItem item : items) {
			if (paintBounds.intersects(item.getX(), item.getY(), item.getWidth(), item.getHeight())) {
				renderItem(gc, paintBounds, scroll, item);
			}
		}
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		gc.drawRectangle(canvasBounds.x, canvasBounds.y, canvasBounds.width - 1, canvasBounds.height - 1);
	}

}
