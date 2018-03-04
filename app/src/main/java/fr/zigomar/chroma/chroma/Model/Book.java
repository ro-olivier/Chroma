package fr.zigomar.chroma.chroma.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Book {
    private final String title;
    private final String author;
    private String description;
    private float rating;
    private boolean finished;
    private Date dateopen;
    private Date datefinished;

    public Book(String title, String author, Date dateopen) {
        this.title = title;
        this.author = author;
        this.dateopen = dateopen;
        this.finished = false;
    }

    public void rateBook(String description, float rating, Date datefinished) {
        this.description = description;
        this.rating = rating;
        this.finished = true;
        this.datefinished = datefinished;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public float getRating() {
        return rating;
    }

    public boolean getFinished() {
        return finished;
    }

    public Date getDateOpen() { return dateopen; }

    public Date getDateFinished() { return datefinished; }

    private JSONObject getBookAsJSON() {
        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
        JSONObject json = new JSONObject();
        try {
            json.put("title", this.title);
            json.put("author", this.author);
            json.put("dateOpen", df.format(this.dateopen));
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getBookAsJSON().toString();
    }
}