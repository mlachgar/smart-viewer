package fr.mla.swt.smart.viewer.group;

public class GroupData {

	private Object data;
	private DataGroup group;

	public GroupData(Object data, DataGroup group) {
		this.data = data;
		this.group = group;
	}

	public Object getData() {
		return data;
	}

	public DataGroup getGroup() {
		return group;
	}

	public void setGroup(DataGroup group) {
		this.group = group;
	}

}
