package fr.zigomar.chroma.chroma.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.R;
import uk.co.markormesher.android_fab.FloatingActionButton;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

public abstract class InputListActivity extends InputActivity {

    private FloatingActionButton fab;
    private ArrayList<SpeedDialMenuItem> fab_items = new ArrayList<>();
    private ListView activity_list_view = null;

    public FloatingActionButton getFAB() {
        return this.fab;
    }

    public ArrayList<SpeedDialMenuItem> getFABItems() {
        return this.fab_items;
    }

    public ListView getListView() {
        return this.activity_list_view;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity_list_view = findViewById(R.id.data_list);

        this.fab = findViewById(R.id.fab);
        this.fab.setContentCoverColour(0x88ffffff);
        this.fab.setElevation(this.getListView().getElevation() + 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
