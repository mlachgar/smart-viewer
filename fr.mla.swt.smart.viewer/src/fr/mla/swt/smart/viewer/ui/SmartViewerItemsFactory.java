package fr.mla.swt.smart.viewer.ui;

public interface SmartViewerItemsFactory<T> {

	public SmartViewerItem<T> createItem(T data, int index);

}