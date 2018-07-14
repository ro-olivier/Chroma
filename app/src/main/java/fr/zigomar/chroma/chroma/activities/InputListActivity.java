package fr.zigomar.chroma.chroma.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

        this.activity_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("CHROMA", "Clicked the " + position + "-th item.");

                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle(R.string.DeleteTitle);
                builder.setMessage(R.string.DeleteItemQuestion);

                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getItems().remove(pos);
                        getAdapter().notifyDataSetChanged();
                        updateSummary();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        this.activity_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener()  {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("CHROMA", "Clicked the " + position + "-th item.");

                buildInputFragmentForUpdate(position);
            }

        });
    }

    protected abstract void buildInputFragmentForUpdate(int position);

    protected abstract ArrayList<?> getItems();
    protected abstract ArrayAdapter<?> getAdapter();

    protected abstract void updateSummary();

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
