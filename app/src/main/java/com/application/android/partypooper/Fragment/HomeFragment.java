package com.application.android.partypooper.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    /** Reference to the Home Activity */
    private HomeActivity act;

    private String user;

    private ImageView logo, banner;

    private TextView rateValue;

    private RatingBar rating;

    private DatabaseReference rated;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        updateRatingDisplay();
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

        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rated = FirebaseDatabase.getInstance().getReference().child("Users").child(user).child("rating");

        logo = view.findViewById(R.id.frag_home_logo);
        banner = view.findViewById(R.id.frag_home_banner);
        rating = view.findViewById(R.id.frag_home_rating);
        rateValue = view.findViewById(R.id.frag_home_rated);

        Glide.with(act.getApplicationContext()).load(R.drawable.logo).into(logo);
        Glide.with(act.getApplicationContext()).load(R.drawable.banner_home).into(banner);
    }

    private void updateRatingDisplay() {
        rated.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    onRatingListener();
                    rateValue.setVisibility(View.INVISIBLE);
                    rating.setVisibility(View.VISIBLE);
                } else {
                    rateValue.setText(returnRating(Integer.parseInt(String.valueOf(dataSnapshot.getValue()))));
                    rateValue.setVisibility(View.VISIBLE);
                    rating.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onRatingListener() {
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rated.setValue((int) rating);

                Uri uri = Uri.parse("market://details?id=" + act.getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    act.showMessage("Unable to find market app");
                }
            }
        });
    }

    private String returnRating (int value) {
        String text;

        switch (value) {
            case 1: text="Thank you for rating PartyPooper 1 stars " +getEmojiByUnicode(0x1F628); break;
            case 2: text="Thank you for rating PartyPooper 2 stars " +getEmojiByUnicode(0x1F633); break;
            case 3: text="Thank you for rating PartyPooper 3 stars " +getEmojiByUnicode(0x1F60A); break;
            case 4: text="Thank you for rating PartyPooper 4 stars " +getEmojiByUnicode(0x1F60D); break;
            case 5: text="Thank you for rating PartyPooper 5 stars " +getEmojiByUnicode(0x1F496); break;
            default: text="Thank you for rating PartyPooper 0 stars " +getEmojiByUnicode(0x1F62D); break;
        }

        return text;
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
