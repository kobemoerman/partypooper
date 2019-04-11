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

import com.application.android.partypooper.Activity.CreateEventActivity;
import com.application.android.partypooper.R;

public class HomeFragment extends Fragment {

  private Button createEvent;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);

    findFragmentElements(view);

    createEventButtonListener();

    return view;
  }

  private void createEventButtonListener() {
    createEvent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(getContext(), CreateEventActivity.class));
      }
    });
  }

  private void findFragmentElements(View v) {
    createEvent = v.findViewById(R.id.frag_home_create_event);
  }
}
