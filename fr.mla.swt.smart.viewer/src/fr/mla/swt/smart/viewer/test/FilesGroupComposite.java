package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import fr.mla.swt.smart.viewer.Activator;
import fr.mla.swt.smart.viewer.dnd.DragAndDropManager;
import fr.mla.swt.smart.viewer.group.DataGroup;
import fr.mla.swt.smart.viewer.group.DataGroupLayout;
import fr.mla.swt.smart.viewer.group.GroupData;
import fr.mla.swt.smart.viewer.layout.SmartGridLayout;
import fr.mla.swt.smart.viewer.model.CustomSmartViewerModel;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewer;
import fr.mla.swt.smart.viewer.ui.SmartViewerAction;
import fr.mla.swt.smart.viewer.ui.SmartViewerActionListener;
import fr.mla.swt.smart.viewer.ui.SmartViewerActionsProvider;
import fr.mla.swt.smart.viewer.ui.SmartViewerCanvas;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class FilesGroupComposite extends Composite {

	private SmartViewerCanvas listViewer;
	private SmartViewerCanvas groupViewer;
	private CustomSmartViewerModel listModel;
	private FileGroupModel groupModel;

	public FilesGroupComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(GridLayoutFactory.fillDefaults().spacing(0, 5).create());

		listViewer = createListViewer(this);
		groupViewer = createGroupViewer(this);

		listViewer.setDragAndDropManager(new DragAndDropManager() {

			@Override
			public Transfer[] getDragTransfers(DragSourceEvent event,
					Collection<SmartViewerItem> items) {
				return new Transfer[] { FilePathTransfer.getInstance() };
			}

			@Override
			public Transfer[] getDropTransfers() {
				return new Transfer[] { FilePathTransfer.getInstance(),
						FileGroupTransfer.getInstance() };
			}

			@Override
			public void performExternalDrop(DropTargetEvent event,
					SmartViewerItem targetItem) {
				if (FileGroupTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					List<DataGroup> groups = parseGroups(event.data);
					List<File> files = new ArrayList<File>();
					for (DataGroup group : groups) {
						for (GroupData gf : group.getData()) {
							files.add((File) gf.getData());
						}
					}
					groupModel.removeGroups(groups);
					listModel.addAll(files);

				} else if (FilePathTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					List<File> files = parseFiles(event.data);
					if (!files.isEmpty()) {
						groupModel.removeFilesFromGroup(files);
						listModel.addAll(files);
					}
				}
			}

			@Override
			public Object getDragData(Collection<SmartViewerItem> items) {
				return serilizeData(items);
			}

		});

		groupViewer.setDragAndDropManager(new DragAndDropManager() {

			@Override
			public Transfer[] getDragTransfers(DragSourceEvent event,
					Collection<SmartViewerItem> items) {
				Transfer transfer = null;
				for (SmartViewerItem item : items) {
					Object data = item.getData();
					Transfer tr = null;
					if (data instanceof DataGroup) {
						tr = FileGroupTransfer.getInstance();
					} else if (data instanceof GroupData) {
						tr = FilePathTransfer.getInstance();
					}
					if (transfer != null && transfer != tr) {
						return null;
					} else {
						transfer = tr;
					}
				}
				if (transfer == null) {
					return null;
				}
				return new Transfer[] { transfer };
			}

			@Override
			public Transfer[] getDropTransfers() {
				return new Transfer[] { FilePathTransfer.getInstance() };
			}

			@Override
			public void performExternalDrop(DropTargetEvent event,
					SmartViewerItem targetItem) {
				List<File> files = parseFiles(event.data);
				if (files != null && !files.isEmpty()) {
					DataGroup group = null;
					if (targetItem != null) {
						Object data = targetItem.getData();
						if (data instanceof DataGroup) {
							group = (DataGroup) data;
						}
					}
					listModel.removeAll(files);
					groupModel.addToGroup(files, group);
				}
			}

			@Override
			public Object getDragData(Collection<SmartViewerItem> items) {
				return serilizeData(items);
			}
		});

		groupViewer.addActionListener(new SmartViewerActionListener() {

			@Override
			public void mouseDoubleClick(SmartViewer viewer,
					SmartViewerItem item, int x, int y) {
				Object data = item.getData();
				if (data instanceof DataGroup) {
					DataGroup group = (DataGroup) data;
					if (group.getData().size() > 1) {
						group.setExpanded(!group.isExpanded());
						groupViewer.refresh(false);
					}
				}
			}

			@Override
			public void defaultAction(SmartViewer viewer,
					Collection<SmartViewerItem> items) {

			}
		});

		groupViewer.setActionsProvider(new SmartViewerActionsProvider() {

			@Override
			public List<SmartViewerAction> getToolbarActions(Object data) {
				if (data instanceof DataGroup) {
					DataGroup group = (DataGroup) data;
					return Arrays.asList(newExpandGroupAction(group),
							newDeleteGroupAction(group));
				}
				return null;
			}
		});

		groupViewer.refresh(true);
	}

	private SmartViewerAction newExpandGroupAction(final DataGroup group) {
		final Image expandImage = Activator.getImage("expand.png");
		final Image collapseImage = Activator.getImage("collapse.png");
		SmartViewerAction action = new SmartViewerAction() {
			@Override
			public void run(Event event, SmartViewerItem item) {
				group.setExpanded(!group.isExpanded());
				groupViewer.refresh(false);
			}

			@Override
			public Image getImage() {
				return group.isExpanded() ? collapseImage : expandImage;
			}

			@Override
			public String getText() {
				return group.isExpanded() ? "Collapse" : "Expand";
			}

			@Override
			public boolean isVisible() {
				return group.getData().size() > 4;
			}

		};
		return action;
	}

	private SmartViewerAction newDeleteGroupAction(final DataGroup group) {
		SmartViewerAction action = new SmartViewerAction("Remove",
				Activator.getImage("trash.png")) {
			@Override
			public void run(Event event, SmartViewerItem item) {
				List<File> files = new ArrayList<File>();
				for (GroupData gf : group.getData()) {
					files.add((File) gf.getData());
				}
				groupModel.removeGroup(group);
				listModel.addAll(files);
			}

		};
		return action;
	}

	private List<File> parseFiles(Object data) {
		List<File> files = new ArrayList<File>();
		if (data instanceof Object[]) {
			Object[] array = (Object[]) data;
			for (Object o : array) {
				if (o instanceof File) {
					files.add((File) o);
				}
			}
		}
		return files;
	}

	private List<DataGroup> parseGroups(Object data) {
		List<DataGroup> groups = new ArrayList<DataGroup>();
		if (data instanceof Object[]) {
			Object[] array = (Object[]) data;
			for (Object o : array) {
				DataGroup group = groupModel.getGroupById(String.valueOf(o));
				if (group != null) {
					groups.add(group);
				}
			}
		}
		if (data instanceof String) {
			String str = (String) data;
			String[] groupIds = str.split("\n");
			for (String id : groupIds) {
				DataGroup group = groupModel.getGroupById(id);
				if (group != null) {
					groups.add(group);
				}
			}
		}
		return groups;
	}

	private Object serilizeData(Collection<SmartViewerItem> items) {
		Object[] dndData = new Object[items.size()];
		int i = 0;
		for (SmartViewerItem item : items) {
			dndData[i++] = item.getData();
		}
		return dndData;
	}

	@Override
	public boolean setFocus() {
		return listViewer.setFocus();
	}

	private SmartViewerCanvas createListViewer(Composite parent) {
		FileGroupRenderer renderer = new FileGroupRenderer(parent.getDisplay());
		renderer.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		File dir = new File("C:\\Users\\mlachgar\\Pictures\\image-test\\100");
		listModel = new CustomSmartViewerModel(dir.listFiles());
		SmartGridLayout layout = new SmartGridLayout(0, 1);
		SmartViewerCanvas viewer = new SmartViewerCanvas(parent,
				SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
		viewer.setLayout(layout);
		viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		viewer.setRenderer(renderer);
		viewer.setModel(listModel);
		return viewer;
	}

	private SmartViewerCanvas createGroupViewer(Composite parent) {
		FileGroupRenderer renderer = new FileGroupRenderer(parent.getDisplay());
		renderer.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		groupModel = new FileGroupModel();
		DataGroupLayout layout = new DataGroupLayout(OrientationType.HORIZONTAL);
		SmartViewerCanvas viewer = new SmartViewerCanvas(parent,
				SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
		viewer.setLayout(layout);
		viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		viewer.setRenderer(renderer);
		viewer.setModel(groupModel);
		return viewer;
	}

}
