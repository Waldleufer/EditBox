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
 * @author Martin Wagner : Modified the Builder to Support the TRCOverlay
 * 
 */

package mw.eclipse.TRC_Overlay.impl;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Color;

import pm.eclipse.editbox.Box;
import pm.eclipse.editbox.impl.AbstractBoxBuilder;
import pm.eclipse.editbox.impl.TRCFileInteraction;
import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

/**
 * Is able to create and manage boxes.
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
	public List<List<Box>> build() {

		currentBoxes = new LinkedList<Box>();
		boxes = new LinkedList<List<Box>>();
		List<TRCRequirement> requirements = null;
		int len = text.length() - 1;
		currentColor = new Color(null, 0, 0, 0);
		currentbox = newbox(0, len, -1, null);
		rootbox = currentbox;
		currentBoxes.clear(); // skip root box

		requirements = TRCFileInteraction.ReadTRCsFromFileAndUpdate(filePath);
		if (requirements == null) {
			// return the empty Linked List
			return boxes;
		}

		emptyPrevLine = false;
		int start = 0;
		int end = 0;
		int offset = 0;
		boolean startline = true;
		lineHasStartTab = false;
		boolean empty = true;

		checkCarret();

		/**
		 * This for loop traverses over the all requirements
		 */
		for (TRCRequirement req : requirements) {
			currentBoxes = new LinkedList<Box>();
			currentColor = req.getColor();
			List<int[]> pairs = req.getPositions();

			/**
			 * this loop reads the begin and end position in the char stream of the file or
			 * every box.
			 */
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
				 * It then calls addLine with parameter start that represents this position.
				 * <code>i</code> is at that time the index of the current character from the
				 * stream. <code>offset</code> is the calculated offset from this line.
				 * <code>empty</code> is false at that point if the line did NOT only contain
				 * whitespace
				 * 
				 * it also calculates the beginnings of the currentBoxes this way, as addLine
				 * called with (empty = true) will set (emptyPrevLine = true);
				 */
				for (int i = start; i <= end; i++) { // setze i zu beginn, da boxen ja gespeichert. und begrenze end.
					if (i >= text.length() || i < 0) {
						// Do not attempt to draw after EOF. Or before file start
						break;
					}
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
			for (Box b : currentBoxes) {
				b.setRequirement(req); // Make every box aware of the ReqID it represents
				System.err.println(b.toString() + " " + b.getRequirement().toString());
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
	 * Project window - recursive!
	 * 
	 * @param start
	 * @param end
	 * @param offset
	 */
	protected void addbox0(int start, int end, int offset) {
		System.err.println("BOX: " + currentbox.toString());

		if (offset == currentbox.offset) { // Same indentation level
			if ((emptyPrevLine && currentbox.parent != null)) { // handles empty lines on same indentation level
					currentbox = invisibleNewbox(start, end, offset, currentbox.parent);
					updateParentEnds(currentbox);
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
		// currentBoxes.add(box); // Don't add it - makes it invisible
		return box;
	}

	public void setFilePath(IPath filePath) {
		this.filePath = filePath;

	}

}
