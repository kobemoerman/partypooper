package com.application.android.partypooper.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.android.partypooper.Activity.CreateEventActivity;
import com.application.android.partypooper.Activity.HomeActivity;
import com.application.android.partypooper.Model.Events;
import com.application.android.partypooper.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EventThemeFragment extends Fragment implements Events {

  /** Reference to the Home Activity */
  private CreateEventActivity act;

  /** Card views representing the theme to select. */
  private CardView house, birthday, dinner, other;

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
    View view = inflater.inflate(R.layout.fragment_event_theme, container, false);

    initView(view);

    cardViewListener(house, HOUSE_PARTY);
    cardViewListener(birthday, BIRTHDAY_PARTY);
    cardViewListener(dinner, DINNER_PARTY);
    cardViewListener(other, OTHER);

    return view;
  }

  /**
   * Initialises the fragment view.
   * @param view fragment_event_theme
   */
  private void initView(View view) {
    act = (CreateEventActivity) getActivity();

    house = view.findViewById(R.id.frag_selection_house_party);
    birthday = view.findViewById(R.id.frag_selection_birthday_party);
    dinner = view.findViewById(R.id.frag_selection_dinner_party);
    other = view.findViewById(R.id.frag_selection_other_party);
  }

  /**
   * On click listener for a card view.
   * @param card the card to listen
   * @param theme the corresponding theme of the card
   */
  private void cardViewListener(CardView card, final String theme) {
    card.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        act.addItem("theme",theme);
        act.updateFragment(new CreateEventFragment());
      }
    });
  }
}
