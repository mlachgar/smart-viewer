package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.mla.swt.smart.viewer.model.AbstractSmartViewerModel;

public class FileGroupModel extends AbstractSmartViewerModel {

	private final FileGroupFactory factory = new FileGroupFactory();
	private Map<File, GroupFile> filesMap = new LinkedHashMap<>();
	private Map<String, FileGroup> groupsMap = new LinkedHashMap<>();

	public FileGroupModel() {

	}

	public FileGroup getGroupById(String id) {
		return groupsMap.get(id);
	}

	public void addToGroup(Collection<File> files, FileGroup group) {
		if (group == null) {
			group = factory.newGroup();
			groupsMap.put(group.getId(), group);
		}
		for (File file : files) {
			GroupFile fg = group.addFile(file);
			filesMap.put(file, fg);
		}
		fireModelChange();
	}

	public void removeGroups(Collection<FileGroup> groups) {
		for (FileGroup group : groups) {
			for (GroupFile file : group.getFiles()) {
				filesMap.remove(file.getFile());
			}
			groupsMap.remove(group.getId());
		}
		fireModelChange();
	}

	public void removeFromGroup(Collection<GroupFile> files) {
		for (GroupFile file : files) {
			file.getGroup().removeFile(file);
			filesMap.remove(file.getFile());
		}
		fireModelChange();
	}

	public void removeFilesFromGroup(Collection<File> files) {
		for (File file : files) {
			GroupFile fg = filesMap.get(file);
			if (fg != null) {
				FileGroup group = fg.getGroup();
				group.removeFile(fg);
				filesMap.remove(file);
				if (group.getFiles().isEmpty()) {
					groupsMap.remove(group);
				}
			}
		}
		fireModelChange();
	}

	@Override
	public List<?> getItems() {
		return new ArrayList<>(groupsMap.values());
	}

	@Override
	public List<Object> getChildren(Object data) {
		if (data instanceof FileGroup) {
			FileGroup group = (FileGroup) data;
			return castTo(group.getFiles(), Object.class);
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends T> List<T> castTo(List<E> src, Class<T> type) {
		return (List<T>) src;
	}

}
