package fr.mla.swt.smart.viewer.ui;

import java.util.ArrayList;
import java.util.Collection;
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

import fr.mla.swt.smart.viewer.layout.SmartGridLayout;
import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.DirectionType;
import fr.mla.swt.smart.viewer.model.ListModel;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.model.SmartModelListener;
import fr.mla.swt.smart.viewer.renderer.DefaultObjectRenderer;
import fr.mla.swt.smart.viewer.renderer.SmartViewerRenderer;
import fr.mla.swt.smart.viewer.scroll.DefaultScrollManager;
import fr.mla.swt.smart.viewer.scroll.ScrollManager;
import fr.mla.swt.smart.viewer.scroll.ScrollViewport;

public class SmartViewer<T> extends Canvas {

	private static final int BAR_SIZE = 16;

	private ListModel<T> model;
	private final List<T> dataList = new ArrayList<>();
	private SmartModelListener<T> modelListener;
	private SmartViewerItemsFactory<T> itemsFactory = new DefaultViewerItemsFactory<>();
	private InternalScroll hScroll;
	private InternalScroll vScroll;
	private SmartViewerRenderer<T> renderer = new DefaultObjectRenderer<T>();
	private SmartViewerLayout<T> layout = new SmartGridLayout<>(-1, 2);
	private ScrollManager<T> scrollManager = new DefaultScrollManager<T>();
	private SelectionManager<T> selectionManager = new DefaultSelectionManager<>();
	private final List<SmartViewerSelectionListener<T>> selectionListeners = new ArrayList<>();

	protected final List<SmartViewerItem<T>> items = new ArrayList<>();

	public SmartViewer(Composite parent, int style) {
		super(parent, style);

		vScroll = new InternalScroll(OrientationType.VERTICAL);
		hScroll = new InternalScroll(OrientationType.HORIZONTAL);

		setUpModelListener();
		setUpPaintListener();
		setUpControlListener(parent);
		setUpMouseListener();
		setUpKeyListener();
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
					hScroll.handleEvent(e);
				}
				if (vScroll.isVisible()) {
					vScroll.handleEvent(e);
				}
				if (e.doit) {
					switch (e.type) {
					case SWT.MouseUp:

						break;
					case SWT.MouseDown:
						handleMouseDown(e);
						break;
					case SWT.MouseEnter:

						break;
					case SWT.MouseExit:
						if (hScroll.isVisible()) {
							hScroll.mouseExit(e, true);
						}
						if (vScroll.isVisible()) {
							vScroll.mouseExit(e, true);
						}
						break;
					case SWT.MouseMove:

						break;
					case SWT.MouseWheel:
						break;
					}
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

	private void setUpKeyListener() {
		Listener listener = new Listener() {

			@Override
			public void handleEvent(Event e) {
				if (e.doit) {
					if (e.type == SWT.KeyDown) {
						handleKeyPressed(e);
					} else if (e.type == SWT.KeyUp) {
						handleKeyReleased(e);
					}
				}
			}
		};

		addListener(SWT.KeyUp, listener);
		addListener(SWT.KeyDown, listener);
	}

	protected void handleKeyReleased(final Event e) {

	}

	protected void handleKeyPressed(final Event e) {
		if (!e.doit) {
			return;
		}
		int index = getLastSelectedIndex();
		if (index == -1) {
			if (items.isEmpty()) {
				return;
			} else {
				index = 0;
			}
		}
		final boolean shiftMask = (e.stateMask & SWT.SHIFT) != 0;
		int nextIndex = -1;
		switch (e.keyCode) {
		case SWT.ARROW_LEFT:
			if (layout.isNavigable(OrientationType.HORIZONTAL)) {
				nextIndex = layout.getNeighborItem(index, DirectionType.LEFT, items);
			}
			break;
		case SWT.ARROW_RIGHT:
			if (layout.isNavigable(OrientationType.HORIZONTAL)) {
				nextIndex = layout.getNeighborItem(index, DirectionType.RIGHT, items);
			}
			break;
		case SWT.ARROW_UP:
			if (layout.isNavigable(OrientationType.VERTICAL)) {
				nextIndex = layout.getNeighborItem(index, DirectionType.UP, items);
			}
			break;
		case SWT.ARROW_DOWN:
			if (layout.isNavigable(OrientationType.VERTICAL)) {
				nextIndex = layout.getNeighborItem(index, DirectionType.DOWN, items);
			}
			break;
		case SWT.PAGE_DOWN:
			if (hScroll.isVisible()) {
				hScroll.nextPage();
			} else if (vScroll.isVisible()) {
				vScroll.nextPage();
			}
			break;
		case SWT.PAGE_UP:
			if (hScroll.isVisible()) {
				hScroll.previousPage();
			} else if (vScroll.isVisible()) {
				vScroll.previousPage();
			}
			break;
		}
		if (nextIndex >= 0 && nextIndex < items.size()) {
			SmartViewerItem<T> nextItem = items.get(nextIndex);
			if (shiftMask) {
				if (selectionManager.appendToSelection(nextItem, true)) {
					showControl(nextItem);
					fireSelectionChanged();
				}
			} else {
				if (selectionManager.selectOnly(items, nextItem, true)) {
					showControl(nextItem);
					fireSelectionChanged();
				}
			}
		}
	}

	private void handleMouseDown(Event e) {
		final SmartViewerItem<T> item = getItemAt(e.x, e.y);
		final boolean dragDetect = dragDetect(e);
		if (item != null) {
			if (isLeftClick(e)) {
				if ((e.stateMask & SWT.CTRL) != 0) {
					if (selectionManager.appendToSelection(item, !dragDetect)) {
						fireSelectionChanged();
					}
				} else if ((e.stateMask & SWT.SHIFT) != 0) {
					if (selectionManager.expandSelectionTo(items, item)) {
						fireSelectionChanged();
					}
				} else {
					if (selectionManager.selectOnly(items, item, !dragDetect)) {
						fireSelectionChanged();
					}
				}
			} else if ((e.stateMask & SWT.CTRL) == 0) {
				if (selectionManager.selectOnly(items, item, !dragDetect)) {
					fireSelectionChanged();
				}
			}
		} else if (isLeftClick(e) || selectionManager.getSelectedData().isEmpty()) {
			selectionManager.clearSelection(items, null);
			fireSelectionChanged();
		}
		setFocus();
	}

	private int getLastSelectedIndex() {
		Collection<T> data = selectionManager.getSelectedData();
		if (!data.isEmpty()) {
			T last = null;
			for (final T d : data) {
				last = d;
			}
			return model.indexOf(last);
		}
		return -1;
	}

	protected boolean isLeftClick(final Event e) {
		return e.button == 1;
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
		if (renderer != null) {
			this.renderer = renderer;
		}
	}

	public void setLayout(SmartViewerLayout<T> layout) {
		if (layout != null) {
			this.layout = layout;
		} else {
			layout = new SmartGridLayout<>();
		}
	}

	public void setScrollManager(ScrollManager<T> scrollManager) {
		if (scrollManager != null) {
			this.scrollManager = scrollManager;
		} else {
			this.scrollManager = new DefaultScrollManager<T>();
		}
	}

	public void setSelectionManager(SelectionManager<T> selectionManager) {
		if (selectionManager != null) {
			this.selectionManager = selectionManager;
		} else {
			this.selectionManager = new DefaultSelectionManager<>();
		}
	}

	public void setItemsFactory(SmartViewerItemsFactory<T> itemsFactory) {
		if (itemsFactory != null) {
			this.itemsFactory = itemsFactory;
		}
	}

	public void updateScrolls() {
		Rectangle area = super.getClientArea();
		if (!dataList.isEmpty()) {
			Point neededSize = layout.getNeededSize(area.width, area.height, items);
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
		Rectangle clientArea = super.getClientArea();
		if (clientArea.width > 0 && clientArea.height > 0) {
			Point neededSize = layout.getNeededSize(clientArea.width, clientArea.height, items);
			if (neededSize.x > clientArea.width) {
				clientArea.height -= BAR_SIZE;
			}
			if (neededSize.y > clientArea.height) {
				clientArea.width -= BAR_SIZE;
			}
		}
		return clientArea;
	}

	public ScrollViewport getViewport() {
		return new ScrollViewport(getClientArea(), hScroll.isVisible() ? hScroll.getValue() : -1,
				vScroll.isVisible() ? vScroll.getValue() : -1);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point size = super.computeSize(wHint, hHint, changed);
		Point neededSize = layout.getNeededSize(size.x, size.y, items);
		int hBar = 0;
		int vBar = 0;
		if (neededSize.x > size.x) {
			size.y -= BAR_SIZE;
			vBar = BAR_SIZE;
		}
		if (neededSize.y > size.y) {
			size.x -= BAR_SIZE;
			hBar = BAR_SIZE;
		}
		final Point p = layout.getPreferredSize(size.x, size.y, items);
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
		updateScrolls();
		if (modelSize > 0) {
			Rectangle clientArea = getClientArea();
			allocateItems();
			layout.layoutItems(clientArea.width, clientArea.height, items);
			scrollManager.applyScroll(getViewport(), layout, items, selectionManager.getSelectedData());
		}
		redrawCanvas();
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
			Rectangle bounds = getBounds();
			Point neededSize = layout.getNeededSize(bounds.width, bounds.height, items);
			return neededSize.x > bounds.width || neededSize.y > bounds.height;
		}
		return filled;
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

	private void allocateItems() {
		int modelSize = dataList.size();
		//
		for (int i = 0; i < items.size(); i++) {
			if (i < modelSize) {
				SmartViewerItem<T> item = items.get(i);
				item.setData(dataList.get(i), i);
				item.setSelected(false);
			} else {
				break;
			}
		}
		if (modelSize > items.size()) {
			for (int i = items.size(); i < modelSize; i++) {
				items.add(itemsFactory.createItem(dataList.get(i), i));
			}
		} else {
			Iterator<SmartViewerItem<T>> it = items.iterator();
			while (items.size() > modelSize) {
				it.remove();
			}
		}
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setData(dataList.get(i), i);
		}
	}

	protected void showControl(SmartViewerItem<T> item) {
		if (hScroll.isVisible()) {
			hScroll.fastScroll(scrollManager.computeScrollToMakeVisible(getViewport(), OrientationType.HORIZONTAL,
					layout, item));
			hScroll.scrolled();
		} else if (vScroll.isVisible()) {
			vScroll.fastScroll(scrollManager.computeScrollToMakeVisible(getViewport(), OrientationType.VERTICAL,
					layout, item));
			hScroll.scrolled();
		}
	}

	private class InternalScroll extends VirtualScroll {

		public InternalScroll(OrientationType type) {
			super(SmartViewer.this, type);
		}

		@Override
		protected void previous() {
			fastScroll(scrollManager.previous(getViewport(), type, layout, items));
			scrolled();
		}

		@Override
		protected void next() {
			fastScroll(scrollManager.next(getViewport(), type, layout, items));
			scrolled();
		}

		@Override
		protected void previousPage() {
			fastScroll(scrollManager.previousPage(getViewport(), type, layout, items));
			scrolled();
		}

		@Override
		protected void nextPage() {
			fastScroll(scrollManager.nextPage(getViewport(), type, layout, items));
			scrolled();
		}

		@Override
		protected void scrolled() {
			scrollManager.applyScroll(getViewport(), layout, items, selectionManager.getSelectedData());
			redrawCanvas();
		}
	}

	public SmartViewerItem<T> getItemAt(int x, int y) {
		for (final SmartViewerItem<T> item : items) {
			if (item.contains(x, y)) {
				return item;
			}
		}
		return null;
	}

	public boolean addSelectionListener(SmartViewerSelectionListener<T> l) {
		return selectionListeners.add(l);
	}

	public boolean removeSelectionListener(SmartViewerSelectionListener<T> l) {
		return selectionListeners.remove(l);
	}

	protected void fireSelectionChanged() {
		redrawCanvas();
		for (int i = selectionListeners.size() - 14; i >= 0; i--) {
			selectionListeners.get(i).selctionChanged(this, selectionManager.getSelectedData());
		}
	}

}
