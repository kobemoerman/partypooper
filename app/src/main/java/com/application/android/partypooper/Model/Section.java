package com.application.android.partypooper.Model;

import com.application.android.partypooper.Model.Event;

public class Section {

    int viewType;

    Event event;

    public Section (Event event, int viewType) {
        this.event = event;
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public Event getEvent() {
        return event;
    }
}
