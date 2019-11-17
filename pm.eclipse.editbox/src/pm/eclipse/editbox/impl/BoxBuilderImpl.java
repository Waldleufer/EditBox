package pm.eclipse.editbox.impl;

import org.eclipse.swt.graphics.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;

import com.sun.org.apache.xml.internal.resolver.readers.TR9401CatalogReader;

import pm.eclipse.editbox.Box;
import pm.eclipse.editbox.impl.TRCFileInteraction;
import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;
import pm.eclipse.editbox.views.TRCView;

/**
 * Is able to create and manage boxes This is the entry point for creating the
 * boxes
 *
 */
public class BoxBuilderImpl extends AbstractBoxBuilder {

	protected List<Box> currentBoxes; // The Boxes for one specific Requirement
	protected List<List<Box>> boxes; // All the Boxes for All Requirements for one File
	protected Box currentbox;
	protected Color currentColor;
	protected Box rootbox;
	protected boolean emptyPrevLine;
	protected boolean lineHasStartTab;
	protected IPath filePath; // the Path of the currently inspected file

	/**
	 * This method calculates the starting boundaries of the very first box and all
	 * following currentBoxes
	 */
	public List<Box> buildOld() {
		currentBoxes = new LinkedList<Box>();
		int len = text.length() - 1;
		currentbox = newbox(0, len, -1, null);
		currentBoxes.clear(); // skip root box

		emptyPrevLine = false;
		int start = 0;
		int offset = 0;
		boolean startline = true;
		lineHasStartTab = false;
		boolean empty = true;

		int line = 0;

		checkCarret();

		/**
		 * This for loop traverses over the whole document (represented by a char
		 * stream) and seeks the positions of the first and last occurring Symbol for
		 * each line
		 * 
		 * It then calls addLine with parameter start that represents this position. i
		 * is at that time the line length offset is the calculated offset from this
		 * line empty evaluates to false at that point if the line did not contain only
		 * whitespace
		 * 
		 * it also calculates the beginnings of the currentBoxes this way, as addLine
		 * called with (empty = true) will set (emptyPrevLine = true);
		 */
		for (int i = 0; i <= len; i++) { // TODO: setze i zu Beginn, da boxxen ja gespeichert. und begrenze end.

			char c = text.charAt(i);
			boolean isWhitespace = Character.isWhitespace(c) && i != caretOffset;

			empty = empty && isWhitespace;

			if (c == '\n' || i == len) { // if end of line || EOF is reached
				line++;
				if (startline)
					start = i;
				addLine(start, i, offset, empty);
				startline = true;
				offset = 0;
				start = i;
				lineHasStartTab = false;
				empty = true;
			} else {
				if (startline) {
					if (isWhitespace) {
						if (c == '\t') { // Heading Tab
							offset += tabSize;
							lineHasStartTab = true;
						} else // Heading Space
							offset++;
					} else {
						start = i;
						startline = false;
					}
				}
			}
		}
		return currentBoxes;
	}

	public List<List<Box>> build() {

		currentBoxes = new LinkedList<Box>();
		boxes = new LinkedList<List<Box>>();
		List<TRCRequirement> requirements = null;
		int len = text.length() - 1;
		currentColor = new Color(null, 0, 0, 0);
		currentbox = newbox(0, len, -1, null);
		rootbox = currentbox;
		currentBoxes.clear(); // skip root box

		// TODO: delete line below:  Debug Starting Point
		// TRCFileInteraction.debug(filePath);
		requirements = TRCFileInteraction.ReadTRCsFromFile(filePath);
		TRCView.updateViewer(); //update Viewers context

		emptyPrevLine = false;
		int start = 0;
		int end = 0;
		int offset = 0;
		boolean startline = true;
		lineHasStartTab = false;
		boolean empty = true;

		checkCarret();

		for (TRCRequirement req : requirements) {
			currentBoxes = new LinkedList<Box>();
			//int[] rgb = req.getColor();
			currentColor = req.getColor();
			System.out.println("Requirement Color: " + currentColor.toString());
			new Throwable("TRC Requirement: " + req.toString()).printStackTrace();
			// TODO: handle colour
			List<int[]> pairs = req.getPositions();

			for (int[] pair : pairs) {
				currentbox = rootbox;
				currentbox.setColor(currentColor);
				
				emptyPrevLine = true;
				start = pair[0];
				end = pair[1];
				/**
				 * This for loop below traverses over the whole document (represented by a char
				 * stream) and seeks the positions of the first and last occurring Symbol for
				 * each line
				 * 
				 * It then calls addLine with parameter start that represents this position. i
				 * is at that time the line length offset is the calculated offset from this
				 * line empty evaluates to false at that point if the line did not contain only
				 * whitespace
				 * 
				 * it also calculates the beginnings of the currentBoxes this way, as addLine
				 * called with (empty = true) will set (emptyPrevLine = true);
				 */
				for (int i = start; i <= end; i++) { // setze i zu beginn, da boxen ja gespeichert. und begrenze end.

					char c = text.charAt(i);
					boolean isWhitespace = Character.isWhitespace(c) && i != caretOffset;

					empty = empty && isWhitespace;

					if (c == '\n' || i == len || i == end) { // if end of line || EOF || End of Tracing box
						// line++;
						if (startline)
							start = i;
						addLine(start, i, offset, empty);
						startline = true;
						offset = 0;
						start = i;
						lineHasStartTab = false;
						empty = true;
					} else {
						if (startline) {
							if (isWhitespace) {
								if (c == '\t') { // Heading Tab
									offset += tabSize;
									lineHasStartTab = true;
								} else // Heading Space
									offset++;
							} else {
								start = i;
								startline = false;
							}
						}
					}
				}
			}
			boxes.add(currentBoxes);
		}
		return boxes;
	}

	private void checkCarret() {
		if (caretOffset < 0)
			return;

		if (caretOffset > 0 && text.charAt(caretOffset - 1) == '\n') {
			caretOffset = -1;
			return;
		}

		int end = text.length();
		for (int i = caretOffset; i < end; i++) {
			char c = text.charAt(i);
			if (!Character.isWhitespace(c)) {
				caretOffset = -1;
				return;
			}
			if (c == '\n')
				return;
		}
	}

	/**
	 * If a line is not empty addbox0 is evaluated.
	 * 
	 * @param start  - the starting character in this line
	 * @param end    - the line length
	 * @param offset - the offset of this whole box
	 * @param empty  - whether the found box was empty
	 * 
	 *               also emptyPrevLine is set to true, if this line was empty.
	 */
	protected void addLine(int start, int end, int offset, boolean empty) {
		if (!empty) {
			addbox0(start, end, offset);
		}
		emptyPrevLine = empty;
	}

	/**
	 * This little code snippet creates all the Boxes for every opened file in the
	 * Project window - highly recursive! - magically reactive!
	 * 
	 * @param start
	 * @param end
	 * @param offset
	 */
	// TODO: Old addbox0 - Remove @martin
	protected void addbox1(int start, int end, int offset) {

		if (offset == currentbox.offset) {
			if ((emptyPrevLine && currentbox.parent != null)) {
				currentbox = newbox(start, end, offset, currentbox.parent);
				updateParentEnds(currentbox);
			} else if (end > currentbox.end) {
				currentbox.end = end;
				if (currentbox.tabsStart < 0 && lineHasStartTab)
					currentbox.tabsStart = start;
				updateMaxEndOffset(start, currentbox);
				updateParentEnds(currentbox);
			}
		} else if (offset > currentbox.offset) {
			currentbox = newbox(start, end, offset, currentbox);
			updateParentEnds(currentbox);
		} else if (currentbox.parent != null) {
			currentbox = currentbox.parent;
			addbox0(start, end, offset);
		}

	}

	protected void addbox0(int start, int end, int offset) {
		System.err.println("BOX: " + currentbox.toString());

		if (offset == currentbox.offset) { // Same indentation level
			if ((emptyPrevLine && currentbox.parent != null)) { // handles empty lines on same indentation level
				if (offset <= 4) {
					currentbox = newbox(start, end, offset, currentbox.parent);
					updateParentEnds(currentbox);
				} else {
					currentbox = invisibleNewbox(start, end, offset, currentbox.parent);
					updateParentEnds(currentbox);
				}
			} else if (end > currentbox.end) { // increases width of boxes and extends boxes line numbers
				currentbox.end = end;
				if (currentbox.tabsStart < 0 && lineHasStartTab)
					currentbox.tabsStart = start;
				updateMaxEndOffset(start, currentbox);
				updateParentEnds(currentbox);
			}
		} else if (offset > currentbox.offset) { // handle bigger indent
			if (currentbox.level < 0) { // we only want one level of boxes to be visible
				currentbox = newbox(start, end, offset, currentbox);
				updateParentEnds(currentbox);
			} else {
				currentbox = invisibleNewbox(start, end, offset, currentbox);
				updateParentEnds(currentbox);
			}
//			currentbox = newbox(start, end, offset, currentbox);
//			updateParentEnds(currentbox);
		} else if (currentbox.parent != null) { // running from inside out, biggest indent to lowest.
			currentbox = currentbox.parent;
			addbox0(start, end, offset);
		}

	}

	/**
	 * Enlarges the EndOffset of a <code>Box</code> if necessary. If a following
	 * line is wider than a starting line the box needs to be widened
	 * 
	 * @param start - the starting point
	 * @param b     - the box whose EndOffsett has to be adjusted
	 */
	protected void updateMaxEndOffset(int start, Box b) {
		int n = b.end - start + b.offset;
		if (n >= b.maxLineLen) {
			b.maxLineLen = n;
			b.maxEndOffset = b.end;
		}
	}

	/**
	 * If a (child)box is wider than its parent, the parent has to be widened
	 * 
	 * @param box - the box whose parents have to be widened
	 */
	protected void updateParentEnds(Box box) {
		Box b = box.parent;
		while (b != null && b.end < box.end) {
			b.end = box.end;
			if (b.maxLineLen <= box.maxLineLen) {
				b.maxEndOffset = box.maxEndOffset;
				b.maxLineLen = box.maxLineLen;
			}
			b = b.parent;
		}

	}

	/**
	 * Creates a new <code>Box</code>
	 * 
	 * @param start
	 * @param end    - the width of the box (number of chars - 1)
	 * @param offset
	 * @param parent - the box in that this box will be placed
	 * @return <code>Box</code> - the new box
	 */
	protected Box newbox(int start, int end, int offset, Box parent) {
		Box box = new Box();
		box.end = end;
		box.start = start;
		System.out.println("CURRENT COLOR IS: " + currentColor.toString());
		box.setColor(currentColor);
		box.offset = offset;
		box.parent = parent;
		box.maxLineLen = end - start + offset;
		box.maxEndOffset = end;
		box.level = parent != null ? parent.level + 1 : -1;
		if (lineHasStartTab)
			box.tabsStart = start;
		if (parent != null)
			parent.hasChildren = true;
		// TODO: Remove Debug
		currentBoxes.add(box);
		return box;
	}

	/**
	 * Creates a new <code>Box</code>
	 * 
	 * @param start
	 * @param end    - the width of the box (number of chars - 1)
	 * @param offset
	 * @param parent - the box in that this box will be placed
	 * @return <code>Box</code> - the new box
	 */
	protected Box invisibleNewbox(int start, int end, int offset, Box parent) {
		Box box = new Box();
		box.end = end;
		box.start = start;
		box.offset = offset;
		box.parent = parent;
		box.maxLineLen = end - start + offset;
		box.maxEndOffset = end;
		box.level = parent != null ? parent.level + 1 : -1;
		if (lineHasStartTab)
			box.tabsStart = start;
		if (parent != null)
			parent.hasChildren = true;
		// TODO: Remove Debug
		// currentBoxes.add(box); // Don't add it - makes it invisible
		return box;
	}

	public void setFilePath(IPath filePath) {
		this.filePath = filePath;

	}

}
