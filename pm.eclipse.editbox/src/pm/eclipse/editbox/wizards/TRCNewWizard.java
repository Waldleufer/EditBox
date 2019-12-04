package pm.eclipse.editbox.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import pm.eclipse.editbox.impl.TRCFileInteraction.TRCRequirement;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "trc".
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
	 * Adding the newWisardPage to the wizard.
	 */
	@Override
	public void addPages() {
		newWisardPage = new TRCNewWizardPage(selection);
		addPage(newWisardPage);
		selectRequirementsPage = new TRCWizardSelectRequirementsPage();
		addPage(selectRequirementsPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
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
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		final String sourceFileName,
		final String fileName,
		final String[] requirementIDs,
		IProgressMonitor monitor)
		throws CoreException {
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
			LinkedList<TRCRequirement> trcReqs = getTRCReqs(requirementIDs);
			FileOutputStream fileOut = new FileOutputStream(absolut);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(trcReqs);
            objectOut.close();
            fileOut.close(); //TODO: Neccesarry?
		} catch (IOException e) {
			throwCoreException(e.getMessage());
		}
//		monitor.worked(1);
//		monitor.setTaskName("Opening file for editing...");
//		getShell().getDisplay().asyncExec(() -> {
//			IWorkbenchPage page =
//				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//			try {
//				IDE.openEditor(page, file, true);
//			} catch (PartInitException e) {
//			}
//		});
		monitor.worked(1);
	}
	
	/**
	 * We will initialise file contents with the specified requirement IDs.
	 * @param ids - the String Array containing the IDs
	 */

	private LinkedList<TRCRequirement> getTRCReqs(String[] ids) {
		LinkedList<TRCRequirement> trcReqs = new LinkedList<TRCRequirement>();
		for(String id : ids) {
			TRCRequirement trcReq = new TRCRequirement(id, new LinkedList<int[]>());
			trcReqs.add(trcReq);
		}
		return trcReqs;
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "pm.eclipse.editbox", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will take the selection in the workbench in order to see if
	 * we can initialise from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}