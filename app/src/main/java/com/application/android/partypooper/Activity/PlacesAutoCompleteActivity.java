/*package com.application.android.partypooper.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.application.android.partypooper.R;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class PlacesAutoCompleteActivity extends AppCompatActivity {

  private String TAG = "PlacesAutoCompleteActivity";

  private TextView txtView;

  int AUTOCOMPLETE_REQUEST_CODE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_places_auto_complete);
    txtView = findViewById(R.id.txtView);

    // Initialize Places.
    Places.initialize(getApplicationContext(), "AIzaSyD2DoB9z4ZTzKDCPciCCVrycQpjZ1LCEbI");
    // Create a new Places client instance.
    PlacesClient placesClient = Places.createClient(this);

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    Intent intent = new Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this);
    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        Place place = Autocomplete.getPlaceFromIntent(data);
        Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
      } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
        // TODO: Handle the error.
        Status status = Autocomplete.getStatusFromIntent(data);
        Log.i(TAG, status.getStatusMessage());
      } else if (resultCode == RESULT_CANCELED) {
        // The user canceled the operation.
      }
    }
  }
}
*/
