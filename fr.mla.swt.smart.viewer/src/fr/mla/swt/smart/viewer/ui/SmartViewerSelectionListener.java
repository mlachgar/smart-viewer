package fr.mla.swt.smart.viewer.ui;

import java.util.Collection;

public interface SmartViewerSelectionListener<T> {
	
	public void selctionChanged(SmartViewer<T> viewer, Collection<T> selectedData);
	
}
