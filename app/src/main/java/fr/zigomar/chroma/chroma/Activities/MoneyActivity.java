package fr.zigomar.chroma.chroma.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.zigomar.chroma.chroma.Adapters.SpendingAdapter;
import fr.zigomar.chroma.chroma.Model.Spending;
import fr.zigomar.chroma.chroma.R;

public class MoneyActivity extends InputActivity {

    // the list holding the data
    List<Spending> spendings;
    // the adapter managing the view of the data
    private SpendingAdapter spendingAdapter;

    private TextView descField;
    private Spinner catField;
    private TextView amountField;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // setting the view's layout
        setContentView(R.layout.activity_money);

        // call inherited initiating method
        init();

        // getting the views from their id
        this.descField = (TextView) findViewById(R.id.TextDescription);
        this.catField = (Spinner) findViewById(R.id.TextCategory);
        this.amountField = (TextView) findViewById(R.id.TextAmount);
        Button addButton = (Button) findViewById(R.id.AddButton);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String d = descField.getText().toString();
                String c = catField.getSelectedItem().toString();
                if (amountField.getText().length() > 0 && descField.getText().length() > 0 && catField.getSelectedItem().toString().length() > 0) {
                    try {
                        Double a = Double.valueOf(amountField.getText().toString());

                        spendingAdapter.add(new Spending(d, c, a));
                        Log.i("CHROMA", "Currently " + spendings.size() + " spendings.");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Unable to parse the value.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "All three values are required.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // setting the spinner Adapter (for the spendings category)
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.spendingsCategories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.catField.setAdapter(spinnerAdapter);

        // init of the data : fetch spendings data in the currentDate file if it exist
        this.spendings = getSpendings();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) spendings
        ListView spendingsListView = (ListView) findViewById(R.id.ListViewMoney);

        this.spendingAdapter = new SpendingAdapter(MoneyActivity.this, this.spendings);
        spendingsListView.setAdapter(this.spendingAdapter);

        /*
        TODO : could be nice to add a "delete a spending" feature:
        a long click on one of the spendings could open a small dialog with
        an option to delete the spending.
         */
    }

    private List<Spending> getSpendings(){
        // getting the data is handled by the DataHandler
        return this.dh.getSpendingsList();
    }

    @Override
    protected void onStop() {
        // surcharge the onStop() method to include a call to the method updating the data and then
        // using the DataHandler to write it to file before closing
        super.onStop();
        Log.i("CHROMA","Starting activity closing...");

        updateMoneyData();
        dh.writeDataToFile(getApplicationContext());
        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }

    private void updateMoneyData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current spendings");
        this.dh.saveMoneyData(this.spendings);
    }
}