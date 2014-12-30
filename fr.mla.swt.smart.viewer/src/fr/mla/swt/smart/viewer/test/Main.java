package fr.mla.swt.smart.viewer.test;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.ListModel;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.renderer.SmartViewerRenderer;
import fr.mla.swt.smart.viewer.scroll.DefaultScrollManager;
import fr.mla.swt.smart.viewer.scroll.GridScrollManager;
import fr.mla.swt.smart.viewer.scroll.ScrollManager;
import fr.mla.swt.smart.viewer.ui.SmartViewer;
import fr.mla.swt.smart.viewer.ui.SmartViewerItemsFactory;

public class Main {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		FillLayout l = new FillLayout();
		l.marginWidth = 0;
		l.marginHeight = 0;
		shell.setLayout(l);

		final CTabFolder folder = new CTabFolder(shell, SWT.NONE);

		FileRenderer renderer = new FileRenderer(display);
		renderer.setBackground(shell.getBackground());
		File userHome = new File("C:\\Users\\mlachgar\\Pictures\\image-test");
		FileSystemModel model = new FileSystemModel(userHome);
		FileLayout layout = new FileLayout(OrientationType.HORIZONTAL);
		FileItemsFactory itemsFactory = new FileItemsFactory();

		final SmartViewer<File> defaultViewer = createSmartViewer("Default scroll", folder, model, layout, itemsFactory, renderer,
				new DefaultScrollManager<File>());
		final SmartViewer<File> gridViewer = createSmartViewer("Grid scroll", folder, model, layout, itemsFactory, renderer,
				new GridScrollManager<File>());
		shell.setSize(1100, 677);
		shell.open();
		defaultViewer.refresh();
		gridViewer.refresh();

		folder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (folder.getSelectionIndex() == 0) {
					defaultViewer.refresh();
					defaultViewer.setFocus();
				} else {
					gridViewer.refresh();
					gridViewer.setFocus();
				}
			}
		});

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static <T> SmartViewer<T> createSmartViewer(String title, CTabFolder parent, ListModel<T> model,
			SmartViewerLayout<T> layout, SmartViewerItemsFactory<T> itemsFactory, SmartViewerRenderer<T> renderer, ScrollManager<T> scrollManager) {
		CTabItem item = new CTabItem(parent, SWT.NONE);
		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout());
		SmartViewer<T> viewer = new SmartViewer<>(cmp, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND | SWT.BORDER);
		viewer.setLayout(layout);
		viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setItemsFactory(itemsFactory);
		viewer.setRenderer(renderer);
		viewer.setModel(model);
		viewer.setScrollManager(scrollManager);
		item.setControl(cmp);
		item.setText(title);
		return viewer;
	}

}
