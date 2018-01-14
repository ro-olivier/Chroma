package fr.zigomar.chroma.chroma.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.zigomar.chroma.chroma.Model.Trip;
import fr.zigomar.chroma.chroma.R;

public class TripAdapter extends ArrayAdapter<Trip> {

    public TripAdapter(Context context, List<Trip> trips) {
        super(context, 0, trips);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unit_trip, parent, false);
        }

        TripViewHolder viewHolder = (TripViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new TripViewHolder();
            viewHolder.description = convertView.findViewById(R.id.trip_description);
            viewHolder.cost = convertView.findViewById(R.id.trip_cost);
            convertView.setTag(viewHolder);
        }

        Trip trip = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        assert trip != null;
        viewHolder.description.setText(trip.tripString());
        viewHolder.cost.setText(String.valueOf(trip.getCost()));

        return convertView;
    }

    private class TripViewHolder {
        TextView description;
        TextView cost;
    }
}
