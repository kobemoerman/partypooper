package com.application.android.partypooper.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.Model.Recommendation;
import com.application.android.partypooper.R;

import java.util.ArrayList;
import java.util.Objects;

public class RecommendationAdapter extends ListViewAdapter {

    /** Displays the item Recommendation */
    private TextView itemView;

    /** Displays the amount Recommendation */
    private EditText amountView;

    /** Icon responsible to remove a Recommendation */
    private ImageView remove;

    /**
     * Base constructor.
     *
     * @param context  of the fragment
     * @param resource layout to be displayed inside the list view
     * @param items    list of items to be displayed
     */
    public RecommendationAdapter(Context context, int resource, ArrayList<Recommendation> items) {
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
    public View getView(final int pos, View view, ViewGroup parent) {
        final int amount = Objects.requireNonNull(getItem(pos)).getAmount();
        final String item = Objects.requireNonNull(getItem(pos)).getItem();

        // inflate the view
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(mResource, parent,false);

        // retrieve the display items
        remove = view.findViewById(R.id.recommendation_remove);
        itemView = view.findViewById(R.id.recommendation_item);
        amountView = view.findViewById(R.id.recommendation_amount);

        // set the values for the view
        itemView.setText(item);
        amountView.setText(String.valueOf(amount));

        // listeners
        removeItemListener(pos);
        editAmountListener(pos);

        return view;
    }

    /**
     * Listener for text change in the amount view.
     *
     * @param position position in the adapter
     */
    private void editAmountListener(final int position) {
        amountView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String amount = s.toString();
                if (!amount.equals("")) {
                    edit(Integer.parseInt(amount), position);
                }
            }
        });
    }

    /**
     * Listener to remove an item from the Recommendations.
     *
     * @param position position in the adapter
     */
    private void removeItemListener(final int position) {
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });
    }
}
