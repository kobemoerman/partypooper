package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;

public class HomeFragment extends Fragment {

    /** Reference to the Home Activity */
    private HomeActivity act;

    private ImageView logo, banner;

    private RatingBar rating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        onRatingListener();

        return view;
    }

    /**
     * Initialises the fragment view.
     * @param view fragment_home
     */
    private void initView(View view) {
        act = (HomeActivity) getActivity();
        assert act != null;

        logo = view.findViewById(R.id.frag_home_logo);
        banner = view.findViewById(R.id.frag_home_banner);
        rating = view.findViewById(R.id.frag_home_rating);

        Glide.with(act.getApplicationContext()).load(R.drawable.logo).into(logo);
        Glide.with(act.getApplicationContext()).load(R.drawable.banner_home).into(banner);
    }

    private void onRatingListener() {
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                act.showMessage(((int) rating) + " stars");
            }
        });
    }
}
