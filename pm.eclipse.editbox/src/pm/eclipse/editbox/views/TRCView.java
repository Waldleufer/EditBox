package pm.eclipse.editbox.views;

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

import pm.eclipse.editbox.impl.BoxDecoratorImpl;
import pm.eclipse.editbox.impl.TRCFileInteraction;
import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;


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
	
	private static TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	 

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
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
	}
	
	public static void updateViewer() {
		List<TRCRequirement> requirements = TRCFileInteraction.ReadTRCsFromFile(BoxDecoratorImpl.getCurrentActivePath());
		LinkedList<String> requirementIDs = new LinkedList<String>();
		for (TRCRequirement trcRequirement : requirements) {
			requirementIDs.addFirst(trcRequirement.getId());
		}
		//viewer.setInput(new String[] { TRCFileInteraction.ReadTRCsFromFile(BoxDecoratorImpl.getCurrentActivePath()).toString() });
		viewer.setInput(requirementIDs);

		// Line below for testing: prints file Path of currently opened file
		// viewer.setInput(new String[] { BoxDecoratorImpl.getCurrentActivePath().toString() });
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		updateViewer();
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "pm.eclipse.editbox.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
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
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
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
