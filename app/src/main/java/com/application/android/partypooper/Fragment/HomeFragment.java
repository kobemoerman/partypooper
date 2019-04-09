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

import com.application.android.partypooper.R;

public class HomeFragment extends Fragment {

  Button houseParty, birthdayParty, getTogether, bbq, poolParty, bachelorParty, weddingParty, babyParty, randomBtn;

  Dialog dialog;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);

    dialog = new Dialog(getActivity());

    findFragmentElements(view);

    houseParty.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDialogPopup();
      }
    });

    return view;
  }

  private void showDialogPopup() {
    dialog.setContentView(R.layout.popup_window);

    randomBtn = dialog.findViewById(R.id.random_button);
    randomBtn.setOnClickListener(new View.OnClickListener() {
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
