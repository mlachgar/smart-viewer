package fr.mla.swt.smart.viewer.test.colors;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import fr.mla.swt.smart.viewer.color.ColorCache;
import fr.mla.swt.smart.viewer.color.ColorDescriptor;

public class ColorTest {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout l = new GridLayout(2, true);
		shell.setLayout(l);
		int power = 5;
		final int max = (int) Math.pow(2, power);
		final ColorDescriptor[] colors = getColors(power, new float[] { 0f, 0.5f, 0.5f }, new float[] { 1f, 1f, 1f });

		Composite cmp = new Composite(shell, SWT.NONE);
		cmp.setLayout(new RowLayout());
		cmp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		Spinner sp = new Spinner(cmp, SWT.NONE);
		sp.setMinimum(2);
		sp.setMaximum(10);

		final TableViewer colorTable = new TableViewer(shell, SWT.V_SCROLL | SWT.BORDER);
		colorTable.setContentProvider(ArrayContentProvider.getInstance());
		final ColorLabelProvider labelProvider = new ColorLabelProvider(display);
		colorTable.setLabelProvider(labelProvider);
		colorTable.setInput(colors);
		TableLayout tableLayout = new TableLayout();
		colorTable.getTable().setLayout(tableLayout);
		colorTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		ColumnViewerToolTipSupport.enableFor(colorTable);

		final Canvas wheelCanvas = new Canvas(shell, SWT.BORDER);
		GridData canvasData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		canvasData.widthHint = 300;
		canvasData.heightHint = 300;
		wheelCanvas.setLayoutData(canvasData);
		wheelCanvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				Image image = new Image(e.display, 300, 300);
				GC gc = new GC(image);
				try {
					int step = 360 / max;
					gc.setAdvanced(true);
					gc.setAntialias(SWT.ON);
					gc.setTextAntialias(SWT.ON);
					gc.setInterpolation(SWT.HIGH);
					gc.setLineWidth(1);
					for (int i = 0; i < colors.length; i++) {
						gc.setBackground(labelProvider.cache.getColor(colors[i].red, colors[i].green, colors[i].blue));
						gc.fillArc(0, 0, 300, 300, (int) (colors[i].hue * 360), step + 1);
						// double t = colors[i].hue * 2 * Math.PI;
						// gc.drawLine(150, 150, 150 + (int) (Math.cos(t) *
						// 150.0), 150 + (int) (Math.sin(t) * 150.0));
					}
					StructuredSelection ss = (StructuredSelection) colorTable.getSelection();
					Object element = ss.getFirstElement();
					if (element instanceof ColorDescriptor) {
						ColorDescriptor color = (ColorDescriptor) element;
						float h = -color.hue;
						double t0 = h * 2 * Math.PI;
						double t1 = t0 - (2 * Math.PI / max);
						gc.setLineWidth(2);
						gc.drawLine(150, 150, 150 + (int) (Math.cos(t0) * 150.0), 150 + (int) (Math.sin(t0) * 150.0));
						gc.drawLine(150, 150, 150 + (int) (Math.cos(t1) * 150.0), 150 + (int) (Math.sin(t1) * 150.0));
					}

					gc.drawOval(0, 0, 299, 299);
				} finally {
					e.gc.drawImage(image, -e.x, -e.y);
					gc.dispose();
					image.dispose();
				}
			}
		});

		colorTable.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				wheelCanvas.redraw();
			}
		});

		shell.setSize(1100, 800);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static ColorDescriptor[] getColors(int power, float[] minValue, float[] maxValue) {
		int max = (int) Math.pow(2, power);
		ColorDescriptor[] colors = new ColorDescriptor[max];
		for (int i = 0; i < colors.length; i++) {
			float h = 0f;
			float s = minValue[1];
			float b = minValue[2];
			for (int p = 0; p < power; p++) {
				int c = (int) Math.pow(2, p);
				h += ((i / c) % 2) * (maxValue[0] - minValue[0]) / 2 / c;
				s += ((i / c) % 2) * (maxValue[1] - minValue[1]) / 2 / c;
				b += ((i / c) % 2) * (maxValue[2] - minValue[2]) / 2 / c;
			}

			colors[i] = new ColorDescriptor(h, s, b);
		}
		return colors;
	}

	private static class ColorLabelProvider extends ColumnLabelProvider {

		private ColorCache cache;

		public ColorLabelProvider(Display display) {
			cache = new ColorCache(display);
		}

		@Override
		public String getText(Object element) {
			return "                                                        ";
		}

		@Override
		public Color getBackground(Object element) {
			if (element instanceof ColorDescriptor) {
				ColorDescriptor color = (ColorDescriptor) element;
				return cache.getColor(color.red, color.green, color.blue);
			}
			return null;
		}

		@Override
		public String getToolTipText(Object element) {
			return String.valueOf(element);
		}

		@Override
		public Color getForeground(Object element) {
			return null;
		}

	}

}
