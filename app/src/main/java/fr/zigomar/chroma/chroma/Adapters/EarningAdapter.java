package fr.zigomar.chroma.chroma.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.zigomar.chroma.chroma.Model.Earning;
import fr.zigomar.chroma.chroma.R;

public class EarningAdapter extends ArrayAdapter<Earning> {

    public EarningAdapter(Context context, List<Earning> earnings) {
        super(context, 0, earnings);
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

        Earning earning = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        assert earning != null;
        viewHolder.description.setText(earning.getDescription());
        viewHolder.amount.setText(String.valueOf(earning.getAmount()));

        return convertView;
    }

    private class EarningViewHolder{
        TextView description;
        TextView amount;
    }
}
