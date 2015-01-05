package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.mla.swt.smart.viewer.group.DataGroup;
import fr.mla.swt.smart.viewer.group.GroupData;
import fr.mla.swt.smart.viewer.model.AbstractSmartViewerModel;

public class FileGroupModel extends AbstractSmartViewerModel {

	private final FileGroupFactory factory = new FileGroupFactory();
	private Map<File, GroupData> filesMap = new LinkedHashMap<>();
	private Map<String, DataGroup> groupsMap = new LinkedHashMap<>();
	private List<Object> data = new ArrayList<>();

	public FileGroupModel() {
		data.add(new NewGroupData());
	}

	public DataGroup getGroupById(String id) {
		return groupsMap.get(id);
	}

	public void addToGroup(Collection<File> files, DataGroup group) {
		if (group == null) {
			group = factory.newGroup();
			groupsMap.put(group.getId(), group);
			data.add(data.size() - 1, group);
		}
		for (File file : files) {
			GroupData fg = group.addData(file);
			filesMap.put(file, fg);
		}
		fireModelChange();
	}

	public void removeGroups(Collection<DataGroup> groups) {
		for (DataGroup group : groups) {
			for (GroupData file : group.getData()) {
				filesMap.remove(file.getData());
			}
			groupsMap.remove(group.getId());
			data.remove(group);
		}
		fireModelChange();
	}

	public void removeGroup(DataGroup group) {
		for (GroupData file : group.getData()) {
			filesMap.remove(file.getData());
		}
		groupsMap.remove(group.getId());
		data.remove(group);
		fireModelChange();
	}

	public void removeFromGroup(Collection<GroupData> files) {
		for (GroupData file : files) {
			DataGroup group = file.getGroup();
			group.removeData(file);
			filesMap.remove(file.getData());
			if (group.getData().isEmpty()) {
				groupsMap.remove(group);
				data.remove(group);
			}
		}
		fireModelChange();
	}

	public void removeFilesFromGroup(Collection<File> files) {
		for (File file : files) {
			GroupData fg = filesMap.get(file);
			if (fg != null) {
				DataGroup group = fg.getGroup();
				group.removeData(fg);
				filesMap.remove(file);
				if (group.getData().isEmpty()) {
					groupsMap.remove(group);
					data.remove(group);
				}
			}
		}
		fireModelChange();
	}

	@Override
	public List<?> getItems() {
		return data;
	}

	@Override
	public List<Object> getChildren(Object data) {
		if (data instanceof DataGroup) {
			DataGroup group = (DataGroup) data;
			return castTo(group.getData(), Object.class);
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends T> List<T> castTo(List<E> src, Class<T> type) {
		return (List<T>) src;
	}

}
