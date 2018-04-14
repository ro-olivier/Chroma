package fr.zigomar.chroma.chroma.adapters.modeladapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import fr.zigomar.chroma.chroma.model.Income;
import fr.zigomar.chroma.chroma.R;

public class IncomeAdapter extends ArrayAdapter<Income> {

    public IncomeAdapter(Context context, List<Income> incomes) {
        super(context, 0, incomes);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unit_earning, parent, false);
        }

        EarningViewHolder viewHolder = (EarningViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new EarningViewHolder();
            viewHolder.description = convertView.findViewById(R.id.earning_description);
            viewHolder.amount = convertView.findViewById(R.id.earning_amount);
            convertView.setTag(viewHolder);
        }

        Income income = getItem(position);

        DecimalFormat df = new DecimalFormat("0.00");

        //il ne reste plus qu'à remplir notre vue
        assert income != null;
        viewHolder.description.setText(income.getDescription());
        viewHolder.amount.setText(df.format(income.getAmount()));

        return convertView;
    }

    private class EarningViewHolder{
        TextView description;
        TextView amount;
    }
}
