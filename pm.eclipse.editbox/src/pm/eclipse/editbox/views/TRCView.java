package pm.eclipse.editbox.views;

import org.eclipse.swt.widgets.ColorDialog;

//import java.util.*;
//
//import javax.inject.Inject;
//
//import org.eclipse.jface.viewers.ArrayContentProvider;
//import org.eclipse.jface.viewers.ColumnLabelProvider;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.jface.viewers.TableViewerColumn;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.ui.ISharedImages;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.part.ViewPart;
//
//public class TRCView extends ViewPart {
//	public static final String ID = "pm.eclipse.editbox.views.TRCView";
//
//	@Inject IWorkbench workbench;
//	
//	private TableViewer viewer;
//	
//	private class StringLabelProvider extends ColumnLabelProvider {
//		@Override
//		public String getText(Object element) {
//			return super.getText(element);
//		}
//
//		@Override
//		public Image getImage(Object obj) {
//			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
//		}
//
//	}
//
//	@Override
//	public void createPartControl(Composite parent) {
//		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//		viewer.getTable().setLinesVisible(true);
//
//		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
//		column.setLabelProvider(new StringLabelProvider());
//
//		viewer.getTable().getColumn(0).setWidth(200);
//		
//		viewer.setContentProvider(ArrayContentProvider.getInstance());
//		
//		// Provide the input to the ContentProvider
//		viewer.setInput(createInitialDataModel());
//	}
//
//
//	@Override
//	public void setFocus() {
//		viewer.getControl().setFocus();
//	}
//	
//	private List<String> createInitialDataModel() {
//		return Arrays.asList("One", "Two", "Three");
//	}
//}



import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.impl.BoxDecoratorImpl;
import pm.eclipse.editbox.impl.TRCFileInteraction;
import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class TRCView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "pm.eclipse.editbox.views.TRCView";

	private IWorkbench workbench = PlatformUI.getWorkbench();
	
	private static CheckboxTableViewer viewer;
	private static Table table;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	private Action dragAction;

	class ViewLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			if (obj instanceof TRCRequirement) {
		    	return ((TRCRequirement) obj).getId();
			}
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
		@Override	
		public Color getBackground(final Object element) {
		    if (element instanceof TRCRequirement) {
		    	return ((TRCRequirement) element).getColor();
		    }
		    return super.getBackground(element);
		}
	}
	
	private class TableCellModifier implements ICellModifier {
		private TableViewer fViewer;

		public TableCellModifier(TableViewer viewer) {
			fViewer = viewer;
		}

		public boolean canModify(Object element, String property) {
			if (property.equals("Color") || property.equals("Name")) {
				return true;
			}
			return false;
		}

		public void modify(Object element, String property, Object value) {
		}

		public Object getValue(Object element, String property) {
			if (property.equals("Color")) {
				return new RGB(255, 0, 0);
			} else if (property.equals("Name")) {
				return ((ITableLabelProvider)
						fViewer.getLabelProvider()).getColumnText(element, 0);
			}
			return null;
		}
	}
	
	 
	
	public static void updateViewer() {
		List<TRCRequirement> requirements = TRCFileInteraction.ReadTRCsFromFile(BoxDecoratorImpl.getCurrentActivePath());
		LinkedList<String> requirementIDs = new LinkedList<String>();
		for (TRCRequirement trcRequirement : requirements) {
			requirementIDs.addFirst(trcRequirement.getId());
		}
		//viewer.setInput(new String[] { TRCFileInteraction.ReadTRCsFromFile(BoxDecoratorImpl.getCurrentActivePath()).toString() });
//		viewer.setInput(requirementIDs);
		viewer.setInput(requirements);

		// Line below for testing: prints file Path of currently opened file
		// viewer.setInput(new String[] { BoxDecoratorImpl.getCurrentActivePath().toString() });
	}

	@Override
	public void createPartControl(Composite parent) {
		//viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		table = new Table(parent, SWT.BORDER
				| SWT.SINGLE
				| SWT.H_SCROLL
				| SWT.V_SCROLL
				| SWT.FULL_SELECTION );
		
		//DragDetectListener listener;
		//table.addDragDetectListener(listener);
		viewer = new CheckboxTableViewer(table);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		updateViewer();
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setAllChecked(true);
		viewer.setCellModifier(new TableCellModifier(viewer));

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "pm.eclipse.editbox.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		//hookDragAction();  // experimental TODO: remove?
		contributeToActionBars();
		
		// Newly found:
//		
//		table.setHeaderVisible(true);
//		table.setLinesVisible(true);
//		table.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL |
//		GridData.FILL_BOTH));
//
//		TableColumn column = new TableColumn(table, SWT.NONE, 0);
//		column.setText("Name");
//		column.setAlignment(SWT.LEFT);
//		column.setWidth(300);
//
//		column = new TableColumn(table, SWT.NONE, 1);
//		column.setText("Color");
//		column.setAlignment(SWT.LEFT);
//		column.setWidth(100);
//
//		//TableViewer tableViewer = new TableViewer(table);
//		viewer.setUseHashlookup(true);
//		viewer.setColumnProperties(new String[] { "Name", "Color" });
//
//		CellEditor[] editors =
//		new CellEditor[viewer.getColumnProperties().length];
//		editors[0] = new TextCellEditor(table);
//		editors[1] = new ColorCellEditor(table);
//		viewer.setCellEditors(editors);
//
//		//viewer.setLabelProvider(new TableLabelProvider());
//		//viewer.setContentProvider(new TableContentProvider());
//		viewer.setCellModifier(new TableCellModifier(viewer));
//
//		List list = new ArrayList();
//		list.add(new String[] { "Tree", "Green" });
//		list.add(new String[] { "Sun", "Yellow" });
//		list.add(new String[] { "IBM", "Blue" });
//		viewer.setInput(list);
		
	}

	private void hookDragAction() {
		table.addDragDetectListener(new DragDetectListener() {
			
			@Override
			public void dragDetected(DragDetectEvent e) {
				dragAction.run();	
			}
		});
		
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TRCView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	/**
	 * Defines all the Actions that are meant to be executed when interacting with this viewer
	 */
	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(workbench.getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				IPath path = BoxDecoratorImpl.getCurrentActivePath();
				List<TRCRequirement> requirements = TRCFileInteraction.ReadTRCsFromFile(path);
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				if(obj instanceof TRCRequirement) {
					TRCRequirement req = (TRCRequirement) obj;
					Shell shell = new Shell();
					ColorDialog dlg = new ColorDialog(shell);
					for (TRCRequirement trcRequirement : requirements) {
						if (trcRequirement.getId().equals(req.getId())) {
							dlg.setRGB(trcRequirement.getColor().getRGB());
							RGB rgb = dlg.open();
							trcRequirement.setColor(new Color(null, rgb.red, rgb.green, rgb.blue));
						}
					}
					TRCFileInteraction.WriteTRCsToFile(requirements, path);
					IWorkbenchWindow window = 
							workbench == null ? null : workbench.getActiveWorkbenchWindow();
					IWorkbenchPage activePage = 
							window == null ? null : window.getActivePage();		
					IEditorPart editor = 
							activePage == null ? null : activePage.getActiveEditor();
					if (editor != null) {
						editor.setFocus();
					}
					BoxDecoratorImpl.change();
				}
				else {
					showMessage("Double-click detected on "+obj.toString());	
				}
			}
		};
		dragAction = new Action() {
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				showMessage("Drag detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"TRC View",
			message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
