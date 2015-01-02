package fr.mla.swt.smart.viewer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomSmartViewerModel extends AbstractSmartViewerModel {
	protected List<Object> items = new ArrayList<Object>();

	public CustomSmartViewerModel() {

	}

	public CustomSmartViewerModel(List<?> items) {
		this.items.addAll(items);
	}

	public CustomSmartViewerModel(Object[] items) {
		if (items != null) {
			for (Object item : items) {
				this.items.add(item);
			}
		}
	}

	public void move(int from, int to) {
		if (checkIndex(from) && checkIndex(to)) {
			Object item1 = items.get(from);
			Object item2 = items.get(to);
			items.set(to, item1);
			items.set(from, item2);
			fireModelChange();
		}
	}

	public void set(int index, Object item) {
		if (checkIndex(index)) {
			items.set(index, item);
			fireModelChange();
		}
	}

	public int size() {
		return items.size();
	}

	public Object getItemAt(int i) {
		return items.get(i);
	}

	public int indexOf(Object item) {
		return items.indexOf(item);
	}

	public void add(Object item, int index) {
		items.add(index, item);
		fireModelChange();
	}

	public void addIfNotExists(Object item) {
		if (items.indexOf(item) == -1) {
			items.add(item);
			fireModelChange();
		}
	}

	public void addOrReplace(Object item) {
		int index = items.indexOf(item);
		if (index == -1) {
			items.add(item);
		} else {
			items.set(index, item);
		}
		fireModelChange();
	}

	public boolean moveToEnd(int index) {
		if (index < items.size() - 1) {
			items.add(items.remove(index));
			fireModelChange();
			return true;
		}
		return false;
	}

	public Object remove(int index) {
		Object item = items.remove(index);
		fireModelChange();
		return item;
	}

	public boolean remove(Object item) {
		if (items.remove(item)) {
			fireModelChange();
			return true;
		}
		return false;
	}

	public boolean removeAll(Collection<?> items) {
		if (this.items.removeAll(items)) {
			fireModelChange();
			return true;
		}
		return false;
	}

	public void addAll(Collection<?> items) {
		this.items.addAll(items);
		fireModelChange();
	}

	public void clear() {
		if (!items.isEmpty()) {
			items.clear();
			fireModelChange();
		}
	}

	private boolean checkIndex(int i) {
		return i >= 0 && i < items.size();
	}

	public void setItems(List<?> items) {
		if (!this.items.equals(items)) {
			this.items.clear();
			this.items.addAll(items);
			fireModelChange();
		}
	}

	public void sort(Comparator<Object> comparator) {
		Collections.sort(items, comparator);
	}

	public List<?> getItems() {
		return new ArrayList<>(items);
	}

	@Override
	public List<?> getChildren(Object item) {
		return Collections.emptyList();
	}
}
