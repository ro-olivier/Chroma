package fr.zigomar.chroma.chroma.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.zigomar.chroma.chroma.adapters.modeladapters.SpendingAdapter;
import fr.zigomar.chroma.chroma.adapters.speeddialmenuadapters.SpendingSpeedDialMenuAdapter;
import fr.zigomar.chroma.chroma.fragments.SpendingInputFragment;
import fr.zigomar.chroma.chroma.model.Spending;
import fr.zigomar.chroma.chroma.R;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

public class MoneyActivity extends InputListActivity {

    private ArrayList<Spending> spendings;
    private SpendingAdapter spendingAdapter;

    // Overridden getters from InputListActivity
    @Override
    public ArrayList<Spending> getItems() {
        return this.spendings;
    }

    @Override
    public SpendingAdapter getAdapter() {
        return this.spendingAdapter;
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

        // init of the data : fetch spendings data in the currentDate file if it exist
        this.spendings = this.dh.getSpendingsList();
        updateSummary();

        //Building the action menu
        SpeedDialMenuItem fab_add = new SpeedDialMenuItem(getApplicationContext());
        fab_add.setIcon(R.drawable.plus_sign_16dp);
        fab_add.setLabel(R.string.addSpending);

        this.getFABItems().add(fab_add);

        this.getFAB().setSpeedDialMenuAdapter(new SpendingSpeedDialMenuAdapter(this, this.getFABItems()));

        // Setting the adapter for the list view
        this.spendingAdapter = new SpendingAdapter(MoneyActivity.this, this.spendings);
        this.getListView().setAdapter(this.spendingAdapter);

    }

    // Overridden method from InputListActivity
    @Override
    protected void buildInputFragmentForUpdate(int position) {
        Bundle data = new Bundle();
        Spending spending = this.spendings.get(position);
        data.putString("description", spending.getDescription());
        data.putString("category", spending.getCategory());
        data.putString("amount", String.valueOf(spending.getAmount()));
        data.putInt("position", position);

        SpendingInputFragment inputFragment = new SpendingInputFragment();
        inputFragment.setArguments(data);
        inputFragment.show(this.getFragmentManager(), "SpendingInputDialogFragment");
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

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current spendings");
        this.dh.saveMoneyData(this.spendings);
    }
    ///////////////////////////////////////////////

    public void addSpending(Spending spending) {
        this.spendingAdapter.add(spending);
        updateSummary();
        Log.i("CHROMA", "Currently " + this.spendings.size() + " spendings.");
    }

    public void updateSpending(int position, Spending spending) {
        this.spendings.set(position, spending);
        updateSummary();
        this.spendingAdapter.notifyDataSetChanged();
    }
}