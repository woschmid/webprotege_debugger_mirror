package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;

@ProjectSingleton
public interface DebuggingSessionManager {

    DebuggingResult startDebugging();

    DebuggingResult stopDebugging();

    DebuggingResult submitQuery();

}
