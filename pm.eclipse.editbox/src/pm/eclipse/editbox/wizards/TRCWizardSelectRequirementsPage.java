package pm.eclipse.editbox.wizards;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (.trc).
 */

public class TRCWizardSelectRequirementsPage extends WizardPage {
	private Text requirementIDsText;

	/**
	 * Constructor for TRCWizardSelectRequirementsPage.
	 */
	public TRCWizardSelectRequirementsPage() {
		super("wizardPage");
		setTitle("Traceability Data File");
		setDescription("Please specify the Requirements you want to parse in this file");
	}

	@Override
	public void createControl(Composite parent) {
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
		//TODO: Select from SPEC File
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
}