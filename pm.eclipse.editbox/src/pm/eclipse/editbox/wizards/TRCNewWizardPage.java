package pm.eclipse.editbox.wizards;

import java.util.concurrent.Exchanger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import pm.eclipse.editbox.impl.TRCFileInteraction;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (trc).
 */

public class TRCNewWizardPage extends WizardPage {
	private Text sourceFileText;

	private Text fileText;

	private ISelection selection;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public TRCNewWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Traceability Data File");
		setDescription("This wizard creates a new file with *.trc extension that maintains the Traceability Data for the corresponding File");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Source File:");

		sourceFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		sourceFileText.setLayoutData(gd);
		sourceFileText.addModifyListener(e -> dialogChanged());

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText("&TRC File:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY );
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(e -> dialogChanged());
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		fileText.setText("new_file.trc");
		
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IFile file;
				//TODO: Look into, why this sometimes works and sometimes not.
				if (obj instanceof IFile) {
					file = (IFile) obj;
					sourceFileText.setText(file.getFullPath().toOSString());
					fileText.setText(TRCFileInteraction.exchangeEnding(file.getFullPath().toOSString()));
				}
			}
		}

	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		dialog.setText("Select the already existing source file that shall be traced");
		dialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
		String path = dialog.open();
		if (path != null) {
			String localString = path.split(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString())[1];
			System.out.println(path);
			sourceFileText.setText(localString);
			fileText.setText(TRCFileInteraction.exchangeEnding(localString));
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getSourceFileName()));
		String fileName = getNewFileName();
		System.out.println(container.toString());
		System.out.println(fileName);

		if (getSourceFileName().length() == 0) {
			updateStatus("Source File must be specified");
			return;
		}
		if (container == null || (container.getType() != IResource.FILE )) {
			updateStatus("Source File must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		int dotLoc1 = getSourceFileName().lastIndexOf('.');
		if (dotLoc1 != -1) {
			String ext = getSourceFileName().substring(dotLoc1 + 1);
			if (ext.equalsIgnoreCase("trc") == true) {
				updateStatus("Source file extentsion must not be \"trc\"");
				return;
			}
		}
		IResource target = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getNewFileName()));
		if (target != null && (container.getType() == IResource.FILE)) {
			updateStatus("Would overwrite an already existing .trc File");
			return;
		}
//		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
//			updateStatus("File name must be valid");
//			return;
//		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("trc") == false) {
				updateStatus("File extension must be \".trc\"");
				return;
			}
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getSourceFileName() {
		return sourceFileText.getText();
	}

	public String getNewFileName() {
		return fileText.getText();
	}
}