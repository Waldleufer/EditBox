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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

/**
 * The BoxActualizerHelper Class offers the actualizeBoxes Method that handles Changes
 * in the Sourcecode. The Boxes are automatically altered according to the
 * change. New Boxes will be created, as well as Boxes removed, split or
 * altered as needed.
 * 
 * @author Martin Wagner
 *
 */
public class BoxActualizerHelper {

	/**
	 * actualises the Boxes after a change at position positionOfChange with the length amountOfChange
	 * @param positionOfChange 		0-indicated position of the change in the source code file
	 * @param amountOfChange		the length of the change in chars.
	 * @param reqs					the requirements and Boxes before the Change. 
	 */
	public static LinkedList<TRCRequirement> actualizeBoxes(int positionOfChange, int amountOfChange, LinkedList<TRCRequirement> reqs) {
		
		if (reqs == null) {
			return null;
		}

		for (TRCRequirement r : reqs) {
//			TODO: remove DEBUGs many Sysouts below:
//			System.out.println("Requirement from Table: " + r.toString());					
			
			boolean active = r.isActive();
			boolean changeHandled = false;
			LinkedList<int[]> pairs = r.getPositions();
			int[] toBeAddedFirst = null;
			int splitIndex = -1;
			int[] splitleft = null;
			int[] splitright = null;

			// No occurrence of this requirement yet -> Create new pair if active
			if(pairs.size() <= 0) {
//				System.out.println("pairs.size() == 0");
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
//							System.out.println("Active + Missed first Box");
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
						if (active && amountOfChange >= 0) {
//							System.out.println("Active + Inside Box Change! (Start)");
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
						if (active && amountOfChange > 0) {
//							System.out.println("Active + Inside Box Change!");
							//box end needs to be altered. 
							pair[1] = (pair[1] + amountOfChange);
							changeHandled = true;
						} else if (amountOfChange <= 0) {
							pair[1] = (pair[1] + amountOfChange);
							changeHandled = true;
						} else {
							// Split occurring
//							System.out.println("Split!");
							splitIndex = pairs.indexOf(pair);
							int[] changed1 = {pair[0]                          , positionOfChange        };
							int[] changed2 = {positionOfChange + amountOfChange, pair[1] + amountOfChange};
							splitleft = changed1;
							splitright = changed2;	
						}
					} else if (positionOfChange == pair[1]) {
						//Change occurs at the end of the box.
						if (active) {
//							System.out.println("Active + Inside Box Change! (End)");
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
//					System.out.println(pairs.toString());
					pairs.add(splitIndex, splitleft);
//					System.out.println(pairs.toString());
				}

				r.setPositions(pairs);	


				LinkedList<int[]> newpairs = new LinkedList<int[]>();
				// Cleanup Pairs
				int[] pair = null;
				int[] nextPair = null;
				Iterator<int[]> iterator = pairs.iterator();
				if (iterator.hasNext()) {
//					System.out.println("One Element found");
					nextPair = (int[]) iterator.next();
				}

				// Start at comparing the first and the second pair
				while (iterator.hasNext()) {
//					System.out.println("Next Element found");
					pair = nextPair;
					nextPair = (int[]) iterator.next();

					if (pair[1] <= pair[0]) {
						// Do not add the pair: pair is after change no longer valid
					} else if (pair[1] >= nextPair[0]) {
						//Merge two boxes
						System.out.println("Merging");
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
						
					} else { // no change
						newpairs.add(pair);
					}
				}
				//Inspect last pair manually
				if (nextPair[1] <= nextPair[0]) {
					// Do not add the pair - pair is not valid
				} else if (!newpairs.isEmpty() && newpairs.getLast()[1] >= nextPair[1]) {
					
				} else {
//					System.out.println("Last | Single Element found");
					newpairs.add(nextPair);
				}

				r.setPositions(newpairs);
				
				// TODO: Remove the following 
				// Debug Sysouts
				for (TRCRequirement trcRequirement : reqs) {
					for(int[] p : trcRequirement.getPositions()) {
						System.out.print(" " + Arrays.toString(p));
					}
					System.out.println();
				}
			}		
		}

		return reqs;

	}
}
