package fr.zigomar.chroma.chroma.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.Adapters.EarningAdapter;
import fr.zigomar.chroma.chroma.Model.Earning;
import fr.zigomar.chroma.chroma.R;

public class MoneyInActivity extends InputActivity {

    // the list holding the data
    private ArrayList<Earning> earnings;
    // the adapter managing the view of the data
    private EarningAdapter earningAdapter;

    private TextView descField;
    private TextView amountField;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // setting the view's layout
        setContentView(R.layout.activity_money_in);

        // call inherited initiating method
        init();

        // getting the views from their id
        this.descField = findViewById(R.id.TextDescription);
        this.amountField = findViewById(R.id.TextAmount);
        Button addButton = findViewById(R.id.AddButton);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String d = descField.getText().toString();
                if (amountField.getText().length() > 0 && descField.getText().length() > 0) {
                    try {
                        Double a = Double.valueOf(amountField.getText().toString());

                        earningAdapter.add(new Earning(d, a));
                        Log.i("CHROMA", "Currently " + earnings.size() + " earnings.");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MissingDataThreeValuesRequired, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // init of the data : fetch earnings data in the currentDate file if it exist
        this.earnings = getEarnings();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) earnings
        ListView earningsListView = findViewById(R.id.ListViewMoney);

        this.earningAdapter = new EarningAdapter(MoneyInActivity.this, this.earnings);
        earningsListView.setAdapter(this.earningAdapter);


        earningsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                        earningAdapter.remove(earnings.get(pos));
                        earningAdapter.notifyDataSetChanged();
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

    private ArrayList<Earning> getEarnings(){
        // getting the data is handled by the DataHandler
        return this.dh.getEarningsList();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current earnings");
        this.dh.saveMoneyInData(this.earnings);
    }
}