package fr.zigomar.chroma.chroma.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Baise {
    private final String partner;
    private final String notes;
    private final float rating;

    public Baise(String partner, String notes, float rating) {
        this.partner = partner;
        this.notes = notes;
        this.rating = rating;
    }

    public String getPartner() {
        return partner;
    }

    public String getNotes() {
        return notes;
    }

    public float getRating() {
        return rating;
    }

    JSONObject getBaiseAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("partner", this.partner);
            json.put("description", this.notes);
            json.put("rating", String.valueOf(this.rating));
        } catch (JSONException a) {
            return new JSONObject();
        }

        return json;
    }

    public String toString() {
        return getBaiseAsJSON().toString();
    }
}