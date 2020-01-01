package pm.eclipse.editbox.wizards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

/**
 * This is a TRCNewWizard wizard. Its role is to create a new .trc file resource
 * in the provided container. If the container resource (a folder or a project
 * or a sourcecode file) is selected in the workspace when the wizard is opened,
 * it will accept it as the target source container. The file name for the file
 * which is to be created will be derived by the wizard. The wizard creates one
 * file with the extension "trc".
 */
public class TRCNewWizard extends Wizard implements INewWizard {
	private TRCNewWizardPage newWisardPage;
	private TRCWizardSelectRequirementsPage selectRequirementsPage;
	private ISelection selection;

	/**
	 * Constructor for TRCNewWizard.
	 */
	public TRCNewWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the newWisardPage and selectRequirementsPage to the wizard.
	 */
	@Override
	public void addPages() {
		newWisardPage = new TRCNewWizardPage(selection);
		addPage(newWisardPage);
		selectRequirementsPage = new TRCWizardSelectRequirementsPage(newWisardPage);
		addPage(selectRequirementsPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We will
	 * create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final String sourceFileName = newWisardPage.getSourceFileName();
		final String fileName = newWisardPage.getNewFileName();
		final String[] requirementIDs = selectRequirementsPage.getSpecifiedIDs();
		IRunnableWithProgress op = monitor -> {
			try {
				doFinish(sourceFileName, fileName, requirementIDs, monitor);
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container and create the file if missing
	 * or just replace its contents
	 */

	/**
	 * 
	 * @param sourceFileName Project relative path of the File whose content shall
	 *                       be traced
	 * @param fileName       Project relative path of the .trc file that shall be
	 *                       edited / created
	 * @param requirementIDs the IDs from the
	 *                       {@link TRCWizardSelectRequirementsPage}
	 * @param monitor        the progress monitor
	 * @throws CoreException
	 */
	private void doFinish(final String sourceFileName, final String fileName, final String[] requirementIDs,
			IProgressMonitor monitor) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile resource = root.getFile((new Path(sourceFileName)));
		if (!resource.exists()) {
			throwCoreException("File \"" + sourceFileName + "\" does not exist.");
		}
		final IFile file = root.getFile(new Path(fileName));
		System.out.println(file.toString());
		final String absolut = file.getLocation().toOSString();
		System.out.println("ABSOLUT: " + absolut);
		try {
			LinkedList<TRCRequirement> trcReqs = getTRCReqs(requirementIDs,
					selectRequirementsPage.getOldRequirements());
			FileOutputStream fileOut = new FileOutputStream(absolut);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(trcReqs);
			objectOut.close();
			fileOut.close();
		} catch (IOException e) {
			throwCoreException(e.getMessage());
		}
		monitor.worked(1);
	}

	/**
	 * We will initialise file contents with the specified requirement IDs.
	 * 
	 * @param ids             - the String Array containing the IDs
	 * @param oldRequirements - the Requirements found in the already existing .trc
	 *                        file, or null if there are none.
	 */
	private LinkedList<TRCRequirement> getTRCReqs(String[] ids, LinkedList<TRCRequirement> oldRequirements) {
		LinkedList<TRCRequirement> trcReqs = new LinkedList<TRCRequirement>();
		for (String id : ids) {
			if (oldRequirements != null) {
				boolean added = false;
				for (TRCRequirement r : oldRequirements) {
					if (r.getId().equals(id)) {
						trcReqs.add(r);
						added = true;
					}
				}
				if (!added) {
					TRCRequirement trcReq = new TRCRequirement(id, new LinkedList<int[]>());
					trcReqs.add(trcReq);
				}
			} else {
				TRCRequirement trcReq = new TRCRequirement(id, new LinkedList<int[]>());
				trcReqs.add(trcReq);
			}
		}
		return trcReqs;
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "pm.eclipse.editbox", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will take the selection in the workbench in order to see if we can
	 * initialise from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}