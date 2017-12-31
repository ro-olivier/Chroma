package fr.zigomar.chroma.chroma;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class DrinkAdapter extends ArrayAdapter<Drink> {

    DrinkAdapter(Context context, List<Drink> drinks) {
        super(context, 0, drinks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unit_alcohol, parent, false);
        }

        DrinkViewHolder viewHolder = (DrinkViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new DrinkViewHolder();
            viewHolder.description = (TextView) convertView.findViewById(R.id.drink_description);
            viewHolder.ua = (TextView) convertView.findViewById(R.id.drink_ua);
            convertView.setTag(viewHolder);
        }

        Drink drink = getItem(position);

        DecimalFormat df = new DecimalFormat("0.00");

        //il ne reste plus qu'à remplir notre vue
        viewHolder.description.setText(drink.getDescription());
        viewHolder.ua.setText(df.format(drink.getUA()));

        return convertView;
    }

    private class DrinkViewHolder{
        TextView description;
        TextView ua;
    }
}
