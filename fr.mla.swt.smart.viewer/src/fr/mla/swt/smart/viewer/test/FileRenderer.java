package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import fr.mla.swt.smart.viewer.renderer.DefaultRenderer;
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
		if (item.getData() != null) {
			TextLayout l = new TextLayout(gc.getDevice());
			try {
				int startX = item.getX() - paintBounds.x;
				int startY = item.getY() - paintBounds.y;
				File file = item.getData();
				gc.drawRectangle(startX, startY, item.getWidth(), item.getHeight());
				l.setAlignment(SWT.CENTER);
				l.setWidth(item.getWidth());
				l.setText(file.getName());
				l.draw(gc, startX + 5, startY + 5);
				l.setText(dateFormat.format(new Date(file.lastModified())));
				Rectangle b = l.getBounds();
				l.draw(gc, startX + 5, startY + item.getHeight() - b.height - 5);

				l.setText(String.valueOf(item.getIndex()));
				l.draw(gc, startX, startY + item.getHeight() / 2);
			} finally {
				l.dispose();
			}
		}
	}

	void drawImage(GC gc, SmartViewerItem<File> item, int x, int y) {
		File file = item.getData();
		Image img = getImage(file.getName());
		if (img != null) {
			Rectangle imgBounds = img.getBounds();
			if (item.getWidth() > imgBounds.width) {
				x += (item.getWidth() - imgBounds.width) / 2;
			}
			if (item.getHeight() > imgBounds.height) {
				y += (item.getHeight() - imgBounds.height) / 2;
			}
			gc.drawImage(img, 0, 0, imgBounds.width, imgBounds.height, x, y, imgBounds.width, imgBounds.height);
		}
	}

	private Image getImage(String fileName) {
		String ext = null;
		int i = fileName.lastIndexOf('.');
		if (i >= 0) {
			ext = fileName.substring(i + 1);
			Image img = iconRegistry.get(ext);
			if (img == null) {
				Program p = Program.findProgram(ext);
				if (p != null) {
					ImageData data = p.getImageData();
					img = new Image(display, data);
					iconRegistry.put(ext, img);
				}
			}
			return img;
		}
		return null;
	}

}