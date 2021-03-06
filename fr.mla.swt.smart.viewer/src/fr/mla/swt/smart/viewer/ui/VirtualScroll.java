package fr.mla.swt.smart.viewer.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import fr.mla.swt.smart.viewer.Activator;
import fr.mla.swt.smart.viewer.model.OrientationType;

public abstract class VirtualScroll implements Listener {

	private int value = 0;
	private int max = 0;
	private int thumb = 5;
	private Rectangle bounds = new Rectangle(0, 0, 0, 0);
	private Rectangle thumbBounds = new Rectangle(0, 0, 0, 0);
	private Rectangle prevArrowBounds = new Rectangle(0, 0, 0, 0);
	private Rectangle nextArrowBounds = new Rectangle(0, 0, 0, 0);
	private Rectangle startArrowBounds = new Rectangle(0, 0, 0, 0);
	private Rectangle endArrowBounds = new Rectangle(0, 0, 0, 0);
	protected final OrientationType type;
	private Point clickedPoint;
	private int clickedValue;
	private Image prevImage;
	private Image nextImage;

	private Image startImage;
	private Image endImage;

	private Color backgroundColor;
	private Color borderColor;
	private Color thumbColor;

	private Composite parent;

	private final MouseClickTimer clickTimer;

	public VirtualScroll(Composite parent, OrientationType type) {
		this.parent = parent;
		this.type = type;
		// backgroundColor =
		// parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
		thumbColor = parent.getDisplay().getSystemColor(SWT.COLOR_GRAY);
		borderColor = parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
		if (type == OrientationType.HORIZONTAL) {
			prevImage = Activator.getImage("round_arrow_left.png");
			nextImage = Activator.getImage("round_arrow_right.png");
			startImage = Activator.getImage("round_arrow_left_start.png");
			endImage = Activator.getImage("round_arrow_right_end.png");
		} else {
			prevImage = Activator.getImage("round_arrow_up.png");
			nextImage = Activator.getImage("round_arrow_down.png");
			startImage = Activator.getImage("round_arrow_up_start.png");
			endImage = Activator.getImage("round_arrow_down_end.png");
		}
		clickTimer = new MouseClickTimer(parent.getDisplay(), 300, 100, 50, 10) {

			@Override
			protected void changed(Event e) {
				if (prevArrowBounds.contains(e.x, e.y)) {
					previous();
				} else if (nextArrowBounds.contains(e.x, e.y)) {
					next();
				} else {
					stop();
				}
			}
		};
	}

	public boolean isVisible() {
		return bounds.width > 0 && bounds.height > 0;
	}

	public void setSize(int width, int height) {
		this.bounds.width = width;
		this.bounds.height = height;
		computeArrowsBounds();
		computeThumbLocation();
	}

	public void setLocation(int x, int y) {
		this.bounds.x = x;
		this.bounds.y = y;
		computeArrowsBounds();
		computeThumbLocation();
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void draw(GC gc, int dx, int dy) {
		Color bg = gc.getBackground();
		Color fg = gc.getForeground();
		try {
			if (backgroundColor != null) {
				gc.setBackground(backgroundColor);
				gc.fillRoundRectangle(bounds.x - dx, bounds.y - dy, bounds.width, bounds.height, 10, 10);
			}
			if (borderColor != null) {
				gc.setForeground(borderColor);
				gc.drawRectangle(bounds.x - dx, bounds.y - dy, bounds.width - 1, bounds.height - 1);
			}
			gc.setBackground(thumbColor);
			if (type == OrientationType.HORIZONTAL) {
				gc.fillRoundRectangle(thumbBounds.x - dx, thumbBounds.y - dy, thumbBounds.width, thumbBounds.height,
						10, 10);
			} else {
				gc.fillRoundRectangle(thumbBounds.x - dx, thumbBounds.y - dy, thumbBounds.width, thumbBounds.height,
						10, 10);
			}
			drawImage(gc, startImage, startArrowBounds, dx, dy);
			drawImage(gc, prevImage, prevArrowBounds, dx, dy);
			drawImage(gc, nextImage, nextArrowBounds, dx, dy);
			drawImage(gc, endImage, endArrowBounds, dx, dy);
		} finally {
			gc.setBackground(bg);
			gc.setForeground(fg);
		}
	}

	private void computeArrowsBounds() {
		int startX = bounds.x;
		int startY = bounds.y;
		if (type == OrientationType.HORIZONTAL) {
			prevArrowBounds.width = bounds.height;
			prevArrowBounds.height = bounds.height;
			nextArrowBounds.width = bounds.height;
			nextArrowBounds.height = bounds.height;
			startArrowBounds.width = bounds.height;
			startArrowBounds.height = bounds.height;
			endArrowBounds.width = bounds.height;
			endArrowBounds.height = bounds.height;

			startArrowBounds.x = startX;
			endArrowBounds.x = startX + bounds.width - bounds.height;
			prevArrowBounds.x = startArrowBounds.x + startArrowBounds.width;
			nextArrowBounds.x = startX + endArrowBounds.x - nextArrowBounds.width;

			startArrowBounds.y = startY;
			endArrowBounds.y = startY;
			prevArrowBounds.y = startY;
			nextArrowBounds.y = startY;
		} else {
			prevArrowBounds.width = bounds.width;
			prevArrowBounds.height = bounds.width;
			nextArrowBounds.width = bounds.width;
			nextArrowBounds.height = bounds.width;
			startArrowBounds.width = bounds.width;
			startArrowBounds.height = bounds.width;
			endArrowBounds.width = bounds.width;
			endArrowBounds.height = bounds.width;

			startArrowBounds.y = startY;
			endArrowBounds.y = startY + bounds.height - bounds.width;
			prevArrowBounds.y = startArrowBounds.y + startArrowBounds.height;
			nextArrowBounds.y = startY + endArrowBounds.y - nextArrowBounds.height;

			startArrowBounds.x = startX;
			endArrowBounds.x = startX;
			prevArrowBounds.x = startX;
			nextArrowBounds.x = startX;
		}
	}

	private void drawImage(GC gc, Image image, Rectangle bounds, int dx, int dy) {
		gc.drawImage(image, 0, 0, 16, 16, bounds.x - dx, bounds.y - dy, bounds.width, bounds.height);
	}

	public int getThumb() {
		return thumb;
	}

	public int getThumbSize() {
		return type == OrientationType.HORIZONTAL ? thumbBounds.width : thumbBounds.height;
	}

	public Rectangle getThumbBounds() {
		return thumbBounds;
	}

	public void setBackground(Color color) {
		backgroundColor = color;
	}

	public void setMax(int max) {
		if (max > 0) {
			this.max = max;
			int visibleSize = type == OrientationType.HORIZONTAL ? bounds.width : bounds.height;
			this.thumb = visibleSize * visibleSize / max;
			if (thumb == 0) {
				thumb = 1;
			}
			computeThumbSize();
			computeThumbLocation();
		}
	}

	public int getMax() {
		return max;
	}

	public int getMaxValue() {
		int visibleSize = type == OrientationType.HORIZONTAL ? bounds.width : bounds.height;
		return max - visibleSize;
	}

	public void setValue(int value) {
		int v = value;
		int visibleSize = type == OrientationType.HORIZONTAL ? bounds.width : bounds.height;
		if (visibleSize > max) {
			v = 0;
		} else if (v < 0) {
			v = 0;
		} else if (v > max - visibleSize) {
			v = max - visibleSize;
		}
		if (this.value != v) {
			this.value = v;
			computeThumbLocation();
		}
	}

	private void computeThumbLocation() {
		if (max > 0) {
			if (type == OrientationType.HORIZONTAL) {
				thumbBounds.x = (bounds.width > 4 * bounds.height) ? (value * (bounds.width - 4 * bounds.height) / max)
						+ 2 * bounds.height : 2 * bounds.height;
				thumbBounds.y = 1;
			} else {
				thumbBounds.x = 1;
				thumbBounds.y = (bounds.height > 4 * bounds.width) ? (value * (bounds.height - 4 * bounds.width) / max)
						+ 2 * bounds.width : 2 * bounds.width;
			}
			thumbBounds.x += bounds.x;
			thumbBounds.y += bounds.y;
		}
	}

	private void computeThumbSize() {
		if (type == OrientationType.HORIZONTAL) {
			thumbBounds.width = bounds.width > 0 ? Math.max(5, thumb * (bounds.width - 4 * bounds.height)
					/ bounds.width) : 0;
			thumbBounds.height = bounds.height - 2;
		} else {
			thumbBounds.height = bounds.height > 0 ? Math.max(5, thumb * (bounds.height - 4 * bounds.width)
					/ bounds.height) : 0;
			thumbBounds.width = bounds.width - 2;
		}
	}

	public void scrollTo(Rectangle bounds, Point spacing) {
		scrollTo(bounds.x, bounds.y, bounds.width, bounds.height, spacing);
	}

	public void scrollTo(int x, int y, int width, int height, Point spacing) {
		if (type == OrientationType.HORIZONTAL) {
			if (value + width < x + width) {
				value += (x + width) - (value + width) + spacing.x;
				computeThumbLocation();
				scrolled();
			} else if (x < value) {
				value = x - spacing.x;
				computeThumbLocation();
				scrolled();
			}
		} else {
			if (value + bounds.height < y + height) {
				value += (y + height) - (value + bounds.height) + spacing.y;
				computeThumbLocation();
				scrolled();
			} else if (y < value) {
				value = y - spacing.y;
				computeThumbLocation();
				scrolled();
			}
		}
	}

	public void goToEnd() {
		setValue(max);
		scrolled();
	}

	public void scrollIn(int shift) {
		if (shift != 0) {
			int visibleSize = type == OrientationType.HORIZONTAL ? bounds.width - 4 * bounds.height : bounds.height - 4
					* bounds.width;
			int v = this.value + (shift * max / visibleSize);
			setValue(v);
		}
	}

	public void relativeScrollIn(int originValue, int shift) {
		if (shift != 0) {
			int visibleSize = type == OrientationType.HORIZONTAL ? bounds.width - 4 * bounds.height : bounds.height - 4
					* bounds.width;
			int v = originValue + (shift * max / visibleSize);
			setValue(v);
		}
	}

	public void fastScroll(int shift) {
		if (shift > 0 && this.value == getMaxValue()) {
			return;
		}
		if (shift != 0) {
			setValue(this.value + shift);
		}
	}

	protected void scrolled() {

	}

	abstract protected void previous();

	abstract protected void next();

	protected void nextPage() {

	}

	protected void previousPage() {

	}

	public int getValue() {
		return value;
	}

	public void mouseScrolled(Event e) {
		scrollIn(-e.count * 3);
		scrolled();
	}

	public void mouseDown(Event e) {
		if (thumbBounds.contains(e.x, e.y)) {
			clickedPoint = new Point(e.x, e.y);
			e.doit = false;
			clickedValue = value;
		}
		if (prevArrowBounds.contains(e.x, e.y) || nextArrowBounds.contains(e.x, e.y)) {
			clickTimer.start(e);
			e.doit = false;
		}
	}

	public void mouseUp(Event e) {
		if (prevArrowBounds.contains(e.x, e.y)) {
			previous();
			e.doit = false;
		} else if (nextArrowBounds.contains(e.x, e.y)) {
			next();
			e.doit = false;
		} else if (startArrowBounds.contains(e.x, e.y)) {
			resetScroll();
			e.doit = false;
		} else if (endArrowBounds.contains(e.x, e.y)) {
			goToEnd();
			e.doit = false;
		} else if (clickedPoint == null && bounds.contains(e.x, e.y)) {
			if (type == OrientationType.HORIZONTAL) {
				if (e.x < thumbBounds.x) {
					previousPage();
				} else if (e.x > thumbBounds.x + thumbBounds.width) {
					nextPage();
				}
			} else {
				if (e.y < thumbBounds.y) {
					previousPage();
				} else if (e.y > thumbBounds.y + thumbBounds.height) {
					nextPage();
				}
			}
			scrolled();
			e.doit = false;
		}
		if (showHand(e)) {
			parent.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
			e.doit = false;
		} else {
			parent.setCursor(null);
		}
		if (clickedPoint != null) {
			clickedPoint = null;
			clickedValue = -1;
			e.doit = false;
		}
		if (clickTimer.isStarted()) {
			clickTimer.stop();
			e.doit = false;
		}
	}

	public void mouseMove(Event e) {
		if (parent.isDisposed()) {
			return;
		}
		if (!prevArrowBounds.contains(e.x, e.y) && !nextArrowBounds.contains(e.x, e.y)) {
			clickTimer.stop();
		}
		if (clickedPoint != null) {
			int d = 0;
			if (type == OrientationType.HORIZONTAL) {
				d = e.x - clickedPoint.x;
			} else {
				d = e.y - clickedPoint.y;
			}
			relativeScrollIn(clickedValue, d);
			scrolled();
		}
		if (showHand(e)) {
			parent.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		} else {
			parent.setCursor(null);
		}
	}

	public int getWidth() {
		return bounds.width;
	}

	public int getHeight() {
		return bounds.height;
	}

	public void mouseEnter(Event e) {
		if (parent.isDisposed()) {
			return;
		}
		if (showHand(e)) {
			parent.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		} else {
			parent.setCursor(null);
		}
	}

	private boolean showHand(Event e) {
		return thumbBounds.contains(e.x, e.y) || prevArrowBounds.contains(e.x, e.y)
				|| nextArrowBounds.contains(e.x, e.y) || startArrowBounds.contains(e.x, e.y)
				|| endArrowBounds.contains(e.x, e.y);
	}

	public void mouseExit(Event e, boolean reset) {
		if (parent.isDisposed()) {
			return;
		}
		parent.setCursor(null);
		clickTimer.stop();
		if (reset) {
			clickedPoint = null;
		}
	}

	public void resetScroll() {
		setValue(0);
		if (!parent.isDisposed()) {
			scrolled();
		}
	}

	public void hide() {
		bounds.width = 0;
		bounds.height = 0;
		setMax(0);
		setValue(0);
	}

	@Override
	public void handleEvent(Event e) {
		switch (e.type) {
		case SWT.MouseUp:
			mouseUp(e);
			break;
		case SWT.MouseDown:
			mouseDown(e);
			break;
		case SWT.MouseEnter:
			mouseEnter(e);
			break;
		case SWT.MouseExit:
			mouseExit(e, true);
			break;
		case SWT.MouseMove:
			mouseMove(e);
			break;
		case SWT.MouseWheel:
			mouseScrolled(e);
			break;
		}
	}
}
