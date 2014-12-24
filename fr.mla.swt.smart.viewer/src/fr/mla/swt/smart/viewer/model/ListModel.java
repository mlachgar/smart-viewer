package fr.mla.swt.smart.viewer.model;

import java.util.List;

/**
 * @author mlachgar
 */
public interface ListModel<T> {

	public int size();

	public void setItems(List<? extends T> items);

	public T getItemAt(int i);

	public int indexOf(T item);

	public String getLabel(int i);

	public void add(T item, int index);

	public T remove(int index);
	
	public boolean remove(T item);

	public void clear();

	public void move(int from, int to);

	public void addListChangeListener(SmartModelListener<T> l);

	public void removeListChangeListener(SmartModelListener<T> l);

	public void set(int index, T item);

	public List<T> getItems();
}
