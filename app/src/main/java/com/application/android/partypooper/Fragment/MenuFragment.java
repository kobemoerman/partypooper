package com.application.android.partypooper.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuFragment extends Fragment {

    private ImageView userImg;

    private FirebaseUser mUser;
    private String userID;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        //userID = prefs.getString()

        return view;
    }
}
