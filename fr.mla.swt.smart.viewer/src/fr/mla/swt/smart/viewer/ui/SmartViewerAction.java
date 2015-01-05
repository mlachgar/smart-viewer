package fr.mla.swt.smart.viewer.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

public class SmartViewerAction {

	private String text;
	private Image image;
	private boolean visible = true;

	public SmartViewerAction() {

	}

	public SmartViewerAction(String text, Image image) {
		this.text = text;
		this.image = image;
	}

	public void run(Event event, SmartViewerItem item) {

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public String toString() {
		return getText();
	}

}
