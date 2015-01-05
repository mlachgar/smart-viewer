package fr.mla.swt.smart.viewer.test.colors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import fr.mla.swt.smart.viewer.color.ColorCache;
import fr.mla.swt.smart.viewer.color.ColorDescriptor;
import fr.mla.swt.smart.viewer.color.ColorModel;

public class ColorWheelCanvas extends Canvas {

	private ColorModel model;
	private ColorCache cache;
	private ColorDescriptor selectedColor;

	public ColorWheelCanvas(Composite parent, int style, final ColorCache cache) {
		super(parent, style);
		this.cache = cache;
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				if (model == null) {
					return;
				}
				ColorDescriptor[] colors = model.getAllColors();
				if (colors == null) {
					return;
				}
				Image image = new Image(e.display, 300, 300);
				GC gc = new GC(image);
				try {
					int max = model.getSize();
					int step = 360 / max;
					gc.setAdvanced(true);
					gc.setAntialias(SWT.ON);
					gc.setTextAntialias(SWT.ON);
					gc.setInterpolation(SWT.HIGH);
					gc.setLineWidth(1);
					for (int i = 0; i < colors.length; i++) {
						gc.setBackground(cache.getColor(colors[i].red,
								colors[i].green, colors[i].blue));
						gc.fillArc(2, 2, 296, 296, (int) (colors[i].hue * 360),
								step + 1);
						// double t = colors[i].hue * 2 * Math.PI;
						// gc.drawLine(150, 150, 150 + (int) (Math.cos(t) *
						// 150.0), 150 + (int) (Math.sin(t) * 150.0));
					}
					if (selectedColor != null) {
						float h = -selectedColor.hue;
						double t0 = h * 2 * Math.PI;
						double t1 = t0 - (2 * Math.PI / max);
						gc.setLineWidth(2);
						gc.drawLine(150, 150,
								150 + (int) (Math.cos(t0) * 150.0),
								150 + (int) (Math.sin(t0) * 150.0));
						gc.drawLine(150, 150,
								150 + (int) (Math.cos(t1) * 150.0),
								150 + (int) (Math.sin(t1) * 150.0));
					}

					gc.drawOval(2, 2, 296, 296);
				} finally {
					e.gc.drawImage(image, -e.x, -e.y);
					gc.dispose();
					image.dispose();
				}
			}
		});
	}

	public void setModel(ColorModel model) {
		this.model = model;
		redraw();
	}

	public void setSelectedColor(ColorDescriptor selectedColor) {
		this.selectedColor = selectedColor;
		redraw();
	}

}
