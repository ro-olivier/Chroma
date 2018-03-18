package fr.zigomar.chroma.chroma.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import fr.zigomar.chroma.chroma.model.Spending;
import fr.zigomar.chroma.chroma.R;

public class SpendingAdapter extends ArrayAdapter<Spending> {

    public SpendingAdapter(Context context, List<Spending> spendings) {
        super(context, 0, spendings);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unit_spending, parent, false);
        }

        SpendingViewHolder viewHolder = (SpendingViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new SpendingViewHolder();
            viewHolder.description = convertView.findViewById(R.id.spending_description);
            viewHolder.category = convertView.findViewById(R.id.spending_category);
            viewHolder.amount = convertView.findViewById(R.id.spending_amount);
            convertView.setTag(viewHolder);
        }

        Spending spending = getItem(position);

        DecimalFormat df = new DecimalFormat("0.00");

        //il ne reste plus qu'à remplir notre vue
        assert spending != null;
        viewHolder.description.setText(spending.getDescription());
        viewHolder.category.setText(spending.getCategory());
        viewHolder.amount.setText(df.format(spending.getAmount()));

        return convertView;
    }

    private class SpendingViewHolder{
        TextView description;
        TextView category;
        TextView amount;
    }
}
