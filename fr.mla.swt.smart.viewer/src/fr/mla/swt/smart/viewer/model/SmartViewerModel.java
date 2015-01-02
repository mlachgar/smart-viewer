package fr.mla.swt.smart.viewer.model;

import java.util.List;

/**
 * @author mlachgar
 */
public interface SmartViewerModel {

	public void addListChangeListener(SmartModelListener l);

	public void removeListChangeListener(SmartModelListener l);

	public List<?> getItems();

	public List<?> getChildren(Object parent);

}
