package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileGroup {

	private boolean expanded = false;
	private final String id;
	private GroupColor color;
	private final List<GroupFile> files = new ArrayList<GroupFile>();

	public FileGroup(String id, GroupColor color) {
		this.id = id;
		this.color = color;
	}

	public String getId() {
		return id;
	}

	public GroupColor getColor() {
		return color;
	}

	public void setColor(GroupColor color) {
		this.color = color;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public boolean isExpanded() {
		return expanded;
	}
	
	public List<GroupFile> getFiles() {
		return files;
	}
	
	public GroupFile addFile(File file) {
		GroupFile fg = new GroupFile(file, this);
		files.add(fg);
		return fg;
	}
	
	public boolean removeFile(GroupFile file) {
		return files.remove(file);
	}

}
