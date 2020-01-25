/**
 * 
 */
package pm.eclipse.editbox.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.WorkbenchActivityHelper;

import mw.eclipse.TRC_Overlay.impl.BoxDecoratorImpl;
import mw.eclipse.TRC_Overlay.views.TRCView;
import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

/**
 * @author Martin
 *
 */
public class TRCFileInteraction {

	private static String page;

	/**
	 * The TRCRequirement class defines how TRCRequirements shall be stored
	 * internally. In Order to enable simple Read and Write operations this class
	 * has to implement {@link Serializable}.
	 * 
	 * @author Martin
	 *
	 */
	public static final class TRCRequirement implements Serializable {
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
			System.out.println("COLOR IS: " + randomColor.toString());
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

	public static void WriteReversedTRCsToFile(LinkedList<TRCRequirement> trcReqs) {
		WriteReversedTRCsToFile(trcReqs, BoxDecoratorImpl.getCurrentActivePath());
	}

	/**
	 * The TRCView handles the Requirements in the reversed order. In order to save
	 * these correctly, the list has to be reversed.
	 * 
	 * @param trcReqs the TRCRequirement Array that needs to be saved in reversed
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
			System.out.println("The Object  was succesfully reversed-written to the file: " + stringPath);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void WriteTRCsToFile(LinkedList<TRCRequirement> trcReqs) {
		WriteTRCsToFile(trcReqs, BoxDecoratorImpl.getCurrentActivePath());
	}

	/**
	 * 
	 * @param trcReqs the TRCRequirement Array that needs to be saved
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
			System.out.println("The Object  was succesfully written to the file: " + stringPath);

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
	 * @return the TRCRequirement[] read from file in the same order, as it is
	 *         displayed. Which means reversed order corsesponding to as it is saved
	 *         in the file.
	 */
	public static LinkedList<TRCRequirement> ReadTRCsFromFile(IPath filePath) {
		checkWindowChanged();

		System.out.println("Initialised?: " + TRCView.isInitialized());
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

				System.out.println("The Object was succesfully read from the file: " + stringPath);

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
					System.out.println("FileNotFound");
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
		System.out.println("CHECK!");
		System.out.println("page: " + page);
		System.out.println("curp: " + currPage);
		if (page == null || currPage == null || !page.equals(currPage)) {
			System.out.println("Current page: " + currPage);
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
