package fr.mla.swt.smart.viewer.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class SmartViewerItem {

	private Rectangle bounds = new Rectangle(0, 0, 0, 0);
	private Point absoluteLocation = new Point(0, 0);
	private Object data;
	private int index = 0;
	private int depth = 0;
	private boolean selected;
	private final Map<Object, Object> dataMap = new HashMap<>();
	private final List<SmartViewerItem> children = new ArrayList<SmartViewerItem>();
	private final List<SmartViewerAction> toolbarActions = new ArrayList<>();

	public SmartViewerItem() {

	}

	public SmartViewerItem(Object data, int index, int depth) {
		this.data = data;
		this.index = index;
		this.depth = depth;
	}

	public void setData(Object data, int index) {
		this.data = data;
		this.index = index;
	}

	public void setBounds(int x, int y, int width, int height) {
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;
	}

	public void setAbsoluteLocation(int x, int y) {
		absoluteLocation.x = x;
		absoluteLocation.y = y;
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

	public int getAbsoluteX() {
		return absoluteLocation.x;
	}

	public int getAbsoluteY() {
		return absoluteLocation.y;
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

	public Object getData() {
		return data;
	}

	public void putExtraData(Object key, Object data) {
		dataMap.put(key, data);
	}

	public void clearExtraData() {
		dataMap.clear();
	}

	public Object getExtraData(Object key) {
		return dataMap.get(key);
	}

	public Set<Object> getExtraDataKeys() {
		return dataMap.keySet();
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public boolean contains(int x, int y) {
		return bounds.contains(x, y);
	}

	public boolean intersects(int x, int y, int width, int height) {
		return (x < absoluteLocation.x + bounds.width)
				&& (y < absoluteLocation.y + bounds.height)
				&& (x + width > absoluteLocation.x)
				&& (y + height > absoluteLocation.y);
	}

	public boolean addChild(SmartViewerItem child) {
		return children.add(child);
	}

	public boolean removeChild(SmartViewerItem child) {
		return children.remove(child);
	}

	public boolean clearChildren() {
		boolean empty = children.isEmpty();
		children.clear();
		return !empty;
	}

	public void setChildren(List<SmartViewerItem> children) {
		this.children.clear();
		this.children.addAll(children);
	}

	public List<SmartViewerItem> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public List<SmartViewerAction> getToolbarActions() {
		return toolbarActions;
	}

	public void setToolbarActions(List<SmartViewerAction> actions) {
		toolbarActions.clear();
		if (actions != null) {
			toolbarActions.addAll(actions);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SmartViewerItem other = (SmartViewerItem) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		return true;
	}

}
