package fr.zigomar.chroma.chroma.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.Adapters.OpenBooksAdapter;
import fr.zigomar.chroma.chroma.Model.Book;
import fr.zigomar.chroma.chroma.Model.DataHandler;
import fr.zigomar.chroma.chroma.R;

public class NewBookActivity extends InputActivity {

    private TextView title;
    private TextView author;

    private ArrayList<Book> openBooks;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        this.dh = new DataHandler(this.getApplicationContext(),
                getString(R.string.OpenBooksFileName));

        // setting the view's layout, yay, we can see stuff on the screen!
        setContentView(R.layout.activity_newbook);

        init();

        this.title = findViewById(R.id.BookTitle);
        this.author = findViewById(R.id.BookAuthor);

        Button addNewBookButton = findViewById(R.id.AddNewBookButton);
        addNewBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (title.getText().toString().length() > 0 && author.getText().toString().length() > 0) {
                    Log.i("CHROMA", "Added a new book !");
                    openBooks.add(new Book(title.getText().toString(), author.getText().toString(), currentDate));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.BookDetailsRequired, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // init of the data : fetch spendings data in the currentDate file if it exist
        this.openBooks = dh.getOpenBooksData();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) spendings
        ListView openBooksView = findViewById(R.id.ListViewOpenBooks);

        OpenBooksAdapter openBooksAdapter = new OpenBooksAdapter(NewBookActivity.this, this.openBooks);
        openBooksView.setAdapter(openBooksAdapter);
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current open books");
        dh.saveOpenBookData(openBooks);
    }
}

