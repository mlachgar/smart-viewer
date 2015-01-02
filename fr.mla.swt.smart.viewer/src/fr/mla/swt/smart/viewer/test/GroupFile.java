package fr.mla.swt.smart.viewer.test;

import java.io.File;

public class GroupFile {

	private File file;
	private FileGroup group;

	public GroupFile(File file, FileGroup group) {
		this.file = file;
		this.group = group;
	}

	public File getFile() {
		return file;
	}

	public FileGroup getGroup() {
		return group;
	}

	public void setGroup(FileGroup group) {
		this.group = group;
	}
	
}
