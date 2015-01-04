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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import fr.mla.swt.smart.viewer.ui.ColorCache;

public class CopyOfColorTest {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout l = new GridLayout(2, true);
		shell.setLayout(l);
		final ColorModel model = new ColorModel();
		Composite cmp = new Composite(shell, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.center = true;
		cmp.setLayout(rowLayout);
		cmp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		final Spinner sp = new Spinner(cmp, SWT.NONE);
		sp.setMinimum(2);
		sp.setMaximum(10);
		sp.setSelection(model.getPower());

		final ColorParamComposite hParam = new ColorParamComposite(cmp, SWT.NONE, "Hue");
		final ColorParamComposite sParam = new ColorParamComposite(cmp, SWT.NONE, "Stauration");
		final ColorParamComposite bParam = new ColorParamComposite(cmp, SWT.NONE, "Brightness");

		hParam.setRange(model.getMinH(), model.getMaxH());
		sParam.setRange(model.getMinS(), model.getMaxS());
		bParam.setRange(model.getMinB(), model.getMaxB());

		Button button = new Button(cmp, SWT.FLAT);
		button.setText("Generate");

		final TableViewer colorTable = new TableViewer(shell, SWT.V_SCROLL | SWT.BORDER);
		colorTable.setContentProvider(ArrayContentProvider.getInstance());
		final ColorLabelProvider labelProvider = new ColorLabelProvider(display);
		colorTable.setLabelProvider(labelProvider);
		TableLayout tableLayout = new TableLayout();
		colorTable.getTable().setLayout(tableLayout);
		colorTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		ColumnViewerToolTipSupport.enableFor(colorTable);

		colorTable.setInput(model.getColors());

		final Canvas wheelCanvas = new Canvas(shell, SWT.BORDER);
		GridData canvasData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		canvasData.widthHint = 300;
		canvasData.heightHint = 300;
		wheelCanvas.setLayoutData(canvasData);
		wheelCanvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				GroupColor[] colors = model.getColors();
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
						gc.setBackground(labelProvider.cache.getColor(colors[i].red, colors[i].green, colors[i].blue));
						gc.fillArc(2, 2, 296, 296, (int) (colors[i].hue * 360), step + 1);
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

					gc.drawOval(2, 2, 296, 296);
				} finally {
					e.gc.drawImage(image, -e.x, -e.y);
					gc.dispose();
					image.dispose();
				}
			}
		});

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setPower(sp.getSelection());
				model.setMinValue(hParam.getMinValue(), sParam.getMinValue(), bParam.getMinValue());
				model.setMaxValue(hParam.getMaxValue(), sParam.getMaxValue(), bParam.getMaxValue());
				labelProvider.cache.dispose();
				colorTable.setInput(model.getColors());
				wheelCanvas.redraw();
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