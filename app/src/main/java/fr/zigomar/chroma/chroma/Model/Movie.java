package fr.zigomar.chroma.chroma.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Movie {
    private final String title;
    private final String director;
    private final String description;

    public Movie(String title, String director, String description) {
        this.title = title;
        this.description = description;
        this.director = director;
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

    private JSONObject getSpendingAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("title", this.title);
            json.put("director", this.director);
            json.put("description", this.description);
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getSpendingAsJSON().toString();
    }
}