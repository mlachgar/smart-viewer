package fr.mla.swt.smart.viewer.ui;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

public class DefaultTooltipHandler extends DefaultToolTip implements
		TooltipHandler {
	private DateFormat dateFormat;
	private SmartViewerItem currenItem;
	private Object currentData;

	public DefaultTooltipHandler(SmartViewer viewer) {
		super(viewer.getControl(), NO_RECREATE, false);
		dateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
	}

	@Override
	protected Object getToolTipArea(Event event) {
		return currentData;
	}

	@Override
	protected boolean shouldCreateToolTip(Event event) {
		return currenItem != null;
	}

	@Override
	public void hideTooltip() {
		hide();
	}

	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {
		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(new GridLayout());
		Label label = new Label(cmp, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (currentData != null) {
			if (currentData instanceof File) {
				File file = (File) currentData;
				label.setText(String.format("%s\n%s\n%s", file.getName(),
						formatSize(file.length()),
						formatDate(file.lastModified())));
			} else {
				label.setText(currentData.toString());
			}
		}
		Point size = cmp.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		size.x = 0;
		size.y = -size.y;
		setShift(size);
		return cmp;
	}

	@Override
	public void handleTooltip(Event event, SmartViewerItem item, Object data) {
		this.currenItem = item;
		this.currentData = data;
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
