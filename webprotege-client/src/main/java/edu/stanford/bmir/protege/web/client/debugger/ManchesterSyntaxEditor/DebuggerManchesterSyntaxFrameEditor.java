package edu.stanford.bmir.protege.web.client.debugger.ManchesterSyntaxEditor;

import com.google.gwt.user.client.ui.HasEnabled;
import edu.stanford.bmir.gwtcodemirror.client.AutoCompletionHandler;
import edu.stanford.bmir.protege.web.client.editor.ValueEditor;
import edu.stanford.bmir.protege.web.client.frame.ApplyChangesActionHandler;
import edu.stanford.bmir.protege.web.client.frame.CreateAsEntityTypeHandler;
import edu.stanford.bmir.protege.web.shared.frame.ManchesterSyntaxFrameParseError;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 18/03/2014
 */
public interface DebuggerManchesterSyntaxFrameEditor extends ValueEditor<String>, HasEnabled {

    void clearError();

    void setError(ManchesterSyntaxFrameParseError error);

    void setCreateAsEntityTypeHandler(CreateAsEntityTypeHandler handler);

    void setAutoCompletionHandler(AutoCompletionHandler autoCompletionHandler);

    void setApplyChangesViewVisible(boolean visible);

    void setApplyChangesHandler(ApplyChangesActionHandler handler);


}
