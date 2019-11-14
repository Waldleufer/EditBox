package pm.eclipse.editbox.impl;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TRCView_backup extends ViewPart {
	

	BrowserExample instance = null;
	
	/**
	 * Create the example
	 * 
	 * @see ViewPart#createPartControl
	 */
	public void createPartControl(Composite frame) {
		instance = new BrowserExample(frame, true);
	}
	
	/**
	 * Called when we must grab focus.
	 * 
	 * @see org.eclipse.ui.part.ViewPart#setFocus
	 */
	public void setFocus() {
		instance.focus();
	}

	/**
	 * Called when the View is to be disposed
	 */	
	public void dispose() {
		instance.dispose();
		instance = null;
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
