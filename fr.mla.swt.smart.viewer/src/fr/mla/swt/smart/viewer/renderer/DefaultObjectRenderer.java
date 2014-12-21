package fr.mla.swt.smart.viewer.renderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;

import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class DefaultObjectRenderer<T> extends DefaultRenderer<T> {

	@Override
	public void renderItem(GC gc, Rectangle paintBounds, Point scroll, SmartViewerItem<T> item) {
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

}