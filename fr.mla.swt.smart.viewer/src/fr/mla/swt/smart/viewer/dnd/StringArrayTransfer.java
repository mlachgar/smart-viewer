package fr.mla.swt.smart.viewer.dnd;

import java.lang.reflect.Array;
import java.util.Collection;

public abstract class StringArrayTransfer extends StringTransfer {

	public StringArrayTransfer(String mimeType) {
		super(mimeType);
	}

	@Override
	final protected String serialize(Object data) {
		if (data != null) {
			StringBuilder sb = new StringBuilder();
			if (data instanceof Collection) {
				Collection<?> col = (Collection<?>) data;
				for (Object item : col) {
					String str = serializeItem(item);
					if (str != null) {
						sb.append(str);
						sb.append('\n');
					}
				}
			}
			if (data.getClass().isArray()) {
				for (int i = 0; i < Array.getLength(data); i++) {
					String str = serializeItem(Array.get(data, i));
					if (str != null) {
						sb.append(str);
						sb.append('\n');
					}
				}

			}
			return sb.toString();
		}
		return null;
	}

	@Override
	final protected Object deserialize(String str) {
		String[] items = str.split("\n");
		Object[] data = new Object[items.length];
		for (int i = 0; i < items.length; i++) {
			data[i] = deserializeItem(items[i]);
		}
		return data;
	}

	protected Object deserializeItem(String str) {
		return str;
	}

	protected String serializeItem(Object item) {
		if (item != null) {
			return item.toString();
		}
		return null;
	}

}
