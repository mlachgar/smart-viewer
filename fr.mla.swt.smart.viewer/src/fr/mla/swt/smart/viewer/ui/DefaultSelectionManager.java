package fr.mla.swt.smart.viewer.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class DefaultSelectionManager<T> implements SelectionManager<T> {

	protected final LinkedHashSet<T> selectedData = new LinkedHashSet<>();
	protected final Set<T> unmodifiableSelectedData = Collections.unmodifiableSet(selectedData);

	@Override
	public Collection<T> getSelectedData() {
		return unmodifiableSelectedData;
	}

	protected boolean canSelect(final SmartViewerItem<T> item) {
		return true;
	}

	@Override
	public void clearSelection(List<SmartViewerItem<T>> items, SmartViewerItem<T> exceptedItem) {
		for (final SmartViewerItem<T> item : items) {
			if (exceptedItem == null || !item.getData().equals(exceptedItem.getData())) {
				item.setSelected(false);
			}
		}
		selectedData.clear();
		if (exceptedItem != null) {
			selectedData.add(exceptedItem.getData());
		}
	}

	@Override
	public boolean selectRange(List<SmartViewerItem<T>> items, int start, int end) {
		final List<SmartViewerItem<T>> toSelect = new ArrayList<>();
		if (start < end) {
			for (int i = start; i <= end; i++) {
				final SmartViewerItem<T> item = items.get(i);
				if (canSelect(item)) {
					toSelect.add(item);
				}
			}
		} else {
			for (int i = start; i >= end; i--) {
				final SmartViewerItem<T> item = items.get(i);
				if (canSelect(item)) {
					toSelect.add(item);
				}
			}
		}
		if (!toSelect.isEmpty()) {
			clearSelection(items, null);
			for (final SmartViewerItem<T> item : toSelect) {
				item.setSelected(true);
				selectedData.add(item.getData());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean appendToSelection(SmartViewerItem<T> item, boolean unselectIfSelected) {
		if (!item.isSelected()) {
			if (canSelect(item)) {
				selectedData.add(item.getData());
				item.setSelected(true);
				return true;
			}
		} else if (unselectIfSelected) {
			selectedData.remove(item.getData());
			item.setSelected(false);
			return true;
		}
		return false;
	}

	@Override
	public boolean selectNext(List<SmartViewerItem<T>> items, int index, int shift, boolean clearSelection) {
		SmartViewerItem<T> nextItem = null;
		index += shift;
		while (index >= 0 && index < items.size()) {
			nextItem = items.get(index);
			if (canSelect(nextItem)) {
				break;
			}
			index += shift;
		}
		if (nextItem != null) {
			if (clearSelection) {
				clearSelection(items, nextItem);
				nextItem.setSelected(true);
				selectedData.add(nextItem.getData());
				return true;
			} else {
				if (nextItem.isSelected()) {
					nextItem.setSelected(false);
					selectedData.remove(nextItem.getData());
					return true;
				} else {
					if (canSelect(nextItem)) {
						nextItem.setSelected(true);
						selectedData.add(nextItem.getData());
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean selectOnly(List<SmartViewerItem<T>> items, SmartViewerItem<T> item, boolean forceClear) {
		if (!item.isSelected()) {
			clearSelection(items, item);
			item.setSelected(true);
			selectedData.add(item.getData());
			return true;
		} else {
			if (forceClear) {
				clearSelection(items, item);
			}
			item.setSelected(true);
			return forceClear;
		}
	}

	@Override
	public boolean expandSelectionTo(List<SmartViewerItem<T>> items, SmartViewerItem<T> item) {
		final int index = items.indexOf(item);
		if (index < 0) {
			return false;
		}

		// get the selection bounds
		final ListIterator<SmartViewerItem<T>> it = items.listIterator();
		int minSelect = -1;
		int maxSelect = -1;
		while (it.hasNext()) {
			final int i = it.nextIndex();
			final SmartViewerItem<T> e = it.next();
			if (!e.isSelected()) {
				continue;
			}

			if (minSelect == -1) {
				minSelect = i;
			}

			if (i > maxSelect) {
				maxSelect = i;
			}
		}

		if (minSelect == -1) {
			// no existing selection, select block 0-index
			return selectRange(items, 0, index);
		} else {
			return selectRange(items, minSelect, index);
		}
	}

}
