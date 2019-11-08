package pm.eclipse.editbox;

import java.util.List;

import org.eclipse.core.runtime.IPath;


public interface IBoxBuilder {
	String getName();
	void setName(String newName);
	
	void setTabSize(int tabSize);
	int getTabSize();
	
	void setCaretOffset(int carretOffset);
	int getCaretOffset();
	
	void setText(StringBuilder sb);
	
	List<List<Box>> build();
	
	void setFilePath(IPath path);
}
