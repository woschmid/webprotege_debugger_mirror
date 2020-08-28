package edu.stanford.bmir.protege.web.client.debugger.statement;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.CheckBox;

import java.util.List;

public interface CheckCheckBoxHandler{

    void onClick(ClickEvent clickEvent, CheckBox checkBoxOther, List<CheckBox> list);
}
