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

import fr.zigomar.chroma.chroma.model.Transaction;
import fr.zigomar.chroma.chroma.R;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        super(context, 0, transactions);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unit_transaction, parent, false);
        }

        TransactionViewHolder viewHolder = (TransactionViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TransactionViewHolder();
            viewHolder.description = convertView.findViewById(R.id.transaction_description);
            viewHolder.category = convertView.findViewById(R.id.transaction_category);
            viewHolder.amount = convertView.findViewById(R.id.transaction_amount);
            convertView.setTag(viewHolder);
        }

        Transaction transaction = getItem(position);

        DecimalFormat df = new DecimalFormat("0.00");

        //il ne reste plus qu'à remplir notre vue
        assert transaction != null;
        viewHolder.description.setText(transaction.getDescription());
        viewHolder.category.setText(transaction.getCategory());
        viewHolder.amount.setText(df.format(transaction.getAmount()));

        return convertView;
    }

    private class TransactionViewHolder{
        TextView description;
        TextView category;
        TextView amount;
    }
}
