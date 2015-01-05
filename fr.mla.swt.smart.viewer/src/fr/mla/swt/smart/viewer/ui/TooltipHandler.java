package fr.mla.swt.smart.viewer.ui;

import org.eclipse.swt.widgets.Control;

public class TooltipHandler {

	public void handleTooltip(SmartViewer viewer, Object data) {
		Control control = viewer.getControl();
		if (data != null) {
			control.setToolTipText(data.toString());
		} else {
			control.setToolTipText(null);
		}
	}
}
