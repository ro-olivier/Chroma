package fr.zigomar.chroma.chroma.listeners;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import fr.zigomar.chroma.chroma.R;

public class RemoveTransportStepClickListener implements View.OnClickListener {

    private LinearLayout stepsList;

    public RemoveTransportStepClickListener(LinearLayout stepLists) {
        this.stepsList = stepLists;
    }

    @Override
    public void onClick(View v) {

        this.stepsList.removeViewAt(this.stepsList.getChildCount() - 1);

        ImageView addButton = this.stepsList.getChildAt(this.stepsList.getChildCount() - 1).findViewById(R.id.AddStep);
        addButton.setVisibility(View.VISIBLE);

        if (this.stepsList.getChildCount() > 1) {
            ImageView removeButton = this.stepsList.getChildAt(this.stepsList.getChildCount() - 1).findViewById(R.id.RemoveStep);
            removeButton.setVisibility(View.VISIBLE);
        }
    }
}
