package edu.stanford.bmir.protege.web.client.debugger.statement;

import edu.stanford.bmir.protege.web.shared.debugger.Diagnosis;

public interface RepairDebuggingHandler {

    void handleRepairDebugging(Diagnosis diagnosis, int index);

}
