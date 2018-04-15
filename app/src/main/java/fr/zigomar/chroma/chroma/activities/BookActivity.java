package fr.zigomar.chroma.chroma.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import fr.zigomar.chroma.chroma.adapters.modeladapters.BooksAdapter;
import fr.zigomar.chroma.chroma.model.Book;
import fr.zigomar.chroma.chroma.model.DataHandler;
import fr.zigomar.chroma.chroma.R;

public class BookActivity extends InputActivity {

    private TextView title;
    private TextView author;

    private ArrayList<Book> openBooks;
    private BooksAdapter openBooksAdapter;

    private ArrayList<Book> bookReviews;
    private BooksAdapter bookReviewsAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        this.dh = new DataHandler(this.getApplicationContext(),
                getString(R.string.OpenBooksFileName));

        super.onCreate(savedInstanceState);

        this.title = findViewById(R.id.BookTitle);
        this.author = findViewById(R.id.BookAuthor);

        // init of the data : fetch spendings data in the currentDate file if it exist
        this.openBooks = this.dh.getOpenBooksData();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) spendings
        ListView openBooksView = findViewById(R.id.ListViewOpenBooks);

        this.openBooksAdapter = new BooksAdapter(BookActivity.this, this.openBooks);
        openBooksView.setAdapter(this.openBooksAdapter);

        openBooksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("CHROMA", "Clicked the " + position + "-th item.");

                Log.i("CHROMA", "Switching to book review activity");
                Intent bookReviewIntent = new Intent (BookActivity.this, BookReviewActivity.class);
                int requestCode = 12;

                bookReviewIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                bookReviewIntent.putExtra("BOOK_TITLE", openBooks.get(position).getTitle());
                bookReviewIntent.putExtra("BOOK_AUTHOR", openBooks.get(position).getAuthor());
                bookReviewIntent.putExtra("BOOK_OPENDATE", openBooks.get(position).getDateOpen());
                bookReviewIntent.putExtra("REQUEST_CODE", requestCode);
                startActivityForResult(bookReviewIntent, requestCode);
            }
        });


        Button addNewBookButton = findViewById(R.id.AddNewBookButton);
        addNewBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (title.getText().toString().length() > 0 && author.getText().toString().length() > 0) {
                    Log.i("CHROMA", "Added a new book !");
                    openBooks.add(new Book(title.getText().toString(), author.getText().toString(), currentDate));
                    openBooksAdapter.notifyDataSetChanged();
                    resetViews();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.BookDetailsRequired, Toast.LENGTH_SHORT).show();
                }
            }
        });

        openBooksView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                        openBooksAdapter.remove(openBooks.get(pos));
                        openBooksAdapter.notifyDataSetChanged();
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

        DataHandler dh_reviews = new DataHandler(this.getApplicationContext(), this.currentDate);
        this.bookReviews = dh_reviews.getReviewedBooksList();
        final ListView reviewsView = findViewById(R.id.ListViewBookReviews);

        this.bookReviewsAdapter = new BooksAdapter(BookActivity.this, this.bookReviews);
        reviewsView.setAdapter(this.bookReviewsAdapter);

        reviewsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("CHROMA", "Clicked the " + position + "-th item.");

                Log.i("CHROMA", "Switching to book review activity (read-only mode)");
                Intent bookReviewIntent = new Intent (BookActivity.this, BookReviewActivity.class);
                int requestCode = 13;
                bookReviewIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                bookReviewIntent.putExtra("DISPLAY_ONLY", true);
                bookReviewIntent.putExtra("BOOK_TITLE", bookReviews.get(position).getTitle());
                bookReviewIntent.putExtra("BOOK_AUTHOR", bookReviews.get(position).getAuthor());
                bookReviewIntent.putExtra("BOOK_OPENDATE", bookReviews.get(position).getDateOpen());
                bookReviewIntent.putExtra("REQUEST_CODE", requestCode);
                startActivityForResult(bookReviewIntent, requestCode);
            }
        });

    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current open books");
        this.dh.saveOpenBookData(this.openBooks);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("CHROMA", "onActivityResult, request code : " + requestCode);

        if (requestCode != 12) {
            // if the request code is not 12 (this means that we opened the BookReview in read-only mode) we return here
            // this is just "in case" something went wrong in the BookReview activity since the request code should
            // make the fields read-only so no data should have changed.
            return;
        }

        int a = -1;

        switch (resultCode) {
            case 200:
                Log.i("CHROMA", "Apparently a book was closed, lets delete it from the openBook list !");
                Log.i("CHROMA", "Hash is : " + data.getStringExtra("HASH"));
                for (Book b : this.openBooks) {
                    if (Objects.equals(b.getHash(), data.getStringExtra("HASH"))) {
                        a = this.openBooks.indexOf(b);
                        Log.i("CHROMA", "Found corresponding book at index : " + a);
                    }
                }
                this.openBooks.remove(a);
                this.openBooksAdapter.notifyDataSetChanged();
                this.dh.saveOpenBookData(this.openBooks);
                break;

            case 201:
                Log.i("CHROMA", "Book was reviewed but not closed.");
                break;

            default:
                Log.i("CHROMA", "Received code : " + resultCode);
                break;
        }

        Log.i("CHROMA", "Apparently a book was reviewed, let's display it in the list of today's review");
        Log.i("CHROMA", "Hash is : " + data.getStringExtra("HASH"));
        Log.i("CHROMA", "Review is : " + data.getStringExtra("REVIEW"));

        Log.i("CHROMA", "Currently " + this.bookReviews.size() + " books reviews");

        boolean found = false;
        for (Book b : this.bookReviews) {
            if (Objects.equals(b.getHash(), data.getStringExtra("HASH"))) {
                a = this.bookReviews.indexOf(b);
                Log.i("CHROMA", "Found corresponding book at index : " + a);
                Log.i("CHROMA", "Previous review : " + this.bookReviews.get(a).getReview());

                if (!data.hasExtra("BOOK_CLOSEDDATE")) {
                    this.bookReviews.get(a).updateReview(data.getStringExtra("REVIEW"));
                } else {
                    try {
                        this.bookReviews.get(a).rateBook(data.getStringExtra("REVIEW"),
                                new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(data.getStringExtra("BOOK_CLOSEDDATE")),
                                data.getFloatExtra("RATING", 0));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("CHROMA", "New review : " + this.bookReviews.get(a).getReview());
                found = true;
            }
        }


        if (!found) {
            try {
                if (!data.hasExtra("BOOK_CLOSEDDATE")) {
                    this.bookReviews.add(new Book(data.getStringExtra("TITLE"),
                            data.getStringExtra("AUTHOR"),
                            new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(data.getStringExtra("BOOK_OPENDATE")),
                            data.getStringExtra("REVIEW")));
                } else {
                    this.bookReviews.add(new Book(data.getStringExtra("TITLE"),
                            data.getStringExtra("AUTHOR"),
                            new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(data.getStringExtra("BOOK_OPENDATE")),
                            data.getStringExtra("REVIEW"),
                            new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(data.getStringExtra("BOOK_CLOSEDDATE")),
                            data.getFloatExtra("RATING", 0)));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        this.bookReviewsAdapter.notifyDataSetChanged();

    }

    private void resetViews() {
        this.title.setText("");
        this.author.setText("");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
}