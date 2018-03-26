package fr.zigomar.chroma.chroma.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.model.Movie;
import fr.zigomar.chroma.chroma.R;

public class MovieActivity extends InputActivity {

    private LinearLayout moviesList;
    private ArrayList<EditText> titles = new ArrayList<>();
    private ArrayList<EditText> directors = new ArrayList<>();
    private ArrayList<EditText> notes = new ArrayList<>();
    private ArrayList<RatingBar> ratings = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.titles.add((EditText) findViewById(R.id.MovieTitle));
        this.directors.add((EditText) findViewById(R.id.MovieDirector));
        this.notes.add((EditText) findViewById(R.id.MovieTextData));
        this.ratings.add((RatingBar) findViewById(R.id.MovieRating));

        this.moviesList = findViewById(R.id.MovieLinearLayout);

        Button addStepButton = findViewById(R.id.AddButton);
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkData()) {

                    @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.unit_input_movie, null);
                    moviesList.addView(child);
                    titles.add((EditText) child.findViewById(R.id.MovieTitle));
                    directors.add((EditText) child.findViewById(R.id.MovieDirector));
                    notes.add((EditText) child.findViewById(R.id.MovieTextData));
                    ratings.add((RatingBar) child.findViewById(R.id.MovieRating));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MovieTitleAndNotesRequired, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayList<Movie> movies = getMovies();

        for (int i = 0; i < movies.size(); i++) {
            this.titles.get(i).setText(movies.get(i).getTitle());
            this.directors.get(i).setText(movies.get(i).getDirector());
            this.notes.get(i).setText(movies.get(i).getDescription());
            this.ratings.get(i).setRating(movies.get(i).getRating());
            if (i < movies.size() - 1) {
                this.moviesList = findViewById(R.id.MovieLinearLayout);
                @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.unit_input_movie, null);
                this.moviesList.addView(child);
                this.titles.add((EditText) child.findViewById(R.id.MovieTitle));
                this.directors.add((EditText) child.findViewById(R.id.MovieDirector));
                this.notes.add((EditText) child.findViewById(R.id.MovieTextData));
                this.ratings.add((RatingBar) child.findViewById(R.id.MovieRating));
            }
        }
    }


    private ArrayList<Movie> getMovies(){
        // getting the data is handled by the DataHandler
        return this.dh.getMoviesList();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current movies");

        ArrayList<Movie> movies = new ArrayList<>();
        // assuming we have as many elements in the three ArrayList titles directors and notes
        for (int i = 0; i < this.titles.size(); i++) {
            movies.add(new Movie(this.titles.get(i).getText().toString(),
                    this.directors.get(i).getText().toString(),
                    this.notes.get(i).getText().toString(),
                    this.ratings.get(i).getRating()
            ));
        }
        this.dh.saveMovieData(movies);
    }


    private boolean checkData() {
        int current_size = this.titles.size();
        for (int i = 0; i < current_size; i++) {
            int title_size = this.titles.get(i).getText().toString().length();
            int description_size = this.notes.get(i).getText().toString().length();
            if (title_size == 0 || description_size == 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }
}