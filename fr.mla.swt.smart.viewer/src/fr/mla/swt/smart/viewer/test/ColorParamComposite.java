package fr.mla.swt.smart.viewer.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

public class ColorParamComposite extends Composite {

	private Scale minScale;
	private Scale maxScale;
	private Label label;
	private String title;

	public ColorParamComposite(Composite parent, int style, final String title) {
		super(parent, style);
		this.title = title;
		setLayout(new GridLayout(1, true));

		label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		minScale = new Scale(this, SWT.HORIZONTAL);
		minScale.setMinimum(0);
		minScale.setMaximum(100);

		maxScale = new Scale(this, SWT.HORIZONTAL);
		maxScale.setMinimum(0);
		maxScale.setMaximum(100);

		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshLabel();
			}
		};
		minScale.addSelectionListener(listener);
		maxScale.addSelectionListener(listener);

		refreshLabel();
	}

	private void refreshLabel() {
		label.setText(String.format("%s (%.2f)(%.2f)", title, getMinValue(), getMaxValue()));
	}

	public int getMax() {
		return maxScale.getSelection();
	}

	public int getMin() {
		return minScale.getSelection();
	}

	public float getMaxValue() {
		return maxScale.getSelection() * 1f / 100;
	}

	public float getMinValue() {
		return minScale.getSelection() * 1f / 100;
	}

	public void setRange(float min, float max) {
		minScale.setSelection((int) (min * 100));
		maxScale.setSelection((int) (max * 100));
	}

}
