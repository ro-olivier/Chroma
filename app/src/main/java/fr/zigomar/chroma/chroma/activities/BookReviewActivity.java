package fr.zigomar.chroma.chroma.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import fr.zigomar.chroma.chroma.model.Book;
import fr.zigomar.chroma.chroma.R;

import static java.security.MessageDigest.getInstance;

public class BookReviewActivity extends InputActivity {

    private TextView title;
    private TextView author;
    private TextView openDate;
    private TextView closeDate;
    private EditText notes;
    private RatingBar rating;
    private CheckBox closeBook;

    private Book reviewedBook;

    private ArrayList<Book> reviewedBooks;

    private DateFormat df;
    private int reviewedBookIndex;
    private String bookHash;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        df = new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE);

        title = findViewById(R.id.BookTitle);
        author = findViewById(R.id.BookAuthor);
        openDate = findViewById(R.id.BookOpenDate);
        closeDate = findViewById(R.id.BookCloseDate);
        notes = findViewById(R.id.BookNotes);
        closeBook = findViewById(R.id.BookCloseCheckBox);
        rating = findViewById(R.id.BookRating);

        if (extras != null) {
            title.setText(extras.getString("BOOK_TITLE"));
            author.setText(extras.getString("BOOK_AUTHOR"));
            openDate.setText(df.format(extras.get("BOOK_OPENDATE")));
        }

        this.bookHash = getBookHash();
        this.reviewedBooks = this.dh.getReviewedBooksList();

        for (Book b : reviewedBooks) {
            if (Objects.equals(b.getHash(), this.bookHash)) {
                this.reviewedBook = b;
                this.reviewedBookIndex = this.reviewedBooks.indexOf(b);
            }
        }

        if (this.reviewedBook != null) {
            if (reviewedBook.getReview() != null) {
                notes.setText(reviewedBook.getReview());
            }

            if (reviewedBook.getRating() != 0) {
                rating.setRating(reviewedBook.getRating());
            }

            if (reviewedBook.getDateFinished() != null) {
                closeDate.setText(df.format(reviewedBook.getDateFinished()));
            }

            if (reviewedBook.getFinished()) {
                closeBook.setChecked(true);
                rating.setVisibility(View.VISIBLE);
            }
        }

        this.closeBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    closeDate.setText(df.format(currentDate));
                    rating.setVisibility(View.VISIBLE);
                } else {
                    closeBook.setChecked(true);
                    Toast.makeText(getApplicationContext(), R.string.CannotReOpenBook, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getBookHash() {
        Log.i("CHROMA", "Book hash requested for Book in intent");

        Bundle extras = getIntent().getExtras();

        MessageDigest md = null;
        try {
            md = getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String text = null;
        if (extras != null) {
            Log.i("CHROMA", "Book intent title : " + extras.getString("BOOK_TITLE"));
            Log.i("CHROMA", "Book intent author : " + extras.getString("BOOK_AUTHOR"));
            text = extras.getString("BOOK_TITLE") + extras.getString("BOOK_AUTHOR");
        }

        if (md != null) {
            if (text != null) {
                md.update(text.getBytes(StandardCharsets.UTF_8));
                byte[] digest = md.digest();
                String s = String.format("%064x", new BigInteger(1, digest));
                Log.i("CHROMA", "Hash is = " + s);
                return s;
            }
        }

        return null;
    }

    @Override
    protected void saveData() {
        Log.i("CHROMA", "Updating the data object with current reviewed books");

        if (this.reviewedBook == null) {
            if (this.closeBook.isChecked()) {
                try {
                    this.reviewedBook = new Book(this.title.getText().toString(),
                            this.author.getText().toString(),
                            this.df.parse(this.openDate.getText().toString()),
                            this.notes.getText().toString(),
                            this.df.parse(this.closeDate.getText().toString()),
                            this.rating.getRating());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    this.reviewedBook = new Book(this.title.getText().toString(),
                            this.author.getText().toString(),
                            this.df.parse(this.openDate.getText().toString()),
                            this.notes.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            this.reviewedBooks.add(this.reviewedBook);
        } else {
            if (this.closeBook.isChecked()) {
                try {
                    this.reviewedBooks.get(this.reviewedBookIndex).rateBook(this.notes.getText().toString(),
                            this.df.parse(this.closeDate.getText().toString()),
                            this.rating.getRating());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                this.reviewedBooks.get(this.reviewedBookIndex).updateReview(this.notes.getText().toString());
            }
        }

        this.dh.saveReviewedBooksData(this.reviewedBooks);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra(CURRENT_DATE, currentDate.getTime());
        returnIntent.putExtra("HASH", this.bookHash);

        if (this.closeBook.isChecked()) {
            Log.i("CHROMA", "Setting result of BookReview Activity to 200");
            setResult(200, returnIntent);
        } else {
            Log.i("CHROMA", "Setting result of BookReview Activity to 201");
            setResult(201, returnIntent);
        }

        super.onBackPressed();
    }

}