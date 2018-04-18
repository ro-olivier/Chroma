package fr.zigomar.chroma.chroma.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

    private boolean inputsVisible = true;

    private Book reviewedBook;

    private ArrayList<Book> reviewedBooks;

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
    private int reviewedBookIndex;
    private String bookHash;

    private Bundle extras;

    private boolean bookAlreadyClosed = false;

    private boolean read_only = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.extras = getIntent().getExtras();

        this.title = findViewById(R.id.BookTitle);
        this.author = findViewById(R.id.BookAuthor);
        this.openDate = findViewById(R.id.BookOpenDate);
        this.closeDate = findViewById(R.id.BookCloseDate);
        this.notes = findViewById(R.id.BookNotes);
        this.closeBook = findViewById(R.id.BookCloseCheckBox);
        this.rating = findViewById(R.id.BookRating);

        if (extras != null) {
            this.read_only = this.extras.getInt("REQUEST_CODE") == 13 ;
            this.title.setText(this.extras.getString("BOOK_TITLE"));
            this.author.setText(this.extras.getString("BOOK_AUTHOR"));
            this.openDate.setText(this.df.format(this.extras.get("BOOK_OPENDATE")));
        }

        if (this.read_only) {
            this.notes.setEnabled(false);
            this.closeBook.setVisibility(View.GONE);
            this.rating.setEnabled(false);
        }

        this.bookHash = getBookHash();
        this.reviewedBooks = this.dh.getReviewedBooksList();

        for (Book b : this.reviewedBooks) {
            if (Objects.equals(b.getHash(), this.bookHash)) {
                this.reviewedBook = b;
                this.reviewedBookIndex = this.reviewedBooks.indexOf(b);
            }
        }

        if (this.reviewedBook != null) {
            Log.i("CHROMA", this.reviewedBook.toString());
            if (this.reviewedBook.getReview() != null) {
                this.notes.setText(this.reviewedBook.getReview());
            }

            if (this.reviewedBook.getRating() != 0) {
                this.rating.setRating(this.reviewedBook.getRating());
            }

            if (this.reviewedBook.getDateFinished() != null) {
                this.closeDate.setText(this.df.format(this.reviewedBook.getDateFinished()));
            }

            if (this.reviewedBook.getFinished()) {
                this.bookAlreadyClosed = true;
                this.closeBook.setChecked(true);
                this.rating.setVisibility(View.VISIBLE);
            }
        }

        if (!read_only) {
            this.closeBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        closeDate.setVisibility(View.VISIBLE);
                        closeDate.setText(df.format(currentDate));
                        rating.setVisibility(View.VISIBLE);
                    } else if (bookAlreadyClosed) {
                        closeBook.setChecked(true);
                        Toast.makeText(getApplicationContext(), R.string.CannotReOpenBook, Toast.LENGTH_SHORT).show();
                    } else {
                        closeDate.setText("");
                        rating.setVisibility(View.INVISIBLE);
                    }
                }
            });


            this.notes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        title.setVisibility(View.GONE);
                        author.setVisibility(View.GONE);
                        openDate.setVisibility(View.GONE);
                        closeBook.setVisibility(View.GONE);
                        rating.setVisibility(View.GONE);
                        closeDate.setVisibility(View.GONE);
                        inputsVisible = false;
                    }
                }
            });
        }
    }

    private String getBookHash() {
        Log.i("CHROMA", "Book hash requested for Book in intent");

        MessageDigest md = null;
        try {
            md = getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String text = null;
        if (this.extras != null) {
            Log.i("CHROMA", "Book intent title : " + this.extras.getString("BOOK_TITLE"));
            Log.i("CHROMA", "Book intent author : " + this.extras.getString("BOOK_AUTHOR"));
            text = this.extras.getString("BOOK_TITLE") + this.extras.getString("BOOK_AUTHOR");
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
        if (!read_only) {
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
    }

    @Override
    public void onBackPressed() {

        if (!read_only) {

            if (!this.inputsVisible) {
                this.title.setVisibility(View.VISIBLE);
                this.author.setVisibility(View.VISIBLE);
                this.openDate.setVisibility(View.VISIBLE);
                this.closeBook.setVisibility(View.VISIBLE);
                this.rating.setVisibility(View.INVISIBLE);
                this.closeDate.setVisibility(View.INVISIBLE);

                if (this.closeBook.isChecked()) {
                    this.closeBook.setVisibility(View.VISIBLE);
                    this.rating.setVisibility(View.VISIBLE);
                    this.closeDate.setVisibility(View.VISIBLE);
                }

                this.inputsVisible = true;

            } else {

                Intent returnIntent = new Intent();
                returnIntent.putExtra(CURRENT_DATE, this.currentDate.getTime());
                returnIntent.putExtra("HASH", this.bookHash);
                returnIntent.putExtra("REVIEW", this.notes.getText().toString());
                returnIntent.putExtra("TITLE", this.title.getText().toString());
                returnIntent.putExtra("AUTHOR", this.author.getText().toString());
                returnIntent.putExtra("BOOK_OPENDATE", this.openDate.getText().toString());

                if (this.closeBook.isChecked()) {
                    returnIntent.putExtra("BOOK_CLOSEDDATE", this.closeDate.getText().toString());
                    returnIntent.putExtra("RATING", this.rating.getRating());
                    Log.i("CHROMA", "Setting result of BookReview Activity to 200");
                    setResult(200, returnIntent);
                } else {
                    Log.i("CHROMA", "Setting result of BookReview Activity to 201");
                    setResult(201, returnIntent);
                }

                super.onBackPressed();

            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
}