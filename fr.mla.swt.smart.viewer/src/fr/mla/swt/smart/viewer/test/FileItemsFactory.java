package fr.mla.swt.smart.viewer.test;

import java.io.File;

import fr.mla.swt.smart.viewer.ui.CompositeSmartViewerItem;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;
import fr.mla.swt.smart.viewer.ui.SmartViewerItemsFactory;

public class FileItemsFactory implements SmartViewerItemsFactory<File> {

	@Override
	public SmartViewerItem<File> createItem(File file, int index) {
		CompositeSmartViewerItem<File, File> item = new CompositeSmartViewerItem<>(file, index);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					item.addChild(new SmartViewerItem<File>(files[i], i));
				}
			}
		}
		return item;
	}

}
