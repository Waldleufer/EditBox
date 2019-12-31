/**
 * 
 */
package pm.eclipse.editbox.impl;

import java.io.FileInputStream;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;
import pm.eclipse.editbox.views.TRCView;
/**
 * @author Martin
 *
 */
public class TRCFileInteraction {
	
	private static String page;
	
	public static final class TRCRequirement implements Serializable
	{
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
		 * 
		 * @param id - the unique ID of the requirement
		 * @param positions - a list of (start, end) tuples.
		 */
		public TRCRequirement(String id, LinkedList<int[]> positions) {
			this.id = id;
			this.positions = positions;
			this.info = null;
			Random rand = new Random();
			int r =  (int) (180.0 + rand.nextFloat() * 76.0);
			int g =  (int) (180.0 + rand.nextFloat() * 76.0);
			int b =  (int) (180.0 + rand.nextFloat() * 76.0);
			Color randomColor = new Color(null, r, g, b);
			setActive(false);
			// Debug: 
			//Color randomColor = new Color(null, 100, 150, 0);
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
	 * The TRCView handles the Requirements in the reversed order. In order to save these, the list has to be reversed.
	 * 
	 * @param trcReqs the TRCRequirement Array that needs to be saved in reversed order
	 * @param path - the Absolute system Path of the corresponding Code file
	 */
	public static void WriteReversedTRCsToFile(LinkedList<TRCRequirement> trcReqs, IPath path) {
		
		LinkedList<TRCRequirement> reversed = new LinkedList<TRCRequirement>(Arrays.asList(new TRCRequirement[trcReqs.size()]));
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
	 * @param path - the Absolute system Path of the corresponding Code file
	 */
	public static void WriteTRCsToFile(LinkedList<TRCRequirement> trcReqs, IPath path) {
			// TODO: Debug print below 
//		 	for (TRCRequirement trcRequirement : trcReqs) {
//				System.out.println("WriteTRCsToFile: " + trcRequirement.toString());
//			}
        try {
        	String stringPath = exchangeEnding(path.toOSString());
        	
            FileOutputStream fileOut = new FileOutputStream(stringPath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(trcReqs);
//            for (TRCRequirement trcRequirement : trcReqs) {
//            	objectOut.writeObject(trcRequirement);
//			}
            objectOut.close();
            fileOut.close(); //TODO: Neccesarry?
            System.out.println("The Object  was succesfully written to the file: " + stringPath);
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	public static LinkedList<TRCRequirement> ReadTRCsFromFile() {
		return ReadTRCsFromFile(BoxDecoratorImpl.getCurrentActivePath());
	}
    
	/**
	 * Opens the corresponding .trc file to the file at filePath and reads its contents.
	 * 
	 * @param filePath the absolute system path to the currently opened file.
	 * @return the TRCRequirement[] read.
	 */
    public static LinkedList<TRCRequirement> ReadTRCsFromFile(IPath filePath) {
    	checkWindowChanged();
    	
    	System.out.println("Initialised?: " + TRCView.isInitialized());
    	new Throwable("I Hate to do this printStackTrace thing").printStackTrace();
    	
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
    	
    	String stringPath = exchangeEnding(filePath.toOSString());
    	
    	//Localise:
//    	String local = stringPath.split(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString())[1];
//    	System.out.println("LOCAL: "+ local);
    	
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
            
            //TODO: Eventually launch a thread who gets the Info from the ReqIF File in Background
            ReqIFFileInteraction.setInfos(reqs);

            return reqs;
        } catch (Exception ex) {
        	// No file found or file access error
        	new Throwable("File access Error").printStackTrace();
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Checks whether the user eventually switched page
     */
    private static void checkWindowChanged() {
		String currPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		if (page == null || !page.equals(currPage)) {
			TRCView.setInitialized(false);
			page = currPage;
		}
		
	}

	public static List<TRCRequirement> ReadTRCsFromFileAndUpdate(IPath filePath) {
    	
		LinkedList<TRCRequirement> reqs = TRCView.updateViewer();
		if(reqs == null) {
			return null;
		}
//		TRCView.updateViewer(reqs); //update Viewers context // is done in updateViewer();
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
	 * @return the List of all active Requirements if there are any or an empty list.
	 */
	public static LinkedList<TRCRequirement> getActiveTRCRequirements(LinkedList<TRCRequirement> reqs) {
		LinkedList<TRCRequirement> active = new LinkedList<TRCRequirement>();
		for (TRCRequirement trcRequirement : reqs) {
			if (trcRequirement.isActive()) {
				active.add(trcRequirement);
			}
		}
		return active;
	}
	
}
