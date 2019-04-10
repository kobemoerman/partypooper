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
  ImageView closeDialog;
  Button houseParty, birthdayParty, getTogether, bbq, poolParty, bachelorParty, weddingParty, babyParty;

  Dialog dialog;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);

    dialog = new Dialog(getActivity());

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
        showDialogPopup(HOUSE_PARTY);
        break;
      case R.id.frag_home_birthday:
        showDialogPopup(BIRTHDAY_PARTY);
        break;
      case R.id.frag_home_get_together:
        showDialogPopup(GET_TOGETHER);
        break;
      case R.id.frag_home_bbq:
        showDialogPopup(BBQ);
        break;
      case R.id.frag_home_pool:
        showDialogPopup(POOL_PARTY);
        break;
      case R.id.frag_home_bachelor:
        showDialogPopup(BACHELOR_PARTY);
        break;
      case R.id.frag_home_wedding:
        showDialogPopup(WEDDING_PARTY);
        break;
      case R.id.frag_home_baby:
        showDialogPopup(BABY_SHOWER);
        break;

      default:
        break;
    }
  }

  private void showDialogPopup(String event) {
    dialog.setContentView(R.layout.popup_window);

    header = dialog.findViewById(R.id.popup_header_text);
    header.setText(event);

    closeDialog = dialog.findViewById(R.id.popup_close);
    closeDialog.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.show();
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
