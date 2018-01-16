package fr.zigomar.chroma.chroma.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.zigomar.chroma.chroma.Adapters.MovieAdapter;
import fr.zigomar.chroma.chroma.Model.Movie;
import fr.zigomar.chroma.chroma.R;

public class MovieActivity extends InputActivity {

    private LinearLayout stepsList;
    // the list holding the data
    private ArrayList<Movie> movies;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // setting the view's layout, yay, we can see stuff on the screen!
        setContentView(R.layout.activity_movie);

        // call inherited initiating method
        init();

        Button addStepButton = findViewById(R.id.AddButton);
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepsList = findViewById(R.id.MovieLinearLayout);
                View child = getLayoutInflater().inflate(R.layout.unit_input_movie, null);
                stepsList.addView(child);
            }
        });

        // init of the data : fetch movies data in the currentDate file if it exist
        this.movies = getMovies();

        // finishing up the setting of the adapter for the list view of the retrieved (and
        // new) trips
        //LinearLayout tripsListView = findViewById(R.id.MovieLinearLayout);

        //this.movieAdapter = new MovieAdapter(MovieActivity.this, this.movies);
        //tripsListView.setAdapter(this.movieAdapter);
    }


    private ArrayList<Movie> getMovies(){
        // getting the data is handled by the DataHandler
        return this.dh.getMovies();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current movies");
        this.dh.saveMovieData(this.movies);
    }


}

