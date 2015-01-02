package fr.mla.swt.smart.viewer.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import fr.mla.swt.smart.viewer.dnd.DragAndDropManager;
import fr.mla.swt.smart.viewer.layout.SmartGridLayout;
import fr.mla.swt.smart.viewer.model.CustomSmartViewerModel;
import fr.mla.swt.smart.viewer.model.OrientationType;
import fr.mla.swt.smart.viewer.ui.SmartViewer;
import fr.mla.swt.smart.viewer.ui.SmartViewerActionListener;
import fr.mla.swt.smart.viewer.ui.SmartViewerItem;

public class FilesGroupComposite extends Composite {

	private SmartViewer listViewer;
	private SmartViewer groupViewer;
	private CustomSmartViewerModel listModel;
	private FileGroupModel groupModel;

	public FilesGroupComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(GridLayoutFactory.fillDefaults().spacing(0, 5).create());

		listViewer = createListViewer(this);
		groupViewer = createGroupViewer(this);

		List<File> files = new ArrayList<File>();
		for (int i = 0; i < 5; i++) {
			files.add((File) listModel.remove(0));
		}
		groupModel.addToGroup(files, null);

		listViewer.setDragAndDropManager(new DragAndDropManager() {

			@Override
			public Transfer[] getDragTransfers(DragSourceEvent event, Collection<SmartViewerItem> items) {
				return new Transfer[] { FilePathTransfer.getInstance() };
			}

			@Override
			public Transfer[] getDropTransfers() {
				return new Transfer[] { FilePathTransfer.getInstance(), FileGroupTransfer.getInstance() };
			}

			@Override
			public void performExternalDrop(DropTargetEvent event, SmartViewerItem targetItem) {
				if (FileGroupTransfer.getInstance().isSupportedType(event.currentDataType)) {
					List<FileGroup> groups = parseGroups(event.data);
					List<File> files = new ArrayList<File>();
					for (FileGroup group : groups) {
						for (GroupFile gf : group.getFiles()) {
							files.add(gf.getFile());
						}
					}
					groupModel.removeGroups(groups);
					listModel.addAll(files);

				} else if (FilePathTransfer.getInstance().isSupportedType(event.currentDataType)) {
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
			public Transfer[] getDragTransfers(DragSourceEvent event, Collection<SmartViewerItem> items) {
				Transfer transfer = null;
				for (SmartViewerItem item : items) {
					Object data = item.getData();
					Transfer tr = null;
					if (data instanceof FileGroup) {
						tr = FileGroupTransfer.getInstance();
					} else if (data instanceof GroupFile) {
						tr = FilePathTransfer.getInstance();
					}
					if (transfer != null && transfer != tr) {
						return null;
					} else {
						transfer = tr;
					}
				}
				return new Transfer[] { transfer };
			}

			@Override
			public Transfer[] getDropTransfers() {
				return new Transfer[] { FilePathTransfer.getInstance() };
			}

			@Override
			public void performExternalDrop(DropTargetEvent event, SmartViewerItem targetItem) {
				List<File> files = parseFiles(event.data);
				if (files != null && !files.isEmpty()) {
					FileGroup group = null;
					if (targetItem != null) {
						Object data = targetItem.getData();
						if (data instanceof FileGroup) {
							group = (FileGroup) data;
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
			public void mouseDoubleClick(SmartViewer viewer, SmartViewerItem item, int x, int y) {
				Object data = item.getData();
				if (data instanceof FileGroup) {
					FileGroup group = (FileGroup) data;
					if (group.getFiles().size() > 1) {
						group.setExpanded(!group.isExpanded());
						groupViewer.refresh(false);
					}
				}
			}

			@Override
			public void defaultAction(SmartViewer viewer, Collection<SmartViewerItem> items) {

			}
		});
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

	private List<FileGroup> parseGroups(Object data) {
		List<FileGroup> groups = new ArrayList<FileGroup>();
		if (data instanceof Object[]) {
			Object[] array = (Object[]) data;
			for (Object o : array) {
				FileGroup group = groupModel.getGroupById(String.valueOf(o));
				if (group != null) {
					groups.add(group);
				}
			}
		}
		if (data instanceof String) {
			String str = (String) data;
			String[] groupIds = str.split("\n");
			for (String id : groupIds) {
				FileGroup group = groupModel.getGroupById(id);
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

	private SmartViewer createListViewer(Composite parent) {
		FileGroupRenderer renderer = new FileGroupRenderer(parent.getDisplay());
		renderer.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		File dir = new File("C:\\Users\\mlachgar\\Pictures\\image-test\\320655894");
		listModel = new CustomSmartViewerModel(dir.listFiles());
		SmartGridLayout layout = new SmartGridLayout(0, 1);
		SmartViewer viewer = new SmartViewer(parent, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
		viewer.setLayout(layout);
		viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		viewer.setRenderer(renderer);
		viewer.setModel(listModel);
		return viewer;
	}

	private SmartViewer createGroupViewer(Composite parent) {
		FileGroupRenderer renderer = new FileGroupRenderer(parent.getDisplay());
		renderer.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		groupModel = new FileGroupModel();
		FileLayout layout = new FileLayout(OrientationType.HORIZONTAL);
		SmartViewer viewer = new SmartViewer(parent, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
		viewer.setLayout(layout);
		viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		viewer.setRenderer(renderer);
		viewer.setModel(groupModel);
		return viewer;
	}

}
