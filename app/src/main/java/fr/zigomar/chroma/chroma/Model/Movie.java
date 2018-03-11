package fr.zigomar.chroma.chroma.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {
    private final String title;
    private final String director;
    private final String description;
    private final float rating;

    public Movie(String title, String director, String description, float rating) {
        this.title = title;
        this.description = description;
        this.director = director;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getDescription() {
        return description;
    }

    public float getRating() {
        return rating;
    }

    public JSONObject getMovieAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("title", this.title);
            json.put("director", this.director);
            json.put("description", this.description);
            json.put("rating", String.valueOf(this.rating));
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getMovieAsJSON().toString();
    }
}