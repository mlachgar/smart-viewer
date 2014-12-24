package fr.mla.swt.smart.viewer.model;

public interface SmartModelListener<T> {
	public void listChanged(Object source);
	public void itemModified(Object source, T item);
}
