package edu.stanford.bmir.protege.web.shared.debugger;

/**
 * INIT ... session has been created not not yet started<br/>
 * STARTED ... session has been started manually<br/>
 * COMPUTING ... session has been started and currently in a (longer lasting) computing state (either diagnosis search or query generation)<br/>
 * STOPPED ... session has been stopped manually<br/>
 */
public enum SessionState {INIT, STARTED, COMPUTING, STOPPED}
