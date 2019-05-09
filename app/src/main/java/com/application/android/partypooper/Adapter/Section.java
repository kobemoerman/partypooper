package com.application.android.partypooper.Adapter;

public interface Section {
    /**
     * This method gets called by {@link CalendarDecoration} to verify whether the item represents a header.
     * @param position int.
     * @return true, if item at the specified adapter's position represents a header.
     */
    boolean isHeader(int position);

    /**
     * This method gets called by {@link CalendarDecoration} to set the text of the header.
     * @param position int.
     * @return the string to be displayed.
     */
    CharSequence getSectionHeader(int position);
}
