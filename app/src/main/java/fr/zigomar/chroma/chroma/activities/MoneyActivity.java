package fr.zigomar.chroma.chroma.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import fr.zigomar.chroma.chroma.adapters.modeladapters.TransactionAdapter;
import fr.zigomar.chroma.chroma.adapters.speeddialmenuadapters.TransactionSpeedDialMenuAdapter;
import fr.zigomar.chroma.chroma.fragments.TransactionInputFragment;
import fr.zigomar.chroma.chroma.model.Transaction;
import fr.zigomar.chroma.chroma.R;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

public class MoneyActivity extends InputListActivity {

    private ArrayList<Transaction> transactions;
    private TransactionAdapter transactionAdapter;

    // Overridden getters from InputListActivity
    @Override
    public ArrayList<Transaction> getItems() {
        return this.transactions;
    }

    @Override
    public TransactionAdapter getAdapter() {
        return this.transactionAdapter;
    }
    //////////////////////////////////////////

    // Other getter methods //////////////////
    @Override
    public FragmentManager getFragmentManager() {
        return super.getFragmentManager();
    }
    //////////////////////////////////////////

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init of the data : fetch transactions data in the currentDate file if it exist
        this.transactions = this.dh.getTransactionsList();
        updateSummary();

        //Building the action menu
        SpeedDialMenuItem fab_add_spending = new SpeedDialMenuItem(getApplicationContext());
        fab_add_spending.setIcon(R.drawable.spending_16dp);
        fab_add_spending.setLabel(R.string.addSpending);

        SpeedDialMenuItem fab_add_income = new SpeedDialMenuItem(getApplicationContext());
        fab_add_income.setIcon(R.drawable.income_16dp);
        fab_add_income.setLabel(R.string.addIncome);

        this.getFABItems().add(fab_add_spending);
        this.getFABItems().add(fab_add_income);

        this.getFAB().setSpeedDialMenuAdapter(new TransactionSpeedDialMenuAdapter(this, this.getFABItems()));

        // Setting the adapter for the list view
        this.transactionAdapter = new TransactionAdapter(MoneyActivity.this, this.transactions);
        this.getListView().setAdapter(this.transactionAdapter);

    }

    // Overridden method from InputListActivity
    @Override
    protected void buildInputFragmentForUpdate(int position) {
        Bundle data = new Bundle();
        Transaction transaction = this.transactions.get(position);
        data.putString("description", transaction.getDescription());
        data.putString("category", transaction.getCategory());
        data.putString("amount", String.valueOf(transaction.getAmount()));
        data.putInt("position", position);

        TransactionInputFragment inputFragment = new TransactionInputFragment();
        inputFragment.setArguments(data);
        inputFragment.show(this.getFragmentManager(), "TransactionInputDialogFragment");
    }

    // Overridden methods from InputActivity //////
    protected void updateSummary() {
        LinearLayout data_summary = findViewById(R.id.data_summary);
        data_summary.setVisibility(View.VISIBLE);

        TextView field0_data = findViewById(R.id.data_summary_field0);
        TextView field1_data = findViewById(R.id.data_summary_field1);
        TextView field2_data = findViewById(R.id.data_summary_field2);
        TextView field1_text = findViewById(R.id.data_summary_text1);
        TextView field2_text = findViewById(R.id.data_summary_text2);

        if (this.transactions.size() > 0) {

            field0_data.setText(R.string.Summary);

            field1_data.setVisibility(View.INVISIBLE);
            field1_text.setVisibility(View.INVISIBLE);

            double amount_total = 0;
            for (Transaction s : this.transactions) {
                if (!Objects.equals(s.getCategory(), "Income")) {
                    amount_total += s.getAmount();
                }
            }
            DecimalFormat df = new DecimalFormat("0.00");
            field2_data.setText(df.format(amount_total));

            field2_text.setText(R.string.Currency);

        } else {
            data_summary.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current transactions");
        this.dh.saveMoneyData(this.transactions);
    }
    ///////////////////////////////////////////////

    public void addTransaction(Transaction transaction) {
        this.transactionAdapter.add(transaction);
        updateSummary();
        Log.i("CHROMA", "Currently " + this.transactions.size() + " transactions.");
    }

    public void updateTransaction(int position, Transaction transaction) {
        this.transactions.set(position, transaction);
        updateSummary();
        this.transactionAdapter.notifyDataSetChanged();
    }
}