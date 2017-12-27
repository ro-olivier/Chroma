package fr.zigomar.chroma.chroma;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Robin on 27/12/2017.
 */

public class MoneyActivity extends AppCompatActivity {

    public static final String CURRENT_DATE = "com.example.chroma.current_date";

    private JSONObject moneyData;

    private Date currentDate = new Date();
    private String filename;

    private ListView spendingsListView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);

        this.currentDate.setTime(getIntent().getLongExtra(CURRENT_DATE, -1));
        updateDateView();
        this.filename = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(this.currentDate);

        // init : récupérer les dépenses courantes dans le JSON s'il en existe
        List<Spending> spendings = getSpendings();
        Log.i("CHORMA", "Generated spendings : " + spendings.toString());

        this.spendingsListView = (ListView) findViewById(R.id.ListViewMoney);

        SpendingAdapter adapter = new SpendingAdapter(MoneyActivity.this, spendings);
        this.spendingsListView.setAdapter(adapter);



    }

    private List<Spending> getSpendings(){
        List<Spending> spendings = new ArrayList<Spending>();
        spendings.add(new Spending("Un premier test, les courses.", "Food", 10));
        spendings.add(new Spending("Un second test, un verre.", "Drinks", 5));
        spendings.add(new Spending("Un troisième test, quelques satoshis...", "Tech", 50));
        spendings.add(new Spending("Un premier test, les courses.", "Food", 10));
        spendings.add(new Spending("Un second test, un verre.", "Drinks", 5));
        spendings.add(new Spending("Un troisième test, quelques satoshis...", "Tech", 50));
        spendings.add(new Spending("Un premier test, les courses.", "Food", 10));
        spendings.add(new Spending("Un second test, un verre.", "Drinks", 5));
        spendings.add(new Spending("Un troisième test, quelques satoshis...", "Tech", 50));
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
