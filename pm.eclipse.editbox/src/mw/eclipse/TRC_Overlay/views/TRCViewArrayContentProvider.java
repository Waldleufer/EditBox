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
package mw.eclipse.TRC_Overlay.views;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ITreeContentProvider;

import mw.eclipse.TRC_Overlay.impl.TRCRequirement;               

/**
 * The TRCViewArrayContentProvider works as a content provider for the
 * TRCRequirements.
 * 
 * The layer system is configured to display things bottom up, but it is easier
 * to understand if Information is provided top down. Thus the Content Provider
 * usually provides information in reversed order.
 * 
 * @author Martin
 *
 */
public class TRCViewArrayContentProvider implements ITreeContentProvider {

	private static TRCViewArrayContentProvider instance;
	private boolean reversedOrder = true; // Default: true

	public boolean isReversedOrder() {
		return this.reversedOrder;
	}

	public void setReversedOrder(boolean reversedOrder) {
		this.reversedOrder = reversedOrder;
	}

	/**
	 * Returns an instance of TRCViewArrayContentProvider. Since instances of this
	 * class do not maintain any state, they can be shared between multiple clients.
	 *
	 * @return an instance of TRCViewArrayContentProvider
	 */
	public static TRCViewArrayContentProvider getInstance() {
		synchronized (TRCViewArrayContentProvider.class) {
			if (instance == null) {
				instance = new TRCViewArrayContentProvider();
				instance.setReversedOrder(true);
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
	public Object[] getElements(final Object inputElement) {
		reversedOrder = true;
		if (reversedOrder) {

			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			}
			if (inputElement instanceof Collection) {
				if (inputElement instanceof LinkedList<?>) {
					final LinkedList<TRCRequirement> list = (LinkedList<TRCRequirement>) inputElement;
					LinkedList<TRCRequirement> out = new LinkedList<TRCRequirement>();
					for (Iterator<TRCRequirement> iterator = list.descendingIterator(); iterator.hasNext();) {
						TRCRequirement r = (TRCRequirement) iterator.next();
						out.add(r);
					}
					return out.toArray();
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

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TRCRequirement) {
			String[] out = { ((TRCRequirement) parentElement).getInfo() };
			return out;
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TRCRequirement) {
			return true;
		}
		return false;
	}

}
