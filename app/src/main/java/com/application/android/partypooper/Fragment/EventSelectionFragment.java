package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.android.partypooper.Model.Events;
import com.application.android.partypooper.R;

public class EventSelectionFragment extends Fragment implements Events {

  private CardView house, birthday, dinner, other;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_event_selection, container, false);

    findFragmentElements(view);

    cardViewListener();

    return view;
  }

  private void cardViewListener() {
    house.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openCreateEventFragment(new CreateEventFragment(HOUSE_PARTY));
      }
    });

    birthday.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openCreateEventFragment(new CreateEventFragment(BIRTHDAY_PARTY));
      }
    });

    dinner.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openCreateEventFragment(new CreateEventFragment(DINNER_PARTY));
      }
    });

    other.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openCreateEventFragment(new CreateEventFragment(OTHER));
      }
    });
  }

  private void openCreateEventFragment(Fragment frag) {
    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.event_fragment_container, frag).commit();
  }

  private void findFragmentElements(View view) {
    house = view.findViewById(R.id.frag_selection_house_party);
    birthday = view.findViewById(R.id.frag_selection_birthday_party);
    dinner = view.findViewById(R.id.frag_selection_dinner_party);
    other = view.findViewById(R.id.frag_selection_other_party);
  }
}
