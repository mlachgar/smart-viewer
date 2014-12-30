package fr.mla.swt.smart.viewer.ui;

public class DefaultViewerItemsFactory<T> implements SmartViewerItemsFactory<T> {

	@Override
	public SmartViewerItem<T> createItem(T data, int index) {
		return new SmartViewerItem<T>(data, index);
	}
	
}
