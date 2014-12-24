package fr.mla.swt.smart.viewer.test;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fr.mla.swt.smart.viewer.model.ListModel;
import fr.mla.swt.smart.viewer.renderer.SmartViewerRenderer;
import fr.mla.swt.smart.viewer.scroll.DefaultScrollManager;
import fr.mla.swt.smart.viewer.scroll.GridScrollManager;
import fr.mla.swt.smart.viewer.scroll.ScrollManager;
import fr.mla.swt.smart.viewer.ui.SmartViewer;

public class Main {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		FillLayout layout = new FillLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		shell.setLayout(layout);

		final CTabFolder folder = new CTabFolder(shell, SWT.NONE);

		FileRenderer renderer = new FileRenderer(display);
		renderer.setBackground(shell.getBackground());
		File userHome = new File(System.getProperty("user.home"));
		FileSystemModel model = new FileSystemModel(userHome);

		final SmartViewer<File> defaultViewer = createSmartViewer("Default scroll", folder, model, renderer,
				new DefaultScrollManager<File>());
		final SmartViewer<File> gridViewer = createSmartViewer("Grid scroll", folder, model, renderer,
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
			SmartViewerRenderer<T> renderer, ScrollManager<T> scrollManager) {
		CTabItem item = new CTabItem(parent, SWT.NONE);
		SmartViewer<T> viewer = new SmartViewer<>(parent, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
		viewer.setRenderer(renderer);
		viewer.setModel(model);
		viewer.setScrollManager(scrollManager);
		item.setControl(viewer);
		item.setText(title);
		return viewer;
	}

}
