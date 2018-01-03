package fr.zigomar.chroma.chroma.Activities;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.zigomar.chroma.chroma.Adapters.DrinkAdapter;
import fr.zigomar.chroma.chroma.Model.Drink;
import fr.zigomar.chroma.chroma.R;

public class AlcoholActivity extends InputActivity {

    List<Drink> drinks;

    private TextView descField;
    private TextView volumeField;
    private TextView degreeField;
    private Button addButton;
    private ListView drinksListView;

    private DrinkAdapter drinkAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // setting the view's layout
        setContentView(R.layout.activity_alcohol);

        // call inherited initiating method
        init();

        // getting the views from their id
        this.descField = (TextView) findViewById(R.id.TextDescription);
        this.volumeField = (TextView) findViewById(R.id.DrinkVolume);
        this.degreeField = (TextView) findViewById(R.id.DrinkDegree);
        this.addButton = (Button) findViewById(R.id.AddButton);

        this.addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String d = descField.getText().toString();
                String vs = volumeField.getText().toString();
                String ds = degreeField.getText().toString();
                if (d.length() > 0 && vs.length() > 0 && ds.length() > 0) {
                    try {
                        double vol = Double.parseDouble(vs);
                        double deg = Double.parseDouble(ds);
                        drinkAdapter.add(new Drink(d, vol, deg));
                        Log.i("CHROMA", "Currently " + drinks.size() + " drinks.");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Unable to parse the value.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "All values are required.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // init of the data : fetch drinks data in the currentDate file if it exist
        this.drinks = getDrinks();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) drinks
        this.drinksListView = (ListView) findViewById(R.id.ListViewAlcohol);

        this.drinkAdapter = new DrinkAdapter(AlcoholActivity.this, this.drinks);
        this.drinksListView.setAdapter(this.drinkAdapter);

    }

    private List<Drink> getDrinks(){
        // getting the data is handle by the DataHandler
        return this.dh.getDrinksList();
    }

    @Override
    protected void onStop() {
        // surcharge the onStop() method to include a call to the method updating the data and then
        // using the DataHandler to write it to file before closing
        super.onStop();
        Log.i("CHROMA","Starting activity closing...");

        updateDrinkData();
        dh.writeDataToFile(getApplicationContext());
        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }

    private void updateDrinkData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current drinks");
        this.dh.saveAlcoholData(this.drinks);
    }
}
