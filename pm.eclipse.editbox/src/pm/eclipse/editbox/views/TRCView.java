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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import java.util.Arrays;
import java.util.Iterator;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
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

	private static CheckboxTreeViewer viewer;
	private Tree tree;
	private static boolean initialized = false;

	private Action actionSetRequirementBoxes;
	private Action action2;
	private Action doubleClickAction;
	private Action dragAction;
	
	public Tree getTable() {
		return tree;
	}

	public static CheckboxTreeViewer getViewer() {
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
			if (obj instanceof String) {
				if (index == 1) {
					int width = TRCView.getViewer().getTree().getColumn(1).getWidth();
					return "width: " + width + " " + (String) obj;
				} else {
					return "";
				}
			}
			return getText(obj);
		}
		
		@Override
		public String getText(Object obj) {
			return super.getText(obj);
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

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	/**
	 * Updates TRC View without reading
	 * @param requirements
	 */
	public static void updateViewer(LinkedList<TRCRequirement> requirements) {

		viewer.setInput(requirements);

		for (TRCRequirement trcRequirement : requirements) {
			viewer.setChecked(trcRequirement, trcRequirement.isActive());
		}
		
		refreshed();

		TRCView.setInitialized(true);
	}


	/**
	 * reads the TRCRequirements and updates the TRC View
	 */
	public static LinkedList<TRCRequirement> updateViewer() {
		LinkedList<TRCRequirement> requirements = TRCFileInteraction.ReadTRCsFromFile(BoxDecoratorImpl.getCurrentActivePath());
		if(requirements == null) {
			System.out.println("Requirements == null");
			viewer.setInput(new LinkedList<TRCRequirement>());
			refreshed();
			return null;
		}
		updateViewer(requirements);
		return requirements;
	}


	@Override
	public void createPartControl(Composite parent) {
		//viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree = new Tree(parent, SWT.MULTI
				| SWT.CHECK
				| SWT.V_SCROLL
				| SWT.FULL_SELECTION );
		
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		

		TreeColumn column = new TreeColumn(tree, SWT.BORDER | SWT.WRAP, 0);
		column.setText("Requirement ID");
		column.setAlignment(SWT.LEFT);
		
		
		TreeColumn column2 = new TreeColumn(tree, SWT.BORDER | SWT.WRAP, 1);
		column2.setText("Info");
		column2.setAlignment(SWT.LEFT);
		
		//TODO: Layout might be improvable: Size of Columns should automatically adapt to Size of first column
		//TODO: Cells content in Info column should be wrapped or extended or else.
		
		TableLayout tableLayout = new TableLayout(true);
		tableLayout.addColumnData(new ColumnWeightData(15));
		tableLayout.addColumnData(new ColumnWeightData(85));
		tree.setLayout(tableLayout);
		
		
		viewer = new CheckboxTreeViewer(tree);

		viewer.setContentProvider(TRCViewArrayContentProvider.getInstance());
//		updateViewer();  // Do not update here as Build Boxes will set the content in a second
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
		contributeToActionBars();

		// Newly found: // Advanced Styling of tree
		//		
//				tree.setHeaderVisible(true);
//				tree.setLinesVisible(true);
//				tree.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL |
//				GridData.FILL_BOTH));
//		
//				TableColumn column = new TableColumn(tree, SWT.NONE, 0);
//				column.setText("Name");
//				column.setAlignment(SWT.LEFT);
//				column.setWidth(300);
//		
//				column = new TableColumn(tree, SWT.NONE, 1);
//				column.setText("Color");
//				column.setAlignment(SWT.LEFT);
//				column.setWidth(100);
		//
		//		//TableViewer tableViewer = new TableViewer(tree);
		//		viewer.setUseHashlookup(true);
		//		viewer.setColumnProperties(new String[] { "Name", "Color" });
		//
		//		CellEditor[] editors =
		//		new CellEditor[viewer.getColumnProperties().length];
		//		editors[0] = new TextCellEditor(tree);
		//		editors[1] = new ColorCellEditor(tree);
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
		
		int TEXT_MARGIN = 10;
		
		tree.addListener(SWT.MeasureItem, event -> {
			/**
			 * Check whether the Item is a root or the second line. Format the second line accordingly.
			 * TODO: Format only the second lines ... somehow
			 */
			TreeItem item = (TreeItem) event.item;
			String text = item.getText(event.index);
			int displaywidth = item.getTextBounds(1).width;
			int textwidth = event.width;
			System.out.println("Length: " + text.length());
			System.out.println("width: " + textwidth);
			System.out.println("displaywidth: " + displaywidth);
			if(textwidth > displaywidth) {
				String line = "";
				int spaceAt = text.indexOf(" ");
				String currentLine = text.substring(0, spaceAt);
				String lineBefore = "";
				int progressor = 0;
				boolean reachedEnd = false;
				while(!reachedEnd) {
					
					do {
						spaceAt = text.indexOf(" ", spaceAt + 1);
						if(spaceAt == -1) {
							reachedEnd = true;
							break;
						}
						lineBefore = currentLine;
						currentLine = text.substring(progressor, spaceAt);
						System.out.println("continueing?");
						System.out.println("Textwidth: " + event.gc.textExtent(currentLine).x);
					} while (event.gc.textExtent(currentLine).x < displaywidth);
					progressor = progressor + lineBefore.length();
					currentLine = lineBefore;
					if(line.equals("")) {
						line += lineBefore;
					} else {
						line += "\n" + lineBefore;
					}
					
				}
				item.setText(1, line);
			}

			Point size = event.gc.textExtent(text);
//			if(item.getParentItem() != null) {
//				event.width = size.x + 2 * TEXT_MARGIN;
//				event.height = Math.max(event.height, size.y + TEXT_MARGIN);				
//			} else {
				event.width = size.x;
				event.height = size.y;	
				System.out.println("Height:" + event.height);
//			}
		});
		tree.addListener(SWT.EraseItem, event -> event.detail &= ~SWT.FOREGROUND);
		tree.addListener(SWT.PaintItem, event -> {
			TreeItem item = (TreeItem) event.item;
			String text = item.getText(event.index);
			/* center column 1 vertically */
			int yOffset = 0;
			/**
			 * Check whether the Item is a root or the second line. Make the second line wrap text.
			 */
//			if (item.getParentItem() != null) {
//				Point size = event.gc.textExtent(text);
//				yOffset = Math.max(0, (event.height - size.y) / 2);
//				event.gc.drawText(text, event.x + TEXT_MARGIN, event.y + yOffset, true);
//			} else {
				event.gc.drawText(text, event.x, event.y, true);
//			}
		});
		
		/**
		 * Adds a Listener that creates a custom selection Highlighting
		 * TODO: It is getting Overwritten by the Requirement Color
		 */
		tree.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {

				event.detail &= ~SWT.HOT;
				if ((event.detail & SWT.SELECTED) == 0) return; /* item not selected */
				int clientWidth = tree.getClientArea().width;
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

		viewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object checkChanged = event.getElement();
				if (checkChanged instanceof TRCRequirement) {
					TRCRequirement req = (TRCRequirement) checkChanged;
					req.setActive(event.getChecked());
				}
				CheckboxTreeViewer viewerz = 
						event == null ? null : (CheckboxTreeViewer) event.getSource();
				LinkedList<TRCRequirement> reqs = 
						viewerz == null ? null : (LinkedList<TRCRequirement>) viewerz.getInput();
				if (reqs != null && reqs instanceof List) {
					TRCFileInteraction.WriteTRCsToFile(reqs, BoxDecoratorImpl.getCurrentActivePath());	
				}


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
//				showMessage("Moved Requirement up");
				moveRequirementOneUp();
			}
		};
		action2.setText("1 up");
		action2.setToolTipText("Move the selected Requirement 1 up");
		action2.setImageDescriptor(workbench.getSharedImages().
				getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
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

	protected void moveRequirementOneUp() {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IStructuredSelection selected = viewer.getStructuredSelection();
		if (selected != null) {
			Object obj = selected.getFirstElement();
			if (obj instanceof TRCRequirement) {
				TRCRequirement r = (TRCRequirement) obj;
				LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
				LinkedList<TRCRequirement> out = new LinkedList<TRCRequirement>();
				for (Iterator iterator = reqs.iterator(); iterator.hasNext();) {
					TRCRequirement trcRequirement = (TRCRequirement) iterator.next();
					if(trcRequirement.getId().equals(r.getId())) {
						if(iterator.hasNext()) {
							TRCRequirement after = (TRCRequirement) iterator.next();
							out.add(after);
							out.add(r);
						} else {
							out.add(r);
						}
					} else {
						out.add(trcRequirement);
					}
				}
				
				TRCFileInteraction.WriteReversedTRCsToFile(out);
				BoxDecoratorImpl.change();
				updateViewer(out);
				
			}
		}
		
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
	 *   - enforces Redraw of the View's tree
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
			
			refreshed();
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
		viewer.getTree().getParent().pack();
		viewer.getTree().getParent().layout(true);
		viewer.getTree().setFocus();
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
