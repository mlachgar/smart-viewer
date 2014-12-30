package fr.mla.swt.smart.viewer.test;

import java.io.File;

import fr.mla.swt.smart.viewer.model.DefaultListModel;

public class FileSystemModel extends DefaultListModel<File> {

	public FileSystemModel(File dir) {
		for (File f : dir.listFiles()) {
			addIfNotExists(f);
		}
	}

}
