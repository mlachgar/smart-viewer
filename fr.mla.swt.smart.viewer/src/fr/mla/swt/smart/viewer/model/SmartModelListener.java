package fr.mla.swt.smart.viewer.model;

public interface SmartModelListener {
	
	public void modelChanged(Object source);

	public void itemModified(Object source, Object item);
	
}
