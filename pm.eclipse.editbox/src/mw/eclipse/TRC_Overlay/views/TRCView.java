/**
 * Copyright (c) 2020 Martin Wagner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.spdx.org/licenses/EPL-1.0
 * 
 * Contributors
 * @author Martin Wagner
 * 
 */
package mw.eclipse.TRC_Overlay.views;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import mw.eclipse.TRC_Overlay.impl.BoxDecoratorImpl;
import mw.eclipse.TRC_Overlay.impl.TRCFileInteraction;
import pm.eclipse.editbox.EditBox;
import mw.eclipse.TRC_Overlay.impl.TRCRequirement;               

/**
 * This TRCView class plugs-in a new workbench view. The view shows
 * {@link TRCRequirement}s obtained via {@link TRCFileInteraction}. The view is
 * connected to the model using a {@link TRCViewArrayContentProvider}
 * <p>
 * The view uses a {@link TRCViewLabelProvider} to define how TRCRequirements
 * should be presented in the view.
 */
public class TRCView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "mw.eclipse.TRC_Overlay.views.TRCView";

	protected static final int CHECKBOX_CLEARED = -1;
	protected static final int CHECKBOX_UNSELECTED = 0;
	protected static final int CHECKBOX_SELECTED = 1;

	private static IWorkbench workbench = PlatformUI.getWorkbench();

	private static CheckboxTableViewer viewer;
	private static Table table;
	private static boolean initialized = false;

	private Action actionSetRequirementBoxes;
	private Action actionMoveRequirementUp;
	private Action actionMoveRequirementUpTop;
	private Action actionMoveRequirementDown;
	private Action actionMoveRequirementDownBottom;
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

	/**
	 * The TRCViewLabelProvider is the custom {@link ColumnLabelProvider} for the
	 * TRC View. It defines how the list of {@link TRCRequirement}s should be
	 * displayed, including Text, Background Color and ToolTipText.
	 * 
	 * @author Martin Wagner
	 */
	class TRCViewLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {

		@Override
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
					return (String) obj;
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
			// return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
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
			return null;
		}
	}

	/**
	 * Updates TRC View without reading
	 * 
	 * @param requirements
	 */
	public static void updateViewer(LinkedList<TRCRequirement> requirements) {

		if (requirements == null) {
			System.out.println("Requirements == null");
			viewer.setInput(new LinkedList<TRCRequirement>());
			refreshed();
			TRCView.setInitialized(false);
			return;
		} else {
			viewer.setInput(requirements);
			
			table.layout();
			
			for (TRCRequirement trcRequirement : requirements) {
				viewer.setChecked(trcRequirement, trcRequirement.isActive());
			}
			
			refreshed();
			
			TRCView.setInitialized(true);
		}
		
	}

	/**
	 * reads the TRCRequirements and updates the TRC View
	 */
	public static LinkedList<TRCRequirement> updateViewer() {
		LinkedList<TRCRequirement> requirements = TRCFileInteraction
				.ReadTRCsFromFile(BoxDecoratorImpl.getCurrentActivePath());
		if (requirements == null) {
			System.out.println("Requirements == null");
			if(viewer != null) {
				viewer.setInput(new LinkedList<TRCRequirement>());				
				refreshed();
			}
			return null;
		} else {
			updateViewer(requirements);
			return requirements;			
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		FillLayout layer = new FillLayout(SWT.VERTICAL);
		parent.setLayout(layer);

		table = new Table(parent, SWT.MULTI | SWT.CHECK | SWT.V_SCROLL | SWT.FULL_SELECTION);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		/**
		 * Set the Table Layout
		 */
		int tablewidth = table.getParent().getClientArea().width;
		TableLayout tableLayout = new TableLayout(true);

		TableColumn column = new TableColumn(table, SWT.BORDER | SWT.WRAP, 0);
		column.setText("Requirement ID");
		column.setAlignment(SWT.LEFT);
		LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
		LinkedList<TRCRequirement> active = TRCFileInteraction.getActiveTRCRequirements(reqs);
		if (active == null) {
			column.setImage(EditBox.getImageDescriptor(EditBox.IMG_CHECKBOX_CLEARED).createImage());
		} else if (active.size() <= 0) {
			column.setImage(EditBox.getImageDescriptor(EditBox.IMG_CHECKBOX_CLEARED).createImage());
		} else if (active.size() > 0 && active.size() < reqs.size()) {
			column.setImage(EditBox.getImageDescriptor(EditBox.IMG_CHECKBOX_UNSELECTED).createImage());
		} else {
			column.setImage(EditBox.getImageDescriptor(EditBox.IMG_CHECKBOX_SELECTED).createImage());
		}

		column.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {

				int checkBoxFlag = TRCView.CHECKBOX_UNSELECTED;
				LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
				LinkedList<TRCRequirement> active = TRCFileInteraction.getActiveTRCRequirements(reqs);

				if (active.size() <= 0) {
					checkBoxFlag = TRCView.CHECKBOX_CLEARED;
				} else if (active.size() > 0 && active.size() < reqs.size()) {
					checkBoxFlag = TRCView.CHECKBOX_UNSELECTED;
				} else {
					checkBoxFlag = TRCView.CHECKBOX_SELECTED;
				}

				if (checkBoxFlag == TRCView.CHECKBOX_CLEARED) {
					// select all
					for (TRCRequirement r : reqs) {
						r.setActive(true);
					}
					viewer.setAllChecked(true);
					column.setImage(EditBox.getImageDescriptor(EditBox.IMG_CHECKBOX_SELECTED).createImage());
				} else if (checkBoxFlag == TRCView.CHECKBOX_SELECTED || checkBoxFlag == TRCView.CHECKBOX_UNSELECTED) {
					// deselect all
					for (TRCRequirement r : reqs) {
						r.setActive(false);
					}
					viewer.setAllChecked(false);
					column.setImage(EditBox.getImageDescriptor(EditBox.IMG_CHECKBOX_CLEARED).createImage());
				}
				TRCFileInteraction.WriteReversedTRCsToFile(reqs);
			}
		});

		TableColumn column2 = new TableColumn(table, SWT.BORDER | SWT.WRAP, 1);
		column2.setText("Info");
		column2.setAlignment(SWT.LEFT);

		tableLayout.addColumnData(new ColumnWeightData(15, (int) (tablewidth / 10)));
		tableLayout.addColumnData(new ColumnWeightData(85, (int) (tablewidth * 7 / 10)));
		table.setLayout(tableLayout);

		viewer = new CheckboxTableViewer(table);

		viewer.setContentProvider(TRCViewArrayContentProvider.getInstance());
		viewer.setLabelProvider(new TRCViewLabelProvider());

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "pm.eclipse.editbox.viewer");
		getSite().setSelectionProvider(viewer);
		hookListeners();
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	/**
	 * Adds Listeners to the table and the viewer.
	 */
	private void hookListeners() {
		/**
		 * adding Text wrapping / 2 Line Info
		 */
		table.addListener(SWT.MeasureItem, event -> {
			/**
			 * Check The Column of the Event. Format Info Column in two lines Allow
			 * <code>MAX_LINES</code> at max.
			 */
			final int MAX_LINES = 2;
			if (event.index == 1) {

				TableItem item = (TableItem) event.item;
				final String text = item.getText(event.index);

				item.getParent().setToolTipText(((TRCRequirement) item.getData()).getInfo());

				String finalText = "";
				int displaywidth = item.getTextBounds(1).width;
				int textwidth = event.width;

				if (textwidth > displaywidth) {
					int i = 1; // first Line, 2nd line, ...
					int spaceFoundAtIndex = 0;
					int lineProgressionIndex = 0;
					String finalTextLine = "";
					String currentBuildingTextLine = "";
					String more = " ...";
					boolean reachedEnd = false;

					while (!reachedEnd) {

						do {
							finalTextLine = currentBuildingTextLine;
							spaceFoundAtIndex = text.indexOf(" ", spaceFoundAtIndex + 1); // +1 to avoid finding same
																							// space over and over again
							if (spaceFoundAtIndex == -1 || spaceFoundAtIndex > text.length()) {
								reachedEnd = true;
								currentBuildingTextLine = text.substring(lineProgressionIndex);
								break;
								// We found the end of the text
							}
							currentBuildingTextLine = text.substring(lineProgressionIndex, spaceFoundAtIndex);
						} while (!reachedEnd && event.gc.textExtent(currentBuildingTextLine).x < displaywidth);

						// the line was to long; finalTextLine is a line that fits
						if (!reachedEnd) {
							lineProgressionIndex += finalTextLine.length();
							if (i <= 1) {
								finalText = finalTextLine;
							} else if (i < MAX_LINES) {
								finalText += "\n" + finalTextLine;
							} else if (i == MAX_LINES) {
								if (event.gc.textExtent(finalTextLine + more + more).x < displaywidth) {
									finalText += "\n" + finalTextLine + more;
								} else {
									finalText += "\n" + finalTextLine;
								}
							}

						} else { // We reached End of Text
							String endText = "";
							if (event.gc.textExtent(currentBuildingTextLine).x < displaywidth) {
								endText = currentBuildingTextLine;
							} else {
								endText = finalTextLine;
							}

							if (i <= 1) {
								finalText = endText;
								if (currentBuildingTextLine.length() > finalTextLine.length()) {
									// We might have missed a word or two.
									if (finalText.indexOf("\n") == -1) {
										finalText += "\n" + currentBuildingTextLine.substring(finalTextLine.length());
									} // Otherwise we already expanded
								}
							} else if (i < MAX_LINES) {
								finalText += "\n" + endText;
							} else if (i == MAX_LINES) {
								finalText += "\n" + endText;
							}
						}

						i++;
					}

					item.setText(1, finalText);
					// Setting the correct height and width for the SWT.PaintItem Job
					Point size = event.gc.textExtent(finalText);
					event.width = size.x;
					event.height = size.y;
				}
			}
		});
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
		 * Adds a Listener that creates a custom selection Highlighting As the Colour is
		 * specified by the Requirements, we only change the Foreground Colour By
		 * Setting SWT.HOT
		 */
		table.addListener(SWT.EraseItem, new Listener() {
			public void handleEvent(Event event) {
				event.detail &= ~SWT.HOT;
				event.detail &= ~SWT.FOREGROUND;
				if ((event.detail & SWT.SELECTED) == 0)
					return; /* item not selected */
				event.detail &= ~SWT.SELECTED;
			}
		});

		/**
		 * Adds a CheckStateListener for the Checkbox events of the {@link TRCView} and
		 * adjusts the column checkbox
		 */
		viewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object checkChanged = event.getElement();
				if (checkChanged instanceof TRCRequirement) {
					TRCRequirement req = (TRCRequirement) checkChanged;
					req.setActive(event.getChecked());
					LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
					LinkedList<TRCRequirement> active = TRCFileInteraction.getActiveTRCRequirements(reqs);
					if (active.size() <= 0) {
						table.getColumn(0)
								.setImage(EditBox.getImageDescriptor(EditBox.IMG_CHECKBOX_CLEARED).createImage());
					} else if (active.size() > 0 && active.size() < reqs.size()) {
						table.getColumn(0)
								.setImage(EditBox.getImageDescriptor(EditBox.IMG_CHECKBOX_UNSELECTED).createImage());
					} else {
						table.getColumn(0)
								.setImage(EditBox.getImageDescriptor(EditBox.IMG_CHECKBOX_SELECTED).createImage());
					}
					if (reqs != null && reqs instanceof List) {
						TRCFileInteraction.WriteReversedTRCsToFile(reqs, BoxDecoratorImpl.getCurrentActivePath());
					}
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
		manager.add(actionMoveRequirementUpTop);
		manager.add(actionMoveRequirementUp);
		manager.add(actionMoveRequirementDown);
		manager.add(actionMoveRequirementDownBottom);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionSetRequirementBoxes);
		manager.add(new Separator());
		manager.add(actionMoveRequirementUpTop);
		manager.add(actionMoveRequirementUp);
		manager.add(actionMoveRequirementDown);
		manager.add(actionMoveRequirementDownBottom);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionSetRequirementBoxes);
		manager.add(new Separator());
		manager.add(actionMoveRequirementUpTop);
		manager.add(actionMoveRequirementUp);
		manager.add(actionMoveRequirementDown);
		manager.add(actionMoveRequirementDownBottom);
		manager.add(new Separator());
	}

	/**
	 * Defines all the Actions that are meant to be executed when interacting with
	 * this viewer
	 */
	private void makeActions() {
		actionSetRequirementBoxes = new Action() {
			public void run() {
				setRequirementBoxes();
			}

			private void setRequirementBoxes() {
				IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				ISelection selected = editor.getSite().getSelectionProvider().getSelection();
				if (selected != null) {
					if (selected instanceof TextSelection) {
						TextSelection sel = (TextSelection) selected;
						int start = sel.getOffset();
						int length = sel.getLength();
						int end = start + length;
						int[] out = { start, end };
						if (end > start) {
							System.out.println("setRequirementBoxes: " + Arrays.toString(out));
							BoxDecoratorImpl.changeBoxes(start, length);
							editor.setFocus();
						}
					}
				}
			}
		};
		actionSetRequirementBoxes.setText("Set Requirement(s)");
		actionSetRequirementBoxes
				.setToolTipText("Sets the currently checked Requirements in the Selection in the Editor");
		actionSetRequirementBoxes.setImageDescriptor(EditBox.getImageDescriptor(EditBox.IMG_SET_REQUIREMENTS));

		actionMoveRequirementUp = new Action() {
			public void run() {
				moveRequirementOneUp();
			}
		};
		actionMoveRequirementUp.setText("1 up");
		actionMoveRequirementUp.setToolTipText("Move the selected Requirement 1 Layer up");
		actionMoveRequirementUp.setImageDescriptor(EditBox.getImageDescriptor(EditBox.IMG_ARROW_UP));

		actionMoveRequirementUpTop = new Action() {
			public void run() {
				moveToTop();
			}
		};
		actionMoveRequirementUpTop.setText("up to top");
		actionMoveRequirementUpTop.setToolTipText("Move the selected Requirement to the uppermost Layer");
		actionMoveRequirementUpTop.setImageDescriptor(EditBox.getImageDescriptor(EditBox.IMG_ARROW_UP_TOP));

		actionMoveRequirementDown = new Action() {
			public void run() {
				moveRequirementOneDown();
			}
		};
		actionMoveRequirementDown.setText("1 down");
		actionMoveRequirementDown.setToolTipText("Move the selected Requirement 1 Layer down");
		actionMoveRequirementDown.setImageDescriptor(EditBox.getImageDescriptor(EditBox.IMG_ARROW_DOWN));

		actionMoveRequirementDownBottom = new Action() {
			public void run() {
				moveToBottom();
			}
		};
		actionMoveRequirementDownBottom.setText("down to bottom");
		actionMoveRequirementDownBottom.setToolTipText("Move the selected Requirement to the lowermost Layer");
		actionMoveRequirementDownBottom.setImageDescriptor(EditBox.getImageDescriptor(EditBox.IMG_ARROW_DOWN_END));

		doubleClickAction = new Action() {
			public void run() {
				showColorDialoge();
			}
		};
	}

	/**
	 * Move the current Requirement one up
	 */
	protected void moveRequirementOneUp() {
		IStructuredSelection selected = viewer.getStructuredSelection();
		if (selected != null) {
			Object obj = selected.getFirstElement();
			if (obj instanceof TRCRequirement) {
				TRCRequirement r = (TRCRequirement) obj;
				TRCRequirement tmp = null;
				LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
				int i = reqs.indexOf(r);
				if (i >= 0 && i < reqs.size() - 1) {
					tmp = reqs.get(i + 1);
					reqs.set(i + 1, r);
					reqs.set(i, tmp);
				}

				TRCFileInteraction.WriteReversedTRCsToFile(reqs);
				BoxDecoratorImpl.change();
				updateViewer(reqs);

			}
			viewer.setSelection(selected);
		}
	}

	/**
	 * Move the current Requirement to the top
	 */
	protected void moveToTop() {
		IStructuredSelection selected = viewer.getStructuredSelection();
		if (selected != null) {
			Object obj = selected.getFirstElement();
			if (obj instanceof TRCRequirement) {
				TRCRequirement r = (TRCRequirement) obj;
				LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
				int i = reqs.indexOf(r);
				if (i >= 0 && i < reqs.size() - 1) {
					reqs.remove(i);
					reqs.add(r);
				}
				TRCFileInteraction.WriteReversedTRCsToFile(reqs);
				BoxDecoratorImpl.change();
				updateViewer(reqs);
			}
			viewer.setSelection(selected);
		}
	}

	/**
	 * moves the currently selected Requirement one down.
	 */
	protected void moveRequirementOneDown() {
		IStructuredSelection selected = viewer.getStructuredSelection();
		if (selected != null) {
			Object obj = selected.getFirstElement();
			if (obj instanceof TRCRequirement) {
				TRCRequirement r = (TRCRequirement) obj;
				TRCRequirement tmp = null;
				LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
				int i = reqs.indexOf(r);
				if (i > 0 && i < reqs.size()) {
					tmp = reqs.get(i - 1);
					reqs.set(i - 1, r);
					reqs.set(i, tmp);
				}
				TRCFileInteraction.WriteReversedTRCsToFile(reqs);
				BoxDecoratorImpl.change();
				updateViewer(reqs);
			}
			viewer.setSelection(selected);
		}
	}

	/**
	 * moves the currently selected Requirement to the bottom.
	 */
	protected void moveToBottom() {
		IStructuredSelection selected = viewer.getStructuredSelection();
		if (selected != null) {
			Object obj = selected.getFirstElement();
			if (obj instanceof TRCRequirement) {
				TRCRequirement r = (TRCRequirement) obj;
				LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile();
				int i = reqs.indexOf(r);
				if (i > 0 && i < reqs.size()) {
					reqs.remove(i);
					reqs.addFirst(r);
				}
				TRCFileInteraction.WriteReversedTRCsToFile(reqs);
				BoxDecoratorImpl.change();
				updateViewer(reqs);
			}
			viewer.setSelection(selected);
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
		MessageDialog.openInformation(viewer.getControl().getShell(), "TRC View", message);
	}

	/**
	 * Opens a ColorDialoge Window where the user can change the color of the
	 * Requirement.
	 * 
	 * After the change: - enforces Redraw of boxes - enforces Redraw of the View's
	 * table
	 */
	private void showColorDialoge() {
		IPath path = BoxDecoratorImpl.getCurrentActivePath();
		LinkedList<TRCRequirement> requirements = TRCFileInteraction.ReadTRCsFromFile(path);
		if (requirements == null) {
			return;
		}
		IStructuredSelection selection = viewer.getStructuredSelection();
		Object obj = selection.getFirstElement();
		if (obj instanceof TRCRequirement) {
			TRCRequirement req = (TRCRequirement) obj;
			Shell shell = new Shell();
			ColorDialog dlg = new ColorDialog(shell);
			for (TRCRequirement trcRequirement : requirements) {
				if (trcRequirement.getId().equals(req.getId())) {
					dlg.setRGB(trcRequirement.getColor().getRGB());
					RGB rgb = dlg.open();
					if (rgb != null) {
						trcRequirement.setColor(new Color(null, rgb.red, rgb.green, rgb.blue));
					}
				}
			}
			TRCFileInteraction.WriteReversedTRCsToFile(requirements, path);
			BoxDecoratorImpl.change();

			refreshed();
		} else {
			showMessage("Object is no instance of TRCRequirment: Double-click detected on " + obj.toString());
		}
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public static void refreshed() {
//		System.out.println("Refreshed");
		viewer.getTable().setFocus();
		IWorkbenchWindow window = workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window == null ? null : window.getActivePage();
		IEditorPart editor = activePage == null ? null : activePage.getActiveEditor();
		if (editor != null) {
			editor.setFocus();
		}
		IViewReference[] references = activePage.getViewReferences();
		for (IViewReference v : references) {
			if (v.getPartName().equals("TRC View")) {
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
