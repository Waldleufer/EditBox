package pm.eclipse.editbox.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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

import pm.eclipse.editbox.Box;
import pm.eclipse.editbox.EditBox;
import pm.eclipse.editbox.IBoxBuilder;
import pm.eclipse.editbox.IBoxDecorator;
import pm.eclipse.editbox.IBoxProvider;
import pm.eclipse.editbox.IBoxSettings;
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
			new Throwable("MAGIC here is the path: " + path).printStackTrace();
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
	 * Calculates the path to the currently opened File.
	 * @return the IPath of the currently opened File
	 */
	public static IPath getCurrentActivePath() {
		// from here on: get the path of the file that is inspected TODO: remove MAGIC
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

		// fill background
		Color bc = settings.getColor(0);
		if (settings.getNoBackground() && oldBackground != null) 
			bc = new Color(null,oldBackground);
		if (bc!=null){
			Rectangle rec = newImage.getBounds();		
			fillRectangle(bc, gc, rec.x, rec.y, rec.width, rec.height);
		}

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
			//			TODO: Remove Sysout
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

		//TODO : Think about a smarter way of merging the colors:
		//TODO IDEA: Create Extra "Requirements" that conist of merged ones e.g. R01+R02
		// and then the Boxes where they shall be. Or Just use Patterns.
		//Color a = gc.getBackground();
		//System.out.println("Color before: " + a.toString() + "; Color afterwards: " + c.toString());
		//Color merge = new Color(null, (a.getRed() + c.getRed()) / 2,(a.getGreen() + c.getGreen()) / 2, (a.getBlue() + c.getBlue()) / 2, 100);
		//gc.setBackground(merge);
		//Pattern z = gc.getBackgroundPattern();

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

		//TODO: Add Tooltip Info of Requirements
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
				Rectangle rec2 = new Rectangle(s.x + xOffset -2, s.y + yOffset-1, e.x - s.x + 6, e.y - s.y + boxText.getLineHeight(b.end));
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
		for (Box b : visibleBoxes()) {
			if (contains(b.rec,x, y)) {
				if (!b.isOn) {
					b.isOn = true;
					redraw = true;
				}
				if (newCurrent == null || newCurrent.offset < b.offset)
					newCurrent = b;
			} else if (b.isOn) {
				b.isOn = false;
				redraw = true;
			}
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
	class BoxTextChangeListener implements TextChangeListener {

		/**
		 * This method is actualising all existing Boxes and writing them back to the .trc file.
		 * @param event
		 */
		private void actualizeBoxes(TextChangingEvent event) {
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
			//			LinkedList<TRCRequirement> activeRequirements = TRCFileInteraction.getActiveTRCRequirements(reqs);


			for(TRCRequirement r : reqs) {
				boolean active = r.isActive();
				boolean changeHandled = false;
				LinkedList<int[]> pairs = r.getPositions();
				int[] toBeAddedFirst = null;
				int splitIndex = -1;
				int[] splitleft = null;
				int[] splitright = null;

				// No occurrence of this requirement yet -> Create new pair if active
				if(pairs.size() <= 0) {
					System.out.println("pairs.size() == 0");
					if (active) {
						int[] changed = {positionOfChange, positionOfChange+amountOfChange};
						pairs.add(changed);
						r.setPositions(pairs);						
					}

				} else { // Requirement does already have at least one occurrence

					for(int[] pair : pairs) {
						if(positionOfChange < pair[0]) {
							/*
							 * As the boxes are sorted ascending, we can tell at this point if the change is happening
							 * before the current box and if we missed to create that box. 
							 */
							if(active && !changeHandled) {
								System.out.println("Active + Missed first Box");
								// We missed to create one box before the current one
								int[] changed = {positionOfChange, positionOfChange+amountOfChange};
								toBeAddedFirst = changed;
								changeHandled = true;
							}
							//whole box has to be moved because the change occurs before the current box.
							pair[0] = (pair[0] + amountOfChange);
							pair[1] = (pair[1] + amountOfChange);
						} else if (positionOfChange == pair[0]) {
							//change is happening at the start of the current box
							if (active) {
								System.out.println("Active + Inside Box Change! (Start)");
								//Change occurs "inside" of the current box.
								pair[1] = (pair[1] + amountOfChange);
								changeHandled = true;
							} else {
								//whole box has to be moved because the change occurs before the current box.
								pair[0] = (pair[0] + amountOfChange);
								pair[1] = (pair[1] + amountOfChange);
							}
						}
						else if(positionOfChange < pair[1]) {
							//Change occurs inside of the current box.
							if (active) {
								System.out.println("Active + Inside Box Change!");
								//box end needs to be altered. 
								pair[1] = (pair[1] + amountOfChange);
								changeHandled = true;
							} else {
								// Split occurring
								System.out.println("Split!");
								splitIndex = pairs.indexOf(pair);
								int[] changed1 = {pair[0]                          , positionOfChange};
								int[] changed2 = {positionOfChange + amountOfChange, pair[1]         };
								splitleft = changed1;
								splitright = changed2;	
							}
						} else if (positionOfChange == pair[1]) {
							//Change occurs at the end of the box.
							//change is happening at the start of the current box
							if (active) {
								System.out.println("Active + Inside Box Change! (End)");
								//Change occurs "inside" of the current box.
								pair[1] = (pair[1] + amountOfChange);
								changeHandled = true;
							} else {
								// Else: change is outside of the box, nothing has to be done
							}
						}
						// else: the change is happening after the current box -> no action required, successor will handle.
					}
					if (active && !changeHandled) {
						int[] changed = {positionOfChange, positionOfChange+amountOfChange};
						pairs.add(changed);
						changeHandled = true;
					} else if (toBeAddedFirst != null) {
						pairs.addFirst(toBeAddedFirst);
					} else if (splitIndex >= 0) {
						pairs.set(splitIndex, splitright);
						System.out.println(pairs.toString());
						pairs.add(splitIndex, splitleft);
						System.out.println(pairs.toString());
					}

					r.setPositions(pairs);	


					LinkedList<int[]> newpairs = new LinkedList<int[]>();
					// Cleanup Pairs
					int[] pair = null;
					int[] nextPair = null;
					Iterator<int[]> iterator = pairs.iterator();
					if (iterator.hasNext()) {
						System.out.println("One Element found");
						nextPair = (int[]) iterator.next();
					}

					// Start at comparing the first and the second pair
					while (iterator.hasNext()) {
						System.out.println("Next Element found");
						pair = nextPair;
						nextPair = (int[]) iterator.next();

						if (pair[1] <= pair[0]) {
							// Do not add the pair: pair is after change no longer valid
						} else if (pair[1] >= nextPair[0]) {
							//Merge two boxes

							if (pair[1] < nextPair[1]) {
								System.out.println("Omnomnom - eating some pairs");
								int[] toAdd = {pair[0], nextPair[1]};
								while(iterator.hasNext()) { //eating all boxes in between
									nextPair = iterator.next();
									if (toAdd[1] >= nextPair[0] && toAdd[1] < nextPair[1]) {
										toAdd[1] = nextPair[1];
									} else {
										break;
									}
								}
								newpairs.add(toAdd);
							}
						} else { // no change
							newpairs.add(pair);
						}
					}
					//Inspect last pair manually
					if (nextPair[1] <= nextPair[0]) {
						// Do not add the pair - pair is not valid
					} else {
						System.out.println("Last | Single Element found");
						newpairs.add(nextPair);
					}

					System.out.print("    Pairs:");
					for(int[] p : pairs) {
						System.out.print(" " + Arrays.toString(p));
					}
					System.out.println();
					System.out.print("New Pairs:");
					for(int[] p : newpairs) {
						System.out.print(" " + Arrays.toString(p));
					}
					System.out.println();
					r.setPositions(newpairs);
				}		
			}

			TRCFileInteraction.WriteTRCsToFile(reqs, path);

		}

		//TODO: Change Text Changed Text Text has changed event listener
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

	public static void changeBoxes(int positionOfChange, int amountOfChange) {
		IPath path = getCurrentActivePath();
		LinkedList<TRCRequirement> reqs = TRCFileInteraction.ReadTRCsFromFile(path);
		int endOfChange = positionOfChange + amountOfChange;

		for(TRCRequirement r : reqs) {
			boolean active = r.isActive();
			boolean changeHandled = false;
			LinkedList<int[]> pairs = r.getPositions();
			LinkedList<int[]> newPairs = new LinkedList<int[]>();

			// No occurrence of this requirement yet -> Create new pair if active
			if(pairs.size() <= 0) {
				System.out.println("pairs.size() == 0");
				if (active) {
					int[] changed = {positionOfChange, positionOfChange+amountOfChange};
					newPairs.add(changed);
					r.setPositions(newPairs);	
					changeHandled = true;
				}

			} else { // Requirement does already have at least one occurrence

				for(int[] pair : pairs) {
					if (changeHandled) {
						int[] lastPair = newPairs.getLast();
						if (lastPair[1] >= pair[0]) {
							System.out.println("Fusion!");
							if (pair[1] > lastPair[1]) {
								lastPair[1] = pair[1];								
							}
						} else {
							newPairs.add(pair);							
						}
					} else {
						if(positionOfChange < pair[0]) {
							if (endOfChange > pair[0]) {
								// the selections right side overlaps with this box' left side
								if (endOfChange >= pair[1]) {
									//The selection overlaps the box completely
									if (active) {
										pair[0] = positionOfChange;
										pair[1] = endOfChange;
										newPairs.add(pair);
										changeHandled = true;
									} else {
										// do NOT add the box to the new list
									}
								}
								else if(active) {
									//Add start to box
									pair[0] = positionOfChange;	
									newPairs.add(pair);	
									changeHandled = true;
								} else {
									//Remove everything from start to end from selection 
									//TODO: Check if the end has to be in or excluded
									pair[0] = endOfChange;
									newPairs.add(pair);	
									changeHandled = true;
								}
							} else {
								// No overlapp
								if(active && !changeHandled) {
									// New Box has to be created
									int[] insert = {positionOfChange, endOfChange};
									newPairs.add(insert); //add the new box
									changeHandled = true;
								}
								newPairs.add(pair); // append this box
							}
						} else if(positionOfChange <= pair[1]) {
							//Change occurs inside of the current box.
							if (active && !changeHandled) {
								System.out.println("Active + Inside Box Change!");
								//box end needs to be altered. 
								if(endOfChange >= pair[1]) {
									pair[1] = endOfChange;
								}
								// else no change needed
								newPairs.add(pair);	
								changeHandled = true;
							} else {
								// Not active: Deletion of trc info of selection requested
								System.out.println("Delete!");
								if(positionOfChange > pair[0]) {
									//handle remainder on left side
									int[] insert = {pair[0], positionOfChange};
									newPairs.add(insert);
									changeHandled = true;
								}
								if (endOfChange < pair[1]) {
									//handle remainder on right side
									int[] insert = {endOfChange, pair[1]};
									newPairs.add(insert);
									changeHandled = true;
								}
							}
						} else {
							// the change is happening after the current box -> no action required, successor will handle.
							newPairs.add(pair);
						}
					}


				}
				if (active && !changeHandled) {
					// handle insertion after last boxes
					int[] insert = {positionOfChange, positionOfChange+amountOfChange};
					newPairs.add(insert);
					changeHandled = true;
				} 

				System.out.print("    Pairs:");
				for(int[] p : pairs) {
					System.out.print(" " + Arrays.toString(p));
				}
				System.out.println();
				System.out.print("New Pairs:");
				for(int[] p : newPairs) {
					System.out.print(" " + Arrays.toString(p));
				}
				System.out.println();
				r.setPositions(newPairs);
			}
		}
		
		TRCFileInteraction.WriteTRCsToFile(reqs, path);
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
