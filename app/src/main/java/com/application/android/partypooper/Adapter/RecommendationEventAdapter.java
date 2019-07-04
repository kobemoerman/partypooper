package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Model.Recommendation;
import com.application.android.partypooper.R;

import java.util.ArrayList;
import java.util.Objects;

public class RecommendationEventAdapter extends ListViewAdapter {

    /** Displays the item Recommendation */
    private TextView itemView;

    /** Displays the amount Recommendation */
    private TextView amountView;

    /** Icons responsible to add or remove a Recommendation */
    private ImageView remove, add;

    /**
     * Base constructor.
     *
     * @param context  of the fragment
     * @param resource layout to be displayed inside the list view
     * @param items    list of items to be displayed
     */
    public RecommendationEventAdapter(Context context, int resource, ArrayList<Recommendation> items) {
        super(context, resource, items);
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param pos The position of the item within the adapter's data set of the item
     * @param view This value may be null
     * @param parent This value must never be null
     * @return the view
     */
    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        final int amount = Objects.requireNonNull(getItem(pos)).getAmount();
        final String item = Objects.requireNonNull(getItem(pos)).getItem();

        // inflate the view
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(mResource, parent,false);

        // retrieve the display items
        add = view.findViewById(R.id.recommendation_event_add);
        remove = view.findViewById(R.id.recommendation_event_remove);
        itemView = view.findViewById(R.id.recommendation_event_item);
        amountView = view.findViewById(R.id.recommendation_event_amount);

        // set the values for the view
        itemView.setText(item);
        amountView.setText(String.valueOf(amount));

        return view;
    }
}
