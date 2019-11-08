package pm.eclipse.editbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

public class Box_colorBackup {
	/**
	 *  start and end represent vertical positioning. Offset is the horizontal offset
	 */
	public int start,offset,end;

	

	public Box_colorBackup parent;
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
	private List<Box_colorBackup> children;
	public Object data;
	
	public List<String> requirements;
	
	@Override
	public String toString() {
		return "["+start+","+end+","+offset+","+maxEndOffset+"]";
	}

	public void setColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int[] getColor() {
		int[] a = {red, green, blue};
		return a;
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

	public List<Box_colorBackup> children() {
		return children != null ? children : Collections.EMPTY_LIST;
	}
	
	public void addChild(Box_colorBackup box){
		if (children == null) 
			children = new ArrayList<Box_colorBackup>();
		children.add(box);
	}
}
