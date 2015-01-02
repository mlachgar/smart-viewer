package fr.mla.swt.smart.viewer.ui;

public interface SmartViewerItemsFactory {

	public SmartViewerItem createItem(Object data, int index, int depth);

}