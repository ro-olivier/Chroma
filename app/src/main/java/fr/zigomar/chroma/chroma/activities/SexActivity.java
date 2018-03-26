package fr.zigomar.chroma.chroma.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.model.Baise;
import fr.zigomar.chroma.chroma.R;

public class SexActivity extends InputActivity {

    private LinearLayout baisesList;
    /* It could have been "Shag", or even "Nooky" (which it didn't know
    but sounds very cute) but I decided to go for a French word, because I'm French
    (it case it does show in the rest of the code...)
    Also, if somebody other than me reads this code one day, yes, I added a SexActivity.
    Because Sex is indeed an activity. Go figure.
    */
    private ArrayList<EditText> partner = new ArrayList<>();
    private ArrayList<EditText> notes = new ArrayList<>();
    private ArrayList<RatingBar> ratings = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.partner.add((EditText) findViewById(R.id.SexPartner));
        this.notes.add((EditText) findViewById(R.id.SexTextData));
        this.ratings.add((RatingBar) findViewById(R.id.SexRating));

        this.baisesList = findViewById(R.id.SexLinearLayout);

        Button addStepButton = findViewById(R.id.AddButton);
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkData()) {

                    @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.unit_input_sex, null);
                    baisesList.addView(child);
                    partner.add((EditText) child.findViewById(R.id.SexPartner));
                    notes.add((EditText) child.findViewById(R.id.SexTextData));
                    ratings.add((RatingBar) child.findViewById(R.id.SexRating));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.PartnerRequired, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayList<Baise> baises = getBaises();

        for (int i = 0; i < baises.size(); i++) {
            this.partner.get(i).setText(baises.get(i).getPartner());
            this.notes.get(i).setText(baises.get(i).getNotes());
            this.ratings.get(i).setRating(baises.get(i).getRating());
            if (i < baises.size() - 1) {
                this.baisesList = findViewById(R.id.SexLinearLayout);
                @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.unit_input_sex, null);
                this.baisesList.addView(child);
                this.partner.add((EditText) child.findViewById(R.id.SexPartner));
                this.notes.add((EditText) child.findViewById(R.id.SexTextData));
                this.ratings.add((RatingBar) child.findViewById(R.id.SexRating));
            }
        }
    }


    private ArrayList<Baise> getBaises(){
        // getting the data is handled by the DataHandler
        return this.dh.getBaisesList();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current baises");

        ArrayList<Baise> baises = new ArrayList<>();
        // assuming we have as many elements in the ArrayList
        for (int i = 0; i < this.partner.size(); i++) {
            baises.add(new Baise(this.partner.get(i).getText().toString(),
                    this.notes.get(i).getText().toString(),
                    this.ratings.get(i).getRating()
            ));
        }
        this.dh.saveBaiseData(baises);
    }


    private boolean checkData() {
        int current_size = this.partner.size();
        for (int i = 0; i < current_size; i++) {
            int partner_size = this.partner.get(i).getText().toString().length();
            // oh god the innuendo...
            int description_size = this.notes.get(i).getText().toString().length();
            if (partner_size == 0 || description_size == 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
}