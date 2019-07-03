package com.application.android.partypooper.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.application.android.partypooper.Activity.CreateEventActivity;
import com.application.android.partypooper.Model.Recommendation;
import com.application.android.partypooper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateRecommendationFragment extends Fragment {

    /** TAG reference of this fragment */
    private final static String TAG = "fragment/CreateRecommendation";

    /** Reference to the CreateEvent Activity */
    private CreateEventActivity act;

    private Button back;

    private ImageView add;

    private EditText item, amount;

    private ListView list;

    private ArrayList<Recommendation> recom;

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

        return view;
    }
    /**
     * Initialises the fragment view.
     * @param view fragment_create_recommendation
     */
    private void initView(View view) {
        act = (CreateEventActivity) getActivity();

        add = view.findViewById(R.id.frag_create_recommendation_add);
        back = view.findViewById(R.id.frag_create_recommendation_back);
        list = view.findViewById(R.id.frag_create_recommendation_list_view);
        item = view.findViewById(R.id.frag_create_recommendation_item);
        amount = view.findViewById(R.id.frag_create_recommendation_amount);

        assert act != null;

        recom = new ArrayList<>();

        final RecommendationAdapter mAdapter = new RecommendationAdapter(getContext(), R.layout.item_recommendation, recom);

        list.setAdapter(mAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = Integer.parseInt(amount.getText().toString());
                String i = item.getText().toString();

                recom.add(new Recommendation(a,i));

                mAdapter.notifyDataSetChanged();

                item.getText().clear();
                amount.getText().clear();
            }
        });
    }

    private void navigationListener () {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.updateFragment(new CreateInviteFragment(),"fragment/CreateInvite");
            }
        });
    }

    class RecommendationAdapter extends ArrayAdapter<Recommendation> {

        private int mResource;
        private Context mContext;

        public RecommendationAdapter(Context context, int resource, ArrayList<Recommendation> objects) {
            super(context, resource, objects);
            mContext = context;
            mResource = resource;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            int amount = Objects.requireNonNull(getItem(pos)).getAmount();
            String item = Objects.requireNonNull(getItem(pos)).getItem();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(mResource,parent,false);

            TextView itemView = view.findViewById(R.id.recommendation_item);
            EditText amountView = view.findViewById(R.id.recommendation_amount);

            itemView.setText(item);
            amountView.setText(String.valueOf(amount));

            return view;
        }
    }
}
