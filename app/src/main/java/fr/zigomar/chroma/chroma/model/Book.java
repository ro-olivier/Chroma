package fr.zigomar.chroma.chroma.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.security.MessageDigest.getInstance;

public class Book {
    private final String title;
    private final String author;
    private String review;
    private float rating;
    private boolean finished;
    private Date dateopen;
    private Date datefinished;
    private String hash;

    public Book(String title, String author, Date dateopen) {
        // Constructor used when book is first opened
        this.title = title;
        this.author = author;
        this.dateopen = dateopen;
        this.finished = false;
        this.hash = computeHash(title, author);
    }

    public Book(String title, String author, Date dateopen, String review) {
        // Constructor used when opened book is reviewed
        this.title = title;
        this.author = author;
        this.dateopen = dateopen;
        this.review = review;
        this.finished = false;
        this.hash = computeHash(title, author);
    }

    public Book(String title, String author, Date dateopen, String review, Date datefinished, float rating) {
        // Constructor used when booked as been finished
        this.title = title;
        this.author = author;
        this.dateopen = dateopen;
        this.datefinished = datefinished;
        this.review = review;
        this.rating = rating;
        this.finished = true;
        this.hash = computeHash(title, author);
    }

    private String computeHash(String title, String author) {

        MessageDigest md = null;
        try {
            md = getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String text = title + author;

        if (md != null) {
            md.update(text.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            return String.format( "%064x", new BigInteger(1, digest) );
        }

        return null;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getReview() {
        return review;
    }

    public float getRating() {
        return rating;
    }

    public boolean getFinished() {
        return finished;
    }

    public Date getDateOpen() { return dateopen; }

    public Date getDateFinished() { return datefinished; }

    public String getHash() { return hash; }

    public boolean hasReview() { return this.review != null; }

    JSONObject getBookAsJSON() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
        JSONObject json = new JSONObject();
        try {
            json.put("hash", this.hash);
            json.put("title", this.title);
            json.put("author", this.author);
            json.put("date_opened", df.format(this.dateopen));
            if (this.datefinished != null) { json.put("date_finished", df.format(this.datefinished)); }
            json.put("finished", this.finished);
            json.put("review", this.review);
            json.put("rating", this.rating);
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getBookAsJSON().toString();
    }

    public void updateReview(String notes) {
        this.review = notes;
    }

    public void rateBook(String notes, Date closeDate, float rating) {
        this.review = notes;
        this.datefinished = closeDate;
        this.rating = rating;
        this.finished = true;
    }
}