package fr.mla.swt.smart.viewer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DefaultListModel<T> implements ListModel<T> {
	private List<T> items = new ArrayList<T>();
	private List<SmartModelListener<T>> modelListeners = new ArrayList<SmartModelListener<T>>();

	public DefaultListModel() {

	}

	public DefaultListModel(List<T> items) {
		this.items.addAll(items);
	}

	public DefaultListModel(T[] items) {
		if (items != null) {
			for (T item : items) {
				this.items.add(item);
			}
		}
	}

	@Override
	public void move(int from, int to) {
		if (checkIndex(from) && checkIndex(to)) {
			T item1 = items.get(from);
			T item2 = items.get(to);
			items.set(to, item1);
			items.set(from, item2);
			fireModelChange();
		}
	}

	@Override
	public void set(int index, T item) {
		if (checkIndex(index)) {
			items.set(index, item);
			fireModelChange();
		}
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public T getItemAt(int i) {
		return items.get(i);
	}

	@Override
	public int indexOf(T item) {
		return items.indexOf(item);
	}

	@Override
	public void add(T item, int index) {
		items.add(index, item);
		fireModelChange();
	}

	public void addIfNotExists(T item) {
		if (items.indexOf(item) == -1) {
			items.add(item);
			fireModelChange();
		}
	}

	public void addOrReplace(T item) {
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

	public T remove(int index) {
		T item = items.remove(index);
		fireModelChange();
		return item;
	}

	public boolean remove(T item) {
		if (items.remove(item)) {
			fireModelChange();
		}
		return false;
	}

	public void addAll(Collection<? extends T> items) {
		this.items.addAll(items);
		fireModelChange();
	}

	@Override
	public void addListChangeListener(SmartModelListener<T> l) {
		modelListeners.add(l);
	}

	@Override
	public void removeListChangeListener(SmartModelListener<T> l) {
		modelListeners.remove(l);
	}

	@Override
	public void clear() {
		if (!items.isEmpty()) {
			items.clear();
			fireModelChange();
		}
	}

	@Override
	public String getLabel(int i) {
		if (checkIndex(i)) {
			T item = items.get(i);
			if (item != null) {
				return item.toString();
			}
		}
		return "";
	}

	private boolean checkIndex(int i) {
		return i >= 0 && i < items.size();
	}

	public void setItems(List<? extends T> items) {
		if (!this.items.equals(items)) {
			this.items.clear();
			this.items.addAll(items);
			fireModelChange();
		}
	}

	public void sort(Comparator<T> comparator) {
		Collections.sort(items, comparator);
	}

	public List<T> getItems() {
		return new ArrayList<>(items);
	}

	protected void fireModelChange() {
		for (int i = modelListeners.size() - 1; i >= 0; i--) {
			modelListeners.get(i).listChanged(this);
		}
	}
}
