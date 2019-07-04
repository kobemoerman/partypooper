package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.application.android.partypooper.Activity.CreateEventActivity;
import com.application.android.partypooper.Adapter.RecommendationAdapter;
import com.application.android.partypooper.Model.Recommendation;
import com.application.android.partypooper.R;

import java.util.ArrayList;

public class CreateRecommendationFragment extends Fragment {

    /** TAG reference of this fragment */
    private final static String TAG = "fragment/CreateRecommendation";

    /** Reference to the CreateEvent Activity */
    private CreateEventActivity act;

    /** List view to display the recommendations */
    private ListView list;

    /** Button to navigate back */
    private Button back;

    /** ImageView to add a recommendation to the adapter */
    private ImageView add;

    /** Inputs from the user */
    private EditText item, amount;

    /** Adapter to display items in the list view */
    private RecommendationAdapter mAdapter;

    /**
     * On create method of the fragment.
     * @param inflater inflate any views in the fragment
     * @param container parent view that the fragment's UI should be attached to
     * @param savedInstanceState this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_recommendation, container, false);

        initView(view);
        navigationListener();
        addRecommendationListener();

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_create_recommendation
     */
    private void initView(View view) {
        act = (CreateEventActivity) getActivity();

        assert act != null;

        add = view.findViewById(R.id.frag_create_recommendation_add);
        back = view.findViewById(R.id.frag_create_recommendation_back);
        list = view.findViewById(R.id.frag_create_recommendation_list_view);
        item = view.findViewById(R.id.frag_create_recommendation_item);
        amount = view.findViewById(R.id.frag_create_recommendation_amount);

        mAdapter = new RecommendationAdapter(getContext(),
            R.layout.item_recommendation, act.getmRecommendations());

        list.setAdapter(mAdapter);
    }

    /**
     * Listener to add a Recommendation to the adapter.
     */
    private void addRecommendationListener() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRecommendationValues()) return;

                int a = Integer.parseInt(amount.getText().toString());
                String i = item.getText().toString();

                mAdapter.add(new Recommendation(a,i));

                item.getText().clear();
                amount.getText().clear();
            }
        });
    }

    /**
     * Determines if the values of the EditText fields are valid
     * @return false when no text was found for item or amount
     */
    private boolean checkRecommendationValues() {
        String a = amount.getText().toString();
        String i = item.getText().toString();

        if (i.equals("")) {
            act.showMessage("Name your item");
            return false;
        }

        if (amount.getText().toString().equals("")) {
            act.showMessage("Give an amount for \"" + i + "\"");
            return false;
        }

        return true;
    }

    /**
     * Listener to navigate to the previous fragment.
     */
    private void navigationListener () {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.setmRecommendations(mAdapter.getItems());
                act.updateFragment(new CreateInviteFragment(),"fragment/CreateInvite");
            }
        });
    }
}
