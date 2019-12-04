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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.*;

import pm.eclipse.editbox.impl.BoxDecoratorImpl;
import pm.eclipse.editbox.impl.TRCFileInteraction;
import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
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

	private static IWorkbench workbench = PlatformUI.getWorkbench();

	private static CheckboxTableViewer viewer;
	private Table table;
	private static boolean initialized = false;

	private Action actionSetRequirementBoxes;
	private Action action2;
	private Action doubleClickAction;
	private Action dragAction;
	
	public Table getTable() {
		return table;
	}

	public static CheckboxTableViewer getViewer() {
		return viewer;
	}

	public static boolean isInitialized() {
		return initialized;
	}
	
	public static void setInitialized(boolean state) {
		initialized = state;
	}

	class ViewLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {

		@Override
		//Displaying only the useful things
		public String getColumnText(Object obj, int index) {
			if (obj instanceof TRCRequirement) {
				if (index == 0) {
					return ((TRCRequirement) obj).getId();
				} else if (index == 1) {
					return ((TRCRequirement) obj).getInfo();
				}
				
			}
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			//			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
			return super.getImage(obj);
		}
		@Override	
		public Color getBackground(final Object element) {
			if (element instanceof TRCRequirement) {
				return ((TRCRequirement) element).getColor();
			}
			return super.getBackground(element);
		}
		
		@Override
		public String getToolTipText(Object element) {
			if (element instanceof TRCRequirement) {
				return ((TRCRequirement) element).getId();
			}
			return super.getToolTipText(element);
		}
		
		@Override
		public boolean useNativeToolTip(Object object) {
			if (object instanceof TRCRequirement) {
				return true;
			}
			return super.useNativeToolTip(object);
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
	
	/**
	 * Updates TRC View without reading
	 * @param requirements
	 */
	public static void updateViewer(LinkedList<TRCRequirement> requirements) {

		viewer.setAllGrayed(false);
		viewer.setInput(requirements);

		for (TRCRequirement trcRequirement : requirements) {
			viewer.setChecked(trcRequirement, trcRequirement.isActive());
		}
		
		refreshed();

		TRCView.setInitialized(true);

		// Line below for testing: prints file Path of currently opened file
		// viewer.setInput(new String[] { BoxDecoratorImpl.getCurrentActivePath().toString() });			

	}


	/**
	 * reads the TRCRequirements and updates the TRC View
	 */
	public static LinkedList<TRCRequirement> updateViewer() {
		LinkedList<TRCRequirement> requirements = TRCFileInteraction.ReadTRCsFromFile(BoxDecoratorImpl.getCurrentActivePath());
		if(requirements == null) {
			System.out.println("Requirements == null");
			viewer.setInput(new LinkedList<TRCRequirement>());
			viewer.setAllGrayed(true);
			refreshed();
			return null;
		}
		updateViewer(requirements);
		return requirements;
	}


	@Override
	public void createPartControl(Composite parent) {
		//viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		table = new Table(parent, SWT.BORDER
				| SWT.MULTI
				| SWT.CHECK
				| SWT.V_SCROLL
				| SWT.FULL_SELECTION );
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn column = new TableColumn(table, SWT.BORDER, 0);
		column.setText("Requirement ID");
		column.setAlignment(SWT.LEFT);
		
		
		TableColumn column2 = new TableColumn(table, SWT.BORDER, 1);
		column2.setText("Info");
		column2.setAlignment(SWT.LEFT);
		
		//TODO: Layout can be improved: Size of Columns should automatically adapt to Size of first column
		//TODO: Cells content in Info column should be wrapped.
		
		TableLayout tableLayout = new TableLayout(true);
		tableLayout.addColumnData(new ColumnWeightData(20));
		tableLayout.addColumnData(new ColumnWeightData(80));
		table.setLayout(tableLayout);
		
		
		viewer = new CheckboxTableViewer(table);

		viewer.setContentProvider(TRCViewArrayContentProvider.getInstance());
//		updateViewer();  Do not update here as Build Boxes will set the content in a second
		//Reverting Display Order
		//		TRCViewArrayContentProvider t = (TRCViewArrayContentProvider) viewer.getContentProvider();
		//		t.setReversedOrder(true);
		viewer.setLabelProvider(new ViewLabelProvider());
		//viewer.setCellModifier(new TableCellModifier(viewer));

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "pm.eclipse.editbox.viewer");
		getSite().setSelectionProvider(viewer);
		hookListeners();	// TODO: eventually redesign all listeners?
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		//hookDragAction();  // experimental TODO: remove?
		contributeToActionBars();

		// Newly found: // Advanced Styling of table
		//		
//				table.setHeaderVisible(true);
//				table.setLinesVisible(true);
//				table.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL |
//				GridData.FILL_BOTH));
//		
//				TableColumn column = new TableColumn(table, SWT.NONE, 0);
//				column.setText("Name");
//				column.setAlignment(SWT.LEFT);
//				column.setWidth(300);
//		
//				column = new TableColumn(table, SWT.NONE, 1);
//				column.setText("Color");
//				column.setAlignment(SWT.LEFT);
//				column.setWidth(100);
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


	private void hookListeners() {
		table.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {

				event.detail &= ~SWT.HOT;
				if ((event.detail & SWT.SELECTED) == 0) return; /* item not selected */
				int clientWidth = table.getClientArea().width;
				GC gc = event.gc;
				Color oldForeground = gc.getForeground();
				Color oldBackground = gc.getBackground();
				gc.setBackground(event.display.getSystemColor(SWT.COLOR_YELLOW));
				gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLUE));
				//gc.fillGradientRectangle(event.x, event.y, event.width, event.height, false);
				gc.fillGradientRectangle(0, event.y, clientWidth, event.height, true);
				//gc.drawRectangle(event.x, event.y, event.width, event.height);
				System.out.println("MAGIC: " + event.x +  " " + event.y + " " + clientWidth + " " + event.height);
				//gc.fillGradientRectangle(0, event.y, clientWidth, event.height, false);

				gc.setForeground(oldForeground);
				gc.setBackground(oldBackground);
				event.detail &= ~SWT.SELECTED;
			}
		});

		//DragDetectListener listener;
		//table.addDragDetectListener(listener);

		viewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object checkChanged = event.getElement();
				if (checkChanged instanceof TRCRequirement) {
					TRCRequirement req = (TRCRequirement) checkChanged;
					req.setActive(event.getChecked());
				}
				CheckboxTableViewer viewerz = 
						event == null ? null : (CheckboxTableViewer) event.getSource();
				LinkedList<TRCRequirement> reqs = 
						viewerz == null ? null : (LinkedList<TRCRequirement>) viewerz.getInput();
				if (reqs != null && reqs instanceof List) {
					TRCFileInteraction.WriteTRCsToFile(reqs, BoxDecoratorImpl.getCurrentActivePath());	
				}


			}

		});


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
		manager.add(actionSetRequirementBoxes);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionSetRequirementBoxes);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionSetRequirementBoxes);
		manager.add(action2);
	}

	/**
	 * Defines all the Actions that are meant to be executed when interacting with this viewer
	 */
	private void makeActions() {
		actionSetRequirementBoxes = new Action() {
			public void run() {
				//				showMessage("Set Requirement(s) now executing");
				setRequirementBoxes();
				//				showMessage("Set Requirement(s) executed");
			}

			private void setRequirementBoxes() {
				IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				ISelection selected = editor.getSite().getSelectionProvider().getSelection();
				if (selected != null) {
					if (selected instanceof TextSelection) {
						TextSelection sel = (TextSelection) selected;
						int start = sel.getOffset();
						int length = sel.getLength();
						int end = start + length;
						int[] out = {start, end};
						if(end > start) {
							System.out.println("setRequirementBoxes: " + Arrays.toString(out));
							BoxDecoratorImpl.changeBoxes(start, length);
							editor.setFocus();
						}
					}
				}
			}
		};
		actionSetRequirementBoxes.setText("Set Requirement(s)");
		actionSetRequirementBoxes.setToolTipText("Sets the currently checked Requirements in the Selection in the Editor");
		actionSetRequirementBoxes.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

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
				showColorDialoge();
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

	/**
	 * Opens a ColorDialoge Window where the user can change the color of the Requirement.
	 * 
	 * After the change: 
	 *   - enforces Redraw of boxes
	 *   - enforces Redraw of the View's table
	 */
	private void showColorDialoge() {
		IPath path = BoxDecoratorImpl.getCurrentActivePath();
		LinkedList<TRCRequirement> requirements = TRCFileInteraction.ReadTRCsFromFile(path);
		if(requirements == null) {
			return;
		}
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
			BoxDecoratorImpl.change();
			
			refreshed();  // This should do the same as the commented section below. // TODO: Remove section below and this comment

//			IWorkbenchWindow window = 
//					workbench == null ? null : workbench.getActiveWorkbenchWindow();
//			IWorkbenchPage activePage = 
//					window == null ? null : window.getActivePage();		
//			IEditorPart editor = 
//					activePage == null ? null : activePage.getActiveEditor();
//			if (editor != null) {
//				editor.setFocus();
//			}
//			IViewReference[] references = activePage.getViewReferences();
//			for ( IViewReference v : references) {
//				if(v.getPartName().equals("TRC View")) {
//					IViewPart p = v.getView(true);
//					if (p instanceof TRCView) {
//						TRCView trcView = (TRCView) p;
//						trcView.table.deselectAll();  //TODO: do you want to deselect all? or should the eddited one remain highlighted?
//						trcView.table.removeAll(); //This enforces redrawing with the updated values
//					}
//					p.setFocus();
//				}
//			}
		}
		else {
			showMessage("Object is no instance of TRCRequirment: Double-click detected on "+obj.toString());	
		}
	}

	@Override
	public void setFocus() {
		//TODO: This does not work properly.
		viewer.getControl().setFocus();
	}
	
	public static void refreshed() {
		System.out.println("Refreshed");
		viewer.getTable().getParent().pack();
		viewer.getTable().getParent().layout(true);
		viewer.getTable().setFocus();
		IWorkbenchWindow window = 
				workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = 
				window == null ? null : window.getActivePage();		
		IEditorPart editor = 
				activePage == null ? null : activePage.getActiveEditor();
		if (editor != null) {
			editor.setFocus();
		}
		IViewReference[] references = activePage.getViewReferences();
		for ( IViewReference v : references) {
			if(v.getPartName().equals("TRC View")) {
				IViewPart p = v.getView(true);
				if (p instanceof TRCView) {
				}
				p.setFocus();		
			}
		}
		if (editor != null) {
			editor.setFocus();
		}
	}
}
