package fr.mla.swt.smart.viewer.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

import fr.mla.swt.smart.viewer.renderer.SmartViewerRenderer;

public interface SmartViewer {

	public Rectangle getDrawArea();

	public Point getScrollValues();

	public Control getControl();

	public SmartViewerRenderer getRenderer();

}