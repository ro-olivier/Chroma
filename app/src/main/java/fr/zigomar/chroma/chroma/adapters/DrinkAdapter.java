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

import fr.zigomar.chroma.chroma.model.Drink;
import fr.zigomar.chroma.chroma.R;

public class DrinkAdapter extends ArrayAdapter<Drink> {

    public DrinkAdapter(Context context, List<Drink> drinks) {
        super(context, 0, drinks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unit_alcohol, parent, false);
        }

        DrinkViewHolder viewHolder = (DrinkViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new DrinkViewHolder();
            viewHolder.description = convertView.findViewById(R.id.drink_description);
            viewHolder.ua = convertView.findViewById(R.id.drink_ua);
            convertView.setTag(viewHolder);
        }

        Drink drink = getItem(position);

        DecimalFormat df = new DecimalFormat("0.00");

        //il ne reste plus qu'à remplir notre vue
        assert drink != null;
        viewHolder.description.setText(drink.getDescription());
        viewHolder.ua.setText(df.format(drink.getUA()));

        return convertView;
    }

    private class DrinkViewHolder{
        TextView description;
        TextView ua;
    }
}
