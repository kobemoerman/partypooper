package com.application.android.partypooper.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.application.android.partypooper.Activity.LoginActivity;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseAuth;

public class MenuFragment extends Fragment {

    private Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        logoutButton = view.findViewById(R.id.logOutButton);

        return view;
    }
}
