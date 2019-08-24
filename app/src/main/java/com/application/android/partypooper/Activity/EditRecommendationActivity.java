package com.application.android.partypooper.Activity;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.android.partypooper.Adapter.RecommendationAdapter;
import com.application.android.partypooper.Fragment.EventRecommendationFragment;
import com.application.android.partypooper.Model.Event;
import com.application.android.partypooper.Model.Recommendation;
import com.application.android.partypooper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditRecommendationActivity extends AppCompatActivity {

    /** Reference to the event */
    private String eventID;

    /** List view to display the recommendations */
    private ListView list;

    /** ImageView to add a recommendation to the adapter */
    private ImageView add;

    /** Inputs from the user */
    private EditText item, amount;

    /** Adapter to display items in the list view */
    private RecommendationAdapter mAdapter;

    /** Firebase reference to all event recommendations */
    private DatabaseReference refRecommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recommendation);

        initView();

        fillRecommendationList();
        addRecommendationListener();

        hideKeyboardListener(item);
        hideKeyboardListener(amount);
    }

    private void initView() {
        Bundle b = getIntent().getExtras();
        eventID = Objects.requireNonNull(b).getString("id");

        refRecommendation = FirebaseDatabase.getInstance().getReference().child("Recommendation").child(eventID);

        list = findViewById(R.id.edit_recommendation_list_view);
        add = findViewById(R.id.edit_recommendation_add);
        item = findViewById(R.id.edit_recommendation_item);
        amount = findViewById(R.id.edit_recommendation_amount);

        mAdapter = new RecommendationAdapter(this,
            R.layout.item_recommendation, new ArrayList<Recommendation>(),eventID,true);

        list.setAdapter(mAdapter);
    }

    /**
     * Listener to add a Recommendation to the adapter.
     */
    private void addRecommendationListener() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRecommendationValues()) return;

                int a = Integer.parseInt(amount.getText().toString());
                String i = item.getText().toString();

                mAdapter.add(new Recommendation(0,a,i));

                item.getText().clear();
                amount.getText().clear();
            }
        });
    }

    /**
     * Determines if the values of the EditText fields are valid
     * @return false when no text was found for item or amount
     */
    private boolean checkRecommendationValues() {
        String a = amount.getText().toString();
        String i = item.getText().toString();

        if (i.equals("")) {
            showMessage("Name your item");
            return false;
        }

        if (a.equals("")) {
            showMessage("Give an amount for \"" + i + "\"");
            return false;
        }

        return true;
    }

    /**
     * Creates a Firebase reference to all event recommendations
     */
    private void updateRecommendationDatabase() {
        ArrayList<Recommendation> l = mAdapter.getItems();
        HashMap<String, Object> recommendation = new HashMap<>();
        for (final Recommendation r : l) {
            recommendation.put("total", r.getAmount());
            recommendation.put("brought", r.getBrought());
            refRecommendation.child(r.getItem()).updateChildren(recommendation);
        }
    }

    private void fillRecommendationList() {
        refRecommendation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAdapter.clear();
                for (DataSnapshot sp: dataSnapshot.getChildren()) {
                    String item = sp.getKey();
                    Map<String, Object> amount = (Map<String, Object>) sp.getValue();

                    int total = ((Long) amount.get("total")).intValue();
                    int brought = ((Long) amount.get("brought")).intValue();

                    mAdapter.add(new Recommendation(brought,total,item));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Displays a toast on the screen.
     * @param s text to display
     */
    public void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    /**
     * Calls @hideKeyboard when the users touches outside the edit text.
     */
    private void hideKeyboardListener(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    /**
     * Hides an open keyboard.
     * @param view of the activity
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onClickEditRecommendation(View view) {
        updateRecommendationDatabase();
        finish();
    }
}
