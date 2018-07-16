package fr.zigomar.chroma.chroma.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.activities.MoneyActivity;
import fr.zigomar.chroma.chroma.model.Spending;

public class SpendingInputFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private MoneyActivity callingActivity;

    private TextView descField;
    private Spinner catField;
    private TextView amountField;

    private int position;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.callingActivity = (MoneyActivity) getActivity();
        LayoutInflater inflater = LayoutInflater.from(this.callingActivity);
        Bundle data = this.getArguments();

        this.position = data.getInt("position", -1);

        View v = inflater.inflate(R.layout.input_money, (ViewGroup) getView(), false);

        this.descField = v.findViewById(R.id.TextDescription);
        this.catField = v.findViewById(R.id.TextCategory);
        this.amountField = v.findViewById(R.id.TextAmount);

        this.descField.setText(data.getString("description"));
        this.amountField.setText(data.getString("amount"));

        this.descField.setBackgroundColor(this.callingActivity.getResources().getColor(R.color.colorDialogFields));
        this.amountField.setBackgroundColor(this.callingActivity.getResources().getColor(R.color.colorDialogFields));

        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(callingActivity,
                R.array.spendingsCategories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.catField.setAdapter(spinnerAdapter);
        if (data.getString("category") != null) {
            int spinnerPosition = spinnerAdapter.getPosition(data.getString("category"));
            this.catField.setSelection(spinnerPosition);
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setMessage(R.string.addSpending)
                .setPositiveButton("OK", this)
                .setView(v)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String d = descField.getText().toString();
        String c = catField.getSelectedItem().toString();
        String a_str = amountField.getText().toString();
        Double a_dbl;

        try {
            a_dbl = Double.valueOf(amountField.getText().toString());

            if (this.position < 0) {
                this.callingActivity.addSpending(new Spending(d, c, a_dbl));
            } else {
                this.callingActivity.updateSpending(this.position, new Spending(d, c, a_dbl));
            }

        } catch (NumberFormatException e) {
            Toast.makeText(callingActivity, R.string.InvalidSpending_Amount, Toast.LENGTH_SHORT).show();
            rebuildDialog(d, c, a_str);
        } catch (Spending.InvalidDescriptionException e) {
            Toast.makeText(this.callingActivity, R.string.InvalidSpending_Description, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            rebuildDialog(d, c, a_str);
        } catch (Spending.InvalidCategoryException e) {
            Toast.makeText(this.callingActivity, R.string.InvalidSpending_Category, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            rebuildDialog(d, c, a_str);
        }
    }

    private void rebuildDialog(String d, String c, String a) {
        Bundle data = new Bundle();
        data.putString("amount", a);
        data.putString("description", d);
        data.putString("category", c);
        data.putInt("position", this.position);

        this.dismiss();

        SpendingInputFragment inputFragment = new SpendingInputFragment();
        inputFragment.setArguments(data);
        inputFragment.show(this.callingActivity.getFragmentManager(), "SpendingInputDialogFragment");
    }
}
