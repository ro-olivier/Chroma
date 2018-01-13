package fr.zigomar.chroma.chroma.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.Adapters.SpendingAdapter;
import fr.zigomar.chroma.chroma.Model.Spending;
import fr.zigomar.chroma.chroma.R;

public class MoneyActivity extends InputActivity {

    // the list holding the data
    ArrayList<Spending> spendings;
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
                        Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MissingDataThreeValuesRequired, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // setting the spinner Adapter (for the spendings category)
        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
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


        spendingsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("CHROMA", "Clicked the " + position + "-th item.");

                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle(R.string.DeleteTitle);
                builder.setMessage(R.string.DeleteItemQuestion);

                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spendingAdapter.remove(spendings.get(pos));
                        spendingAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
    }

    private ArrayList<Spending> getSpendings(){
        // getting the data is handled by the DataHandler
        return this.dh.getSpendingsList();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current spendings");
        this.dh.saveMoneyData(this.spendings);
    }
}