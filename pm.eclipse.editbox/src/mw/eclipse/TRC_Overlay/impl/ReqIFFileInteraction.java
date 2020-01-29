/**
 * Copyright (c) 2020 Martin Wagner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.spdx.org/licenses/EPL-1.0
 * 
 * Contributors
 * @author Martin Wagner
 */
package mw.eclipse.TRC_Overlay.impl;

import java.util.LinkedList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.SpecElementWithAttributes;
import org.eclipse.rmf.reqif10.SpecHierarchy;
import org.eclipse.rmf.reqif10.Specification;
import org.eclipse.rmf.reqif10.impl.AttributeValueStringImpl;

import mw.eclipse.TRC_Overlay.views.ReqIF10Parser;

import org.eclipse.rmf.reqif10.common.util.ReqIF10Util;

import mw.eclipse.TRC_Overlay.impl.TRCRequirement;               

public class ReqIFFileInteraction {

	public static final int NOT_SIMILAR = -1;
	public static final int SIMILAR = 0;
	public static final int EQUAL = 1;
	
	// The name of the file located in the Projects "reqif" folder. It can be
	// altered here if needed.
	// Further Extension of this tool might include a initial welcome dialog where
	// the user can set the path to the ReqIF file.
	private static final String REQIF_FILENAME = "TRCOverlay.reqif";

	// The name aka. the String has to match the Label of the Field of the ReqIF
	// File that has to be scanned.
	// So please Specify the Filed in what the unique ID of the Requirement is
	// stored and the Field whose PlainText shall be displayed.
	private static final String ID_FIELD_NAME = "requirementID";
	private static final String TEXTUAL_DESCRIPTION_FIELD_NAME = "PlainText";
	
	/**
	 * TODO: Changes in the ReqIF File will never be acknowledged. Retrieves the
	 * information for the Requirements from the ReqIF File as specified in this
	 * class.
	 * 
	 * @param trcList the List of the Requirements whose Info needs to be set.
	 */
	public static void setInfos(LinkedList<TRCRequirement> trcList) {

		// TODO: Change Indication. The Requirements are only read once this way, and
		// for further clarification after a change to the ReqIF File, Requirements
		// should be market as invalid

		ReqIF10Parser parser = new ReqIF10Parser();
		parser.setRemoveTemporaries(true);
		parser.setRemoveToolExtensions(true);
		parser.setReqIFFilename(REQIF_FILENAME);
		ReqIF r = parser.parseReqIFContent();
		if(r == null) {
			return;
		}
		EList<Specification> list = r.getCoreContent().getSpecifications();

		for(TRCRequirement req : trcList) {
			String info = req.getInfo();
			if (info == null || info.isEmpty()) {

				String id = req.getId();

				for (Specification spec : list) {
					EList<SpecHierarchy> children = spec.getChildren();
					SpecHierarchy goal = descend(id, children);
					if(goal == null) {
						System.out.println("not found");
					} else {

						SpecElementWithAttributes specWithAttributes = goal.getObject();
						AttributeValue atNoCast = ReqIF10Util.getAttributeValueForLabel(specWithAttributes, TEXTUAL_DESCRIPTION_FIELD_NAME);
						AttributeValueStringImpl at = (AttributeValueStringImpl) atNoCast;
						String desc =  at.getTheValue();

						System.out.println("DESC: " + desc);
						req.setInfo(desc);
					}
				}
			}
		}
	}

	private static SpecHierarchy descend(final String id, final EList<SpecHierarchy> specs) {
		for (SpecHierarchy spec : specs) {
			// first, look if ID is similar
			int similarity = isSimilar(id, spec);
			switch (similarity) {
			case NOT_SIMILAR:
				continue;
			case EQUAL: 
				return spec;
			case SIMILAR:
				SpecHierarchy lookFurther = descend(id, spec.getChildren());
				if(lookFurther == null) {
					continue;
				} else {
					return lookFurther;
				}
			}
		}
		//if not found at all:
		return null;
	}

	/**
	 * Checks if the first chars of id match to the ID of spec
	 * 
	 * ID_LABEL is the 'name' of the column in which the requirement ID is stored.
	 * 
	 * The size of the 
	 * 
	 * @param id - the unique ID of the Requirement that is currently searched
	 * @param spec - the currently Hierarchical specification object.
	 * @return 
	 * 	- ReqIFFileInteraction.EQUAL if all chars of specID match to the all chars in id. (id.length == specID.length)
	 * 	- ReqIFFileInteraction.SIMILAR if all chars of specID match to some chars in id. (id.length > specID.length)
	 *  - ReqIFFileInteraction.NOT_SIMILAR as soon as the check failed for a char.
	 * 
	 */
	private static int isSimilar(final String id, final SpecHierarchy spec) {
		SpecElementWithAttributes specWithAttributes = spec.getObject();
		AttributeValue atNoCast = ReqIF10Util.getAttributeValueForLabel(specWithAttributes, ID_FIELD_NAME);
		// TODO: find a more common way to retreive the ID. Experience told me "8" is the field of the requirement ID;
		AttributeValueStringImpl at = (AttributeValueStringImpl) atNoCast;
		String specID =  at.getTheValue();
		System.out.println("ID: " + specID);
		//		System.out.println("Values: " + Arrays.toString(spec.getObject().getValues().toArray()));
		for(int i = 0; i<specID.length() ; i++) {
			char c1 = specID.charAt(i);
			char c2 = id.charAt(i);

			if (c1 != c2) {
				return NOT_SIMILAR;
			}	
		}
		if(id.length() == specID.length()) {
			return EQUAL;
		}
		return SIMILAR;
	}

}
