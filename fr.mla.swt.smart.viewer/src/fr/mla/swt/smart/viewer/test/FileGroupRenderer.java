package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import fr.mla.swt.smart.viewer.Activator;
import fr.mla.swt.smart.viewer.color.ColorCache;
import fr.mla.swt.smart.viewer.color.ColorDescriptor;
import fr.mla.swt.smart.viewer.group.DataGroup;
import fr.mla.swt.smart.viewer.group.GroupData;
import fr.mla.swt.smart.viewer.renderer.DefaultRenderer;
import fr.mla.swt.smart.viewer.ui.SmartViewer;
import fr.mla.swt.smart.viewer.ui.SmartViewerAction;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class FileGroupRenderer extends DefaultRenderer {

	private final ImageRegistry imageRegistry;
	private Display display;
	private ColorCache colorCache;

	public FileGroupRenderer(Display display) {
		this.display = display;
		imageRegistry = new ImageRegistry(display);
		colorCache = new ColorCache(display);
	}

	public void dispose() {
		imageRegistry.dispose();
		colorCache.dispose();
	}

	private void renderItemBorder(GC gc, Rectangle paintBounds, Color bgColor,
			SmartViewerItem item) {
		int width = item.getWidth();
		int height = item.getHeight();
		int x = item.getX() - paintBounds.x;
		int y = item.getY() - paintBounds.y;
		if (bgColor != null) {
			gc.setBackground(bgColor);
		}
		gc.fillRoundRectangle(x, y, width, height, 10, 10);
		if (item.isSelected()) {
			gc.setLineWidth(3);
			gc.setForeground(gc.getDevice().getSystemColor(
					SWT.COLOR_LIST_SELECTION));
			gc.drawRoundRectangle(x, y, width, height, 10, 10);
		} else {
			gc.setLineWidth(1);
			gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
			gc.drawRoundRectangle(x, y, width, height, 10, 10);
		}

	}

	private void renderNewGroupItem(GC gc, Rectangle paintBounds,
			SmartViewerItem item) {
		int width = item.getWidth();
		int height = item.getHeight();
		int x = item.getX() - paintBounds.x;
		int y = item.getY() - paintBounds.y;
		gc.setLineStyle(SWT.LINE_DASH);
		gc.setBackground(background);
		gc.fillRoundRectangle(x, y, width, height, 10, 10);
		gc.setLineWidth(1);
		gc.setLineDash(new int[] { 5 });
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		gc.drawRoundRectangle(x, y, width, height, 10, 10);
		Image image = Activator.getImage("plus_32.png");
		if (image != null) {
			Rectangle imageBounds = image.getBounds();
			x += (item.getWidth() - imageBounds.width) / 2;
			y += (item.getHeight() - imageBounds.height) / 2;
			gc.drawImage(image, x, y);
		}
	}

	private void renderItemContent(GC gc, Rectangle paintBounds,
			SmartViewerItem item) {
		int width = item.getWidth() - 5;
		int height = item.getHeight() - 5;
		int x = item.getX() - paintBounds.x + 2;
		int y = item.getY() - paintBounds.y + 2;
		Object data = item.getData();
		File file = null;
		if (data instanceof GroupData) {
			GroupData groupFile = (GroupData) data;
			file = (File) groupFile.getData();
		} else if (data instanceof File) {
			file = (File) data;
		}
		if (file != null) {
			if (file.isDirectory()) {
				renderDirectory(gc, file, x, y, width, height, false);
			} else {
				renderFile(gc, file, item.getIndex(), x, y, width, height,
						false);
			}
		}
	}

	@Override
	public void renderItem(GC gc, Rectangle paintBounds, SmartViewer viewer,
			SmartViewerItem item) {
		Object data = item.getData();
		item.clearExtraData();
		if (data instanceof NewGroupData) {
			renderNewGroupItem(gc, paintBounds, item);
		} else if (data instanceof DataGroup) {
			DataGroup group = (DataGroup) data;
			ColorDescriptor color = group.getColor();
			renderItemBorder(gc, paintBounds,
					colorCache.getColor(color.red, color.green, color.blue),
					item);
		} else {
			renderItemBorder(gc, paintBounds,
					gc.getDevice().getSystemColor(SWT.COLOR_BLACK), item);
		}
		List<SmartViewerItem> children = item.getChildren();
		if (children.isEmpty()) {
			renderItemContent(gc, paintBounds, item);
		} else {
			for (SmartViewerItem child : children) {
				renderItemBorder(gc, paintBounds, gc.getDevice()
						.getSystemColor(SWT.COLOR_BLACK), child);
				renderItemContent(gc, paintBounds, child);
			}
		}
		Control control = viewer.getControl();
		Point cursorLoc = control.toControl(display.getCursorLocation());
		if (item.contains(cursorLoc.x, cursorLoc.y)) {
			renderActions(gc, paintBounds, viewer, item, cursorLoc);
		}
	}

	private void renderActions(GC gc, Rectangle paintBounds,
			SmartViewer viewer, SmartViewerItem item, Point cursorLoc) {
		List<SmartViewerAction> toolbarActions = item.getToolbarActions();
		if (toolbarActions != null) {
			int x = item.getX() + item.getWidth() - 18;
			int y = item.getY() + 2;
			gc.setLineWidth(1);
			gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
			for (SmartViewerAction action : toolbarActions) {
				if (action.isVisible()) {
					Image image = action.getImage();
					if (image != null) {
						Rectangle imageBounds = image.getBounds();
						gc.drawImage(image, x - paintBounds.x, y
								- paintBounds.y);
						Rectangle actionBounds = new Rectangle(x, y,
								imageBounds.width, imageBounds.height);
						item.putExtraData(action, actionBounds);
						putTooltipData(item, action.getText(), actionBounds);
						if (cursorLoc != null
								&& actionBounds.contains(cursorLoc.x,
										cursorLoc.y)) {
							gc.drawRectangle(actionBounds.x - paintBounds.x,
									actionBounds.y - paintBounds.y,
									actionBounds.width, actionBounds.height);
						}
						y += imageBounds.height + 2;
					}
				}
			}
		}
	}

	private void renderFile(GC gc, File file, int index, int x, int y,
			int width, int height, boolean selected) {
		if (isImageFile(file)) {
			drawImage(gc, file, x, y, width, height - 14);
		}
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		String txt = file.getName();
		int maxCar = width / gc.getCharWidth('A');
		if (txt.length() > maxCar) {
			txt = txt.substring(0, maxCar);
		}
		Point p = gc.textExtent(txt);
		gc.drawText(txt, x + (width - p.x) / 2, y + height - 14, true);
	}

	private void renderDirectory(GC gc, File file, int x, int y, int width,
			int height, boolean selected) {
		TextLayout l = new TextLayout(gc.getDevice());
		try {
			l.setAlignment(SWT.CENTER);
			l.setWidth(width);
			l.setText(file.getName() + " (empty)");
			l.draw(gc, x + 5, y + 5);
		} finally {
			l.dispose();
		}
	}

	private boolean isImageFile(File file) {
		try {
			String mimeType = Files.probeContentType(file.toPath());
			if ("image/jpeg".equals(mimeType)) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void drawImage(GC gc, File file, int x, int y, int width, int height) {
		try {
			String mimeType = Files.probeContentType(file.toPath());
			if ("image/jpeg".equals(mimeType)) {
				Image img = getImage(file.getPath());
				if (img != null) {
					Rectangle imgBounds = img.getBounds();
					Rectangle scaled = scaleTo(imgBounds, width - 2, height - 2);
					if (width > scaled.width) {
						x += (width - scaled.width) / 2;
					}
					if (height > scaled.height) {
						y += (height - scaled.height) / 2;
					}
					gc.drawImage(img, 0, 0, imgBounds.width, imgBounds.height,
							x + 1, y + 1, scaled.width, scaled.height);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Rectangle scaleTo(Rectangle data, int maxWidth, int maxHeight) {
		if (data.width < maxWidth && data.height < maxHeight) {
			return data;
		}
		float ratio = Math.min((((float) maxWidth) / data.width),
				(((float) maxHeight) / data.height));
		int w = (int) (data.width * ratio);
		int h = (int) (data.height * ratio);
		return new Rectangle(0, 0, w, h);
	}

	private Image getImage(String fileName) {
		Image img = imageRegistry.get(fileName);
		if (img == null) {
			img = new Image(display, fileName);
			imageRegistry.put(fileName, img);
		}
		return img;
	}

}
