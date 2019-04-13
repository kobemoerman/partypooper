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
import com.application.android.partypooper.Model.Events;
import com.application.android.partypooper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

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
    FirebaseUser mUser = ((CreateEventActivity)getActivity()).getmUser();
    String timeStamp = ((CreateEventActivity)getActivity()).getTimeStamp();

    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events").
            child(timeStamp + "?" + mUser.getUid());

    final HashMap eventMap = new HashMap();

    house.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        eventMap.put("theme", HOUSE_PARTY);
        ref.updateChildren(eventMap);
        openCreateEventFragment(new CreateEventFragment());
      }
    });

    birthday.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        eventMap.put("theme", BIRTHDAY_PARTY);
        ref.updateChildren(eventMap);
        openCreateEventFragment(new CreateEventFragment());
      }
    });

    dinner.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        eventMap.put("theme", DINNER_PARTY);
        ref.updateChildren(eventMap);
        openCreateEventFragment(new CreateEventFragment());
      }
    });

    other.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        eventMap.put("theme", OTHER);
        ref.updateChildren(eventMap);
        openCreateEventFragment(new CreateEventFragment());
      }
    });
  }

  private void openCreateEventFragment(Fragment frag) {
    String TAG = "CreateEventFragment";
    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.event_fragment_container, frag, TAG).commit();
  }

  private void findFragmentElements(View view) {
    house = view.findViewById(R.id.frag_selection_house_party);
    birthday = view.findViewById(R.id.frag_selection_birthday_party);
    dinner = view.findViewById(R.id.frag_selection_dinner_party);
    other = view.findViewById(R.id.frag_selection_other_party);
  }
}
