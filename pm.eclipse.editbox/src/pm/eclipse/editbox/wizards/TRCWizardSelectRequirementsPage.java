package pm.eclipse.editbox.wizards;

import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import pm.eclipse.editbox.impl.TRCFileInteraction;
import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (.trc).
 */

public class TRCWizardSelectRequirementsPage extends WizardPage {
	private Text requirementIDsText;
	private TRCNewWizardPage newWisardPage;
	private LinkedList<TRCRequirement> reqs;

	/**
	 * Constructor for TRCWizardSelectRequirementsPage.
	 */
	public TRCWizardSelectRequirementsPage(TRCNewWizardPage newWisardPage) {
		super("wizardPage");
		setTitle("Traceability Data File");
		setDescription("Please specify the Requirements you want to parse in this file");
		this.newWisardPage = newWisardPage;
	}

	@Override
	public void createControl(Composite parent) {
		reqs = null;
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.SINGLE);
		label.setText("&Requirement IDs: " + "\n" + 
				"seperate with \"\\n\" or \";\"");
		requirementIDsText = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		requirementIDsText.setLayoutData(gd);
		requirementIDsText.addModifyListener(e -> dialogChanged());
		IPath path = new Path(newWisardPage.getNewFileName());
		IResource ressource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		IPath absolutLocalized = ressource.getLocation();
		if (path.toOSString().indexOf(".trc") != -1) {
			reqs = TRCFileInteraction.ReadTRCsFromFile(absolutLocalized, true);
			if(reqs != null) {
				Collections.reverse(reqs);				
			}
		}
		String reqIDs = "";
		if(reqs != null) {
			for (TRCRequirement r : reqs) {
				reqIDs += r.getId() + "\n";
			}			
		}
		requirementIDsText.setText(reqIDs);
		dialogChanged();
		setControl(container);
	}


	/**
	 * Ensures that the ID text field is set.
	 */

	private void dialogChanged() {
		if (requirementIDsText.getText() == null || requirementIDsText.getText().length() <= 0) {
			updateStatus("Please Specify at least one Requirement");
			return;
		}
		//TODO: Validation
		//TODO: Select from ReqIF File
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public String[] getSpecifiedIDs() {
		String[] out;
		out = requirementIDsText.getText().split("\n|;");
		for (int i = 0; i < out.length; i++) {
			out[i] = out[i].trim();
		}
		return out;
	}
	
	public LinkedList<TRCRequirement> getOldRequirements() {
		return reqs;
	}
}