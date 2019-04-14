package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.R;

public class HomeFragment extends Fragment {

    /** Reference to the Home Activity */
    private HomeActivity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_home
     */
    private void initView(View view) {
        act = (HomeActivity) getActivity();
        assert act != null;
    }
}
