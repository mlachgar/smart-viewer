package fr.mla.swt.smart.viewer.ui;

import java.util.Collection;
import java.util.List;

public interface SelectionManager {

	public Collection<?> getSelectedData();

	public Collection<SmartViewerItem> getSelectedItems();

	public void clearSelection(SmartViewerItem exceptedItem);

	public boolean appendToSelection(SmartViewerItem item, boolean unselectIfSelected);

	public boolean selectOnly( SmartViewerItem item, boolean forceClear);

	public boolean expandSelectionTo(SmartViewerItem item);

	void setItems(List<SmartViewerItem> items);

	List<SmartViewerItem> getDepthItems(int depth);

	boolean selectAll();

}