package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.R;

public class EventsFragment extends Fragment {

    /** Reference to the Home Activity */
    private HomeActivity act;

    /**
     * On create method of the fragment.
     * @param inflater inflate any views in the fragment
     * @param container parent view that the fragment's UI should be attached to
     * @param savedInstanceState this fragment is being re-constructed from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        initView(view);

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_events
     */
    private void initView(View view) {
        act = (HomeActivity) getActivity();
        assert act != null;
    }
}
