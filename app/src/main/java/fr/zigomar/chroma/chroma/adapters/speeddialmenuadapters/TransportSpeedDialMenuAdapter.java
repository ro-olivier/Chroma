package fr.zigomar.chroma.chroma.adapters.speeddialmenuadapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.activities.SettingsActivity;
import fr.zigomar.chroma.chroma.activities.TransportActivity;
import fr.zigomar.chroma.chroma.fragments.TransportInputFragment;
import fr.zigomar.chroma.chroma.model.Step;
import fr.zigomar.chroma.chroma.model.Trip;
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

public class TransportSpeedDialMenuAdapter extends SpeedDialMenuAdapter {

    private List<SpeedDialMenuItem> items;
    private TransportActivity callingActivity;

    public TransportSpeedDialMenuAdapter(TransportActivity callingActivity, List<SpeedDialMenuItem> items) {
        this.items = items;
        this.callingActivity = callingActivity;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public boolean onMenuItemClick(int position) {
        switch (position) {
            case 0:
                FragmentManager fragmentManager = callingActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top, R.anim.exit_to_top, R.anim.exit_to_top);

                callingActivity.findViewById(R.id.TransportActivityInputContainer).setVisibility(View.VISIBLE);

                TransportInputFragment inputFragment = new TransportInputFragment();
                //fragmentTransaction.add(R.id.TransportActivityInputContainer, inputFragment, "inputFragment");
                fragmentTransaction.replace(R.id.TransportActivityInputContainer, inputFragment, "inputFragment");
                fragmentTransaction.addToBackStack("inputFragment");
                //fragmentTransaction.show(inputFragment);
                fragmentTransaction.commit();

                callingActivity.setInputEnabled(inputFragment);

                break;

            case 1:
                if (callingActivity.getNumberOfTrips() > 0) {
                    Trip lastTrip = callingActivity.getLastTrip();

                    ArrayList<Step> backward_steps = new ArrayList<>();

                    try {
                        for (int index = lastTrip.getNumberOfSteps() - 1; index > 0; index--) {

                            backward_steps.add(new Step(lastTrip.getSteps().get(index).getStop(),
                                    lastTrip.getSteps().get(index - 1).getLine()));
                        }

                        backward_steps.add(new Step(lastTrip.getSteps().get(0).getStop()));
                    } catch (Step.EmptyStationException | Step.EmptyLineException e) {
                        e.printStackTrace();
                    }

                    callingActivity.getTripAdapter().add(new Trip(backward_steps, lastTrip.getCost()));
                    callingActivity.updateSummary();
                } else {
                    Toast.makeText(callingActivity, R.string.TripCannotRevertEmptyTrip, Toast.LENGTH_SHORT).show();
                }

                break;

            case 2:
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(callingActivity);
                String commuteString = sharedPref.getString(SettingsActivity.KEY_PREF_COMMUTE, "");
                String tripString = commuteString.split("€")[0];

                double tripCost = Double.parseDouble(commuteString.split("€")[1]);

                callingActivity.getTripAdapter().add(new Trip(tripString, tripCost));
                callingActivity.updateSummary();
                break;
        }

        return true;
    }

    @NotNull
    @Override
    public SpeedDialMenuItem getMenuItem(Context context, int i) {
        return this.items.get(i);
    }
}
