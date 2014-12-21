package fr.mla.swt.smart.viewer.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Rectangle;

public class SmartViewerItem<T> {

	private Rectangle bounds = new Rectangle(0, 0, 0, 0);
	private T data;
	private int index = 0;
	private boolean selected;
	private final Map<String, Object> dataMap = new HashMap<>();

	public SmartViewerItem() {

	}

	public void setData(T data, int index) {
		this.data = data;
		this.index = index;
	}

	public void setBounds(int x, int y, int width, int height) {
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;
	}

	public int getX() {
		return bounds.x;
	}

	public int getY() {
		return bounds.y;
	}

	public int getWidth() {
		return bounds.width;
	}

	public int getHeight() {
		return bounds.height;
	}

	public void setSize(int width, int height) {
		bounds.width = width;
		bounds.height = height;
	}

	public void setLocation(int x, int y) {
		bounds.x = x;
		bounds.y = y;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public T getData() {
		return data;
	}

	public void putExtraData(String key, Object data) {
		dataMap.put(key, data);
	}

	public Object getExtraData(String key) {
		return dataMap.get(key);
	}

	public Rectangle getBounds() {
		return bounds;
	}

}
