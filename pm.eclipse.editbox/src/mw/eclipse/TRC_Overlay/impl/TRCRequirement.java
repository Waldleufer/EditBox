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
package mw.eclipse.TRC_Overlay.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import org.eclipse.swt.graphics.Color;

import mw.eclipse.TRC_Overlay.views.TRCView;

/**
 * The TRCRequirement class defines how TRCRequirements shall be stored
 * internally. In Order to enable simple Read and Write operations this class
 * has to implement {@link Serializable}.
 * 
 * @author Martin
 *
 */
public final class TRCRequirement implements Serializable {
	/**
	 * The generated serialVersionUID of this Object
	 */
	private static final long serialVersionUID = 3704543854027523254L;
	private String id;
	private LinkedList<int[]> positions;
	private int red;
	private int green;
	private int blue;
	private boolean active;
	private String info;

	/**
	 * Constructor for a new {@link TRCRequirement}
	 * 
	 * Creates a new TRCRequirement with the id, and initial boxes. Picks a random
	 * pastel color that can be changed later via {@link TRCView}
	 * 
	 * @param id        - the unique ID of the requirement
	 * @param positions - a list of (start, end) tuples.
	 */
	public TRCRequirement(String id, LinkedList<int[]> positions) {
		this.id = id;
		this.positions = positions;
		this.info = null;
		Random rand = new Random();
		int r = (int) (180.0 + rand.nextFloat() * 76.0);
		int g = (int) (180.0 + rand.nextFloat() * 76.0);
		int b = (int) (180.0 + rand.nextFloat() * 76.0);
		Color randomColor = new Color(null, r, g, b);
		setActive(false);
		// Debug:
		// Color randomColor = new Color(null, 100, 150, 0);
		setColor(randomColor);
	}

	/**
	 * @return the unique id of a specific requirement.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the position Pairs: [Start, End] contained in a list.
	 */
	public LinkedList<int[]> getPositions() {
		return positions;
	}

	/**
	 * sets the new position pairs: [Start, End] contained in a list.
	 */
	public void setPositions(LinkedList<int[]> positions) {
		this.positions = positions;
	}

	@Override
	public String toString() {
		String out = id + ": ";
		for (int[] i : positions) {
			out += Arrays.toString(i) + "; ";
		}
		return out;
	}

	public Color getColor() {
		return new Color(null, red, green, blue);
	}

	public void setColor(Color color) {
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
