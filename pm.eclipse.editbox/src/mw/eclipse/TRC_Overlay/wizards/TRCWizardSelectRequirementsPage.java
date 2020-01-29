package mw.eclipse.TRC_Overlay.wizards;

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
import mw.eclipse.TRC_Overlay.impl.TRCRequirement;               

/**
 * The TRCWizardSelectRequirementsPage allows setting / editing the Requirement
 * ID's for the in {@link TRCNewWizardPage} specified file. One has to specify
 * at least one Requirement
 */
public class TRCWizardSelectRequirementsPage extends WizardPage {
	private Text requirementIDsText;
	private TRCNewWizardPage newWisardPage;
	private LinkedList<TRCRequirement> reqs;

	/**
	 * Constructor for TRCWizardSelectRequirementsPage.
	 * 
	 * @param newWisardPage - requires the newWisardPage to check the contents of a
	 *                      possibly already existing .trc file.
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
		label.setText("&Requirement IDs: " + "\n" + "seperate with \"\\n\" or \";\"");
		requirementIDsText = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		requirementIDsText.setLayoutData(gd);
		requirementIDsText.addModifyListener(e -> dialogChanged());
		IPath path = new Path(newWisardPage.getNewFileName());
		IResource ressource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		IPath absolutLocalized = ressource.getLocation();
		if (path.toOSString().indexOf(".trc") != -1) {
			reqs = TRCFileInteraction.ReadTRCsFromFile(absolutLocalized, true);
			if (reqs != null) {
				Collections.reverse(reqs);
			}
		}
		String reqIDs = "";
		if (reqs != null) {
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
		// TODO: Possibly Validation
		// TODO: Selecting from ReqIF File instead of specifying IDs
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/**
	 * returns the Requirement IDs in a fitting format for the TextBox
	 * 
	 * @return the Requirement IDs seperated by '\n'
	 */
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