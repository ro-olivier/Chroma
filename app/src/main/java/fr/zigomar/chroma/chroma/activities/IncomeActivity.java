package fr.zigomar.chroma.chroma.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.zigomar.chroma.chroma.adapters.modeladapters.IncomeAdapter;
import fr.zigomar.chroma.chroma.model.Income;
import fr.zigomar.chroma.chroma.R;

public class IncomeActivity extends InputActivity {

    private TextView descField;
    private TextView amountField;

    private ArrayList<Income> incomes;
    private IncomeAdapter incomeAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

                        incomeAdapter.add(new Income(d, a));
                        updateSummary();
                        Log.i("CHROMA", "Currently " + incomes.size() + " incomes.");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.InvalidSpending_Amount, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.InvalidIncome_AllValuesRequired, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // init of the data : fetch incomes data in the currentDate file if it exist
        this.incomes = getIncomes();
        updateSummary();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) incomes
        ListView incomesListView = findViewById(R.id.ListViewMoney);

        this.incomeAdapter = new IncomeAdapter(IncomeActivity.this, this.incomes);
        incomesListView.setAdapter(this.incomeAdapter);


        incomesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                        incomeAdapter.remove(incomes.get(pos));
                        incomeAdapter.notifyDataSetChanged();
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

    private void updateSummary() {
        LinearLayout data_summary = findViewById(R.id.data_summary);
        data_summary.setVisibility(View.VISIBLE);

        TextView field0_data = findViewById(R.id.data_summary_field0);
        TextView field1_data = findViewById(R.id.data_summary_field1);
        TextView field2_data = findViewById(R.id.data_summary_field2);
        TextView field1_text = findViewById(R.id.data_summary_text1);
        TextView field2_text = findViewById(R.id.data_summary_text2);

        if (this.incomes.size() > 0) {

            field0_data.setText(R.string.Summary);

            field1_data.setVisibility(View.INVISIBLE);
            field1_text.setVisibility(View.INVISIBLE);

            double amount_total = 0;
            for (Income d : this.incomes) {
                amount_total += d.getAmount();
            }
            DecimalFormat df = new DecimalFormat("0.00");
            field2_data.setText(df.format(amount_total));

            field2_text.setText(R.string.Currency);

        } else {
            data_summary.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<Income> getIncomes(){
        // getting the data is handled by the DataHandler
        return this.dh.getIncomesList();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current incomes");
        this.dh.saveMoneyInData(this.incomes);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
}