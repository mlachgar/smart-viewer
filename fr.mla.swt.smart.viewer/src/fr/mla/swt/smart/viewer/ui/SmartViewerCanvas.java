package fr.mla.swt.smart.viewer.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import fr.mla.swt.smart.viewer.layout.MosaicLayout;
import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.ListModel;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.model.SmartModelListener;
import fr.mla.swt.smart.viewer.renderer.DefaultObjectRenderer;
import fr.mla.swt.smart.viewer.renderer.SmartViewerRenderer;
import fr.mla.swt.smart.viewer.scroll.GridScrollManager;
import fr.mla.swt.smart.viewer.scroll.ScrollManager;

public class SmartViewerCanvas<T> extends Canvas {

	private static final int BAR_SIZE = 16;

	private ListModel<T> model;
	private final List<T> dataList = new ArrayList<>();
	private final List<SmartViewerItem<T>> items = new ArrayList<>();
	private SmartModelListener<T> modelListener;

	private InternalScroll hScroll;
	private InternalScroll vScroll;
	private SmartViewerRenderer<T> renderer = new DefaultObjectRenderer<T>();
	private SmartViewerLayout<T> layout = new MosaicLayout<>();
	private ScrollManager<T> scrollManager = new GridScrollManager<T>();

	public SmartViewerCanvas(Composite parent, int style) {
		super(parent, style);
		initScrolls();
		setUpModelListener();
		setUpPaintListener();
		setUpControlListener(parent);
		setUpMouseListener();
	}

	private void initScrolls() {
		vScroll = new InternalScroll(OrientationType.VERTICAL);

		hScroll = new InternalScroll(OrientationType.HORIZONTAL);
	}

	private void setUpPaintListener() {
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				int xScroll = hScroll.isVisible() ? hScroll.getValue() : 0;
				int yScroll = vScroll.isVisible() ? vScroll.getValue() : 0;
				final Rectangle paintBounds = new Rectangle(e.x, e.y, e.width, e.height);
				final Image img = new Image(getDisplay(), new Rectangle(0, 0, e.width, e.height));
				final GC gc = new GC(img);
				try {
					renderer.renderItems(gc, paintBounds, new Point(xScroll, yScroll), items);
					if (hScroll.isVisible()) {
						hScroll.draw(gc, e.x, e.y);
					}
					if (vScroll.isVisible()) {
						vScroll.draw(gc, e.x, e.y);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					e.gc.drawImage(img, 0, 0);
					img.dispose();
					gc.dispose();
				}
			}
		});
	}

	private void setUpModelListener() {
		modelListener = new SmartModelListener<T>() {

			@Override
			public void listChanged(Object source) {
				dataList.clear();
				dataList.addAll(model.getItems());
				updateScrolls();
				refresh();
			}

			@Override
			public void itemModified(Object source, T data) {
				SmartViewerItem<T> item = findItem(data);
				if (item != null) {
					redraw(item);
				}
			}

		};
	}

	private void setUpControlListener(Composite parent) {
		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				refresh();
			}
		});
	}

	private void setUpMouseListener() {
		Listener listener = new Listener() {

			@Override
			public void handleEvent(Event e) {
				if (hScroll.isVisible()) {
					if (hScroll.getBounds().contains(e.x, e.y)) {
						hScroll.handleEvent(e);
						return;
					} else {
						hScroll.mouseExit(e);
					}
				} else if (vScroll.isVisible()) {
					if (vScroll.getBounds().contains(e.x, e.y)) {
						vScroll.handleEvent(e);
						return;
					} else {
						vScroll.mouseExit(e);
					}
				}
				switch (e.type) {
				case SWT.MouseUp:

					break;
				case SWT.MouseDown:

					break;
				case SWT.MouseEnter:

					break;
				case SWT.MouseExit:
					break;
				case SWT.MouseMove:
					break;
				case SWT.MouseWheel:
					if (vScroll.isVisible()) {
						vScroll.mouseScrolled(e);
					}
					if (hScroll.isVisible()) {
						hScroll.mouseScrolled(e);
					}
					break;
				}
			}
		};
		addListener(SWT.MouseUp, listener);
		addListener(SWT.MouseDown, listener);
		addListener(SWT.MouseMove, listener);
		addListener(SWT.MouseEnter, listener);
		addListener(SWT.MouseExit, listener);
		addListener(SWT.MouseDoubleClick, listener);
		addListener(SWT.MouseWheel, listener);
	}

	public void setModel(ListModel<T> model) {
		if (this.model != null) {
			this.model.removeListChangeListener(modelListener);
		}
		this.model = model;
		dataList.clear();
		if (model != null) {
			model.addListChangeListener(modelListener);
			dataList.addAll(model.getItems());
		}
		refresh();
	}

	public void setRenderer(SmartViewerRenderer<T> renderer) {
		this.renderer = renderer;
	}

	public void setLayout(SmartViewerLayout<T> layout) {
		this.layout = layout;
	}

	public void updateScrolls() {
		Rectangle area = super.getClientArea();
		if (!dataList.isEmpty()) {
			Point neededSize = layout.getNeededSize(area.width, area.height, dataList);
			System.err.println(neededSize);
			if (neededSize.x > area.width) {
				hScroll.setSize(area.width, BAR_SIZE);
				hScroll.setMax(neededSize.x);
				hScroll.setLocation(0, area.height - BAR_SIZE);
			} else {
				hScroll.hide();
			}
			if (neededSize.y > area.height) {
				vScroll.setSize(BAR_SIZE, area.height);
				vScroll.setMax(neededSize.y);
				vScroll.setLocation(area.width - BAR_SIZE, 0);
			} else {
				vScroll.hide();
			}
		} else {
			hScroll.hide();
			vScroll.hide();
		}
	}

	@Override
	public Rectangle getClientArea() {
		Rectangle area = super.getClientArea();
		if (vScroll.isVisible()) {
			area.width -= vScroll.getWidth();
		}
		if (hScroll.isVisible()) {
			area.height -= hScroll.getHeight();
		}
		return area;
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		final Point p = layout.getPreferredSize(dataList);
		final int hBar = hScroll.isVisible() ? BAR_SIZE : 1;
		final int vBar = vScroll.isVisible() ? BAR_SIZE : 1;
		if (p.x != SWT.DEFAULT) {
			p.x += vBar;
		}
		if (p.y != SWT.DEFAULT) {
			p.y += hBar;
		}
		return p;
	}

	public void refresh() {
		int modelSize = dataList.size();
		int firstIndex = scrollManager.getStartModelIndex(getClientArea(), hScroll.getValue(), vScroll.getValue(),
				layout, items, dataList);
		checkItems(modelSize, firstIndex);
		updateScrolls();
		if (modelSize > 0) {
			scrollManager.applyScrollFromIndex(firstIndex, items, dataList);
			Rectangle area = getClientArea();
			layout.layoutItems(area.width - vScroll.getWidth(), area.height - hScroll.getHeight(), items);
		}
		redrawCanvas();
	}

	private void checkItems(int modelSize, int firstIndex) {
		Rectangle area = getClientArea();
		int maxItems = layout.getMaxItemsCount(area.width, area.height, firstIndex, dataList);
		int max = Math.min(maxItems, modelSize);
		if (items.size() < max) {
			for (int i = items.size(); i < max; i++) {
				items.add(new SmartViewerItem<>());
			}
		} else {
			Iterator<SmartViewerItem<T>> it = items.iterator();
			while (items.size() > max) {
				it.remove();
			}
		}
	}

	private void redrawCanvas() {
		redraw();
		update();
	}

	public void redraw(final SmartViewerItem<T> item) {
		if (!isDisposed()) {
			redraw(item.getX() - hScroll.getValue(), item.getY() - vScroll.getValue(), item.getWidth(),
					item.getHeight(), false);
			update();
		}
	}

	public boolean isVisibleListFilled() {
		boolean filled = false;
		if (!isDisposed()) {
			filled = layout.isBoundsFilled(getBounds(), dataList);
		}
		return filled;
	}

	public SmartViewerItem<T> findItem(final T data) {
		if (data != null) {
			for (final SmartViewerItem<T> item : items) {
				if (data.equals(item.getData())) {
					return item;
				}
			}
		}
		return null;
	}

	public Rectangle getItemBounds(final SmartViewerItem<T> item) {
		int x = item.getX();
		int y = item.getY();
		if (hScroll.isVisible()) {
			x -= hScroll.getValue();
		}
		if (vScroll.isVisible()) {
			y -= vScroll.getValue();
		}
		return new Rectangle(x, y, item.getWidth(), item.getHeight());
	}

	private class InternalScroll extends VirtualScroll {

		public InternalScroll(OrientationType type) {
			super(SmartViewerCanvas.this, type);
		}

		@Override
		protected void previous() {
			fastScroll(scrollManager.previous(getClientArea(), type, layout, items, dataList));
			scrolled();
		}

		@Override
		protected void next() {
			fastScroll(scrollManager.next(getClientArea(), type, layout, items, dataList));
			scrolled();
		}

		@Override
		protected void previousPage() {
			fastScroll(scrollManager.previousPage(getClientArea(), type, layout, items, dataList));
			scrolled();
		}

		@Override
		protected void nextPage() {
			fastScroll(scrollManager.nextPage(getClientArea(), type, layout, items, dataList));
			scrolled();
		}

		@Override
		protected void scrolled() {
			scrollManager.applyScroll(getClientArea(), hScroll.getValue(), vScroll.getValue(), layout, items, dataList);
			redrawCanvas();
		}

	}
}
