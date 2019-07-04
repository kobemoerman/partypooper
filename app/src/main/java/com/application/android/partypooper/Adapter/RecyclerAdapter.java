package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.android.partypooper.Adapter.RecyclerAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic RecyclerView adapter.
 * adds/removes items, binds ViewHolders.
 *
 * @param <T>  type of objects, which will be used by the adapter
 * @param <VH> ViewHolder {@link ViewHolder}
 */
public abstract class RecyclerAdapter<T, VH extends ViewHolder> extends RecyclerView.Adapter<VH> {

    /** List of items to be displayed in the adapter */
    private List<T> items;

    /** Context of the current view */
    private Context mContext;

    /**
     * Base constructor.
     *
     * @param context Context needed to retrieve LayoutInflater
     */
    RecyclerAdapter(Context context) {
        mContext = context;
        items = new ArrayList<>();
    }

    public abstract static class ViewHolder<T> extends RecyclerView.ViewHolder {

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * Bind data to the item.
         *
         * @param item object, associated with the item.
         */
        public abstract void onBind(T item);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the itemView to reflect the item at the given
     * position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param pos The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull final VH holder, int pos) {
        T item = getItem(pos);
        holder.onBind(item);
    }

    /**
     * To be implemented in as specific adapter
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * Returns the total number of items in the list held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    /**
     * Returns an items from the list at a certain position.
     *
     * @return An item in this adapter.
     */
    public T getItem(int position) {
        return items.get(position);
    }

    /**
     * Returns all items from the adapter.
     *
     * @return All items
     */
    public List<T> getAllItems() {
        return items;
    }

    /**
     * Sets items to the adapter and notifies that data set has been changed.
     *
     * @param items items to set to the adapter
     * @throws IllegalArgumentException in case of setting `null` items
     */
    public void setItems(List<T> items) {
        if (items == null) {
            throw new IllegalArgumentException("List to set in the RecyclerView is null");
        }
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Adds item to the end of the list.
     * Notifies that item has been inserted.
     *
     * @param item item which has to be added to the adapter.
     */
    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Item to add to the RecyclerView is null");
        }
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    /**
     * Clears all the items in the adapter.
     */
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    /**
     * Removes an item from the adapter.
     * Notifies that item has been removed.
     *
     * @param item to be removed
     */
    public void remove(T item) {
        int position = items.indexOf(item);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Inflates a view.
     *
     * @param layout layout to me inflater
     * @param parent container where to inflate
     * @return inflated View
     */
    @NonNull
    protected View inflate(@LayoutRes final int layout, final @Nullable ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(layout, parent,false);
    }
}