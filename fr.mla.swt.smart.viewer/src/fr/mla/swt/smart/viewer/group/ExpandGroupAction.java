package fr.mla.swt.smart.viewer.group;

import fr.mla.swt.smart.viewer.ui.SmartViewerAction;

public class ExpandGroupAction extends SmartViewerAction {

	private DataGroup group;

	public ExpandGroupAction(DataGroup group) {
		this.group = group;
		update();
	}

	private void update() {
		if (group.isExpanded()) {

		} else {

		}
	}
}
