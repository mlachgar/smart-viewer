package fr.mla.swt.smart.viewer.ui;

public class DefaultViewerItemsFactory implements SmartViewerItemsFactory {

	@Override
	public SmartViewerItem createItem(Object data, int index, int depth) {
		return new SmartViewerItem(data, index, depth);
	}

}
