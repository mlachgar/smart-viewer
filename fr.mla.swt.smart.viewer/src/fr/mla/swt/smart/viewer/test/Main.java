package fr.mla.swt.smart.viewer.test;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		FillLayout l = new FillLayout();
		l.marginWidth = 0;
		l.marginHeight = 0;
		shell.setLayout(GridLayoutFactory.fillDefaults().create());

		FilesGroupComposite composite = new FilesGroupComposite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		shell.setSize(1100, 800);
		shell.open();
		composite.setFocus();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
