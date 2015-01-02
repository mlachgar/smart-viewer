package fr.mla.swt.smart.viewer.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;

public class DefaultSelectionManager implements SelectionManager {

	protected final LinkedHashSet<Object> selectedData = new LinkedHashSet<>();
	protected final LinkedHashSet<SmartViewerItem> selectedItems = new LinkedHashSet<>();

	private List<SmartViewerItem> items = new ArrayList<>();
	private final List<List<SmartViewerItem>> depthItems = new ArrayList<>();

	@Override
	public void setItems(List<SmartViewerItem> items) {
		HashSet<?> previousSelection = new HashSet<>(selectedData);
		this.items.clear();
		this.selectedItems.clear();
		this.selectedData.clear();
		this.depthItems.clear();
		this.items.addAll(items);
		initDepthItems(0, items, previousSelection);

	}

	private void initDepthItems(int depth, List<SmartViewerItem> items, Collection<?> selectedData) {
		if (!items.isEmpty()) {
			if (depth >= depthItems.size()) {
				for (int i = depthItems.size(); i <= depth; i++) {
					depthItems.add(new ArrayList<SmartViewerItem>());
				}
			}
			List<SmartViewerItem> list = depthItems.get(depth);
			for (SmartViewerItem item : items) {
				list.add(item);
				if (selectedData.contains(item.getData())) {
					select(item);
				}
				initDepthItems(depth + 1, item.getChildren(), selectedData);
			}
		}
	}

	@Override
	public List<SmartViewerItem> getDepthItems(int depth) {
		if (depth >= 0 && depth < depthItems.size()) {
			return depthItems.get(depth);
		}
		return Collections.emptyList();
	}

	@Override
	public Collection<?> getSelectedData() {
		return selectedData;
	}

	@Override
	public Collection<SmartViewerItem> getSelectedItems() {
		return selectedItems;
	}

	protected boolean canSelect(final SmartViewerItem item) {
		if (!selectedItems.isEmpty()) {
			SmartViewerItem firstItem = selectedItems.iterator().next();
			return firstItem.getDepth() == item.getDepth();
		}
		return true;
	}

	@Override
	public boolean selectAll() {
		if (!selectedItems.isEmpty()) {
			SmartViewerItem firstItem = selectedItems.iterator().next();
			selectAll(depthItems.get(firstItem.getDepth()));
		} else {
			selectAll(depthItems.get(depthItems.size() - 1));
		}
		return true;
	}

	private void selectAll(List<SmartViewerItem> items) {
		clearSelection(null);
		for (SmartViewerItem item : items) {
			select(item);
		}
	}

	@Override
	public void clearSelection(SmartViewerItem exceptedItem) {
		for (final SmartViewerItem item : items) {
			if (exceptedItem == null || !item.getData().equals(exceptedItem.getData())) {
				item.setSelected(false);
			}
			unselectChildren(item, exceptedItem);
		}
		selectedItems.clear();
		selectedData.clear();
		select(exceptedItem);
	}

	private void unselectChildren(SmartViewerItem item, SmartViewerItem exceptedItem) {
		for (final SmartViewerItem child : item.getChildren()) {
			if (exceptedItem == null || !child.getData().equals(exceptedItem.getData())) {
				child.setSelected(false);
			}
			unselectChildren(child, exceptedItem);
		}
	}

	private void select(SmartViewerItem item) {
		if (item != null) {
			item.setSelected(true);
			selectedItems.add(item);
			selectedData.add(item.getData());
		}
	}

	private void unselect(SmartViewerItem item) {
		if (item != null) {
			item.setSelected(false);
			selectedItems.remove(item);
			selectedData.remove(item.getData());
		}
	}

	protected boolean selectRange(List<SmartViewerItem> items, int start, int end) {
		final List<SmartViewerItem> toSelect = new ArrayList<>();
		if (start < end) {
			for (int i = start; i <= end; i++) {
				final SmartViewerItem item = items.get(i);
				if (canSelect(item)) {
					toSelect.add(item);
				}
			}
		} else {
			for (int i = start; i >= end; i--) {
				final SmartViewerItem item = items.get(i);
				if (canSelect(item)) {
					toSelect.add(item);
				}
			}
		}
		if (!toSelect.isEmpty()) {
			clearSelection(null);
			for (final SmartViewerItem item : toSelect) {
				select(item);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean appendToSelection(SmartViewerItem item, boolean unselectIfSelected) {
		if (item != null) {
			if (!item.isSelected()) {
				if (canSelect(item)) {
					select(item);
					return true;
				}
			} else if (unselectIfSelected) {
				unselect(item);
				return true;
			}
		}
		return false;
	}

	// @Override
	// public boolean selectNext(List<SmartViewerItem> items, int index, int
	// shift, boolean clearSelection) {
	// SmartViewerItem nextItem = null;
	// index += shift;
	// while (index >= 0 && index < items.size()) {
	// nextItem = items.get(index);
	// if (canSelect(nextItem)) {
	// break;
	// }
	// index += shift;
	// }
	// if (nextItem != null) {
	// if (clearSelection) {
	// clearSelection(items, nextItem);
	// nextItem.setSelected(true);
	// selectedData.add(nextItem.getData());
	// return true;
	// } else {
	// if (nextItem.isSelected()) {
	// nextItem.setSelected(false);
	// selectedData.remove(nextItem.getData());
	// return true;
	// } else {
	// if (canSelect(nextItem)) {
	// nextItem.setSelected(true);
	// selectedData.add(nextItem.getData());
	// return true;
	// }
	// }
	// }
	// }
	// return false;
	// }

	@Override
	public boolean selectOnly(SmartViewerItem item, boolean forceClear) {
		if (item != null) {
			if (!item.isSelected()) {
				clearSelection(item);
				select(item);
				return true;
			} else {
				if (forceClear) {
					clearSelection(item);
				}
				item.setSelected(true);
				return forceClear;
			}
		}
		return false;
	}

	@Override
	public boolean expandSelectionTo(SmartViewerItem item) {
		List<SmartViewerItem> items = depthItems.get(item.getDepth());
		final int index = items.indexOf(item);
		if (index < 0) {
			return false;
		}

		// get the selection bounds
		final ListIterator<SmartViewerItem> it = items.listIterator();
		int minSelect = -1;
		int maxSelect = -1;
		while (it.hasNext()) {
			final int i = it.nextIndex();
			final SmartViewerItem e = it.next();
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
