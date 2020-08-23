package edu.stanford.bmir.protege.web.server.debugger;

import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.debugger.DebuggingResult;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;

import javax.annotation.Nonnull;

@ProjectSingleton
public interface DebuggingSessionManager {

    DebuggingResult startDebugging(@Nonnull RevisionManager revisionManager);

    DebuggingResult submitQuery();

    DebuggingResult stopDebugging();

}
