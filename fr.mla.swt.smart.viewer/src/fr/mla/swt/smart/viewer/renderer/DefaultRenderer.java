package fr.mla.swt.smart.viewer.renderer;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;

import fr.mla.swt.smart.viewer.ui.SmartViewer;
import fr.mla.swt.smart.viewer.ui.SmartViewerAction;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class DefaultRenderer implements SmartViewerRenderer {

	protected Color background;

	public DefaultRenderer() {

	}

	@Override
	public void dispose() {

	}

	public void setBackground(Color background) {
		this.background = background;
	}

	@Override
	public void renderItem(GC gc, Rectangle paintBounds, SmartViewer viewer,
			SmartViewerItem item) {
		if (item.getData() != null) {
			TextLayout l = new TextLayout(gc.getDevice());
			try {
				int startX = item.getX() - paintBounds.x;
				int startY = item.getY() - paintBounds.y;
				gc.drawRectangle(startX, startY, item.getWidth(),
						item.getHeight());
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
	public void renderItems(GC gc, Rectangle paintBounds, SmartViewer viewer,
			List<SmartViewerItem> items) {
		if (background == null) {
			background = gc.getDevice().getSystemColor(SWT.COLOR_BLACK);
		}
		Rectangle drawArea = viewer.getDrawArea();
		gc.setBackground(background);
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.fillRectangle(drawArea.x, drawArea.y, drawArea.width,
				drawArea.height);
		for (SmartViewerItem item : items) {
			if (paintBounds.intersects(item.getX(), item.getY(),
					item.getWidth(), item.getHeight())) {
				renderItem(gc, paintBounds, viewer, item);
			}
		}
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		gc.drawRectangle(drawArea.x - paintBounds.x,
				drawArea.y - paintBounds.y, drawArea.width, drawArea.height);
	}

	@Override
	public SmartViewerAction getActionAt(SmartViewer viewer,
			SmartViewerItem item, int x, int y) {
		List<SmartViewerAction> actions = item.getToolbarActions();
		if (actions != null) {
			for (SmartViewerAction action : actions) {
				if (action.isVisible()) {
					Rectangle bounds = (Rectangle) item.getExtraData(action);
					if (bounds != null && bounds.contains(x, y)) {
						return action;
					}
				}
			}
		}
		return null;
	}

	protected void putTooltipData(SmartViewerItem item, Object data,
			Rectangle bounds) {
		item.putExtraData(bounds, data);
	}

	@Override
	public Object getTooltipData(SmartViewer viewer, SmartViewerItem item,
			int x, int y) {
		for (Object key : item.getExtraDataKeys()) {
			if (key instanceof Rectangle) {
				Rectangle bounds = (Rectangle) key;
				if (bounds.contains(x, y)) {
					return item.getExtraData(key);
				}
			}
		}
		return null;
	}

}
