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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import mw.eclipse.TRC_Overlay.views.TRCView;


/**
 * @author Martin
 *
 */
public class TRCFileInteraction {

	private static String page;

	public static void WriteReversedTRCsToFile(LinkedList<TRCRequirement> trcReqs) {
		WriteReversedTRCsToFile(trcReqs, BoxDecoratorImpl.getCurrentActivePath());
	}

	/**
	 * The TRCView handles the Requirements in the reversed order. In order to save
	 * these correctly, the list has to be reversed.
	 * 
	 * @param trcReqs the TRCRequirementOld Array that needs to be saved in reversed
	 *                order
	 * @param path    - the Absolute system Path of the corresponding Code file
	 */
	public static void WriteReversedTRCsToFile(LinkedList<TRCRequirement> trcReqs, IPath path) {

		LinkedList<TRCRequirement> reversed = new LinkedList<TRCRequirement>(
				Arrays.asList(new TRCRequirement[trcReqs.size()]));
		Collections.copy(reversed, trcReqs);
		Collections.reverse(reversed);

		try {
			String stringPath = exchangeEnding(path.toOSString());

			FileOutputStream fileOut = new FileOutputStream(stringPath);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(reversed);
			objectOut.close();
			fileOut.close();
//			System.out.println("The Object  was succesfully reversed-written to the file: " + stringPath);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void WriteTRCsToFile(LinkedList<TRCRequirement> trcReqs) {
		WriteTRCsToFile(trcReqs, BoxDecoratorImpl.getCurrentActivePath());
	}

	/**
	 * 
	 * @param trcReqs the TRCRequirementOld Array that needs to be saved
	 * @param path    - the Absolute system Path of the corresponding Code file
	 */
	public static void WriteTRCsToFile(LinkedList<TRCRequirement> trcReqs, IPath path) {
		try {
			String stringPath = exchangeEnding(path.toOSString());

			FileOutputStream fileOut = new FileOutputStream(stringPath);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(trcReqs);
			objectOut.close();
			fileOut.close();
//			System.out.println("The Object  was succesfully written to the file: " + stringPath);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static LinkedList<TRCRequirement> ReadTRCsFromFile() {
		return ReadTRCsFromFile(BoxDecoratorImpl.getCurrentActivePath());
	}

	/**
	 * 
	 * @param filePath           - the file Path where to read the File.
	 * @param alwaysReadFromFile - makes ReadFile read from File not from view if
	 *                           set to TRUE.
	 * @return the linked List obtained from that file
	 */
	public static LinkedList<TRCRequirement> ReadTRCsFromFile(IPath filePath, boolean alwaysReadFromFile) {
		if (alwaysReadFromFile) {
			TRCView.setInitialized(false);
		}
		return ReadTRCsFromFile(filePath);
	}

	/**
	 * Opens the corresponding .trc file to the file at filePath and reads its
	 * contents.
	 * 
	 * @param filePath the absolute system path to the currently opened file.
	 * @return the TRCRequirementOld[] read from file in the same order, as it is
	 *         displayed. Which means reversed order corsesponding to as it is saved
	 *         in the file.
	 */
	public static LinkedList<TRCRequirement> ReadTRCsFromFile(IPath filePath) {
		checkWindowChanged();

//		System.out.println("Initialised?: " + TRCView.isInitialized());
//		new Throwable("I Hate to do this printStackTrace thing").printStackTrace();

		// Try the shortcut. Don't read from file, take Requirements from View.
		if (TRCView.isInitialized()) {
			LinkedList<TRCRequirement> reqs;
			try {
				reqs = (LinkedList<TRCRequirement>) TRCView.getViewer().getInput();
				return reqs;
			} catch (Exception e) {
				System.err.println("Not Initialised, due to: " + e.getMessage());
			}
		}

		if (filePath != null) {
			String stringPath = exchangeEnding(filePath.toOSString());

			try {
				FileInputStream fis = new FileInputStream(stringPath);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object read = ois.readObject();
				LinkedList<TRCRequirement> list = null;
				if (read == null) {
					new Throwable("read = null").printStackTrace();
				} else {
					if (read instanceof LinkedList<?>) {
						if (read != null) {
							list = (LinkedList<TRCRequirement>) read;
						}
					}
				}
				ois.close();
				fis.close();

//				System.out.println("The Object was succesfully read from the file: " + stringPath);

				LinkedList<TRCRequirement> reqs = new LinkedList<TRCRequirement>();

				for (Iterator<TRCRequirement> descIterator = list.descendingIterator(); descIterator.hasNext();) {
					TRCRequirement r = (TRCRequirement) descIterator.next();
					reqs.add(r);
				}

				// TODO: Eventually launch a thread who gets the Info from the ReqIF File in
				// Background
				ReqIFFileInteraction.setInfos(reqs);

				return reqs;
			} catch (Exception ex) {
				// No file found or file access error
				if (ex instanceof FileNotFoundException) {
					System.err.println("FileNotFound");
				} else {
					new Throwable("File access Error").printStackTrace();
					ex.printStackTrace();
				}
				return null;
			}
		} else {
			return null;
		}

	}

	/**
	 * Checks whether the user eventually switched editor (edited file) meanwhile
	 */
	private static void checkWindowChanged() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window == null ? null : window.getActivePage();
		IEditorPart editor = activePage == null ? null : activePage.getActiveEditor();
		String currPage = editor == null ? null : editor.getTitle();
//		System.out.println("CHECK!");
//		System.out.println("        page: " + page);
//		System.out.println("current page: " + currPage);
		if (page == null || currPage == null || !page.equals(currPage)) {
//			System.out.println("Current page: " + currPage);
			TRCView.setInitialized(false);
			page = currPage;
		}

	}

	public static List<TRCRequirement> ReadTRCsFromFileAndUpdate(IPath filePath) {

		LinkedList<TRCRequirement> reqs = TRCView.updateViewer();
		if (reqs == null) {
			return null;
		}
		return reqs;
	}

	/**
	 * 
	 * @param filenameOrPath ending with .*
	 * @return filePath of the corresponding .trc file
	 */
	public static String exchangeEnding(String filenameOrPath) {
		int lastDot = filenameOrPath.lastIndexOf(".");
		if (lastDot == -1) {
			return "";
		}
		String trcString = filenameOrPath.substring(0, lastDot) + ".trc";
		return trcString;
	}

	/**
	 * 
	 * @param reqs the current list of requirements
	 * @return the List of all active Requirements if there are any or an empty
	 *         list.
	 */
	public static LinkedList<TRCRequirement> getActiveTRCRequirements(LinkedList<TRCRequirement> reqs) {
		if (reqs == null) {
			return null;
		}
		LinkedList<TRCRequirement> active = new LinkedList<TRCRequirement>();
		for (TRCRequirement trcRequirement : reqs) {
			if (trcRequirement.isActive()) {
				active.add(trcRequirement);
			}
		}
		return active;
	}

}
