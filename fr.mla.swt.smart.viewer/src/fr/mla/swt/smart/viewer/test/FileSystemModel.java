package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.util.Collections;
import java.util.List;

import fr.mla.swt.smart.viewer.model.CustomSmartViewerModel;

public class FileSystemModel extends CustomSmartViewerModel {

	public FileSystemModel(File dir) {
		for (File f : dir.listFiles()) {
			addIfNotExists(f);
		}
	}

	@Override
	public List<?> getChildren(Object data) {
		return Collections.emptyList();
	}

}
