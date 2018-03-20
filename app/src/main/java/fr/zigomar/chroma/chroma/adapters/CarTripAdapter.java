package fr.zigomar.chroma.chroma.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.zigomar.chroma.chroma.model.CarTrip;
import fr.zigomar.chroma.chroma.R;

public class CarTripAdapter extends ArrayAdapter<CarTrip> {

    public CarTripAdapter(Context context, List<CarTrip> carTrips) {
        super(context, 0, carTrips);
    }

        @SuppressLint("DefaultLocale")
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unit_cartrip, parent, false);
        }

        CarTripAdapter.CarTripViewHolder viewHolder = (CarTripAdapter.CarTripViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new CarTripAdapter.CarTripViewHolder();
            viewHolder.description = convertView.findViewById(R.id.carTrip_description);
            viewHolder.duration = convertView.findViewById(R.id.carTrip_duration);
            viewHolder.distance = convertView.findViewById(R.id.carTrip_distance);
            viewHolder.distanceUnit = convertView.findViewById(R.id.carTrip_distanceUnit);
            convertView.setTag(viewHolder);
        }

        CarTrip carTrip = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        if (carTrip != null && carTrip.getCompleted()) {
            long h = TimeUnit.MILLISECONDS.toHours(carTrip.getDuration());
            long m = TimeUnit.MILLISECONDS.toMinutes(carTrip.getDuration()) - TimeUnit.HOURS.toMinutes(h);
            //long s = TimeUnit.MILLISECONDS.toSeconds(carTrip.getDuration())
            //            - TimeUnit.MINUTES.toSeconds(m)
            //            - TimeUnit.HOURS.toSeconds(h);

            viewHolder.description.setText(carTrip.getDescription());
            viewHolder.duration.setText(String.format("%02d:%02d",h,m));

            DecimalFormat df = new DecimalFormat("0.0");

            viewHolder.distance.setText(df.format(carTrip.getDistance()));
            viewHolder.distanceUnit.setVisibility(View.VISIBLE);
        } else {
            viewHolder.description.setText(R.string.OngoingTrip);
            viewHolder.duration.setText("");
            viewHolder.distance.setText("");
            viewHolder.distanceUnit.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

        private class CarTripViewHolder {
            TextView description;
            TextView duration;
            TextView distance;
            TextView distanceUnit;
        }
}
