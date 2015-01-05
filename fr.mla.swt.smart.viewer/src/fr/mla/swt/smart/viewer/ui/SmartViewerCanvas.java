package fr.mla.swt.smart.viewer.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import fr.mla.swt.smart.viewer.dnd.DragAndDropManager;
import fr.mla.swt.smart.viewer.layout.SmartGridLayout;
import fr.mla.swt.smart.viewer.layout.SmartViewerLayout;
import fr.mla.swt.smart.viewer.model.DirectionType;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.model.SmartModelListener;
import fr.mla.swt.smart.viewer.model.SmartViewerModel;
import fr.mla.swt.smart.viewer.renderer.DefaultRenderer;
import fr.mla.swt.smart.viewer.renderer.SmartViewerRenderer;
import fr.mla.swt.smart.viewer.scroll.DefaultScrollManager;
import fr.mla.swt.smart.viewer.scroll.ScrollManager;
import fr.mla.swt.smart.viewer.scroll.ScrollViewport;

public class SmartViewerCanvas extends Canvas implements SmartViewer {

	private static final int BAR_SIZE = 16;

	private SmartViewerModel model;
	private final List<Object> dataList = new ArrayList<>();
	private SmartModelListener modelListener;
	private SmartViewerItemsFactory itemsFactory = new DefaultViewerItemsFactory();
	private InternalScroll hScroll;
	private InternalScroll vScroll;
	private SmartViewerRenderer renderer = new DefaultRenderer();
	private SmartViewerLayout layout = new SmartGridLayout(-1, 2);
	private ScrollManager scrollManager = new DefaultScrollManager();
	private SelectionManager selectionManager = new DefaultSelectionManager();
	private DragAndDropManager dndManager;
	private TooltipHandler tooltipHandler = new TooltipHandler();
	private SmartViewerActionsProvider actionsProvider;
	private final List<SmartViewerSelectionListener> selectionListeners = new ArrayList<>();
	private final List<SmartViewerActionListener> actionListeners = new ArrayList<>();

	protected final List<SmartViewerItem> items = new ArrayList<>();
	protected final List<SmartViewerItem> hoverItems = new ArrayList<>();
	private final Map<Object, SmartViewerItem> dataMap = new HashMap<>();

	public SmartViewerCanvas(Composite parent, int style) {
		super(parent, style);

		vScroll = new InternalScroll(OrientationType.VERTICAL);
		hScroll = new InternalScroll(OrientationType.HORIZONTAL);

		setUpModelListener();
		setUpPaintListener();
		setUpControlListener(parent);
		setUpMouseListener();
		setUpKeyListener();

		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (renderer != null) {
					renderer.dispose();
				}
				if (dndManager != null) {
					dndManager.dispose();
				}
			}
		});
	}

	private void setUpPaintListener() {
		addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				final Rectangle paintBounds = new Rectangle(e.x, e.y, e.width,
						e.height);
				final Image img = new Image(getDisplay(), new Rectangle(0, 0,
						e.width, e.height));
				final GC gc = new GC(img);
				try {
					gc.setFont(getFont());
					gc.setAntialias(SWT.ON);
					renderer.renderItems(gc, paintBounds,
							SmartViewerCanvas.this, items);
					if (hScroll.isVisible()) {
						hScroll.draw(gc, e.x, e.y);
					}
					if (vScroll.isVisible()) {
						vScroll.draw(gc, e.x, e.y);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					e.gc.drawImage(img, 0, 0, e.width, e.height, e.x, e.y,
							e.width, e.height);
					img.dispose();
					gc.dispose();
				}
			}
		});
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public Rectangle getDrawArea() {
		Rectangle bounds = getBounds();
		Rectangle area = new Rectangle(0, 0, bounds.width, bounds.height);
		if (hScroll.isVisible()) {
			area.height -= BAR_SIZE;
		}
		if (vScroll.isVisible()) {
			area.width -= BAR_SIZE;
		}
		return area;
	}

	@Override
	public Point getScrollValues() {
		int xScroll = hScroll.isVisible() ? hScroll.getValue() : 0;
		int yScroll = vScroll.isVisible() ? vScroll.getValue() : 0;
		return new Point(xScroll, yScroll);
	}

	private void setUpModelListener() {
		modelListener = new SmartModelListener() {

			@Override
			public void modelChanged(Object source) {
				refresh(true);
			}

			@Override
			public void itemModified(Object source, Object data) {
				SmartViewerItem item = dataMap.get(data);
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
				refresh(false);
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
					final SmartViewerItem item = getItemAt(e.x, e.y);
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
						handleMouseMove(e, items);
						handleItemTooltip(e, items);
						break;
					case SWT.MouseHover:
						
						break;
					case SWT.MouseWheel:
						break;
					case SWT.MouseDoubleClick:
						if (item != null) {
							fireMouseDoubleClick(item, e.x, e.y);
						}
						break;
					}
				}
			}
		};

		addListener(SWT.MouseUp, listener);
		addListener(SWT.MouseDown, listener);
		addListener(SWT.MouseMove, listener);
		addListener(SWT.MouseHover, listener);
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

	private List<SmartViewerItem> draggingItems = new ArrayList<SmartViewerItem>();
	private Image dragImage = null;

	private void setUpDrag() {
		final DragSource dragSource = new DragSource(this,
				dndManager.getDragOperations());
		dragSource.addDragListener(new DragSourceAdapter() {

			@Override
			public void dragSetData(final DragSourceEvent event) {
				event.data = dndManager.getDragData(draggingItems);
			}

			@Override
			public void dragFinished(final DragSourceEvent event) {
				if (event.image != null && !event.image.isDisposed()) {
					event.image.dispose();
				}
				draggingItems.clear();
				if (dragImage != null) {
					dragImage.dispose();
				}
			}

			@Override
			public void dragStart(final DragSourceEvent event) {
				SmartViewerItem clickedItem = getItemAt(event.x, event.y);
				if (clickedItem != null) {
					Collection<SmartViewerItem> selectedItems = selectionManager
							.getSelectedItems();
					if (!selectedItems.isEmpty()) {
						Transfer[] transfers = dndManager.getDragTransfers(
								event, selectedItems);
						if (transfers == null) {
							event.doit = false;
						} else {
							dragSource.setTransfer(transfers);
							if (dragImage != null) {
								dragImage.dispose();
							}
							draggingItems.addAll(selectedItems);
							dragImage = dndManager.getDragImage(getDisplay(),
									SmartViewerCanvas.this, draggingItems);
							event.image = dragImage;
						}
					} else {
						event.doit = false;
					}
				} else {
					event.doit = false;
					draggingItems.clear();
				}
			}

		});
	}

	private void setUpDrop() {
		final DropTarget target = new DropTarget(this,
				dndManager.getDropOperations());
		target.setTransfer(dndManager.getDropTransfers());
		target.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetEvent event) {
				final Point p = toControl(event.x, event.y);
				SmartViewerItem targetItem = null;
				int index = layout.itemAt(getBounds(), p.x, p.y, items);
				if (index >= items.size()) {
					index = items.size() - 1;
				}
				if (index > -1) {
					targetItem = items.get(index);
				}

				// internal drop
				if (!draggingItems.isEmpty()) {
					dndManager.performInternalDrop(event, targetItem,
							draggingItems);
				}
				// External drop
				else {
					dndManager.performExternalDrop(event, targetItem);
				}
			}

			@Override
			public void dragEnter(final DropTargetEvent event) {
				if (!dndManager.canDrop(event, draggingItems,
						!draggingItems.isEmpty())) {
					event.detail = DND.DROP_NONE;
				}
			}

		});
	}

	protected void handleKeyReleased(final Event e) {
		if (!e.doit) {
			return;
		}
		final boolean ctrlMask = (e.stateMask & SWT.CTRL) != 0;
		switch (e.keyCode) {
		case SWT.CR:
			fireDefaultAction();
			break;

		case SWT.END:
			if (vScroll.isVisible()) {
				vScroll.goToEnd();
			}
			if (hScroll.isVisible()) {
				hScroll.goToEnd();
			}
			break;
		case SWT.HOME:
			if (vScroll.isVisible()) {
				vScroll.resetScroll();
			}
			if (hScroll.isVisible()) {
				hScroll.resetScroll();
			}
			break;
		case 'a':
			if (ctrlMask) {
				if (selectionManager.selectAll()) {
					fireSelectionChanged();
				}
			}
			break;
		}
	}

	protected void handleKeyPressed(final Event e) {
		if (!e.doit || items.isEmpty()) {
			return;
		}
		final boolean shiftMask = (e.stateMask & SWT.SHIFT) != 0;
		DirectionType directionType = null;
		switch (e.keyCode) {
		case SWT.ARROW_LEFT:
			if (layout.isNavigable(OrientationType.HORIZONTAL)) {
				directionType = DirectionType.LEFT;
			}
			break;
		case SWT.ARROW_RIGHT:
			if (layout.isNavigable(OrientationType.HORIZONTAL)) {
				directionType = DirectionType.RIGHT;
			}
			break;
		case SWT.ARROW_UP:
			if (layout.isNavigable(OrientationType.VERTICAL)) {
				directionType = DirectionType.UP;
			}
			break;
		case SWT.ARROW_DOWN:
			if (layout.isNavigable(OrientationType.VERTICAL)) {
				directionType = DirectionType.DOWN;
			}
			break;
		case SWT.PAGE_DOWN:
			if (hScroll.isVisible()) {
				hScroll.nextPage();
			} else if (vScroll.isVisible()) {
				vScroll.nextPage();
			}
			return;
		case SWT.PAGE_UP:
			if (hScroll.isVisible()) {
				hScroll.previousPage();
			} else if (vScroll.isVisible()) {
				vScroll.previousPage();
			}
			return;
		}
		if (directionType != null) {
			SmartViewerItem item = getLastSelectedItem();
			SmartViewerItem nextItem = null;
			if (item == null) {
				nextItem = items.get(0);
			} else {
				List<SmartViewerItem> itemsList = selectionManager
						.getDepthItems(item.getDepth());
				nextItem = layout.getNeighborItem(item, directionType,
						itemsList);
			}
			if (nextItem != null) {
				if (shiftMask) {
					if (selectionManager.appendToSelection(nextItem, true)) {
						showControl(nextItem);
						fireSelectionChanged();
					}
				} else {
					if (selectionManager.selectOnly(nextItem, true)) {
						showControl(nextItem);
						fireSelectionChanged();
					}
				}
			}
		}
	}

	private void handleMouseDown(Event e) {
		if (handleItemAction(e, items)) {
			return;
		}
		final SmartViewerItem item = getItemAt(e.x, e.y);
		final boolean dragDetect = dragDetect(e);
		if (item != null) {
			if (isLeftClick(e)) {
				if ((e.stateMask & SWT.CTRL) != 0) {
					if (selectionManager.appendToSelection(item, !dragDetect)) {
						fireSelectionChanged();
					}
				} else if ((e.stateMask & SWT.SHIFT) != 0) {
					if (selectionManager.expandSelectionTo(item)) {
						fireSelectionChanged();
					}
				} else {
					if (selectionManager.selectOnly(item, !dragDetect)) {
						fireSelectionChanged();
					}
				}
			} else if ((e.stateMask & SWT.CTRL) == 0) {
				if (selectionManager.selectOnly(item, !dragDetect)) {
					fireSelectionChanged();
				}
			}
		} else if (isLeftClick(e)
				|| selectionManager.getSelectedData().isEmpty()) {
			selectionManager.clearSelection(null);
			fireSelectionChanged();
		}
		setFocus();
	}

	private SmartViewerItem getLastSelectedItem() {
		Collection<SmartViewerItem> selectedItems = selectionManager
				.getSelectedItems();
		if (!selectedItems.isEmpty()) {
			SmartViewerItem last = null;
			for (final SmartViewerItem item : selectedItems) {
				last = item;
			}
			return last;
		}
		return null;
	}

	protected boolean isLeftClick(final Event e) {
		return e.button == 1;
	}

	public void setModel(SmartViewerModel model) {
		if (this.model != null) {
			this.model.removeListChangeListener(modelListener);
		}
		this.model = model;
		dataList.clear();
		if (model != null) {
			model.addListChangeListener(modelListener);
			dataList.addAll(model.getItems());
		}
		refresh(true);
	}

	public void setRenderer(SmartViewerRenderer renderer) {
		if (renderer != null) {
			this.renderer = renderer;
		}
	}

	@Override
	public SmartViewerRenderer getRenderer() {
		return renderer;
	}

	public void setLayout(SmartViewerLayout layout) {
		if (layout != null) {
			this.layout = layout;
		} else {
			layout = new SmartGridLayout();
		}
	}

	public void setScrollManager(ScrollManager scrollManager) {
		if (scrollManager != null) {
			this.scrollManager = scrollManager;
		} else {
			this.scrollManager = new DefaultScrollManager();
		}
	}

	public void setDragAndDropManager(DragAndDropManager dragAndDropManager) {
		if (this.dndManager == null && dragAndDropManager != null) {
			this.dndManager = dragAndDropManager;
			setUpDrag();
			setUpDrop();
		}
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		if (selectionManager != null) {
			this.selectionManager = selectionManager;
		} else {
			this.selectionManager = new DefaultSelectionManager();
		}
		this.selectionManager.setItems(items);
	}

	public void setItemsFactory(SmartViewerItemsFactory itemsFactory) {
		if (itemsFactory != null) {
			this.itemsFactory = itemsFactory;
		}
	}

	public void setActionsProvider(SmartViewerActionsProvider actionsProvider) {
		this.actionsProvider = actionsProvider;
	}

	public void setTooltipHandler(TooltipHandler tooltipHandler) {
		this.tooltipHandler = tooltipHandler;
	}

	public void updateScrolls() {
		Rectangle area = super.getBounds();
		if (!dataList.isEmpty()) {
			Point neededSize = layout.getNeededSize(area.width, area.height,
					items);
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
		Rectangle clientArea = super.getBounds();
		if (clientArea.width > 0 && clientArea.height > 0) {
			Point neededSize = layout.getNeededSize(clientArea.width,
					clientArea.height, items);
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
		return new ScrollViewport(getClientArea(),
				hScroll.isVisible() ? hScroll.getValue() : 0,
				vScroll.isVisible() ? vScroll.getValue() : 0);
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
			p.x += hBar;
		}
		if (p.y != SWT.DEFAULT) {
			p.y += vBar;
		}
		return p;
	}

	public void refresh(boolean modelChanged) {
		if (modelChanged) {
			dataList.clear();
			dataList.addAll(model.getItems());
			allocateItems();
		}
		int modelSize = dataList.size();
		getParent().layout(true, true);
		updateScrolls();
		if (modelSize > 0) {
			Rectangle clientArea = getClientArea();
			layout.layoutItems(clientArea.width, clientArea.height, items);
			scrollManager.applyScroll(getViewport(), layout, items);
		}
		redrawCanvas();
	}

	private void redrawCanvas() {
		redraw();
		update();
	}

	public void redraw(final SmartViewerItem item) {
		if (!isDisposed()) {
			redraw(item.getX(), item.getY(), item.getWidth(), item.getHeight(),
					false);
			update();
		}
	}

	public boolean isVisibleListFilled() {
		boolean filled = false;
		if (!isDisposed()) {
			Rectangle bounds = getBounds();
			Point neededSize = layout.getNeededSize(bounds.width,
					bounds.height, items);
			return neededSize.x > bounds.width || neededSize.y > bounds.height;
		}
		return filled;
	}

	public Rectangle getItemBounds(final SmartViewerItem item) {
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

	private void allocateItems() {
		int modelSize = dataList.size();
		//
		for (int i = 0; i < items.size(); i++) {
			if (i < modelSize) {
				SmartViewerItem item = items.get(i);
				item.setData(dataList.get(i), i);
				item.setSelected(false);
				item.clearChildren();
				item.clearExtraData();
			} else {
				break;
			}
		}
		if (modelSize > items.size()) {
			for (int i = items.size(); i < modelSize; i++) {
				items.add(itemsFactory.createItem(dataList.get(i), i, 0));
			}
		} else {
			while (items.size() > modelSize) {
				items.remove(items.size() - 1);
			}
		}
		dataMap.clear();
		for (int i = 0; i < items.size(); i++) {
			SmartViewerItem item = items.get(i);
			Object data = dataList.get(i);
			item.setData(data, i);
			fillActions(item);
			dataMap.put(data, item);
			allocateChildren(item);
		}
		selectionManager.setItems(items);
	}

	private void fillActions(SmartViewerItem item) {
		if (actionsProvider != null && item != null) {
			item.setToolbarActions(actionsProvider.getToolbarActions(item
					.getData()));
		}
	}

	private void allocateChildren(SmartViewerItem item) {
		Object data = item.getData();
		List<?> children = model.getChildren(data);
		if (children != null) {
			int depth = item.getDepth() + 1;
			for (int j = 0; j < children.size(); j++) {
				Object childData = children.get(j);
				SmartViewerItem child = itemsFactory.createItem(childData, j,
						depth);
				item.addChild(child);
				fillActions(child);
				dataMap.put(childData, child);
				allocateChildren(child);
			}
		}
	}

	protected void showControl(SmartViewerItem item) {
		if (hScroll.isVisible()) {
			hScroll.fastScroll(scrollManager.computeScrollToMakeVisible(
					getViewport(), OrientationType.HORIZONTAL, layout, item));
			hScroll.scrolled();
		} else if (vScroll.isVisible()) {
			vScroll.fastScroll(scrollManager.computeScrollToMakeVisible(
					getViewport(), OrientationType.VERTICAL, layout, item));
			hScroll.scrolled();
		}
	}

	public SmartViewerItem getItemAt(int x, int y) {
		for (final SmartViewerItem item : items) {
			if (item.contains(x, y)) {
				SmartViewerItem lastChild = null;
				for (SmartViewerItem child : item.getChildren()) {
					if (child.contains(x, y)) {
						lastChild = child;
					}
				}
				if (lastChild != null) {
					return lastChild;
				}
				return item;
			}
		}
		return null;
	}

	public void handleMouseMove(Event e, List<SmartViewerItem> items) {
		for (final SmartViewerItem item : items) {
			if (item.contains(e.x, e.y)) {
				hoverItems.add(item);
				redraw(item);
				handleMouseMove(e, item.getChildren());
			} else if (hoverItems.remove(item)) {
				redraw(item);
			}
		}
	}

	public boolean handleItemAction(Event e, List<SmartViewerItem> items) {
		for (final SmartViewerItem item : items) {
			if (item.contains(e.x, e.y)) {
				SmartViewerAction action = renderer.getActionAt(this, item,
						e.x, e.y);
				if (action != null) {
					action.run(e, item);
					return true;
				}
				return handleItemAction(e, item.getChildren());
			}
		}
		return false;
	}

	public boolean handleItemTooltip(Event e, List<SmartViewerItem> items) {
		if (tooltipHandler != null) {
			for (final SmartViewerItem item : items) {
				if (item.contains(e.x, e.y)) {
					Object data = renderer.getTooltipData(this, item, e.x, e.y);
					if (data != null) {
						tooltipHandler.handleTooltip(this, data);
						return true;
					}
					boolean found = handleItemTooltip(e, item.getChildren());
					if (found) {
						return true;
					}
				}
			}
			tooltipHandler.handleTooltip(this, null);
		}
		return false;
	}

	public boolean addSelectionListener(SmartViewerSelectionListener l) {
		return selectionListeners.add(l);
	}

	public boolean removeSelectionListener(SmartViewerSelectionListener l) {
		return selectionListeners.remove(l);
	}

	protected void fireSelectionChanged() {
		redrawCanvas();
		for (int i = selectionListeners.size() - 1; i >= 0; i--) {
			selectionListeners.get(i).selctionChanged(this,
					selectionManager.getSelectedData());
		}
	}

	public boolean addActionListener(SmartViewerActionListener l) {
		return actionListeners.add(l);
	}

	public boolean removeActionListener(SmartViewerActionListener l) {
		return actionListeners.remove(l);
	}

	protected void fireMouseDoubleClick(SmartViewerItem item, int x, int y) {
		for (int i = actionListeners.size() - 1; i >= 0; i--) {
			actionListeners.get(i).mouseDoubleClick(this, item, x, y);
		}
	}

	protected void fireDefaultAction() {
		for (int i = actionListeners.size() - 1; i >= 0; i--) {
			actionListeners.get(i).defaultAction(this,
					selectionManager.getSelectedItems());
		}
	}

	private class InternalScroll extends VirtualScroll {

		public InternalScroll(OrientationType type) {
			super(SmartViewerCanvas.this, type);
			setBackground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		}

		@Override
		protected void previous() {
			fastScroll(scrollManager.previous(getViewport(), type, layout,
					items));
			scrolled();
		}

		@Override
		protected void next() {
			fastScroll(scrollManager.next(getViewport(), type, layout, items));
			scrolled();
		}

		@Override
		protected void previousPage() {
			fastScroll(scrollManager.previousPage(getViewport(), type, layout,
					items));
			scrolled();
		}

		@Override
		protected void nextPage() {
			fastScroll(scrollManager.nextPage(getViewport(), type, layout,
					items));
			scrolled();
		}

		@Override
		protected void scrolled() {
			scrollManager.applyScroll(getViewport(), layout, items);
			redrawCanvas();
		}
	}

}
