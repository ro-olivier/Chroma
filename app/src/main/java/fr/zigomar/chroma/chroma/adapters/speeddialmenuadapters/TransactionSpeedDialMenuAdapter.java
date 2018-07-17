package fr.zigomar.chroma.chroma.adapters.speeddialmenuadapters;

import android.content.Context;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import fr.zigomar.chroma.chroma.activities.MoneyActivity;
import fr.zigomar.chroma.chroma.fragments.TransactionInputFragment;
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

public class TransactionSpeedDialMenuAdapter extends SpeedDialMenuAdapter {
    private List<SpeedDialMenuItem> items;
    private MoneyActivity callingActivity;

    public TransactionSpeedDialMenuAdapter(MoneyActivity callingActivity, List<SpeedDialMenuItem> items) {
        this.items = items;
        this.callingActivity = callingActivity;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @NotNull
    @Override
    public SpeedDialMenuItem getMenuItem(Context context, int i) {
        return this.items.get(i);
    }

    @Override
    public boolean onMenuItemClick(int position) {
        Bundle data = new Bundle();
        TransactionInputFragment inputFragment = new TransactionInputFragment();

        switch (position) {
            case 0:
                inputFragment.setArguments(data);
                inputFragment.show(this.callingActivity.getFragmentManager(), "TransactionInputDialogFragment");
                break;

            case 1:
                data.putString("category", "Income");
                inputFragment.setArguments(data);
                inputFragment.show(this.callingActivity.getFragmentManager(), "TransactionInputDialogFragment");
                break;
        }

        return true;
    }
}
