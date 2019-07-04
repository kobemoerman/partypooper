package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.application.android.partypooper.Model.Recommendation;

import java.util.ArrayList;

public abstract class ListViewAdapter extends ArrayAdapter<Recommendation> {

    /** Layout to be displayed for each item */
    protected int mResource;

    /** Context of the current view */
    protected Context mContext;

    /** List of items to be displayed in the adapter */
    protected ArrayList<Recommendation> mItems;

    /**
     * Base constructor.
     *
     * @param context   of the fragment
     * @param resource  layout to be displayed inside the list view
     * @param items     list of items to be displayed
     */
    public ListViewAdapter(Context context, int resource, ArrayList<Recommendation> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
        mItems = items;
    }

    /**
     * Returns the list of all items.
     *
     * @return All of items in this adapter.
     */
    public ArrayList<Recommendation> getItems() {
        if (mItems == null) {
            throw new IllegalArgumentException("The list of items is null");
        }
        return mItems;
    }

    /**
     * Edits the amount of an item from the adapter.
     *
     * @param amount new value
     * @param position position of the item
     */
    protected void edit(int amount, int position) {
        if (position < 0) {
            throw new IllegalArgumentException("Position is not valid");
        }
        mItems.get(position).setAmount(amount);
    }

    /**
     * Removes an item from the adapter.
     * Notifies that item has been removed.
     *
     * @param position of the item
     */
    public void remove(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("Position is not valid");
        }
        mItems.remove(position);
        notifyDataSetChanged();
    }


    /**
     * Adds item to the end of the list.
     * Notifies that item has been inserted.
     *
     * @param item item which has to be added to the adapter.
     */
    public void add(Recommendation item) {
        if (item == null) {
            throw new IllegalArgumentException("Item to add to the ListView is null");
        }
        mItems.add(item);
        notifyDataSetChanged();
    }
}
