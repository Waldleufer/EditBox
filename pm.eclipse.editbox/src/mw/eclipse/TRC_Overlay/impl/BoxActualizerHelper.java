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

import org.eclipse.core.runtime.IPath;

import pm.eclipse.editbox.impl.TRCFileInteraction;
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
			System.out.println("Requirement from Table: " + r.toString());		
			System.out.println("Corresponding Action: " + positionOfChange + "; amount: " + amountOfChange);
			
			boolean active = r.isActive();
			boolean changeHandled = false;
			LinkedList<int[]> pairsList = r.getPositions();
			int[] toBeAddedFirst = null;
			int splitIndex = -1;
			int[] splitleft = null;
			int[] splitright = null;

			// No occurrence of this requirement yet -> Create new pair if active
			if(pairsList.size() <= 0) {
//				System.out.println("pairs.size() == 0");
				if (active) {
					int[] changed = {positionOfChange, positionOfChange+amountOfChange};
					pairsList.add(changed);
					r.setPositions(pairsList);						
				}

			} else { // Requirement does already have at least one occurrence

				for(int[] pair : pairsList) {
					
					if(amountOfChange < 0) {
						// Deletion Modus
						changeHandled = true;  // No post processing required when deleting.
//						System.out.println("DELETING");
						
						int startOfAffectedArea = positionOfChange;
						int endOfAffectedArea = positionOfChange - amountOfChange;
						if(endOfAffectedArea <= pair[0]) {
							// Move Box. Deletion before this box
							pair[0] = (pair[0] + amountOfChange);
							pair[1] = (pair[1] + amountOfChange);
						} else if (endOfAffectedArea > pair[0]) {
							// this Box is affected
							if(startOfAffectedArea <= pair[0] && endOfAffectedArea <= pair[1]) {
								// left side is cut
								pair[0] = startOfAffectedArea;
								pair[1] = (pair[1] + amountOfChange);
							} else if (startOfAffectedArea > pair[0] && endOfAffectedArea <= pair[1]) {
								// Inside deletion
								pair[1] = (pair[1] + amountOfChange);
							}
							else if(startOfAffectedArea < pair[1] && endOfAffectedArea >= pair[1]) {
								// right side or completely deleted
								pair[1] =  startOfAffectedArea;
								if(startOfAffectedArea <= pair[0]) {
									// this Box is consumed
									pair[0] = startOfAffectedArea;
								}
							} // else deletion happens to be after this box so no change required.
						}
					}
					else {
//						System.out.println("Now in Active mode");
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
							if (active) {
//							System.out.println("Active + Inside Box Change!");
								//box end needs to be altered. 
								pair[1] = (pair[1] + amountOfChange);
								changeHandled = true;
							} else {
								// Split occurring
//							System.out.println("Split!");
								splitIndex = pairsList.indexOf(pair);
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
				}
				if (active && !changeHandled) {
					int[] changed = {positionOfChange, positionOfChange+amountOfChange};
					pairsList.add(changed);
					changeHandled = true;
				} else if (toBeAddedFirst != null) {
					pairsList.addFirst(toBeAddedFirst);
				} else if (splitIndex >= 0) {
					pairsList.set(splitIndex, splitright);
//					System.out.println(pairs.toString());
					pairsList.add(splitIndex, splitleft);
//					System.out.println(pairs.toString());
				}

				r.setPositions(pairsList);	


				LinkedList<int[]> newpairs = new LinkedList<int[]>();
				// Cleanup Pairs
				int[] pair = null;
				int[] nextPair = null;
				Iterator<int[]> iterator = pairsList.iterator();
				if (iterator.hasNext()) {
					nextPair = (int[]) iterator.next();
//					System.out.println("One Element found: " + Arrays.toString(nextPair));
				}

				// Start at comparing the first and the second pair
				while (iterator.hasNext()) {
					pair = nextPair;
					nextPair = (int[]) iterator.next();
//					System.out.println("Next Element found: " + Arrays.toString(nextPair));

					if (pair[1] <= pair[0]) {
						// Do not add the pair: pair is after change no longer valid
					} else if (pair[1] >= nextPair[0]) {
						//Merge two boxes
//						System.out.println("Merging");
//						System.out.println("Omnomnom - eating some pairs");
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
				
				// Debug Sysouts
//				for (TRCRequirement trcRequirement : reqs) {
//					for(int[] p : trcRequirement.getPositions()) {
//						System.out.print(" " + Arrays.toString(p));
//					}
//					System.out.println();
//				}
			}		
		}

		return reqs;
	}
	
	/**
	 * sets the Requirement Boxes as selected in the Editor
	 * @param positionOfChange
	 * @param amountOfChange
	 * @return reqs - the list of Requirements which must not be null;
	 */
	public static LinkedList<TRCRequirement> changeBoxes(int positionOfChange, int amountOfChange, LinkedList<TRCRequirement> reqs) {
		
		if(amountOfChange <= 0) {
			return reqs;
		}
		
		int endOfChange = positionOfChange + amountOfChange;

		for (Iterator<TRCRequirement> iterator = reqs.iterator(); iterator.hasNext();) {
			TRCRequirement r = (TRCRequirement) iterator.next();
			
			boolean active = r.isActive();
			boolean changeHandled = false;
			LinkedList<int[]> pairs = r.getPositions();
			LinkedList<int[]> newPairs = new LinkedList<int[]>();

			// No occurrence of this requirement yet -> Create new pair if active
			if(pairs.size() <= 0) {
//				System.out.println("pairs.size() == 0");
				if (active) {
					int[] changed = {positionOfChange, positionOfChange+amountOfChange};
					newPairs.add(changed);
					r.setPositions(newPairs);	
					changeHandled = true;
				}

			} else { // Requirement does already have at least one occurrence

				for(int[] pair : pairs) {
//					System.out.println("Current Pair:" + Arrays.toString(pair));
					if (changeHandled) {
//						System.out.println("Change has been Handeled");
						int[] lastPair = newPairs.getLast();
						if (lastPair[1] >= pair[0]) {
//							System.out.println("Fusion!");
							if (pair[1] > lastPair[1]) {
								lastPair[1] = pair[1];								
							}
						} else {
							newPairs.add(pair);							
						}
					} else {
						if(positionOfChange < pair[0]) {
//							System.out.println("Set occuring before this box");
							// selection starts before this box
							if (endOfChange > pair[0]) {
								// the selections right side overlaps with this box' left side
//								System.out.println("Left side Affected");
								if (endOfChange >= pair[1]) {
//									System.out.println("Enlargement of box");
									//The selection overlaps the box completely
									if (active) {
										pair[0] = positionOfChange;
										pair[1] = endOfChange;
										newPairs.add(pair);
										changeHandled = true;
									} else {
//										System.out.println("Consumption of box");
										// do NOT add the box to the new list => remove the box
									}
								} else if(active) {
									// Selection only overlaps the left side of the box.
									//Add start to box
									System.out.println("Adding left Side");
									pair[0] = positionOfChange;	
									newPairs.add(pair);	
									changeHandled = true;
								} else {
									// Selection only overlaps the left side of the box.
									//Remove everything from start to end from selection
									System.out.println("Deleting left Side");
									pair[0] = endOfChange;
									newPairs.add(pair);	
									changeHandled = true;
								}
							} else {
								// No overlapp
//								System.out.println("No Overlap");
								if(active && !changeHandled) {
									// New Box has to be created
									int[] insert = {positionOfChange, endOfChange};
									newPairs.add(insert); //add the new box
									changeHandled = true;
								}
								newPairs.add(pair); // append this box
							}
						} else if(positionOfChange <= pair[1]) {
//							System.out.println("Inside Box Change");
							//Change occurs inside of the current box.
							if (active) {
//								System.out.println("Active + Inside Box Change!");
								//box end needs to be altered. 
								if(endOfChange > pair[1]) {
//									System.out.println("Box widened");
									pair[1] = endOfChange;
									changeHandled = true;
								}
								// else no change needed change is completely in the active box.
								newPairs.add(pair);	
//								System.out.println("Pair is: " + Arrays.toString(pair));
								changeHandled = true;
							} else {
								if (positionOfChange < pair[1]) {
									// Not active: Deletion of Box of selection requested
//									System.out.println("Delete! positionOfChange: " + positionOfChange + "; endOfChange: " + endOfChange);
									if(positionOfChange <= pair[0] && endOfChange > pair[0]) {
										if(endOfChange > pair[1]) {
											// Box is completeley selected -> completeley deleted
										} else {
											// Left side cut
//											System.out.println("Left side Cut");
											pair[0] = endOfChange;
											newPairs.add(pair);
											changeHandled = true;
										}
									} else if (positionOfChange > pair[0]  && endOfChange < pair[1]) {
										// Split
//										System.out.println("Split");
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
									} else if (positionOfChange >= pair[0] && positionOfChange < pair[1]  && endOfChange >= pair[1]) {
										// Right side cut
										pair[1] = positionOfChange;
										newPairs.add(pair);
//										System.out.println("Right side Cut: "+ Arrays.toString(pair));
										changeHandled = true;
									} 	
								} else {
									// Else Change After Box
									newPairs.add(pair);
								}
							}
						} else {
//							System.out.println("After Box Change");
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
				r.setPositions(newPairs);
			}
			// Debug Sysout
			for (TRCRequirement trcRequirement : reqs) {
				for(int[] p : trcRequirement.getPositions()) {
					System.out.print(" " + Arrays.toString(p));
				}
				System.out.println();
			}
		}
		return reqs;
	}
	
	
}
