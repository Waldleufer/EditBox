package pm.eclipse.editbox.views;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public class TRCViewArrayContentProvider implements IStructuredContentProvider {

	private static TRCViewArrayContentProvider instance;
	private boolean reversedOrder = true;  //Default: true

	public boolean isReversedOrder() {
		return this.reversedOrder;
	}


	public void setReversedOrder(boolean reversedOrder) {
		this.reversedOrder = reversedOrder;
	}


	/**
	 * Returns an instance of TRCViewArrayContentProvider. Since instances of this
	 * class do not maintain any state, they can be shared between multiple
	 * clients.
	 *
	 * @return an instance of TRCViewArrayContentProvider
	 *
	 * @since 3.5
	 */
	public static TRCViewArrayContentProvider getInstance() {
		synchronized(TRCViewArrayContentProvider.class) {
			if (instance == null) {
				instance = new TRCViewArrayContentProvider();
			}
			return instance;
		}
	}

	
	/**
	 * Returns the elements in the same Order as they lay in the .trc file.
	 * 
	 * @return the elements
	 */
	public Object[] getElementsInSaveOrder(Object inputElement) {

		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		if (inputElement instanceof Collection) {
			return ((Collection) inputElement).toArray();
		}
		return new Object[0];
	}
	

	/**
	 * Returns the elements in the input, which must be either an array or a
	 * <code>Collection</code>.
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if(reversedOrder) {
			
			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			}
			if (inputElement instanceof Collection) {
				if (inputElement instanceof List) {
					List<?> list = (List<?>) inputElement;
					Collections.reverse(list);
					return list.toArray();
				}
				return ((Collection) inputElement).toArray();
			}
			return new Object[0];
			
		} else {
			
			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			}
			if (inputElement instanceof Collection) {
				return ((Collection) inputElement).toArray();
			}
			return new Object[0];
		}

	}

}
