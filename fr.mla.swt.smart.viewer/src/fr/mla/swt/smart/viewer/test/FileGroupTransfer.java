package fr.mla.swt.smart.viewer.test;

import fr.mla.swt.smart.viewer.dnd.StringArrayTransfer;
import fr.mla.swt.smart.viewer.group.DataGroup;

public class FileGroupTransfer extends StringArrayTransfer {
	private static FileGroupTransfer instance = new FileGroupTransfer();
	private static final String TYPE_NAME = "group.file.transfer";

	public FileGroupTransfer() {
		super(TYPE_NAME);
	}

	public static FileGroupTransfer getInstance() {
		return instance;
	}

	@Override
	protected String serializeItem(Object item) {
		if (item instanceof DataGroup) {
			DataGroup group = (DataGroup) item;
			return group.getId();
		}
		return null;
	}

}
