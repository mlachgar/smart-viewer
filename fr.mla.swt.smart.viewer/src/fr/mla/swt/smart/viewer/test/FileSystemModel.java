package fr.mla.swt.smart.viewer.test;

import java.io.File;

import fr.mla.swt.smart.viewer.model.DefaultListModel;

public class FileSystemModel extends DefaultListModel<File> {

	public FileSystemModel(File dir) {
		for (File f : dir.listFiles()) {
			addIfNotExists(f);
			if (f.isDirectory()) {
				File[] listFiles = f.listFiles();
				if (listFiles != null) {
					for (File c : listFiles) {
						addIfNotExists(c);
					}
				}
			}
		}
	}

}
