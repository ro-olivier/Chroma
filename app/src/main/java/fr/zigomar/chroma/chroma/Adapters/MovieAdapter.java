package fr.zigomar.chroma.chroma.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.List;

import fr.zigomar.chroma.chroma.Model.Movie;
import fr.zigomar.chroma.chroma.R;

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unit_input_movie, parent, false);
        }

        MovieViewHolder viewHolder = (MovieViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new MovieViewHolder();
            viewHolder.description = convertView.findViewById(R.id.MovieTextData);
            viewHolder.title = convertView.findViewById(R.id.MovieTitle);
            viewHolder.director = convertView.findViewById(R.id.MovieDirector);
            convertView.setTag(viewHolder);
        }

        Movie movie = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        assert movie != null;
        viewHolder.description.setText(movie.getDescription());
        viewHolder.title.setText(movie.getTitle());
        viewHolder.director.setText(movie.getDirector());

        return convertView;
    }

    private class MovieViewHolder{
        EditText description;
        EditText title;
        EditText director;
    }
}
