package fr.mla.swt.smart.viewer.ui;

import org.eclipse.swt.widgets.Event;

public interface TooltipHandler {

	public void handleTooltip(Event event, SmartViewerItem item, Object data);
	
	public void hideTooltip();

}