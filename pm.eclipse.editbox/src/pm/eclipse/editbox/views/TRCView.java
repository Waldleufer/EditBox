package pm.eclipse.editbox.views;

import org.eclipse.swt.widgets.ColorDialog;

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
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.jface.layout.TableColumnLayout;
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
import org.eclipse.swt.custom.ScrolledComposite;
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

	private static CheckboxTableViewer viewer;
	private static Table table;
	private static boolean initialized = false;

	private Action actionSetRequirementBoxes;
	private Action actionMoveRequirementUp;
	private Action actionMoveRequirementDown;
	private Action doubleClickAction;
	
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
			if (obj instanceof String) {
				if (index == 1) {
					int width = TRCView.getViewer().getTable().getColumn(1).getWidth();
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
		
		table.layout();

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
		
		FillLayout layer = new FillLayout(SWT.VERTICAL);
		parent.setLayout(layer);
		
		//viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		table = new Table(parent, SWT.MULTI
				| SWT.CHECK
				| SWT.V_SCROLL
				| SWT.FULL_SELECTION );
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		
		//TODO: Layout might be improvable: Size of Columns should automatically adapt to Size of first column
		//TODO: Cells content in Info column should be wrapped or extended or else.
		/**
		 * Set the Table Layout
		 */
//		table.getColumn(0).pack();
		int tablewidth = table.getParent().getClientArea().width;
		TableLayout tableLayout = new TableLayout(true);
		TableColumn column = new TableColumn(table, SWT.BORDER | SWT.WRAP, 0);
		column.setText("Requirement ID");
		column.setAlignment(SWT.LEFT);
		
		TableColumn column2 = new TableColumn(table, SWT.BORDER | SWT.WRAP, 1);
		column2.setText("Info");
		column2.setAlignment(SWT.LEFT);
		
		tableLayout.addColumnData(new ColumnWeightData(15, (int)(tablewidth/10)));
		tableLayout.addColumnData(new ColumnWeightData(85, (int)(tablewidth*7/10)));
		table.setLayout(tableLayout);
		
//		TableLayout tableLayout = new TableLayout(true);
//		tableLayout.addColumnData(new ColumnWeightData(15));
////		tableLayout.addColumnData(new ColumnWeightData(85));
//		table.setLayout(tableLayout);
		
		
		viewer = new CheckboxTableViewer(table);

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
	}


	private void hookListeners() {
		
		int TEXT_MARGIN = 10;
		
		/**
		 * adding Text wrapping / 2 Line Info
		 */
		table.addListener(SWT.MeasureItem, event -> {
			/**
			 * Check The Column of the Event. Format Info Column in two lines
			 * TODO: Format only the second lines ... somehow
			 */
			final int MAX_LINES = 2;
			if(event.index == 1) {

				TableItem item = (TableItem) event.item;
				final String text = item.getText(event.index);

				item.getParent().setToolTipText(((TRCRequirement) item.getData()).getInfo());		
				
				String finalText = "";
				int displaywidth = item.getTextBounds(1).width;
				int textwidth = event.width;
				
				if( textwidth > displaywidth ) {
					int i = 1; // first Line, 2nd line, ...
					int spaceFoundAtIndex = 0;
					int lineProgressionIndex = 0;
					String finalTextLine = "";
					String currentBuildingTextLine = "";
					String more = " ...";
					boolean reachedEnd = false;
					
					while(!reachedEnd) {
						
						do {
							finalTextLine = currentBuildingTextLine;
							spaceFoundAtIndex = text.indexOf(" ", spaceFoundAtIndex + 1); //+1 to avoid  finding same space over and over again
							if(spaceFoundAtIndex == -1 || spaceFoundAtIndex > text.length()) {
								reachedEnd = true;
								currentBuildingTextLine = text.substring(lineProgressionIndex);
								break;
								//We found the end of the text
							}
							currentBuildingTextLine = text.substring(lineProgressionIndex, spaceFoundAtIndex);
						} while (!reachedEnd && event.gc.textExtent(currentBuildingTextLine).x < displaywidth);
						
						//the line was to long; finalTextLine is a line that fits
						if (! reachedEnd ) {
							lineProgressionIndex += finalTextLine.length();
							if(i <= 1) {
								finalText = finalTextLine;
							} else if (i < MAX_LINES){
								finalText += "\n" + finalTextLine;
							} else if (i == MAX_LINES) {
								if(event.gc.textExtent(finalTextLine+more+more).x < displaywidth) {
									finalText += "\n" + finalTextLine + more;	
								} else {
									finalText += "\n" + finalTextLine;
								}
							}
							
						} else {  // We reached End of Text
							String endText = "";
							if(event.gc.textExtent(currentBuildingTextLine).x < displaywidth ) {
								endText += currentBuildingTextLine;								
							} else {
								endText += finalTextLine;
							}
							
							if(i <= 1) {
								finalText = endText;
								if(currentBuildingTextLine.length() > finalTextLine.length()) {
									//We might have missed a word or two.
									finalText += "\n" + currentBuildingTextLine.substring(finalTextLine.length());
								}
							} else if (i < MAX_LINES){
								finalText += "\n" + endText;
							} else if (i == MAX_LINES) {
								finalText += "\n" + endText;
							}	
						}
						
						i++;
					}

					item.setText(1, finalText);
					//Setting the correct height and width for the SWT.PaintItem Job
					Point size = event.gc.textExtent(finalText);
					event.width = size.x;
					event.height = size.y;	
				}
			}
		});
		table.addListener(SWT.EraseItem, event -> event.detail &= ~SWT.FOREGROUND);
		table.addListener(SWT.PaintItem, event -> {
			TableItem item = (TableItem) event.item;
			String text = item.getText(event.index);
			/* center all columns vertically */
			int yOffset = 0;
				Point size = event.gc.textExtent(text);
				yOffset = Math.max(0, (event.height - size.y) / 2);
				event.gc.drawText(text, event.x, event.y + yOffset, true);
		});
		
		/**
		 * Adds a Listener that creates a custom selection Highlighting
		 * As the Colour is specified by the Requirements, we only change the Foreground Colour 
		 * By Setting SWT.HOT
		 */
		table.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {

				event.detail &= ~SWT.HOT;
				if ((event.detail & SWT.SELECTED) == 0) return; /* item not selected */
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
		manager.add(actionMoveRequirementUp);
		manager.add(actionMoveRequirementDown);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionSetRequirementBoxes);
		manager.add(actionMoveRequirementUp);
		manager.add(actionMoveRequirementDown);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionSetRequirementBoxes);
		manager.add(actionMoveRequirementUp);
		manager.add(actionMoveRequirementDown);
	}

	/**
	 * Defines all the Actions that are meant to be executed when interacting with this viewer
	 */
	private void makeActions() {
		actionSetRequirementBoxes = new Action() {
			public void run() {
				setRequirementBoxes();
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

		actionMoveRequirementUp = new Action() {
			public void run() {
//				showMessage("Moved Requirement up");
				moveRequirementOneUp();
			}
		};
		actionMoveRequirementUp.setText("1 up");
		actionMoveRequirementUp.setToolTipText("Move the selected Requirement 1 Layer up");
		actionMoveRequirementUp.setImageDescriptor(workbench.getSharedImages().
				getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
		
		
		actionMoveRequirementDown = new Action() {
			public void run() {
//				showMessage("Moved Requirement up");
				moveRequirementOneDown();
			}
		};
		actionMoveRequirementDown.setText("1 down");
		actionMoveRequirementDown.setToolTipText("Move the selected Requirement 1 Layer down");
		actionMoveRequirementDown.setImageDescriptor(workbench.getSharedImages().
				getImageDescriptor(ISharedImages.IMG_TOOL_REDO));

		
		
		doubleClickAction = new Action() {
			public void run() {
				showColorDialoge();
			}
		};
	}

	protected void moveRequirementOneUp() {
		IStructuredSelection selected = viewer.getStructuredSelection();
		if (selected != null) {
			Object obj = selected.getFirstElement();
			if (obj instanceof TRCRequirement) {
				TRCRequirement r = (TRCRequirement) obj;
				LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
				LinkedList<TRCRequirement> out = new LinkedList<TRCRequirement>();
				for (Iterator<TRCRequirement> iterator = reqs.iterator(); iterator.hasNext();) {
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
	
	
	//TODO: to TEST this.
	/**
	 * moves the currently selected Requirement one up.
	 */
	protected void moveRequirementOneDown() {
		IStructuredSelection selected = viewer.getStructuredSelection();
		if (selected != null) {
			Object obj = selected.getFirstElement();
			if (obj instanceof TRCRequirement) {
				TRCRequirement r = (TRCRequirement) obj;
				TRCRequirement last = null;
				LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
				LinkedList<TRCRequirement> out = new LinkedList<TRCRequirement>();
				Iterator<TRCRequirement> iterator = reqs.iterator();
				do  {
					TRCRequirement trcRequirement = (TRCRequirement) iterator.next();
					if(trcRequirement.getId().equals(r.getId())) {
						if(last != null) {
							out.add(trcRequirement);
							continue;
						}
					} else {
						if(last != null) {
							out.add(last);							
						}
					}
					last = trcRequirement;
				} while (iterator.hasNext());
				
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
//		viewer.getTable().pack();
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
