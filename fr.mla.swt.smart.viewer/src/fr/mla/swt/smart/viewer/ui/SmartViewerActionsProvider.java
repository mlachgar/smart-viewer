package fr.mla.swt.smart.viewer.ui;

import java.util.List;

public interface SmartViewerActionsProvider {
	public List<SmartViewerAction> getToolbarActions(Object data);
}
