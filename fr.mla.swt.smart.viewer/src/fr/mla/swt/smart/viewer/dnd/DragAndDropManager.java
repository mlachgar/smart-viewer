package fr.mla.swt.smart.viewer.dnd;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import fr.mla.swt.smart.viewer.renderer.SmartViewerRenderer;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class DragAndDropManager {

	private Font dragCountFont;

	public boolean canDrag(DragSourceEvent event, Collection<SmartViewerItem> items) {
		return true;
	}

	public boolean canDrop(DropTargetEvent event, Collection<SmartViewerItem> items, boolean isInternal) {
		return true;
	}

	public boolean isDragEnabled() {
		return true;
	}

	public boolean isDropEnabled() {
		return true;
	}

	public int getDragOperations() {
		return DND.DROP_MOVE;
	}

	public Transfer[] getDragTransfers(DragSourceEvent event, Collection<SmartViewerItem> items) {
		return new Transfer[] { TextTransfer.getInstance() };
	}

	public int getDropOperations() {
		return DND.DROP_MOVE;
	}

	public Transfer[] getDropTransfers() {
		return new Transfer[] { TextTransfer.getInstance() };
	}

	public Image getDragImage(Display display, SmartViewerRenderer renderer, List<SmartViewerItem> items) {
		if (!items.isEmpty()) {
			int shift = 6;
			int margin = 2;
			int maxDepth = Math.min(items.size(), 10);
			SmartViewerItem firstItem = items.get(0);
			int width = firstItem.getWidth() + (shift * (maxDepth - 1)) + 2 * margin;
			int height = firstItem.getHeight() + (shift * (maxDepth - 1)) + 2 * margin;

			Image dragImage = new Image(display, width, height);
			final GC gc = new GC(dragImage);
			try {
				gc.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
				gc.fillRectangle(0, 0, width, height);
				gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				int dxy = 0;
				Rectangle paintBounds = new Rectangle(0, 0, width, height);
				for (int i = maxDepth - 1; i >= 0; i--) {
					SmartViewerItem item = items.get(i);
					boolean selected = item.isSelected();
					item.setSelected(false);
					paintBounds.x = item.getX() - margin - dxy;
					paintBounds.y = item.getY() - margin - dxy;
					renderer.renderItem(gc, paintBounds, null, item);
					item.setSelected(selected);
					gc.drawRoundRectangle(margin + dxy, margin + dxy, item.getWidth(), item.getHeight(), 10, 10);
					dxy += shift;
				}
				if (items.size() > 1) {
					gc.setAlpha(255);
					gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
					String count = String.valueOf(items.size());
					Point countSize = gc.textExtent(count);
					int countWidth = countSize.x + 4;
					int countHeight = countSize.y + 4;
					int x = ((maxDepth - 1) * shift) + margin + (firstItem.getWidth() - countWidth) / 2;
					int y = ((maxDepth - 1) * shift) + margin + (firstItem.getHeight() - countHeight) / 2;
					if (dragCountFont == null) {
						dragCountFont = new Font(display, "Arial", 18, SWT.BOLD);
					}
					gc.setFont(dragCountFont);
					gc.fillRoundRectangle(x, y, countWidth, countHeight, 10, 10);
					gc.drawRoundRectangle(x, y, countWidth, countHeight, 10, 10);
					gc.drawText(count, x + 2, y + 2);
				}
				return dragImage;
			} finally {
				gc.dispose();
			}
		}
		return null;
	}

	public Object getDragData(Collection<SmartViewerItem> items) {
		StringBuilder sb = new StringBuilder();
		for (SmartViewerItem item : items) {
			sb.append(String.valueOf(item.getData()));
			sb.append('\n');
		}
		return sb.toString();
	}

	public void performDrop(DropTargetEvent event) {

	}

	public void performInternalDrop(DropTargetEvent event, SmartViewerItem targetItem, Collection<SmartViewerItem> items) {

	}

	public void performExternalDrop(DropTargetEvent event, SmartViewerItem targetItem) {

	}

	public void dispose() {
		if (dragCountFont != null) {
			dragCountFont.dispose();
		}
	}

}
