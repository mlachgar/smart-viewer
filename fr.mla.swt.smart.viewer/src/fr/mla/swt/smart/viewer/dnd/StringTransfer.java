package fr.mla.swt.smart.viewer.dnd;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public abstract class StringTransfer extends ByteArrayTransfer {
	private final String mimeType;
	private final int typeId;

	public StringTransfer(String mimeType) {
		this.mimeType = mimeType;
		typeId = registerType(mimeType);
	}

	@Override
	final protected int[] getTypeIds() {
		return new int[] { typeId };
	}

	@Override
	final protected String[] getTypeNames() {
		return new String[] { mimeType };
	}

	@Override
	final protected void javaToNative(Object object, TransferData transferData) {
		String str = serialize(object);
		if (str != null) {
			object = str.getBytes();
		}
		super.javaToNative(object, transferData);
	}

	@Override
	final protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return deserialize(new String(bytes));
	}

	protected abstract String serialize(Object data);

	protected abstract Object deserialize(String data);

}
