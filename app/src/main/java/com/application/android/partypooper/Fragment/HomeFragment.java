package com.application.android.partypooper.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.android.partypooper.R;

import static com.application.android.partypooper.Model.Events.BABY_SHOWER;
import static com.application.android.partypooper.Model.Events.BACHELOR_PARTY;
import static com.application.android.partypooper.Model.Events.BBQ;
import static com.application.android.partypooper.Model.Events.BIRTHDAY_PARTY;
import static com.application.android.partypooper.Model.Events.GET_TOGETHER;
import static com.application.android.partypooper.Model.Events.HOUSE_PARTY;
import static com.application.android.partypooper.Model.Events.POOL_PARTY;
import static com.application.android.partypooper.Model.Events.WEDDING_PARTY;

public class HomeFragment extends Fragment implements View.OnClickListener{

  TextView header;
  ImageView closeCreate, closeFriends;
  Button nextCreate, backCreate, nextFriends, backFriends;
  Button houseParty, birthdayParty, getTogether, bbq, poolParty, bachelorParty, weddingParty, babyParty;

  Dialog event, friends;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);

    event = new Dialog(getActivity());
    friends = new Dialog(getActivity());

    findFragmentElements(view);

    houseParty.setOnClickListener(this);
    birthdayParty.setOnClickListener(this);
    getTogether.setOnClickListener(this);
    bbq.setOnClickListener(this);
    poolParty.setOnClickListener(this);
    bachelorParty.setOnClickListener(this);
    weddingParty.setOnClickListener(this);
    babyParty.setOnClickListener(this);

    return view;
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.frag_home_house:
        createEventDialogPopup(HOUSE_PARTY);
        break;
      case R.id.frag_home_birthday:
        createEventDialogPopup(BIRTHDAY_PARTY);
        break;
      case R.id.frag_home_get_together:
        createEventDialogPopup(GET_TOGETHER);
        break;
      case R.id.frag_home_bbq:
        createEventDialogPopup(BBQ);
        break;
      case R.id.frag_home_pool:
        createEventDialogPopup(POOL_PARTY);
        break;
      case R.id.frag_home_bachelor:
        createEventDialogPopup(BACHELOR_PARTY);
        break;
      case R.id.frag_home_wedding:
        createEventDialogPopup(WEDDING_PARTY);
        break;
      case R.id.frag_home_baby:
        createEventDialogPopup(BABY_SHOWER);
        break;

      default:
        break;
    }
  }

  private void createEventDialogPopup(String event) {
    this.event.setContentView(R.layout.popup_create_event);

    header = this.event.findViewById(R.id.popup_event_header_text);
    header.setText(event);

    closeCreate = this.event.findViewById(R.id.popup_event_close);
    closeCreate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        HomeFragment.this.event.dismiss();
      }
    });

    nextCreate = this.event.findViewById(R.id.popup_event_next);
    nextCreate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        friendsDialogPopup();
      }
    });


    this.event.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    this.event.show();
  }

  private void friendsDialogPopup () {
    this.friends.setContentView(R.layout.popup_invite_friends);

    closeFriends = this.friends.findViewById(R.id.popup_friends_close);
    closeFriends.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        HomeFragment.this.friends.dismiss();
        HomeFragment.this.event.dismiss();
      }
    });

    backFriends = this.friends.findViewById(R.id.popup_friends_back);
    backFriends.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        HomeFragment.this.friends.dismiss();
      }
    });

    this.friends.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    this.friends.show();
  }

  private void findFragmentElements(View view) {
    houseParty = view.findViewById(R.id.frag_home_house);
    birthdayParty= view.findViewById(R.id.frag_home_birthday);
    getTogether = view.findViewById(R.id.frag_home_get_together);
    bbq = view.findViewById(R.id.frag_home_bbq);
    poolParty = view.findViewById(R.id.frag_home_pool);
    bachelorParty = view.findViewById(R.id.frag_home_bachelor);
    weddingParty = view.findViewById(R.id.frag_home_wedding);
    babyParty = view.findViewById(R.id.frag_home_baby);
  }
}
