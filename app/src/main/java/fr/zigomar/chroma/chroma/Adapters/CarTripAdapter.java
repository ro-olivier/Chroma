package fr.zigomar.chroma.chroma.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.zigomar.chroma.chroma.Model.CarTrip;
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
            viewHolder.description = (TextView) convertView.findViewById(R.id.carTrip_description);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.carTrip_duration);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.carTrip_distance);
            convertView.setTag(viewHolder);
        }

        CarTrip carTrip = getItem(position);


        //il ne reste plus qu'à remplir notre vue
        assert carTrip != null;
        if (carTrip.getCompleted()) {
            long h = TimeUnit.MILLISECONDS.toHours(carTrip.getDuration());
            long m = TimeUnit.MILLISECONDS.toMinutes(carTrip.getDuration()) - TimeUnit.HOURS.toMinutes(h);
            long s = TimeUnit.MILLISECONDS.toSeconds(carTrip.getDuration())
                        - TimeUnit.MINUTES.toSeconds(m)
                        - TimeUnit.HOURS.toSeconds(h);
            viewHolder.description.setText(carTrip.getDescription());
            viewHolder.duration.setText(String.format("%dh %dmin %ds",h,m,s));
            viewHolder.distance.setText(String.format("%s km", String.valueOf(carTrip.getDistance())));
        }

        return convertView;
    }

        private class CarTripViewHolder {
            TextView description;
            TextView duration;
            TextView distance;
        }
}
