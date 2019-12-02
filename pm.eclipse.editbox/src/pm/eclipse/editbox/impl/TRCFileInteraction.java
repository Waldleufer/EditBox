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
import java.util.LinkedList;
import java.util.Random;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Color;
/**
 * @author Martin
 *
 */
public class TRCFileInteraction {
	
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
			int r =  (int) (rand.nextFloat() * 256.0);
			int g =  (int) (rand.nextFloat() * 256.0);
			int b =  (int) (rand.nextFloat() * 256.0);
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
	
	public static final Path ANCHOR = Paths.get("/Users/Martin/Uni/Bachelorarbeit/runtime-EclipseApplication");
	
	/**
	 * Creating a test Purpose .trc File
	 * @param filePath - the IPath of the .trc file
	 * 
	 */
	public static void debug(IPath filePath) {
		
		System.err.println("DEBUGGING");
		
//		String path = filePath.toOSString();
//    	String name = exchangeEnding(path);
		
    	LinkedList<TRCRequirement> requirements = new LinkedList<TRCRequirement>();
		LinkedList<int[]> foo = new LinkedList<int[]>();
		//int[] a1 = {0, 1543};
		//int[] a2 = {13, 28};
		int[] a2 = {34, 210};
		int[] a3 = {809, 940};
		int[] a4 = {211, 1484};
		int[] a5 = {1487, 1539};
//		int[] b = {0, 212};
		//foo.add(a1);
		foo.add(a2);
		foo.add(a3);
		foo.add(a4);
		foo.add(a5); 
//		foo.add(b);
	    TRCRequirement a = new TRCRequirement("R01", foo);
	    //a.setColor(new Color(null, 250, 1, 1, 100));
	    //TRCRequirement b = new TRCRequirement("R02", positions)
	   
	    LinkedList<int[]> bar = new LinkedList<int[]>();
	    int[] b1 = {250, 1200}; //currently not used
	    bar.add(b1);
	    TRCRequirement b = new TRCRequirement("R02", bar);
	   // b.setColor(new Color(null, 1, 150, 1, 100));
	    requirements.add(b);
	    requirements.add(a);
		WriteTRCsToFile(requirements, filePath);
		new Throwable("DEBUG: TRCRequirement: " + a.toString() +"\n + TRCRequirement: " + b.toString()).printStackTrace();
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
	
	
	/**
	 * 
	 * @param trcReqs the TRCRequirement Array that needs to be saved
	 * @param path - the Absolute system Path of the corresponding Code file
	 */
	public static void WriteTRCsToFile(LinkedList<TRCRequirement> trcReqs, IPath path) {
		 	for (TRCRequirement trcRequirement : trcReqs) {
				System.out.println(trcRequirement.toString());
			}
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
    
	/**
	 * Opens the corresponding .trc file to the file at filePath and reads its contents.
	 * 
	 * @param filePath the absolute system path to the currently opened file.
	 * @return the TRCRequirement[] read.
	 */
    public static LinkedList<TRCRequirement> ReadTRCsFromFile(IPath filePath) {
    	LinkedList<TRCRequirement> debugs = new LinkedList<TRCRequirement>();
        try {
        	String stringPath = exchangeEnding(filePath.toOSString());    	
        	
            FileInputStream fis = new FileInputStream(stringPath);
            System.err.println("one");
            ObjectInputStream ois = new ObjectInputStream(fis);
            System.err.println("two");
            Object read = ois.readObject();	
            LinkedList<TRCRequirement> list = null;
            if (read == null) {
				new Throwable("read = null").printStackTrace();
			} else {
				if (read instanceof LinkedList) {
					list = (LinkedList<TRCRequirement>) read;
				}			
			}
            System.err.println("three");
            //TRCRequirement[] trcsFromSavedFile = (TRCRequirement[]) ois.readObject();
            ois.close();
            fis.close(); //TODO: Neccesarry?
//            System.out.println("The OBJECT is: " + debugs[0].toString());
            System.out.println("The Object was succesfully read from the file: " + stringPath);
            
            //TODO: Eventually launch a thread who does the following
            
            for (TRCRequirement trcRequirement : list) {
				if (trcRequirement.getInfo() == null) {
					String info = ReqIFFileInteraction.getInfo(trcRequirement.getId());
				}
			}

            
            
            return list;
        } catch (Exception ex) {
        	new Throwable("File access Error").printStackTrace();
            ex.printStackTrace();
            return null;
        }
    }
    
    /** 
     * 
     * @param path The relative path in the project Directory
     * @return the absolute system path
     */
    public static String computePath(String path) {
    	return ANCHOR.resolve(path.substring(1)).toString();
    }
    
    /**
     * 
     * @param filenameOrPath ending with .*
     * @return filePath of the corresponding .trc file
     */
	private static String exchangeEnding(String filenameOrPath) {
		String trcString = filenameOrPath.split("\\.")[0] + ".trc";
		return trcString;
	}

	/**
	 * 
	 * @param reqs the current list of requirements
	 * @return
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
