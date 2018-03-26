package fr.zigomar.chroma.chroma.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.zigomar.chroma.chroma.adapters.SpendingAdapter;
import fr.zigomar.chroma.chroma.model.Spending;
import fr.zigomar.chroma.chroma.R;

public class MoneyActivity extends InputActivity {

    private TextView descField;
    private Spinner catField;
    private TextView amountField;

    private ArrayList<Spending> spendings;
    private SpendingAdapter spendingAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting the views from their id
        this.descField = findViewById(R.id.TextDescription);
        this.catField = findViewById(R.id.TextCategory);
        this.amountField = findViewById(R.id.TextAmount);
        Button addButton = findViewById(R.id.AddButton);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String d = descField.getText().toString();
                String c = catField.getSelectedItem().toString();
                if (amountField.getText().length() > 0 && descField.getText().length() > 0 && catField.getSelectedItem().toString().length() > 0) {
                    try {
                        Double a = Double.valueOf(amountField.getText().toString());

                        spendingAdapter.add(new Spending(d, c, a));
                        updateSummary();
                        resetViews();
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
        updateSummary();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) spendings
        ListView spendingsListView = findViewById(R.id.ListViewMoney);

        this.spendingAdapter = new SpendingAdapter(MoneyActivity.this, this.spendings);
        spendingsListView.setAdapter(this.spendingAdapter);


        spendingsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle(R.string.DeleteTitle);
                builder.setMessage(R.string.DeleteItemQuestion);

                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spendingAdapter.remove(spendings.get(pos));
                        spendingAdapter.notifyDataSetChanged();
                        updateSummary();
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

    private void resetViews() {
        descField.setText("");
        amountField.setText("");
        catField.setSelection(0);
    }


    private void updateSummary() {
        LinearLayout data_summary = findViewById(R.id.data_summary);
        data_summary.setVisibility(View.VISIBLE);

        TextView field0_data = findViewById(R.id.data_summary_field0);
        TextView field1_data = findViewById(R.id.data_summary_field1);
        TextView field2_data = findViewById(R.id.data_summary_field2);
        TextView field1_text = findViewById(R.id.data_summary_text1);
        TextView field2_text = findViewById(R.id.data_summary_text2);

        if (this.spendings.size() > 0) {

            field0_data.setText(R.string.Summary);

            field1_data.setVisibility(View.INVISIBLE);
            field1_text.setVisibility(View.INVISIBLE);

            double amount_total = 0;
            for (Spending s : this.spendings) {
                amount_total += s.getAmount();
            }
            DecimalFormat df = new DecimalFormat("0.00");
            field2_data.setText(df.format(amount_total));

            field2_text.setText(R.string.Currency);

        } else {
            data_summary.setVisibility(View.INVISIBLE);
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
}