package pm.eclipse.editbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;

import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

public class Box {
	/**
	 *  start and end represent vertical positioning. Offset is the horizontal offset
	 */
	public int start,offset,end;

	

	public Box parent;
	public Rectangle rec;
	public boolean isOn;
	public int red;
	public int green;
	public int blue;
	/**
	 * horizontal End position
	 */
	public int maxEndOffset;
	public int maxLineLen;
	public int level;
	public int tabsStart = -1;
	public boolean hasChildren;
	private List<Box> children;
	public Object data;
	
	private TRCRequirement requirement;  // Requirement represented in this box
	
	@Override
	public String toString() {
		return "["+start+","+end+","+offset+","+maxEndOffset+"]";
	}
	
	/**
	 * Adds the given Requirement ID to the List
	 */
	public void setRequirement(TRCRequirement requirement) {
		this.requirement = requirement;
	}
	
	public TRCRequirement getRequirement() {
		return requirement;
	}

	public void setColor(Color color) {
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
	}
	
	public Color getColor() {
		return new Color(null, red, green, blue);
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
	public boolean intersects(int s, int e) {
		return between(s, start, end) || between(e, start, end) || between(start, s, e) || between(end, s ,e);
	}

	protected boolean between(int m, int s, int e) {
		return s<=m && e>=m;
	}

	public List<Box> children() {
		return children != null ? children : Collections.EMPTY_LIST;
	}
	
	public void addChild(Box box){
		if (children == null) 
			children = new ArrayList<Box>();
		children.add(box);
	}
}
