package fr.mla.swt.smart.viewer.test;

import java.io.File;

import fr.mla.swt.smart.viewer.dnd.StringArrayTransfer;

public class FilePathTransfer extends StringArrayTransfer {
	private static FilePathTransfer instance = new FilePathTransfer();
	private static final String TYPE_NAME = "file.path.transfer";

	public FilePathTransfer() {
		super(TYPE_NAME);
	}

	public static FilePathTransfer getInstance() {
		return instance;
	}

	@Override
	protected String serializeItem(Object data) {
		if (data instanceof GroupFile) {
			GroupFile gf = (GroupFile) data;
			return gf.getFile().getPath();
		} else if (data instanceof File) {
			File file = (File) data;
			return file.getPath();
		}
		return null;
	}

	@Override
	protected Object deserializeItem(String str) {
		return new File(str);
	}

}
