package com.application.android.partypooper.Activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.application.android.partypooper.Fragment.EventSelectionFragment;
import com.application.android.partypooper.R;

public class CreateEventActivity extends AppCompatActivity {

  private Fragment currentFrag;

  private ImageView closeActivity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_event);

    updateFragment(new EventSelectionFragment());

    closeActivityListener();
  }

  private void updateFragment(Fragment frag) {
    getSupportFragmentManager().beginTransaction().replace(R.id.event_fragment_container, frag).commit();
  }

  private void closeActivityListener () {
    closeActivity = findViewById(R.id.event_close);

    closeActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }
}
