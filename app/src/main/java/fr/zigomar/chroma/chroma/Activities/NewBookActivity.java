package fr.zigomar.chroma.chroma.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
        Log.i("CHROMA", "onCreate New Book");

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

        final OpenBooksAdapter openBooksAdapter = new OpenBooksAdapter(NewBookActivity.this, this.openBooks);
        openBooksView.setAdapter(openBooksAdapter);

        openBooksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("CHROMA", "Clicked the " + position + "-th item.");

                Log.i("CHROMA", "Switching to book review activity");
                Intent bookReviewIntent = new Intent (NewBookActivity.this, BookReviewActivity.class);
                bookReviewIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                bookReviewIntent.putExtra("BOOK_TITLE", openBooks.get(position).getTitle());
                bookReviewIntent.putExtra("BOOK_AUTHOR", openBooks.get(position).getAuthor());
                bookReviewIntent.putExtra("BOOK_OPENDATE", openBooks.get(position).getDateOpen());
                startActivityForResult(bookReviewIntent, 0);
            }
        });
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current open books");
        dh.saveOpenBookData(openBooks);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("CHROMA", "onActivityResult");
        //Log.i("CHROMA", String.valueOf(data.getLongExtra(CURRENT_DATE, -1)));
        //this.currentDate.setTime(data.getLongExtra(CURRENT_DATE, -1));
        super.updateDateView();
    }

    @Override
    protected void onResume() {
        Log.i("CHROMA", "onResume New Book");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("CHROMA", "onPause New Book");
        super.onPause();
    }


    @Override
    protected void onStop() {
        Log.i("CHROMA", "onStop New Book");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("CHROMA", "onDestroy New Book");
        super.onDestroy();
    }

}

