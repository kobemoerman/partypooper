package com.application.android.partypooper.Adapter;

public interface Section {
    /**
     * This method gets called by {@link CalendarDecoration} to verify whether the item represents a header.
     * @param position int.
     * @return true, if item at the specified adapter's position represents a header.
     */
    boolean isHeader(int position);

    /**
     * This method gets called by {@link CalendarDecoration} to verify whether a new header must be created.
     * @param position int.
     * @return true, if the next item has the same subSequence.
     */
    CharSequence getSectionHeader(int position);
}
