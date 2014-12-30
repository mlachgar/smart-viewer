package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

import fr.mla.swt.smart.viewer.renderer.DefaultRenderer;
import fr.mla.swt.smart.viewer.ui.CompositeSmartViewerItem;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class FileRenderer extends DefaultRenderer<File> {

	private final ImageRegistry iconRegistry;
	private Display display;
	private DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

	public FileRenderer(Display display) {
		this.display = display;
		iconRegistry = new ImageRegistry(display);
	}

	public void dispose() {
		iconRegistry.dispose();
	}

	@Override
	public void renderItem(GC gc, Rectangle paintBounds, Point scroll, SmartViewerItem<File> item) {
		int width = item.getWidth();
		int height = item.getHeight();
		int x = item.getX() - paintBounds.x;
		int y = item.getY() - paintBounds.y;
		if (item.isSelected()) {
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION));
			gc.fillRoundRectangle(x, y, width, height, 10, 10);
		}
		gc.drawRoundRectangle(x, y, width, height, 10, 10);
		File file = item.getData();
		if (item instanceof CompositeSmartViewerItem) {
			CompositeSmartViewerItem<File, File> cmp = (CompositeSmartViewerItem<File, File>) item;
			List<SmartViewerItem<File>> children = cmp.getChildren();
			Rectangle parentBounds = new Rectangle(paintBounds.x - item.getX(), paintBounds.y - item.getY(),
					paintBounds.width, paintBounds.height);
			for (SmartViewerItem<File> child : children) {
				renderItem(gc, parentBounds, scroll, child);
			}
		} else if (file != null) {
			if (file.isDirectory()) {
				renderDirectory(gc, file, x, y, width, height, false);
			} else {
				renderFile(gc, file, x, y, width, height, false);
			}
		}

	}

	private void renderFile(GC gc, File file, int x, int y, int width, int height, boolean selected) {
		if (isImageFile(file)) {
			drawImage(gc, file, x, y, width, height);
		} else {
			TextLayout l = new TextLayout(gc.getDevice());
			try {
				l.setAlignment(SWT.CENTER);
				l.setWidth(width);
				l.setText(file.getName());
				l.draw(gc, x + 5, y + 5);
			} finally {
				l.dispose();
			}
		}

	}

	private void renderDirectory(GC gc, File file, int x, int y, int width, int height, boolean selected) {
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
					gc.drawImage(img, 0, 0, imgBounds.width, imgBounds.height, x + 1, y + 1, scaled.width,
							scaled.height);
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
		float ratio = Math.min((((float) maxWidth) / data.width), (((float) maxHeight) / data.height));
		int w = (int) (data.width * ratio);
		int h = (int) (data.height * ratio);
		return new Rectangle(0, 0, w, h);
	}

	private Image getImage(String fileName) {
		Image img = iconRegistry.get(fileName);
		if (img == null) {
			img = new Image(display, fileName);
			iconRegistry.put(fileName, img);
		}
		return img;
	}

}
