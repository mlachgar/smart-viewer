package fr.mla.swt.smart.viewer.test.colors;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;

import fr.mla.swt.smart.viewer.color.ColorCache;
import fr.mla.swt.smart.viewer.color.ColorDescriptor;
import fr.mla.swt.smart.viewer.color.ColorModel;

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

		final ColorParamComposite hParam = new ColorParamComposite(cmp,
				SWT.NONE, "Hue");
		final ColorParamComposite sParam = new ColorParamComposite(cmp,
				SWT.NONE, "Stauration");
		final ColorParamComposite bParam = new ColorParamComposite(cmp,
				SWT.NONE, "Brightness");

		hParam.setRange(model.getMinH(), model.getMaxH());
		sParam.setRange(model.getMinS(), model.getMaxS());
		bParam.setRange(model.getMinB(), model.getMaxB());

		Button button = new Button(cmp, SWT.FLAT);
		button.setText("Generate");

		final TableViewer colorTable = new TableViewer(shell, SWT.V_SCROLL
				| SWT.BORDER);
		colorTable.setContentProvider(ArrayContentProvider.getInstance());
		final ColorLabelProvider labelProvider = new ColorLabelProvider(display);
		colorTable.setLabelProvider(labelProvider);
		TableLayout tableLayout = new TableLayout();
		colorTable.getTable().setLayout(tableLayout);
		colorTable.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		ColumnViewerToolTipSupport.enableFor(colorTable);

		colorTable.setInput(model.getAllColors());

		final ColorWheelCanvas wheelCanvas = new ColorWheelCanvas(shell,
				SWT.BORDER, labelProvider.cache);
		GridData canvasData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		canvasData.widthHint = 300;
		canvasData.heightHint = 300;
		wheelCanvas.setLayoutData(canvasData);
		wheelCanvas.setModel(model);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setPower(sp.getSelection());
				model.setMinValue(hParam.getMinValue(), sParam.getMinValue(),
						bParam.getMinValue());
				model.setMaxValue(hParam.getMaxValue(), sParam.getMaxValue(),
						bParam.getMaxValue());
				labelProvider.cache.dispose();
				colorTable.setInput(model.getAllColors());
				wheelCanvas.setSelectedColor(null);
			}
		});

		colorTable.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) colorTable
						.getSelection();
				Object element = ss.getFirstElement();
				if (element instanceof ColorDescriptor) {
					ColorDescriptor color = (ColorDescriptor) element;
					wheelCanvas.setSelectedColor(color);
				}
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

	private static class ColorLabelProvider extends OwnerDrawLabelProvider {

		private ColorCache cache;

		public ColorLabelProvider(Display display) {
			cache = new ColorCache(display);
		}

		@Override
		protected void measure(Event event, Object element) {
			TableItem tableItem = (TableItem) event.item;
			Rectangle itemBounds = tableItem.getParent().getBounds();
			event.width = itemBounds.width-30;
			event.height = 40;
			event.setBounds(new Rectangle(event.x, event.y, event.width,
					event.height));
		}

		@Override
		protected void paint(Event event, Object element) {
			if (element instanceof ColorDescriptor) {
				TableItem tableItem = (TableItem) event.item;
				Rectangle itemBounds = tableItem.getParent().getBounds();
				ColorDescriptor color = (ColorDescriptor) element;
				Color c = cache.getColor(color.red, color.green, color.blue);
				event.gc.setBackground(c);
				event.gc.fillRectangle(event.x + 2, event.y + 2,
						itemBounds.width - 2, 36);
			}
		}

		@Override
		public String getToolTipText(Object element) {
			return String.valueOf(element);
		}

	}

}
