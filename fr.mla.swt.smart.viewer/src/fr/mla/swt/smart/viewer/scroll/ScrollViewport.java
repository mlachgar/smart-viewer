package fr.mla.swt.smart.viewer.scroll;

import org.eclipse.swt.graphics.Rectangle;

public class ScrollViewport {
	public final int hScroll;
	public final int vScroll;
	public final Rectangle clientArea;

	public ScrollViewport(Rectangle clientArea, int hScroll, int vScroll) {
		this.clientArea = clientArea;
		this.hScroll = hScroll;
		this.vScroll = vScroll;
	}

}
