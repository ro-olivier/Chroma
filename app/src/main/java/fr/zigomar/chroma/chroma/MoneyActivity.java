package fr.zigomar.chroma.chroma;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MoneyActivity extends AppCompatActivity {

    public static final String CURRENT_DATE = "com.example.chroma.current_date";

    private JSONObject moneyData;
    List<Spending> spendings;

    private Date currentDate = new Date();
    private String filename;

    private TextView descField;
    private Spinner catField;
    private TextView amountField;
    private Button addButton;
    private ListView spendingsListView;

    private SpendingAdapter spendingAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);

        this.currentDate.setTime(getIntent().getLongExtra(CURRENT_DATE, -1));
        updateDateView();
        this.filename = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(this.currentDate);

        this.descField = (TextView) findViewById(R.id.TextDescription);
        this.catField = (Spinner) findViewById(R.id.TextCategory);
        this.amountField = (TextView) findViewById(R.id.TextAmount);
        this.addButton = (Button) findViewById(R.id.AddButton);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String d = descField.getText().toString();
                String c = catField.getSelectedItem().toString();
                if (amountField.getText().length() > 0 && descField.getText().length() > 0 && catField.getSelectedItem().toString().length() > 0) {
                    try {
                        Double a = Double.valueOf(amountField.getText().toString());

                        spendingAdapter.add(new Spending(d, c, a));
                        Log.i("CHORMA", "Currently " + spendings.size() + " spendings.");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Unable to parse the value.", Toast.LENGTH_SHORT).show();
                        // afficher un message indiquant que le chiffre n'a pas pu être parsé
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "All three values are required.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.spendingsCategories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catField.setAdapter(spinnerAdapter);

        // init : récupérer les dépenses courantes dans le JSON s'il en existe
        this.spendings = getSpendings();

        this.spendingsListView = (ListView) findViewById(R.id.ListViewMoney);

        this.spendingAdapter = new SpendingAdapter(MoneyActivity.this, spendings);
        this.spendingsListView.setAdapter(spendingAdapter);
    }

    private List<Spending> getSpendings(){
        List<Spending> spendings = new ArrayList<Spending>();
        spendings.add(new Spending("Un premier test, les courses.", "Food", 10));
        spendings.add(new Spending("Un second test, un verre.", "Drinks", 5));
        spendings.add(new Spending("Un troisième test, quelques satoshis...", "Tech", 50));
        return spendings;
    }

    private void updateDateView() {
        TextView dateView = (TextView) findViewById(R.id.DateTextView);
        String formattedDate = (new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(this.currentDate));
        Log.i("CHROMA", "Updating date : " + formattedDate);
        dateView.setText(formattedDate);
    }
}
