package fr.mla.swt.smart.viewer.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSmartViewerModel implements SmartViewerModel {
	private List<SmartModelListener> modelListeners = new ArrayList<SmartModelListener>();

	@Override
	public void addListChangeListener(SmartModelListener l) {
		modelListeners.add(l);
	}

	@Override
	public void removeListChangeListener(SmartModelListener l) {
		modelListeners.remove(l);
	}

	protected void fireModelChange() {
		for (int i = modelListeners.size() - 1; i >= 0; i--) {
			modelListeners.get(i).modelChanged(this);
		}
	}

}
