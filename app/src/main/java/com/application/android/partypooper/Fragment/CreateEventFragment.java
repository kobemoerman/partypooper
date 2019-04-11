package com.application.android.partypooper.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.application.android.partypooper.Model.Events;
import com.application.android.partypooper.R;

@SuppressLint("ValidFragment")
public class CreateEventFragment extends Fragment implements Events {

  private String theme;

  public CreateEventFragment(String theme) {
    this.theme = theme;
  }

  TextView text;
  Button next, back;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_create_event, container, false);

    findFragmentElements(view);

    navigationListener();

    return view;
  }

  private void navigationListener () {
    text.setText(theme);

    next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO: send the user to next fragment.
      }
    });

    back.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openCreateEventFragment(new EventSelectionFragment());
      }
    });
  }

  private void openCreateEventFragment(Fragment frag) {
    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.event_fragment_container, frag).commit();
  }

  private void findFragmentElements(View view) {
    next = view.findViewById(R.id.frag_event_next);
    back = view.findViewById(R.id.frag_event_back);
    text = view.findViewById(R.id.theme_text);
  }
}
