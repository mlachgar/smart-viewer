package fr.mla.swt.smart.viewer.ui;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CustomTooltipHandler implements TooltipHandler {
	private DateFormat dateFormat;
	private SmartViewerItem currenItem;
	private Object currentData;
	private SmartViewer viewer;
	private Shell shell;
	private Label label;

	public CustomTooltipHandler(SmartViewer viewer) {
		this.viewer = viewer;
		dateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
	}

	@Override
	public void hideTooltip() {
		if (shell != null) {
			currenItem = null;
			currentData = null;
			shell.close();
			shell = null;
		}
	}

	private void createTooltipArea() {
		if (shell == null || shell.isDisposed()) {
			shell = new Shell(viewer.getControl().getShell(), SWT.ON_TOP
					| SWT.TOOL | SWT.NO_FOCUS);
			shell.setLayout(new GridLayout());
			label = new Label(shell, SWT.WRAP);
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}
	}

	protected void fillToolTipContent(Event event) {
		if (currentData instanceof File) {
			File file = (File) currentData;
			label.setText(String.format("%s\n%s\n%s", file.getName(),
					formatSize(file.length()), formatDate(file.lastModified())));
		} else {
			label.setText(currentData.toString());
		}
		shell.pack();
		checkShellLocation(event);
	}

	private void checkShellLocation(Event event) {
		Point size = shell.getSize();
		Point p = viewer.getControl().toDisplay(event.x, event.y);
		shell.setLocation(p.x, p.y - size.y);
	}

	@Override
	public void handleTooltip(Event event, SmartViewerItem item, Object data) {
		if (item != null && data != null) {
			createTooltipArea();
			if (item != currenItem || !data.equals(currentData)) {
				this.currenItem = item;
				this.currentData = data;
				fillToolTipContent(event);
			} else {
				checkShellLocation(event);
			}
			shell.open();
			viewer.getControl().setFocus();
		} else {
			hideTooltip();
		}
	}

	public static String formatSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}

	private String formatDate(long milis) {
		return dateFormat.format(new Date(milis));
	}

}
