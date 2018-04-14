package fr.zigomar.chroma.chroma.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.zigomar.chroma.chroma.adapters.modeladapters.DrinkAdapter;
import fr.zigomar.chroma.chroma.model.Drink;
import fr.zigomar.chroma.chroma.R;

public class AlcoholActivity extends InputActivity {

    private Spinner genericSpinner;
    private TextView middleText;
    private Spinner specificSpinner;

    private TextView textShared;
    private Spinner sharedSpinner;
    private LinearLayout sharedLayout;

    private EditText detailsField;
    private EditText commentField;

    private EditText volumeField;
    private EditText degreeField;
    private LinearLayout valueLayout;

    private ArrayList<Drink> drinks;
    private DrinkAdapter drinkAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting the views from their id
        this.genericSpinner = findViewById(R.id.TextInputGeneric);
        this.middleText = findViewById(R.id.AlcoholTextBetweenInputFields);
        this.specificSpinner = findViewById(R.id.AlcoholSpinnerField);
        this.sharedLayout = findViewById(R.id.SharedAlcoholLayout);
        this.textShared = findViewById(R.id.AlcoholTextShared);
        this.sharedSpinner = findViewById(R.id.AlcoholSharedSpinnerField);
        this.detailsField = findViewById(R.id.TextInputSpecific);
        this.commentField = findViewById(R.id.TextInputComment);
        this.valueLayout = findViewById(R.id.VolumeAndDegreeLayout);
        this.volumeField = findViewById(R.id.DrinkVolume);
        this.degreeField = findViewById(R.id.DrinkDegree);

        Button addButton = findViewById(R.id.AddButton);

        ArrayAdapter<CharSequence> genericAdapter = ArrayAdapter.createFromResource(AlcoholActivity.this,
                R.array.genericDrinks, android.R.layout.simple_spinner_item);
        genericAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.genericSpinner.setAdapter(genericAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "addButton.setOnClickListener.onClick was called");
                String generic = genericSpinner.getSelectedItem().toString();
                String specific = specificSpinner.getSelectedItem().toString();
                String details = detailsField.getText().toString();
                String comments = commentField.getText().toString();
                String vs = volumeField.getText().toString();
                String ds = degreeField.getText().toString();
                String description;

                if (genericSpinner.getSelectedItemPosition() == 5) {
                    // "Other" selected in first spinner
                    description = details;
                } else if ((genericSpinner.getSelectedItemPosition() == 3 ||
                        genericSpinner.getSelectedItemPosition() == 4) &&
                        specificSpinner.getSelectedItemPosition() == 2) {
                    // "Other" (specific) was selected for glass or bottle (generic)
                    description = "A " + generic + " of " + details;
                } else {
                    description = "A " + generic + " of " + specific;
                }

                if (genericSpinner.getSelectedItemPosition() == 4) {
                    // "bottle" was selected, let's hope it was shared, so let's include it in the drink description
                    String shared = sharedSpinner.getSelectedItem().toString();
                    description += " shared among " + shared;
                }

                if (description.length() > 0) {
                    if (vs.length() > 0) {
                        if (ds.length() > 0) {
                            try {
                                double vol = Double.parseDouble(vs);
                                double deg = Double.parseDouble(ds);
                                drinkAdapter.add(new Drink(description, comments, vol, deg));
                                updateSummary();
                                resetViews();
                            } catch (NumberFormatException e) {
                                Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.MissingAlcoholContent, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.MissingAlcoholVolume, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MissingAlcoholDescription, Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.genericSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("CHROMA", "this.genericSpinner.setOnItemSelectedListener.onItemSelect called");
                middleText.setText(R.string.of);
                ArrayAdapter<CharSequence> specificAdapter;

                specificSpinner.setOnItemSelectedListener(null);
                sharedSpinner.setOnItemSelectedListener(null);

                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                        Log.i("CHROMA", "THE BEER WAS CHOSEN!");

                        valueLayout.setVisibility(View.INVISIBLE);
                        middleText.setVisibility(View.VISIBLE);
                        specificSpinner.setVisibility(View.VISIBLE);

                        specificAdapter = ArrayAdapter.createFromResource(AlcoholActivity.this,
                                R.array.beers, android.R.layout.simple_spinner_item);
                        specificAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        specificSpinner.setAdapter(specificAdapter);

                        specificSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.i("CHROMA", " specificSpinner.setOnItemSelectedListener.onItemSelected was called");
                                String[] beer_alcohol_content = getApplicationContext().getResources().getStringArray(R.array.beer_alcohol);
                                degreeField.setText(beer_alcohol_content[position]);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Nothing for now
                            }
                        });


                        final String volume = (position == 0) ? "500": (position == 1) ? "330":"250";
                        volumeField.setText(volume);

                        break;
                    case 3:
                        // glass
                        specificAdapter = ArrayAdapter.createFromResource(AlcoholActivity.this,
                                R.array.glass, android.R.layout.simple_spinner_item);
                        specificAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        specificSpinner.setAdapter(specificAdapter);

                        volumeField.setText("120");
                        degreeField.setText("13");
                        middleText.setVisibility(View.VISIBLE);
                        specificSpinner.setVisibility(View.VISIBLE);
                        detailsField.setVisibility(View.VISIBLE);
                        valueLayout.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        // bottle
                        Log.i("CHROMA", "THE MIGHTY BOTTLE WAS CHOSEN !");
                        specificAdapter = ArrayAdapter.createFromResource(AlcoholActivity.this,
                                R.array.bottle, android.R.layout.simple_spinner_item);
                        specificAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        specificSpinner.setAdapter(specificAdapter);

                        specificSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                textShared.setText("Shared among ");
                                textShared.setVisibility(View.VISIBLE);

                                ArrayAdapter<CharSequence> sharedAdapter = ArrayAdapter.createFromResource(AlcoholActivity.this,
                                        R.array.bottle_division, android.R.layout.simple_spinner_item);
                                sharedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sharedSpinner.setAdapter(sharedAdapter);

                                sharedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        float volume = 700 / (position + 2);
                                        volumeField.setText(String.valueOf(volume));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                degreeField.setText("13");
                                sharedLayout.setVisibility(View.VISIBLE);
                                middleText.setVisibility(View.VISIBLE);
                                specificSpinner.setVisibility(View.VISIBLE);
                                sharedSpinner.setVisibility(View.VISIBLE);
                                detailsField.setVisibility(View.VISIBLE);
                                valueLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing for now
                            }
                        });
                        break;
                    case 5:
                        // selected other
                        specificSpinner.setVisibility(View.INVISIBLE);
                        sharedLayout.setVisibility(View.INVISIBLE);
                        volumeField.setText("");
                        degreeField.setText("");
                        middleText.setVisibility(View.INVISIBLE);
                        detailsField.setVisibility(View.VISIBLE);
                        valueLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("CHROMA", "this.genericSpinner.setOnItemSelectedListener.onNothingSelected called");
            }
        });

        // init of the data : fetch drinks data in the currentDate file if it exist
        this.drinks = getDrinks();
        updateSummary();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) drinks
        ListView drinksListView = findViewById(R.id.ListViewAlcohol);

        this.drinkAdapter = new DrinkAdapter(AlcoholActivity.this, this.drinks);
        drinksListView.setAdapter(this.drinkAdapter);

        drinksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle(R.string.DeleteTitle);
                builder.setMessage(R.string.DeleteItemQuestion);

                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drinkAdapter.remove(drinks.get(pos));
                        drinkAdapter.notifyDataSetChanged();
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
    }

    private void resetViews() {
        this.degreeField.setText("");
        this.volumeField.setText("");
        this.detailsField.setText("");
        this.commentField.setText("");
        this.detailsField.setVisibility(View.INVISIBLE);
        this.sharedLayout.setVisibility(View.INVISIBLE);
        this.valueLayout.setVisibility(View.INVISIBLE);

        this.genericSpinner.setSelection(1, true);
        this.genericSpinner.setSelection(0, true);
        this.middleText.setVisibility(View.VISIBLE);
        this.specificSpinner.setVisibility(View.VISIBLE);
        this.specificSpinner.setSelection(1, true);
        this.specificSpinner.setSelection(0, true);
    }

    private void updateSummary() {
        LinearLayout data_summary = findViewById(R.id.data_summary);
        data_summary.setVisibility(View.VISIBLE);

        TextView field0_data = findViewById(R.id.data_summary_field0);
        TextView field1_data = findViewById(R.id.data_summary_field1);
        TextView field2_data = findViewById(R.id.data_summary_field2);
        TextView field1_text = findViewById(R.id.data_summary_text1);
        TextView field2_text = findViewById(R.id.data_summary_text2);

        if (this.drinks.size() > 0) {

            field0_data.setText(R.string.Summary);

            field1_data.setVisibility(View.INVISIBLE);
            field1_text.setVisibility(View.INVISIBLE);

            double ua_total = 0;
            for (Drink d : this.drinks) {
                ua_total += d.getUA();
            }
            DecimalFormat df = new DecimalFormat("0.00");
            field2_data.setText(df.format(ua_total));

            field2_text.setText(R.string.ua);

        } else {
            data_summary.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<Drink> getDrinks(){
        // getting the data is handled by the DataHandler
        return this.dh.getDrinksList();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current drinks");
        this.dh.saveAlcoholData(this.drinks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
}