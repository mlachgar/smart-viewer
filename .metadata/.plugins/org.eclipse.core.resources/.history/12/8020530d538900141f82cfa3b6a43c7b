package fr.mla.swt.smart.viewer.test;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fr.mla.swt.smart.viewer.ui.SmartViewerCanvas;

public class Main {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		SmartViewerCanvas<File> canvas = new SmartViewerCanvas<>(shell, SWT.DOUBLE_BUFFERED|SWT.NO_BACKGROUND);
		FileRenderer renderer = new FileRenderer(display);
		renderer.setBackground(canvas.getBackground());
		canvas.setRenderer(renderer);
		canvas.setModel(new FileSystemModel(new File("/Users/mlachgar/Documents")));
		
		shell.setSize(1100, 780);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
