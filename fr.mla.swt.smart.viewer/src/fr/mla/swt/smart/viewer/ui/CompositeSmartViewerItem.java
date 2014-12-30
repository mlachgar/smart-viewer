package fr.mla.swt.smart.viewer.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeSmartViewerItem<T, E> extends SmartViewerItem<T> {

	private final List<SmartViewerItem<E>> children = new ArrayList<SmartViewerItem<E>>();

	public CompositeSmartViewerItem() {

	}

	public CompositeSmartViewerItem(T data, int index) {
		super(data, index);
	}

	public boolean addChild(SmartViewerItem<E> child) {
		return children.add(child);
	}

	public boolean removeChild(SmartViewerItem<E> child) {
		return children.remove(child);
	}

	public boolean clearChildren() {
		boolean empty = children.isEmpty();
		children.clear();
		return !empty;
	}

	public void setChildren(List<SmartViewerItem<E>> children) {
		this.children.clear();
		this.children.addAll(children);
	}

	public List<SmartViewerItem<E>> getChildren() {
		return Collections.unmodifiableList(children);
	}
}
