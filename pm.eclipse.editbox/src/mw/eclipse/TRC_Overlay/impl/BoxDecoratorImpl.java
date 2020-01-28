/**
 * Copyright (c) 2020 Martin Wagner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.spdx.org/licenses/EPL-1.0
 * 
 * Contributors
 * @author Piotr Metel
 * @author Paul Verest 
 * @author Martin Wagner : Modified the Builder and ChangeListener to Support the TRCOverlay
 */
package mw.eclipse.TRC_Overlay.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import mw.eclipse.TRC_Overlay.views.TRCView;
import pm.eclipse.editbox.Box;
import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.IBoxBuilder;
import pm.eclipse.editbox.IBoxDecorator;
import pm.eclipse.editbox.IBoxProvider;
import pm.eclipse.editbox.IBoxSettings;
import pm.eclipse.editbox.impl.TRCFileInteraction;
import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

public class BoxDecoratorImpl implements IBoxDecorator {

	protected static final int ROUND_BOX_ARC = 5;
	protected IBoxProvider provider;
	protected boolean visible;
	protected IBoxSettings settings;
	protected StyledText boxText;
	protected BoxKeyListener boxKey;
	protected BoxModifyListener boxModify;
	protected BoxPaintListener boxPaint;
	protected BoxMouseMoveListener boxMouseMove;
	protected BoxMouseTrackListener boxMouseTrack;
	protected BoxTextChangeListener boxTextChange;
	protected BoxMouseClickListener boxMouseClick;
	protected FillBoxMouseClick fillMouseClick;
	protected SettingsChangeListener settingsChangeListener;
	protected RGB oldBackground;
	protected int oldIndent;
	protected boolean decorated;
	protected static List<List<Box>> boxes;
	protected static boolean setCaretOffset;
	protected String builderName;
	protected IBoxBuilder builder;
	protected Box currentBox;
	protected Point oldCaretLoc;
	protected int oldXOffset = -1;
	protected int oldYOffset = -1;
	protected Rectangle oldClientArea;
	protected int fillBoxStart = -1;
	protected int fillBoxEnd = -1;
	protected int fillBoxLevel = -1;
	protected int stateMask;
	public boolean keyPressed;
	protected int charCount;
	protected IPath path; //The absolute path of the file that is active in the Editor
	
	// Boundaries of Boxes can be customised here 0 makes the box cling exactly to
	// the text. higher values widen the box about the specified number of pixels.
	final private int RIGHT_SIDE_BOUNDARY = 1;
	final private int LEFT_SIDE_BOUNDARY = 1; 
	final private int TOP_SIDE_BOUNDARY = 1;
	final private int BOTTOM_SIDE_BOUNDARY = 0;


	public void enableUpdates(boolean flag) {
		boolean update = flag && !this.visible;
		this.visible = flag;
		if (update){
			boxes = null;
			update();
		}
	}

	public IBoxProvider getProvider() {
		return provider;
	}

	public void setProvider(IBoxProvider newProvider) {
		this.provider = newProvider;
	}

	public void setSettings(IBoxSettings newSettings) {
		this.settings = newSettings;
		settingsChangeListener = new SettingsChangeListener();
		this.settings.addPropertyChangeListener(settingsChangeListener);
	}

	public void setStyledText(StyledText newSt) {
		this.boxText = newSt;
	}

	/**
	 * This is where the magic happens and the currentBoxes are being built
	 */
	protected void buildBoxes() {
		IBoxBuilder boxBuilder = getBuilder();
		if (boxBuilder == null)
			return;

		IPath path = getCurrentActivePath();

		if (path != null)
		{
			builder.setFilePath(path);
			this.path = path;
		}


		builder.setTabSize(boxText.getTabs());
		builder.setCaretOffset(setCaretOffset?boxText.getCaretOffset():-1);
		setCaretOffset = false;

		StringBuilder text = new StringBuilder(boxText.getText());

		if (text.length() > 0 && text.charAt(text.length()-1)!='\n')
			text.append(".");

		boxBuilder.setText(text);

		boxes = boxBuilder.build();

		charCount = boxText.getCharCount();
	}

	/**
	 * Calculates the path to the currently opened File in the editor, if any.
	 * @return the IPath of the currently opened File or null
	 */
	public static IPath getCurrentActivePath() {
		// from here on: get the path of the file that is currently inspected
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = 
				workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = 
				window == null ? null : window.getActivePage();		
		IEditorPart editor = 
				activePage == null ? null : activePage.getActiveEditor();
		IEditorInput input = 
				editor == null ? null : editor.getEditorInput();
		IPath path = input instanceof IPathEditorInput ? ((IPathEditorInput)input).getPath() : null;
		return path;
	}

	protected void updateOffsetColors() {
		int maxLevel = 0;
		for (List<Box> list : boxes) {
			for (Box b : list) {
				if (b.level > maxLevel)
					maxLevel = b.level;
			}
		}
		settings.setColorsSize(maxLevel + 2);
	}

	protected IBoxBuilder getBuilder() {
		if (settings.getBuilder() == null)
			return null;
		if (builder == null || builderName == null || !builderName.equals(settings.getBuilder())) {
			builderName = settings.getBuilder();
			builder = provider.createBoxBuilder(builderName);
		}
		return builder;
	}

	protected void update() {
		if (decorated && visible) {
			if (builder !=null && (!builderName.equals(settings.getBuilder()) || builder.getTabSize() != boxText.getTabs()))
				boxes = null;

			if (boxes == null)
				buildBoxes();

			offsetMoved();
			updateCaret();
			drawBackgroundBoxes();
		}
	}

	void drawBackgroundBoxes() {
		if (boxes == null || !visible)
			return;

		Rectangle r0 = boxText.getClientArea();

		if (r0.width < 1 || r0.height < 1)
			return;

		int xOffset = boxText.getHorizontalPixel();
		int yOffset = boxText.getTopPixel();

		Image newImage = new Image(null, r0.width, r0.height);
		GC gc = new GC(newImage);

		// fill background //TODO: Evtl. remove this. The TRCOverlay does not use the settings page yet.
		Color bc = settings.getColor(0);
		if (settings.getNoBackground() && oldBackground != null) 
			bc = new Color(null,oldBackground);
		if (bc!=null){
			Rectangle rec = newImage.getBounds();		
			fillRectangle(bc, gc, rec.x, rec.y, rec.width, rec.height);
		}

		// TODO: It might be possible do a implementation of opacity instead of the
		// Layer system
		// But that would mess up the clear identification as the user can not easily
		// say what colours are mixed
		if (settings.getAlpha()>0)
			gc.setAlpha(settings.getAlpha());

		// fill currentBoxes
		Box fillBox = null;
		boolean checkFillbox = !settings.getFillOnMove();
		Collection<Box> visibleBoxes = visibleBoxes();

		boolean ex = settings.getExpandBox();

		/**
		 * This Loop below is filling the visible Boxes with the color
		 */
		for (Box b : visibleBoxes) {
			if (checkFillbox && b.level == fillBoxLevel && b.start <= fillBoxStart && b.end >=fillBoxEnd)
				fillBox = b;

			//Old version:
			//fillRectangle(settings.getColor(b.level + 1), gc, b.rec.x - xOffset, b.rec.y - yOffset, ex?r0.width:b.rec.width, b.rec.height);
			//			System.out.println("FARBEN: " + b.getColor().toString());
			Color c = b.getColor();
			Color transparent = new Color(null, c.getRed(), c.getGreen(), c.getBlue(), 100);
			fillRectangle(transparent, gc, b.rec.x - xOffset, b.rec.y - yOffset, ex?r0.width:b.rec.width, b.rec.height);
		}

		// fill selected
		if (settings.getFillSelected() ) {
			if (settings.getFillOnMove() && currentBox != null && stateMask == settings.getFillKeyModifierSWTInt())
				fillRectangle(settings.getFillSelectedColor(), gc, currentBox.rec.x - xOffset, currentBox.rec.y - yOffset, ex?r0.width:(currentBox.rec.width + 1), (currentBox.rec.height + 1));
			else if (fillBox != null)
				fillRectangle(settings.getFillSelectedColor(), gc, fillBox.rec.x - xOffset, fillBox.rec.y - yOffset, ex?r0.width:(fillBox.rec.width + 1), (fillBox.rec.height + 1));
		}

		for (Box b : visibleBoxes)
			if (!b.isOn)
				drawBox(gc, yOffset, xOffset, b, r0.width);

		for (Box b : visibleBoxes)
			if (b.isOn)
				drawBox(gc, yOffset, xOffset, b, r0.width);

		Image oldImage = boxText.getBackgroundImage();
		boxText.setBackgroundImage(newImage);
		if (oldImage != null)
			oldImage.dispose();
		gc.dispose();

		oldClientArea = r0;
		oldXOffset = xOffset;
		oldYOffset = yOffset;
	}

	protected void drawBox(GC gc, int yOffset, int xOffset, Box b, int exWidth) {
		drawRect(gc, b, b.rec.x - xOffset, b.rec.y - yOffset, settings.getExpandBox() ? exWidth : b.rec.width, b.rec.height);
	}

	private void drawRect(GC gc, Box b, int x, int y, int width, int height) {
		if (b.isOn && settings.getHighlightWidth() > 0 && settings.getHighlightColor(b.level) != null) {
			gc.setLineStyle(settings.getHighlightLineStyleSWTInt());
			gc.setLineWidth(settings.getHighlightWidth());
			gc.setForeground(settings.getHighlightColor(b.level));
			if (settings.getHighlightDrawLine())
				gc.drawLine(x, y, x,  y + b.rec.height);
			else{
				//3D
				//gc.drawLine(x-1, y+3, x-1,  y + b.rec.height+1);
				//gc.drawLine(x-1, y + b.rec.height +1, x+b.rec.width-1,  y + b.rec.height +1);
				//gc.drawPoint(x, y+b.rec.height);
				drawRectangle(gc, x, y, width, height);
			}
		} else if (!b.isOn && settings.getBorderWidth() > 0 && settings.getBorderColor(b.level) != null) {
			gc.setLineStyle(settings.getBorderLineStyleSWTInt());
			gc.setLineWidth(settings.getBorderWidth());
			gc.setForeground(settings.getBorderColor(b.level));
			if (settings.getBorderDrawLine())
				gc.drawLine(x, y+1, x,  y + b.rec.height-1);
			else{
				drawRectangle(gc, x, y, width, height);
			}
		}
	}

	void drawRectangle(GC gc, int x, int y, int width, int height) {
		if (settings.getRoundBox())
			gc.drawRoundRectangle(x, y, width, height, ROUND_BOX_ARC, ROUND_BOX_ARC);
		else
			gc.drawRectangle(x, y, width, height);
	}

	/**
	 * fills the rectangle with the given Color, overpainting everything that might have been there before.
	 * @param c
	 * @param gc
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	void fillRectangle(Color c, GC gc, int x, int y, int width, int height) {
		if (c == null)
			return;

		gc.setBackground(c);

		if (settings.getRoundBox()){
			gc.fillRoundRectangle(x, y, width, height, ROUND_BOX_ARC, ROUND_BOX_ARC);
		}
		else {
			if (settings.getFillGradient() && settings.getFillGradientColor()!=null) {
				gc.setBackground(settings.getFillGradientColor());
				gc.setForeground(c);
				gc.fillGradientRectangle(x, y, width, height, false);
			}else {
				gc.fillRectangle(x, y, width, height);
			}
		}
	}

	public void decorate(boolean mouseDbClickColorChange) {
		decorated = false;
		if (boxText == null || settings == null)
			return;

		boxPaint = new BoxPaintListener();
		boxMouseMove = new BoxMouseMoveListener();
		boxMouseTrack = new BoxMouseTrackListener();
		boxTextChange = new BoxTextChangeListener();
		fillMouseClick = new FillBoxMouseClick();
		boxKey = new BoxKeyListener();
		boxModify = new BoxModifyListener();

		if (mouseDbClickColorChange)
			boxMouseClick = new BoxMouseClickListener();

		Color c = boxText.getBackground();
		if (c != null)
			oldBackground = c.getRGB();
		oldIndent = boxText.getIndent();
		if (oldIndent < 3)
			boxText.setIndent(3);
		boxText.addPaintListener(boxPaint);
		boxText.addMouseMoveListener(boxMouseMove);
		boxText.addMouseTrackListener(boxMouseTrack);
		boxText.getContent().addTextChangeListener(boxTextChange);
		boxText.addMouseListener(fillMouseClick);
		boxText.addModifyListener(boxModify);
		boxText.addKeyListener(boxKey);

		if (mouseDbClickColorChange)
			boxText.addMouseListener(boxMouseClick);

		decorated = true;
	}

	public void undecorate() {
		if (boxText == null && !decorated)
			return;
		decorated = false;
		if (boxMouseClick != null)
			boxText.removeMouseListener(boxMouseClick);
		boxText.getContent().removeTextChangeListener(boxTextChange);
		boxText.removeMouseTrackListener(boxMouseTrack);
		boxText.removeMouseMoveListener(boxMouseMove);
		boxText.removePaintListener(boxPaint);
		boxText.removeMouseListener(fillMouseClick);
		boxText.removeModifyListener(boxModify);
		boxText.removeKeyListener(boxKey);
		boxText.setIndent(oldIndent);
		boxText.setBackgroundImage(null);
		if (oldBackground != null)
			boxText.setBackground(new Color(null,oldBackground));
		else
			boxText.setBackground(null);
		if (settingsChangeListener!=null)
			settings.removePropertyChangeListener(settingsChangeListener);
	}

	protected Collection<Box> visibleBoxes() {
		Rectangle r0 = boxText.getClientArea();
		int start = boxText.getHorizontalIndex() + boxText.getOffsetAtLine(boxText.getTopIndex());
		int end = boxText.getCharCount() -1;
		int lineIndex = boxText.getLineIndex(r0.height);
		if (lineIndex < boxText.getLineCount()-1)
			end = boxText.getOffsetAtLine(lineIndex);

		List<Box> result = new ArrayList<Box>();
		for (List<Box> list : boxes) {
			for (Box b : list)
				if (b.intersects(start, end))
					result.add(b);
		}
		calcBounds(result);
		return result;
	}

	/**
	 * Adds a boundary to the Collection of Boxes.
	 * 
	 * @param boxes0 - The Collection of Boxes that shall be inflicted with boundaries
	 */
	protected void calcBounds(Collection<Box> boxes0) {
		int yOffset = boxText.getTopPixel();
		int xOffset = boxText.getHorizontalPixel();
		for (Box b : boxes0) {
			if (b.rec == null) {
				Point s = boxText.getLocationAtOffset(b.start);
				if (b.tabsStart > -1 && b.tabsStart != b.start) {
					Point s1 = boxText.getLocationAtOffset(b.tabsStart);
					if (s1.x < s.x)
						s.x = s1.x;
				}
				Point e = boxText.getLocationAtOffset(b.end);
				if (b.end != b.maxEndOffset) {
					Point e1 = boxText.getLocationAtOffset(b.maxEndOffset);
					e.x = e1.x;
				}
				
				Rectangle rec2 = new Rectangle(
						s.x + xOffset - LEFT_SIDE_BOUNDARY, 
						s.y + yOffset - TOP_SIDE_BOUNDARY,
						e.x - s.x + RIGHT_SIDE_BOUNDARY,
						e.y - s.y + boxText.getLineHeight(b.end) + BOTTOM_SIDE_BOUNDARY);
				b.rec = rec2;
				updateWidth(b);
				updateWidth3(b);
			}
		}
	}

	void updateWidth(Box b) {
		Box p = b.parent;
		while (p != null && p.rec != null && p.rec.x + p.rec.width <= b.rec.x + b.rec.width) {
			p.rec.width += 5;
			b = p;
			p = p.parent;
		}
	}

	void updateWidth3(Box b) {
		Box p = b.parent;
		while (p != null && p.rec != null && p.rec.x >= b.rec.x) {
			p.rec.width += p.rec.x - b.rec.x + 3;
			p.rec.x = b.rec.x - 3 > 0 ? b.rec.x - 3:0 ;
			b = p;
			p = p.parent;
		}
	}

	protected boolean turnOnBox(int x0, int y0) {
		if (boxes == null || !visible)
			return false;

		int x = x0 + boxText.getHorizontalPixel();
		int y = y0 + boxText.getTopPixel();

		return settings.getHighlightOne() ? turnOnOne(x, y) : turnOnAll(x, y);
	}

	protected boolean turnOnAll(int x, int y) {
		boolean redraw = false;

		Box newCurrent = null;
		
		LinkedList<TRCRequirement> newSelection = new LinkedList<TRCRequirement>();
		
		for (Box b : visibleBoxes()) {
			if (contains(b.rec,x, y)) {
				if (!b.isOn) {
					b.isOn = true;
					redraw = true;
				}
				if(!newSelection.contains(b.getRequirement())) {
					//Notifies the TRC View to mark these as selected.
					newSelection.add(b.getRequirement());						
				}
				if (newCurrent == null || newCurrent.offset < b.offset)
					newCurrent = b;
			} else if (b.isOn) {
				b.isOn = false;
				newSelection.remove(b.getRequirement());
				if(newSelection.isEmpty()) {
					TRCView.getViewer().setSelection(new StructuredSelection(), true);	
				}
				redraw = true;
			}
		}
		if(!newSelection.isEmpty()) {
			TRCView.getViewer().setSelection(new StructuredSelection(newSelection), true);			
		} else {
			// No change, do nothing.
		}
		
		if (!redraw)
			redraw = newCurrent != currentBox;
		currentBox = newCurrent;

		return redraw;
	}

	protected boolean turnOnOne(int x, int y) {
		Box newCurrent = null;
		for (Box b : visibleBoxes()) {
			if (contains(b.rec,x,y))
				newCurrent = b;
			b.isOn = false;
		}
		if (newCurrent != null)
			newCurrent.isOn = true;
		boolean redraw = newCurrent != currentBox;
		currentBox = newCurrent;
		return redraw;
	}

	private boolean contains(Rectangle rec, int x, int y) {
		return (x >= rec.x) && (y >= rec.y) && ((x - rec.x) < rec.width) && ((y - rec.y) < rec.height);
	}

	boolean redrawIfClientAreaChanged() {
		if (oldClientArea == null || !oldClientArea.equals(boxText.getClientArea())){
			drawBackgroundBoxes();
			return true;
		}
		return false;
	}

	void updateCaret() {
		oldCaretLoc = boxText.getLocationAtOffset(boxText.getCaretOffset());
		turnOnBox(oldCaretLoc.x > 0 ? oldCaretLoc.x-1:oldCaretLoc.x, oldCaretLoc.y);
	}

	public boolean offsetMoved() {
		int yOffset = boxText.getTopPixel();
		int xOffset = boxText.getHorizontalPixel();
		if (xOffset != oldXOffset || yOffset != oldYOffset) {
			oldXOffset = xOffset;
			oldYOffset = yOffset;
			return true;
		}
		return false;
	}

	protected void carretMoved() {
		Point newLoc = boxText.getLocationAtOffset(boxText.getCaretOffset());
		if (boxes != null && (oldCaretLoc == null || !oldCaretLoc.equals(newLoc))) {
			oldCaretLoc = newLoc;
			boolean build = false;
			if (!setCaretOffset && builder!=null && builder.getCaretOffset() > -1 && builder.getCaretOffset() != boxText.getCaretOffset()) {
				buildBoxes();
				build = true;
			}
			if (turnOnBox(oldCaretLoc.x > 0? oldCaretLoc.x -1 : oldCaretLoc.x, oldCaretLoc.y) || build) 
				drawBackgroundBoxes();
		}
	}

	private final class BoxModifyListener implements ModifyListener {

		public void modifyText(ModifyEvent e) {
			//it is more efficient to not draw currentBoxes in PaintListner (especially on Linux)
			//and in this event caret offset is correct
			if (boxes == null) {
				buildBoxes();
				updateCaret();
				drawBackgroundBoxes();
			}
		}
	}

	private final class BoxKeyListener implements KeyListener {

		public void keyReleased(KeyEvent e) {
			keyPressed = true;
			carretMoved();
		}

		public void keyPressed(KeyEvent e) {
		}
	}

	class SettingsChangeListener implements IPropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			update();
		}
	}

	class BoxPaintListener implements PaintListener {
		volatile boolean paintMode;

		public void paintControl(PaintEvent e) {
			if (paintMode)
				return;
			paintMode = true;
			try {
				//check charCount as workaround for no event when StyledText.setContent()
				if (boxes == null || charCount != boxText.getCharCount()) {
					buildBoxes();
					updateCaret();
					drawBackgroundBoxes();
				} else if (offsetMoved()) {
					updateCaret();
					drawBackgroundBoxes();
				} else {
					redrawIfClientAreaChanged();
				}
			} catch (Throwable t) {
				EditBox.logError(this, "Box paint error", t);
			} finally {
				paintMode = false;
			}
		}
	}

	class BoxMouseMoveListener implements MouseMoveListener {

		public void mouseMove(MouseEvent e) {
			stateMask = e.stateMask;
			if (turnOnBox(e.x, e.y)) {
				drawBackgroundBoxes();
			}
		}
	}

	class BoxMouseTrackListener implements MouseTrackListener {

		public void mouseEnter(MouseEvent e) {
		}

		public void mouseExit(MouseEvent e) {
			boolean redraw = false;
			if (boxes != null)
				for (List<Box> list : boxes) {
					for (Box b : list) {
						if (b.isOn){
							redraw = true;
							b.isOn = false;
						}
					}
				}
			if (redraw)
				drawBackgroundBoxes();
		}

		public void mouseHover(MouseEvent e) {
		}
	}

	public static void change() {
		boxes = null;
		setCaretOffset = true;
	}

	/**
	 * Change Management
	 * @author Martin Wagner
	 *
	 */
	public class BoxTextChangeListener implements TextChangeListener {

		/**
		 * This method is actualising all existing Boxes and writing them back to the .trc file.
		 * @param event
		 */
		public void actualizeBoxes(TextChangingEvent event) {
			int positionOfChange = event.start;
			int amountOfChange = event.newCharCount - event.replaceCharCount;	
			actualizeBoxes(positionOfChange, amountOfChange);	
		}

		/**
		 * actualises the Boxes after a change at position positionOfChange with the length amountOfChange
		 * @param positionOfChange 		0-indicated position of the change in the source code file
		 * @param amountOfChange		the length of the change in chars.
		 */
		public void actualizeBoxes(int positionOfChange, int amountOfChange) {

			//TODO: remove DEBUG: 
			//System.err.println("Änderung bei: " + positionOfChange + "; Änderungsmenge: " + amountOfChange);
			
			LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile(path);
			
			if (reqs == null) {
				return;
			}

			LinkedList<TRCRequirement> afterChange = BoxActualizerHelper.actualizeBoxes(positionOfChange, amountOfChange, reqs);
			
			TRCFileInteraction.WriteReversedTRCsToFile(afterChange, path);

		}

		/**
		 * This method is being called every time after a Text Change has occured
		 */
		public void textChanged(TextChangedEvent event) {
			change();
		}

		public void textChanging(TextChangingEvent event) {
			actualizeBoxes(event);
		}

		public void textSet(TextChangedEvent event) {
			change();
		}
	}

	
	/**
	 * sets the Requirement Boxes as selected in the Editor
	 * @param positionOfChange
	 * @param amountOfChange
	 */
	public static void changeBoxes(int positionOfChange, int amountOfChange) {
		IPath path = getCurrentActivePath();
		LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile(path);
		if (reqs == null) {
			change();
			return;
		}
		LinkedList<TRCRequirement> result = BoxActualizerHelper.changeBoxes(positionOfChange, amountOfChange, reqs);
		
		TRCFileInteraction.WriteReversedTRCsToFile(result, path);
		change();
	}

	class BoxMouseClickListener extends MouseAdapter {

		public void mouseDoubleClick(MouseEvent e) {
			int x = e.x + boxText.getHorizontalPixel();
			int y = e.y + boxText.getTopPixel();

			int level = -1;
			for (Box b : visibleBoxes())
				if (contains(b.rec,x, y))
					if (level < b.level)
						level = b.level;
			level++;

			ColorDialog colorDialog = new ColorDialog(boxText.getShell());
			Color oldColor1 = settings.getColor(level);
			if (oldColor1 != null)
				colorDialog.setRGB(oldColor1.getRGB());

			settings.setColor(level, colorDialog.open());
		}

	}

	class FillBoxMouseClick extends MouseAdapter {

		public void mouseDown(MouseEvent e) {

			if (e.button != 1 || settings.getFillOnMove() || e.stateMask != settings.getFillKeyModifierSWTInt()){
				if (keyPressed) {
					keyPressed = false;
					carretMoved();
				}
				return;
			}

			int x = e.x + boxText.getHorizontalPixel();
			int y = e.y + boxText.getTopPixel();

			Box fillBox = null;
			for (Box b : visibleBoxes())
				if (contains(b.rec, x, y))
					fillBox = b;

			if (fillBox != null && (fillBox.end != fillBoxEnd ||fillBox.start != fillBoxStart || fillBox.level != fillBoxLevel)){ 
				fillBoxEnd = fillBox.end;
				fillBoxLevel = fillBox.level;
				fillBoxStart = fillBox.start;
			} else {
				fillBoxEnd = -1;
				fillBoxStart = -1;
				fillBoxLevel = -1;
			}

			if (keyPressed) {
				keyPressed = false;
				Point newLoc = boxText.getLocationAtOffset(boxText.getCaretOffset());
				if (oldCaretLoc == null || !oldCaretLoc.equals(newLoc)){
					buildBoxes();
					oldCaretLoc = newLoc;
				}
			}

			drawBackgroundBoxes();
		}
	}

	class BoxSettingsPropertyListner implements IPropertyChangeListener {

		public void propertyChange(PropertyChangeEvent event) {
			update();
		}
	}


	public void selectCurrentBox() {
		if (decorated && visible && boxes != null){
			Box b = null;
			Point p = boxText.getSelection();
			if (p == null || p.x == p.y) 
				b = currentBox;
			else{

				for (List<Box> list : boxes) {
					for (Box box : list)
						if (p.x <= box.start && p.y >= box.end - 1) {
							b = box.parent;
							break;
						} 
				}
			}
			if (b!=null) {
				int end = Character.isWhitespace(boxText.getText(b.end-1, b.end-1).charAt(0))? b.end -1 : b.end;
				boxText.setSelection(b.start, end);
				Event event = new Event();
				event.x = b.start;
				event.y = end;
				boxText.notifyListeners(SWT.Selection, event);
			}
		}
	}

	public void unselectCurrentBox() {
		boxText.setSelection(boxText.getCaretOffset());
	}

}
