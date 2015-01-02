package fr.mla.swt.smart.viewer.test;

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
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fr.mla.swt.smart.viewer.ui.ColorCache;

public class ColorTest {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout l = new GridLayout(2, true);
		shell.setLayout(l);
		final int max = 20;
		final int kinds = 4;
		final GroupColor[] colors = getColors(max, kinds);
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
					if (element instanceof GroupColor) {
						GroupColor color = (GroupColor) element;
						float h = -color.hue;
						double t0 = h * 2 * Math.PI;
						double t1 = t0 - (2 * Math.PI / max);
						gc.setLineWidth(2);
						gc.drawLine(150, 150, 150 + (int) (Math.cos(t0) * 150.0), 150 + (int) (Math.sin(t0) * 150.0));
						gc.drawLine(150, 150, 150 + (int) (Math.cos(t1) * 150.0), 150 + (int) (Math.sin(t1) * 150.0));
					}

					gc.drawOval(0, 0, 300, 300);
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

	private static GroupColor[] getColors(int max, int kinds) {
		GroupColor[] colors = new GroupColor[max];
		float kindSize = 1f / kinds;
		for (int i = 0; i < colors.length; i++) {
			float f;
			if (i % 2 != 0) {
				f = 0.5f + ((i / 2) % kinds) * kindSize;
			} else {
				f = ((i / 2) % kinds) * kindSize;
			}
			int cycle = i / kinds;
			float h = (cycle * 1f / max) + f;
			float s = 1f;
			float b = 1f;
			s = 1f - cycle * 0.1f;
			b = 1f - (cycle / 2) * 0.05f;
			colors[i] = new GroupColor(h, s, b);
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
			if (element instanceof GroupColor) {
				GroupColor color = (GroupColor) element;
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
