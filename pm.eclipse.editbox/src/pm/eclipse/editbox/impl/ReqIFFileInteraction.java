package pm.eclipse.editbox.impl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.ReqIFContent;
import org.eclipse.rmf.reqif10.SpecObject;

import pm.eclipse.editbox.views.ReqIF10Parser;

public class ReqIFFileInteraction {

	public static String getInfo(String id) {
		
		ReqIF10Parser parser = new ReqIF10Parser();
		parser.setRemoveTemporaries(true);
		parser.setRemoveToolExtensions(true);
		parser.setReqIFFilename("chapter3");
//		
		ReqIF r = parser.parseReqIFContent();
		EList<SpecObject> list = r.getCoreContent().getSpecObjects();
		for (SpecObject specObject : list) {
			System.out.println(specObject.toString());
		}
		
		return "";
	}

	
	
}
