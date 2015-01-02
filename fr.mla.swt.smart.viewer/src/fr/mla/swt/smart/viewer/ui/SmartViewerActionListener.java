package fr.mla.swt.smart.viewer.ui;

import java.util.Collection;

public interface SmartViewerActionListener {

	public void mouseDoubleClick(SmartViewer viewer, SmartViewerItem item, int x, int y);

	public void defaultAction(SmartViewer viewer, Collection<SmartViewerItem> items);

}
