package fr.mla.swt.smart.viewer.group;

import java.util.ArrayList;
import java.util.List;

import fr.mla.swt.smart.viewer.color.ColorDescriptor;

public class DataGroup {

	private boolean expanded = false;
	private final String id;
	private ColorDescriptor color;
	private final List<GroupData> data = new ArrayList<GroupData>();

	public DataGroup(String id, ColorDescriptor color) {
		this.id = id;
		this.color = color;
	}

	public String getId() {
		return id;
	}

	public ColorDescriptor getColor() {
		return color;
	}

	public void setColor(ColorDescriptor color) {
		this.color = color;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public List<GroupData> getData() {
		return data;
	}

	public GroupData addData(Object data) {
		GroupData fg = new GroupData(data, this);
		this.data.add(fg);
		return fg;
	}

	public boolean removeData(GroupData data) {
		return this.data.remove(data);
	}

}
