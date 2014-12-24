package fr.mla.swt.smart.viewer.ui;

import java.util.Collection;
import java.util.List;

public interface SelectionManager<T> {

	public Collection<T> getSelectedData();

	public boolean canSelect(Collection<SmartViewerItem<T>> items);

	public void clearSelection(List<SmartViewerItem<T>> items, SmartViewerItem<T> exceptedItem);

	public boolean selectRange(List<SmartViewerItem<T>> items, int start, int end);

	public boolean appendToSelection(SmartViewerItem<T> item, boolean unselectIfSelected);

	public boolean selectOnly(List<SmartViewerItem<T>> items, SmartViewerItem<T> item, boolean forceClear);

	public boolean expandSelectionTo(List<SmartViewerItem<T>> items, SmartViewerItem<T> item);

}